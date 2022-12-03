package com.los.cmisbackend.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.los.cmisbackend.dao.RoleRepository;
import com.los.cmisbackend.entity.ERole;
import com.los.cmisbackend.entity.Role;

@Component
public class DbInit {
	
	@Autowired
	private RoleRepository repository;

	@PostConstruct
	private void init() {
	
		Long num = (long) 3;
		
		if(!(repository.findById(num).isPresent())){
			Role role1 = new Role(ERole.ROLE_STUDENT);
			Role role2 = new Role(ERole.ROLE_ADMIN);
			Role role3 = new Role(ERole.ROLE_COMMUNITY);
			repository.save(role1);
			repository.save(role2);
			repository.save(role3);
		}
	}
}
