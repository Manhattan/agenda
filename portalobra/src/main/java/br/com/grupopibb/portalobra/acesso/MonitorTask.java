/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.acesso;

import br.com.grupopibb.portalobra.acesso.dao.MonitorFacade;
import br.com.grupopibb.portalobra.exceptions.EntityException;
import br.com.grupopibb.portalobra.model.funcionario.Funcionario;
import java.io.Serializable;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author administrator
 */
public class MonitorTask extends TimerTask implements Serializable {

    private MonitorFacade monitorFacade;
    private Monitor monitor;
    private Funcionario funcionario;

    public MonitorTask() {
    }

    public MonitorTask(Monitor monitor, MonitorFacade monitorFacade, Funcionario funcionario) {
        this.monitor = monitor;
        this.monitorFacade = monitorFacade;
        this.funcionario = funcionario;
    }

    @Override
    public void run() {
        try {
            if (funcionario == null){
                cancel();
            }
            if (monitor != null) {
                monitor.setDataAtual(new Date());
                if (monitor.getDataSaida() != null){
                    monitor.setDataSaida(null);
                }
                monitorFacade.update(monitor);
            }
        } catch (EntityException ex) {
            Logger.getLogger(MonitorTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
