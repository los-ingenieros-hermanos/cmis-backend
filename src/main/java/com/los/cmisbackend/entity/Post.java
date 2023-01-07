package com.los.cmisbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="post")
public class Post {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="text", nullable = false, length = 5000)
    private String text;

    @Column(name="visibility", nullable = false)
    private String visibility;

    // mappings

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "likedPosts")
    @JsonIgnore
    private Set<Student> likes = new HashSet<>();

    @Column(name="like_num")
    private Integer likeNum = Integer.valueOf(0);

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name= "event_id")
    private List<Event> event = new ArrayList<>();

    // add date to the post
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name= "date_id")
    private Date date;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "bookmarkedPosts")
    @JsonIgnore
    private Set<Student> students = new HashSet<>();


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="community_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Community community;

    @Lob
	@Column(name = "image", nullable = true, columnDefinition = "MEDIUMBLOB", length = Integer.MAX_VALUE)
    private String image;

    public Post() {

    }

    public Post(String title, String text, String visibility, List<Event> event, Set<Student> students, Community community, String image) {
        this.title = title;
        this.text = text;
        this.visibility = visibility;
        this.event = event;
        this.students = students;
        this.community = community;
        this.image = image;
    }

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

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public List<Event> getEvent() {
        return event;
    }

    public void setEvent(List<Event> event) {
        this.event = event;
    }

    public void addEvent(Event event) {
        this.event.add(event);
    }

    public void removeEvent(Event event) {
        this.event.remove(event);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", visibility='" + visibility + '\'' +
                ", event=" + event +
                ", students=" + students +
                ", community=" + community +
                ", image='" + image + '\'' +
                '}';
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
