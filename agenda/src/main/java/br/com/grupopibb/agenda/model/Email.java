/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.model;

import br.com.grupopibb.agenda.model.common.EntityInterface;
import br.com.grupopibb.agenda.types.EnumTypeEmail;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author tone.lima
 */
@Entity
@Table(name = "Email")
public class Email implements EntityInterface<Email> {

    public Email() {
    }

    public Email(Contact contact) {
        this.contact = contact;
    }

    public Email(Contact contact, Integer code) {
        this.contact = contact;
        this.code = code;
    }

    @Id
    @NotNull
    @ManyToOne(targetEntity = Contact.class)
    @JoinColumn(name = "Con_Cod")
    private Contact contact;
    /*
     */
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Email_Cod")
    private Integer code;
    /*
     */
    @Column(name = "Email_Name")
    private String name;
    /*
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "Email_Type")
    private EnumTypeEmail typeEmail;

    @Override
    public Serializable getId() {
        return this.getCode();
    }

    @Override
    public String getLabel() {
        return this.getName();
    }

    @Override
    public boolean verifyId() {
        return false;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public int compareTo(Email o) {
        return (this.getContact().getId().toString() + "_" +this.getCode().toString()).compareTo(o.getContact().getId().toString() + "_" +o.getCode().toString());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.contact != null ? this.contact.hashCode() : 0);
        hash = 53 * hash + (this.code != null ? this.code.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Email other = (Email) obj;
        if (this.contact != other.contact && (this.contact == null || !this.contact.equals(other.contact))) {
            return false;
        }
        if (this.code != other.code && (this.code == null || !this.code.equals(other.code))) {
            return false;
        }
        return true;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumTypeEmail getTypeEmail() {
        return typeEmail;
    }

    public void setTypeEmail(EnumTypeEmail typeEmail) {
        this.typeEmail = typeEmail;
    }
}
