package com.tuflex.web.user.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tuflex.web.user.model.Role;
import com.tuflex.web.user.model.User;

// @Repository
public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String phone);

        Boolean existsByEmail(String phone);

        Boolean existsByEmailAndSnsId(String email, String snsId);
}