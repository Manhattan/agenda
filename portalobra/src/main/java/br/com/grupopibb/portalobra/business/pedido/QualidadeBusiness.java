/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.business.pedido;

import br.com.grupopibb.portalobra.model.ar.DocumentoEntradaAvaliacao;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author administrator
 */
@Stateless
@LocalBean
public class QualidadeBusiness {

    private DocumentoEntradaAvaliacao findItemAvalByCodigo(List<DocumentoEntradaAvaliacao> itensAvaliacao, Integer codigo) {
        if (itensAvaliacao != null && codigo != null) {
            try {
                for (DocumentoEntradaAvaliacao item : itensAvaliacao) {
                    if (item.getCodigoItem().intValue() == codigo.intValue()) {
                        return item;
                    }
                }
            } catch (NullPointerException e) {
                return null;
            }
        }
        return null;
    }

    public DocumentoEntradaAvaliacao getPontualidade(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        DocumentoEntradaAvaliacao pont = findItemAvalByCodigo(itensAvaliacao, 2511);
        return pont;
    }

    public DocumentoEntradaAvaliacao getEspecPedido(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        return findItemAvalByCodigo(itensAvaliacao, 2745);
    }

    public DocumentoEntradaAvaliacao getQualidadeIntegridade(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        return findItemAvalByCodigo(itensAvaliacao, 2911);
    }

    public DocumentoEntradaAvaliacao getDadosNf(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        return findItemAvalByCodigo(itensAvaliacao, 2922);
    }

    public DocumentoEntradaAvaliacao getQualidadeIntrinseca(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        return findItemAvalByCodigo(itensAvaliacao, 5730);
    }

    public DocumentoEntradaAvaliacao getRequisitosComerciais(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        return findItemAvalByCodigo(itensAvaliacao, 5910);
    }

    public DocumentoEntradaAvaliacao getRequisitosSgq(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        return findItemAvalByCodigo(itensAvaliacao, 5911);
    }

   
}
