/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.geral;

import br.com.grupopibb.portalobra.business.geral.PosicaoCredorBusiness;
import br.com.grupopibb.portalobra.dao.geral.NucleoNegocioFacade;
import br.com.grupopibb.portalobra.model.geral.Credor;
import br.com.grupopibb.portalobra.model.geral.Empresa;
import br.com.grupopibb.portalobra.model.geral.NucleoNegocio;
import br.com.grupopibb.portalobra.model.geral.PosicaoCredor;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author administrator
 */
@ManagedBean
@ViewScoped
public class PosicaoCredorController implements Serializable {

    @EJB
    private PosicaoCredorBusiness posicaoCredorBusiness;
    @EJB
    private NucleoNegocioFacade nucleoNegocioFacade;
    private PosicaoCredor current;
    private Date data;
    private Empresa empresa;
    private NucleoNegocio nucleoNegocio;
    private Credor credor;
    private List<PosicaoCredor> parcelas;
    private List<PosicaoCredor> previsoes;

    @PostConstruct
    public void init() {
      //  data = DateUtils.newDate(2014, 1, 1);
      //  empresa = new Empresa();
      //  empresa.setCodigo("MBR");
      //  nucleoCod = "01";
      //  credorCod = "000118";
    }

    /**
     * Executado antes do bean JSF ser destruído.
     */
    @PreDestroy
    public void end() {
        current = null;
        data = null;
        empresa = null;
        nucleoNegocio = null;
        credor = null;
    }
    
    public void initCredor(Credor cred){
        this.credor = cred;
        this.nucleoNegocio = nucleoNegocioFacade.findRange(new int[]{0,1}).get(0);
        this.data = DateUtils.toFirstDate(new Date());
    }

    public void clean(){
        this.credor = null;
        this.nucleoNegocio = null;
        this.data = null;
    }
    
    public PosicaoCredor getCurrent() {
        return current;
    }

    public void setCurrent(PosicaoCredor current) {
        this.current = current;
    }

    public void pesquisar() {
        parcelas = null;
        previsoes = null;
    }
    
    public SelectItem[] getNucleoSelect(){
        return JsfUtil.getSelectItems(nucleoNegocioFacade.findAll(), false, FacesContext.getCurrentInstance());
    }

    public List<PosicaoCredor> getParcelas() {
        if (parcelas == null) {
            parcelas = posicaoCredorBusiness.getParcelas(data, 
                    empresa == null ? null : empresa.getCodigo(), 
                    nucleoNegocio == null ? null : nucleoNegocio.getCodigo(), 
                    credor == null ? null : credor.getCodigo());
        }
        return parcelas;
    }

    public List<PosicaoCredor> getPrevisoes() {
        if (previsoes == null) {
            previsoes = posicaoCredorBusiness.getPrevisoes(data, 
                    empresa == null ? null : empresa.getCodigo(), 
                    nucleoNegocio == null ? null : nucleoNegocio.getCodigo(), 
                    credor == null ? null : credor.getCodigo());
        }
        return previsoes;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public NucleoNegocio getNucleoNegocio() {
        return nucleoNegocio;
    }

    public void setNucleoNegocio(NucleoNegocio nucleoNegocio) {
        this.nucleoNegocio = nucleoNegocio;
    }

    public Credor getCredor() {
        return credor;
    }

    public void setCredor(Credor credor) {
        this.credor = credor;
    }

}
