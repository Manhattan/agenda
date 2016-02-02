/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.followup;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author tone.lima
 */
@Stateless
@LocalBean
public class FollowupDlg {
    //------------------------------------------
    // NOME DOS DIALOGS DO FOLLOWUP
    //------------------------------------------
    /*Registro Solicitação Dialog*/
    public String getDialogRS() {
        return "dlgRS";
    }

    /*Insumo Dialog*/
    public String getDialogInsumo() {
        return "dlgInsumo";
    }

    /*Conclusão Solicitação Dialog*/
    public String getDialogCS() {
        return "dlgCS";
    }

    /*Autorização Solicitação Dialog*/
    public String getDialogAS() {
        return "dlgAS";
    }

    /*Coleta Dialog*/
    public String getDialogAC() {
        return "dlgAC";
    }

    /*Registro Pedido Dialog*/
    public String getDialogRP() {
        return "dlgRP";
    }

    /*Autorização Pedido Dialog*/
    public String getDialogAP() {
        return "dlgAP";
    }

    /*Emissão Nota Fiscal Dialog*/
    public String getDialogENF() {
        return "dlgENF";
    }

    /*Entrega Material Obra Dialog*/
    public String getDialogEMO() {
        return "dlgEMO";
    }

    /*Registro AR Dialog*/
    public String getDialogRA() {
        return "dlgRA";
    }

    /*Autorização AR Dialog*/
    public String getDialogAA() {
        return "dlgAA";
    }

    /*Pagamento Dialog*/
    public String getDialogPG() {
        return "dlgPG";
    }

    /*OK Dialog*/
    public String getDialogOK() {
        return "dlgOK";
    }

    /*Kardex do Material Dialog*/
    public String getDialogKardex() {
        return "dlgKardex";
    }

    /*Seleção de Centro de Custo Dialog*/
    public String getDialogCentroCusto() {
        return "dlgCentroCusto";
    }

    public String getDialogAjuda() {
        return "dlgAjuda";
    }
}
