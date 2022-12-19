package com.los.cmisbackend.util;

import javax.annotation.PostConstruct;

import com.los.cmisbackend.dao.AdminRepository;
import com.los.cmisbackend.dao.TagRepository;
import com.los.cmisbackend.dao.UserRepository;
import com.los.cmisbackend.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.los.cmisbackend.dao.RoleRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class DbInit {
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@PostConstruct
	private void init() {
	
		Long num = (long) 4;
		
		if(!(roleRepository.findById(num).isPresent())){
			Role role1 = new Role(ERole.ROLE_STUDENT);
			Role role2 = new Role(ERole.ROLE_ADMIN);
			Role role3 = new Role(ERole.ROLE_COMMUNITY);
			Role role4 = new Role(ERole.ROLE_UNVERIFIED);
			roleRepository.save(role1);
			roleRepository.save(role2);
			roleRepository.save(role3);
			roleRepository.save(role4);
		}

		if(!(tagRepository.findById((long) 19).isPresent())) {
			tagRepository.save(new Tag(ETag.TAG_ART));
			tagRepository.save(new Tag(ETag.TAG_BIOLOGY));
			tagRepository.save(new Tag(ETag.TAG_DRONE));
			tagRepository.save(new Tag(ETag.TAG_CHEMISTRY));
			tagRepository.save(new Tag(ETag.TAG_COMPUTER));
			tagRepository.save(new Tag(ETag.TAG_ENGINEERING));
			tagRepository.save(new Tag(ETag.TAG_ENTERTAINMENT));
			tagRepository.save(new Tag(ETag.TAG_FOOD));
			tagRepository.save(new Tag(ETag.TAG_GAMING));
			tagRepository.save(new Tag(ETag.TAG_LITERATURE));
			tagRepository.save(new Tag(ETag.TAG_MATH));
			tagRepository.save(new Tag(ETag.TAG_MUSIC));
			tagRepository.save(new Tag(ETag.TAG_PHILOSOPHY));
			tagRepository.save(new Tag(ETag.TAG_PHYSICS));
			tagRepository.save(new Tag(ETag.TAG_ROBOT));
			tagRepository.save(new Tag(ETag.TAG_SCIENCE));
			tagRepository.save(new Tag(ETag.TAG_SOCIAL));
			tagRepository.save(new Tag(ETag.TAG_SPORT));
			tagRepository.save(new Tag(ETag.TAG_TEAM));
		}

		if(adminRepository.findAll().isEmpty()) {
			Admin admin = new Admin();
			User user = new User("ADMIN", null, "admin@gtu.edu.tr"
					, "admin@gtu.edu.tr", encoder.encode("admin123"));
			admin.setUser(user);
			adminRepository.save(admin);
			Set<Role> roles = new HashSet<>();
			Role role = roleRepository.findByName(ERole.ROLE_ADMIN)
				.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(role);
			user.setRoles(roles);
			userRepository.save(user);
		}
	}
}
