package com.crm.transactions;

import com.crm.user.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Transactions {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String propertyName;
	private String transactionId;
	private double amount;
	private Status status;
	private String paymentMode;
	private String action;
	private long userId;
	private long trxDate;
	private long createdOn;

	public Transactions() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getTrxDate() {
		return trxDate;
	}

	public void setTrxDate(long trxDate) {
		this.trxDate = trxDate;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public Transactions(long id, String propertyName, String transactionId, double amount, Status status,
			String paymentMode, String action, long userId, long trxDate, long createdOn) {
		super();
		this.id = id;
		this.propertyName = propertyName;
		this.transactionId = transactionId;
		this.amount = amount;
		this.status = status;
		this.paymentMode = paymentMode;
		this.action = action;
		this.userId = userId;
		this.trxDate = trxDate;
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "Transactions [id=" + id + ", propertyName=" + propertyName + ", transactionId=" + transactionId
				+ ", amount=" + amount + ", status=" + status + ", paymentMode=" + paymentMode + ", action=" + action
				+ ", userId=" + userId + ", trxDate=" + trxDate + ", createdOn=" + createdOn + "]";
	}

}
