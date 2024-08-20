package odb.gradle;

import javax.jdo.annotations.Join;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Transaction implements Serializable {
    @Id
    private long id;

    private Date date;
    private double amount;
    private String type;

    @ManyToOne()
    @JoinColumn(name = "sender_id", nullable = false)
    private BankAccount sender;

    @ManyToOne()
    @JoinColumn(name = "receiver_id", nullable = false)
    private BankAccount receiver;

    public Transaction(long id, Date date, double amount, String type, BankAccount sender, BankAccount receiver) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Transaction() {
    }

    // Getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BankAccount getSender() {
        return sender;
    }

    public void setSender(BankAccount sender) {
        this.sender = sender;
    }

    public BankAccount getReceiver() {
        return receiver;
    }

    public void setReceiver(BankAccount receiver) {
        this.receiver = receiver;
    }
}

