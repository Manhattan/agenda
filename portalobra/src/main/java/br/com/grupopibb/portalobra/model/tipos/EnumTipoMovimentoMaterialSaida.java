/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.tipos;

/**
 *
 * @author administrator
 */
public enum EnumTipoMovimentoMaterialSaida {

    S("Saida para Consumo"),
    T("Transferência"),
    D("Devolução do Consumo"),
    V("Venda"),
    I("Outros");
    
    private String label;

    private EnumTipoMovimentoMaterialSaida(String label) {
        this.label = label;
    }

    private String getLabel() {
        return label;
    }
}
