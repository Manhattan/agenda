/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.materiais;

import br.com.grupopibb.portalobra.acesso.controller.LoginController;
import br.com.grupopibb.portalobra.business.materiais.EstoqueBusiness;
import br.com.grupopibb.portalobra.business.materiais.MaterialBusiness;
import br.com.grupopibb.portalobra.controller.common.EntityController;
import br.com.grupopibb.portalobra.controller.common.EntityPagination;
import br.com.grupopibb.portalobra.controller.followup.FollowupMsg;
import br.com.grupopibb.portalobra.dao.followup.FollowUpSolicitacoesFacade;
import br.com.grupopibb.portalobra.dao.followup.LogFollowupFacade;
import br.com.grupopibb.portalobra.dao.geral.SequenciaisFacade;
import br.com.grupopibb.portalobra.dao.insumo.InsumoSubFacade;
import br.com.grupopibb.portalobra.dao.materiais.MateriaisEstoqueFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialEntradaFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialEntradaItensFacade;
import br.com.grupopibb.portalobra.exceptions.BusinessException;
import br.com.grupopibb.portalobra.exceptions.EntityException;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntrada;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntradaItens;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaidaItens;
import br.com.grupopibb.portalobra.model.tipos.EnumSistemaOrigemEstoque;
import br.com.grupopibb.portalobra.model.tipos.EnumTipoDocumentoMaterialEntrada;
import br.com.grupopibb.portalobra.model.tipos.EnumTipoMovimentoMaterialEntrada;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import java.io.Serializable;
import java.sql.SQLException;
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
public class MaterialEntradaController extends EntityController<MaterialEntrada> implements Serializable {

    @EJB
    private MaterialEntradaFacade materialEntradaFacade;
    @EJB
    private MaterialBusiness materialBusiness;
    @EJB
    private FollowupMsg msg;
    @EJB
    private InsumoSubFacade insumoSubFacade;
    @EJB
    private SequenciaisFacade sequenciaisFacade;
    @EJB
    private LogFollowupFacade logFollowupFacade;
    @EJB
    private EstoqueBusiness estoqueBusiness;
    @EJB
    private MateriaisEstoqueFacade materiaisEstoqueFacade;
    @EJB
    private MaterialEntradaItensFacade materialEntradaItensFacade;
    @EJB
    private FollowUpSolicitacoesFacade followUpSolicitacoesFacade;
    private MaterialEntrada current;
    private Date dataInicial;
    private Date dataFinal;
    private Long numeroEntrada;
    private Long insumoCod;
    private EnumSistemaOrigemEstoque origem = EnumSistemaOrigemEstoque.SIMAT;
    private String especificacao;
    // private List<String> insumosSelecionados;
    private List<MaterialEntradaItens> itensRemovidos;
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

    public MaterialEntradaFacade getFacade() {
        return materialEntradaFacade;
    }

