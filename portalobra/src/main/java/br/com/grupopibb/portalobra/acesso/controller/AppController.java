/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.acesso.controller;

import br.com.grupopibb.portalobra.acesso.MonitorScheduler;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author administrator
 */
@ApplicationScoped
@ManagedBean(eager = true)
public class AppController {
    
    @EJB
    private MonitorScheduler monitorScheduler;
    
    
    @PostConstruct
    public void init(){
        monitorScheduler.initMonitorGeral();
    }
    
}
