package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.ERole;
import com.los.cmisbackend.entity.Role;
import com.los.cmisbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User findUserById(Long id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Page<User> findUsersByFirstNameContaining(String firstName, Pageable pageable);

    Page<User> findUsersByRolesAndFirstNameContaining(Role role, String firstName, Pageable pageable);
}
