package com.los.cmisbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

import java.util.HashSet;
import java.util.Objects;
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
    
	@Lob
	@Column(name = "image", nullable = true, columnDefinition = "MEDIUMBLOB", length = Integer.MAX_VALUE)
    private String image;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "student_tag",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> interests = new HashSet<>();

    // many to many relationship with event
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "event_student",
            joinColumns = { @JoinColumn(name = "student_id") },
            inverseJoinColumns = { @JoinColumn(name = "event_id") })
    private Set<Event> events = new HashSet<>();

    @OneToMany(
		cascade = {CascadeType.REMOVE
				,CascadeType.MERGE
				,CascadeType.REFRESH})
	@JoinColumn(name = "student_id")
	@JsonIgnore
    private Set<MemberApplication> memberApplications = new HashSet<>();

    @OneToMany(
		cascade = {CascadeType.REMOVE
				,CascadeType.MERGE
				,CascadeType.REFRESH})
	@JoinColumn(name = "student_id")
	@JsonIgnore
    private Set<Member> memberOf = new HashSet<>();

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="instagram")
    private String instagram;

    @Column(name="linkedin")
    private String linkedin;

    @Column(name="github")
    private String github;

    @Column(name="twitter")
    private String twitter;

    public Student () {

    }

    public Student(User user, Set<Post> bookmarkedPosts, Set<Community> followingCommunities,
                    String image, Set<Tag> interests)
    {
        this.user = user;
        this.bookmarkedPosts = bookmarkedPosts;
        this.followingCommunities = followingCommunities;
        this.image = image;
        this.interests = interests;
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<Member> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(Set<Member> memberOf) {
        this.memberOf = memberOf;
    }

    public void addMember(Member member) {
        memberOf.add(member);
    }

    public void removeMember(Member member) {
        memberOf.remove(member);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<Tag> getInterests() {
        return interests;
    }

    public void setInterests(Set<Tag> interests) {
        this.interests = interests;
    }

    public Set<Post> getBookmarkedPosts() {
        return bookmarkedPosts;
    }

    public void setBookmarkedPosts(Set<Post> bookmarkedPosts) {
        this.bookmarkedPosts = bookmarkedPosts;
    }

    public void addInterest(Tag tag) {
        interests.add(tag);
    }

    public void removeTag(Tag tag) {
        interests.remove(tag);
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }

    public Set<MemberApplication> getMemberApplications() {
        return memberApplications;
    }

    public void setMemberApplications(Set<MemberApplication> memberApplications) {
        this.memberApplications = memberApplications;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void addMemberApplication(MemberApplication memberApplication) {
        memberApplications.add(memberApplication);
    }

    public void removeMemberApplication(MemberApplication memberApplication) {
        memberApplications.remove(memberApplication);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id.equals(student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}