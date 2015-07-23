/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.followup;

import br.com.grupopibb.portalobra.acesso.controller.LoginController;
import br.com.grupopibb.portalobra.business.followup.FollowUpBusiness;
import br.com.grupopibb.portalobra.business.materiais.EstoqueBusiness;
import br.com.grupopibb.portalobra.business.materiais.MaterialBusiness;
import br.com.grupopibb.portalobra.business.orcamento.OrcamentoBusiness;
import br.com.grupopibb.portalobra.controller.common.EntityController;
import br.com.grupopibb.portalobra.controller.common.EntityPagination;
import br.com.grupopibb.portalobra.dao.ar.DocumentoEntradaItemFacade;
import br.com.grupopibb.portalobra.dao.followup.FollowUpSolicitacoesFacade;
import br.com.grupopibb.portalobra.dao.followup.LogFollowupFacade;
import br.com.grupopibb.portalobra.dao.geral.CentroCustoFacade;
import br.com.grupopibb.portalobra.dao.geral.SequenciaisFacade;
import br.com.grupopibb.portalobra.dao.geral.SequenciaisRangeFacade;
import br.com.grupopibb.portalobra.dao.insumo.InsumoSubFacade;
import br.com.grupopibb.portalobra.dao.materiais.MateriaisEstoqueFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialEntradaFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialEntradaItensFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialSaidaFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialSaidaItensFacade;
import br.com.grupopibb.portalobra.dao.pagamento.TituloAPagarFacade;
import br.com.grupopibb.portalobra.dao.pedido.PedidoAutorizacoesFacade;
import br.com.grupopibb.portalobra.dao.projeto.ProjetoPlanejamentoFacade;
import br.com.grupopibb.portalobra.dao.solicitacaocompra.SolicitacaoCompraFacade;
import br.com.grupopibb.portalobra.dao.solicitacaocompra.SolicitacaoCompraItemFacade;
import br.com.grupopibb.portalobra.dao.solicitacaocompra.SolicitanteFacade;
import br.com.grupopibb.portalobra.exceptions.BusinessException;
import br.com.grupopibb.portalobra.exceptions.EntityException;
import br.com.grupopibb.portalobra.model.ar.DocumentoEntrada;
import br.com.grupopibb.portalobra.model.ar.DocumentoEntradaItem;
import br.com.grupopibb.portalobra.model.followup.FollowUpSolicitacoes;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.Insumo;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntrada;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntradaItens;
import br.com.grupopibb.portalobra.model.materiais.MaterialItemSelecionado;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaida;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaidaItens;
import br.com.grupopibb.portalobra.model.pagamento.TituloAPagar;
import br.com.grupopibb.portalobra.model.solicitacaocompra.SolicitacaoCompra;
import br.com.grupopibb.portalobra.model.solicitacaocompra.SolicitacaoCompraItem;
import br.com.grupopibb.portalobra.model.solicitacaocompra.Solicitante;
import br.com.grupopibb.portalobra.model.tipos.EnumAutorizacao;
import br.com.grupopibb.portalobra.model.tipos.EnumOpeEvtOrcamento;
import br.com.grupopibb.portalobra.model.tipos.EnumSistemaOrigemEstoque;
import br.com.grupopibb.portalobra.model.tipos.EnumSituacaoSolicitacao;
import br.com.grupopibb.portalobra.model.tipos.EnumTipoDocumentoMaterialEntrada;
import br.com.grupopibb.portalobra.model.tipos.EnumTipoDocumentoMaterialSaida;
import br.com.grupopibb.portalobra.model.tipos.EnumTipoMovimentoMaterialEntrada;
import br.com.grupopibb.portalobra.model.tipos.EnumTipoMovimentoMaterialSaida;
import br.com.grupopibb.portalobra.model.tipos.EnumsGeral;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import br.com.grupopibb.portalobra.utils.MessageUtils;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import org.apache.commons.lang3.StringUtils;

/**
 * Classe que representa do followup das solicitações de compra. Reponsável por
 * gerenciar as solicitações de compra e movimentações de material de estoque.
 *
 * @author administrator
 */
@ManagedBean
@ViewScoped
public class FollowUpSolicitacoesController extends EntityController<FollowUpSolicitacoes> implements Serializable {

