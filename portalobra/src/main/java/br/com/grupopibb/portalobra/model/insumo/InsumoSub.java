/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.insumo;

import br.com.grupopibb.portalobra.model.common.EntityInterface;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author tone.lima
 */
@Entity
@Table(name = "InsumoSub")
@NamedQueries({
    @NamedQuery(name = "InsumoSub.findParam",
            query = " SELECT DISTINCT isu FROM InsumoSub isu join isu.insumo i "
            + " WHERE (:insumoCod2 = 'todos' OR i.codigo LIKE :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR isu.codigo LIKE :insumoSubCod) "
            + " AND (:especificacao2 = 'todos' OR i.especificacao LIKE :especificacao) "
            + " AND (:classe2 = 'todos' OR i.caracterizacaoInsumo.grupoInsumos.classeInsumos.codigo = :classe) "
            + " AND (:grupo2 = 'todos' OR i.caracterizacaoInsumo.grupoInsumos.codigo = :grupo) "
            + " AND (:caracterizacao2 = 'todos' OR i.caracterizacaoInsumo.codigo = :caracterizacao) "
            + " AND (i.itemObsoleto = 'N' OR i.itemObsoleto IS NULL) "),
    @NamedQuery(name = "InsumoSub.countParam",
            query = " SELECT COUNT(DISTINCT isu) FROM InsumoSub isu join isu.insumo i "
            + " WHERE (:insumoCod2 = 'todos' OR i.codigo LIKE :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR isu.codigo LIKE :insumoSubCod) "
            + " AND (:especificacao2 = 'todos' OR i.especificacao LIKE :especificacao) "
            + " AND (:classe2 = 'todos' OR i.caracterizacaoInsumo.grupoInsumos.classeInsumos.codigo = :classe) "
            + " AND (:grupo2 = 'todos' OR i.caracterizacaoInsumo.grupoInsumos.codigo = :grupo) "
            + " AND (:caracterizacao2 = 'todos' OR i.caracterizacaoInsumo.codigo = :caracterizacao) "
            + " AND (i.itemObsoleto = 'N' OR i.itemObsoleto IS NULL) "),
    @NamedQuery(name = "InsumoSub.findParamOrc",
            query = " SELECT DISTINCT isu FROM InsumoSub isu join isu.insumo i join i.projetoItemOrcPlanIns orcIns join orcIns.projetoItemOrcPlan orc join i.caracterizacaoInsumo.grupoInsumos.classeInsumos.classeInsumosOrcamento classeOrc"
            + " WHERE (:insumoCod2 = 'todos' OR i.codigo LIKE :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR isu.codigo LIKE :insumoSubCod) "
            + " AND (:especificacao2 = 'todos' OR isu.especificacao LIKE :especificacao) "
            + " AND (:classe2 = 'todos' OR i.caracterizacaoInsumo.grupoInsumos.classeInsumos.codigo = :classe) "
            + " AND (:grupo2 = 'todos' OR i.caracterizacaoInsumo.grupoInsumos.codigo = :grupo) "
            + " AND (:caracterizacao2 = 'todos' OR i.caracterizacaoInsumo.codigo = :caracterizacao) "
            + " AND (i.itemObsoleto = 'N' OR i.itemObsoleto IS NULL) "
            + " AND (orcIns.orcPlanCod = 0) "
            + " AND (orc.planCod = :planCod) "
            + " AND (classeOrc.codigo <> 'T')"),
    @NamedQuery(name = "InsumoSub.countParamOrc",
            query = " SELECT COUNT(DISTINCT isu) FROM InsumoSub isu join isu.insumo i join i.projetoItemOrcPlanIns orcIns join orcIns.projetoItemOrcPlan orc join i.caracterizacaoInsumo.grupoInsumos.classeInsumos.classeInsumosOrcamento classeOrc"
            + " WHERE (:insumoCod2 = 'todos' OR i.codigo LIKE :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR isu.codigo LIKE :insumoSubCod) "
            + " AND (:especificacao2 = 'todos' OR isu.especificacao LIKE :especificacao) "
            + " AND (:classe2 = 'todos' OR i.caracterizacaoInsumo.grupoInsumos.classeInsumos.codigo = :classe) "
            + " AND (:grupo2 = 'todos' OR i.caracterizacaoInsumo.grupoInsumos.codigo = :grupo) "
            + " AND (:caracterizacao2 = 'todos' OR i.caracterizacaoInsumo.codigo = :caracterizacao) "
            + " AND (i.itemObsoleto = 'N' OR i.itemObsoleto IS NULL) "
            + " AND (orcIns.orcPlanCod = 0) "
            + " AND (orc.planCod = :planCod) "
            + " AND (classeOrc.codigo <> 'T') "),
    
    @NamedQuery(name = "InsumoSub.find",
            query = " SELECT DISTINCT isu from InsumoSub isu "
            + " WHERE ( isu.insumoCod = :insumoCod ) "
            + " AND ( isu.codigo = :insumoSubCod )")
})
public class InsumoSub implements EntityInterface<InsumoSub> {

    public InsumoSub() {
    }

    public InsumoSub(Long insumoCod, Integer codigo) {
        this.insumoCod = insumoCod;
        this.codigo = codigo;
    }

    public InsumoSub(Long insumoCod, Integer codigo, String especificacao, Insumo insumo) {
        this.insumoCod = insumoCod;
        this.codigo = codigo;
        this.especificacao = especificacao;
        this.insumo = insumo;
    }
    @Id
    @Column(name = "Insumo_Cod")
    private Long insumoCod;
    /*
     */
    @Id
    @Column(name = "SubInsumo_Cod")
    private Integer codigo;
    /*
     */
    @Column(name = "SubInsumo_Especificacao")
    private String especificacao;
    /*
     */
    @ManyToOne(targetEntity = Insumo.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "Insumo_Cod", insertable = false, updatable = false)
    private Insumo insumo;
    /*
     */
    @Transient
    private boolean marcado;

    @Override
    public Serializable getId() {
        return insumoCod + "_" + codigo;
    }

    @Override
    public String getLabel() {
        return especificacao;
    }

    @Override
    public boolean verificarId() {
        return false;
    }

    @Override
    public boolean isMarcado() {
        return marcado;
    }

    public void setMarcado(boolean marcado) {
        this.marcado = marcado;
    }

    @Override
    public int compareTo(InsumoSub o) {
        return this.getId().toString().compareTo(o.getId().toString());
    }

    public String getIdCompleto() {
        return NumberUtils.preencheZeroEsquerda(insumoCod.toString(), 5) + "-" + NumberUtils.preencheZeroEsquerda(codigo.toString(), 3);
    }

    public Long getInsumoCod() {
        return insumoCod;
    }

    public void setInsumoCod(Long insumoCod) {
        this.insumoCod = insumoCod;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.insumoCod);
        hash = 53 * hash + Objects.hashCode(this.codigo);
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
        final InsumoSub other = (InsumoSub) obj;
        if (!Objects.equals(this.insumoCod, other.insumoCod)) {
            return false;
        }
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return insumoCod + "-" + codigo + " - " + especificacao;
    }
    
}
