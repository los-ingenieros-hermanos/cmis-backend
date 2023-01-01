package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findTagById(Long tagId);
}
