package com.los.cmisbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.los.cmisbackend.util.DefaultImages;

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

	@Column(name="info", length = 1000)
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

	@Column(name="type")
	private String type = "community";

	@Column(name="name")
	private String name;

	@Lob
	@Column(name = "image", nullable = true, columnDefinition = "MEDIUMBLOB", length = Integer.MAX_VALUE)
    private String image = DefaultImages.DEFAULT_COMMUNITY;

	@Lob
	@Column(name = "banner", nullable = true, columnDefinition = "MEDIUMBLOB", length = Integer.MAX_VALUE)
    private String banner = DefaultImages.DEFAULT_BANNER;

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

	@Column(name = "role")
	private String role = "community";

	@Column(name="instagram")
	private String instagram;

	@Column(name="linkedin")
	private String linkedin;

	@Column(name="github")
	private String github;

	@Column(name="twitter")
	private String twitter;

	@Column(name="application_criteria", length = 600)
	private String applicationCriteria = "No criteria";

	public Community() {
		this.type = "community";
	}

	public Community(User user, String info, String type, Set<Student> followers, String image, Set<Tag> tags,
				String banner, Integer followerCount, Integer memberCount)
	{
		this.user = user;
		this.info = info;
		this.followers = followers;
		this.image = image;
		this.tags = tags;
		this.banner = banner;
		this.type = type;
		this.name = user.getFirstName();
		this.role = "community";
		Set<Role> roles = user.getRoles();
		Role role = roles.iterator().next();
		ERole name = role.getName();
		if(name.equals(ERole.ROLE_UNVERIFIED))
			this.role = "unverified";

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
		this.name = user.getFirstName();
		this.role = "community";
		Set<Role> roles = user.getRoles();
		Role role = roles.iterator().next();
		ERole name = role.getName();
		if(name.equals(ERole.ROLE_UNVERIFIED))
			this.role = "unverified";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addFollower(Student follower) {
		followers.add(follower);
	}

	public void removeFollower(Student follower) {
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
		members.add(member);
	}

	public void removeMember(Member member) {
		members.remove(member);
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
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

	public String getApplicationCriteria() {
		return applicationCriteria;
	}

	public void setApplicationCriteria(String applicationCriteria) {
		this.applicationCriteria = applicationCriteria;
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

	public String getRole() {

		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
