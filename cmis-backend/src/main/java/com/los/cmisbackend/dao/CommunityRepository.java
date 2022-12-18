package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.los.cmisbackend.entity.Community;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Community findCommunityById(Long id);
    List<Community> findCommunitiesByFollowersId(Long id);
    List<Community> findCommunitiesByTagsId(Long id);
}
