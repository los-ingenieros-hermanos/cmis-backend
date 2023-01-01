package com.los.cmisbackend.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.los.cmisbackend.entity.Community;

import java.util.List;
import java.util.Set;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Community findCommunityById(Long id);
    List<Community> findCommunitiesByFollowersId(Long id);
    List<Community> findCommunitiesByTagsId(Long id);
    Set<Community> findCommunitiesByMembersId(Long id);
    Page<Community> findById(Long id, Pageable pageable);
	Page<Community> findCommunitiesByFollowersId(Long id, Pageable pageable);
    Page<Community> findCommunitiesByTagsId(Long id, Pageable pageable);
    Page<Community> findCommunitiesByMembersId(Long id, Pageable pageable);
}
