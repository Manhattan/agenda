/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.materiais;

import br.com.grupopibb.portalobra.acesso.controller.LoginController;
import br.com.grupopibb.portalobra.controller.common.EntityController;
import br.com.grupopibb.portalobra.controller.common.EntityPagination;
import br.com.grupopibb.portalobra.dao.geral.CentroCustoFacade;
import br.com.grupopibb.portalobra.dao.insumo.InsumoSubFacade;
import br.com.grupopibb.portalobra.dao.materiais.MateriaisEstoqueFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialSaidaItensFacade;
import br.com.grupopibb.portalobra.model.followup.FollowUpSolicitacoes;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaidaItens;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import java.io.Serializable;
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
import javax.persistence.NoResultException;

/**
 *
 * @author administrator
 */
@ManagedBean
@ViewScoped
public class MaterialSaidaItensController extends EntityController<MaterialSaidaItens> implements Serializable {

    @EJB
    private MaterialSaidaItensFacade materialSaidaItensFacade;
    @EJB
    private CentroCustoFacade centroCustoFacade;
    @EJB
    private MateriaisEstoqueFacade materiaisEstoqueFacade;
    private MaterialSaidaItens current;
    private Date dataInicial;
    private Date dataFinal;
    private String numeroDoc;
    private String insumoCod;
    private String insumoSubCod;
    private String especificacao;
    private String tipoMovimento;
    private Integer registrosPorPagina = 20;
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

    public MaterialSaidaItensFacade getFacade() {
        return materialSaidaItensFacade;
    }

    public MaterialSaidaItens findSaidaItem(Long saidaNumero, Integer itemNumero) {
        return getFacade().find(saidaNumero, itemNumero);
    }

    @Override
    public EntityPagination getPagination() {
        if (pagination == null) {
            pagination = new EntityPagination(registrosPorPagina) {
                @Override
                public int getItemsCount() {
                    return getFacade().countParam(loginController.getCentroSelecionado(), numeroDoc, dataInicial, dataFinal, insumoCod, insumoSubCod, especificacao, tipoMovimento).intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRangeParam(loginController.getCentroSelecionado(), numeroDoc, dataInicial, dataFinal, insumoCod, insumoSubCod, especificacao, tipoMovimento, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    @Override
    protected void setEntity(MaterialSaidaItens t) {
        this.current = t;
    }

    @Override
    protected MaterialSaidaItens getNewEntity() {
        return new MaterialSaidaItens();
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
        numeroDoc = null;
        insumoCod = null;
        especificacao = null;
        recreateTable();
        return super.clean();
    }

    @Override
    public void pesquisar() {
        recreateTable();
    }

    /**
     * Inicia a data inicial na pesquisa de saidas de material com o primeiro
     * dia do mês atual.
     */
    private void initDataInicial() {
        if (dataInicial == null) {
            dataInicial = new Date();
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

    public Double getEstoqueAtual(InsumoSub insumoSub) {
        String anoMes = DateUtils.getDataFormatada("YYYYMM", new Date());
        return materiaisEstoqueFacade.findSaldo(loginController.getCentroSelecionado(), insumoSub, anoMes);
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

    /**
     * Limpa o cache da tabela do MaterialSaidaItensController da request atual.
     */
    public void updateController() {
        materialSaidaItensFacade.clearCache();
    }

    public MaterialSaidaItens getCurrent() {
        return current;
    }

    public void setCurrent(MaterialSaidaItens current) {
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

    public String getNumeroDoc() {
        return numeroDoc;
    }

    public void setNumeroDoc(String numeroDoc) {
        this.numeroDoc = numeroDoc;
    }

    public String getInsumoCod() {
        return insumoCod;
    }

    public void setInsumoCod(String insumoCod) {
        this.insumoCod = insumoCod;
    }

    public String getInsumoSubCod() {
        return insumoSubCod;
    }

    public void setInsumoSubCod(String insumoSubCod) {
        this.insumoSubCod = insumoSubCod;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public Integer getRegistrosPorPagina() {
        return registrosPorPagina;
    }

    public void setRegistrosPorPagina(Integer registrosPorPagina) {
        this.registrosPorPagina = registrosPorPagina;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }
}
