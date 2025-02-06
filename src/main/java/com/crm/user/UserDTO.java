package com.crm.user;

import jakarta.persistence.PrePersist;

public class UserDTO {

	private long id;
	private String firstName;
	private String lastName;
	private String email;
	private String mobile;
	private String roleName;
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

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
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

	public UserDTO(long id, String firstName, String lastName, String email, String mobile, String roleName,
			long createdOn, String action, String profilePic) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobile = mobile;
		this.roleName = roleName;
		this.createdOn = createdOn;
		this.action = action;
		this.profilePic = profilePic;
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", mobile=" + mobile + ", roleName=" + roleName + ", createdOn=" + createdOn + ", action=" + action
				+ ", profilePic=" + profilePic + "]";
	}

}