    @Override
    public EntityPagination getPagination() {
        if (pagination == null) {
            pagination = new EntityPagination(15) {
                @Override
                public int getItemsCount() {
                    return getFacade().countParam(loginController.getCentroSelecionado(), numeroEntrada, dataInicial, dataFinal, origem, insumoCod, especificacao).intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRangeParam(loginController.getCentroSelecionado(), numeroEntrada, dataInicial, dataFinal, origem, insumoCod, especificacao, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    @Override
    protected void setEntity(MaterialEntrada t) {
        this.current = t;
    }

    public boolean isNewEntity() {
        return newEntity;
    }

    @Override
    protected MaterialEntrada getNewEntity() {
        return new MaterialEntrada();
    }

    /**
     * Quando o usuário clica em apagar um registro da tabela esse método deve
     * implementar a lógica.
     */
    @Override
    public void destroy() {
        performDestroy();
        recreateTable();
    }

    @Override
    protected void performDestroy() {
        if (loginController.isRemoveEntradaMaterial()) {
            try {
                materialEntradaFacade.remove(current);
                estoqueBusiness.atualizaEstoqueItensEntrada(current.getItens(), loginController.getCentroSelecionado(), current);
                msg.infoMsg("MaterialEntradaRemoved", current.getNumeroEntrada().toString());;
            } catch (EntityException ex) {
                msg.errorMsg("erroRemoverMovimentacaoEntrada", ex, null);
                throw new RuntimeException(ex);
            } finally {
                super.clean();
            }
        } else {
            msg.warnMsg("materialOrigemNaoSimat", null);
        }
    }

    /**
     * Define se o botão que remove (exclui) a entrada de material será
     * habilitado. De acordo com o negócio, só permitirá remoção se a data da
     * entrada for no mês atual.
     *
     * @return
     */
    public boolean isEnableRemocao() {
        return (current != null && current.getNumeroEntrada() != null
                && current.getNumeroEntrada() > 0 && !current.isItemRemovido()
                && DateUtils.isMesAtual(current.getDataEntrada())
                && current.getEntradaOrigem() == EnumSistemaOrigemEstoque.SIMAT);
    }

    public boolean isEnableInsercao() {
        return (current != null && loginController.isIncluiEntradaMaterial()
                && isMatEntradaTemItens() && (newEntity || isEnableEdicao()));
    }

    public boolean isEnableEdicao() {
        return (newEntity || (current != null && loginController.isAlteraEntradaMaterial()
                && DateUtils.isMesAtual(current.getDataEntrada())
                && current.getEntradaOrigem() == EnumSistemaOrigemEstoque.SIMAT));
    }

    /**
     * Verifica se a entrada de material possui itens.
     *
     * @return
     */
    public boolean isMatEntradaTemItens() {
        return (current != null && current.getItens() != null && !current.getItens().isEmpty());
    }

    public SelectItem[] getTipoMovimentoSelect() {
        return JsfUtil.getEnumSelectItems(EnumTipoMovimentoMaterialEntrada.class, false, FacesContext.getCurrentInstance());
    }

    public SelectItem[] getTipoDocumentoSelect() {
        return JsfUtil.getEnumSelectItems(EnumTipoDocumentoMaterialEntrada.class, false, FacesContext.getCurrentInstance());
    }

    /**
     * Transferência de Materiais
     *
     * @param selecionadosT
     */
    public void initSelecionadosT(List<MaterialSaidaItens> selecionadosT) {
        for (MaterialSaidaItens item : selecionadosT) {
            if (!materialBusiness.isContainsInsumoSub(current, item.getInsumoSub())) {
                current.getItens().add(materialBusiness.convertItemSaidaToItemEntrada(item, current));
            } else {
                msg.warnMsg("insumoJaIncluso", item.getInsumoSub().getId().toString());
            }
        }
    }

    public void removeItem(MaterialEntradaItens item) {
        if (current != null && current.getItens() != null) {
            current.getItens().remove(item);
            if (!newEntity) {
                current.setItemRemovido(true);
                if (itensRemovidos == null) {
                    itensRemovidos = new ArrayList<>();
                }
                itensRemovidos.add(item);
            }
        }
        materialBusiness.decrementItemMaterialEntrada(current);
    }

    public Double getEstoqueAtual(InsumoSub insumoSub) {
        return NumberUtils.arredondarHalfUp(NumberUtils.isNull(materiaisEstoqueFacade.findSaldo(loginController.getCentroSelecionado(), insumoSub, DateUtils.getYearMonth(new Date())), 0.0), 4);
    }

    public void prepareCreate(List<String> selecionados) {
        super.prepareCreate();
        Date hoje = new Date();
        current.setNumeroEntrada(0L);
        current.rebuildFields(loginController.getCentroSelecionado(),
                hoje, EnumTipoMovimentoMaterialEntrada.C, "", "",
                String.valueOf(DateUtils.getDay(hoje)),
                EnumSistemaOrigemEstoque.SIMAT,
                loginController.getFuncionario().getLogin(), hoje);
        incluirItens(selecionados);
    }

    /**
     * Prepara o Material de Entrada atual para alteração.
     */
    @Override
    public String prepareEdit() {
        super.prepareEdit();
        current.setUsuarioAlteracao(loginController.getFuncionario().getLogin());
        current.setDataAlteracao(new Date());
        return JsfUtil.MANTEM;
    }

    public String prepareEdit(Long numeroEntrada) {
        setEntity(materialEntradaFacade.find(numeroEntrada));
        newEntity = false;
        current.setUsuarioAlteracao(loginController.getFuncionario().getLogin());
        current.setDataAlteracao(new Date());
        return JsfUtil.MANTEM;
    }

    /**
     * Inclui itens na movimentação de material de entrada.
     *
     * @return Página atual.
     */
    public String incluirItens(List<String> selecionados) {
        if (current.getItens() == null) {
            current.setItens(new ArrayList<MaterialEntradaItens>());
        }
        if (selecionados != null && !selecionados.isEmpty()) {
            for (String insumoSubCod : selecionados) {
                InsumoSub insumoSub = insumoSubFacade.find(insumoSubCod);
                if (insumoSub != null) {
                    if (insumoSub.getInsumo().getGeraEstoque()) {
                        Integer itemNum = materialBusiness.incrementItemMatEntrada(current);
                        String itemItem = NumberUtils.preencheZeroEsquerda(String.valueOf(itemNum), 3);
                        Double valor = materialBusiness.getEntradaUltimoPreco(loginController.getCentroSelecionado(), insumoSub);
                        valor = valor == null || valor < 0.01 ? 0.01 : valor;
                        current.getItens().add(new MaterialEntradaItens(current, itemNum, itemItem, insumoSub, 1.0, valor, new Date(), loginController.getCentroSelecionado().getEmpresaCod(), loginController.getCentroSelecionado().getFilialCod(), loginController.getCentroSelecionado().getCodigo(), ""));
                        itemNum = null;
                        itemItem = null;
                    } else {
                        msg.warnMsg("insumoNaoGeraEstoque", insumoSub.getIdCompleto() + " - " + insumoSub.getInsumo().getEspecificacaoAbrev());
                    }
                    insumoSubCod = null;
                }
            }
        }
        selecionados = null;
        return JsfUtil.MANTEM;
    }

    @Override
    protected String create() {
        if (current.getItens() == null || current.getItens().isEmpty()) {
            msg.warnMsg("materialSemItem", null);
        } else {
            try {
                Integer sequencial = materialEntradaFacade.createMaterialEntrada(current);
                sequenciaisFacade.update(sequenciaisFacade.getSequencialMaterialEntrada().initNumber(sequencial));
                logFollowupFacade.createLogMatEnt(current, loginController.getFuncionario().getLogin(), null, null);
                logFollowupFacade.updateLogFollowup();

                for (MaterialEntradaItens item : current.getItens()) {
                    estoqueBusiness.atualizaSaldoMaterial(loginController.getCentroSelecionado(), current.getDataEntrada(), item.getInsumoSub());
                }
                msg.infoMsg("MaterialEntradaCreated", current.getNumeroEntrada().toString());
            } catch (BusinessException ex) {
                if (current.getItens().isEmpty()) {
                    msg.warnMsg("materialEntradaSemItem", ex, null);
                } else {
                    msg.businessException(ex);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        super.clean();
        followUpSolicitacoesFacade.clearCache();
        materiaisEstoqueFacade.clearCache();
        return JsfUtil.MANTEM;
    }

    @Override
    protected String update() {
        if (current.getEntradaOrigem() == EnumSistemaOrigemEstoque.SIMAT) {
            try {
                if (current.isItemRemovido()) {
                    MaterialEntrada mat = new MaterialEntrada(current);
                    mat.setItens(null);
                    materialEntradaFacade.update(mat);
                    materialEntradaItensFacade.createItens(current.getItens(), loginController.getCentroSelecionado(), current);
                    estoqueBusiness.atualizaEstoqueItensEntrada(itensRemovidos, loginController.getCentroSelecionado(), current);
                    itensRemovidos = null;
                    mat = null;
                } else {
                    materialEntradaFacade.update(current);
                }
                estoqueBusiness.atualizaEstoqueItensEntrada(current.getItens(), loginController.getCentroSelecionado(), current);
                msg.infoMsg("MaterialEntradaUpdated", current.getNumeroEntrada().toString());
            } catch (EntityException ex) {
                clean();
                throw new RuntimeException(ex);
            }
        } else {
            msg.warnMsg("materialOrigemNaoSimat", null);
        }
        clean();
        followUpSolicitacoesFacade.clearCache();
        materiaisEstoqueFacade.clearCache();
        return JsfUtil.MANTEM;
    }

    @Override
    public String clean() {
        initDataInicial();
        initDataFinal();
        numeroEntrada = null;
        insumoCod = null;
        origem = null;
        especificacao = null;
        recreateTable();
        return super.clean();
    }

    public void pesquisa() {
        recreateTable();
    }

    /**
     * Inicia a data inicial na pesquisa de entradas de material com o primeiro
     * dia do mês atual.
     */
    private void initDataInicial() {
        if (dataInicial == null) {
            dataInicial = DateUtils.incrementar(new Date(), -3, Calendar.MONTH);
        }
    }

    /**
     * Inicia a data inicial na pesquisa de entradas de material com a data
     * atual.
     */
    private void initDataFinal() {
        if (dataFinal == null) {
            dataFinal = new Date();
        }
    }

    public SelectItem[] getOrigemSelect() {
        return JsfUtil.getEnumSelectItems(EnumSistemaOrigemEstoque.class, false, FacesContext.getCurrentInstance());
    }

    public MaterialEntrada getCurrent() {
        return current;
    }

    public void setCurrent(MaterialEntrada current) {
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

    public Long getNumeroEntrada() {
        return numeroEntrada;
    }

    public void setNumeroEntrada(Long numeroEntrada) {
        this.numeroEntrada = numeroEntrada;
    }

    public Long getInsumoCod() {
        return insumoCod;
    }

    public void setInsumoCod(Long insumoCod) {
        this.insumoCod = insumoCod;
    }

    public EnumSistemaOrigemEstoque getOrigem() {
        return origem;
    }

    public void setOrigem(EnumSistemaOrigemEstoque origem) {
        this.origem = origem;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }

    /**
     * Limpa o cache da tabela do MaterialEntradaController da request atual.
     */
    public void updateDataModel() {
        clean();
        materialEntradaFacade.clearCache();
    }
}
