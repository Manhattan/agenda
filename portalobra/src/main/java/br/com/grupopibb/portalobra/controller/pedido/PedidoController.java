/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.pedido;

import br.com.grupopibb.portalobra.controller.common.EntityController;
import br.com.grupopibb.portalobra.controller.common.EntityPagination;
import br.com.grupopibb.portalobra.dao.geral.CentroCustoFacade;
import br.com.grupopibb.portalobra.dao.geral.CredorFacade;
import br.com.grupopibb.portalobra.dao.pedido.PedidoFacade;
import br.com.grupopibb.portalobra.utils.ReportUtil;
import br.com.grupopibb.portalobra.model.ar.DocumentoEntradaAvaliacao;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.geral.Credor;
import br.com.grupopibb.portalobra.model.pedido.Pedido;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;

/**
 *
 * @author tone.lima
 */
@ManagedBean
@ViewScoped
public class PedidoController extends EntityController<Pedido> implements Serializable {

    private Pedido current;
    @EJB
    private ReportUtil report;
    @EJB
    private PedidoFacade pedidoFacade;
    @EJB
    private CentroCustoFacade centroFacade;
    @EJB
    private CredorFacade credorFacade;
    private SelectItem[] pedidoSelected;
    private SelectItem[] centroSelected;
    private SelectItem[] fornecedorSelected;
    private SelectItem[] fornecedorPedidoSelected;
    private Pedido filtroPedido = new Pedido();
    private CentroCusto filtroCentroCusto;
    private Credor filtroCredor;
    private Credor filtroCredorPedido;
    private List<Pedido> listPedidos;
    private boolean enableButton = false;

    public List<String> getPedidoSelectedString() {
//        List<Pedido> listPedidos = new ArrayList<>();
        if (this.getFiltroCentroCusto() != null && this.getFiltroCredor() != null) {
            List<Pedido> pedido;
            pedido = pedidoFacade.findByCentroAndCredor(this.getFiltroCentroCusto(), this.getFiltroCredor());
            List<String> numeroPedido = new ArrayList<>();

            for (Pedido numero : pedido) {
                numeroPedido.add(String.valueOf(numero.getIdSistema()));
            }

            if (pedido == null) {
                pedido = new ArrayList<>();
            }

            return numeroPedido;
        } else {

            return null;
        }
    }

    public SelectItem[] getPedidoSelected() {
        List<Pedido> listPedido;
        listPedido = pedidoFacade.findByCentroAndCredor(this.getFiltroCentroCusto(), this.getFiltroCredor());

        this.pedidoSelected = JsfUtil.getSelectItems(listPedidos, false, FacesContext.getCurrentInstance());
        return this.pedidoSelected;
    }

    public void setPedidoSelected(SelectItem[] pedidoSelected) {
        this.pedidoSelected = pedidoSelected;
    }

    public List<Pedido> getListPedidos() {

        try {
            this.listPedidos = pedidoFacade.findByCentroAndCredor(this.getFiltroCentroCusto(), this.getFiltroCredor());
            return this.listPedidos;
        } catch (NullPointerException | NoResultException e) {
            return null;
        }
    }

    public void setListPedidos(List<Pedido> listPedidos) {
        this.listPedidos = listPedidos;
    }

    public SelectItem[] getCentroSelected() {
        try {
            this.centroSelected = JsfUtil.getSelectItems(centroFacade.findCentrosWithPedidosByForn(filtroCredor.getCpfCnpj()), false, FacesContext.getCurrentInstance());
            return this.centroSelected;
        } catch (NullPointerException | NoResultException e) {
            return null;
        }
//        
//        if (this.getFiltroCredor() != null) {
//            Set<CentroCusto> c = new HashSet<CentroCusto>();
//            List<CentroCusto> cAtual = new ArrayList<>();
//            List<Pedido> p = this.pedidoFacade.findCentroByCredor(this.getFiltroCredor());
//            int i = 0;
//            for (Pedido pedido : p) {
//                c.add(pedido.getCentroCusto());
//            }
//            CentroCusto[] listAtual = c.toArray(new CentroCusto[c.size()]);
//            cAtual.addAll(Arrays.asList(listAtual));
//            
//            findByEmpresaCodigo
//            
//            this.centroSelected = JsfUtil.getSelectItems(cAtual, false, FacesContext.getCurrentInstance());
//            
//            this.centroSelected = JsfUtil.getSelectItems(cAtual, false, FacesContext.getCurrentInstance());
//            return centroSelected;
//        } else {
//            return null;
//        }
//        
    }

