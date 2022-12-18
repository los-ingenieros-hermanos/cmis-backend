package com.los.cmisbackend.util;

import javax.annotation.PostConstruct;

import com.los.cmisbackend.dao.TagRepository;
import com.los.cmisbackend.entity.ETag;
import com.los.cmisbackend.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.los.cmisbackend.dao.RoleRepository;
import com.los.cmisbackend.entity.ERole;
import com.los.cmisbackend.entity.Role;

@Component
public class DbInit {
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TagRepository tagRepository;

	@PostConstruct
	private void init() {
	
		Long num = (long) 3;
		
		if(!(roleRepository.findById(num).isPresent())){
			Role role1 = new Role(ERole.ROLE_STUDENT);
			Role role2 = new Role(ERole.ROLE_ADMIN);
			Role role3 = new Role(ERole.ROLE_COMMUNITY);
			roleRepository.save(role1);
			roleRepository.save(role2);
			roleRepository.save(role3);
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
	}
}
