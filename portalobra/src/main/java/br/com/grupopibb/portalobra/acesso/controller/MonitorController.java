/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.acesso.controller;

import br.com.grupopibb.portalobra.acesso.Monitor;
import br.com.grupopibb.portalobra.acesso.business.MonitorBusiness;
import br.com.grupopibb.portalobra.acesso.dao.MonitorFacade;
import br.com.grupopibb.portalobra.acesso.status.StatusLogin;
import br.com.grupopibb.portalobra.exceptions.EntityException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author administrator
 */
@ManagedBean
@RequestScoped
public class MonitorController {

    @EJB
    private MonitorFacade monitorFacade;
    @EJB
    private MonitorBusiness monitorBusiness;
    private Monitor current;
    /**
     * Componente que contém o usuário logado.
     */
    @ManagedProperty(value = "#{loginController}")
    private LoginController loginController;

    public void setLoginController(LoginController loginContronller) {
        this.loginController = loginContronller;
    }

    public void registraAcesso() {
        if (loginController != null && loginController.getFuncionario() != null && loginController.getStatusLogin() == StatusLogin.ATIVO) {
            this.current = getNewMonitor();
            create();
        }
    }

    public Monitor getNewMonitor() {
        return monitorBusiness.getNewMonitor(loginController.getFuncionario());
    }

    private void create() {
        try {
            monitorFacade.create(current);
        } catch (EntityException ex) {
            Logger.getLogger(MonitorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void update() {
        try {
            monitorFacade.update(current);
        } catch (EntityException ex) {
            Logger.getLogger(MonitorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Monitor> getConectedUsers(){
        return monitorFacade.findActiveUsers();
    }
    
    public Long getCountConectedUsers(){
        return monitorFacade.countActiveUsers();
    }
    
    public void atualizaMonitor(){
        monitorFacade.atualizaMonitor();
    }
    
    public void recreateTable(){}
}
