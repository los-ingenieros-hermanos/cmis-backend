package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.*;
import com.los.cmisbackend.entity.*;
import com.los.cmisbackend.entity.Date;
import com.los.cmisbackend.security.service.UserDetailsImpl;
import com.los.cmisbackend.util.CmisConstants;
import com.los.cmisbackend.util.MemberUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "${cmis.app.baseUrl}", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/cmis")
public class PostController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    DateRepository dateRepository;

    @Autowired
    MemberUtil memberUtil;

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts(
        @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
        @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) 
        {

        Pageable pageable = PageRequest.of(page, size);

		Page<Post> posts = postRepository.findAll(pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

       
        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Post with id = " + id));

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable("id") Long id) {
        //postRepository.deleteById(id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Post with id = " + id));

        Long communityId = post.getCommunity().getId();

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (!userDetails.getId().equals(communityId) &&
            !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        List<Student> students = studentRepository.findStudentsByBookmarkedPostsId(id);

        for(Student s: students) {
            s.removePost(post);
            studentRepository.save(s);
        }

        Set<Student> students2 = post.getLikes();
        for(Student s: students2) {
            s.getLikedPosts().remove(post);
            studentRepository.save(s);
        }

        List<Event> events = post.getEvent();
        if(!events.isEmpty()) {
            Event event = events.get(0);
            Set<Student> attendants = event.getAttendants();
            for(Student s: attendants) {
                s.getEvents().remove(event);
                studentRepository.save(s);
            }
        }
        postRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/students/{studentId}/bookmarkedPosts")
    public ResponseEntity<List<Post>> getAllPostsByStudentId(@PathVariable(value = "studentId") Long studentId,
    @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
    @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findByStudents(student, pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/bookmarkedPosts")
    public ResponseEntity<Post> addBookmarkedPost(@PathVariable(value = "studentId") Long studentId, @RequestBody Post postRequest) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Post post = studentRepository.findById(studentId).map(student -> {
            Long postId = postRequest.getId();

            // post is existed
            if (postId != null) {
                Post _post = postRepository.findById(postId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Post with id = " + postId));
                student.addPost(_post);
                studentRepository.save(student);
                return _post;
            }
            throw new ResourceNotFoundException("Post does not exists");
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/students/{studentId}/bookmarkedPosts/{postId}")
    public ResponseEntity<HttpStatus> deletePostFromStudent(@PathVariable(value = "studentId") Long studentId, @PathVariable(value = "postId") Long postId) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        Post post = postRepository.findPostById(postId);
        student.removePost(post);
        studentRepository.save(student);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/communities/{communityId}/posts")
    public ResponseEntity<List<Post>> getAllPostsByCommunityId(@PathVariable(value = "communityId") Long communityId,
    @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
    @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) 
    {
        // add authentication check for community members
        // check authentication
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
        //         | memberUtil.isAuthorized(communityId, userDetails.getId())))
        //     return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findByCommunityId(communityId, pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    @PostMapping("/communities/{communityId}/posts")
    public ResponseEntity<Post> createPost(@PathVariable(value = "communityId") Long communityId,
                                                 @RequestBody Post postRequest) {
        System.out.println(postRequest);
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | memberUtil.isAuthorized(communityId, userDetails.getId())))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Post post = communityRepository.findById(communityId).map(community -> {
            postRequest.setCommunity(community);
            if (postRequest.getEvent() != null && !postRequest.getEvent().isEmpty()) {
                List<Event> events = postRequest.getEvent();
                Event event = events.get(0);

                // if date is null throw exception
                if (event.getDate() == null) {
                    throw new ResourceNotFoundException("Date is null");
                }

                Date date = new Date();
                date.setDay(event.getDate().getDay());
                date.setMonth(event.getDate().getMonth());
                date.setYear(event.getDate().getYear());
                if (event.getDate().getHour() != null) {
                    date.setHour(event.getDate().getHour());
                }
                if (event.getDate().getMinute() != null) {
                    date.setMinute(event.getDate().getMinute());
                }
                date = dateRepository.save(date);
                event.setDate(date);

                System.out.println(event);
                System.out.println(date);
                events.set(0, eventRepository.save(event));
                postRequest.setEvent(events);
                System.out.println(postRequest);
            }
            System.out.println(postRequest);
            Date date = new Date();
            date.setDay(postRequest.getDate().getDay());
            date.setMonth(postRequest.getDate().getMonth());
            date.setYear(postRequest.getDate().getYear());
            if(postRequest.getDate().getHour() != null) {
                date.setHour(postRequest.getDate().getHour());
            }

            if(postRequest.getDate().getMinute() != null) {
                date.setMinute(postRequest.getDate().getMinute());
            }

            date = dateRepository.save(date);
            postRequest.setDate(date);

            return postRepository.save(postRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }
    @DeleteMapping("/communities/{communityId}/posts")
    public ResponseEntity<List<Post>> deleteAllPostsOfCommunity(@PathVariable(value = "communityId") Long communityId) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | memberUtil.isAuthorized(communityId, userDetails.getId())))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException("Not found Community with id = " + communityId);
        }

        postRepository.deleteByCommunityId(communityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // get all posts by title search
    @GetMapping("/posts/searchTitle")
    public ResponseEntity<List<Post>> getAllPostsByTitle(@RequestParam(value = "title") String title,
    @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
    @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findPostsByTitleContaining(title, pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }
    // get all posts by title search
    @GetMapping("/posts/search")
    public ResponseEntity<List<Post>> getAllPostsByTitleOrText(@RequestParam(value = "search") String search,
                                                         @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                         @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findPostsByTitleContainingOrTextContaining(search, search, pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    // get sorted posts by date
    @GetMapping("/posts/sortDescending")
    public ResponseEntity<List<Post>> getAllPostsSortedByDateDescending(
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findAllByOrderByDateDesc(pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    // get sorted posts by date ascending
    @GetMapping("/posts/sortAscending")
    public ResponseEntity<List<Post>> getAllPostsSortedByDateAscending(
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findAllByOrderByDateAsc(pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    // get all posts by title ascending
    @GetMapping("/posts/sortTitleAscending")
    public ResponseEntity<List<Post>> getAllPostsSortedByTitleAscending(
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findAllByOrderByTitleAsc(pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    // get all posts by title descending
    @GetMapping("/posts/sortTitleDescending")
    public ResponseEntity<List<Post>> getAllPostsSortedByTitleDescending(
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findAllByOrderByTitleDesc(pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    // get all students who liked the post
    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<Set<Student>> getAllUsersWhoLikedPost(@PathVariable(value = "postId") Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Not found Post with id = " + postId);
        }
        Post post = postRepository.findPostById(postId);

        Set<Student> students = post.getLikes();

        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    // get all posts student liked
    @GetMapping("/students/{studentId}/likedPosts")
    public ResponseEntity<Set<Post>> getLikedPosts(@PathVariable(value = "studentId") Long studentId) {
        if(!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        Student student = studentRepository.findStudentById(studentId);

        Set<Post> posts = student.getLikedPosts();

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }



    // student likes post
    @PostMapping("students/{studentId}/posts/{postId}/like")
    public ResponseEntity<Post> likePost(@PathVariable(value = "studentId") Long studentId,
                                         @PathVariable(value = "postId") Long postId) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))
                | memberUtil.isAuthorized(postRepository.findById(postId).get().getCommunity().getId(), userDetails.getId())))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Not found Post with id = " + postId);
        }

        Post post = postRepository.findById(postId).get();
        Student student = studentRepository.findById(studentId).get();

        if (post.getLikes().contains(student)) {
            student.getLikedPosts().remove(post);
            post.getLikes().remove(student);
            post.setLikeNum(post.getLikeNum().intValue() - 1);
        } else {
            student.getLikedPosts().add(post);
            post.getLikes().add(student);
            post.setLikeNum(post.getLikeNum().intValue() + 1);
        }

        studentRepository.save(student);
        Post updatedPost = postRepository.save(post);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    // get all posts sorted by like number descending
    @GetMapping("/posts/sortLikeNumDescending")
    public ResponseEntity<List<Post>> getAllPostsSortedByLikeNumDescending(
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findAllByOrderByLikeNumDesc(pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    // get like number of single post
    @GetMapping("/posts/{postId}/likeNum")
    public ResponseEntity<Integer> getLikeNum(@PathVariable(value = "postId") Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Not found Post with id = " + postId);
        }
        Post post = postRepository.findPostById(postId);

        return new ResponseEntity<>(post.getLikeNum(), HttpStatus.OK);

    }

    // get all global posts
    @GetMapping("/posts/global")
    public ResponseEntity<List<Post>> getAllGlobalPosts(
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findAllByVisibilityContaining("global", pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    // is post liked by student
    @GetMapping("/posts/{postId}/isLikedByStudent/{studentId}")
    public ResponseEntity<Boolean> isPostLikedByStudent(@PathVariable(value = "postId") Long postId,
                                                       @PathVariable(value = "studentId") Long studentId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Post with id = " + postId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(post.getLikes().contains(student), HttpStatus.OK);
    }

    // is post is bookmarked by student
    @GetMapping("/posts/{postId}/isBookmarkedByStudent/{studentId}")
    public ResponseEntity<Boolean> isPostBookmarkedByStudent(@PathVariable(value = "postId") Long postId,
                                                       @PathVariable(value = "studentId") Long studentId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Post with id = " + postId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(post.getStudents().contains(student), HttpStatus.OK);
    }

    // get all private posts of a community
    @GetMapping("/posts/communities/{communityId}/private")
    public ResponseEntity<List<Post>> getAllPrivatePostsOfCommunity(
            @PathVariable(value = "communityId") Long communityId,
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Page<Post> posts = postRepository.findAllByVisibilityAndCommunity("private", community, pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    // get all private posts of a community
    @GetMapping("/posts/communities/{communityId}/global")
    public ResponseEntity<List<Post>> getAllGlobalPostsOfCommunity(
            @PathVariable(value = "communityId") Long communityId,
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Page<Post> posts = postRepository.findAllByVisibilityAndCommunity("global", community, pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    // get all private posts of a communities which a student is member of
    @GetMapping("/posts/{studentId}/private")
    public ResponseEntity<List<Post>> getAllPrivatePostsOfCommunitiesStudentIsMemberOf(
            @PathVariable(value = "studentId") Long studentId,
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        Set<Member> memberOfSet = student.getMemberOf();

        List<Community> communities = new ArrayList<>();

        for (Member member : memberOfSet) {
            communities.add(member.getCommunity());
        }

        Page<Post> posts = postRepository.findAllByVisibilityAndCommunityIn("private", communities, pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }
}
