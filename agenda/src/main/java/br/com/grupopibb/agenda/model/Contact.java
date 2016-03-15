/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.model;

import br.com.grupopibb.agenda.model.common.EntityInterface;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author tone.lima
 */
@Entity
@Table(name = "Contact")
@NamedQueries({
    @NamedQuery(name = "Contact.findParam",
            query = " SELECT DISTINCT c FROM Contact c left join c.telephones t "
            + " WHERE ( :name2 = 'todos' OR (c.name LIKE :name OR t.number LIKE :name)) "
            + " AND ( :caracter2 = 'todos' OR c.name LIKE :caracter )"),
    @NamedQuery(name = "Contact.countParam",
            query = " SELECT COUNT(DISTINCT c) FROM Contact c left join c.telephones t "
            + " WHERE ( :name2 = 'todos' OR (c.name LIKE :name OR t.number LIKE :name)) "
            + " AND ( :caracter2 = 'todos' OR c.name LIKE :caracter )"),
    @NamedQuery(name = "Contact.findMaxId",
            query = " SELECT MAX(c.code) FROM Contact c ")
})
public class Contact implements EntityInterface<Contact> {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Con_Cod")
    private Integer code;
    /*
     */
    @Column(name = "Con_Name")
    private String name;
    /*
     */
    @Column(name = "Con_Obs")
    private String observation;
    /*
     */
    @Column(name = "Con_Address")
    private String address;
    /*
     */
    @Column(name = "Con_Number")
    private String number;
    /*
     */
    @Column(name = "Con_Complement")
    private String complement;
    /*
     */
    @Column(name = "Con_Neigh")
    private String neighborhood;
    /*
     */
    @Column(name = "Con_City")
    private String city;
    /*
     */
    @Column(name = "Con_Country")
    private String country;
    /*
     */
    @OneToMany(targetEntity = Telephone.class, mappedBy = "contact", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Telephone> telephones;
    /*
     */
    @OneToMany(targetEntity = Email.class, mappedBy = "contact", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Email> emails;

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
    public int compareTo(Contact o) {
        return this.getCode().compareTo(o.getCode());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.code != null ? this.code.hashCode() : 0);
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
        final Contact other = (Contact) obj;
        if (this.code != other.code && (this.code == null || !this.code.equals(other.code))) {
            return false;
        }
        return true;
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

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Telephone> getTelephones() {
        return telephones;
    }

    public void setTelephones(List<Telephone> telephones) {
        this.telephones = telephones;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }
}
