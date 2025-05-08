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
@Table(name = "tower_details")
public class TowerDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String towerName;

	private int totalTowers;
	private int totalFloors;
	private int flatPerFloor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	@JsonIgnore
	private ProjectDetails project;

	private String layoutImage;

	public long getId() {
		return id;
	}

	public String getTowerName() {
		return towerName;
	}

	public ProjectDetails getProject() {
		return project;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setTowerName(String towerName) {
		this.towerName = towerName;
	}

	public void setProject(ProjectDetails project) {
		this.project = project;
	}

	public int getTotalTowers() {
		return totalTowers;
	}

	public int getTotalFloors() {
		return totalFloors;
	}

	public void setTotalTowers(int totalTowers) {
		this.totalTowers = totalTowers;
	}

	public void setTotalFloors(int totalFloors) {
		this.totalFloors = totalFloors;
	}

	public int getFlatPerFloor() {
		return flatPerFloor;
	}

	public void setFlatPerFloor(int flatPerFloor) {
		this.flatPerFloor = flatPerFloor;
	}

	public String getLayoutImage() {
		return layoutImage;
	}

	public void setLayoutImage(String layoutImage) {
		this.layoutImage = layoutImage;
	}

	public TowerDetails() {
	}

	public TowerDetails(long id, String towerName, int totalTowers, int totalFloors, int flatPerFloor,
			ProjectDetails project, String layoutImage) {
		super();
		this.id = id;
		this.towerName = towerName;
		this.totalTowers = totalTowers;
		this.totalFloors = totalFloors;
		this.flatPerFloor = flatPerFloor;
		this.layoutImage = layoutImage;
		this.project = project;
	}

	@Override
	public String toString() {
		return "TowerDetails [id=" + id + ", towerName=" + towerName + ", totalTowers=" + totalTowers + ", totalFloors="
				+ totalFloors + ", flatPerFloor=" + flatPerFloor + ", project=" + project + ", layoutImage="
				+ layoutImage + "]";
	}

}
