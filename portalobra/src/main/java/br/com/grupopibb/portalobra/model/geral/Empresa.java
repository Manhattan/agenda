/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.geral;

import br.com.grupopibb.portalobra.model.common.EntityInterface;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author tone.lima
 */
@Entity
@Table(name="Empresa")
public class Empresa implements EntityInterface<Empresa> {

    @Id
    @NotNull
    @Size(min = 3, max = 3)
    @Column(name = "Empresa_Cod", nullable = false, length = 3)
    private String codigo;
    /**/
    @Size(min = 1, max = 100)
    @Column(name = "Empresa_Nome", nullable = false)
    private String nome;
    
    /*
    @Size(max = 6)
    @Column(name = "Empresa_CtbMesAnoIni")
    private String contabMesAnoInicio;
    
    @Size(max=4)
    @Column(name="Empresa_ExercicioCTB")
    private String exercicioContabil;
    
    @OneToOne(targetEntity=Credor.class)
    @JoinColumn(name="Cre_Cod")
    private Credor credor;
 
    private String logomarca;
    private String logomarcaClasse;
  
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CTB_DataLimite")
    private Date contabDataLimite;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CTB_DataLimite_DocEnt")
    private Date contabDataLimiteDocEnt;
    
    @Size(max=12)
    @Column(name="Conta_Fornecedor")
    private String contaFornecedor;
 */
    
    @Override
    public Serializable getId() {
        return codigo;
    }

    @Override
    public String getLabel() {
        return codigo + " - " + nome;
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
    public int compareTo(Empresa o) {
        return this.getCodigo().compareTo(o.getCodigo());
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return codigo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.codigo);
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
        final Empresa other = (Empresa) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }
    
}
