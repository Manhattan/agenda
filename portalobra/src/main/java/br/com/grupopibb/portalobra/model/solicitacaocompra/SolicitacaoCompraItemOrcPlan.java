/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.solicitacaocompra;

import br.com.grupopibb.portalobra.model.common.EntityInterface;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity; 
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient; 
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author administrator 
 */
@Entity 
@Table(name = "Solicitacao_de_Compra_Itens_OrcPlan")
public class SolicitacaoCompraItemOrcPlan implements EntityInterface<SolicitacaoCompraItemOrcPlan> {

    public SolicitacaoCompraItemOrcPlan() {
        this.classeItem = "IN";
        if (itemPlanCodOrc == null){
            itemPlanCodOrc = 0;
        }
    }

    public SolicitacaoCompraItemOrcPlan(Integer keyField, Long insumoCod, String discriminacao, String unidade) {
        this.keyField = keyField;
        this.insumoCod = insumoCod;
        this.discriminacao = discriminacao;
        this.unidade = unidade;
    }

    public SolicitacaoCompraItemOrcPlan(Integer keyField, Integer planCod, SolicitacaoCompraItem solicitacaoCompraItem, Integer numero, Integer itemPlanCodOrc, Long insumoCod, Double valorSolic, Integer planCodOrc, Integer numeroEvento) {
        this.keyField = keyField;
        this.solicitacaoCompraItem = solicitacaoCompraItem;
        this.numero = numero;
        this.itemPlanCodOrc = itemPlanCodOrc;
        this.insumoCod = insumoCod;
        this.valorSolic = valorSolic;
        this.planCodOrc = planCodOrc;
        this.numeroEvento = numeroEvento;
    }

    public SolicitacaoCompraItemOrcPlan(Integer keyField, Integer planCod, SolicitacaoCompraItem solicitacaoCompraItem, Integer numero, Integer itemPlanCodOrc, Long insumoCod, Double valorSolic, Integer planCodOrc, Integer numeroEvento, Integer itemPlanCod, Double valorSaldo, Double valorSaldoOriginal, String discriminacao, String classeItem, String ordemSequencial, Double valorOrcado, String unidade) {
        this.keyField = keyField;
        this.planCod = planCod;
        this.solicitacaoCompraItem = solicitacaoCompraItem;
        this.numero = numero;
        this.itemPlanCodOrc = itemPlanCodOrc;
        this.insumoCod = insumoCod;
        this.valorSolic = valorSolic;
        this.planCodOrc = planCodOrc;
        this.numeroEvento = numeroEvento;
        this.itemPlanCod = itemPlanCod;
        this.valorSaldo = valorSaldo;
        this.valorSaldoOriginal = valorSaldoOriginal;
        this.discriminacao = discriminacao;
        this.classeItem = classeItem;
        this.ordemSequencial = ordemSequencial;
        this.valorOrcado = valorOrcado; 
        this.unidade = unidade;
    }

    public SolicitacaoCompraItemOrcPlan(Integer keyField, Integer planCod, Long insumoCod, Integer itemPlanCod, String discriminacao, String classeItem, String ordemSequencial, String unidade) {
        this.keyField = keyField;
        this.insumoCod = insumoCod;
        this.itemPlanCod = itemPlanCod;
        this.discriminacao = discriminacao;
        this.classeItem = classeItem;
        this.ordemSequencial = ordemSequencial;
        this.unidade = unidade;
    }

