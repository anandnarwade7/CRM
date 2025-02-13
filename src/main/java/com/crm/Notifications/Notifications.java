package com.crm.Notifications;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Notifications {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private boolean isSeen;
	private String message;
	private String email;
	@Column(columnDefinition = "bigint default 0")
	private long createdOn;

	private String redirectKey;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean getIsSeen() {
		return isSeen;
	}

	public void setIsSeen(boolean isSeen) {
		this.isSeen = isSeen;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSeen(boolean isSeen) {
		this.isSeen = isSeen;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public String getRedirectKey() {
		return redirectKey;
	}

	public void setRedirectKey(String redirectKey) {
		this.redirectKey = redirectKey;
	}

	public Notifications(long id, boolean isSeen, String message, String email, long createdOn) {
		super();
		this.id = id;
		this.isSeen = isSeen;
		this.message = message;
		this.email = email;
		this.createdOn = createdOn;
	}

	public Notifications(boolean isSeen, String message, String email, String redirectKey, long createdOn) {
		super();
		this.isSeen = isSeen;
		this.message = message;
		this.email = email;
		this.redirectKey = redirectKey;
		this.createdOn = createdOn;
	}

	public Notifications() {
		super();
	}

	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
	}
}
