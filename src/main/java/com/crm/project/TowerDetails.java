package com.crm.project;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.PrePersist;
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

	private String evenLayout;
	private String oddLayout;
	private String groundLayout;
	private String customLayout;

	@ElementCollection
	@CollectionTable(name = "tower_custom_layouts", joinColumns = @JoinColumn(name = "tower_id"))
	@MapKeyColumn(name = "layout_name")
	@Column(name = "layout_file_path")
	private Map<String, String> customLayouts = new HashMap<>();

	private long createdOn;

	public TowerDetails() {
	}

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

	public String getEvenLayout() {
		return evenLayout;
	}

	public String getOddLayout() {
		return oddLayout;
	}

	public String getGroundLayout() {
		return groundLayout;
	}

	public String getCustomLayout() {
		return customLayout;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setEvenLayout(String evenLayout) {
		this.evenLayout = evenLayout;
	}

	public void setOddLayout(String oddLayout) {
		this.oddLayout = oddLayout;
	}

	public void setGroundLayout(String groundLayout) {
		this.groundLayout = groundLayout;
	}

	public void setCustomLayout(String customLayout) {
		this.customLayout = customLayout;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
	}

	public Map<String, String> getCustomLayouts() {
		return customLayouts;
	}

	public void setCustomLayouts(Map<String, String> customLayouts) {
		this.customLayouts = customLayouts;
	}

	public TowerDetails(long id, String towerName, int totalTowers, int totalFloors, int flatPerFloor,
			ProjectDetails project, String evenLayout, String oddLayout, String groundLayout, String customLayout,
			Map<String, String> customLayouts, long createdOn) {
		super();
		this.id = id;
		this.towerName = towerName;
		this.totalTowers = totalTowers;
		this.totalFloors = totalFloors;
		this.flatPerFloor = flatPerFloor;
		this.project = project;
		this.evenLayout = evenLayout;
		this.oddLayout = oddLayout;
		this.groundLayout = groundLayout;
		this.customLayout = customLayout;
		this.customLayouts = customLayouts;
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "TowerDetails [id=" + id + ", " + (towerName != null ? "towerName=" + towerName + ", " : "")
				+ "totalTowers=" + totalTowers + ", totalFloors=" + totalFloors + ", flatPerFloor=" + flatPerFloor
				+ ", " + (project != null ? "project=" + project + ", " : "")
				+ (evenLayout != null ? "evenLayout=" + evenLayout + ", " : "")
				+ (oddLayout != null ? "oddLayout=" + oddLayout + ", " : "")
				+ (groundLayout != null ? "groundLayout=" + groundLayout + ", " : "")
				+ (customLayout != null ? "customLayout=" + customLayout + ", " : "")
				+ (customLayouts != null ? "customLayouts=" + customLayouts + ", " : "") + "createdOn=" + createdOn
				+ "]";
	}

	public void setCustomLayouts(String orDefault) {
		// TODO Auto-generated method stub
		
	}

}
