package com.crm.project;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class FlatBookDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long clientId;
	private String clientName;
	private long salesId;
	private String salesName;
	private long crmId;
	private String crmName;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flat_id")
	private Flat flat;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public long getSalesId() {
		return salesId;
	}

	public void setSalesId(long salesId) {
		this.salesId = salesId;
	}

	public String getSalesName() {
		return salesName;
	}

	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}

	public long getCrmId() {
		return crmId;
	}

	public void setCrmId(long crmId) {
		this.crmId = crmId;
	}

	public String getCrmName() {
		return crmName;
	}

	public void setCrmName(String crmName) {
		this.crmName = crmName;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public FlatBookDetails(long clientId, String clientName, long salesId, String salesName, long crmId,
			String crmName, Flat flat) {
		super();
		this.clientId = clientId;
		this.clientName = clientName;
		this.salesId = salesId;
		this.salesName = salesName;
		this.crmId = crmId;
		this.crmName = crmName;
		this.flat = flat;
	}

	public FlatBookDetails() {}
	
	@Override
	public String toString() {
		return "FlatBookDetails [id=" + id + ", clientId=" + clientId + ", clientName=" + clientName + ", salesId="
				+ salesId + ", salesName=" + salesName + ", crmId=" + crmId + ", crmName=" + crmName + ", flat=" + flat
				+ "]";
	}

}