    public void setCentroSelected(SelectItem[] centroSelected) {
        this.centroSelected = centroSelected;
    }

    public SelectItem[] getFornecedorSelected() {
        List<Credor> credorList = this.credorFacade.findAll();

        this.fornecedorSelected = JsfUtil.getSelectItems(credorList, false, FacesContext.getCurrentInstance());
        // this.fornecedorSelected = JsfUtil.getSelectItems(this.credorFacade.findAll(), false, FacesContext.getCurrentInstance());
        return fornecedorSelected;
    }

    public void setFornecedorSelected(SelectItem[] fornecedorSelected) {
        this.fornecedorSelected = fornecedorSelected;
    }

    public SelectItem[] getFornecedorPedidoSelected() {

        return fornecedorPedidoSelected;
    }

    public List<Credor> getSelectedCredorPedido() {

        if (this.getFiltroCentroCusto() != null && this.getFiltroCredor() != null) {
            return credorFacade.listCredorByPedido(this.filtroCentroCusto, this.filtroCredor);
        } else {
            return null;
        }
    }

    public void setFornecedorPedidoSelected(SelectItem[] fornecedorPedidoSelected) {
        this.fornecedorPedidoSelected = fornecedorPedidoSelected;
    }

    public Credor getFiltroCredorPedido() {
        return filtroCredorPedido;
    }

    public void setFiltroCredorPedido(Credor filtroCredorPedido) {
        this.filtroCredorPedido = filtroCredorPedido;
    }

    public CentroCusto getFiltroCentroCusto() {
        return filtroCentroCusto;
    }

    public void setFiltroCentroCusto(CentroCusto filtroCentroCusto) {
        this.filtroCentroCusto = filtroCentroCusto;
    }

    public Credor getFiltroCredor() {

        return filtroCredor;
    }

    public void setFiltroCredor(Credor filtroCredor) {
        this.filtroCredor = filtroCredor;
    }

    public Pedido getFiltroPedido() {
//        if (filtroPedido == null) {
//            filtroPedido  = new Pedido();
//        }
        return filtroPedido;
    }

    public void setFiltroPedido(Pedido filtroPedido) {
        this.filtroPedido = filtroPedido;
    }

    public boolean isEnableButton() {
        return enableButton;
    }

    public void setEnableButton(boolean enableButton) {
        this.enableButton = enableButton;
    }
    
    

    @PostConstruct
    public void init() {
    }

    @Override
    protected void setEntity(Pedido t) {
        this.current = t;
    }

    @Override
    protected Pedido getNewEntity() {
        return new Pedido();
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

    @Override
    public EntityPagination getPagination() {
        return null;
    }

    public String initPedido(Pedido pedido) {
        this.current = pedido;
        report.abrirRelatorioPedidosDS(pedido);

        return JsfUtil.MANTEM;
    }

    public Pedido getCurrent() {
        return current;
    }

    public void setCurrent(Pedido current) {
        this.current = current;
    }

    public List<DocumentoEntradaAvaliacao> getItensAvaliacao() {
        if (current != null && current.getItens() != null && !current.getItens().isEmpty() && current.getItens().get(0).getDocumentoEntradaItem() != null && current.getItens().get(0).getDocumentoEntradaItem().getDocumentoEntrada() != null && current.getItens().get(0).getDocumentoEntradaItem().getDocumentoEntrada().getItensAvaliacao() != null) {
            return current.getItens().get(0).getDocumentoEntradaItem().getDocumentoEntrada().getItensAvaliacao();
        }
        return new ArrayList<>();
    }
    
    /**
     * Metodo que controla o botao de executar do dialog.
     * Verifica se os objetos de Credor, Centro e Pedido_Numero
     * estão nullos, mudando o estado da variavel 
     * enableButton de false para true senão estiverem.
     */
    public void verifySelectsAreNull(){
        if(this.getFiltroCredor() != null && this.getFiltroCentroCusto() != null && this.getFiltroPedido().getIdSistema() != null){
            this.setEnableButton(true);
        } else {
            this.setEnableButton(false);
        }
    }
 

    public void abrirRelatorioPedidos() {
        this.report.abrirRelatorioPosicaoPedidos(this.getFiltroPedido(), this.getFiltroCredor(), this.getFiltroCentroCusto());
    }
    
    public void abrirRelatorioContratos(){
        this.report.abrirRelatorioPosicaoContrato(this.getFiltroPedido(), this.getFiltroCredor(), this.getFiltroCentroCusto());
    }
}
