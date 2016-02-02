/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra;

import br.com.grupopibb.portalobra.model.solicitacaocompra.SolicitacaoCompraItem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tone.lima
 */
public class Teste {

    public static void main(String[] args) {
        
        int f = 5;
        int aux = 1;
        int res = f;
        
        for (int i = 1; i < f; i++){
            res = res * aux;
            aux++;
        }
        
        System.out.println(res);
    }
}
