package com.digitalhealth.platform.users.repository;


import com.digitalhealth.platform.users.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findByCode(String code);

    Optional<PasswordResetCode> findByUserId(Long userId);


    @Modifying
    @Query("DELETE FROM PasswordResetCode p WHERE p.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM PasswordResetCode p WHERE p.user.id = :userId AND p.used = false")
    List<PasswordResetCode> findUnusedCodesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM PasswordResetCode p WHERE p.expiryDate < :now")
    void deleteExpiredCodes(@Param("now") OffsetDateTime now);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PasswordResetCode p " +
            "WHERE p.code = :code AND p.used = false AND p.expiryDate > :now")
    boolean isCodeValid(@Param("code") String code, @Param("now") OffsetDateTime now);
}