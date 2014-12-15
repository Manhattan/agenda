/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.materiais;

import br.com.grupopibb.portalobra.model.common.EntityInterface;
import br.com.grupopibb.portalobra.model.insumo.Insumo;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author administrator
 */
@Entity
@Table(name = "MateriaisSaida_Itens")
@NamedQueries({
    @NamedQuery(name = "MaterialSaidaItens.find",
            query = " SELECT DISTINCT msi FROM MaterialSaidaItens msi "
            + " WHERE msi.insumo.codigo = :insumoCod "
            + " AND msi.empresaCod = :empresaCod "
            + " AND msi.centroCod = :centroCod "
            + " AND msi.filialCod = :filialCod "
            + " AND msi.dataSaida BETWEEN :dataInicial AND :dataFinal"),
    @NamedQuery(name = "MaterialSaidaItens.findSaidaNumeroByInsumo",
            query = " SELECT DISTINCT (mi.materialSaida.numeroSaida) FROM MaterialSaidaItens mi"
            + " WHERE (:insumoCod2 = 'todos' OR mi.insumo.codigo = :insumoCod) "
            + " AND (:insumoEspecificacao2 = 'todos' OR mi.insumo.especificacao LIKE :insumoEspecificacao) "
            + " AND (mi.empresaCod = :empresaCod) "
            + " AND (mi.centroCod = :centroCod) "
            + " AND (mi.filialCod = :filialCod) "),
    @NamedQuery(name = "MaterialSaidaItens.findEstoqueSaidas",
            query = " SELECT SUM(mi.quantidade) FROM MaterialSaidaItens mi"
            + " WHERE (mi.insumo.codigo = :insumoCod) "
            + " AND (mi.dataSaida BETWEEN :dataInicial AND :dataFinal) "
            + " AND (mi.empresaCod = :empresaCod) "
            + " AND (mi.centroCod = :centroCod) "
            + " AND (mi.filialCod = :filialCod) "),
    @NamedQuery(name = "MaterialSaidaItens.findTransferencias",
            query = " SELECT DISTINCT mi FROM MaterialSaidaItens mi join mi.materialSaida ms "
        + " WHERE ms.centro = :centroOrigem "
        + " AND mi.dataSaida = :dataSaida "
        + " AND mi.materialSaida.numeroDocumento = :numeroDocumento "
        + " AND mi.materialSaida.centroDestino = :centroDestino ")
})
public class MaterialSaidaItens implements EntityInterface<MaterialSaidaItens> {

    public MaterialSaidaItens() {
    }

    public MaterialSaidaItens(MaterialSaida materialSaida, Integer numero, String itemItem, Insumo insumo, Double quantidade, Double valor, Date dataSaida, String empresaCod, String filialCod, String centroCod, String observacao, Double estoqueAtual) {
        this.materialSaida = materialSaida;
        this.numero = numero;
        this.itemItem = itemItem;
        this.insumo = insumo;
        this.quantidade = quantidade;
        this.valor = valor;
        this.dataSaida = dataSaida;
        this.empresaCod = empresaCod;
        this.filialCod = filialCod;
        this.centroCod = centroCod;
        this.observacao = observacao;
        this.estoqueAtual = estoqueAtual;
    }
    @Id
    @ManyToOne(targetEntity = MaterialSaida.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "Saida_Numero")
    private MaterialSaida materialSaida;
    /*
     */
    @Id
    @Column(name = "SaidaItem_Numero")
    private Integer numero;
    /*
     */
    @Column(name = "SaidaItem_Item")
    private String itemItem;
    /*
     */
    @OneToOne(targetEntity = Insumo.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "Insumo_Cod")
    private Insumo insumo;
    /*
     */
    @Column(name = "SaidaItem_Quantidade")
    private Double quantidade;
    /*
     */
    @Column(name = "SaidaItem_Valor")
    private Double valor;
    /*
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "Saida_Data") 
    private Date dataSaida;
    /*
     */
    @Column(name = "Empresa_Cod")
    private String empresaCod;
    /*
     */
    @Column(name = "Filial_Cod")
    private String filialCod;
    /*
     */
    @Column(name = "Centro_Cod")
    private String centroCod;
    /*
     */
    @Column(name = "SaidaItem_Obs")
    private String observacao;
    /*
     */
    @Transient
    private Double estoqueAtual;
    /*
     */
    @Transient
    private boolean marcado = false;

    @Override
    public Serializable getId() {
        return materialSaida.getNumeroSaida().toString() + '-' + numero.toString();
    }

    @Override
    public String getLabel() {
        return "Insumo: " + insumo;
    }

    @Override
    public boolean verificarId() {
        return false;
    }

    @Override
    public boolean isMarcado() {
        return marcado;
    }
    
    public void setMarcado(boolean marcado){
        this.marcado = marcado;
    }

    @Override
    public int compareTo(MaterialSaidaItens o) {
        return this.getId().toString().compareTo(o.getId().toString());
    }

    public MaterialSaida getMaterialSaida() {
        return materialSaida;
    }

    public void setMaterialSaida(MaterialSaida materialSaida) {
        this.materialSaida = materialSaida;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getItemItem() {
        return itemItem;
    }

    public void setItemItem(String itemItem) {
        this.itemItem = itemItem;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getEmpresaCod() {
        return empresaCod;
    }

    public void setEmpresaCod(String empresaCod) {
        this.empresaCod = empresaCod;
    }

    public String getFilialCod() {
        return filialCod;
    }

    public void setFilialCod(String filialCod) {
        this.filialCod = filialCod;
    }

    public String getCentroCod() {
        return centroCod;
    }

    public void setCentroCod(String centroCod) {
        this.centroCod = centroCod;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Double getEstoqueAtual() {
        if (estoqueAtual == null) {
            estoqueAtual = 0.0;
        }
        return estoqueAtual;
    }

    public void setEstoqueAtual(Double estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }
}
