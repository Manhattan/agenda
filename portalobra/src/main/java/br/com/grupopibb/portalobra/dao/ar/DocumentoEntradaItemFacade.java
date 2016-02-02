/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.dao.ar;

import br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans;
import static br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans.getMapParams;
import br.com.grupopibb.portalobra.model.ar.DocumentoEntradaItem;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.Insumo;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.model.solicitacaocompra.SolicitacaoCompraItem;
import br.com.grupopibb.portalobra.utils.UtilBeans;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author administrator
 */
@Stateless
@LocalBean
public class DocumentoEntradaItemFacade extends AbstractEntityBeans<DocumentoEntradaItem, Long> {

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentoEntradaItemFacade() {
        super(DocumentoEntradaItem.class, DocumentoEntradaItemFacade.class);
    }

    public List<DocumentoEntradaItem> findByInsumo(final CentroCusto centro, final InsumoSub insumoSub) {
        Map<String, Object> params = getMapParams();
        paramsInsumo(params, centro, insumoSub);
        try {
            return listPesqParam("DocumentoEntradaItem.findByInsumo", params);
        } catch (NoResultException | NullPointerException e) {
            return null;
        }
    }

    public List<DocumentoEntradaItem> findBySolic(final SolicitacaoCompraItem solicitacaoCompraItem) {
        Map<String, Object> params = getMapParams();
        paramsSolic(params, solicitacaoCompraItem);
        return listPesqParam("DocumentoEntrataItem.findBySolic", params);
    }

    private void paramsSolic(Map<String, Object> params, final SolicitacaoCompraItem solicitacaoCompraItem) {
        params.put("solicItem", solicitacaoCompraItem);
    }

    private void paramsInsumo(Map<String, Object> params, final CentroCusto centro, final InsumoSub insumoSub) {
        params.put("centro", centro);
        params.put("insumo", insumoSub == null ? null : insumoSub.getInsumo());
        params.put("centro2", centro == null ? "todos" : "filtro");
    }
}
