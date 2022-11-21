package com.los.cmisbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.los.cmisbackend.util.BCryptPasswordDeserializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

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

	@Size(min = 60, max = 60)
	@JsonDeserialize(using = BCryptPasswordDeserializer.class )
	@Column(name="password", nullable = false, length = 60)
	private String password;

	// photo
	// posts
	// members

	public Community() {
	}


	public Community(String name, String email, String info, String password) {
		this.name = name;
		this.email = email;
		this.info = info;
		this.password = password;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Community [id=" + id + ", name=" + name + ", email=" + email + ", info=" + info + "]";
	}

}
