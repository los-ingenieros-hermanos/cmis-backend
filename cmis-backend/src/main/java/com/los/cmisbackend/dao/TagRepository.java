package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.ETag;
import com.los.cmisbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findTagById(Long tagId);
}
