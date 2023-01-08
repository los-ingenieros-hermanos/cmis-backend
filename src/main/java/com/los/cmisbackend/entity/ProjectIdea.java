package com.los.cmisbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "project_idea")
public class ProjectIdea {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="text")
    private String text;

    @Lob
    @Column(name = "image", nullable = true, columnDefinition = "MEDIUMBLOB", length = Integer.MAX_VALUE)
    private String image;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="student_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "likedProjectIdeas")
    @JsonIgnore
    private Set<Student> likes = new HashSet<>();

    @Column(name="like_num")
    private Integer likeNum = Integer.valueOf(0);

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "bookmarkedProjectIdeas")
    @JsonIgnore
    private Set<Student> bookMarkedBy = new HashSet<>();

    // no arg constructor
    public ProjectIdea() {

    }

    public ProjectIdea(String title, String text, String image) {
        this.title = title;
        this.text = text;
        this.image = image;
    }

    // getter setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Set<Student> getLikes() {
        return likes;
    }

    public void setLikes(Set<Student> likes) {
        this.likes = likes;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public Set<Student> getBookMarkedBy() {
        return bookMarkedBy;
    }

    public void setBookMarkedBy(Set<Student> bookMarkedBy) {
        this.bookMarkedBy = bookMarkedBy;
    }
}
