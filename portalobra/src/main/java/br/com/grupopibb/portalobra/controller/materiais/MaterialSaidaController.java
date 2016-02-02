/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.materiais;

import br.com.grupopibb.portalobra.acesso.controller.LoginController;
import br.com.grupopibb.portalobra.controller.common.EntityController;
import br.com.grupopibb.portalobra.controller.common.EntityPagination;
import br.com.grupopibb.portalobra.dao.geral.CentroCustoFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialSaidaFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialSaidaItensFacade;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaida;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaidaItens;
import br.com.grupopibb.portalobra.model.tipos.EnumSistemaOrigemEstoque;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import br.com.grupopibb.portalobra.utils.ReportUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

/**
 *
 * @author administrator
 */
@ManagedBean
@ViewScoped
public class MaterialSaidaController extends EntityController<MaterialSaida> implements Serializable {

    @EJB
    private MaterialSaidaFacade materialSaidaFacade;
    @EJB
    private MaterialSaidaItensFacade materialSaidaItensFacade;
    @EJB
    private CentroCustoFacade centroCustoFacade;
    @EJB
    private ReportUtil report;
    private MaterialSaida current;
    private Date dataInicial;
    private Date dataFinal;
    private Long numeroSaida;
    private Long insumoCod;
    private String especificacao;
//-------------------------------
    private CentroCusto centroOrigemT;
    private String docNumeroT;
    private Date saidaDataT;
    private List<MaterialSaidaItens> transferencias;
    private List<MaterialSaidaItens> selecionadosT;
//-------------------------------
    private boolean transfMarcado = false;
    //----------------------------------------------
    @ManagedProperty(value = "#{loginController}")
    private LoginController loginController;

    public void setLoginController(LoginController loginContronller) {
        this.loginController = loginContronller;
    }

    /**
     * Executado após o bean JSF ser criado.
     */
    @PostConstruct
    public void init() {
        initDataInicial();
        initDataFinal();
    }

    /**
     * Executado antes do bean JSF ser destruído.
     */
    @PreDestroy
    public void end() {
    }

    public String initSaida(MaterialSaida saida) {
        this.current = saida;
        return JsfUtil.REL_AUT_SAIDA;
    }

    public MaterialSaidaFacade getFacade() {
        return materialSaidaFacade;
    }

