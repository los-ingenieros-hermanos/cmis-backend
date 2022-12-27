package com.los.cmisbackend.util;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.Student;

@Component
public class MemberUtil {

	@Autowired
	private CommunityRepository communityRepository;
	
	public boolean isUserMemberOrCommunity(Long communityId, Long userId){
		//iterate in community members and check if the user is a member
		if (userId.equals(communityId)) {
			return true;
		}

		Community community = communityRepository.findById(communityId
				).orElseThrow(() -> new RuntimeException("Error: Community is not found."));

		Set<Student> members = community.getMembers();
		for(Student member : members){
			if(member.getId().equals(userId)){
				System.out.println("\n\n\nUser is a member of community" + community.getId() + "!" + member.getId() + " " + userId);
				return true;
			}
		}
		return false;	
	}
	
}
