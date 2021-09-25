package com.ap.greenpole.clientCompanyModule.entity;


import java.util.Date;
import java.util.List;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;

/**
 * Created By: Oyindamola Akindele
 * Date: 8/11/2020 2:32 AM
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class StockBroker {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String stockBrokerName;
    @Column(name = "cscsAccountNumber")
    private String cscsAccountNumber;
    private String address;
    private String emails;
    private String phoneNumbers;
    private boolean active;
    private String validationState;
    private String suspensionState;
    private String signature;
    private Date dateCreated;
    private Date dateModified;
    private String signatureDownloadLink;

    @Transient
    private List<String> emailAddresses;
    @Transient
    private List<String> phones;
    @Transient
    private List<Shareholder> shareholders;
    @Transient
    private List<Bondholder> bondholders;
    @Transient
    private String signatureFileDownload;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStockBrokerName() {
        return stockBrokerName;
    }
    public void setStockBrokerName(String stockBrokerName) {
        this.stockBrokerName = stockBrokerName;
    }

    public String getCscsAccountNumber() {
        return cscsAccountNumber;
    }
    public void setCscsAccountNumber(String cscsAccountNumber) {
        this.cscsAccountNumber = cscsAccountNumber;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getEmails() {
        return emails;
    }
    public void setEmails(String emails) {
        this.emails = emails;
    }
    public String getPhoneNumbers() {
        return phoneNumbers;
    }
    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
    public List<String> getEmailAddresses() {
        return emailAddresses;
    }
    public void setEmailAddresses(List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }
    public List<String> getPhones() {
        return phones;
    }
    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public boolean getActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public String getValidationState() {
        return validationState;
    }
    public void setValidationState(String validationState) {
        this.validationState = validationState;
    }
    public String getSuspensionState() {
        return suspensionState;
    }
    public void setSuspensionState(String suspensionState) {
        this.suspensionState = suspensionState;
    }
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public List<Shareholder> getShareholders() {
        return shareholders;
    }

    public void setShareholders(List<Shareholder> shareholders) {
        this.shareholders = shareholders;
    }

    public List<Bondholder> getBondholders() {
        return bondholders;
    }

    public void setBondholders(List<Bondholder> bondholders) {
        this.bondholders = bondholders;
    }

    public String getSignatureDownloadLink() {
        return signatureDownloadLink;
    }

    public void setSignatureDownloadLink(String signatureDownloadLink) {
        this.signatureDownloadLink = signatureDownloadLink;
    }

    @Override
    public String toString() {
        try {
            return new Gson().toJson(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

