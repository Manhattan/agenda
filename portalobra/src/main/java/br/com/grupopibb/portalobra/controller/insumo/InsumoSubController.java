/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.insumo;

import br.com.grupopibb.portalobra.acesso.controller.LoginController;
import br.com.grupopibb.portalobra.controller.common.EntityController;
import br.com.grupopibb.portalobra.controller.common.EntityPagination;
import br.com.grupopibb.portalobra.dao.followup.FollowUpSolicitacoesFacade;
import br.com.grupopibb.portalobra.dao.insumo.CaracterizacaoInsumosFacade;
import br.com.grupopibb.portalobra.dao.insumo.ClasseInsumosFacade;
import br.com.grupopibb.portalobra.dao.insumo.GrupoInsumosFacade;
import br.com.grupopibb.portalobra.dao.insumo.InsumoSubFacade;
import br.com.grupopibb.portalobra.dao.projeto.ProjetoPlanejamentoFacade;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.CaracterizacaoInsumos;
import br.com.grupopibb.portalobra.model.insumo.ClasseInsumos;
import br.com.grupopibb.portalobra.model.insumo.GrupoInsumos;
import br.com.grupopibb.portalobra.model.insumo.Insumo;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.model.solicitacaocompra.SolicitacaoCompraItem;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import br.com.grupopibb.portalobra.utils.ReportUtil;
import java.io.Serializable;
import java.util.ArrayList;
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
 * @author tone.lima
 */
@ManagedBean
@ViewScoped
public class InsumoSubController extends EntityController<InsumoSub> implements Serializable {
    @EJB
    private ReportUtil reportUtil;

    @EJB
    private InsumoSubFacade insumoSubFacade;
    @EJB
    private ClasseInsumosFacade classeInsumosFacade;
    @EJB
    private GrupoInsumosFacade grupoInsumosFacade;
    @EJB
    private CaracterizacaoInsumosFacade caracterizacaoInsumosFacade;
    @EJB
    private FollowUpSolicitacoesFacade followUpSolicitacoesFacade;
    @EJB
    private ProjetoPlanejamentoFacade projPlanFacade;
    //Filtros para seleção de insumo 
    private String filtroSolicInsumoCod = "";
    private String filtroSolicInsumoSubCod;
    private String filtroSolicInsumoEspec = "";
    private ClasseInsumos filtroClasseInsumo;
    private GrupoInsumos filtroGrupoInsumo;
    private CaracterizacaoInsumos filtroCaracInsumo;
    //----------------------------------------------
    private SelectItem[] insumoClasseSelect;
    private SelectItem[] insumoGrupoSelect;
    private SelectItem[] insumoCaracterizacaoSelect;
    //----------------------------------------------
    private List<String> insumosSelecionados;
    private List<SolicitacaoCompraItem> solicItemSelecionados;
    //---------------------------------------------------
    private InsumoSub current;
    private boolean obraLinkadaOrcamento = false;
    private boolean desconsideraLinkOrcamento = true;
    //---------------------------------------------------
    private int registrosPorPagina = 10;
    //----------------------------------------------
    @ManagedProperty(value = "#{loginController}")
    private LoginController loginController;
    
    private boolean enableBtnDialogUltimaSolic = false;

    //-----------------------------------------------------------
    private Date dataParametro;

    public Date getDataParametro() {
        return dataParametro;
    }

    public void setDataParametro(Date dataParametro) {
        this.dataParametro = dataParametro;
    }
    
    
    
    public void setLoginController(LoginController loginContronller) {
        this.loginController = loginContronller;
    }

    public boolean isEnableBtnDialogUltimaSolic() {
        return enableBtnDialogUltimaSolic;
    }

    public void setEnableBtnDialogUltimaSolic(boolean enableBtnDialogUltimaSolic) {
        this.enableBtnDialogUltimaSolic = enableBtnDialogUltimaSolic;
    }
    

    /**
     * Executado após o bean JSF ser criado.
     */
    @PostConstruct
    public void init() {
    }

    /**
     * Executado antes do bean JSF ser destruído.
     */
    @PreDestroy
    public void end() {
    }

    @Override
    protected void setEntity(InsumoSub t) {
        this.current = t;
    }

