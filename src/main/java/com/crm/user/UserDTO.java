package com.crm.user;

import jakarta.persistence.PrePersist;

public class UserDTO {

	private long id;
	private String name;
	private String email;
	private String mobile;
	private String role;
	private long createdOn;
	private String action;
	private String profilePic;

	public UserDTO() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
	}

	public UserDTO(long id, String name, String email, String mobile, String role, long createdOn, String action,
			String profilePic) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.role = role;
		this.createdOn = createdOn;
		this.action = action;
		this.profilePic = profilePic;
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", name=" + name + ", email=" + email + ", mobile=" + mobile + ", role=" + role
				+ ", createdOn=" + createdOn + ", action=" + action + ", profilePic=" + profilePic + "]";
	}

	public UserDTO(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.mobile = user.getMobile();
		this.role = user.getRole();
		this.profilePic = user.getProfilePic();
		this.action = user.getAction().toString();
		this.createdOn = user.getCreatedOn();
	}

}