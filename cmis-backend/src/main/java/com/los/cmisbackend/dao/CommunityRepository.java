package com.los.cmisbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.los.cmisbackend.entity.Community;

@RepositoryRestResource(path="communities")
public interface CommunityRepository extends JpaRepository<Community, Long> {

}
