package com.los.cmisbackend.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="member")
public class Member {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "student_id")
	private Student student;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "community_id")
	private Community community;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name="authorizations")
	private Set<String> authorizations = Set.of("NONE");

	public Member() {
		
	}

	public Member(Student student, Community community, Set<String> authorizations) {
		this.student = student;
		this.community = community;
		this.authorizations = authorizations;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}	

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public Set<String> getAuthorizations() {
		return authorizations;
	}

	public void setAuthorizations(Set<String> authorizations) {
		this.authorizations = authorizations;
	}

	public void addAuthorization(String authorization) {
		this.authorizations.add(authorization);
	}

	public void removeAuthorization(String authorization) {
		this.authorizations.remove(authorization);
	}

}
