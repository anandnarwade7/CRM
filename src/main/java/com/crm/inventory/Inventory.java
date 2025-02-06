package com.crm.inventory;

import com.crm.user.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long projectId;
	private String towerName;
	private int flatNumber;
	private String flatArea;
	private String flatType;
	private String action;
	private Status status; /* (Sold/UnSold) */
	private long createdOn;

	public Inventory() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getTowerName() {
		return towerName;
	}

	public void setTowerName(String towerName) {
		this.towerName = towerName;
	}

	public int getFlatNumber() {
		return flatNumber;
	}

	public void setFlatNumber(int flatNumber) {
		this.flatNumber = flatNumber;
	}

	public String getFlatArea() {
		return flatArea;
	}

	public void setFlatArea(String flatArea) {
		this.flatArea = flatArea;
	}

	public String getFlatType() {
		return flatType;
	}

	public void setFlatType(String flatType) {
		this.flatType = flatType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "Inventory [id=" + id + ", projectId=" + projectId + ", towerName=" + towerName + ", flatNumber="
				+ flatNumber + ", flatArea=" + flatArea + ", flatType=" + flatType + ", action=" + action + ", status="
				+ status + ", createdOn=" + createdOn + "]";
	}

	public Inventory(long id, long projectId, String towerName, int flatNumber, String flatArea, String flatType,
			String action, Status status, long createdOn) {
		super();
		this.id = id;
		this.projectId = projectId;
		this.towerName = towerName;
		this.flatNumber = flatNumber;
		this.flatArea = flatArea;
		this.flatType = flatType;
		this.action = action;
		this.status = status;
		this.createdOn = createdOn;
	}

}
