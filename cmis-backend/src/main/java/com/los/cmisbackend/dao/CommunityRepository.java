package com.los.cmisbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.los.cmisbackend.entity.Community;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Community findCommunityById(Long id);
    List<Community> findCommunitiesByFollowersId(Long id);
    List<Community> findCommunitiesByTagsId(Long id);
}
