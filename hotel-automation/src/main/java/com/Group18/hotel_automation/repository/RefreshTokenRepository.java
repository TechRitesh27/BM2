package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.RefreshToken;
import com.Group18.hotel_automation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    Optional<RefreshToken> findByUser(User user);

}
