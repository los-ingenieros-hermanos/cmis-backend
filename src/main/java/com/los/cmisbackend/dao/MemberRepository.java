package com.los.cmisbackend.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.los.cmisbackend.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	Member findByCommunityIdAndStudentId(Long communityId, Long studentId);
	Set<Member> findByCommunityId(Long communityId);
	Set<Member> findByStudentId(Long studentId);
	Page<Member> findByCommunityId(Long communityId, Pageable pageable);
	Page<Member> findByStudentId(Long studentId, Pageable pageable);

}
