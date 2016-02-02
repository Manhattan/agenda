/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.materiais;

import br.com.grupopibb.portalobra.model.common.EntityInterface;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author administrator
 */
@Entity
@Table(name = "MateriaisEstoque")
@NamedQueries({
    @NamedQuery(name = "MateriaisEstoque.find",
            query = " SELECT DISTINCT me FROM MateriaisEstoque me "
            + " WHERE (me.insumoSub = :insumoSub) "
            + " AND (me.centro = :centro) "
            + " AND (me.anoMes = :anoMes) "),
    @NamedQuery(name = "MateriaisEstoque.selectRange",
            query = " SELECT DISTINCT me FROM MateriaisEstoque me join me.insumoSub.insumo i"
            + " WHERE (me.centro = :centro) "
            + " AND (me.anoMes = :anoMes) "
            + " AND (:insumoCod2 = 'todos' OR (me.insumoSub.especificacao LIKE :insumoCod OR me.insumoCod LIKE :insumoCod)) "
            + " AND (:caracterizacao2 = 'todos' OR i.caracterizacaoCod = :caracterizacao) "
            + " AND (:grupo2 = 'todos' OR i.grupoCod = :grupo) "
            + " AND (:classe2 = 'todos' OR i.classeCod = :classe) "
            // + " AND (me.estoqueQuantidade <> 0.0) "
            + " ORDER BY me.insumoSub.insumo.especificacao "),
    @NamedQuery(name = "MateriaisEstoque.selectRangeValorAsc",
            query = " SELECT DISTINCT me FROM MateriaisEstoque me "
            + " WHERE (me.centro = :centro) "
            + " AND (me.anoMes = :anoMes) "
            + " AND (:insumoCod2 = 'todos' OR (me.insumoSub.especificacao LIKE :insumoCod OR me.insumoCod LIKE :insumoCod)) "
            + " AND (:caracterizacao2 = 'todos' OR me.insumoSub.insumo.caracterizacaoCod = :caracterizacao) "
            + " AND (:grupo2 = 'todos' OR me.insumoSub.insumo.grupoCod = :grupo) "
            + " AND (:classe2 = 'todos' OR me.insumoSub.insumo.classeCod = :classe) "
            // + " AND (me.estoqueQuantidade <> 0.0) "
            + " ORDER BY me.estoqueValor "),
    @NamedQuery(name = "MateriaisEstoque.selectRangeValorDesc",
            query = " SELECT DISTINCT me FROM MateriaisEstoque me "
            + " WHERE (me.centro = :centro) "
            + " AND (me.anoMes = :anoMes) "
            + " AND (:insumoCod2 = 'todos' OR (me.insumoSub.especificacao LIKE :insumoCod OR me.insumoCod LIKE :insumoCod)) "
            + " AND (:caracterizacao2 = 'todos' OR me.insumoSub.insumo.caracterizacaoCod = :caracterizacao) "
            + " AND (:grupo2 = 'todos' OR me.insumoSub.insumo.grupoCod = :grupo) "
            + " AND (:classe2 = 'todos' OR me.insumoSub.insumo.classeCod = :classe) "
            // + " AND (me.estoqueQuantidade <> 0.0) "
            + " ORDER BY me.estoqueValor DESC"),
    @NamedQuery(name = "MateriaisEstoque.countRange",
            query = " SELECT COUNT(DISTINCT me) FROM MateriaisEstoque me "
            + " WHERE (me.centro = :centro) "
            + " AND (me.anoMes = :anoMes) "
            + " AND (:insumoCod2 = 'todos' OR (me.insumoSub.especificacao LIKE :insumoCod OR me.insumoSub.insumoCod LIKE :insumoCod)) "
            + " AND (:caracterizacao2 = 'todos' OR me.insumoSub.insumo.caracterizacaoCod = :caracterizacao) "
            + " AND (:grupo2 = 'todos' OR me.insumoSub.insumo.grupoCod = :grupo) "
            + " AND (:classe2 = 'todos' OR me.insumoSub.insumo.classeCod = :classe) "),
    //  + " AND (me.estoqueQuantidade <> 0.0) "),
    @NamedQuery(name = "MateriaisEstoque.findValorTotal",
            query = " SELECT SUM(me.estoqueValor) FROM MateriaisEstoque me"
            + " WHERE (me.centro = :centro) "
            + " AND (me.anoMes = :anoMes) "
            + " AND (me.insumoSub.insumoCod is not null) ")
})
public class MateriaisEstoque implements EntityInterface<MateriaisEstoque> {

    public MateriaisEstoque() {
    }

    public MateriaisEstoque(InsumoSub insumoSub, CentroCusto centro) {
        this.insumoSub = insumoSub;
        this.centro = centro;
    }

