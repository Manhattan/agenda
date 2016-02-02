/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.tipos;

/**
 *
 * @author administrator
 */
public enum EnumTipoMovimentoMaterialEntrada {

    C("Compra"),
    T("Transferência"),
    O("Outros"),
    I("Inventario");
    private String label;

    private EnumTipoMovimentoMaterialEntrada(String label) {
        this.label = label;
    }

    private String getLabel() {
        return label;
    }
}
