package com.tuflex.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.tuflex.web.user.model.ERole;
import com.tuflex.web.user.model.Role;
import com.tuflex.web.user.repository.RoleRepository;

@SpringBootApplication
public class WebApplication {
	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initialize() {
		System.out.println("hello world, I have just started up");
		if (!roleRepository.findByName(ERole.ROLE_ADMIN).isPresent()) {
			roleRepository.save(new Role(ERole.ROLE_ADMIN));
		}
		if (!roleRepository.findByName(ERole.ROLE_OWNER).isPresent()) {
			roleRepository.save(new Role(ERole.ROLE_OWNER));
		}
		if (!roleRepository.findByName(ERole.ROLE_USER).isPresent()) {
			roleRepository.save(new Role(ERole.ROLE_USER));
		}
	}

}
