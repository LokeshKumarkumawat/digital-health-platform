package com.digitalhealth.platform.users.repository;

import com.digitalhealth.platform.common.enums.AuthProvider;
import com.digitalhealth.platform.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByAuthProvider(AuthProvider authProvider);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);


    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllWithRoles();

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> searchByName(@Param("name") String name);


    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    long countByAuthProvider(AuthProvider authProvider);

    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate")
    List<User> findByCreatedAtBetween(@Param("startDate") java.time.OffsetDateTime startDate,
                                      @Param("endDate") java.time.OffsetDateTime endDate);
}

