package com.ap.greenpole.clientCompanyModule.entity;

import com.ap.greenpole.clientCompanyModule.enums.GenericStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Nelu Akejelu on 18/08/2020.
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Shareholder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long shareholder_id;

    private String email, phone, gender, occupation, address, city, country, relationship, rin, nuban, bvn;

    @JsonProperty("first_name")
    @Column(name = "first_name")
    String firstName;

    @JsonProperty("middle_name")
    @Column(name = "middle_name")
    String middleName;

    @JsonProperty("last_name")
    @Column(name = "last_name")
    String lastName;

    @JsonProperty("marital_status")
    @Column(name = "marital_status")
    String maritalStatus;

    @JsonProperty("state_of_origin")
    @Column(name = "state_of_origin")
    String stateOfOrigin;

    @JsonProperty("marriage_certificate_number")
    @Column(name = "marriage_certificate_number")
    String marriageCertificateNumber;

    @JsonProperty("postal_code")
    @Column(name = "postal_code")
    String postalCode;

    @JsonProperty("lga_of_origin")
    @Column(name = "lga_of_origin")
    String lgaOfOrigin;

    @JsonProperty("kin_email")
    @Column(name = "kin_email")
    String kinEmail;

    @JsonProperty("kin_name")
    @Column(name = "kin_name")
    String kinName;

    @JsonProperty("kin_phone")
    @Column(name = "kin_phone")
    String kinPhone;

    @JsonProperty("kin_address")
    @Column(name = "kin_address")
    String kinAddress;

    @JsonProperty("kin_country")
    @Column(name = "kin_country")
    String kinCountry;

    @JsonProperty("kin_state")
    @Column(name = "kin_state")
    String kinState;

    @JsonProperty("kin_lga")
    @Column(name = "kin_lga")
    String kinLga;

    @JsonProperty("shareholder_type")
    @Column(name = "shareholder_type")
    String shareholderType;

    @JsonProperty("client_company")
    @Column(name = "client_company")
    long clientCompany;

    @JsonProperty("stock_broker")
    @Column(name = "stock_broker")
    long stockBroker;

    @JsonProperty("bank_name")
    @Column(name = "bank_name")
    String bankName;

    @JsonProperty("bank_account")
    @Column(name = "bank_account")
    String bankAccount;

    @JsonProperty("clearing_housing_number")
    @Column(name = "clearing_housing_number")
    String clearingHousingNumber;

    @JsonProperty("esop_status")
    @Column(name = "esop_status")
    String esopStatus;

    @JsonProperty("share_unit")
    @Column(name = "share_unit")
    BigInteger shareUnit;

    @DateTimeFormat(pattern="dd-MM-yyyy hh:mm:ss")
    @JsonProperty("created_on")
    @Column(name = "created_on")
    private Date createdOn;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date dob;

    @JsonProperty("tax_exemption")
    @Column(name = "tax_exemption")
    boolean taxExemption;

    @JsonProperty("registrar_mandated")
    @Column(name = "registrar_mandated")
    boolean registrarMandated;

    @Column(name = "status", length = 50)
    @NotNull
    @Enumerated(EnumType.STRING)
    private GenericStatusEnum status;

}
