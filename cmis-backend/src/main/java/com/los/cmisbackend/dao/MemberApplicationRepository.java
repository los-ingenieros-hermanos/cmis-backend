package com.los.cmisbackend.dao;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.MemberApplication;

public interface MemberApplicationRepository extends JpaRepository<MemberApplication, Long> {
	MemberApplication findByCommunity(Community community);
	Set<MemberApplication> findByCommunityId(Long id);
	MemberApplication findByCommunityIdAndStudentId(Long communityId, Long studentId);
	Optional<MemberApplication> findById(Long id);
}
