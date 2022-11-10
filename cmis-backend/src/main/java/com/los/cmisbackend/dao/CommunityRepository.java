package com.los.cmisbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.los.cmisbackend.entity.Community;

public interface CommunityRepository extends JpaRepository<Community, Integer> {

}
