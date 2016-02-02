/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.geral;

import br.com.grupopibb.portalobra.controller.common.EntityController;
import br.com.grupopibb.portalobra.controller.common.EntityPagination;
import br.com.grupopibb.portalobra.dao.geral.CredorFacade;
import br.com.grupopibb.portalobra.dao.geral.EspecieCredorFacade;
import br.com.grupopibb.portalobra.dao.geral.EstadoFacade;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.geral.Credor;
import br.com.grupopibb.portalobra.model.geral.EspecieCredor;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
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
public class CredorController extends EntityController<Credor> implements Serializable {

    @EJB
    private CredorFacade credorFacade;
    @EJB
    private EspecieCredorFacade especieCredorFacade;
    @EJB
    private EstadoFacade estadoFacade;
    private Credor current;
    private Credor filtroCredor;
    private String codigo;
    private String razaoSocial;
    private EspecieCredor especie;
    private String cpfCnpj;
    private String nomeFantasia;
    private String inscricaoEstadual;
    private boolean listaNegra;
    private boolean semEspecificidade;
    private String cidade;
    private String estado;

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
        current = null;
        codigo = null;
        razaoSocial = null;
        especie = null;
        cpfCnpj = null;
        nomeFantasia = null;
        cidade = null;
        estado = null;
    }

    public CredorFacade getFacade() {
        return credorFacade;
    }

    @Override
    protected void setEntity(Credor t) {
        current = t;
    }

    /**
     * Método utilizado para iniciar o Credor a ser exibido na view.
     *
     * @param credor
     */
    public void initCurrent(Credor credor) {
        this.current = credor;
    }

    public Credor getFiltroCredor() {
        return filtroCredor;
    }

    public void setFiltroCredor(Credor filtroCredor) {
        this.filtroCredor = filtroCredor;
    }
    
    /**
     * SelectItem com a lista de espécies de credores.
     *
     * @return Espécies de Credor
     */
    public SelectItem[] getEspecieCredorSelect() {
        return JsfUtil.getSelectItems(especieCredorFacade.findAll(), false, FacesContext.getCurrentInstance());
    }

    public SelectItem[] getCredorSelect() {
        return JsfUtil.getSelectItems(credorFacade.findAll(), false, FacesContext.getCurrentInstance());
    }

    @Override
    protected Credor getNewEntity() {
        return new Credor();
    }

    @Override
    protected void performDestroy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String update() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String clean() {
        super.clean();
        recreateTable();
        return JsfUtil.MANTEM;
    }

    /**
     * Limpa os campos de busca do controller e recria a tabela com os registros
     * de credores.
     */
    public void cleanSearch() {
        codigo = null;
        cpfCnpj = null;
        nomeFantasia = null;
        razaoSocial = null;
        cidade = null;
        estado = null;
        semEspecificidade = false;
        recreateTable();
    }

    /**
     * Cria um SelectItem[] com todos os Estados cadastrados.
     *
     * @return SelectItem[] de Estados.
     */
    public SelectItem[] getEstadoSelect() {
        SelectItem[] selectItem;
        try {
            selectItem = JsfUtil.getSelectItems(estadoFacade.findAll(), false, FacesContext.getCurrentInstance());
        } catch (RuntimeException e) {
            selectItem = new SelectItem[0];
        }
        return selectItem;
    }

    @Override
    public EntityPagination getPagination() {
        if (pagination == null) {
            pagination = new EntityPagination(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().pesqCount(codigo, cpfCnpj, nomeFantasia, razaoSocial, cidade, estado, semEspecificidade).intValue();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().listPesqParamRange(codigo, cpfCnpj, nomeFantasia, razaoSocial, cidade, estado, semEspecificidade, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public void pesquisar() {
        recreateTable();
    }

    public Credor getCurrent() {
        return current;
    }

    public void setCurrent(Credor current) {
        this.current = current;
    }

    public CredorFacade getCredorFacade() {
        return credorFacade;
    }

    public void setCredorFacade(CredorFacade credorFacade) {
        this.credorFacade = credorFacade;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public EspecieCredor getEspecie() {
        return especie;
    }

    public void setEspecie(EspecieCredor especie) {
        this.especie = especie;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public Boolean getListaNegra() {
        return listaNegra;
    }

    public void setListaNegra(Boolean listaNegra) {
        this.listaNegra = listaNegra;
    }

    public Boolean getSemEspecificidade() {
        return semEspecificidade;
    }

    public void setSemEspecificidade(Boolean semEspecificidade) {
        this.semEspecificidade = semEspecificidade;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
