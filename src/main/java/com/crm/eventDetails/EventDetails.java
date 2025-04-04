package com.crm.eventDetails;

import com.crm.user.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class EventDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long eventId;

	private long crManagerId;
	private long salesPersonId;
	private long leadId;
	private long clientId;

	private long flatId;

	private String propertyName;
	private String eventName;

	private double percentage;
	private double basePriceAmount;

	private double gstAmount;

	// pdf
	private String statusReport;

	// pdf
	private String architectsLetter;

	// pdf
	private String invoice;

	private long invoiceDate;
	private long dueDate;

	private long paymentDate;
	private String paidByName;

	private String receipt;

	@Enumerated(EnumType.STRING)
	@Column(name = "eventDetailsStatus", length = 100)
	private Status eventDetailsStatus;

	private long createdOn;
	private long editedOn;

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public long getCrManagerId() {
		return crManagerId;
	}

	public void setCrManagerId(long crManagerId) {
		this.crManagerId = crManagerId;
	}

	public long getSalesPersonId() {
		return salesPersonId;
	}

	public void setSalesPersonId(long salesPersonId) {
		this.salesPersonId = salesPersonId;
	}

	public long getFlatId() {
		return flatId;
	}

	public void setFlatId(long flatId) {
		this.flatId = flatId;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public double getBasePriceAmount() {
		return basePriceAmount;
	}

	public void setBasePriceAmount(double basePriceAmount) {
		this.basePriceAmount = basePriceAmount;
	}

	public double getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(double gstAmount) {
		this.gstAmount = gstAmount;
	}

	public String getStatusReport() {
		return statusReport;
	}

	public void setStatusReport(String statusReport) {
		this.statusReport = statusReport;
	}

	public String getArchitectsLetter() {
		return architectsLetter;
	}

	public void setArchitectsLetter(String architectsLetter) {
		this.architectsLetter = architectsLetter;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public long getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(long invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public long getDueDate() {
		return dueDate;
	}

	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}

	public long getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(long paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaidByName() {
		return paidByName;
	}

	public void setPaidByName(String paidByName) {
		this.paidByName = paidByName;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public Status getEventDetailsStatus() {
		return eventDetailsStatus;
	}

	public void setEventDetailsStatus(Status eventDetailsStatus) {
		this.eventDetailsStatus = eventDetailsStatus;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public long getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(long editedOn) {
		this.editedOn = editedOn;
	}

	public long getLeadId() {
		return leadId;
	}

	public long getClientId() {
		return clientId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setLeadId(long leadId) {
		this.leadId = leadId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	@PrePersist
	protected void prePersist() {
		this.createdOn = System.currentTimeMillis();
		this.editedOn = System.currentTimeMillis();
	}

	public EventDetails(long eventId, long crManagerId, long salesPersonId, long leadId, long clientId, long flatId,
			String propertyName, String eventName, double percentage, double basePriceAmount, double gstAmount,
			String statusReport, String architectsLetter, String invoice, long invoiceDate, long dueDate,
			long paymentDate, String paidByName, String receipt, Status eventDetailsStatus, long createdOn,
			long editedOn) {
		super();
		this.eventId = eventId;
		this.crManagerId = crManagerId;
		this.salesPersonId = salesPersonId;
		this.leadId = leadId;
		this.clientId = clientId;
		this.flatId = flatId;
		this.propertyName = propertyName;
		this.eventName = eventName;
		this.percentage = percentage;
		this.basePriceAmount = basePriceAmount;
		this.gstAmount = gstAmount;
		this.statusReport = statusReport;
		this.architectsLetter = architectsLetter;
		this.invoice = invoice;
		this.invoiceDate = invoiceDate;
		this.dueDate = dueDate;
		this.paymentDate = paymentDate;
		this.paidByName = paidByName;
		this.receipt = receipt;
		this.eventDetailsStatus = eventDetailsStatus;
		this.createdOn = createdOn;
		this.editedOn = editedOn;
	}

	public EventDetails(long crManagerId, long salesPersonId, long leadId, long clientId, long flatId,
			String propertyName, String eventName, double percentage, double basePriceAmount, double gstAmount,
			String statusReport, String architectsLetter, String invoice, long invoiceDate, long dueDate,
			long paymentDate, String paidByName, String receipt, Status eventDetailsStatus) {
		super();
		this.crManagerId = crManagerId;
		this.salesPersonId = salesPersonId;
		this.leadId = leadId;
		this.clientId = clientId;
		this.flatId = flatId;
		this.propertyName = propertyName;
		this.eventName = eventName;
		this.percentage = percentage;
		this.basePriceAmount = basePriceAmount;
		this.gstAmount = gstAmount;
		this.statusReport = statusReport;
		this.architectsLetter = architectsLetter;
		this.invoice = invoice;
		this.invoiceDate = invoiceDate;
		this.dueDate = dueDate;
		this.paymentDate = paymentDate;
		this.paidByName = paidByName;
		this.receipt = receipt;
		this.eventDetailsStatus = eventDetailsStatus;
	}

	public EventDetails() {
		super();
	}

	@Override
	public String toString() {
		return "EventDetails [eventId=" + eventId + ", crManagerId=" + crManagerId + ", salesPersonId=" + salesPersonId
				+ ", leadId=" + leadId + ", clientId=" + clientId + ", flatId=" + flatId + ", propertyName="
				+ propertyName + ", eventName=" + eventName + ", percentage=" + percentage + ", basePriceAmount="
				+ basePriceAmount + ", gstAmount=" + gstAmount + ", statusReport=" + statusReport
				+ ", architectsLetter=" + architectsLetter + ", invoice=" + invoice + ", invoiceDate=" + invoiceDate
				+ ", dueDate=" + dueDate + ", paymentDate=" + paymentDate + ", paidByName=" + paidByName + ", receipt="
				+ receipt + ", eventDetailsStatus=" + eventDetailsStatus + ", createdOn=" + createdOn + ", editedOn="
				+ editedOn + "]";
	}
}
