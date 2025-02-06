package com.crm.support;

import com.crm.user.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Support {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long userId;
	private String query;
	private long createdOn;
	private Status status;

	public Support() {
	}

	public long getId() {
		return id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public Support(long id, long userId, Status status, String query, long createdOn) {
		super();
		this.id = id;
		this.userId = userId;
		this.status = status;
		this.query = query;
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "Support [id=" + id + ", userId=" + userId + ", status=" + status + ", query=" + query + ", createdOn="
				+ createdOn + "]";
	}

}