    /**
     * Constroi o objeto a partir de outro objeto completo.
     *
     * @param itemPlan SolicitacaoCompraItemOrcPlan.
     */
    public SolicitacaoCompraItemOrcPlan(SolicitacaoCompraItemOrcPlan itemPlan) {
        this.solicitacaoCompraItem = itemPlan.getSolicitacaoCompraItem();
        this.numero = itemPlan.getNumero();
        this.itemPlanCodOrc = itemPlan.getItemPlanCodOrc();
        this.insumoCod = itemPlan.getInsumoCod();
        this.valorSolic = itemPlan.getValorSolic();
        this.planCodOrc = itemPlan.getPlanCodOrc();
        this.numeroEvento = itemPlan.getNumeroEvento();
        this.keyField = itemPlan.getKeyField();
        this.itemPlanCod = itemPlan.getItemPlanCod();
        this.planCod = itemPlan.getPlanCod();
        this.valorSaldo = itemPlan.getValorSaldo();
        this.valorSaldoOriginal = itemPlan.getValorSaldoOriginal();
        this.discriminacao = itemPlan.getDiscriminacao();
        this.classeItem = itemPlan.getClasseItem();
        this.ordemSequencial = itemPlan.getOrdemSequencial();
        this.valorOrcado = itemPlan.getValorOrcado();
        this.unidade = itemPlan.getUnidade();
    }
    @Id
    @ManyToOne(targetEntity = SolicitacaoCompraItem.class)
    @JoinColumns({
        @JoinColumn(name = "Solic_Numero", referencedColumnName = "Solic_Numero"),
        @JoinColumn(name = "SolicItem_Numero", referencedColumnName = "SolicItem_Numero")
    })
    private SolicitacaoCompraItem solicitacaoCompraItem;
    /*
     */
    @Id
    @Column(name = "SolicItemOrcPlan_Numero")
    private Integer numero;
    /*
     */
    @Column(name = "ItemOrcPlan_Cod")
    private Integer itemPlanCodOrc;
    /*
     */
    @Column(name = "Insumo_Cod")
    private Long insumoCod;
    /*
     */
    @Column(name = "SolicItemOrcPlan_Quantidade")
    private Double valorSolic;
    /*
     */
    @Column(name = "OrcPlan_Cod")
    private Integer planCodOrc;
    /*
     */
    @Column(name = "ItemOrcPlanEvt_Numero")
    private Integer numeroEvento;
    /*
     * keyField
     */
    @Transient
    private Integer keyField;
    /*
     * codigoPlanoItem
     */
    @Transient
    private Integer itemPlanCod;
    /*
     * codigoPlano
     */
    @Transient
    private Integer planCod;
    /*
     * quantidadeSaldoAjustado
     */
    @Transient
    private Double valorSaldo;
    /*
     * Saldo original
     */
    @Transient
    private Double valorSaldoOriginal;
    /*
     * discriminacao
     */
    @Transient
    private String discriminacao;
    /*
     * classeOrc
     */
    @Transient
    private String classeItem;
    /*
     * ordemCentralSeq
     */
    @Transient
    private String ordemSequencial;
    /*
     * valorOrc
     */
    @Transient
    private Double valorOrcado; 
    /*
     */
    @Column(name = "SolicItemOrcPlan_Quantidade", insertable = false, updatable = false)
    private Double valorSolicOriginal = 0.0;
    /*
     * codigoUnidade
     */
    @Transient
    private String unidade;

    public String getBackgroundColor() {
        switch (getClasseItem()) {
            case "PL":
                return "#B4E5CF"; //VERDE MAIS ESCURO
            case "IT":
                return "#D1F9DD"; //VERDE MAIS CLARO
            default:
                return "white"; //BRANCO
        }
    }
    
        /**
     * Determina o tamanho do recuo da esquerda de acordo com o tamanho do
     * sequencial.
     *
     * @return inteiro com o tamanho do recuo.
     */
    public int getMarginLeft() {
        //return getOrdemCentralSeq().length();
        if (getClasseItem().equals("PL")) { 
            return 0;
        }
        return getParentOrder().length() * 2;
    }
    
        /**
     * O código sequencial do nível superior, sendo o objeto atual subnível de
     * seu superior.
     *
     * @return a ordem sequencial do objeto pai.
     */
    public String getParentOrder() {
        String seq = StringUtils.replace(ordemSequencial, " ", "");
        if (seq.length() > 4) {
            seq = StringUtils.left(seq, seq.length() - 4);
        }
        return seq;
    }

    /**
     * @return ID único por plano de orçamento de um determinado insumo.
     */
    public String getCompareId() {
        return this.getClasseItem() + "_" + String.valueOf(this.getInsumoCod()) + "_" + this.itemPlanCodOrc;
    }
    
    /**
     * @return ID para comparar dois itens de orçamento que não tenham o atributo itemPlanCodOrc;
     */
    public String getCompareIdKey(){
        return this.getClasseItem() + "_" + String.valueOf(this.getInsumoCod()) + "_" + this.keyField;
    }

    @Override
    public Serializable getId() {
        return solicitacaoCompraItem == null ? "0" : String.valueOf(solicitacaoCompraItem.getNumero()) + String.valueOf(numero) + this.getClasseItem() + "_" + String.valueOf(this.getInsumoCod()) + "_" + this.keyField;
    }

