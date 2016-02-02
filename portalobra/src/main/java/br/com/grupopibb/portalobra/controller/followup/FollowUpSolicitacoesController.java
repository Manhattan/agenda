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
import br.com.grupopibb.portalobra.model.materiais.MateriaisEstoque;
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
import br.com.grupopibb.portalobra.model.tipos.EnumSituacaoSolicitacao;
import br.com.grupopibb.portalobra.model.tipos.EnumTipoDocumentoMaterialEntrada;
import br.com.grupopibb.portalobra.model.tipos.EnumTipoDocumentoMaterialSaida;
import br.com.grupopibb.portalobra.model.tipos.EnumTipoMovimentoMaterialEntrada;
import br.com.grupopibb.portalobra.model.tipos.EnumTipoMovimentoMaterialSaida;
import br.com.grupopibb.portalobra.model.tipos.EnumsGeral;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.JsfUtil;
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
import org.eclipse.persistence.exceptions.DatabaseException;

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
    @EJB
    private FollowupMsg msg;
    @EJB
    private FollowupDlg dlg;
    //------------------------------
    // private Funcionario funcionario;
    private MateriaisEstoque materiaisEstoque;
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
        newMovSaida = true;
        materialSaida = null;
        solicitacaoCompra = null;
        marcado = false;
        changeDialogStatus(false);
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
    public boolean isObraLinkadaOrcamento() {
        return loginController.getCentroSelecionado().isObraLinkadaOrcamento();
    }

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
     * ************************************************************************
     */
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

        if (dialog.equals(dlg.getDialogInsumo())) {
            changeDialogStatus(false);
            showDialogInsumo = true;
        } else if (dialog.equals(dlg.getDialogRS())) {
            changeDialogStatus(false);
            showDialogRS = true;
        } else if (dialog.equals(dlg.getDialogCS())) {
            changeDialogStatus(false);
            showDialogCS = true;
        } else if (dialog.equals(dlg.getDialogAS())) {
            changeDialogStatus(false);
            showDialogAS = true;
            //mountSolicitacaoCompraItem();
        } else if (dialog.equals(dlg.getDialogAC())) {
            changeDialogStatus(false);
            showDialogAC = true;
            //mountColetaPrecoItem();
        } else if (dialog.equals(dlg.getDialogRP())) {
            changeDialogStatus(false);
            showDialogRP = true;
        } else if (dialog.equals(dlg.getDialogAP())) {
            changeDialogStatus(false);
            showDialogAP = true;
        } else if (dialog.equals(dlg.getDialogRA())) {
            mountDocumentosEntrada();
            changeDialogStatus(false);
            showDialogRA = true;
        } else if (dialog.equals(dlg.getDialogENF())) {
            mountDocumentosEntrada();
            changeDialogStatus(false);
            showDialogENF = true;
        } else if (dialog.equals(dlg.getDialogEMO())) {
            mountDocumentosEntrada();
            changeDialogStatus(false);
            showDialogEMO = true;
        } else if (dialog.equals(dlg.getDialogAA())) {
            mountDocumentosEntrada();
            changeDialogStatus(false);
            showDialogAA = true;
        } else if (dialog.equals(dlg.getDialogPG())) {
            mountTitulosAPagar();
            changeDialogStatus(false);
            showDialogPG = true;
        } else if (dialog.equals(dlg.getDialogOK())) {
            mountTitulosAPagar();
            changeDialogStatus(false);
            showDialogOK = true;
        } else if (dialog.equals(dlg.getDialogKardex())) {
            changeDialogStatus(false);
            showDialogKardex = true;
        } else if (dialog.equals(dlg.getDialogCentroCusto())) {
            changeDialogStatus(false);
            showDialogCentroCusto = true;
        } else if (dialog.equals(dlg.getDialogAjuda())) {
            changeDialogStatus(false);
            showDialogAjuda = true;
        }
    }

    public void changeDisableButtonAutorizacao(boolean status) {
        disableButtonAutItem = status;
        disableButtonRejItem = status;
    }

    public void changeStatusOperacoes(boolean status) {
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

    public MateriaisEstoque getMateriaisEstoque() {
        return materiaisEstoque;
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
    public List<String> addOrRemoveTodos() {
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

        return insumosSelecionados;
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

    public void prepareIncItemSolic(List<String> insumosSelecionados) {
        if (newSolicitacao || solicitacaoCompra == null) {
            prepareCreateSolicitacao();
        }
        this.insumosSelecionados = insumosSelecionados;
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

    public void prepareIncItemMatSai(List<String> insumosSelecionados) {
        if (newMovSaida || materialSaida == null) {
            prepareCreateMaterialSaida();
        }
        this.insumosSelecionados = insumosSelecionados;
        /*if (insumosSelecionados == null) {
         insumosSelecionados = new ArrayList();
         }
         this.insumosSelecionados.addAll(selecionados);*/
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
        changeDisableButtonAutorizacao(true);
        insumosSelecionados = null;
        return JsfUtil.MANTEM;
    }

    public void incluirItem(List<String> selecionados) {
        if (showMovimentacaoSaida) {
            prepareIncItemMatSai(selecionados);
        } else {
            prepareIncItemSolic(selecionados);
        }
    }

    public void incluirItem(String item) {
        if (showMovimentacaoSaida) {
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

    /**
     * Quando o usuário cria em um item da solicitação, esse método deve prepara
     * o ambiente para editar a solicitação.
     *
     * @return java.lang.String Normalmente retorna contante MANTEM.
     */
    public void prepareEditSolicitacao(SolicitacaoCompra solicitacao) {
        solicitacaoCompra = solicitacao;
        this.newSolicitacao = false;
        changeDisableButtonAutorizacao(true);
        changeStatusOperacoes(false);
        showSolicitacaoCompra = true;
    }

    public void prepareEditMaterialSaida(MaterialSaida materialSaida) {
        this.materialSaida = materialSaida;
        this.newMovSaida = false;
        changeDisableButtonAutorizacao(true);
        changeStatusOperacoes(false);
        showMovimentacaoSaida = true;
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

    public void createOrEditMovimentacaoSaida() {
        if (materialSaida != null && StringUtils.isBlank(materialSaida.getNumeroDocumento())) {
            msg.warnMsg("MaterialSaida.numeroDocumento.campoObrigatorio", null);
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
            msg.businessException(null);
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
            msg.businessException(null);
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
                msg.warnMsg("itemNaoPossuiSolicitacao", null);
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
            msg.warnMsg("solicitacaoItesNaoConcluidos", null);
        }
        if (statusJaAut) {
            msg.warnMsg("solicitacaoItensJaAutorizados", null);
        }
        if (statusAut) {
            if (aut == EnumAutorizacao.A) {
                msg.infoMsg("solicitacaoItensAutorizados", null);
            } else {
                msg.infoMsg("solicitacaoItensRejeitados", null);
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
            msg.errorMsg("erroAutorizaSolicitacao", ex, null);
        }
        return JsfUtil.MANTEM;
    }

    /**
     * Rejeita os itens do followup selecionados. Os que já foram
     * autorizados/rejeitados ou não concluidos não sofrem alteração.
     */
    public String rejeitaSolicitacao(List<SolicitacaoCompraItem> solicItemSelecionados) {
        this.solicItemSelecionados = solicItemSelecionados;
        try {
            if (StringUtils.isBlank(motivoRejeitaSolicitacao)) {
                msg.warnMsg("informeMotivoRejeitaSolicitacao", null);
            } else {
                autorizaOuRejeitaSolicitacao(EnumAutorizacao.R);
            }
        } catch (EntityException | SQLException | NullPointerException ex) {
            msg.errorMsg("erroRejeitaSolicitacao", ex, null);
        }
        return JsfUtil.MANTEM;
    }

    public String desfazAutorizacao() {
        boolean statusAut = false;
        boolean statusErro = false;
        Exception erro = null;
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
                erro = ex;
            }
        }
        if (statusAut) {
            msg.infoMsg("solicitacaoItensDesautorizados", null);
        }
        if (statusErro) {
            msg.errorMsg("erroDesautorizaSolicitacao", erro, null);
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
            if (solicitacaoCompra.getItens() == null || solicitacaoCompra.getItens().isEmpty()) {
                msg.warnMsg("solicitacaoCompraSemItem", null);
            } else {
                try {
                    solicitacaoCompra.setIdSistema(solicitacaoCompraFacade.findMaxCentroNumero(loginController.getCentroSelecionado()) + 1);
                    //Executa se o centro estiver com orçamento ativo


                    solicitacaoCompra.setItens(isObraLinkadaOrcamento() ? atualizaItensPlanoOrcamento(EnumOpeEvtOrcamento.I, solicitacaoCompra.getItens()) : solicitacaoCompra.getItens());

                    Integer seqNumero = 0;
                    //Se ocorrer um erro ao persistir a solicitação, se for linkada serão apagados os eventos que deduziram do saldo a solcitar.
                    try {
                        seqNumero = solicitacaoCompraFacade.createSolicitacaoCompra(solicitacaoCompra);
                    } catch (NullPointerException | NoResultException | SQLServerException | RollbackException | EJBException e) {
                        if (isObraLinkadaOrcamento()) {
                            removeItensPlanoOrcamento(solicitacaoCompra.getItens());
                        }
                        msg.errorMsg("erroInserirSolicitacao", e, null);
                    }
                    if (seqNumero > 0) {
                        atualizaSequenciaisSolicitacaoCompra(seqNumero, solicitacaoCompra.getIdSistema());
                        logFollowupFacade.createLogSolic(solicitacaoCompra);
                        logFollowupFacade.updateLogFollowup();
                        //atualizaSaldoARealizarSolicitacao(solicitacaoCompra);
                        msg.infoMsg("SolicitacaoCompraCreated", solicitacaoCompra.getIdSistema().toString());
                    }
                } catch (BusinessException ex) {
                    newSolicitacao = true;
                    if (solicitacaoCompra.getItens().isEmpty()) {
                        msg.warnMsg("solicitacaoCompraSemItem", null);
                    } else {
                        msg.businessException(ex);
                    }
                } catch (SQLException | NullPointerException | NoResultException e) {
                    msg.errorMsg("erroInserirSolicitacao", e, null);
                } finally {
                    limparSolicitacaoCompra();
                    showSolicitacaoCompra = false;
                }
            }
        } else {
            if (solicitacaoCompra.getSolicitante() == null) {
                msg.warnMsg("usuarioSemAutorizacao", null);
            } else {
                msg.warnMsg("itensSemQuantidadeInformada", null);
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
                msg.infoMsg("SolicitacaoCompraUpdated", solicitacaoCompra.getIdSistema().toString());
                updateFollowup();
                recreateTable();

            } catch (EntityException | SQLException | DatabaseException ex) {
            } finally {
                limparSolicitacaoCompra();
            }
        } else {
            msg.warnMsg("itensSemQuantidadeInformada", null);
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
                msg.infoMsg("SolicitacaoCompraRemoved", solicitacaoCompra.getIdSistema().toString());
            } catch (SQLException | EntityException ex) {
                msg.errorMsg("erroRemoverSolicitacao", ex, null);
                throw new RuntimeException(ex);
            } finally {
                limparSolicitacaoCompra();
            }
        } else {
            msg.warnMsg("solicitacaoNaoPodeSerRemovida", null);
        }
    }

    public void removeMaterialSaida() {
        if (isEnableRemocaoMatSaida()) {
            try {
                materialSaidaFacade.remove(materialSaida);
                estoqueBusiness.atualizaEstoqueItensSaida(materialSaida.getItens(), loginController.getCentroSelecionado(), materialSaida);
                msg.infoMsg("MaterialSaidaRemoved", materialSaida.getNumeroSaida().toString());
            } catch (EntityException ex) {
                msg.errorMsg("erroRemoverMovimentacaoSaida", ex, null);
                throw new RuntimeException(ex);
            } finally {
                limparMovimentacaoMaterial();
                recreateTable();
            }
        } else {
            msg.errorMsg("erroRemoverMovimentacaoSaida", null, null);
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

    public void createMaterialSaidaAux() {
        try {
            Integer sequencial = materialSaidaFacade.createMaterialSaida(materialSaida);
            sequenciaisFacade.update(sequenciaisFacade.getSequencialMaterialSaida().initNumber(sequencial));
            for (MaterialSaidaItens itemS : materialSaida.getItens()) {
                estoqueBusiness.atualizaSaldoMaterial(loginController.getCentroSelecionado(), materialSaida.getDataSaida(), itemS.getInsumoSub());
            }
            logFollowupFacade.createLogMatSai(materialSaida, loginController.getFuncionario().getLogin(), null, null);
            logFollowupFacade.updateLogFollowup();
            msg.infoMsg("MaterialSaidaCreated", materialSaida.getNumeroSaida().toString());

        } catch (BusinessException ex) {
            if (materialSaida.getItens().isEmpty()) {
                msg.warnMsg("materialSaidaSemItem", ex, null);
            } else {
                msg.businessException(ex);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

//    public void createMaterialSaida() {
//        List<MaterialSaidaItens> aux = materialSaida.getItens();
//        double auxi = 0;
//        if (materialSaida.getItens() == null || materialSaida.getItens().isEmpty()) {
//            msg.warnMsg("materialSemItem", null);
//        } else if (true) {
//            for (MaterialSaidaItens item : materialSaida.getItens()) {
//                materiaisEstoque = materiaisEstoqueFacade.findParam(loginController.getCentroSelecionado(), item.getInsumoSub(), DateUtils.getDataFormatada("yyyy/MM", item.getDataSaida()).replace("/", ""));
//                double quant = materiaisEstoque.getEstoqueQuantidade();
//                for (int i = 0; i < materialSaida.getItens().size(); i++) {
//                    if (item.getQuantidade() == quant || item.getQuantidade() < quant) {
//                        if (item.getQuantidade() == quant && materialSaida.getItens().size() > 1) {
//
//                            auxi = item.getQuantidade() - quant;
//                            if (auxi == 0 && aux.get(i).getQuantidade() > auxi) {
//                                System.out.println("Nao pode inserir");
//                                break;
//                            }
//                        } else {
//                            this.createMaterialSaidaAux();
//                            updateFollowup();
//                            limparMovimentacaoMaterial();
//                            break;
//                        }
//
//
//                        if (item.getQuantidade() < quant && materialSaida.getItens().size() > 1) {
//                            auxi = item.getQuantidade() - quant;
//                            if (auxi < 0 && aux.get(i).getQuantidade() > auxi) {
//                                System.out.println("Nao pode inserir");
//                                break;
//                            }
//
//                        } else {
//                            this.createMaterialSaidaAux();
//                            updateFollowup();
//                            limparMovimentacaoMaterial();
//                            break;
//                        }
//                    }
//                }
//            }
//        }
    /**
     * Metodo que valida a saida de materiais verificando a quantidade inserida
     * com a quantidade atual do estoque
     *
     * @param itens Lista de Itens (MaterialSaidaItens)
     * @param centroCusto Centro de custo que o usuario está logado
     * @return Boolean true se a quantidade for igual ou menor que a quantidade
     * do estoque false se a quantidade inserida for maior que o estoque atual
     */
    public boolean isValidaMaterialSaida(List<MaterialSaidaItens> itens, CentroCusto centroCusto) {

        for (MaterialSaidaItens item : itens) {

            materiaisEstoque = materiaisEstoqueFacade.findParam(centroCusto, item.getInsumoSub(), DateUtils.getDataFormatada("yyyy/MM", item.getDataSaida()).replace("/", ""));
            double estoqueAtual = materiaisEstoque.getEstoqueQuantidade();
            double somaQuantItem = 0;
            if (!item.getMaterialSaida().getTipoMovimento().equals("D")) {
                for (MaterialSaidaItens item2 : itens) {
                    
                    if (item.getInsumoSub().getInsumoCod() == item2.getInsumoSub().getInsumoCod()) {
                        somaQuantItem += item2.getQuantidade();
                        if (somaQuantItem > estoqueAtual) {
                            msg.warnMsg("MaterialSaidaOver", null);
                            return false;

                        }
                    }

                }
            }else{
                return true;
            }

        }
        return true;
    }

    /**
     * Metodo que cria a saida de materiais de saida e verifica se a lista esta
     * nulla caso nao esteja e a validação de material esteja ok, é inserida a
     * informação no banco de dados e atualizada a tabela com os insumos
     */
    public void createMaterialSaida() {

        if (materialSaida.getItens() == null || materialSaida.getItens().isEmpty()) {
            msg.warnMsg("materialSemItem", null);
        } else if (isValidaMaterialSaida(materialSaida.getItens(), loginController.getCentroSelecionado())) {
            try {
                Integer sequencial = materialSaidaFacade.createMaterialSaida(materialSaida);
                sequenciaisFacade.update(sequenciaisFacade.getSequencialMaterialSaida().initNumber(sequencial));
                for (MaterialSaidaItens itemS : materialSaida.getItens()) {
                    estoqueBusiness.atualizaSaldoMaterial(loginController.getCentroSelecionado(), materialSaida.getDataSaida(), itemS.getInsumoSub());
                }
                logFollowupFacade.createLogMatSai(materialSaida, loginController.getFuncionario().getLogin(), null, null);
                logFollowupFacade.updateLogFollowup();
                msg.infoMsg("MaterialSaidaCreated", materialSaida.getNumeroSaida().toString());

            } catch (BusinessException ex) {
                if (materialSaida.getItens().isEmpty()) {
                    msg.warnMsg("materialSaidaSemItem", ex, null);
                } else {
                    msg.businessException(ex);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } finally {
                updateFollowup();
                limparMovimentacaoMaterial();
            }
        }

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
            msg.infoMsg("MaterialSaidaUpdated", materialSaida.getNumeroSaida().toString());
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

    public List<SolicitacaoCompraItem> getSolicItemSelecionados() {
        return solicItemSelecionados;
    }

    public void setSolicItemSelecionados(List<SolicitacaoCompraItem> solicItemSelecionados) {
        this.solicItemSelecionados = solicItemSelecionados;
    }
}