package com.crm.project;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "flats")
public class Flat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private int flatNumber;
	private String flatSize;
	private String flatType;
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "floor_id")
	@JsonIgnore
	private FloorDetails floor;
	
	public Flat() {}

	public long getId() {
		return id;
	}

	public int getFlatNumber() {
		return flatNumber;
	}

	public String getFlatSize() {
		return flatSize;
	}

	public String getFlatType() {
		return flatType;
	}

	public String getStatus() {
		return status;
	}

	public FloorDetails getFloor() {
		return floor;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setFlatNumber(int flatNumber) {
		this.flatNumber = flatNumber;
	}

	public void setFlatSize(String flatSize) {
		this.flatSize = flatSize;
	}

	public void setFlatType(String flatType) {
		this.flatType = flatType;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setFloor(FloorDetails floor) {
		this.floor = floor;
	}

	public Flat(long id, int flatNumber, String flatSize, String flatType, String status, FloorDetails floor) {
		super();
		this.id = id;
		this.flatNumber = flatNumber;
		this.flatSize = flatSize;
		this.flatType = flatType;
		this.status = status;
		this.floor = floor;
	}

	@Override
	public String toString() {
		return "Flat [id=" + id + ", flatNumber=" + flatNumber + ", flatSize=" + flatSize + ", flatType=" + flatType
				+ ", status=" + status + ", floor=" + floor + "]";
	}

}
