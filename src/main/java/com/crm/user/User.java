package com.crm.user;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String role; /* (Admin/Sales/CRM) */
	private String firstName;
	private String lastName;
	private String email;
	private String mobile;
	private Status action; /* (Block/Unblock) */
	private String password;
	@Nullable
	private String otp;
	@Nullable
	private LocalDateTime otpCreationTime;
	private long createdOn;
	private long updatedOn;
	private String profilePic;

	public User() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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

	public Status getAction() {
		return action;
	}

	public void setAction(Status action) {
		this.action = action;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public LocalDateTime getOtpCreationTime() {
		return otpCreationTime;
	}

	public void setOtpCreationTime(LocalDateTime otpCreationTime) {
		this.otpCreationTime = otpCreationTime;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public long getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(long updatedOn) {
		this.updatedOn = updatedOn;
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

	public User(long id, String role, String firstName, String lastName, String email, String mobile, Status action,
			String password, String otp, LocalDateTime otpCreationTime, long createdOn, long updatedOn,
			String profilePic) {
		super();
		this.id = id;
		this.role = role;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobile = mobile;
		this.action = action;
		this.password = password;
		this.otp = otp;
		this.otpCreationTime = otpCreationTime;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.profilePic = profilePic;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", role=" + role + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
				+ email + ", mobile=" + mobile + ", action=" + action + ", password=" + password + ", otp=" + otp
				+ ", otpCreationTime=" + otpCreationTime + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn
				+ ", profilePic=" + profilePic + "]";
	}

}