    @Override
    public String getLabel() {
        return this.getClasseItem() + "_" + this.getInsumoCod().toString() + "_" + this.keyField + "_" + (this.itemPlanCodOrc == null ? "" : this.itemPlanCodOrc);
    }

    @Override
    public boolean verificarId() {
        return false;
    }

    @Override
    public boolean isMarcado() {
        return false;
    }

    @Override
    public int compareTo(SolicitacaoCompraItemOrcPlan o) {
        return this.getId().toString().compareTo(o.getId().toString());
    }

    public SolicitacaoCompraItem getSolicitacaoCompraItem() {
        return solicitacaoCompraItem;
    }

    public void setSolicitacaoCompraItem(SolicitacaoCompraItem solicitacaoCompraItem) {
        this.solicitacaoCompraItem = solicitacaoCompraItem;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getItemPlanCodOrc() {
        return itemPlanCodOrc;
    }

    public void setItemPlanCodOrc(Integer itemPlanCodOrc) {
        this.itemPlanCodOrc = itemPlanCodOrc;
    }

    public Long getInsumoCod() {
        return insumoCod;
    }

    public void setInsumoCod(Long insumoCod) {
        this.insumoCod = insumoCod;
    }

    public Double getValorSolic() {
        setValorSolic(valorSolic != null ? valorSolic : 0.0);
        return valorSolic;
    }

    public void setValorSolic(Double valorSolic) {
        this.valorSolic = valorSolic;
    }

    public Integer getPlanCodOrc() {
        return planCodOrc;
    }

    public void setPlanCodOrc(Integer planCodOrc) {
        this.planCodOrc = planCodOrc;
    }

    public Integer getNumeroEvento() {
        return numeroEvento;
    }

    public void setNumeroEvento(Integer numeroEvento) {
        this.numeroEvento = numeroEvento;
    }

    public Integer getItemPlanCod() {
        return itemPlanCod;
    }

    public void setItemPlanCod(Integer itemPlanCod) {
        this.itemPlanCod = itemPlanCod;
    }

    public Integer getPlanCod() {
        return planCod;
    }

    public void setPlanCod(Integer planCod) {
        this.planCod = planCod;
    }

    public Double getValorSaldo() {
        return valorSaldo;
    }

    public void setValorSaldo(Double valorSaldo) {
        this.valorSaldo = valorSaldo;
    }

    public Double getValorSaldoOriginal() {
        return valorSaldoOriginal;
    }

    public void setValorSaldoOriginal(Double valorSaldoOriginal) {
        this.valorSaldoOriginal = valorSaldoOriginal;
    }

    public String getDiscriminacao() {
        return discriminacao;
    }

    public void setDiscriminacao(String discriminacao) {
        this.discriminacao = discriminacao;
    }

    public String getClasseItem() {
        return StringUtils.replace(classeItem, " ", "");
    }

    public void setClasseItem(String classeItem) {
        this.classeItem = classeItem;
    }

    public String getOrdemSequencial() {
        return ordemSequencial;
    }

    public void setOrdemSequencial(String ordemSequencial) {
        this.ordemSequencial = ordemSequencial;
    }

    public Double getValorOrcado() {
        return valorOrcado;
    }

    public void setValorOrcado(Double valorOrcado) {
        this.valorOrcado = valorOrcado;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Integer getKeyField() {
        return keyField;
    }

    public void setKeyField(Integer keyField) {
        this.keyField = keyField;
    }

    public Double getValorSolicOriginal() {
        return valorSolicOriginal;
    }

    public void setValorSolicOriginal(Double valorSolicOriginal) {
        this.valorSolicOriginal = valorSolicOriginal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.numero);
        hash = 59 * hash + Objects.hashCode(this.insumoCod);
        hash = 59 * hash + Objects.hashCode(this.keyField);
        hash = 59 * hash + Objects.hashCode(this.classeItem);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SolicitacaoCompraItemOrcPlan other = (SolicitacaoCompraItemOrcPlan) obj;
        if (!Objects.equals(this.numero, other.numero)) {
            return false;
        }
        if (!Objects.equals(this.insumoCod, other.insumoCod)) {
            return false;
        }
        if (!Objects.equals(this.keyField, other.keyField)) {
            return false;
        }
        if (!Objects.equals(this.classeItem, other.classeItem)) {
            return false;
        }
        return true;
    }
    

}
