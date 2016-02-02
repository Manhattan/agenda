/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.followup;

import br.com.grupopibb.portalobra.exceptions.BusinessException;
import br.com.grupopibb.portalobra.utils.MessageUtils;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author tone.lima
 */
@Stateless
@LocalBean
public class FollowupMsg {

    public void infoMsg(String msg, String obj) {
        MessageUtils.messageFactoringFull(msg,
                new Object[]{obj},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void warnMsg(String msg, String obj) {
        MessageUtils.messageFactoringFull(msg,
                new Object[]{obj},
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void warnMsg(String msg, Exception ex, String obj) {
        MessageUtils.messageFactoringFull(msg,
                new Object[]{obj},
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void errorMsg(String msg, Exception ex, String obj) {
        MessageUtils.messageFactoringFull(msg,
                ex.getMessage(),
                null, FacesMessage.SEVERITY_ERROR,
                FacesContext.getCurrentInstance());
    }

    public void businessException(BusinessException ex) {
        MessageUtils.messageFactoringFull(ex.getMessage(),
                ex.getVariacoes(), FacesMessage.SEVERITY_ERROR,
                FacesContext.getCurrentInstance());
    }

}
