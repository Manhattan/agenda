/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.ar;

import br.com.grupopibb.portalobra.controller.common.EntityController;
import br.com.grupopibb.portalobra.controller.common.EntityPagination;
import br.com.grupopibb.portalobra.dao.ar.DocumentoEntradaItemFacade;
import br.com.grupopibb.portalobra.dao.geral.CentroCustoFacade;
import br.com.grupopibb.portalobra.model.ar.DocumentoEntradaItem;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.Insumo;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;

/**
 *
 * @author administrator
 */
@ManagedBean
@ViewScoped
public class DocumentoEntradaItemController extends EntityController<DocumentoEntradaItem> implements Serializable {

    @EJB
    private DocumentoEntradaItemFacade documentoEntradaItemFacade;
    @EJB
    private CentroCustoFacade centroCustoFacade;
    private DocumentoEntradaItem current;
    private CentroCusto centroFiltro;
    private InsumoSub insumoFiltro;

    @Override
    protected void setEntity(DocumentoEntradaItem t) {
        this.current = t;
    }

    @Override
    protected DocumentoEntradaItem getNewEntity() {
        return new DocumentoEntradaItem();
    }

    @Override
    public EntityPagination getPagination() {
        return null;
    }

    @Override
    protected void performDestroy() {
    }

    @Override
    protected String create() {
        return JsfUtil.MANTEM;
    }

    @Override
    protected String update() {
        return JsfUtil.MANTEM;
    }

    private DocumentoEntradaItemFacade getFacade() {
        return documentoEntradaItemFacade;
    }

    @Override
    public void pesquisar() {
        recreateTable();
    }

    public void initInsumo(InsumoSub insumoSub) {
        this.insumoFiltro = insumoSub;
    }

    /**
     * SelectItem com a lista de todos os centros de custo cadastrados.
     *
     * @return SelectItem[] de CentroCusto.
     */
    public SelectItem[] getCentroSelect() {
        return JsfUtil.getSelectItems(centroCustoFacade.findAll(), false, FacesContext.getCurrentInstance());
    }

    /**
     * Recupera a lista de documentos de entrada conforme o insumo passado por
     * parâmetro.
     *
     * @param centro
     * @param insumo
     * @return
     */
    public List<DocumentoEntradaItem> getItensInsumo() {
        try {
            return getFacade().findByInsumo(centroFiltro, insumoFiltro);
        } catch (NullPointerException | NoResultException e) {
            return null;
        }
    }

    public DocumentoEntradaItem getCurrent() {
        return current;
    }

    public void setCurrent(DocumentoEntradaItem current) {
        this.current = current;
    }

    public CentroCusto getCentroFiltro() {
        return centroFiltro;
    }

    public void setCentroFiltro(CentroCusto centroFiltro) {
        this.centroFiltro = centroFiltro;
    }

    public InsumoSub getInsumoFiltro() {
        return insumoFiltro;
    }

    public void setInsumoFiltro(InsumoSub insumoFiltro) {
        this.insumoFiltro = insumoFiltro;
    }
}