    @Id
    @Column(name = "Insumo_Cod")
    private Long insumoCod;
    /*
     */
    @Id
    @Column(name = "SubInsumo_Cod")
    private Long insumoSubCod;
    /*
     */
    @ManyToOne(targetEntity = InsumoSub.class)
    @JoinColumns(value = {
        @JoinColumn(name = "Insumo_Cod", referencedColumnName = "Insumo_Cod", insertable = false, updatable = false),
        @JoinColumn(name = "SubInsumo_Cod", referencedColumnName = "SubInsumo_Cod", insertable = false, updatable = false)
    })
    private InsumoSub insumoSub;
    /*
     */
    @Id
    @Column(name = "Empresa_Cod")
    private String empresaCod;
    /*
     */
    @Id
    @Column(name = "Filial_Cod")
    private String filialCod;
    /*
     */
    @Id
    @Column(name = "Centro_Cod")
    private String centroCod;
    /*
     */
    @ManyToOne(targetEntity = CentroCusto.class, fetch = FetchType.EAGER)
    @JoinColumns(value = {
        @JoinColumn(name = "Centro_Cod", referencedColumnName = "Centro_Cod", insertable = false, updatable = false),
        @JoinColumn(name = "Empresa_Cod", referencedColumnName = "Empresa_Cod", insertable = false, updatable = false),
        @JoinColumn(name = "Filial_Cod", referencedColumnName = "Filial_Cod", insertable = false, updatable = false)
    })
    private CentroCusto centro;
    /*
     */
    @Id
    @Column(name = "Estoque_AnoMes")
    private String anoMes;
    /*
     */
    @Column(name = "Estoque_Quantidade")
    private Double estoqueQuantidade = 0.0;
    /*
     */
    @Column(name = "Estoque_Valor")
    private Double estoqueValor = 0.0;
    /*
     */
    @Transient
    private List<MaterialEntradaItens> materialEntradaItens;
    /*
     */
    @Transient
    private List<MaterialSaidaItens> materialSaidaItens;
    /*
     */
    @Transient
    private Double saldoInicial;
    /*
     */
    @Transient
    private Double saldoFinal;
    /*
     */
    @Transient
    private List<MaterialEntradasESaidas> materialEntradasESaidas;

    /**
     * Determina o preço unitário do material dividindo o valor total do estoque
     * pela quantidade total.
     *
     * @return Preço unitário do material.
     */
    public Double getPrecoUnitario() {
        if (estoqueQuantidade == null || estoqueQuantidade == 0 || estoqueValor == null || estoqueValor == 0) {
            return 0.0;
        } else {
            return NumberUtils.isNull(estoqueValor, 0.0) / estoqueQuantidade;
        }
    }

    public String getEstoqueQuantidadeFmt() {
        return NumberUtils.formatDecimal(estoqueQuantidade, 4);
    }

    @Override
    public Serializable getId() {
        return centro.getId().toString() + "_" + insumoCod.toString() + "_" + anoMes;
    }

    @Override
    public String getLabel() {
        return "Insumo: " + insumoCod.toString();
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
    public int compareTo(MateriaisEstoque o) {
        return this.getId().toString().compareTo(o.getId().toString());
    }

    public Long getInsumoCod() {
        return insumoCod;
    }

    public void setInsumoCod(Long insumoCod) {
        this.insumoCod = insumoCod;
    }

    public Long getInsumoSubCod() {
        return insumoSubCod;
    }

    public void setInsumoSubCod(Long insumoSubCod) {
        this.insumoSubCod = insumoSubCod;
    }

    public InsumoSub getInsumoSub() {
        return insumoSub;
    }

    public void setInsumoSub(InsumoSub insumoSub) {
        this.insumoSub = insumoSub;
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

    public CentroCusto getCentro() {
        return centro;
    }

    public void setCentro(CentroCusto centro) {
        this.centro = centro;
    }

    public String getAnoMes() {
        return anoMes;
    }

    public void setAnoMes(String anoMes) {
        this.anoMes = anoMes;
    }

    public Double getEstoqueQuantidade() {
        return estoqueQuantidade;
    }

    public void setEstoqueQuantidade(Double estoqueQuantidade) {
        this.estoqueQuantidade = estoqueQuantidade;
    }

    public Double getEstoqueValor() {
        return estoqueValor;
    }

    public void setEstoqueValor(Double estoqueValor) {
        this.estoqueValor = estoqueValor;
    }

    public List<MaterialEntradaItens> getMaterialEntradaItens() {
        return materialEntradaItens;
    }

    public void setMaterialEntradaItens(List<MaterialEntradaItens> materialEntradaItens) {
        this.materialEntradaItens = materialEntradaItens;
    }

    public List<MaterialSaidaItens> getMaterialSaidaItens() {
        return materialSaidaItens;
    }

    public void setMaterialSaidaItens(List<MaterialSaidaItens> materialSaidaItens) {
        this.materialSaidaItens = materialSaidaItens;
    }

    public Double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(Double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public Double getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(Double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public List<MaterialEntradasESaidas> getMaterialEntradasESaidas() {
        return materialEntradasESaidas;
    }

    public void setMaterialEntradasESaidas(List<MaterialEntradasESaidas> materialEntradasESaidas) {
        this.materialEntradasESaidas = materialEntradasESaidas;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.insumoCod);
        hash = 89 * hash + Objects.hashCode(this.empresaCod);
        hash = 89 * hash + Objects.hashCode(this.filialCod);
        hash = 89 * hash + Objects.hashCode(this.centroCod);
        hash = 89 * hash + Objects.hashCode(this.anoMes);
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
        final MateriaisEstoque other = (MateriaisEstoque) obj;
        if (!Objects.equals(this.insumoCod, other.insumoCod)) {
            return false;
        }
        if (!Objects.equals(this.empresaCod, other.empresaCod)) {
            return false;
        }
        if (!Objects.equals(this.filialCod, other.filialCod)) {
            return false;
        }
        if (!Objects.equals(this.centroCod, other.centroCod)) {
            return false;
        }
        if (!Objects.equals(this.anoMes, other.anoMes)) {
            return false;
        }
        return true;
    }
}
