package odb.gradle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class BankAccount implements Serializable {
    
    @Id
    private long accountNumber;

    private String holderName;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
    private List<Transaction> sentTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Transaction> receivedTransactions = new ArrayList<>();

    // Getters and setters

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public List<Transaction> getSentTransactions() {
        return sentTransactions;
    }

    public void addSentTransaction(Transaction transaction) {
        sentTransactions.add(transaction);
    }

    public void addReceivedTransaction(Transaction transaction) {
        receivedTransactions.add(transaction);
    }

    public void setReceivedTransactions(List<Transaction> receivedTransactions) {
        this.receivedTransactions = receivedTransactions;
    }

    public String toString() {
        return "Account Number: " + accountNumber + " Holder Name: " + holderName;
    }
}
