package com.Group18.hotel_automation.config;

import com.Group18.hotel_automation.entity.*;
import com.Group18.hotel_automation.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final StaffTypeRepository staffTypeRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           StaffTypeRepository staffTypeRepository,
                           ServiceTypeRepository serviceTypeRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        this.roleRepository = roleRepository;
        this.staffTypeRepository = staffTypeRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner initData() {
        return args -> {

            // -------- ROLES --------
            List<String> roles = List.of("ADMIN", "GUEST", "STAFF");

            for (String roleName : roles) {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    Role role = new Role();
                    role.setName(roleName);
                    roleRepository.save(role);
                }
            }

            // -------- STAFF TYPES --------
            List<String> staffTypes = List.of(
                    "HOUSEKEEPING",
                    "MAINTENANCE",
                    "FRONT_DESK",
                    "KITCHEN",
                    "TRANSPORT"
            );

            for (String typeName : staffTypes) {
                if (staffTypeRepository.findByName(typeName).isEmpty()) {
                    StaffType staffType = new StaffType();
                    staffType.setName(typeName);
                    staffTypeRepository.save(staffType);
                }
            }

            // -------- SERVICE TYPES --------
            record ServiceSeed(String name, double price) {}

            List<ServiceSeed> services = List.of(
                    new ServiceSeed("ROOM_CLEANING", 300),
                    new ServiceSeed("LAUNDRY", 200),
                    new ServiceSeed("FOOD_ORDER", 500),
                    new ServiceSeed("SPA", 1500),
                    new ServiceSeed("AIRPORT_PICKUP", 1200)
            );


            for (ServiceSeed seed : services) {
                if (serviceTypeRepository.findByName(seed.name()).isEmpty()) {
                    ServiceType st = new ServiceType();
                    st.setName(seed.name());
                    st.setPrice(seed.price());
                    serviceTypeRepository.save(st);

                    System.out.println("✅ ServiceType inserted: " + seed.name());
                }
            }

            long serviceCount = serviceTypeRepository.count();
            System.out.println("ℹ️ Total service types in DB: " + serviceCount);

            // -------- DEFAULT ADMIN --------
            String adminEmail = "admin@hotel.com";

            if (!userRepository.existsByEmail(adminEmail)) {
                Role adminRole = roleRepository.findByName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

                User admin = new User();
                admin.setName("Admin Ritt");
                admin.setEmail(adminEmail);
                admin.setPhone("7517541081");
                admin.setPassword(passwordEncoder.encode("pass123"));
                admin.setRole(adminRole);
                admin.setActive(true);

                userRepository.save(admin);

                System.out.println("✅ Default ADMIN user created");
                System.out.println("   Email: admin@hotel.com");
                System.out.println("   Password: pass123");
            } else {
                System.out.println("ℹ️ Default ADMIN already exists");
            }
        };
    }
}