    @Override
    protected InsumoSub getNewEntity() {
        InsumoSub ins = new InsumoSub();
        return ins;
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

    public InsumoSubFacade getFacade() {
        return insumoSubFacade;
    }

    public boolean isObraLinkadaOrcamento() {
        return obraLinkadaOrcamento;
    }

    public boolean isDesconsideraLinkOrcamento() {
        return desconsideraLinkOrcamento;
    }
    
    public void verifyCentroIsNull(CentroCusto centro){
        
        if(centro != null){
            this.setEnableBtnDialogUltimaSolic(true);
        }else{
            this.setEnableBtnDialogUltimaSolic(false);
        }
        
    }

    /**
     * Pesquisa o valor orçado a realizar de um determinado insumo em um centro
     * de custo.
     *
     * @param insumo Insumo a ser calculado o saldo a realizar.
     * @param centro Centro com orçamento.
     * @return Saldo orçado a realizar.
     */
    public String getValorOrcadoCentro(Insumo insumo, CentroCusto centro) {
        return NumberUtils.formatDecimal(projPlanFacade.getValorOrc(centro.getProjCod(), centro.getOrcCod(), centro.getPlanCod(), insumo.getCodigo().intValue()), 4);
    }

    /**
     * Pesquisa o valor orçado a realizar de um determinado insumo no centro de
     * custo atual.
     *
     * @param insumo Insumo a ser calculado o saldo a realizar.
     * @return Saldo orçado a realizar.
     */
    public String getValorOrcado(Insumo insumo) {
        try {
            return NumberUtils.formatDecimal(projPlanFacade.getValorOrc(loginController.getCentroSelecionado().getProjCod(), loginController.getCentroSelecionado().getOrcCod(), loginController.getCentroSelecionado().getPlanCod(), insumo.getCodigo().intValue()), 4);
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Pesquisa a quantidade orçada a realizar de um Insumo no centro de custo
     * atual selecionado dentro da tabela de FollowUp das Solicitações de
     * Compra.
     *
     * @param insumo
     * @return Quantidade orçada a realizar.
     */
    public Double getQuantidadeOrcamento(Insumo insumo) {
        if (loginController.getCentroSelecionado() != null && insumo != null && insumo.getCodigo() != null) {
            return NumberUtils.isNull(followUpSolicitacoesFacade.findQuantidadeOrcamentoByInsumo(loginController.getCentroSelecionado(), insumo.getCodigo()), 0.0);
        } else {
            return 0.0;
        }
    }

    public void desconsiderarLinkOrcamento() {
        this.desconsideraLinkOrcamento = true;
    }

    public void considerarLinkOrcamento() {
        this.desconsideraLinkOrcamento = false;
    }

    @Override
    public void pesquisar() {
        recreateTable();
    }

    @Override
    public EntityPagination getPagination() {
        if (pagination == null) {
            pagination = new EntityPagination(registrosPorPagina) {
                String codigoCarac = filtroCaracInsumo == null ? "" : filtroCaracInsumo.getCodigo();
                String codigoGrupo = filtroGrupoInsumo == null ? "" : filtroGrupoInsumo.getCodigo();
                String codigoClasse = filtroClasseInsumo == null ? "" : filtroClasseInsumo.getCodigo();

                @Override
                public int getItemsCount() {
                    try {
                        return getFacade().countParam(filtroSolicInsumoCod, filtroSolicInsumoSubCod, filtroSolicInsumoEspec, codigoCarac, codigoClasse, codigoGrupo, loginController.getCentroSelecionado().isObraLinkadaOrcamento(), desconsideraLinkOrcamento, loginController.getCentroSelecionado().getPlanCod()).intValue();
                    } catch (NullPointerException e) {
                        return 0;
                    }
                }

                @Override
                public DataModel createPageDataModel() {
                    try {
                        return new ListDataModel(getFacade().findRangeParam(loginController.getCentroSelecionado(), filtroSolicInsumoCod, filtroSolicInsumoSubCod, filtroSolicInsumoEspec, codigoCarac, codigoClasse, codigoGrupo, loginController.getCentroSelecionado().isObraLinkadaOrcamento(), desconsideraLinkOrcamento, loginController.getCentroSelecionado().getPlanCod(), new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                    } catch (NullPointerException e) {
                        return null;
                    }
                }
            };
        }
        return pagination;
    }

    public String limparInsumos() {
        limparIncluirItem();
        return JsfUtil.MANTEM;
    }

    private void limparIncluirItem() {
        limparInsumosSelecionados();
        solicItemSelecionados = null;
        filtroCaracInsumo = null;
        filtroGrupoInsumo = null;
        filtroClasseInsumo = null;
        filtroSolicInsumoCod = "";
        filtroSolicInsumoEspec = "";
    }

    public String limparInsumosSelecionados() {
        insumosSelecionados = null;
        recreateTable();
        return JsfUtil.MANTEM;
    }

    /**
     * Limpa todos os dados salvos em memória no objeto DAO principal.
     */
    public void updateTable() {
        insumoSubFacade.clearCache();
    }

    public void addOrRemoveInsumo(Long insumoCod, Integer insumoSubCod, boolean marcado) {
        String codigoInsumo = insumoCod + "_" + insumoSubCod;
        insumosSelecionados = insumosSelecionados == null ? new ArrayList<String>() : insumosSelecionados;
        if (marcado) {
            insumosSelecionados.add(codigoInsumo);
        } else {
            insumosSelecionados.remove(codigoInsumo);
        }
    }

    /**
     * Inclui o Insumo para solicitacao de compra e movimentação de materiais.
     * Também inclui a lista de itens solicitados a serem autorizados ou
     * rejeitados.
     *
     * @param codigoInsumo
     * @param marcado
     * @param solicitacaoCompra
     * @param solicItemNumero
     */
    public void addOrRemoveInsumoF(String subInsumoId, boolean marcado, SolicitacaoCompraItem solicItem) {
        insumosSelecionados = insumosSelecionados == null ? new ArrayList<String>() : insumosSelecionados;
        solicItemSelecionados = solicItemSelecionados == null ? new ArrayList<SolicitacaoCompraItem>() : solicItemSelecionados;
        if (marcado) {
            solicItemSelecionados.add(solicItem);
            insumosSelecionados.add(subInsumoId);
        } else {
            solicItemSelecionados.remove(solicItem);
            insumosSelecionados.remove(subInsumoId);
        }
    }

    public boolean isHasSolicItemSelecionados() {
        return solicItemSelecionados != null && !solicItemSelecionados.isEmpty();
    }

    
    
    public SelectItem[] getInsumoClasseSelect() {
        insumoClasseSelect = JsfUtil.getSelectItems(classeInsumosFacade.findAll(), false, FacesContext.getCurrentInstance());
        return insumoClasseSelect;
    }

    public void setInsumoClasseSelect(SelectItem[] insumoClasseSelect) {
        this.insumoClasseSelect = insumoClasseSelect;
    }

    public SelectItem[] getInsumoGrupoSelect() {
        try {
            filtroClasseInsumo = filtroClasseInsumo == null ? new ClasseInsumos() : filtroClasseInsumo;
            List<GrupoInsumos> grupo = grupoInsumosFacade.findByClasse(filtroClasseInsumo.getCodigo());
            grupo = grupo == null ? new ArrayList<GrupoInsumos>() : grupo;
            insumoGrupoSelect = JsfUtil.getSelectItems(grupo, false, FacesContext.getCurrentInstance());
            return insumoGrupoSelect;
        } catch (NullPointerException e) {
            return new SelectItem[0];
        }
    }

    public void setInsumoGrupoSelect(SelectItem[] insumoGrupoSelect) {
        this.insumoGrupoSelect = insumoGrupoSelect;
    }

    public SelectItem[] getInsumoCaracterizacaoSelect() {
        try {
            filtroClasseInsumo = filtroClasseInsumo == null ? new ClasseInsumos() : filtroClasseInsumo;
            filtroGrupoInsumo = filtroGrupoInsumo == null ? new GrupoInsumos() : filtroGrupoInsumo;
            String classeCod = filtroClasseInsumo.getCodigo() == null ? "" : filtroClasseInsumo.getCodigo();
            String grupoCod = filtroGrupoInsumo.getCodigo() == null ? "" : filtroGrupoInsumo.getCodigo();
            List<CaracterizacaoInsumos> caracterizacao = caracterizacaoInsumosFacade.findParam(grupoCod, classeCod);
            caracterizacao = caracterizacao == null ? new ArrayList<CaracterizacaoInsumos>() : caracterizacao;
            insumoCaracterizacaoSelect = JsfUtil.getSelectItems(caracterizacao, false, FacesContext.getCurrentInstance());
            return insumoCaracterizacaoSelect;
        } catch (NullPointerException e) {
            return new SelectItem[0];
        }
    }

    public void setInsumoCaracterizacaoSelect(SelectItem[] insumoCaracterizacaoSelect) {
        this.insumoCaracterizacaoSelect = insumoCaracterizacaoSelect;
    }

    public InsumoSubFacade getInsumoSubFacade() {
        return insumoSubFacade;
    }

    public void setInsumoSubFacade(InsumoSubFacade insumoSubFacade) {
        this.insumoSubFacade = insumoSubFacade;
    }

    public String getFiltroSolicInsumoCod() {
        return filtroSolicInsumoCod;
    }

    public void setFiltroSolicInsumoCod(String filtroSolicInsumoCod) {
        this.filtroSolicInsumoCod = filtroSolicInsumoCod;
    }

    public String getFiltroSolicInsumoSubCod() {
        return filtroSolicInsumoSubCod;
    }

    public void setFiltroSolicInsumoSubCod(String filtroSolicInsumoSubCod) {
        this.filtroSolicInsumoSubCod = filtroSolicInsumoSubCod;
    }

    public String getFiltroSolicInsumoEspec() {
        return filtroSolicInsumoEspec;
    }

    public void setFiltroSolicInsumoEspec(String filtroSolicInsumoEspec) {
        this.filtroSolicInsumoEspec = filtroSolicInsumoEspec;
    }

    public ClasseInsumos getFiltroClasseInsumo() {
        filtroClasseInsumo = filtroClasseInsumo == null ? new ClasseInsumos().initClasse("", "") : filtroClasseInsumo;
        return filtroClasseInsumo;
    }

    public void setFiltroClasseInsumo(ClasseInsumos filtroClasseInsumo) {
        this.filtroClasseInsumo = filtroClasseInsumo;
    }

    public GrupoInsumos getFiltroGrupoInsumo() {
        filtroGrupoInsumo = filtroGrupoInsumo == null ? new GrupoInsumos().initClasse("", "", "") : filtroGrupoInsumo;
        return filtroGrupoInsumo;
    }

    public void setFiltroGrupoInsumo(GrupoInsumos filtroGrupoInsumo) {
        this.filtroGrupoInsumo = filtroGrupoInsumo;
    }

    public CaracterizacaoInsumos getFiltroCaracInsumo() {
        filtroCaracInsumo = filtroCaracInsumo == null ? new CaracterizacaoInsumos().initClasse("", "", "", "") : filtroCaracInsumo;
        return filtroCaracInsumo;
    }

    public void setFiltroCaracInsumo(CaracterizacaoInsumos filtroCaracInsumo) {
        this.filtroCaracInsumo = filtroCaracInsumo;
    }

    public List<String> getInsumosSelecionados() {
        return insumosSelecionados == null ? new ArrayList<String>() : insumosSelecionados;
    }

    public void setInsumosSelecionados(List<String> insumosSelecionados, List<SolicitacaoCompraItem> solicItemSelecionados) {
        this.insumosSelecionados = insumosSelecionados;
        this.solicItemSelecionados = solicItemSelecionados;
    }

    public InsumoSub getCurrent() {
        return current;
    }

    public void setCurrent(InsumoSub current) {
        this.current = current;
    }

    public List<SolicitacaoCompraItem> getSolicItemSelecionados() {
        return solicItemSelecionados;
    }

    public void setSolicItemSelecionados(List<SolicitacaoCompraItem> solicItemSelecionados) {
        this.solicItemSelecionados = solicItemSelecionados;
    }
    
    /**
     * Gera Relatório em PDF e exporta para o navegador
     * @param insSub recebe um InsumoSub e acessa seu codigo usando getInsumoCod().toString()
     * @param centCusto objeto centro contendo suas chaves compostas
     */
    public void generateReportInsumo(InsumoSub insSub, CentroCusto centCusto){
        reportUtil.abrirRelatorioUltimasSolicitacoes(current.getInsumoCod().toString(), centCusto);
    }
    
   
    
    /**
     * Gera Relatório em Excel e exporta como download
     * @param insSub recebe um InsumoSub e acessa seu codigo usando getInsumoCod().toString()
     * @param centCusto objeto centro contendo suas chaves compostas
     */
    public void generateExcelInsumo(InsumoSub insSub, CentroCusto centCusto){
        reportUtil.exportReportExcel(current.getInsumoCod().toString(), centCusto);
    }
}
