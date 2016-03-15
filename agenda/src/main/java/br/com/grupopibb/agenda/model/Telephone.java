/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.model;

import br.com.grupopibb.agenda.model.common.EntityInterface;
import br.com.grupopibb.agenda.types.EnumTypeTelephone;
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
@Table(name = "Telephone")
public class Telephone implements EntityInterface<Telephone> {

    public Telephone() {
    }

    public Telephone(Contact contact) {
        this.contact = contact;
    }

    public Telephone(Contact contact, Integer code) {
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
    @Column(name = "Tel_Cod")
    private Integer code;
    /*
     */
    @Column(name = "Tel_Number")
    private String number;
    /*
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "Tel_Type")
    private EnumTypeTelephone typeTelephone;
    /*
     */
    @Column(name = "Tel_Format")
    private String format;

    @Override
    public Serializable getId() {
        return this.getCode();
    }

    @Override
    public String getLabel() {
        return this.getNumber();
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
    public int compareTo(Telephone o) {
        return (this.getContact().getId().toString() + "_" +this.getCode().toString()).compareTo(o.getContact().getId().toString() + "_" +o.getCode().toString());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.contact != null ? this.contact.hashCode() : 0);
        hash = 61 * hash + (this.code != null ? this.code.hashCode() : 0);
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
        final Telephone other = (Telephone) obj;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public EnumTypeTelephone getTypeTelephone() {
        return typeTelephone;
    }

    public void setTypeTelephone(EnumTypeTelephone typeTelephone) {
        this.typeTelephone = typeTelephone;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
