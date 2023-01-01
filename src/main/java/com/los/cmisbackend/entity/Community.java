package com.los.cmisbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name="community")
public class Community {
	
	@Id
	@Column(name="id")
	private Long id;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapsId
	@JoinColumn(name= "user_id")
	private User user;

	@Column(name="info", length = 120)
	private String info;

	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			})
	@JoinTable(name = "community_follower",
			joinColumns = { @JoinColumn(name = "community_id") },
			inverseJoinColumns = { @JoinColumn(name = "student_id") })
	@JsonIgnore
	private Set<Student> followers = new HashSet<>();

	@Lob
	@Column(name = "image", nullable = true, columnDefinition = "MEDIUMBLOB", length = Integer.MAX_VALUE)
    private String image;

	@Lob
	@Column(name = "banner", nullable = true, columnDefinition = "MEDIUMBLOB", length = Integer.MAX_VALUE)
    private String banner;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "community_tag",
			joinColumns = @JoinColumn(name = "community_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags = new HashSet<>();

	@OneToMany(
		cascade = {CascadeType.REMOVE
				,CascadeType.MERGE
				,CascadeType.REFRESH})
	@JoinColumn(name = "community_id")
	@JsonIgnore
	private Set<MemberApplication> memberApplications = new HashSet<>();

	@OneToMany(
		cascade = CascadeType.ALL)
	@JoinColumn(name = "community_id")
	@JsonIgnore
	private Set<Member> members = new HashSet<>();

	@Column(name="follower_count", columnDefinition = "integer default 0")
	private Integer followerCount = 0;

	@Column(name="member_count", columnDefinition = "integer default 0")
	private Integer memberCount = 0;

	public Community() {
	}

	public Community(User user, String info, Set<Student> followers, String image, Set<Tag> tags, 
				String banner, Integer followerCount, Integer memberCount) 
	{
		this.user = user;
		this.info = info;
		this.followers = followers;
		this.image = image;
		this.tags = tags;
		this.banner = banner;
		if(followerCount != null)
			this.followerCount = followerCount;
		else
			this.followerCount = 0;

		if(memberCount != null)
			this.memberCount = memberCount;
		else
			this.memberCount = 0;	
	}

	public Community(String info) {
		this.info = info;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Set<Student> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<Student> followers) {
		this.followers = followers;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void addFollower(Student follower) {
		followerCount++;
		followers.add(follower);
	}

	public void removeFollower(Student follower) {
		followerCount--;
		followers.remove(follower);
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public void addTag(Tag tag) {
		tags.add(tag);
	}

	public void removeTag(Tag tag) {
		tags.remove(tag);
	}

	public Set<Member> getMembers() {
		return members;
	}

	public void setMembers(Set<Member> members) {
		this.members = members;
	}

	public void addMember(Member member) {
		memberCount++;
		members.add(member);
	}

	public void removeMember(Member member) {
		memberCount--;
		members.remove(member);
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public Set<MemberApplication> getMemberApplications() {
		return memberApplications;
	}

	public void setMemberApplications(Set<MemberApplication> memberApplications) {
		this.memberApplications = memberApplications;
	}

	public void addMemberApplication(MemberApplication memberApplication) {
		memberApplications.add(memberApplication);
	}

	public void removeMemberApplication(MemberApplication memberApplication) {
		memberApplications.remove(memberApplication);
	}

	public int getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(Integer followerCount) {
		if(followerCount == null)
			this.followerCount = 0;
		else
			this.followerCount = followerCount;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Integer memberCount) {
		if(memberCount == null)
			this.memberCount = 0;
		else
			this.memberCount = memberCount;
	}
}