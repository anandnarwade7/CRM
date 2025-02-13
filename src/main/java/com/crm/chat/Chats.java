package com.crm.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Chats {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private long userId;
	private long supportId;
	private String massages;
	private long createdOn;

	public Chats() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getSupportId() {
		return supportId;
	}

	public void setSupportId(long supportId) {
		this.supportId = supportId;
	}

	public String getMassages() {
		return massages;
	}

	public void setMassages(String massages) {
		this.massages = massages;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public Chats(long id, long userId, long supportId, String massages, long createdOn) {
		super();
		this.id = id;
		this.userId = userId;
		this.supportId = supportId;
		this.massages = massages;
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "Chats [id=" + id + ", userId=" + userId + ", supportId=" + supportId + ", massages=" + massages
				+ ", createdOn=" + createdOn + "]";
	}

}
