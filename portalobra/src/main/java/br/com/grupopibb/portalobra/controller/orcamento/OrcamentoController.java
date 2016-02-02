/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.controller.orcamento;

import br.com.grupopibb.portalobra.business.orcamento.OrcamentoBusiness;
import br.com.grupopibb.portalobra.dao.insumo.InsumoFacade;
import br.com.grupopibb.portalobra.model.solicitacaocompra.SolicitacaoCompraItem;
import br.com.grupopibb.portalobra.model.solicitacaocompra.SolicitacaoCompraItemOrcPlan;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author administrator
 */
@ManagedBean
@ViewScoped
public class OrcamentoController implements Serializable {

    @EJB
    InsumoFacade insumoFacade;
    @EJB
    OrcamentoBusiness orcamentoBusiness;
    private Integer insumoCod;
    private TreeNode root;
    private TreeNode selecionado;
    private Double[] valorSolicitado = new Double[10];
    private SolicitacaoCompraItem currentItem;
    private String valorSoma = "0,0000";
    private boolean solicitacao = false;

    public void openOrcamentoGrid(SolicitacaoCompraItem solicItem, boolean solicitacao) {
        this.solicitacao = solicitacao;
        this.currentItem = solicItem;

        root = new DefaultTreeNode(new SolicitacaoCompraItemOrcPlan(0, this.currentItem.getInsumoSub().getInsumoCod(), this.currentItem.getInsumoSub().getEspecificacao(), this.currentItem.getInsumoSub().getInsumo().getUnidade().getCodigo()));
        orcamentoBusiness.initDefaultTreeOrcamento(this.currentItem, this.root);
        
        if (currentItem.getItensPlanoOrcamento() == null || currentItem.getItensPlanoOrcamento().isEmpty()) {
            orcamentoBusiness.initSolicItemOrcamento(this.currentItem, this.root);
        }
        orcamentoBusiness.initUpdatedTreeOrcamento(this.currentItem, this.root);

        valorSoma = NumberUtils.formatDecimal(orcamentoBusiness.getSomaValores(root), 4);
    }

    public SolicitacaoCompraItem confirmOrcamentoGrid() {
        if (solicitacao) {
            this.currentItem.setQuantidade(orcamentoBusiness.getSomaValores(root));
            orcamentoBusiness.pupulateItenPlanNumero(this.currentItem);
            orcamentoBusiness.updateSolicItemOrcamento(this.currentItem, root);
            root = null;
            valorSoma = "0,0000";
        }
        return this.currentItem;
    }
    
    public boolean isValidationFailed(){
        return FacesContext.getCurrentInstance().isValidationFailed();
    }

    public void solicIgualSaldo(SolicitacaoCompraItemOrcPlan itemPlan){
        itemPlan.setValorSolic(itemPlan.getValorSaldo() + itemPlan.getValorSolicOriginal());
    }
    
    /**
     * Verifica se a grade de orçamento foi chamada por uma solicitação de
     * compra.
     *
     * @return Verdadeiro ou falso.s
     */
    public boolean isSolicitacao() {
        return solicitacao;
    }

    public Double getSomaValores() {
        Double valor = orcamentoBusiness.getSomaValores(root);
        valor = NumberUtils.sumPositiveNumbers(NumberUtils.arredondarHalfUp(valor, 4), 0.0);
        valorSoma = NumberUtils.formatDecimal(valor, 4);
        return valor;
    }
    
    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public Integer getInsumoCod() {
        return insumoCod;
    }

    public void setInsumoCod(Integer insumoCod) {
        this.insumoCod = insumoCod;
    }

    public TreeNode getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(TreeNode selecionado) {
        this.selecionado = selecionado;
    }

    public Double[] getValorSolicitado() {
        return valorSolicitado;
    }

    public void setValorSolicitado(Double[] valorSolicitado) {
        this.valorSolicitado = valorSolicitado;
    }

    public SolicitacaoCompraItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(SolicitacaoCompraItem currentItem) {
        this.currentItem = currentItem;
    }

    public String getValorSoma() {
        return valorSoma;
    }

    public void setValorSoma(String valorSoma) {
        this.valorSoma = valorSoma;
    }

    public void recreateTable() {
    }
}