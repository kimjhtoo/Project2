package com.tuflex.web.user.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tuflex.web.user.model.ERole;
import com.tuflex.web.user.model.Role;
import com.tuflex.web.user.model.User;
import com.tuflex.web.user.payload.request.UserRegisterRequest;
import com.tuflex.web.user.repository.RoleRepository;
import com.tuflex.web.user.repository.UserRepository;
import com.google.common.base.Predicate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserRestController {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    PasswordEncoder encoder;

    // @GetMapping("withdrawal")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<?> withdrawal(Authentication authentication,
    // @RequestParam Long pid) {
    // Long myPid = Utils.getPid();
    // if (pid == myPid) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인을 탈퇴시킬 수
    // 없습니다.");
    // }
    // blockUserService.blockUser(adminRepository.findById(pid).get().getPhone());
    // adminRepository.withdrawal(pid);
    // return ResponseEntity.ok().build();
    // }

    @RequestMapping(value = "/register.do", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> authenticateUser(@RequestBody UserRegisterRequest req) throws Exception {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }

        User user = new User(req.getEmail(), encoder.encode(req.getPasswd()),
                req.getName(), req.getSnsType(), req.getSnsId());

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/check.do", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> check(@RequestParam String snsId, @RequestParam String email) throws Exception {
        if (userRepository.existsByEmailAndSnsId(email, snsId)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}