    @Override
    public EntityPagination getPagination() {
        if (pagination == null) {
            pagination = new EntityPagination(15) {
                @Override
                public int getItemsCount() {
                    return getFacade().countParam(loginController.getCentroSelecionado(), numeroSaida, dataInicial, dataFinal, insumoCod, especificacao).intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRangeParam(loginController.getCentroSelecionado(), numeroSaida, dataInicial, dataFinal, insumoCod, especificacao, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    @Override
    protected void setEntity(MaterialSaida t) {
        this.current = t;
    }

    @Override
    protected MaterialSaida getNewEntity() {
        return new MaterialSaida();
    }

    @Override
    protected void performDestroy() {
    }

    @Override
    protected String create() {
        return "";
    }

    @Override
    protected String update() {
        return "";
    }

    @Override
    public String clean() {
        initDataInicial();
        initDataFinal();
        numeroSaida = null;
        insumoCod = null;
        especificacao = null;
        recreateTable();
        return super.clean();
    }

    public void pesquisa() {
        recreateTable();
    }

    public void pesquisaTransferencias() {
        if (selecionadosT != null) {
            selecionadosT.clear();
        }
        transferencias = materialSaidaItensFacade.findTransferencias(centroOrigemT, loginController.getCentroSelecionado(), docNumeroT, saidaDataT);
    }

    public void transfMarcado() {
        for (MaterialSaidaItens item : getTransferencias()) {
            item.setMarcado(transfMarcado);
            addOrRemoveItemT(item);
        }
    }

    public void limpaTransferencias() {
        centroOrigemT = null;
        docNumeroT = null;
        saidaDataT = null;
        transferencias = null;
        selecionadosT = null;
    }

    /**
     * Adiciona ou remove um item da transferência de material na lista de itens
     * selecionados. Essa lista será utilizada para popular a entrada de
     * material no tipo transferência.
     *
     * @param item MaterialSaidaItens item a ser adicionado ou removido de
     * acordo com a propriedade marcado.
     */
    public void addOrRemoveItemT(MaterialSaidaItens item) {
        if (selecionadosT == null) {
            selecionadosT = new ArrayList<>();
        }

        if (item.isMarcado() && !selecionadosT.contains(item)) {
            selecionadosT.add(item);
        } else if (!item.isMarcado() && selecionadosT.contains(item)) {
            selecionadosT.remove(item);
        }
    }

    /**
     * Inicia a data inicial na pesquisa de saidas de material com o primeiro
     * dia do mês atual.
     */
    private void initDataInicial() {
        if (dataInicial == null) {
            dataInicial = DateUtils.incrementar(new Date(), -3, Calendar.MONTH);
        }
    }

    /**
     * Inicia a data inicial na pesquisa de saidas de material com a data atual.
     */
    private void initDataFinal() {
        if (dataFinal == null) {
            dataFinal = new Date();
        }
    }

    /**
     * SelectItem[] com os tipos de sistema de origem de saidas: SIMAT e SISUP.
     *
     * @return SelectItem[]{SISUP, SIMAT}
     */
    public SelectItem[] getOrigemSelect() {
        return JsfUtil.getEnumSelectItems(EnumSistemaOrigemEstoque.class, false, FacesContext.getCurrentInstance());
    }

    /**
     * Pesquisa as transferências de materiais na saida de materiais.
     *
     * @return Lista com os itens transferidos de acordo com os parâmetros
     * passados.
     */
    public List<MaterialSaidaItens> getTransferencias() {
        return transferencias;
    }

    /**
     * Traz todos os Centros de Custo cadastrados no sistema.
     *
     * @return SelectItem[] dos Centros de Custo.
     */
    public SelectItem[] getCentroCustoSelect() {
        List<CentroCusto> centro = centroCustoFacade.findAll();
        return JsfUtil.getSelectItems(centro, false, FacesContext.getCurrentInstance());
    }
    
    public void abrirRelatorioAutorizacao(){
        
        this.report.abrirRelatorioAutorizacao(this.current);
        
    }

    public MaterialSaida getCurrent() {
        return current;
    }

    public void setCurrent(MaterialSaida current) {
        this.current = current;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Long getNumeroSaida() {
        return numeroSaida;
    }

    public void setNumeroSaida(Long numeroSaida) {
        this.numeroSaida = numeroSaida;
    }

    public Long getInsumoCod() {
        return insumoCod;
    }

    public void setInsumoCod(Long insumoCod) {
        this.insumoCod = insumoCod;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }

    public CentroCusto getCentroOrigemT() {
        return centroOrigemT;
    }

    public void setCentroOrigemT(CentroCusto centroOrigemT) {
        this.centroOrigemT = centroOrigemT;
    }

    public String getDocNumeroT() {
        return docNumeroT;
    }

    public void setDocNumeroT(String docNumeroT) {
        this.docNumeroT = docNumeroT;
    }

    public Date getSaidaDataT() {
        return saidaDataT;
    }

    public void setSaidaDataT(Date saidaDataT) {
        this.saidaDataT = saidaDataT;
    }

    public List<MaterialSaidaItens> getSelecionadosT() {
        return selecionadosT;
    }

    public void setSelecionadosT(List<MaterialSaidaItens> selecionadosT) {
        this.selecionadosT = selecionadosT;
    }

    public boolean isTransfMarcado() {
        return transfMarcado;
    }

    public void setTransfMarcado(boolean transfMarcado) {
        this.transfMarcado = transfMarcado;
    }

    /**
     * Limpa o cache da tabela do MaterialSaidaController da request atual.
     */
    public void updateDataModel() {
        materialSaidaFacade.clearCache();
    }
}
