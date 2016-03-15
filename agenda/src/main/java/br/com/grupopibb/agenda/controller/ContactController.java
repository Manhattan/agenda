/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.controller;

import br.com.grupopibb.agenda.controller.common.EntityController;
import br.com.grupopibb.agenda.controller.common.EntityPagination;
import br.com.grupopibb.agenda.dao.ContactDAO;
import br.com.grupopibb.agenda.dao.exceptions.EntityException;
import br.com.grupopibb.agenda.model.Contact;
import br.com.grupopibb.agenda.model.Email;
import br.com.grupopibb.agenda.model.Telephone;
import br.com.grupopibb.agenda.utils.JsfUtil;
import br.com.grupopibb.agenda.utils.MessageUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 *
 * @author tone.lima
 */
@ManagedBean
@ViewScoped
public class ContactController extends EntityController<Contact> implements Serializable {

    @EJB
    private ContactDAO contactDAO;
    private Contact current;
    private String name;
    private String caracter;
    private Integer regsPerPage = 50;
    private boolean changeMaskBrasil = false;
    private boolean changeMaskUsa = false;
    private boolean changeMaskNormal = true;
    private String maskTel;

    private ContactDAO getDAO() {
        return contactDAO;
    }

    @Override
    protected void setEntity(Contact t) {
        this.current = t;
        initPhoneAndEmail(this.current);
    }

    public void cleanForm() {
        name = null;
        caracter = null;
    }

    @Override
    public String clean() {
        recreateTable();
        return super.clean();
    }

    @Override
    public void search() {
        super.search();
        getDAO().clearCache();
    }

    public String getMaskTel() {

        if (this.changeMaskNormal) {
            this.setMaskTel("");
        } else if (this.changeMaskUsa) {
            this.setMaskTel("99999-9999-999-9999");
        } else {
            this.setMaskTel("(99) 99999-9999");
        }
        return maskTel;
    }

    public void setMaskTel(String maskTel) {
        this.maskTel = maskTel;
    }

    public boolean isChangeMaskUsa() {
        return changeMaskUsa;
    }

    public void setChangeMaskUsa(boolean changeMaskUsa) {
        this.changeMaskUsa = changeMaskUsa;
    }

    public boolean isChangeMaskNormal() {
        return changeMaskNormal;
    }

    public void setChangeMaskNormal(boolean changeMaskNormal) {
        this.changeMaskNormal = changeMaskNormal;
    }

    public boolean isChangeMaskBrasil() {
        return changeMaskBrasil;
    }

    public void setChangeMask(boolean changeMask) {
        this.changeMaskBrasil = changeMask;
    }

    public void changeMaskTelefoneNormal() {
        if (!this.changeMaskNormal) {
            this.changeMaskNormal = true;
            this.changeMaskBrasil = false;
            this.changeMaskUsa = false;
        } else {
            this.changeMaskNormal = false;
        }
    }

    public void changeMaskTelefoneBrasil() {
        if (!this.changeMaskBrasil) {
            this.changeMaskBrasil = true;
            this.changeMaskUsa = false;
            this.changeMaskNormal = false;
        } else {
            this.changeMaskBrasil = false;
        }
    }

    public void changeMaskTelefoneUsa() {
        if (!this.changeMaskUsa) {
            this.changeMaskUsa = true;
            this.changeMaskBrasil = false;
            this.changeMaskNormal = false;
        } else {
            this.changeMaskUsa = false;
        }
    }

    public String prepareEdit(Contact t) {
        setEntity(t);
        newEntity = false;
        return JsfUtil.MANTEM;
    }

    public void searchByChar(String c) {
        this.caracter = c;
        search();
    }

    private void initPhoneAndEmail(Contact t) {
        try {
            if (t.getTelephones() == null) {
                t.setTelephones(new ArrayList<Telephone>());
            }
            addTel(t);

            if (t.getEmails() == null) {
                t.setEmails(new ArrayList<Email>());
            }
            addMail(t);
        } catch (NullPointerException e) {
        }
    }

