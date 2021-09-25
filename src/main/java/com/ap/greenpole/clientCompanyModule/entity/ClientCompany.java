package com.ap.greenpole.clientCompanyModule.entity;


import com.ap.greenpole.clientCompanyModule.enums.GenericStatusEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Nelu Akejelu on 18/08/2020.
 */


@Entity(name = "client_company")
public class ClientCompany implements Serializable {

//    private static final long serialVersionUID = 5146914376303226067L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_company_id")
    protected long id;

    @Column(name = "register_name")
    private String registerName;

    @Column(name = "register_code")
    private String registerCode;

    private String symbol;

    @Column(name = "chairman_name")
    private String chairmanName;

    @Column(name = "ceo_name")
    private String ceoName;

    @Column(name = "registration_code")
    private String registrationCode;

    private String address;

    @Column(name = "email_address" ,length = 150)
    private String emailAddress;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clientCompany")
    @JsonBackReference
    private List<Director> directors = new ArrayList<>();
//
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clientCompany",orphanRemoval = true)
//    private List<Meeting> meetings;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String depository;

    private String exchange; //NSE, NASD, FMDQ

    @Column(name = "date_of_incorporation")
    private Date dateOfIncorporation;

    private String secretary;

    @Column(name = "rc_number")
    private String rcNumber;

    private String country;

    private String state;

    private String lga;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "alternate_phone_number")
    private String alternatePhoneNumber;

    @Column(name = "nse_sector")
    private String nseSector;

    @Column(name = "status", length = 50)
    @NotNull
    @Enumerated(EnumType.STRING)
    private GenericStatusEnum status;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    @Column(name = "authorized_share_capital")
    private BigInteger authorizedShareCapital;

    @Column(name = "paid_up_share_capital")
    private BigInteger paidUpShareCapital;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRegisterName() {
        return registerName;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public String getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getChairmanName() {
        return chairmanName;
    }

    public void setChairmanName(String chairmanName) {
        this.chairmanName = chairmanName;
    }

    public String getCeoName() {
        return ceoName;
    }

    public void setCeoName(String ceoName) {
        this.ceoName = ceoName;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<Director> getDirectors() {
        return directors;
    }

    public void setDirectors(List<Director> directors) {
        this.directors = directors;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDepository() {
        return depository;
    }

    public void setDepository(String depository) {
        this.depository = depository;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Date getDateOfIncorporation() {
        return dateOfIncorporation;
    }

    public void setDateOfIncorporation(Date dateOfIncorporation) {
        this.dateOfIncorporation = dateOfIncorporation;
    }

    public String getSecretary() {
        return secretary;
    }

    public void setSecretary(String secretary) {
        this.secretary = secretary;
    }

    public String getRcNumber() {
        return rcNumber;
    }

    public void setRcNumber(String rcNumber) {
        this.rcNumber = rcNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAlternatePhoneNumber() {
        return alternatePhoneNumber;
    }

    public void setAlternatePhoneNumber(String alternatePhoneNumber) {
        this.alternatePhoneNumber = alternatePhoneNumber;
    }

    public String getNseSector() {
        return nseSector;
    }

    public void setNseSector(String nseSector) {
        this.nseSector = nseSector;
    }

    public GenericStatusEnum getStatus() {
        return status;
    }

    public void setStatus(GenericStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigInteger getAuthorizedShareCapital() {
        return authorizedShareCapital;
    }

    public void setAuthorizedShareCapital(BigInteger authorizedShareCapital) {
        this.authorizedShareCapital = authorizedShareCapital;
    }

    public BigInteger getPaidUpShareCapital() {
        return paidUpShareCapital;
    }

    public void setPaidUpShareCapital(BigInteger paidUpShareCapital) {
        this.paidUpShareCapital = paidUpShareCapital;
    }
}
