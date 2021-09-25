package com.ap.greenpole.usermodule.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 20-Aug-20 01:24 AM
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotBlank(message = "The value cannot be empty")
    String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
