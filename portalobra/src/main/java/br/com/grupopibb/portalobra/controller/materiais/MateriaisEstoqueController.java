/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.materiais;

import br.com.grupopibb.portalobra.acesso.controller.LoginController;
import br.com.grupopibb.portalobra.business.materiais.EstoqueBusiness;
import br.com.grupopibb.portalobra.business.solicitacaocompra.SolicitacaoCompraBusiness;
import br.com.grupopibb.portalobra.controller.common.EntityController;
import br.com.grupopibb.portalobra.controller.common.EntityPagination;
import br.com.grupopibb.portalobra.dao.insumo.CaracterizacaoInsumosFacade;
import br.com.grupopibb.portalobra.dao.insumo.ClasseInsumosFacade;
import br.com.grupopibb.portalobra.dao.insumo.GrupoInsumosFacade;
import br.com.grupopibb.portalobra.dao.materiais.MateriaisEstoqueFacade;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.CaracterizacaoInsumos;
import br.com.grupopibb.portalobra.model.insumo.ClasseInsumos;
import br.com.grupopibb.portalobra.model.insumo.GrupoInsumos;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.model.materiais.MateriaisEstoque;
import br.com.grupopibb.portalobra.model.tipos.EnumOrderMatEstField;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import br.com.grupopibb.portalobra.utils.ReportUtil;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class MateriaisEstoqueController extends EntityController<MateriaisEstoque> implements Serializable {

    @EJB
    MateriaisEstoqueFacade materialEstoqueFacade;
    @EJB
    EstoqueBusiness estoqueBusiness;
    @EJB
    private SolicitacaoCompraBusiness solicitacaoCompraBusiness;
    @EJB
    private ClasseInsumosFacade classeInsumosFacade;
    @EJB
    private GrupoInsumosFacade grupoInsumosFacade;
    @EJB
    private CaracterizacaoInsumosFacade caracterizacaoInsumosFacade;
    @EJB
    private ReportUtil report;
    private MateriaisEstoque current;
    private String insumoCod;
    private Date mesAno;
    private int registrosPorPagina = 20;
    private boolean modoSimples = false;
    private SelectItem[] insumoClasseSelect;
    private SelectItem[] insumoGrupoSelect;
    private SelectItem[] insumoCaracterizacaoSelect;
    private ClasseInsumos filtroClasseInsumo;
    private GrupoInsumos filtroGrupoInsumo;
    private CaracterizacaoInsumos filtroCaracInsumo;
    private EnumOrderMatEstField orderBy;
    private String valorTotal;
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
        initMesAno();
    }

    private void initMesAno() {
        mesAno = mesAno == null ? new Date() : mesAno;
    }

    /**
     * Executado antes do bean JSF ser destruído.
     */
    @PreDestroy
    public void end() {
    }

    @Override
    public void pesquisar() {
        super.pesquisar();
        valorTotal = null;
    }

    public MateriaisEstoqueFacade getFacade() {
        return materialEstoqueFacade;
    }

    public String getDataSaldoInicial() {
        initMesAno();
        return DateUtils.getDataFormatada("dd/MM/yyyy", DateUtils.toFirstDate(DateUtils.addDays(mesAno, 1)));
    }

    public String getDataSaldoFinal() {
        initMesAno();
        return DateUtils.getDataFormatada("dd/MM/yyyy", DateUtils.toLastDate(DateUtils.addDays(mesAno, 1)));
    }

    @Override
    protected void setEntity(MateriaisEstoque t) {
        this.current = t;
    }

    @Override
    protected MateriaisEstoque getNewEntity() {
        return new MateriaisEstoque();
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

    /**
     * Pesquisa o valor de preço total dos insumos de acordo com o mes/ano
     * informado.
     *
     * @return Valor encontrado formatado como moeda.
     */
    public String getValorTotal() {
        try {
            if (valorTotal == null) {
                valorTotal = NumberUtils.formatCurrencyNoSymbol(getFacade().findValorTotal(loginController.getCentroSelecionado(), mesAno));
            }
        } catch (NullPointerException e) {
            valorTotal = "0,0000";
        }
        return valorTotal;
    }

    public Double getTotalCompras(InsumoSub insumoSub) {
        return solicitacaoCompraBusiness.getTotalComprasInsumo(loginController.getCentroSelecionado(), insumoSub);
    }

    /**
     * Método criado somente para utilização do inputText como componente de
     * exibição. Pois o inputText necessita dos métodos get e set para ser
     * renderizado corretamente.
     */
    public void setValorTotal(String valorTotal) {
    }

    /**
     * Cria o kardex do item selecionado e atribui o item atualizado ao Material
     * de Estoque atual (current).
     */
    public void mountKardex(MateriaisEstoque item, InsumoSub insumoSub, CentroCusto centro) {
       this.current = estoqueBusiness.mountKardex(item, mesAno, centro, insumoSub);
    }

    public void atualizaSaldo(InsumoSub insumoSub) {
        try {
            Date dataRef = DateUtils.toFirstDate(new Date());
            dataRef = DateUtils.addMonths(dataRef, -2);
            estoqueBusiness.atualizaSaldoMaterial(loginController.getCentroSelecionado(), dataRef, insumoSub);

            dataRef = DateUtils.addMonths(dataRef, 1);
            estoqueBusiness.atualizaSaldoMaterial(loginController.getCentroSelecionado(), dataRef, insumoSub);

            dataRef = DateUtils.addMonths(dataRef, 1);
            estoqueBusiness.atualizaSaldoMaterial(loginController.getCentroSelecionado(), dataRef, insumoSub);

            dataRef = null;
            valorTotal = null;
            getFacade().clearCache();
            recreateTable();
        } catch (SQLException ex) {
            Logger.getLogger(MateriaisEstoqueController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void atualizaSaldoTodosMeses() {
        try {
            estoqueBusiness.atualizaSaldoMaterialTodosMeses(loginController.getCentroSelecionado());
            valorTotal = null;
            getFacade().clearCache();
            recreateTable();
        } catch (SQLException ex) {
            Logger.getLogger(MateriaisEstoqueController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Determina se o panel com os filtros auxiliares será exibido ou não.
     *
     * @return Verdadeiro ou falso.
     */
    public boolean isModoSimples() {
        return modoSimples;
    }

    /**
     *
     * @param showPanelFiltros
     */
    public void setModoSimples(boolean modoSimples) {
        this.modoSimples = modoSimples;
    }

    public void cleanPanelFiltros() {
        filtroCaracInsumo = null;
        filtroClasseInsumo = null;
        filtroGrupoInsumo = null;
    }

    public void alterOrderByValor() {
        if (orderBy == EnumOrderMatEstField.VALOR_ASC) {
            orderBy = EnumOrderMatEstField.VALOR_DESC;
        } else {
            orderBy = EnumOrderMatEstField.VALOR_ASC;
        }
    }

    @Override
    public EntityPagination getPagination() {
        if (pagination == null) {
            pagination = new EntityPagination(registrosPorPagina) {
                @Override
                public int getItemsCount() {
                    if (itensCount == 0) {
                        itensCount = getFacade().pesqCount(loginController.getCentroSelecionado(), insumoCod, mesAno, filtroCaracInsumo, filtroClasseInsumo, filtroGrupoInsumo).intValue();
                    }
                    return itensCount;
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().listPesqParamRange(loginController.getCentroSelecionado(), insumoCod, mesAno, filtroCaracInsumo, filtroClasseInsumo, filtroGrupoInsumo, orderBy, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }
    
    public void abrirRelatorioMateriais(Date date, CentroCusto centro){
        
        if(centro != null){
            this.report.abrirRelatorioEstoqueMateraisPorCentro(date, centro);
        }else{
            this.report.abrirRelatorioEstoqueMaterais(date);
        }
    }

    public String getInsumoCod() {
        return insumoCod;
    }

    public void setInsumoCod(String insumoCod) {
        this.insumoCod = insumoCod;
    }

    public Date getMesAno() {
        return mesAno;
    }

    public void setMesAno(Date mesAno) {
        this.mesAno = mesAno;
    }

    public int getRegistrosPorPagina() {
        return registrosPorPagina;
    }

    public void setRegistrosPorPagina(int registrosPorPagina) {
        this.registrosPorPagina = registrosPorPagina;
    }

    public MateriaisEstoque getCurrent() {
        return current;
    }

    public void setCurrent(MateriaisEstoque current) {
        this.current = current;
    }

    public ClasseInsumos getFiltroClasseInsumo() {
        if (filtroClasseInsumo == null) {
            filtroClasseInsumo = new ClasseInsumos().initClasse("", "");
        }
        return filtroClasseInsumo;
    }

    public void setFiltroClasseInsumo(ClasseInsumos filtroClasseInsumo) {
        this.filtroClasseInsumo = filtroClasseInsumo;
    }

    public GrupoInsumos getFiltroGrupoInsumo() {
        if (filtroGrupoInsumo == null) {
            filtroGrupoInsumo = new GrupoInsumos().initClasse("", "", "");
        }
        return filtroGrupoInsumo;
    }

    public void setFiltroGrupoInsumo(GrupoInsumos filtroGrupoInsumo) {
        this.filtroGrupoInsumo = filtroGrupoInsumo;
    }

    public CaracterizacaoInsumos getFiltroCaracInsumo() {
        if (filtroCaracInsumo == null) {
            filtroCaracInsumo = new CaracterizacaoInsumos().initClasse("", "", "", "");
        }
        return filtroCaracInsumo;
    }

    public void setFiltroCaracInsumo(CaracterizacaoInsumos filtroCaracInsumo) {
        this.filtroCaracInsumo = filtroCaracInsumo;
    }

    public SelectItem[] getInsumoClasseSelect() {
        insumoClasseSelect = JsfUtil.getSelectItems(classeInsumosFacade.findAll(), false, FacesContext.getCurrentInstance());
        return insumoClasseSelect;
    }

    public void setInsumoClasseSelect(SelectItem[] insumoClasseSelect) {
        this.insumoClasseSelect = insumoClasseSelect;
    }

    public SelectItem[] getInsumoGrupoSelect() {

        if (filtroClasseInsumo == null) {
            filtroClasseInsumo = new ClasseInsumos();
        }
        List<GrupoInsumos> grupo = grupoInsumosFacade.findByClasse(filtroClasseInsumo.getCodigo());
        if (grupo == null) {
            grupo = new ArrayList<>();
        }
        insumoGrupoSelect = JsfUtil.getSelectItems(grupo, false, FacesContext.getCurrentInstance());

        return insumoGrupoSelect;
    }

    public void setInsumoGrupoSelect(SelectItem[] insumoGrupoSelect) {
        this.insumoGrupoSelect = insumoGrupoSelect;
    }

    public SelectItem[] getInsumoCaracterizacaoSelect() {
        if (filtroClasseInsumo == null) {
            filtroClasseInsumo = new ClasseInsumos();
        }
        if (filtroGrupoInsumo == null) {
            filtroGrupoInsumo = new GrupoInsumos();
        }

        String classeCod = filtroClasseInsumo.getCodigo() == null ? "" : filtroClasseInsumo.getCodigo();
        String grupoCod = filtroGrupoInsumo.getCodigo() == null ? "" : filtroGrupoInsumo.getCodigo();

        List<CaracterizacaoInsumos> caracterizacao = caracterizacaoInsumosFacade.findParam(grupoCod, classeCod);
        if (caracterizacao == null) {
            caracterizacao = new ArrayList<>();
        }
        insumoCaracterizacaoSelect = JsfUtil.getSelectItems(caracterizacao, false, FacesContext.getCurrentInstance());
        return insumoCaracterizacaoSelect;
    }

    public void setInsumoCaracterizacaoSelect(SelectItem[] insumoCaracterizacaoSelect) {
        this.insumoCaracterizacaoSelect = insumoCaracterizacaoSelect;
    }

    public EnumOrderMatEstField getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(EnumOrderMatEstField orderBy) {
        this.orderBy = orderBy;
    }
}
