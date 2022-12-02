package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Post;
import com.los.cmisbackend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Post findPostById(Long id);
    List<Post> findByCommunityId(Long id);

    @Transactional
    void deleteByCommunityId(Long id);

    List<Post> findPostByStudentsId(Long id);
}
