/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.acesso;

import br.com.grupopibb.portalobra.acesso.dao.MonitorFacade;
import java.util.Date;
import java.util.TimerTask;

/**
 *
 * @author administrator
 */
public class MonitorGeralTask extends TimerTask {

    private MonitorFacade monitorFacade;

    public MonitorGeralTask() {
    }

    public MonitorGeralTask(MonitorFacade monitorFacade) {
        this.monitorFacade = monitorFacade;
    }

    @Override
    public void run() {
        monitorFacade.atualizaMonitor();
    }
}