    private void cleanPhoneAndEmailIfNull(Contact t) {
        cleanPhoneIfNull(t);
        cleanMailIfNull(t);
    }

    private void cleanPhoneIfNull(Contact t) {
        List<Telephone> lTel = new ArrayList<>(t.getTelephones());
        for (Telephone tel : lTel) {
            if (tel.getNumber() == null) {
                t.getTelephones().remove(tel);
            }
        }
        lTel = null;
    }

    private void cleanMailIfNull(Contact t) {
        List<Email> lMail = new ArrayList<>(t.getEmails());
        for (Email mail : lMail) {
            if (mail.getName() == null) {
                t.getEmails().remove(mail);
            }
        }
        lMail = null;
    }

    private int incrementTel(Contact t) {
        int num = 1;
        for (Telephone tel : t.getTelephones()) {
            if (tel.getCode() >= num) {
                num = tel.getCode() + 1;
            }
        }
        return num;
    }

    private int incrementMail(Contact t) {
        int num = 1;
        for (Email mail : t.getEmails()) {
            if (mail.getCode() >= num) {
                num = mail.getCode() + 1;
            }
        }
        return num;
    }

    public void addTel(Contact t) {
        cleanPhoneIfNull(t);
        t.getTelephones().add(new Telephone(t, incrementTel(t)));
    }

    public void addMail(Contact t) {
        cleanMailIfNull(t);
        t.getEmails().add(new Email(t, incrementMail(t)));
    }

    /**
     * Remove o telefone do contato.
     *
     * @param t
     * @param item
     */
    private void removeTel(Contact t, Telephone item) {
        t.getTelephones().remove(item);
    }

    /**
     * Remove o e-mail do contato.
     *
     * @param t
     * @param item
     */
    private void removeMail(Contact t, Email item) {
        t.getEmails().remove(item);
    }

    public void remove(Contact t, Object item) {
        if (item instanceof Telephone) {
            removeTel(t, (Telephone) item);
        } else if (item instanceof Email) {
            removeMail(t, (Email) item);
        }
    }

    @Override
    protected Contact getNewEntity() {
        return new Contact();
    }

    @Override
    protected void performDestroy() {
        try {
            getDAO().remove(current);
            msgContactDeleted();
            clean();
        } catch (EntityException ex) {
            Logger.getLogger(ContactController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected String create() {
        try {
            current.setCode(0);
            cleanPhoneAndEmailIfNull(current);
            getDAO().create(current);
            msgContactCreated();
        } catch (EntityException ex) {
            Logger.getLogger(ContactController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clean();
    }

    @Override
    protected String update() {
        try {
            cleanPhoneAndEmailIfNull(current);
            getDAO().update(current);
            msgContactUpdated();
        } catch (EntityException ex) {
            Logger.getLogger(ContactController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clean();
    }

    @Override
    public EntityPagination getPagination() {
        if (pagination == null) {
            pagination = new EntityPagination(regsPerPage) {
                @Override
                public int getItemsCount() {
                    return getDAO().countParam(name, caracter).intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getDAO().findRangeParam(name, caracter, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public Contact getCurrent() {
        return current;
    }

    public void setCurrent(Contact current) {
        this.current = current;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRegsPerPage() {
        return regsPerPage;
    }

    public void setRegsPerPage(Integer regsPerPage) {
        this.regsPerPage = regsPerPage;
    }

    public void msgContactCreated() {
        MessageUtils.messageFactoringFull("ContactCreated",
                new Object[]{current.getName()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgContactUpdated() {
        MessageUtils.messageFactoringFull("ContactUpdated",
                new Object[]{current.getName()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgContactDeleted() {
        MessageUtils.messageFactoringFull("ContactDeleted",
                new Object[]{current.getName()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }
}
