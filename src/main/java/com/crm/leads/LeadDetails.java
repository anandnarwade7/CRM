package com.crm.leads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crm.user.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;

@Entity
public class LeadDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String leadName;
	private String leadEmail;
	private String leadmobile;
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
	private String crPerson;
	@Column(name = "jsonData", nullable = true, length = 4000)
	private String massagesJsonData;
	@Column(name = "dynamicFields", nullable = true, length = 2000)
	private String dynamicFieldsJson;
	private String agreement;
	private String stampDuty;
	private String tdsDoc;
	private String bankSanction;
	private String action;
	private long createOn;
	private long salesId;

	@Transient
	private List<Map<String, String>> conversationLogs = new ArrayList<>();

	@Transient
	private Map<String, Object> dynamicFields = new HashMap<>();

	public LeadDetails() {
	}

	public long getId() {
		return id;
	}

	public String getLeadName() {
		return leadName;
	}

	public String getLeadEmail() {
		return leadEmail;
	}

	public String getLeadmobile() {
		return leadmobile;
	}

	public long getDate() {
		return date;
	}

	public long getUserId() {
		return userId;
	}

	public long getAssignedTo() {
		return assignedTo;
	}

	public Status getStatus() {
		return status;
	}

	public String getAdName() {
		return adName;
	}

	public String getAdSet() {
		return adSet;
	}

	public String getCampaign() {
		return campaign;
	}

	public String getCity() {
		return city;
	}

	public String getCallTime() {
		return callTime;
	}

	public String getPropertyRange() {
		return propertyRange;
	}

	public String getCrPerson() {
		return crPerson;
	}

	public String getAction() {
		return action;
	}

	public long getCreateOn() {
		return createOn;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLeadName(String leadName) {
		this.leadName = leadName;
	}

	public void setLeadEmail(String leadEmail) {
		this.leadEmail = leadEmail;
	}

	public void setLeadmobile(String leadmobile) {
		this.leadmobile = leadmobile;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setAssignedTo(long assignedTo) {
		this.assignedTo = assignedTo;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public void setAdSet(String adSet) {
		this.adSet = adSet;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}

	public void setPropertyRange(String propertyRange) {
		this.propertyRange = propertyRange;
	}

	public void setCrPerson(String crPerson) {
		this.crPerson = crPerson;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setCreateOn(long createOn) {
		this.createOn = createOn;
	}

	public void setConversationLogs(List<Map<String, String>> conversationLogs) {
		this.conversationLogs = conversationLogs;
	}

	public void setDynamicFields(Map<String, Object> dynamicFields) {
		this.dynamicFields = dynamicFields;
	}

	public String getMassagesJsonData() {
		return massagesJsonData;
	}

	public String getDynamicFieldsJson() {
		return dynamicFieldsJson;
	}

	public void setMassagesJsonData(String massagesJsonData) {
		this.massagesJsonData = massagesJsonData;
	}

	public void setDynamicFieldsJson(String dynamicFieldsJson) {
		this.dynamicFieldsJson = dynamicFieldsJson;
	}

	@PrePersist
	protected void prePersistFunction() {
		this.createOn = System.currentTimeMillis();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getConversationLogs() {
		if (massagesJsonData != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				conversationLogs = objectMapper.readValue(massagesJsonData, List.class);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return conversationLogs;
	}

	public void addConversationLog(String date, String comment) {
		Map<String, String> logEntry = new HashMap<>();
//		logEntry.put("date", date);
		logEntry.put("comment", comment);

		List<Map<String, String>> logs = getConversationLogs();
		logs.add(logEntry);

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			this.massagesJsonData = objectMapper.writeValueAsString(logs);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public List<Map<String, Object>> getDynamicFields() {
		if (this.dynamicFieldsJson == null || this.dynamicFieldsJson.isEmpty()) {
			return new ArrayList<>();
		}
		try {
			return objectMapper.readValue(this.dynamicFieldsJson, new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public void addDynamicField(String key, Object value) {
		List<Map<String, Object>> fields = getDynamicFields();
		Map<String, Object> fieldEntry = new HashMap<>();
		fieldEntry.put(key, value);
		fields.add(fieldEntry);

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			this.dynamicFieldsJson = objectMapper.writeValueAsString(fields);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public String getAgreement() {
		return agreement;
	}

	public String getStampDuty() {
		return stampDuty;
	}

	public String getTdsDoc() {
		return tdsDoc;
	}

	public String getBankSanction() {
		return bankSanction;
	}

	public static ObjectMapper getObjectmapper() {
		return objectMapper;
	}

	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}

	public void setStampDuty(String stampDuty) {
		this.stampDuty = stampDuty;
	}

	public void setTdsDoc(String tdsDoc) {
		this.tdsDoc = tdsDoc;
	}

	public void setBankSanction(String bankSanction) {
		this.bankSanction = bankSanction;
	}

	public long getSalesId() {
		return salesId;
	}

	public void setSalesId(long salesId) {
		this.salesId = salesId;
	}

	public LeadDetails(long id, String leadName, String leadEmail, String leadmobile, long date, long userId,
			long assignedTo, Status status, String adName, String adSet, String campaign, String city, String callTime,
			String propertyRange, String crPerson, String massagesJsonData, String dynamicFieldsJson, String agreement,
			String stampDuty, String tdsDoc, String bankSanction, String action, long createOn,
			List<Map<String, String>> conversationLogs, Map<String, Object> dynamicFields, long salesId) {
		this.id = id;
		this.leadName = leadName;
		this.leadEmail = leadEmail;
		this.leadmobile = leadmobile;
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
		this.crPerson = crPerson;
		this.massagesJsonData = massagesJsonData;
		this.dynamicFieldsJson = dynamicFieldsJson;
		this.agreement = agreement;
		this.stampDuty = stampDuty;
		this.tdsDoc = tdsDoc;
		this.bankSanction = bankSanction;
		this.action = action;
		this.createOn = createOn;
		this.conversationLogs = conversationLogs;
		this.dynamicFields = dynamicFields;
		this.salesId = salesId;
	}

	@Override
	public String toString() {
		return "LeadDetails [id=" + id + ", leadName=" + leadName + ", leadEmail=" + leadEmail + ", leadmobile="
				+ leadmobile + ", date=" + date + ", userId=" + userId + ", assignedTo=" + assignedTo + ", status="
				+ status + ", adName=" + adName + ", adSet=" + adSet + ", campaign=" + campaign + ", city=" + city
				+ ", callTime=" + callTime + ", propertyRange=" + propertyRange + ", crPerson=" + crPerson
				+ ", massagesJsonData=" + massagesJsonData + ", dynamicFieldsJson=" + dynamicFieldsJson + ", agreement="
				+ agreement + ", stampDuty=" + stampDuty + ", tdsDoc=" + tdsDoc + ", bankSanction=" + bankSanction
				+ ", action=" + action + ", createOn=" + createOn + ", salesId=" + salesId + ", conversationLogs="
				+ conversationLogs + ", dynamicFields=" + dynamicFields + "]";
	}
}
