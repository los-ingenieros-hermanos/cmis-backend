package com.los.cmisbackend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.los.cmisbackend.dao.MemberRepository;
import com.los.cmisbackend.entity.Member;

@Component
public class MemberUtil {

	@Autowired
	private MemberRepository memberRepository;
	
	public boolean isAuthorized(Long communityId, Long userId){
		//iterate in community members and check if the user is a member
		if (userId.equals(communityId)) {
			return true;
		}

		Member member = memberRepository.findByCommunityIdAndStudentId(communityId, userId);

		if (member != null && member.getAuthorizations().contains("ALL")) {
			return true;
		}

		return false;
	}
	
}
