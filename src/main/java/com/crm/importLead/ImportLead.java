package com.crm.importLead;

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
@Table(name = "importLead")
public class ImportLead {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String email;
	private String mobileNumber;
	private long date;
	private long userId;
	private long assignedTo;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 100)
	private Status status;
	private String adName;
	private String adSet;
	private String campaign;
	private String city;
	private String callTime;
	private String propertyRange;
//	private List<String> fields;
	@Column(name = "jsonData", nullable = true, length = 2000)
	private String jsonData;
	private long importedOn;
	private String salesPerson;

	public ImportLead() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
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

	public long getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(long assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public String getAdSet() {
		return adSet;
	}

	public void setAdSet(String adSet) {
		this.adSet = adSet;
	}

	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCallTime() {
		return callTime;
	}

	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}

	public String getPropertyRange() {
		return propertyRange;
	}

	public void setPropertyRange(String propertyRange) {
		this.propertyRange = propertyRange;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public long getImportedOn() {
		return importedOn;
	}

	public void setImportedOn(long importedOn) {
		this.importedOn = importedOn;
	}

	@PrePersist
	protected void prePersistFunction() {
		this.importedOn = System.currentTimeMillis();
	}

	public String getSalesPerson() {
		return salesPerson;
	}

	public void setSalesPerson(String salesPerson) {
		this.salesPerson = salesPerson;
	}

	public ImportLead(long id, String name, String email, String mobileNumber, long date, long userId, long assignedTo,
			Status status, String adName, String adSet, String campaign, String city, String callTime,
			String propertyRange, String jsonData, String salesPerson, long importedOn) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.date = date;
		this.userId = userId;
		this.assignedTo = assignedTo;
		this.status = status;
		this.adName = adName;
		this.adSet = adSet;
		this.campaign = campaign;
		this.city = city;
		this.callTime = callTime;
		this.propertyRange = propertyRange;
		this.jsonData = jsonData;
		this.salesPerson = salesPerson;
		this.importedOn = importedOn;
	}

	@Override
	public String toString() {
		return "ImportLead [id=" + id + ", name=" + name + ", email=" + email + ", mobileNumber=" + mobileNumber
				+ ", date=" + date + ", userId=" + userId + ", assignedTo=" + assignedTo + ", status=" + status
				+ ", adName=" + adName + ", adSet=" + adSet + ", campaign=" + campaign + ", city=" + city
				+ ", callTime=" + callTime + ", propertyRange=" + propertyRange + ", jsonData=" + jsonData
				+ ", importedOn=" + importedOn + ", salesPerson=" + salesPerson + "]";
	}

}
