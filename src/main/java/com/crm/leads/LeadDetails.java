package com.crm.leads;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LeadDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String leadName;
	private String leadEmail;
	private String leadmobile;
	private String leadContactDate;
//	private List<String> leadMassages;

	@Column(name = "jsonData", nullable = true, length = 4000)
	private String massagesJsonData;

	private long userId;
	private String invoiceUrl;
	private String action;
	private long createOn;

	public LeadDetails() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLeadName() {
		return leadName;
	}

	public void setLeadName(String leadName) {
		this.leadName = leadName;
	}

	public String getLeadEmail() {
		return leadEmail;
	}

	public void setLeadEmail(String leadEmail) {
		this.leadEmail = leadEmail;
	}

	public String getLeadmobile() {
		return leadmobile;
	}

	public void setLeadmobile(String leadmobile) {
		this.leadmobile = leadmobile;
	}

	public String getLeadContactDate() {
		return leadContactDate;
	}

	public void setLeadContactDate(String leadContactDate) {
		this.leadContactDate = leadContactDate;
	}

	public String getMassagesJsonData() {
		return massagesJsonData;
	}

	public void setMassagesJsonData(String massagesJsonData) {
		this.massagesJsonData = massagesJsonData;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getInvoiceUrl() {
		return invoiceUrl;
	}

	public void setInvoiceUrl(String invoiceUrl) {
		this.invoiceUrl = invoiceUrl;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getCreateOn() {
		return createOn;
	}

	public void setCreateOn(long createOn) {
		this.createOn = createOn;
	}

	public LeadDetails(long id, String leadName, String leadEmail, String leadmobile, String leadContactDate,
			String massagesJsonData, long userId, String invoiceUrl, String action, long createOn) {
		super();
		this.id = id;
		this.leadName = leadName;
		this.leadEmail = leadEmail;
		this.leadmobile = leadmobile;
		this.leadContactDate = leadContactDate;
		this.massagesJsonData = massagesJsonData;
		this.userId = userId;
		this.invoiceUrl = invoiceUrl;
		this.action = action;
		this.createOn = createOn;
	}

	@Override
	public String toString() {
		return "LeadDetails [id=" + id + ", leadName=" + leadName + ", leadEmail=" + leadEmail + ", leadmobile="
				+ leadmobile + ", leadContactDate=" + leadContactDate + ", massagesJsonData=" + massagesJsonData
				+ ", userId=" + userId + ", invoiceUrl=" + invoiceUrl + ", action=" + action + ", createOn=" + createOn
				+ "]";
	}

}