    @EJB
    private FollowUpSolicitacoesFacade followUpSolicitacoesFacade;
    @EJB
    private SequenciaisFacade sequenciaisFacade;
    @EJB
    private SequenciaisRangeFacade sequenciaisRangeFacade;
    @EJB
    private InsumoSubFacade insumoSubFacade;
    @EJB
    private SolicitacaoCompraItemFacade solicitacaoCompraItemFacade;
    @EJB
    private MateriaisEstoqueFacade materiaisEstoqueFacade;
    @EJB
    private MaterialEntradaFacade materialEntradaFacade;
    @EJB
    private MaterialSaidaFacade materialSaidaFacade;
    @EJB
    private MaterialEntradaItensFacade materialEntradaItensFacade;
    @EJB
    private MaterialSaidaItensFacade materialSaidaItensFacade;
    @EJB
    private CentroCustoFacade centroCustoFacade;
    @EJB
    private SolicitanteFacade solicitanteFacade;
    @EJB
    private SolicitacaoCompraFacade solicitacaoCompraFacade;
    @EJB
    private LogFollowupFacade logFollowupFacade;
    @EJB
    private DocumentoEntradaItemFacade documentoEntradaItemFacade;
    @EJB
    private FollowUpBusiness followUpBusiness;
    @EJB
    private EstoqueBusiness estoqueBusiness;
    @EJB
    private MaterialBusiness materialBusiness;
    @EJB
    private ProjetoPlanejamentoFacade projPlanFacade;
    @EJB
    private OrcamentoBusiness orcamentoBusiness;
    @EJB
    private TituloAPagarFacade tituloAPagarFacade;
    @EJB
    private PedidoAutorizacoesFacade pedidoAutorizacoesFacade;
    //------------------------------
    // private Funcionario funcionario;
    private Solicitante solicitante;
    private FollowUpSolicitacoes current;
    private List<MaterialItemSelecionado> itensMaterialList;
    private List<String> insumosSelecionados;
    private List<SolicitacaoCompraItem> solicItemSelecionados;
    private Integer registrosPorPagina;
    private Date dataInicial;
    private Date dataFinal;
    private String motivoRejeitaSolicitacao = "";
    private EnumSituacaoSolicitacao situacaoFiltro;
    private Integer numeroPedido;
    private Integer numeroAR;
    private String codigoCredor;
    private String razaoSocialCredor;
    private String nomeFantasiaCredor;
    private String cpfCnpjCredor;
    private Boolean filtroRS;
    private Boolean filtroCS;
    private Boolean filtroAS;
    //------------------------
    private Boolean filtroAC;
    //------------------------
    private Boolean filtroRP;
    private Boolean filtroAP;
    //------------------------
    private Boolean filtroENF;
    private Boolean filtroEMO;
    private Boolean filtroRA;
    private Boolean filtroAA;
    //------------------------
    private Boolean filtroPG;
    private Boolean filtroOK;
    //------------------------
    private Boolean filtroR = true;
    //------------------------
    private boolean marcado = false;
    //------------------------
    private boolean showMovimentacaoEntrada = false;
    private boolean showMovimentacaoSaida = false;
    private boolean showSolicitacaoCompra = false;
    //------------------------
    private boolean showPanelFiltros = false;
    //------------------------
    private List<EnumSituacaoSolicitacao> filtrosSituacao;
    private Boolean showDialogInsumo;
    private Boolean showDialogRS;
    private Boolean showDialogCS;
    private Boolean showDialogAS;
    private Boolean showDialogAC;
    private Boolean showDialogRP;
    private Boolean showDialogAP;
    private Boolean showDialogENF;
    private Boolean showDialogEMO;
    private Boolean showDialogRA;
    private Boolean showDialogAA;
    private Boolean showDialogPG;
    private Boolean showDialogOK;
    private Boolean showDialogKardex;
    private Boolean showDialogCentroCusto;
    private boolean showDialogAjuda = false;
    //----------------------------------------------
    private boolean disableButtonSolicitacao = false;
    private boolean disableButtonEntradaMateriais = false;
    private boolean disableButtonSaidaMateriais = false;
    private boolean disableButtonAutItem = true;
    private boolean disableButtonRejItem = true;
    //----------------------------------------------
    private Boolean filtroSelecionarTodos = true;
    private boolean filtroMesclarEstoque = false;
    private boolean filtroMesclarEstoqueUsina = false;
    private boolean filtroSolicMaiorEstoque = false;
    //----------------------------------------------
    private String filtroInsumoCod;
    private String filtroInsumoSubCod;
    private String filtroInsumoEspecificacao;
    private String filtroSolicId;
    //----------- SOLICITACAO COMPRA ---------------
    private SolicitacaoCompra solicitacaoCompra;
    //private boolean obraLinkadaOrcamento = false;
    private Date dataEntregaT;
    private List<SolicitacaoCompraItem> itensSolicitacaoRemovidos;
    //----------------- MATERIAIS ------------------
    private MaterialEntrada materialEntrada;
    private MaterialSaida materialSaida;
    private List<MaterialEntradaItens> itensEntradaRemovidos;
    private List<MaterialSaidaItens> itensSaidaRemovidos;
    private EnumTipoMovimentoMaterialEntrada tipoMovimentoMaterial;
    private EnumTipoMovimentoMaterialSaida tipoMovimentoMaterialSaida;
    private EnumTipoDocumentoMaterialSaida tipoDocumentoMaterialSaida;
    private EnumTipoDocumentoMaterialEntrada tipoDocumentoMaterialEntrada;
    private Map<String, String> tipoDocumentoMaterialSelect;
    // private CentroCusto centroSelecionado;
    private SelectItem[] centrosFuncionario;
    private boolean newSolicitacao = true;
    private boolean newMovEntrada = true;
    private boolean newMovSaida = true;
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
        initAll();
        initDataInicial();
        initDataFinal();
        preencheFiltrosSituacao();
        initDocumentoMaterialSelect();
    }

    /**
     * Executado antes do bean JSF ser destruído.
     */
    @PreDestroy
    public void end() {
        cleanAll();
    }

    private void initAll() {
        changeDialogStatus(false);
        registrosPorPagina = 20;
        filtroRS = true;
        filtroCS = true;
        filtroAS = true;
        filtroAC = true;
        filtroRP = true;
        filtroAP = true;
        filtroENF = true;
        filtroEMO = true;
        filtroRA = true;
        filtroAA = true;
        filtroPG = true;
        filtroOK = true;
        //filtroR = true;
        showDialogInsumo = false;
        showDialogAjuda = false;
        disableButtonSolicitacao = false;
        disableButtonEntradaMateriais = false;
        disableButtonSaidaMateriais = false;
        filtroSelecionarTodos = true;
        filtroMesclarEstoque = false;
        filtroMesclarEstoqueUsina = false;
        filtroSolicMaiorEstoque = false;
        filtroSolicId = "";
        motivoRejeitaSolicitacao = "";
    }


    /* ***************************************************************************************************************************************
     * AQUI FICAM OS MÉTODOS DE LIMPEZA. AQUELES QUE SÃO RESPONSÁVEIS POR "ESVAZIAR" AS VARIÁVEIS E RETORNAR A TELA AO ESTADO SEM ALTERAÇÕES.
     * ***************************************************************************************************************************************/
    @Override
    public String clean() {
        super.clean();
        recreateTable();
        return JsfUtil.MANTEM;
    }

    /**
     * Limpa o cache da tabela do FollowUpSolicitacoesController da request
     * atual.
     */
    public void updateFollowup() {
        followUpSolicitacoesFacade.clearCache();
    }

    public void cleanAll() {
        clean();
        itensEntradaRemovidos = null;
        //funcionario = null;
        current = null;
        itensMaterialList = null;
        solicItemSelecionados = null;
        insumosSelecionados = null;
        registrosPorPagina = null;
        dataInicial = null;
        dataFinal = null;
        motivoRejeitaSolicitacao = null;
        situacaoFiltro = null;
        //------------------------
        filtroRS = null;
        filtroCS = null;
        filtroAS = null;
        filtroAC = null;
        filtroRP = null;
        filtroAP = null;
        filtroENF = null;
        filtroEMO = null;
        filtroRA = null;
        filtroAA = null;
        filtroPG = null;
        filtroOK = null;
        //filtroR = null;
        filtrosSituacao = null;
        //------------------------
        showDialogInsumo = null;
        showDialogRS = null;
        showDialogCS = null;
        showDialogAS = null;
        showDialogAC = null;
        showDialogRP = null;
        showDialogAP = null;
        showDialogENF = null;
        showDialogEMO = null;
        showDialogRA = null;
        showDialogAA = null;
        showDialogPG = null;
        showDialogOK = null;
        showDialogKardex = null;
        showDialogCentroCusto = null;
        //----------------------------------------------
        disableButtonSolicitacao = false;
        disableButtonEntradaMateriais = false;
        disableButtonSaidaMateriais = false;
        disableButtonAutItem = false;
        disableButtonRejItem = false;
        //----------------------------------------------
        //filtroSelecionarTodos = null;
        filtroMesclarEstoque = false;
        filtroMesclarEstoqueUsina = false;
        filtroInsumoCod = null;
        filtroInsumoEspecificacao = null;
        filtroSolicId = null;
        filtroSolicMaiorEstoque = false;
        //----------- SOLICITACAO COMPRA ---------------
        solicitacaoCompra = null;
        //----------- MATERIAIS -------------
        materialEntrada = null;
        materialSaida = null;
        tipoMovimentoMaterial = null;
        tipoMovimentoMaterialSaida = null;
        tipoDocumentoMaterialSaida = null;
        tipoDocumentoMaterialEntrada = null;
        tipoDocumentoMaterialSelect = null;
        //centroSelecionado = null;
        updateFollowup();
    }

    /**
     * Auxilia na limpeza da solicitação de compra e movimentação de materiais
     */
    private void limpaOperacoesFollowUp() {
        updateFollowup();
        solicItemSelecionados = null;
        insumosSelecionados = null;
        newSolicitacao = true;
        newMovEntrada = true;
        newMovSaida = true;
        materialEntrada = null;
        materialSaida = null;
        solicitacaoCompra = null;
        marcado = false;
        changeDialogStatus(false);
        changeDisableButtonStatus(false);
        changeDisableButtonAutorizacao(true);
        recreateTable();
    }

    /**
     * Limpa a solicitação de compras.
     *
     * @return página atual.
     */
    public String limparSolicitacaoCompra() {
        showSolicitacaoCompra = false;
        limpaOperacoesFollowUp();
        marcado = false;
        motivoRejeitaSolicitacao = "";
        return JsfUtil.MANTEM;
    }

    /**
     * Limpa as variáveis e objetos da movimentação de materiais
     */
    public String limparMovimentacaoMaterial() {
        showMovimentacaoEntrada = false;
        showMovimentacaoSaida = false;
        itensMaterialList = null;
        tipoDocumentoMaterialSaida = null;
        tipoMovimentoMaterialSaida = null;
        tipoMovimentoMaterial = null;
        tipoDocumentoMaterialSelect = null;
        limpaOperacoesFollowUp();
        marcado = false;
        return JsfUtil.MANTEM;
    }

    /* *************************************************************************************
     ************ AQUI FICAM OS MÉTODOS QUE INTERFEREM NA TELA DE FOLLOWUP *****************
     ***************************************************************************************/
    /**
     * Recebe o funcionário logado e redireciona a página de acordo com o status
     * do login.
     *
     * @param func
     * @param statusLogin
     * @return página de acotdo com o status do login.
     *
     * public String initFuncionario(Funcionario func, int statusLogin) { if
     * (statusLogin == StatusLogin.ATIVO) { this.funcionario = func;
     * //centroSelecionado = funcionario.getCentros().get(0); return
     * JsfUtil.FOLLOWUP; } else if (statusLogin == StatusLogin.INATIVO) { return
     * JsfUtil.LOGIN_ERROR; } else { return JsfUtil.LOGIN_PAGE; } }
     */

    /* **
     * Recebe o centro de custo selecionado no loginController e o inicia no
     * FollowUpSolicitacoesController.
     
     public void initCentroSelecionado() {
     if (loginController.getFuncionario() == null || centroSelecionado == null) {
     if (this.loginController != null) {
     funcionario = this.loginController.getFuncionario();
     }
     //centroSelecionado = this.loginController.getCentroSelecionado();
     // obraLinkadaOrcamento = projPlanFacade.isCentroLinkOrcamento(centroSelecionado);
     }
     }*/
    public boolean isObraLinkadaOrcamento() {
        return loginController.getCentroSelecionado().isObraLinkadaOrcamento();
        //return this.obraLinkadaOrcamento;
    }

    /**
     * Estabelece a lista de centros de custo do funcionário logado.
     *
     * @return lista com os centros do funcionário logado.
     *
     * public SelectItem[] getCentrosFuncionario() { if (funcionario == null ||
     * funcionario.getCentros() == null) { return null; } else if
     * (centrosFuncionario == null) { centrosFuncionario =
     * JsfUtil.getSelectItems(this.funcionario.getCentros(), false,
     * FacesContext.getCurrentInstance()); } return centrosFuncionario; }
     */
    public SelectItem[] getSolicitanteSelect() {
        return JsfUtil.getSelectItems(solicitanteFacade.findAll(), false, FacesContext.getCurrentInstance());
    }

    /**
     * Inicia a data inicial na pesquisa de solicitações do followup dois meses
     * antes da data atual.
     */
    private void initDataInicial() {
        if (dataInicial == null) {
            dataInicial = DateUtils.incrementar(new Date(), -2, Calendar.MONTH);
        }
    }

    /**
     * Inicia a data final na pesquisa de solicitações do followup com a data
     * atual.
     */
    private void initDataFinal() {
        if (dataFinal == null) {
            dataFinal = new Date();
        }
    }

    public String isTituloConciliado(int numeroTitulo) {
        return tituloAPagarFacade.isTituloConciliado(numeroTitulo) ? "Sim" : "Não";
    }

    public Date getDataConcilicacao(int numeroTitulo) {
        return tituloAPagarFacade.getDataConcilicacao(numeroTitulo);
    }

    /**
     * Retorna o facade da entidade principal.
     */
    private FollowUpSolicitacoesFacade getFacade() {
        return followUpSolicitacoesFacade;
    }

    /**
     * Cria os itens a serem exibidos na tabela de followup
     *
     * @return os itens do followup atual.
     */
    @Override
    public EntityPagination getPagination() {
        if (pagination == null) {
            pagination = new EntityPagination(registrosPorPagina) {
                @Override
                public int getItemsCount() {
                    if (itensCount == 0) {
                        itensCount = getFacade().countParam(loginController.getCentroSelecionado(), dataInicial, dataFinal, filtrosSituacao,
                                filtroMesclarEstoque, filtroMesclarEstoqueUsina, filtroInsumoCod, filtroInsumoSubCod, filtroInsumoEspecificacao, filtroSolicId,
                                solicitante, codigoCredor, razaoSocialCredor, nomeFantasiaCredor, cpfCnpjCredor, numeroPedido, numeroAR,
                                filtroSolicMaiorEstoque).intValue();
                    }
                    return itensCount;
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRangeParam(loginController.getCentroSelecionado(), dataInicial, dataFinal, filtrosSituacao,
                            filtroMesclarEstoque, filtroMesclarEstoqueUsina, filtroInsumoCod, filtroInsumoSubCod, filtroInsumoEspecificacao, filtroSolicId,
                            solicitante, codigoCredor, razaoSocialCredor, nomeFantasiaCredor, cpfCnpjCredor, numeroPedido, numeroAR,
                            new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}, marcado, filtroSolicMaiorEstoque));
                }
            };
        }
        return pagination;
    }

    @Override
    protected void setEntity(FollowUpSolicitacoes t) {
        this.current = t;
    }

    @Override
    protected FollowUpSolicitacoes getNewEntity() {
        return new FollowUpSolicitacoes();
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

    public void filtroSelecionarTodosAction() {
        alterarFiltrosSituacao();
    }

    private void alterarFiltrosSituacao() {
        filtroRS = filtroSelecionarTodos;
        filtroCS = filtroSelecionarTodos;
        filtroAS = filtroSelecionarTodos;
        filtroAC = filtroSelecionarTodos;
        filtroRP = filtroSelecionarTodos;
        filtroAP = filtroSelecionarTodos;
        filtroENF = filtroSelecionarTodos;
        filtroEMO = filtroSelecionarTodos;
        filtroRA = filtroSelecionarTodos;
        filtroAA = filtroSelecionarTodos;
        filtroPG = filtroSelecionarTodos;
        filtroOK = filtroSelecionarTodos;
        //filtroR = true;
    }

    /**
     * Determina que o Panel com os filtros será exibido.
     */
    public void showPanelFiltros() {
        showPanelFiltros = true;
    }

    /**
     * Determina que o Panel com os filtros será escondido.
     */
    public void hidePanelFiltros() {
        showPanelFiltros = false;
    }

    /**
     * Restaura os filtros do followup para o estado original.
     */
    public void cleanPanelFiltros() {
        solicitante = null;
        numeroPedido = null;
        numeroAR = null;
        codigoCredor = null;
        razaoSocialCredor = null;
        nomeFantasiaCredor = null;
        cpfCnpjCredor = null;
        filtroMesclarEstoque = false;
        filtroMesclarEstoqueUsina = false;
        filtroR = true;
        filtroSolicMaiorEstoque = false;
        //filtroSolicId = null;
        //filtroInsumoCod = null;
        //filtroInsumoEspecificacao = null;
    }

    /**
     * Responde se o Panel com os filtros será mostrado ou não.
     *
     * @return Situação de exibição do Panel (true or false).
     */
    public boolean isShowPanelFiltros() {
        return showPanelFiltros;
    }

    /**
     * *************************************************************************
     * AQUI FICAM OS MÉTODOS QUE CONTROLAM A MOVIMENTAÇÃO DE MATERIAIS
     * *************************************************************************
     */
    public void initSelecionadosT(List<MaterialSaidaItens> selecionadosT) {
        for (MaterialSaidaItens item : selecionadosT) {
            if (!materialBusiness.isContainsInsumoSub(materialEntrada, item.getInsumoSub())) {
                materialEntrada.getItens().add(materialBusiness.convertItemSaidaToItemEntrada(item, materialEntrada));
            } else {
                msgInsumoJaIncluso(item.getInsumoSub().getId().toString());
            }
        }
    }

    public void initDocumentoMaterialSelect() {
        if (tipoDocumentoMaterialSelect == null) {
            tipoDocumentoMaterialSelect = new HashMap<>();
        } else {
            tipoDocumentoMaterialSelect.clear();
        }
        tipoDocumentoMaterialSelect.put("AE", "AE");
        tipoDocumentoMaterialSelect.put("NF", "NF-");
        tipoDocumentoMaterialSelect.put("NFF", "NFF");
        tipoDocumentoMaterialSelect.put("REC", "REC");
        tipoDocumentoMaterialSelect.put("CFIS", "CFIS");
    }

//----PREENCHENDO OS FILTROS CHECK EM UM LIST<BOOLEAN>----
    private void preencheFiltrosSituacao() {
        if (filtrosSituacao == null) {
            filtrosSituacao = new ArrayList<>();
        } else {
            filtrosSituacao.clear();
        }

        if (filtroRS) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.RS);
        }
        if (filtroCS) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.CS);
        }
        if (filtroAS) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.AS);
        }
        if (filtroAC) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.CO);
            //  filtrosSituacao.add(EnumSituacaoSolicitacao.CC);
            //  filtrosSituacao.add(EnumSituacaoSolicitacao.AC);
        }
        if (filtroRP) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.RP);
        }
        if (filtroAP) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.AP);
        }
        if (filtroRA) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.RA);
        }
        if (filtroAA) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.AA);
        }
        if (filtroPG) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.PG);
            filtrosSituacao.add(EnumSituacaoSolicitacao.EP);
        }
        if (filtroENF) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.ENF);
        }
        if (filtroEMO) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.EMO);
        }
        if (filtroOK) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.OK);
        }
        if (filtroR) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.R);
        }
        if (filtrosSituacao.isEmpty()) {
            filtrosSituacao.add(EnumSituacaoSolicitacao.NENHUM);
        }
    }

    /**
     * Inicia o item do followup selecionado para exibição no dialog de acordo
     * com a coluna selecionada.
     *
     * @param f item do followup selecionado.
     * @param dialog nome do dialog a ser exibido com as informações do item
     * selecionado.
     */
    public void createCurrent(FollowUpSolicitacoes f, String dialog) {
        current = f;

        if (dialog.equals(getDialogInsumo())) {
            changeDialogStatus(false);
            showDialogInsumo = true;
        } else if (dialog.equals(getDialogRS())) {
            changeDialogStatus(false);
            showDialogRS = true;
        } else if (dialog.equals(getDialogCS())) {
            changeDialogStatus(false);
            showDialogCS = true;
        } else if (dialog.equals(getDialogAS())) {
            changeDialogStatus(false);
            showDialogAS = true;
            //mountSolicitacaoCompraItem();
        } else if (dialog.equals(getDialogAC())) {
            changeDialogStatus(false);
            showDialogAC = true;
            //mountColetaPrecoItem();
        } else if (dialog.equals(getDialogRP())) {
            changeDialogStatus(false);
            showDialogRP = true;
        } else if (dialog.equals(getDialogAP())) {
            changeDialogStatus(false);
            showDialogAP = true;
        } else if (dialog.equals(getDialogRA())) {
            mountDocumentosEntrada();
            changeDialogStatus(false);
            showDialogRA = true;
        } else if (dialog.equals(getDialogENF())) {
            mountDocumentosEntrada();
            changeDialogStatus(false);
            showDialogENF = true;
        } else if (dialog.equals(getDialogEMO())) {
            mountDocumentosEntrada();
            changeDialogStatus(false);
            showDialogEMO = true;
        } else if (dialog.equals(getDialogAA())) {
            mountDocumentosEntrada();
            changeDialogStatus(false);
            showDialogAA = true;
        } else if (dialog.equals(getDialogPG())) {
            mountTitulosAPagar();
            changeDialogStatus(false);
            showDialogPG = true;
        } else if (dialog.equals(getDialogOK())) {
            mountTitulosAPagar();
            changeDialogStatus(false);
            showDialogOK = true;
        } else if (dialog.equals(getDialogKardex())) {
            changeDialogStatus(false);
            showDialogKardex = true;
            mountKardex();
        } else if (dialog.equals(getDialogCentroCusto())) {
            changeDialogStatus(false);
            showDialogCentroCusto = true;
        } else if (dialog.equals(getDialogAjuda())) {
            changeDialogStatus(false);
            showDialogAjuda = true;
        }
    }

    public void changeDisableButtonStatus(boolean status) {
        disableButtonEntradaMateriais = status;
        disableButtonSaidaMateriais = status;
        disableButtonSolicitacao = status;
    }

    public void changeDisableButtonAutorizacao(boolean status) {
        disableButtonAutItem = status;
        disableButtonRejItem = status;
    }

    public void changeStatusOperacoes(boolean status) {
        showMovimentacaoEntrada = status;
        showMovimentacaoSaida = status;
        showSolicitacaoCompra = status;
    }

    public void changeDialogStatus(boolean status) {
        showDialogInsumo = status;
        showDialogRS = status;
        showDialogCS = status;
        showDialogAS = status;
        showDialogAC = status;
        showDialogRP = status;
        showDialogAP = status;
        showDialogENF = status;
        showDialogEMO = status;
        showDialogRA = status;
        showDialogAA = status;
        showDialogPG = status;
        showDialogOK = status;
        showDialogKardex = status;
        showDialogCentroCusto = status;
        showDialogAjuda = status;
    }

    public boolean isShowingDialog() {
        if (showDialogInsumo || showDialogRS || showDialogCS || showDialogAS || showDialogAC || showDialogRP || showDialogAP
                || showDialogENF || showDialogEMO || showDialogRA || showDialogAA || showDialogPG || showDialogOK || showDialogKardex) {
            return true;
        } else {
            return false;
        }
    }

    public void destroyCurrent() {
        current = null;
    }

    public void pesquisa() {
        super.pesquisar();
        preencheFiltrosSituacao();
    }

    public Boolean getShowDialogInsumo() {
        return showDialogInsumo;
    }

    public boolean isShowMovimentacaoEntrada() {
        return showMovimentacaoEntrada;
    }

    public void setShowMovimentacaoEntrada(boolean showMovimentacaoEntrada) {
        this.showMovimentacaoEntrada = showMovimentacaoEntrada;
    }

    public boolean isShowMovimentacaoSaida() {
        return showMovimentacaoSaida;
    }

    public void setShowMovimentacaoSaida(boolean showMovimentacaoSaida) {
        this.showMovimentacaoSaida = showMovimentacaoSaida;
    }

    public boolean isShowSolicitacaoCompra() {
        return showSolicitacaoCompra;
    }

    public void setShowSolicitacaoCompra(boolean showSolicitacaoCompra) {
        this.showSolicitacaoCompra = showSolicitacaoCompra;
    }

    public Boolean getShowDialogRS() {
        return showDialogRS;
    }

    public Boolean getShowDialogCS() {
        return showDialogCS;
    }

    public Boolean getShowDialogAS() {
        return showDialogAS;
    }

    public Boolean getShowDialogAC() {
        return showDialogAC;
    }

    public Boolean getShowDialogRP() {
        return showDialogRP;
    }

    public Boolean getShowDialogAP() {
        return showDialogAP;
    }

    public Boolean getShowDialogENF() {
        return showDialogENF;
    }

    public Boolean getShowDialogEMO() {
        return showDialogEMO;
    }

    public Boolean getShowDialogRA() {
        return showDialogRA;
    }

    public Boolean getShowDialogAA() {
        return showDialogAA;
    }

    public Boolean getShowDialogPG() {
        return showDialogPG;
    }

    public Boolean getShowDialogOK() {
        return showDialogOK;
    }

    public Boolean getShowDialogKardex() {
        return showDialogKardex;
    }

    public Boolean getShowDialogCentroCusto() {
        return showDialogCentroCusto;
    }

    public void setShowDialogCentroCusto(Boolean showDialogCentroCusto) {
        this.showDialogCentroCusto = showDialogCentroCusto;
    }

    public Boolean getShowDialogAjuda() {
        return showDialogAjuda;
    }

    public void setShowDialogAjuda(Boolean showDialogAjuda) {
        this.showDialogAjuda = showDialogAjuda;
    }

    public Boolean getFiltroSelecionarTodos() {
        return filtroSelecionarTodos;
    }

    public void setFiltroSelecionarTodos(Boolean filtroSelecionarTodos) {
        this.filtroSelecionarTodos = filtroSelecionarTodos;
    }

    public boolean isFiltroSolicMaiorEstoque() {
        return filtroSolicMaiorEstoque;
    }

    public void setFiltroSolicMaiorEstoque(boolean filtroSolicMaiorEstoque) {
        this.filtroSolicMaiorEstoque = filtroSolicMaiorEstoque;
    }

    public Boolean getFiltroMesclarEstoque() {
        return filtroMesclarEstoque;
    }

    public void setFiltroMesclarEstoque(Boolean filtroMesclarEstoque) {
        this.filtroMesclarEstoque = filtroMesclarEstoque;
    }

    public Boolean getFiltroMesclarEstoqueUsina() {
        return filtroMesclarEstoqueUsina;
    }

    public void setFiltroMesclarEstoqueUsina(Boolean filtroMesclarEstoqueUsina) {
        this.filtroMesclarEstoqueUsina = filtroMesclarEstoqueUsina;
    }

    public String getFiltroInsumoCod() {
        return filtroInsumoCod;
    }

    public void setFiltroInsumoCod(String filtroInsumoCod) {
        this.filtroInsumoCod = filtroInsumoCod;
    }

    public String getFiltroInsumoSubCod() {
        return filtroInsumoSubCod;
    }

    public void setFiltroInsumoSubCod(String filtroInsumoSubCod) {
        this.filtroInsumoSubCod = filtroInsumoSubCod;
    }

    public String getFiltroInsumoEspecificacao() {
        return filtroInsumoEspecificacao;
    }

    public void setFiltroInsumoEspecificacao(String filtroInsumoEspecificacao) {
        this.filtroInsumoEspecificacao = filtroInsumoEspecificacao;
    }

    public String getFiltroSolicId() {
        return filtroSolicId;
    }

    public void setFiltroSolicId(String filtroSolicId) {
        this.filtroSolicId = filtroSolicId;
    }

    private List<DocumentoEntradaItem> getDocEntItens() {
        //mountSolicitacaoCompraItem();
        return documentoEntradaItemFacade.findBySolic(current.getSolicitacaoCompraItem());
    }

    /**
     * Determina se a solicitação de compra está sendo incluida ou não.
     *
     * @return
     */
    public boolean isNewSolicitacao() {
        return newSolicitacao;
    }

    /**
     * Determina se a movimentação de entrada está sendo incluída ou não.
     *
     * @return
     */
    public boolean isNewMovEntrada() {
        return newMovEntrada;
    }

    public boolean isNewMovSaida() {
        return newMovSaida;
    }

    /**
     * Define se o botão que conclui/desconclui a solicitação será habilitado.
     * De acordo com o negócio, só permitirá alteração até a situação Aguardando
     * Autorização da Solicitação.
     *
     * @return
     */
    public boolean isEnableConclusaoSolicitacao() {
        if (newSolicitacao && loginController.isIncluiConcluiSolicitacao() && isSolicitacaoTemItens()) {
            return true;
        }
        boolean enable = true;
        if (solicitacaoCompra != null && solicitacaoCompra.getNumero() != null && solicitacaoCompra.getNumero() > 0 && !solicitacaoCompra.isItemRemovido()) {
            List<FollowUpSolicitacoes> itens = followUpSolicitacoesFacade.findBySolicitacao(solicitacaoCompra);
            for (FollowUpSolicitacoes item : itens) {
                if (!(item.getSituacao() == EnumSituacaoSolicitacao.CS || item.getSituacao() == EnumSituacaoSolicitacao.AS)) {
                    enable = false;
                }
            }
            itens = null;
        } else {
            enable = false;
        }
        return enable;
    }

    /**
     * Define se o botão que remove (exclui) a solicitação será habilitado. De
     * acordo com o negócio, só permitirá remoção se a solicitação não estiver
     * concluida.
     *
     * @return
     */
    public boolean isEnableRemocaoSolicitacao() {
        if (solicitacaoCompra != null && solicitacaoCompra.getNumero() != null && solicitacaoCompra.getNumero() > 0 && !solicitacaoCompra.isItemRemovido() && solicitacaoCompra.getConcluido() == EnumsGeral.N) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Define se o botão Excluir (solicitação) será exibido na tela. Verifica se
     * o usuário tem acesso à essa funcionalidade e se a solicitação de compra
     * está não concluída.
     *
     * @return
     */
    public boolean isRenderRemocaoSolicitacao() {
        if (loginController.isRemoveSolicitacao()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Define se o botão Concluir (solicitação) será exibido na tela. Verifica
     * se o usuário tem acesso à essa funcionalidade e se a solicitação de
     * compra está não concluída.
     *
     * @return
     */
    public boolean isRenderConcluirSolicitacao() {
        if (loginController.isConcluiSolicitacao() && solicitacaoCompra.getConcluido() == EnumsGeral.N) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Define se o botão Desconcluir (solicitação) será exibido na tela.
     * Verifica se o usuário tem acesso à essa funcionalidade e se a solicitação
     * de compra está concluída.
     *
     * @return
     */
    public boolean isRenderDesconcluirSolicitacao() {
        if (loginController.isConcluiSolicitacao() && solicitacaoCompra.getConcluido() == EnumsGeral.S) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Define se os campos da solicitação podem ser alterados ou não. Verifica
     * se há solicitação de compra selecionada, se a solicitação não está
     * concluida e se o usuário tem autorização de perfil para alterar a
     * solicitação.
     *
     * @return true para editar, e false para não editar
     */
    public boolean isEnableEdicaoSolicitacao() {
        if (solicitacaoCompra != null && solicitacaoCompra.getConcluido() == EnumsGeral.N && loginController.isAlteraSolicitacao()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Define se o botão que remove (exclui) a entrada de material será
     * habilitado. De acordo com o negócio, só permitirá remoção se a data da
     * entrada for no mês atual.
     *
     * @return
     */
    public boolean isEnableRemocaoMatEntrada() {
        if (materialEntrada != null && materialEntrada.getNumeroEntrada() != null && materialEntrada.getNumeroEntrada() > 0 && !materialEntrada.isItemRemovido() && DateUtils.isMesAtual(materialEntrada.getDataEntrada()) && materialEntrada.getEntradaOrigem() == EnumSistemaOrigemEstoque.SIMAT) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Define se o botão Excluir (material de entrada) será exibido na tela.
     * Verifica se o usuário tem acesso à essa funcionalidade.
     *
     * @return
     */
    public boolean isRenderRemocaoMatEntrada() {
        if (loginController.isRemoveEntradaMaterial()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Define se o botão que remove (exclui) a saida de material será
     * habilitado. De acordo com o negócio, só permitirá remoção se a data da
     * saida for no mês atual.
     *
     * @return
     */
    public boolean isEnableRemocaoMatSaida() {
        if (materialSaida != null && materialSaida.getNumeroSaida() != null && materialSaida.getNumeroSaida() > 0 && !materialSaida.isItemRemovido() && DateUtils.isMesAtual(materialSaida.getDataSaida())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Define se o botão Excluir (material de saida) será exibido na tela.
     * Verifica se o usuário tem acesso à essa funcionalidade.
     *
     * @return
     */
    public boolean isRenderRemocaoMatSaida() {
        if (loginController.isRemoveSaidaMaterial()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnableEdicaoMatEntrada() {
        if (materialEntrada != null && loginController.isAlteraEntradaMaterial() && DateUtils.isMesAtual(materialEntrada.getDataEntrada()) && materialEntrada.getEntradaOrigem() == EnumSistemaOrigemEstoque.SIMAT) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnableEdicaoMatSaida() {
        if (materialSaida != null && loginController.isAlteraSaidaMaterial() && DateUtils.isMesAtual(materialSaida.getDataSaida())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnableInsercaoSolicitacao() {
        if (solicitacaoCompra != null && loginController.isIncluiSolicitacao() && isSolicitacaoTemItens() && (newSolicitacao || isEnableEdicaoSolicitacao())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnableInsercaoMatEntrada() {
        if (materialEntrada != null && loginController.isIncluiEntradaMaterial() && isMatEntradaTemItens() && (newMovEntrada || isEnableEdicaoMatEntrada())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnableInsercaoMatSaida() {
        if (materialSaida != null && loginController.isIncluiSaidaMaterial() && isMatSaidaTemItens() && (newMovSaida || isEnableEdicaoMatSaida())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Preenche o FollouWpSolicitacoes current com os títulos gerados para
     * pagamento.
     */
    private void mountTitulosAPagar() {
        List<DocumentoEntradaItem> itensEntrada = getDocEntItens();
        List<TituloAPagar> titulosAPagar = new ArrayList<>();

        for (DocumentoEntradaItem item : itensEntrada) {
            titulosAPagar.addAll(item.getDocumentoEntrada().getTitulosAPagar());
        }
        itensEntrada.clear();
        itensEntrada = null;
        current.setTitulosAPagar(titulosAPagar);
    }
    /*
     **
     * Preenche o SolicitacaoCompraItem
     *
     * @transiente do FollowUpSolicitacoes de acordo com a SolicitacaoCompra e o
     * número do item da solicitação.
     *
     private void mountSolicitacaoCompraItem() {
     current.setSolicitacaoCompraItem(solicitacaoCompraItemFacade.find(current.getSolicitacaoCompra(), current.getSolicitacaoCompraItemNumero()));
     }
     */

    /**
     * Preenche o ColetaPrecoItem
     *
     * @transiente do FollowUpSolicitacoes de acordo com a ColetaPreco e o
     * número do item da coleta.
     *
     * private void mountColetaPrecoItem() {
     * current.setColetaPrecoItem(coletaPrecoItemFacade.find(current.getColetaPreco(),
     * current.getColetaPrecoItemNumero())); }
     */
    private void mountDocumentosEntrada() {
        //mountSolicitacaoCompraItem();
        List<DocumentoEntradaItem> itensEntrada = getDocEntItens();
        List<DocumentoEntrada> documentosEntrada = new ArrayList<>();
        for (DocumentoEntradaItem docItem : itensEntrada) {
            if (!documentosEntrada.contains(docItem.getDocumentoEntrada())) {
                documentosEntrada.add(docItem.getDocumentoEntrada());
            }
        }
        current.setDocumentosEntrada(documentosEntrada);
        itensEntrada.clear();
        itensEntrada = null;
    }

    private void mountKardex() {
        String dataSaldoInicial = DateUtils.getDataFormatada("yyyyMM", DateUtils.incrementar(new Date(), -1, Calendar.MONTH));
        String dataSaldoFinal = DateUtils.getDataFormatada("yyyyMM", new Date());
        current.setMateriaisEstoqueInicial(materiaisEstoqueFacade.findParam(current.getCentro(), current.getInsumoSub(), dataSaldoInicial));
        current.setMateriaisEstoqueFinal(materiaisEstoqueFacade.findParam(current.getCentro(), current.getInsumoSub(), dataSaldoFinal));
        current.setMaterialEntradaItens(materialEntradaItensFacade.findParam(current.getCentro().getEmpresaCod(), current.getCentro().getFilialCod(), current.getCentro().getCodigo(), current.getInsumoSub(), new Date()));
        current.setMaterialSaidaItens(materialSaidaItensFacade.findParam(current.getCentro().getEmpresaCod(), current.getCentro().getFilialCod(), current.getCentro().getCodigo(), current.getInsumoSub(), new Date()));
        current.setMaterialEntradasESaidas(followUpBusiness.mountEntradasESaidas(current.getMaterialEntradaItens(), current.getMaterialSaidaItens()));
        current.getMaterialEntradaItens().clear();
        current.getMaterialSaidaItens().clear();
        current.setMaterialEntradaItens(null);
        current.setMaterialSaidaItens(null);
    }

    public ListDataModel getMaterialEntradasESaidas() {
        if (current == null) {
            return null;
        }
        return new ListDataModel(current.getMaterialEntradasESaidas());
    }

    public ListDataModel getPedidoAutorizantes() {
        if (current == null) {
            return null;
        } else if (current.getPedido() == null) {
            return null;
        } else if (current.getPedido().getAutorizantes() == null) {
            return null;
        }

        return new ListDataModel(pedidoAutorizacoesFacade.findNotEmpty(current.getPedido()));
    }

    public String getDataSaldoInicial() {
        return DateUtils.getDataFormatada("dd/MM/yyyy", DateUtils.toFirstDate(new Date()));
    }

    public String getDataSaldoFinal() {
        return DateUtils.getDataFormatada("dd/MM/yyyy", DateUtils.toLastDate(new Date()));
    }

    public boolean getDisableButtonSolicitacao() {
        return disableButtonSolicitacao;
    }

    public void setDisableButtonSolicitacao(boolean disableButtonSolicitacao) {
        this.disableButtonSolicitacao = disableButtonSolicitacao;
    }

    public boolean getDisableButtonEntradaMateriais() {
        return disableButtonEntradaMateriais;
    }

    public void setDisableButtonEntradaMateriais(boolean disableButtonEntradaMateriais) {
        this.disableButtonEntradaMateriais = disableButtonEntradaMateriais;
    }

    public boolean getDisableButtonSaidaMateriais() {
        return disableButtonSaidaMateriais;
    }

    public void setDisableButtonSaidaMateriais(boolean disableButtonSaidaMateriais) {
        this.disableButtonSaidaMateriais = disableButtonSaidaMateriais;
    }

    public boolean isDisableButtonAutItem() {
        return disableButtonAutItem;
    }

    public void setDisableButtonAutItem(boolean disableButtonAutItem) {
        this.disableButtonAutItem = disableButtonAutItem;
    }

    public boolean isDisableButtonRejItem() {
        return disableButtonRejItem;
    }

    public void setDisableButtonRejItem(boolean disableButtonRejItem) {
        this.disableButtonRejItem = disableButtonRejItem;
    }

    /*
     * =================== REGISTRO SOLICITAÇÂO DE COMPRA ======================
     */
    public SolicitacaoCompra getSolicitacaoCompra() {
        return solicitacaoCompra;
    }

    public void setSolicitacaoCompra(SolicitacaoCompra solicitacaoCompra) {
        this.solicitacaoCompra = solicitacaoCompra;
    }

    public Date getDataEntregaT() {
        return dataEntregaT;
    }

    public void setDataEntregaT(Date dataEntregaT) {
        this.dataEntregaT = dataEntregaT;
    }

    public ListDataModel getItensSolicitacao() {
        if (solicitacaoCompra == null || solicitacaoCompra.getItens() == null) {
            return null;
        }
        return new ListDataModel(solicitacaoCompra.getItens());
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
    public void addOrRemoveInsumo(String subInsumoId, boolean marcado, SolicitacaoCompraItem solicItem) {
        if (insumosSelecionados == null) {
            insumosSelecionados = new ArrayList<>();
        }
        if (solicItemSelecionados == null) {
            solicItemSelecionados = new ArrayList<>();
        }
        if (marcado) {
            solicItemSelecionados.add(solicItem);
            insumosSelecionados.add(subInsumoId);
        } else {
            solicItemSelecionados.remove(solicItem);
            insumosSelecionados.remove(subInsumoId);
        }
        if (insumosSelecionados.isEmpty()) {
            changeDisableButtonAutorizacao(true);
        } else {
            changeDisableButtonAutorizacao(false);
        }
    }

    public void addOrRemoveInsumo(InsumoSub insumoSub) {
        if (insumosSelecionados == null) {
            insumosSelecionados = new ArrayList<>();
        }
        incluirItem(insumoSub.getInsumoCod() + "_" + insumoSub.getCodigo());
    }

    /**
     * Método novo: testando.
     */
    public void addOrRemoveTodos() {
        recreateTable();
        getPagination();

        if (insumosSelecionados == null) {
            insumosSelecionados = new ArrayList<>();
        }
        if (solicItemSelecionados == null) {
            solicItemSelecionados = new ArrayList<>();
        }

        for (FollowUpSolicitacoes item : getItems()) {
            if (item.isMarcado()) {
                solicItemSelecionados.add(item.getSolicitacaoCompraItem());
                insumosSelecionados.add(item.getInsumoSub().getId().toString());
            } else {
                solicItemSelecionados.remove(item.getSolicitacaoCompraItem());
                insumosSelecionados.remove(item.getInsumoSub().getId().toString());
            }
        }

        if (insumosSelecionados.isEmpty()) {
            changeDisableButtonAutorizacao(true);
        } else {
            changeDisableButtonAutorizacao(false);
        }
    }

    public List<SolicitacaoCompraItem> getSolicitacaoCompraItemList() {
        if (solicitacaoCompra != null && solicitacaoCompra.getItens() != null) {
            return solicitacaoCompra.getItens();
        } else {
            return new ArrayList<>();
        }
    }

    private void prepareIncItemSolic(String item) {
        if (newSolicitacao || solicitacaoCompra == null) {
            prepareCreateSolicitacao();
        }
        if (insumosSelecionados == null) {
            insumosSelecionados = new ArrayList();
        }
        this.insumosSelecionados.add(item);
        incluirItemSolicitacao();
    }

    public void prepareIncItemSolic(List<String> selecionados) {
        if (newSolicitacao || solicitacaoCompra == null) {
            prepareCreateSolicitacao();
        }
        if (insumosSelecionados == null) {
            insumosSelecionados = new ArrayList();
        }
        this.insumosSelecionados.addAll(selecionados);
        incluirItemSolicitacao();
    }

    private String incluirItemSolicitacao() {
        if (solicitacaoCompra.getItens() == null) {
            solicitacaoCompra.setItens(new ArrayList<SolicitacaoCompraItem>());
        }
        if (insumosSelecionados != null && !insumosSelecionados.isEmpty()) {
            for (String insumoSubId : insumosSelecionados) {
                InsumoSub insumoSub = insumoSubFacade.find(insumoSubId);
                if (insumoSub != null) {
                    Date dataEntrega = DateUtils.incrementar(new Date(), 2, Calendar.WEEK_OF_MONTH);
                    Integer itemNum = incrementItemSolic();
                    String itemItem = "000" + String.valueOf(itemNum);
                    itemItem = StringUtils.substring(itemItem, (itemItem.length() - 3));
                    solicitacaoCompra.getItens().add(new SolicitacaoCompraItem(solicitacaoCompra, itemNum, itemItem, insumoSub, isObraLinkadaOrcamento() ? 0.0 : 1.0, dataEntrega, EnumsGeral.AB, "", ""));
                    if (!newSolicitacao) {
                        decrementItemSolic();
                    }
                    insumoSub = null;
                    itemNum = null;
                    itemItem = null;
                }
            }
        }
        changeDisableButtonStatus(true);
        changeDisableButtonAutorizacao(true);
        insumosSelecionados = null;
        return JsfUtil.MANTEM;
    }

    private void prepareIncItemMatEnt(String item) {
        if (newMovEntrada || materialEntrada == null) {
            prepareCreateMaterialEntrada();
        }
        if (insumosSelecionados == null) {
            insumosSelecionados = new ArrayList();
        }
        this.insumosSelecionados.add(item);
        incluirItemMaterialEntrada();
    }

    public void prepareIncItemMatEnt(List<String> selecionados) {
        if (newMovEntrada || materialEntrada == null) {
            prepareCreateMaterialEntrada();
        }
        if (insumosSelecionados == null) {
            insumosSelecionados = new ArrayList();
        }
        this.insumosSelecionados.addAll(selecionados);
        incluirItemMaterialEntrada();
    }

    /**
     * Inclui um item na movimentação de material de entrada.
     *
     * @param item id do subInsumo.
     * @return I Página atual caso necessário.
     */
    private String incluirItemMaterialEntrada() {
        if (materialEntrada.getItens() == null) {
            materialEntrada.setItens(new ArrayList<MaterialEntradaItens>());
        }

        if (insumosSelecionados != null && !insumosSelecionados.isEmpty()) {
            for (String insumoSubCod : insumosSelecionados) {
                InsumoSub insumoSub = insumoSubFacade.find(insumoSubCod);
                if (insumoSub != null) {
                    if (insumoSub.getInsumo().getGeraEstoque()) {
                        Integer itemNum = incrementItemMatEntrada();
                        String itemItem = NumberUtils.preencheZeroEsquerda(String.valueOf(itemNum), 3);
                        Double valor = materialBusiness.getEntradaUltimoPreco(loginController.getCentroSelecionado(), insumoSub);
                        valor = valor == null || valor < 0.01 ? 0.01 : valor;
                        materialEntrada.getItens().add(new MaterialEntradaItens(materialEntrada, itemNum, itemItem, insumoSub, 1.0, valor, new Date(), loginController.getCentroSelecionado().getEmpresaCod(), loginController.getCentroSelecionado().getFilialCod(), loginController.getCentroSelecionado().getCodigo(), ""));
                        itemNum = null;
                        itemItem = null;
                    } else {
                        msgInsumoNaoGeraEstoque(insumoSub);
                    }
                    insumoSubCod = null;
                }
            }
        }
        changeDisableButtonStatus(true);
        changeDisableButtonAutorizacao(true);
        insumosSelecionados = null;
        return JsfUtil.MANTEM;
    }

    private void prepareIncItemMatSai(String item) {
        if (newMovSaida || materialSaida == null) {
            prepareCreateMaterialSaida();
        }
        if (insumosSelecionados == null) {
            insumosSelecionados = new ArrayList();
        }
        this.insumosSelecionados.add(item);
        incluirItemMaterialSaida();
    }

    public void prepareIncItemMatSai(List<String> selecionados) {
        if (newMovSaida || materialSaida == null) {
            prepareCreateMaterialSaida();
        }
        if (insumosSelecionados == null) {
            insumosSelecionados = new ArrayList();
        }
        this.insumosSelecionados.addAll(selecionados);
        incluirItemMaterialSaida();
    }

    private String incluirItemMaterialSaida() {
        if (materialSaida.getItens() == null) {
            materialSaida.setItens(new ArrayList<MaterialSaidaItens>());
        }

        if (insumosSelecionados != null && !insumosSelecionados.isEmpty()) {
            for (String insumoSubCod : insumosSelecionados) {
                InsumoSub insumoSub = insumoSubFacade.find(insumoSubCod);
                if (insumoSub != null) {
                    insumoSub.getInsumo().setNovoItem(true);
                    Integer itemNum = incrementItemMatSaida();
                    String itemItem = "000" + String.valueOf(itemNum);
                    itemItem = StringUtils.substring(itemItem, (itemItem.length() - 3));
                    Double estoqueAtual = followUpSolicitacoesFacade.findEstoqueByInsumo(loginController.getCentroSelecionado(), insumoSub);
                    materialSaida.getItens().add(new MaterialSaidaItens(materialSaida, itemNum, itemItem, insumoSub, 0.0, 1.0, new Date(), loginController.getCentroSelecionado().getEmpresaCod(), loginController.getCentroSelecionado().getFilialCod(), loginController.getCentroSelecionado().getCodigo(), "", estoqueAtual));
                    insumoSub = null;
                    itemNum = null;
                    itemItem = null;
                }
            }
        }
        changeDisableButtonStatus(true);
        changeDisableButtonAutorizacao(true);
        insumosSelecionados = null;
        return JsfUtil.MANTEM;
    }

    public void incluirItem(List<String> selecionados) {
        if (showMovimentacaoEntrada) {
            prepareIncItemMatEnt(selecionados);
        } else if (showMovimentacaoSaida) {
            prepareIncItemMatSai(selecionados);
        } else {
            prepareIncItemSolic(selecionados);
        }
    }

    public void incluirItem(String item) {
        if (showMovimentacaoEntrada) {
            prepareIncItemMatEnt(item);
        } else if (showMovimentacaoSaida) {
            prepareIncItemMatSai(item);
        } else {
            prepareIncItemSolic(item);
        }
    }

    public void removerItemSolic(SolicitacaoCompraItem item) {
        if (solicitacaoCompra != null && solicitacaoCompra.getItens() != null) {
            solicitacaoCompra.getItens().remove(item);
            if (!newSolicitacao) {
                solicitacaoCompra.setItemRemovido(true);
                if (itensSolicitacaoRemovidos == null) {
                    itensSolicitacaoRemovidos = new ArrayList<>();
                }
                itensSolicitacaoRemovidos.add(item);
            }
        }
        decrementItemSolic();
    }

    public void removerItemMatEntrada(MaterialEntradaItens item) {
        if (materialEntrada != null && materialEntrada.getItens() != null) {
            materialEntrada.getItens().remove(item);
            if (!newMovEntrada) {
                materialEntrada.setItemRemovido(true);
                if (itensEntradaRemovidos == null) {
                    itensEntradaRemovidos = new ArrayList<>();
                }
                itensEntradaRemovidos.add(item);
            }
        }
        decrementItemMatEntrada();
    }

    public void removerItemMatSaida(MaterialSaidaItens item) {
        if (materialSaida != null && materialSaida.getItens() != null) {
            materialSaida.getItens().remove(item);
            if (!newMovSaida) {
                materialSaida.setItemRemovido(true);
                if (itensSaidaRemovidos == null) {
                    itensSaidaRemovidos = new ArrayList<>();
                }
                itensSaidaRemovidos.add(item);
            }
        }
        decrementItemMatSaida();
    }

    private Integer incrementItemSolic() {
        if (solicitacaoCompra != null && solicitacaoCompra.getItens() != null) {
            Integer itemMaior = 1;
            for (SolicitacaoCompraItem item : solicitacaoCompra.getItens()) {
                while (item.getNumero() >= itemMaior) {
                    itemMaior++;
                }
            }
            return itemMaior;
        } else {
            return 1;
        }
    }

    private Integer incrementItemMatEntrada() {
        if (materialEntrada != null && materialEntrada.getItens() != null) {
            Integer itemMaior = 1;
            for (MaterialEntradaItens item : materialEntrada.getItens()) {
                while (item.getNumero() >= itemMaior) {
                    itemMaior++;
                }
            }
            return itemMaior;
        } else {
            return 1;
        }
    }

    private Integer incrementItemMatSaida() {
        if (materialSaida != null && materialSaida.getItens() != null) {
            Integer itemMaior = 1;
            for (MaterialSaidaItens item : materialSaida.getItens()) {
                while (item.getNumero() >= itemMaior) {
                    itemMaior++;
                }
            }
            return itemMaior;
        } else {
            return 1;
        }
    }

    private void decrementItemSolic() {
        if (solicitacaoCompra != null && solicitacaoCompra.getItens() != null) {
            Integer itemAtual = 1;
            for (SolicitacaoCompraItem item : solicitacaoCompra.getItens()) {
                if (item.getNumero() >= itemAtual) {
                    int index = solicitacaoCompra.getItens().indexOf(item);
                    if (newSolicitacao || item.getNumero() == null || item.getNumero() == 0) {
                        item.setNumero(itemAtual);
                    }
                    item.setItemItem(StringUtils.right("000" + itemAtual.toString(), 3));
                    solicitacaoCompra.getItens().set(index, item);
                    itemAtual++;
                }
            }
        }
    }

    private void decrementItemMatEntrada() {
        if (materialEntrada != null && materialEntrada.getItens() != null) {
            Integer itemAtual = 1;
            for (MaterialEntradaItens item : materialEntrada.getItens()) {
                if (item.getNumero() >= itemAtual) {
                    int index = materialEntrada.getItens().indexOf(item);
                    item.setNumero(itemAtual);
                    item.setItemItem(StringUtils.right("000" + itemAtual.toString(), 3));
                    materialEntrada.getItens().set(index, item);
                    itemAtual++;
                }
            }
        }
    }

    private void decrementItemMatSaida() {
        if (materialSaida != null && materialSaida.getItens() != null) {
            Integer itemAtual = 1;
            for (MaterialSaidaItens item : materialSaida.getItens()) {
                if (item.getNumero() >= itemAtual) {
                    int index = materialSaida.getItens().indexOf(item);
                    item.setNumero(itemAtual);
                    item.setItemItem(StringUtils.right("000" + itemAtual.toString(), 3));
                    materialSaida.getItens().set(index, item);
                    itemAtual++;
                }
            }
        }
    }

    public String getSolicitanteAtual() {
        if (solicitacaoCompra != null && solicitacaoCompra.getSolicitante() != null && solicitacaoCompra.getSolicitante().getNome() != null) {
            return solicitacaoCompra.getSolicitante().getNome();
        } else {
            return loginController.getFuncionario().getLogin();
        }
    }

    /**
     * Quando o usuário clica em incluir solicitação, esse método deve preparar
     * o ambiente para criar uma nova solicitação.
     *
     * @return java.lang.String Normalmente retorna contante MANTEM.
     */
    public String prepareCreateSolicitacao() {
        showSolicitacaoCompra = true;
        if (solicitacaoCompra == null) {
            solicitacaoCompra = new SolicitacaoCompra(0L);
        }
        solicitacaoCompra.rebuildFields(loginController.getCentroSelecionado(), solicitanteFacade.findByName(loginController.getFuncionario().getLogin()), new Date(), EnumsGeral.AB, NumberUtils.isNull(solicitacaoCompraFacade.findMaxCentroNumero(loginController.getCentroSelecionado()), 0) + 1, EnumsGeral.N, loginController.getFuncionario().getLogin(), new Date());
        this.newSolicitacao = true;
        return JsfUtil.MANTEM;
    }

    public String prepareCreateMaterialEntrada() {
        changeStatusOperacoes(false);
        showMovimentacaoEntrada = true;
        if (materialEntrada == null) {
            materialEntrada = new MaterialEntrada(0L);
            Date hoje = new Date();
            materialEntrada.rebuildFields(loginController.getCentroSelecionado(), hoje, EnumTipoMovimentoMaterialEntrada.C, "", "", String.valueOf(DateUtils.getDay(hoje)), EnumSistemaOrigemEstoque.SIMAT, loginController.getFuncionario().getLogin(), hoje);
        }
        this.newMovEntrada = true;
        return JsfUtil.MANTEM;
    }

    public String prepareCreateMaterialSaida() {
        changeStatusOperacoes(false);
        showMovimentacaoSaida = true;
        if (materialSaida == null) {
            materialSaida = new MaterialSaida(0L);
            Date hoje = new Date();
            materialSaida.rebuildFields(loginController.getCentroSelecionado(), hoje, EnumTipoMovimentoMaterialSaida.S.name(), "", "", String.valueOf(DateUtils.getDay(hoje)), loginController.getFuncionario().getLogin(), hoje);
        }
        this.newMovSaida = true;
        return JsfUtil.MANTEM;
    }
//4002 6803 2917 2281
//R$ 5.768,80

    /**
     * Quando o usuário cria em um item da solicitação, esse método deve prepara
     * o ambiente para editar a solicitação.
     *
     * @return java.lang.String Normalmente retorna contante MANTEM.
     */
    public void prepareEditSolicitacao(SolicitacaoCompra solicitacao) {
        solicitacaoCompra = solicitacao;
        this.newSolicitacao = false;
        changeDisableButtonStatus(true);
        changeDisableButtonAutorizacao(true);
        changeStatusOperacoes(false);
        showSolicitacaoCompra = true;
    }

    /**
     * Prepara o Material de Entrada atual para alteração.
     *
     * @param materialEntrada Material de Entrada a ser alterado.
     */
    public void prepareEditMaterialEntrada(MaterialEntrada materialEntrada) {
        this.materialEntrada = materialEntrada;
        this.materialEntrada.setUsuarioAlteracao(loginController.getFuncionario().getLogin());
        this.materialEntrada.setDataAlteracao(new Date());
        this.newMovEntrada = false;
        changeDisableButtonStatus(true);
        changeDisableButtonAutorizacao(true);
        changeStatusOperacoes(false);
        showMovimentacaoEntrada = true;
    }

    public void prepareEditMaterialSaida(MaterialSaida materialSaida) {
        this.materialSaida = materialSaida;
        this.newMovSaida = false;
        changeDisableButtonStatus(true);
        changeDisableButtonAutorizacao(true);
        changeStatusOperacoes(false);
        showMovimentacaoSaida = true;
    }

    /**
     * Foi definido que será alterado um Material de Entrada.
     *
     * @param numero Número do movimento de entrada.
     */
    private void prepareEditMaterialEntrada(Long numero) {
        prepareEditMaterialEntrada(materialEntradaFacade.find(numero));
    }

    private void prepareEditMaterialSaida(Long numero) {
        prepareEditMaterialSaida(materialSaidaFacade.find(numero));
    }

    /**
     * Define se o item a ser editado é Entrada ou Saída.
     *
     * @param classe Atributo que define Entrada (E) e Saída (S).
     * @param numero Número do movimento.
     */
    public String prepareEditEntradaOrSaida(String classe, Long numero) {
        switch (classe) {
            case "E":
                prepareEditMaterialEntrada(numero);
                break;
            case "S":
                prepareEditMaterialSaida(numero);
                break;
        }
        changeDialogStatus(false);
        return JsfUtil.MANTEM;
    }

    public void createOrEditSolicitacao() throws SQLException {
        if (newSolicitacao) {
            createSolicitacaoCompra();
        } else {
            updateSolicitacaoCompra();
        }
    }

    public void createOrEditMovimentacaoEntrada() {
        if (materialEntrada != null && StringUtils.isBlank(materialEntrada.getNumeroDocumento())) {
            msgMaterialEntradaSemDocNumero();
        } else if (newMovEntrada) {
            createMaterialEntrada();
        } else {
            updateMaterialEntrada();
            limparMovimentacaoMaterial();
        }
    }

    public void createOrEditMovimentacaoSaida() {
        if (materialSaida != null && StringUtils.isBlank(materialSaida.getNumeroDocumento())) {
            msgMaterialSaidaSemDocNumero();
        } else if (newMovSaida) {
            createMaterialSaida();
        } else {
            updateMaterialSaida();
            limparMovimentacaoMaterial();
        }
    }

    /**
     * Verifica se a solicitação de compra possui itens.
     *
     * @return
     */
    public boolean isSolicitacaoTemItens() {
        if (solicitacaoCompra == null || solicitacaoCompra.getItens() == null || solicitacaoCompra.getItens().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Verifica se a entrada de material possui itens.
     *
     * @return
     */
    public boolean isMatEntradaTemItens() {
        if (materialEntrada == null || materialEntrada.getItens() == null || materialEntrada.getItens().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isMatSaidaTemItens() {
        if (materialSaida == null || materialSaida.getItens() == null || materialSaida.getItens().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Conclui a solicitação de compra atual.
     */
    private void concluiSolicitacao() {
        if (isEnableConclusaoSolicitacao()) {
            solicitacaoCompra.setConcluido(EnumsGeral.S);
            solicitacaoCompra.setUsuarioConcluido(loginController.getLogin());
            solicitacaoCompra.setDataConcluido(new Date());
        } else {
            msgBusinessException(null);
        }
    }

    /**
     * Desconclui a solicitação de compra atual.
     */
    private void desconcluiSolicitacao() {
        if (isEnableConclusaoSolicitacao()) {
            solicitacaoCompra.setConcluido(EnumsGeral.N);
            solicitacaoCompra.setUsuarioConcluido(null);
            solicitacaoCompra.setDataConcluido(null);
        } else {
            msgBusinessException(null);
        }
    }

    /**
     * Define os métodos de conclusão ao executar o botão (Concluir/Desconcluir)
     * da solicitação de compra. Se a solicitação já estiver concluida, irá
     * desconcluí-la. Caso contrário, irá concluir a solicitação.
     */
    public void manageConclusaoSolicitacao() throws SQLException {
        if (solicitacaoCompra.getConcluido() == EnumsGeral.S) {
            desconcluiSolicitacao();
            updateSolicitacaoCompra();
        } else if (solicitacaoCompra.getConcluido() == EnumsGeral.N) {
            concluiSolicitacao();
            if (newSolicitacao) {
                createSolicitacaoCompra();
            } else {
                updateSolicitacaoCompra();
            }
            limparSolicitacaoCompra();
        }
        clean();
    }

    /**
     * Utiliado para definir o limite da quantidade a ser baixada.
     *
     * @param insumo Insumo a ser verificada a quantidade de estoque no Centro
     * de Custo.
     * @return Quantidade em estoque.
     */
    public Double getEstoqueAtualPositivo(InsumoSub insumoSub) {
        Double estoque = getEstoqueAtual(insumoSub);
        return estoque < 0 ? 0.0 : estoque;
    }

    private Double calculaLimiteMovSaida(MaterialSaidaItens item) {
        try {
            if (item.getInsumoSub().getInsumo().isNovoItem()) {
                return getEstoqueAtualPositivo(item.getInsumoSub());
            }
            return getEstoqueAtualPositivo(item.getInsumoSub()) + item.getQuantidade();
        } catch (NullPointerException | NoResultException e) {
            return 0.0;
        }
    }

    public Double getLimiteMovSaida(MaterialSaidaItens item) {
        if (materialSaida.getTipoMovimento().equals(EnumTipoMovimentoMaterialSaida.D.toString())) {
            item.setLimiteSaida(getEstoqueSaidasSemDevolucoes(item.getInsumoSub()));
        } else {
            item.setLimiteSaida(calculaLimiteMovSaida(item));
        }
        return item.getLimiteSaida();
    }

    public void limpaLimiteMovSaida(MaterialSaidaItens item) {
        item.setLimiteSaida(null);
    }

    public Double getEstoqueAtual(InsumoSub insumoSub) {
        return NumberUtils.arredondarHalfUp(NumberUtils.isNull(materiaisEstoqueFacade.findSaldo(loginController.getCentroSelecionado(), insumoSub, DateUtils.getYearMonth(new Date())), 0.0), 4);
    }

    public String getEstoqueAtualFmt(InsumoSub insumoSub) {
        try {
            return NumberUtils.formatDecimal(getEstoqueAtual(insumoSub), 4);
        } catch (NullPointerException | NumberFormatException | NoResultException e) {
            return "0,0000";
        }
    }

    public String getEstoqueAtualFmt1(FollowUpSolicitacoes followup) {
        try {
            return NumberUtils.formatDecimal(followup.getMaterialEstoque().getEstoqueQuantidade(), 4);
        } catch (NullPointerException | NumberFormatException | NoResultException e) {
            return "0,0000";
        }
    }

    public Double getEstoqueUsina(InsumoSub insumoSub) {
        Double estoque = followUpSolicitacoesFacade.findEstoqueUsinaByInsumoSub(loginController.getCentroSelecionado(), insumoSub);
        return estoque == null ? 0.0 : estoque;
    }

    public Double getEstoqueSaidas(Insumo insumo) {
        return NumberUtils.isNull(materialSaidaItensFacade.findEstoqueSaidas(insumo.getCodigo(), loginController.getCentroSelecionado()), 0.0);
    }

    public Double getEstoqueSaidasSemDevolucoes(InsumoSub insumoSub) {
        return NumberUtils.isNull(materialSaidaItensFacade.findEstoqueSaidasSemDevolucoes(insumoSub, loginController.getCentroSelecionado()), 0.0);
    }

    public Double getEstoqueEntradas(Insumo insumo) {
        return NumberUtils.isNull(materialEntradaItensFacade.findEstoqueEntradas(insumo.getCodigo(), loginController.getCentroSelecionado()), 0.0);
    }

    private boolean isItemConcluido(SolicitacaoCompraItem solicItem) {
        return (solicItem != null && solicItem.getSolicitacao() != null && solicItem.getSolicitacao().getConcluido() != null && solicItem.getSolicitacao().getConcluido() == EnumsGeral.S);
    }

    private boolean isItemAutorizadoOuRejeitado(SolicitacaoCompraItem solicItem) {
        return (solicItem != null && solicItem.getSolicitacao() != null && solicItem.getSolicitacao().getConcluido() != null && solicItem.getSolicitacao().getConcluido() == EnumsGeral.S
                && solicItem.getInstrucao() != null && (solicItem.getInstrucao().equals("A") || solicItem.getInstrucao().equals("R")));
    }

    /**
     * Executa a rotina de autorização ou rejeição das solicitações de compra
     * selecionadas, de acordo com o parâmetro passado.
     *
     * @param aut EnumAutorizacao que pode ser "A" para autorizar ou "R" para
     * rejeitar.
     * @return Página que irá suceder a operação.
     * @throws EntityException
     * @throws SQLException
     * @throws NullPointerException
     */
    private void autorizaOuRejeitaSolicitacao(EnumAutorizacao aut) throws EntityException, SQLException, NullPointerException {
        boolean statusAut = false;
        boolean statusNaoConc = false;
        boolean statusJaAut = false;
        for (SolicitacaoCompraItem solicItem : solicItemSelecionados) {
            if (solicItem == null || solicItem.getSolicitacao() == null) {
                msgItemNaoPossuiSolicitacao();
            } else if (isItemConcluido(solicItem)) {
                statusNaoConc = false;
                if (!isItemAutorizadoOuRejeitado(solicItem)) {
                    solicItem.setInstrucaoData(new Date());
                    solicItem.setInstrucao(aut.name());
                    solicItem.setUsuarioInstrucao(loginController.getLogin());
                    solicItem.setInstrucaoMotivo(motivoRejeitaSolicitacao);
                    solicitacaoCompraItemFacade.update(solicItem);
                    followUpBusiness.atualizaFollowUp(loginController.getCentroSelecionado(), solicItem.getSolicitacao());
                    marcado = false;
                    statusAut = true;
                } else {
                    statusJaAut = true;
                    //SOLICITAÇÃO NÃO ESTÁ NO STATUS AS(AGUARDANDO AUTORIZAÇÃO DA SOLICITAÇÃO)
                }
            } else {
                statusNaoConc = true;
                //ALGUNS ITENS NÃO FORAM CONCLUÍDOS
            }
        }
        if (statusNaoConc) {
            msgSolicitacaoItensNaoConcluidos();
        }
        if (statusJaAut) {
            msgSolicitacaoItensJaAutorizados();
        }
        if (statusAut) {
            if (aut == EnumAutorizacao.A) {
                msgSolicitacaoItensAutorizados();
            } else {
                msgSolicitacaoItensRejeitados();
            }
        }
        limpaOperacoesFollowUp();
    }

    /**
     * Autoriza os itens do followup selecionados. Os que já foram
     * autorizados/rejeitados ou não concluidos não sofrem alteração.
     */
    public String autorizaSolicitacao() {
        try {
            autorizaOuRejeitaSolicitacao(EnumAutorizacao.A);
        } catch (EntityException | SQLException | NullPointerException ex) {
            msgErroAutorizaSolicitacao(ex);
        }
        return JsfUtil.MANTEM;
    }

    /**
     * Rejeita os itens do followup selecionados. Os que já foram
     * autorizados/rejeitados ou não concluidos não sofrem alteração.
     */
    public String rejeitaSolicitacao() {
        try {
            if (StringUtils.isBlank(motivoRejeitaSolicitacao)) {
                msgInformeMotivoRejeitaSolicitacao();
            } else {
                autorizaOuRejeitaSolicitacao(EnumAutorizacao.R);
            }
        } catch (EntityException | SQLException | NullPointerException ex) {
            msgErroRejeitaSolicitacao(ex);
        }
        return JsfUtil.MANTEM;
    }

    public String desfazAutorizacao() {
        boolean statusAut = false;
        boolean statusErro = false;
        String erro = "";
        for (SolicitacaoCompraItem solicItem : solicItemSelecionados) {
            try {
                if (!StringUtils.isBlank(solicItem.getInstrucao())) {
                    if (isItemConcluido(solicItem) && isItemAutorizadoOuRejeitado(solicItem) && solicItem.getSituacao() == EnumsGeral.AB) {
                        solicItem.setInstrucaoData(new Date());
                        solicItem.setInstrucao("");
                        solicItem.setUsuarioInstrucao(loginController.getLogin());
                        solicItem.setInstrucaoMotivo(null);
                        solicitacaoCompraItemFacade.update(solicItem);
                        statusAut = true;
                        followUpBusiness.atualizaFollowUp(loginController.getCentroSelecionado(), solicItem.getSolicitacao());
                        motivoRejeitaSolicitacao = "";
                        marcado = false;
                    }
                }
            } catch (NullPointerException | EntityException | SQLException ex) {
                statusErro = true;
                erro = ex.getMessage();
            }
        }
        if (statusAut) {
            msgSolicitacaoItensDesautorizados();
        }
        if (statusErro) {
            msgErroDesautorizaSolicitacao(erro);
        }
        limpaOperacoesFollowUp();
        return JsfUtil.MANTEM;
    }

    public List<SolicitacaoCompraItem> atualizaItensPlanoOrcamento(EnumOpeEvtOrcamento ope, List<SolicitacaoCompraItem> itensSolic) throws SQLException {
        if (projPlanFacade.isCentroLinkOrcamento(loginController.getCentroSelecionado())) {
            for (SolicitacaoCompraItem solicItem : itensSolic) {
                int index = itensSolic.indexOf(solicItem);
                solicItem.setItensPlanoOrcamento(orcamentoBusiness.insertItemOrcPlan(solicItem, ope));
                itensSolic.set(index, solicItem);
            }
        }
        return itensSolic;
    }

    public void removeItensPlanoOrcamento(List<SolicitacaoCompraItem> itensSolic) {
        if (projPlanFacade.isCentroLinkOrcamento(loginController.getCentroSelecionado())) {
            try {
                orcamentoBusiness.removeItemOrcPlan(itensSolic);
            } catch (SQLException ex) {
                Logger.getLogger(FollowUpSolicitacoesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /* public void atualizaSaldoARealizarSolicitacao(SolicitacaoCompra solic) throws SQLException {
     for (SolicitacaoCompraItem solicItem : solic.getItens()) {
     followUpBusiness.atualizaOrcamentoFollowUp(loginController.getCentroSelecionado(), solicItem.getInsumoSub().getInsumo());
     }
     }*/
    private void atualizaSequenciaisSolicitacaoCompra(Integer seqNumero, Integer seqId) {
        sequenciaisFacade.update(sequenciaisFacade.getSequencialSolicitacaoCompra().initNumber(seqNumero));
        sequenciaisRangeFacade.update(sequenciaisRangeFacade.getSequencialSolicitacaoCompra(loginController.getCentroSelecionado()).initNumber(seqId));
    }

    public void atualizaDataEntregaTodos() {
        if (dataEntregaT != null) {
            for (SolicitacaoCompraItem item : solicitacaoCompra.getItens()) {
                item.setDataEntrega(dataEntregaT);
            }
        }
    }

    /**
     * Cria nova solicitação de compra no banco de dados.
     *
     * @return Página atual.
     */
    public String createSolicitacaoCompra() {
        boolean itensOk = true;
        for (SolicitacaoCompraItem solicItem : solicitacaoCompra.getItens()) {
            if (solicItem.getQuantidade() == 0 || (isObraLinkadaOrcamento() && (solicItem.getItensPlanoOrcamento() == null || solicItem.getItensPlanoOrcamento().isEmpty()))) {
                itensOk = false;
            }
        }
        if (itensOk && solicitacaoCompra.getSolicitante() != null) {
            changeDisableButtonStatus(false);
            if (solicitacaoCompra.getItens() == null || solicitacaoCompra.getItens().isEmpty()) {
                msgSolicitacaoCompraSemItem();
            } else {
                try {
                    solicitacaoCompra.setIdSistema(solicitacaoCompraFacade.findMaxCentroNumero(loginController.getCentroSelecionado()) + 1);
                    //Executa se o centro estiver com orçamento ativo
                    if (isObraLinkadaOrcamento()) {
                        solicitacaoCompra.setItens(atualizaItensPlanoOrcamento(EnumOpeEvtOrcamento.I, solicitacaoCompra.getItens()));
                    }
                    Integer seqNumero = 0;
                    //Se ocorrer um erro ao persistir a solicitação, se for linkada serão apagados os eventos que deduziram do saldo a solcitar.
                    try {
                        seqNumero = solicitacaoCompraFacade.createSolicitacaoCompra(solicitacaoCompra);
                    } catch (NullPointerException | NoResultException | SQLServerException | RollbackException | EJBException e) {
                        if (isObraLinkadaOrcamento()) {
                            removeItensPlanoOrcamento(solicitacaoCompra.getItens());
                        }
                        msgErroInserirSolicitacao(e);
                    }
                    if (seqNumero > 0) {
                        atualizaSequenciaisSolicitacaoCompra(seqNumero, solicitacaoCompra.getIdSistema());
                        logFollowupFacade.createLogSolic(solicitacaoCompra);
                        logFollowupFacade.updateLogFollowup();
                        //atualizaSaldoARealizarSolicitacao(solicitacaoCompra);
                        msgSolicitacaoCompraCriada();
                    }
                } catch (BusinessException ex) {
                    newSolicitacao = true;
                    if (solicitacaoCompra.getItens().isEmpty()) {
                        msgSolicitacaoCompraSemItem();
                    } else {
                        msgBusinessException(ex);
                    }
                } catch (SQLException | NullPointerException | NoResultException e) {
                    msgErroInserirSolicitacao(e);
                } finally {
                    limparSolicitacaoCompra();
                    showSolicitacaoCompra = false;
                }
            }
        } else {
            if (solicitacaoCompra.getSolicitante() == null) {
                msgUsuarioSemAutorizacao();
            } else {
                msgItensSemQuantidadeInformada();
            }
        }
        return JsfUtil.MANTEM;
    }

    /**
     * Atualização a solicitação de compra no banco de dados.
     *
     * @return Página atual.
     */
    public String updateSolicitacaoCompra() {
        boolean itensOk = true;
        for (SolicitacaoCompraItem solicItem : solicitacaoCompra.getItens()) {
            if (solicItem.getQuantidade() == 0 || (isObraLinkadaOrcamento() && (solicItem.getItensPlanoOrcamento() == null || solicItem.getItensPlanoOrcamento().isEmpty()))) {
                itensOk = false;
            }
        }
        if (itensOk) {
            try {
                if (solicitacaoCompra.isItemRemovido()) {
                    orcamentoBusiness.removeItemOrcPlan(itensSolicitacaoRemovidos);
                    itensSolicitacaoRemovidos = null;
                }
                orcamentoBusiness.updateItemOrcPlan(solicitacaoCompra.getItens());
                solicitacaoCompraFacade.update(solicitacaoCompra);
                followUpBusiness.atualizaFollowUp(loginController.getCentroSelecionado(), solicitacaoCompra);
                //atualizaSaldoARealizarSolicitacao(solicitacaoCompra);
                msgSolicitacaoCompraAtualizada();
                updateFollowup();
                recreateTable();

            } catch (EntityException | SQLException ex) {
                limparSolicitacaoCompra();
                throw new RuntimeException(ex);
            } finally {
                limparSolicitacaoCompra();
            }
        } else {
            msgItensSemQuantidadeInformada();
        }
        return JsfUtil.MANTEM;
    }

    /**
     * Remove a solicitação de compra no banco de dados.
     *
     * @return Página atual.
     */
    public void removeSolicitacaoCompra() {
        if (solicitacaoCompra.getConcluido() != EnumsGeral.S) {
            try {
                if (solicitacaoCompra.isItemRemovido()) {
                    orcamentoBusiness.removeItemOrcPlan(itensSolicitacaoRemovidos);
                }
                solicitacaoCompraFacade.remove(solicitacaoCompra);
                orcamentoBusiness.removeItemOrcPlan(solicitacaoCompra.getItens());
                followUpBusiness.atualizaFollowUp(loginController.getCentroSelecionado(), solicitacaoCompra);
                //atualizaSaldoARealizarSolicitacao(solicitacaoCompra);
                msgSolicitacaoCompraRemovida();
            } catch (SQLException | EntityException ex) {
                msgErroRemoverSolicitacao(ex);
                throw new RuntimeException(ex);
            } finally {
                limparSolicitacaoCompra();
            }
        } else {
            msgSolicitacaoNaoPodeSerRemovida();
        }
    }

    public void removeMaterialEntrada() {
        if (isEnableRemocaoMatEntrada()) {
            try {
                materialEntradaFacade.remove(materialEntrada);
                estoqueBusiness.atualizaEstoqueItensEntrada(materialEntrada.getItens(), loginController.getCentroSelecionado(), materialEntrada);
                msgMovimentacaoEntradaRemovida();
            } catch (EntityException ex) {
                msgErroRemoverMovimentacaoEntrada(ex);
                throw new RuntimeException(ex);
            } finally {
                limparMovimentacaoMaterial();
            }
        } else {
            msgMaterialOrigemNaoSimat();
        }
    }

    public void removeMaterialSaida() {
        if (isEnableRemocaoMatSaida()) {
            try {
                materialSaidaFacade.remove(materialSaida);
                estoqueBusiness.atualizaEstoqueItensSaida(materialSaida.getItens(), loginController.getCentroSelecionado(), materialSaida);
                msgMovimentacaoSaidaRemovida();
            } catch (EntityException ex) {
                msgErroRemoverMovimentacaoSaida(ex);
                throw new RuntimeException(ex);
            } finally {
                limparMovimentacaoMaterial();
                recreateTable();
            }
        } else {
            msgErroRemoverMovimentacaoSaida(null);
        }
    }

    /**
     * Define o item da solicitação de compra com a quantidade orçada
     * selecionada.
     *
     * @param item
     */
    public void substituteSolicItemOrcado(SolicitacaoCompraItem item, boolean solicitacao) {
        if (item != null && solicitacao) {
            int pos = solicitacaoCompra.getItens().indexOf(item);
            solicitacaoCompra.getItens().set(pos, item);
        }
    }

    /*
     * ============= REGISTRO ENTRADA E SAIDA DE MATERIAL ========================
     */
    public EnumTipoMovimentoMaterialEntrada getTipoMovimentoMaterial() {
        return tipoMovimentoMaterial;
    }

    public void setTipoMovimentoMaterial(EnumTipoMovimentoMaterialEntrada tipoMovimentoMaterial) {
        this.tipoMovimentoMaterial = tipoMovimentoMaterial;
    }

    public EnumTipoMovimentoMaterialSaida getTipoMovimentoMaterialSaida() {
        return tipoMovimentoMaterialSaida;
    }

    public void setTipoMovimentoMaterialSaida(EnumTipoMovimentoMaterialSaida tipoMovimentoMaterialSaida) {
        this.tipoMovimentoMaterialSaida = tipoMovimentoMaterialSaida;
    }

    public EnumTipoDocumentoMaterialSaida getTipoDocumentoMaterialSaida() {
        return tipoDocumentoMaterialSaida;
    }

    public void setTipoDocumentoMaterialSaida(EnumTipoDocumentoMaterialSaida tipoDocumentoMaterialSaida) {
        this.tipoDocumentoMaterialSaida = tipoDocumentoMaterialSaida;
    }

    public EnumTipoDocumentoMaterialEntrada getTipoDocumentoMaterialEntrada() {
        return tipoDocumentoMaterialEntrada;
    }

    public void setTipoDocumentoMaterialEntrada(EnumTipoDocumentoMaterialEntrada tipoDocumentoMaterialEntrada) {
        this.tipoDocumentoMaterialEntrada = tipoDocumentoMaterialEntrada;
    }

    public SelectItem[] getTipoDocumentoMaterialSaidaSelect() {
        return JsfUtil.getEnumSelectItems(EnumTipoDocumentoMaterialSaida.class, false, FacesContext.getCurrentInstance());
    }

    public SelectItem[] getTipoDocumentoMaterialEntradaSelect() {
        return JsfUtil.getEnumSelectItems(EnumTipoDocumentoMaterialEntrada.class, false, FacesContext.getCurrentInstance());
    }

    public SelectItem[] getTipoMovimentoMaterialEntradaSelect() {
        return JsfUtil.getEnumSelectItems(EnumTipoMovimentoMaterialEntrada.class, false, FacesContext.getCurrentInstance());
    }

    public SelectItem[] getTipoMovimentoMaterialSaidaSelect() {
        return JsfUtil.getEnumSelectItems(EnumTipoMovimentoMaterialSaida.class, false, FacesContext.getCurrentInstance());
    }

    public Map<String, String> getTipoDocumentoMaterialSelect() {
        return tipoDocumentoMaterialSelect;
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

    public List<MaterialItemSelecionado> getItensMaterialList() {
        return itensMaterialList;
    }

    public void setItensMaterialList(List<MaterialItemSelecionado> itensMaterialList) {
        this.itensMaterialList = itensMaterialList;
    }

    public List<String> getInsumosSelecionados() {
        return insumosSelecionados;
    }

    public void setInsumosSelecionados(List<String> insumosSelecionados) {
        this.insumosSelecionados = insumosSelecionados;
    }

    public MaterialEntrada getMaterialEntrada() {
        return materialEntrada;
    }

    public void setMaterialEntrada(MaterialEntrada materialEntrada) {
        this.materialEntrada = materialEntrada;
    }

    public MaterialSaida getMaterialSaida() {
        return materialSaida;
    }

    public void setMaterialSaida(MaterialSaida materialSaida) {
        this.materialSaida = materialSaida;
    }

    public ListDataModel getItensMaterial() {
        if (itensMaterialList == null) {
            return null;
        }
        ListDataModel lista;
        lista = new ListDataModel(itensMaterialList);
        return lista;
    }

    public void createMaterialEntrada() {
        changeDisableButtonStatus(false);
        if (materialEntrada.getItens() == null || materialEntrada.getItens().isEmpty()) {
            msgMaterialSemItem();
        } else {
            try {
                Integer sequencial = materialEntradaFacade.createMaterialEntrada(materialEntrada);
                sequenciaisFacade.update(sequenciaisFacade.getSequencialMaterialEntrada().initNumber(sequencial));
                logFollowupFacade.createLogMatEnt(materialEntrada, loginController.getFuncionario().getLogin(), null, null);
                logFollowupFacade.updateLogFollowup();

                for (MaterialEntradaItens item : materialEntrada.getItens()) {
                    estoqueBusiness.atualizaSaldoMaterial(loginController.getCentroSelecionado(), materialEntrada.getDataEntrada(), item.getInsumoSub());
                }
                msgMaterialEntradaCriado();
            } catch (BusinessException ex) {
                if (materialEntrada.getItens().isEmpty()) {
                    msgMaterialEntradaSemItem(ex);
                } else {
                    msgBusinessException(ex);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        updateFollowup();
        limparMovimentacaoMaterial();
    }

    public void createMaterialSaida() {
        changeDisableButtonStatus(false);
        if (materialSaida.getItens() == null || materialSaida.getItens().isEmpty()) {
            msgMaterialSemItem();
        } else {
            try {
                Integer sequencial = materialSaidaFacade.createMaterialSaida(materialSaida);
                sequenciaisFacade.update(sequenciaisFacade.getSequencialMaterialSaida().initNumber(sequencial));
                for (MaterialSaidaItens item : materialSaida.getItens()) {
                    estoqueBusiness.atualizaSaldoMaterial(loginController.getCentroSelecionado(), materialSaida.getDataSaida(), item.getInsumoSub());
                }
                logFollowupFacade.createLogMatSai(materialSaida, loginController.getFuncionario().getLogin(), null, null);
                logFollowupFacade.updateLogFollowup();
                msgMaterialSaidaCriado();
            } catch (BusinessException ex) {
                if (materialSaida.getItens().isEmpty()) {
                    msgMaterialSaidaSemItem(ex);
                } else {
                    msgBusinessException(ex);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        updateFollowup();
        limparMovimentacaoMaterial();
    }

    public String updateMaterialEntrada() {
        if (materialEntrada.getEntradaOrigem() == EnumSistemaOrigemEstoque.SIMAT) {
            try {
                if (materialEntrada.isItemRemovido()) {
                    MaterialEntrada mat = new MaterialEntrada(materialEntrada);
                    mat.setItens(null);
                    materialEntradaFacade.update(mat);
                    materialEntradaItensFacade.createItens(materialEntrada.getItens(), loginController.getCentroSelecionado(), materialEntrada);
                    estoqueBusiness.atualizaEstoqueItensEntrada(itensEntradaRemovidos, loginController.getCentroSelecionado(), materialEntrada);
                    itensEntradaRemovidos = null;
                    mat = null;
                } else {
                    materialEntradaFacade.update(materialEntrada);
                }
                estoqueBusiness.atualizaEstoqueItensEntrada(materialEntrada.getItens(), loginController.getCentroSelecionado(), materialEntrada);
                msgMovimentacaoEntradaAtualizada();
            } catch (EntityException ex) {
                updateFollowup();
                limparMovimentacaoMaterial();
                throw new RuntimeException(ex);
            }
        } else {
            msgMaterialOrigemNaoSimat();
        }
        updateFollowup();
        limparMovimentacaoMaterial();
        return JsfUtil.MANTEM;
    }

    public String updateMaterialSaida() {
        try {
            if (materialSaida.isItemRemovido()) {
                MaterialSaida mat = new MaterialSaida(materialSaida);
                mat.setItens(null);
                mat.setUsuarioAlteracao(loginController.getFuncionario().getLogin());
                mat.setDataAlteracao(new Date());
                materialSaidaFacade.update(mat);
                materialSaidaItensFacade.createItens(materialSaida.getItens(), loginController.getCentroSelecionado(), materialSaida);
                estoqueBusiness.atualizaEstoqueItensSaida(itensSaidaRemovidos, loginController.getCentroSelecionado(), materialSaida);
                itensSaidaRemovidos = null;
                mat = null;
            } else {
                materialSaidaFacade.update(materialSaida);
            }
            estoqueBusiness.atualizaEstoqueItensSaida(materialSaida.getItens(), loginController.getCentroSelecionado(), materialSaida);
            msgMovimentacaoSaidaAtualizada();
        } catch (EntityException ex) {
            throw new RuntimeException(ex);
        } finally {
            updateFollowup();
            limparMovimentacaoMaterial();
        }
        return JsfUtil.MANTEM;
    }

    public void openMudaCentroCusto() {
        changeDialogStatus(false);
        showDialogCentroCusto = true;
    }

    public FollowUpSolicitacoes getCurrent() {
        return current;
    }

    public void setCurrent(FollowUpSolicitacoes current) {
        this.current = current;
    }

    public int getRegistrosPorPagina() {
        return registrosPorPagina;
    }

    public void setRegistrosPorPagina(int registrosPorPagina) {
        this.registrosPorPagina = registrosPorPagina;
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

    public String getMotivoRejeitaSolicitacao() {
        return motivoRejeitaSolicitacao;
    }

    public void setMotivoRejeitaSolicitacao(String motivoRejeitaSolicitacao) {
        this.motivoRejeitaSolicitacao = motivoRejeitaSolicitacao;
    }

    public EnumSituacaoSolicitacao getSituacaoFiltro() {
        return situacaoFiltro;
    }

    public void setSituacaoFiltro(EnumSituacaoSolicitacao situacaoFiltro) {
        this.situacaoFiltro = situacaoFiltro;
    }

    public Solicitante getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Solicitante solicitante) {
        this.solicitante = solicitante;
    }

    public Integer getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(Integer numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public Integer getNumeroAR() {
        return numeroAR;
    }

    public void setNumeroAR(Integer numeroAR) {
        this.numeroAR = numeroAR;
    }

    public String getCodigoCredor() {
        return codigoCredor;
    }

    public void setCodigoCredor(String codigoCredor) {
        this.codigoCredor = codigoCredor;
    }

    public String getRazaoSocialCredor() {
        return razaoSocialCredor;
    }

    public void setRazaoSocialCredor(String razaoSocialCredor) {
        this.razaoSocialCredor = razaoSocialCredor;
    }

    public String getNomeFantasiaCredor() {
        return nomeFantasiaCredor;
    }

    public void setNomeFantasiaCredor(String nomeFantasiaCredor) {
        this.nomeFantasiaCredor = nomeFantasiaCredor;
    }

    public String getCpfCnpjCredor() {
        return cpfCnpjCredor;
    }

    public void setCpfCnpjCredor(String cpfCnpjCredor) {
        this.cpfCnpjCredor = cpfCnpjCredor;
    }

    //--------------------------------------------
    //GETTERS E SETTERS DOS FILTROS DOS CHECKBOXES
    //--------------------------------------------
    public Boolean getFiltroRS() {
        return filtroRS;
    }

    public void setFiltroRS(Boolean filtroRS) {
        this.filtroRS = filtroRS;
    }

    public Boolean getFiltroCS() {
        return filtroCS;
    }

    public void setFiltroCS(Boolean filtroCS) {
        this.filtroCS = filtroCS;
    }

    public Boolean getFiltroAS() {
        return filtroAS;
    }

    public void setFiltroAS(Boolean filtroAS) {
        this.filtroAS = filtroAS;
    }

    public Boolean getFiltroAC() {
        return filtroAC;
    }

    public void setFiltroAC(Boolean filtroAC) {
        this.filtroAC = filtroAC;
    }

    public Boolean getFiltroRP() {
        return filtroRP;
    }

    public void setFiltroRP(Boolean filtroRP) {
        this.filtroRP = filtroRP;
    }

    public Boolean getFiltroAP() {
        return filtroAP;
    }

    public void setFiltroAP(Boolean filtroAP) {
        this.filtroAP = filtroAP;
    }

    public Boolean getFiltroRA() {
        return filtroRA;
    }

    public Boolean getFiltroENF() {
        return filtroENF;
    }

    public void setFiltroENF(Boolean filtroENF) {
        this.filtroENF = filtroENF;
    }

    public Boolean getFiltroEMO() {
        return filtroEMO;
    }

    public void setFiltroEMO(Boolean filtroEMO) {
        this.filtroEMO = filtroEMO;
    }

    public void setFiltroRA(Boolean filtroRA) {
        this.filtroRA = filtroRA;
    }

    public Boolean getFiltroAA() {
        return filtroAA;
    }

    public void setFiltroAA(Boolean filtroAA) {
        this.filtroAA = filtroAA;
    }

    public Boolean getFiltroPG() {
        return filtroPG;
    }

    public void setFiltroPG(Boolean filtroPG) {
        this.filtroPG = filtroPG;
    }

    public Boolean getFiltroOK() {
        return filtroOK;
    }

    public void setFiltroOK(Boolean filtroOK) {
        this.filtroOK = filtroOK;
    }

    public Boolean getFiltroR() {
        return filtroR;
    }

    public void setFiltroR(Boolean filtroR) {
        this.filtroR = filtroR;
    }

    public boolean isMarcado() {
        return marcado;
    }

    public void setMarcado(boolean marcado) {
        this.marcado = marcado;
    }

    //------------------------------------------
    // NOME DOS DIALOGS DO FOLLOWUP
    //------------------------------------------
    /*Registro Solicitação Dialog*/
    public String getDialogRS() {
        return "dlgRS";
    }

    /*Insumo Dialog*/
    public String getDialogInsumo() {
        return "dlgInsumo";
    }

    /*Conclusão Solicitação Dialog*/
    public String getDialogCS() {
        return "dlgCS";
    }

    /*Autorização Solicitação Dialog*/
    public String getDialogAS() {
        return "dlgAS";
    }

    /*Coleta Dialog*/
    public String getDialogAC() {
        return "dlgAC";
    }

    /*Registro Pedido Dialog*/
    public String getDialogRP() {
        return "dlgRP";
    }

    /*Autorização Pedido Dialog*/
    public String getDialogAP() {
        return "dlgAP";
    }

    /*Emissão Nota Fiscal Dialog*/
    public String getDialogENF() {
        return "dlgENF";
    }

    /*Entrega Material Obra Dialog*/
    public String getDialogEMO() {
        return "dlgEMO";
    }

    /*Registro AR Dialog*/
    public String getDialogRA() {
        return "dlgRA";
    }

    /*Autorização AR Dialog*/
    public String getDialogAA() {
        return "dlgAA";
    }

    /*Pagamento Dialog*/
    public String getDialogPG() {
        return "dlgPG";
    }

    /*OK Dialog*/
    public String getDialogOK() {
        return "dlgOK";
    }

    /*Kardex do Material Dialog*/
    public String getDialogKardex() {
        return "dlgKardex";
    }

    /*Seleção de Centro de Custo Dialog*/
    public String getDialogCentroCusto() {
        return "dlgCentroCusto";
    }

    public String getDialogAjuda() {
        return "dlgAjuda";
    }

    /**
     * **************************************
     * MENSAGENS **************************************
     */
    public void msgSolicitacaoCompraCriada() {
        MessageUtils.messageFactoringFull("SolicitacaoCompraCreated",
                new Object[]{solicitacaoCompra.getIdSistema()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgSolicitacaoCompraAtualizada() {
        MessageUtils.messageFactoringFull("SolicitacaoCompraUpdated",
                new Object[]{solicitacaoCompra.getIdSistema()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgSolicitacaoCompraRemovida() {
        MessageUtils.messageFactoringFull("SolicitacaoCompraRemoved",
                new Object[]{solicitacaoCompra.getIdSistema()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgSolicitacaoItensAutorizados() {
        MessageUtils.messageFactoringFull("solicitacaoItensAutorizados",
                null,
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgItemNaoPossuiSolicitacao() {
        MessageUtils.messageFactoringFull("itemNaoPossuiSolicitacao",
                null,
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgItensSemQuantidadeInformada() {
        MessageUtils.messageFactoringFull("itensSemQuantidadeInformada",
                null,
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgUsuarioSemAutorizacao() {
        MessageUtils.messageFactoringFull("usuarioSemAutorizacao",
                null,
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgSolicitacaoItensRejeitados() {
        MessageUtils.messageFactoringFull("solicitacaoItensRejeitados",
                null,
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgInformeMotivoRejeitaSolicitacao() {
        MessageUtils.messageFactoringFull("informeMotivoRejeitaSolicitacao",
                null,
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgSolicitacaoItensNaoConcluidos() {
        MessageUtils.messageFactoringFull("solicitacaoItesNaoConcluidos",
                null,
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgSolicitacaoItensJaAutorizados() {
        MessageUtils.messageFactoringFull("solicitacaoItensJaAutorizados",
                null,
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgMaterialEntradaSemItem(BusinessException ex) {
        MessageUtils.messageFactoringFull("materialEntradaSemItem",
                ex.getMessage(),
                ex.getVariacoes(), FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgMaterialSaidaSemItem(BusinessException ex) {
        MessageUtils.messageFactoringFull("materialSaidaSemItem",
                ex.getMessage(),
                ex.getVariacoes(), FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgMaterialEntradaSemDocNumero() {
        MessageUtils.messageFactoringFull("MaterialEntrada.numeroDocumento.campoObrigatorio",
                null,
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgMaterialSaidaSemDocNumero() {
        MessageUtils.messageFactoringFull("MaterialSaida.numeroDocumento.campoObrigatorio",
                null,
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgErroAutorizaSolicitacao(Exception ex) {
        MessageUtils.messageFactoringFull("erroAutorizaSolicitacao",
                ex.getMessage(),
                null, FacesMessage.SEVERITY_ERROR,
                FacesContext.getCurrentInstance());
    }

    public void msgErroRemoverSolicitacao(Exception ex) {
        MessageUtils.messageFactoringFull("erroRemoverSolicitacao",
                ex.getMessage(),
                null,
                FacesMessage.SEVERITY_ERROR,
                FacesContext.getCurrentInstance());
    }

    public void msgSolicitacaoNaoPodeSerRemovida() {
        MessageUtils.messageFactoringFull("solicitacaoNaoPodeSerRemovida",
                null,
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgErroRejeitaSolicitacao(Exception ex) {
        MessageUtils.messageFactoringFull("erroRejeitaSolicitacao",
                ex.getMessage(),
                null, FacesMessage.SEVERITY_ERROR,
                FacesContext.getCurrentInstance());
    }

    public void msgErroDesautorizaSolicitacao(String ex) {
        MessageUtils.messageFactoringFull("erroDesautorizaSolicitacao",
                ex,
                null, FacesMessage.SEVERITY_ERROR,
                FacesContext.getCurrentInstance());
    }

    public void msgErroInserirSolicitacao(Exception ex) {
        MessageUtils.messageFactoringFull("erroInserirSolicitacao",
                ex.getMessage(),
                null, FacesMessage.SEVERITY_ERROR,
                FacesContext.getCurrentInstance());
    }

    public void msgErroRemoverMovimentacaoEntrada(Exception ex) {
        MessageUtils.messageFactoringFull("erroRemoverMovimentacaoEntrada",
                ex.getMessage(),
                null, FacesMessage.SEVERITY_ERROR,
                FacesContext.getCurrentInstance());
    }

    public void msgErroRemoverMovimentacaoSaida(Exception ex) {
        MessageUtils.messageFactoringFull("erroRemoverMovimentacaoSaida",
                ex.getMessage(),
                null, FacesMessage.SEVERITY_ERROR,
                FacesContext.getCurrentInstance());
    }

    public void msgMaterialSemItem() {
        MessageUtils.messageFactoringFull("materialSemItem",
                null, FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgInsumoJaIncluso(String insumoCod) {
        MessageUtils.messageFactoringFull("insumoJaIncluso",
                new Object[]{insumoCod}, FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgMaterialCamposObrigatorios() {
        MessageUtils.messageFactoringFull("materialCamposObrigatorios",
                null, FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgSolicitacaoCompraSemItem() {
        MessageUtils.messageFactoringFull("solicitacaoCompraSemItem",
                null, FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgSolicitacaoItensDesautorizados() {
        MessageUtils.messageFactoringFull("solicitacaoItensDesautorizados",
                null, FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgMaterialSaidaCriado() {
        MessageUtils.messageFactoringFull("MaterialSaidaCreated",
                new Object[]{materialSaida.getNumeroSaida()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgMaterialEntradaCriado() {
        MessageUtils.messageFactoringFull("MaterialEntradaCreated",
                new Object[]{materialEntrada.getNumeroEntrada()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgInsumoNaoGeraEstoque(InsumoSub insumoSub) {
        String idInsumo = insumoSub.getIdCompleto() + " - " + insumoSub.getInsumo().getEspecificacaoAbrev();
        MessageUtils.messageFactoringFull("insumoNaoGeraEstoque",
                new Object[]{idInsumo},
                FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgNumDocNaoInformado() {
        MessageUtils.messageFactoringFull("numeroDocNaoInformado",
                null, FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgMaterialOrigemNaoSimat() {
        MessageUtils.messageFactoringFull("materialOrigemNaoSimat",
                null, FacesMessage.SEVERITY_WARN,
                FacesContext.getCurrentInstance());
    }

    public void msgBusinessException(BusinessException ex) {
        MessageUtils.messageFactoringFull(ex.getMessage(),
                ex.getVariacoes(), FacesMessage.SEVERITY_ERROR,
                FacesContext.getCurrentInstance());
    }

    public void msgMovimentacaoEntradaAtualizada() {
        MessageUtils.messageFactoringFull("MaterialEntradaUpdated",
                new Object[]{materialEntrada.getNumeroEntrada()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgMovimentacaoEntradaRemovida() {
        MessageUtils.messageFactoringFull("MaterialEntradaRemoved",
                new Object[]{materialEntrada.getNumeroEntrada()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgMovimentacaoSaidaRemovida() {
        MessageUtils.messageFactoringFull("MaterialSaidaRemoved",
                new Object[]{materialSaida.getNumeroSaida()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }

    public void msgMovimentacaoSaidaAtualizada() {
        MessageUtils.messageFactoringFull("MaterialSaidaUpdated",
                new Object[]{materialSaida.getNumeroSaida()},
                FacesMessage.SEVERITY_INFO,
                FacesContext.getCurrentInstance());
    }
}