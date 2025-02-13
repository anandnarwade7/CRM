package com.crm.support;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.crm.user.Status;
import com.crm.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class Support {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.SET_NULL)
	private User user;
	private String query;
	private long createdOn;
	private Status status;

	public Support() {
	}

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Support(long id, User user, Status status, String query, long createdOn) {
		super();
		this.id = id;
		this.user = user;
		this.status = status;
		this.query = query;
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "Support [id=" + id + ", user=" + user + ", status=" + status + ", query=" + query + ", createdOn="
				+ createdOn + "]";
	}

}
