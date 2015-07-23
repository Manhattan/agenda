/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.pedido;

import br.com.grupopibb.portalobra.business.pedido.QualidadeBusiness;
import br.com.grupopibb.portalobra.model.ar.DocumentoEntradaAvaliacao;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author administrator
 */
@ManagedBean
@RequestScoped
public class QualidadeController implements Serializable {

    @EJB
    private QualidadeBusiness qualidadeBusiness;

    public DocumentoEntradaAvaliacao getPontualidade(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        try {
            return qualidadeBusiness.getPontualidade(itensAvaliacao);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public DocumentoEntradaAvaliacao getEspecPedido(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        try {
            return qualidadeBusiness.getEspecPedido(itensAvaliacao);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public DocumentoEntradaAvaliacao getQualidadeIntegridade(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        try {
            return qualidadeBusiness.getQualidadeIntegridade(itensAvaliacao);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public DocumentoEntradaAvaliacao getDadosNf(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        try {
            return qualidadeBusiness.getDadosNf(itensAvaliacao);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public DocumentoEntradaAvaliacao getQualidadeIntrinseca(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        try {
            return qualidadeBusiness.getQualidadeIntrinseca(itensAvaliacao);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public DocumentoEntradaAvaliacao getRequisitosComerciais(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        try {
            return qualidadeBusiness.getRequisitosComerciais(itensAvaliacao);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public DocumentoEntradaAvaliacao getRequisitosSgq(List<DocumentoEntradaAvaliacao> itensAvaliacao) {
        try {
            return qualidadeBusiness.getRequisitosSgq(itensAvaliacao);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
