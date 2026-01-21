package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.*;
import com.Group18.hotel_automation.entity.*;
import com.Group18.hotel_automation.repository.*;
import com.Group18.hotel_automation.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StaffTypeRepository staffTypeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       StaffTypeRepository staffTypeRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.staffTypeRepository = staffTypeRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // -------- LOGIN --------
    @Transactional
    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getRole().getName()
        );

        // delete old refresh token
        RefreshToken refreshToken = refreshTokenRepository
                .findByUser(user)
                .orElseGet(() -> {
                    RefreshToken rt = new RefreshToken();
                    rt.setUser(user);
                    return rt;
                });

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);


        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                user.getRole().getName()
        );
    }

    // -------- GUEST REGISTER --------
    @Transactional
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role guestRole = roleRepository.findByName("GUEST")
                .orElseThrow(() -> new RuntimeException("GUEST role not found"));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(guestRole);
        user.setActive(true);

        userRepository.save(user);
    }

    // -------- ADMIN CREATE STAFF --------
    @Transactional
    public void createStaff(CreateStaffRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role staffRole = roleRepository.findByName("STAFF")
                .orElseThrow(() -> new RuntimeException("STAFF role not found"));

        StaffType staffType = staffTypeRepository.findByName(request.getStaffType())
                .orElseThrow(() -> new RuntimeException("Invalid staff type"));

        String tempPassword = "Staff@" + UUID.randomUUID().toString().substring(0, 6);

        User staff = new User();
        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        staff.setPassword(passwordEncoder.encode(tempPassword));
        staff.setRole(staffRole);
        staff.setStaffType(staffType);
        staff.setActive(true);

        userRepository.save(staff);

        // In real app → email this password
        System.out.println("👷 Staff created: " + request.getEmail());
        System.out.println("🔑 Temp password: " + tempPassword);
    }

    // -------- REFRESH TOKEN --------
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getRole().getName()
        );

        return new LoginResponse(
                newAccessToken,
                refreshToken.getToken(),
                user.getRole().getName()
        );
    }
}
