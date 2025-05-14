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
@Table(name = "floor_details")
public class FloorDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String floorName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tower_id")
	@JsonIgnore
	private TowerDetails tower;

	public long getId() {
		return id;
	}

	public String getFloorName() {
		return floorName;
	}

	public TowerDetails getTower() {
		return tower;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}

	public void setTower(TowerDetails tower) {
		this.tower = tower;
	}

	public FloorDetails() {
	}

	public FloorDetails(String floorName, TowerDetails tower) {
		this.floorName = floorName;
		this.tower = tower;
	}

	@Override
	public String toString() {
		return "FloorDetails [id=" + id + ", floorName=" + floorName + ", tower=" + tower + "]";
	}

}
