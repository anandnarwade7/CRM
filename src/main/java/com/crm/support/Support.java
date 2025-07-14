package com.crm.support;

import com.crm.user.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "support")
public class Support {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String role;
	private long userId;
	private String email;
	private String phone;
	private String department;
	private String query;
	private long adminId;
	private long createdOn;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 100)
	private Status status;

	public Support() {
	}

	public long getId() {
		return id;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void markApproved() {
		this.status = Status.SOLVED;
	}

	public void markRejected() {
		this.status = Status.REJECTED;
	}

	public void markDeleted() {
		this.status = Status.DELETED;
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

	public long getAdminId() {
		return adminId;
	}

	public void setAdminId(long adminId) {
		this.adminId = adminId;
	}

	public String getName() {
		return name;
	}

	public String getRole() {
		return role;
	}

	public long getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getDepartment() {
		return department;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Support(long id, String name, String role, long userId, String email, String phone, String department,
			String query, long adminId, long createdOn, Status status) {
		super();
		this.id = id;
		this.name = name;
		this.role = role;
		this.userId = userId;
		this.email = email;
		this.phone = phone;
		this.department = department;
		this.query = query;
		this.adminId = adminId;
		this.createdOn = createdOn;
		this.status = status;
	}

	@Override
	public String toString() {
		return "Support [id=" + id + ", " + (name != null ? "name=" + name + ", " : "")
				+ (role != null ? "role=" + role + ", " : "") + "userId=" + userId + ", "
				+ (email != null ? "email=" + email + ", " : "") + (phone != null ? "phone=" + phone + ", " : "")
				+ (department != null ? "department=" + department + ", " : "")
				+ (query != null ? "query=" + query + ", " : "") + "adminId=" + adminId + ", createdOn=" + createdOn
				+ ", " + (status != null ? "status=" + status : "") + "]";
	}
}
