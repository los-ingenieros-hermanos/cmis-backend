package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Post;
import com.los.cmisbackend.entity.Student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Post findPostById(Long id);
    List<Post> findByCommunityId(Long id);    

    @Transactional
    void deleteByCommunityId(Long id);

    Optional<Post> findPostByEventId(Long eventId);
    List<Post> findPostByStudentsId(Long id);
    Page<Post> findByStudents(Student student, Pageable pageable);
    Page<Post> findByCommunityId(Long id, Pageable pageable);
    Page<Post> findPostsByTitleContaining(String title, Pageable pageable);
    Page<Post> findPostsByTitleContainingOrTextContaining(String title, String text, Pageable pageable);
    Page<Post> findAllByOrderByDateDesc(Pageable pageable);
    Page<Post> findAllByOrderByDateAsc(Pageable pageable);
    Page<Post> findAllByOrderByTitleAsc(Pageable pageable);
    Page<Post> findAllByOrderByTitleDesc(Pageable pageable);
    Page<Post> findAllByOrderByLikeNumDesc(Pageable pageable);
    Page<Post> findAllByVisibilityContaining(String visibility, Pageable pageable);
    Page<Post> findAllByVisibilityAndCommunity(String visibility, Long id, Pageable pageable);
    Page<Post> findAllByVisibilityAndCommunityIn(String visibility, List<Long> ids, Pageable pageable);
}
