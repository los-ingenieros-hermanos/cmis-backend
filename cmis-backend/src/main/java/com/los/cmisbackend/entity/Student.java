package com.los.cmisbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.los.cmisbackend.util.BCryptPasswordDeserializer;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="student")
public class Student {

    @Id
    Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name= "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "bookmarkedPost_student",
            joinColumns = { @JoinColumn(name = "student_id") },
            inverseJoinColumns = { @JoinColumn(name = "post_id") })
    private Set<Post> bookmarkedPosts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "followers")
    @JsonIgnore
    private Set<Community> followingCommunities = new HashSet<>();

    public Student () {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Post> getBookMarkedPosts() {
        return bookmarkedPosts;
    }

    public void setBookMarkedPosts(Set<Post> bookMarkedPosts) {
        this.bookmarkedPosts = bookMarkedPosts;
    }


    public Set<Community> getFollowingCommunities() {
        return followingCommunities;
    }

    public void setFollowingCommunities(Set<Community> followingCommunities) {
        this.followingCommunities = followingCommunities;
    }

    public void addPost(Post post) {
        bookmarkedPosts.add(post);
    }

    public void removePost(Post post) {
        bookmarkedPosts.remove(post);
    }

    public void addCommunityToFollowing(Community community) {
        followingCommunities.add(community);
    }

    public void deleteCommunityFromFollowing(Community community) {
        followingCommunities.remove(community);
    }
}