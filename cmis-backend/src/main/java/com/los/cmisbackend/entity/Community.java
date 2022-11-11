package com.los.cmisbackend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="community")
public class Community {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@Column(name="name")
	private String name;

	@Column(name="email")
	private String email;

	@Column(name="info")
	private String info;

	// photo
	// posts
	// members

	public Community() {
	}



	public Community(int id, String name, String email, String info) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.info = info;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getInfo() {
		return info;
	}



	public void setInfo(String info) {
		this.info = info;
	}



	@Override
	public String toString() {
		return "Community [id=" + id + ", name=" + name + ", email=" + email + ", info=" + info + "]";
	}

}
