/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.geral;

import br.com.grupopibb.portalobra.model.common.EntityInterface;
import br.com.grupopibb.portalobra.model.funcionario.Funcionario;
import br.com.grupopibb.portalobra.model.pedido.Pedido;
import br.com.grupopibb.portalobra.model.tipos.EnumEspecieCentroCusto;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author tone.lima
 */
@Entity
@Table(name = "Centro_de_Custo")
@NamedQueries({
    @NamedQuery(name = "CentroCusto.selectOne",
            query = " SELECT DISTINCT c FROM CentroCusto c "
            + " WHERE (:empresa = c.empresa AND :centro = c.codigo) "),
    @NamedQuery(name = "CentroCusto.findByEmpresaCodigo",
            query = " SELECT DISTINCT c FROM CentroCusto c "
            + " WHERE CONCAT(c.empresaCod, c.filialCod, c.codigo) = :empresaCodigo"),
    @NamedQuery(name = "CentroCusto.findAll",
            query = " SELECT c.empresaCod, c.filialCod, c.codigo, c.nome FROM CentroCusto c "),
   @NamedQuery(name = "CentroCusto.Nome",
        query = "SELECT cc.nome FROM CentroCusto cc "),
   @NamedQuery(name = "CentroCusto.findCentrosWithPedidosByForn",
        query = "SELECT DISTINCT cc FROM CentroCusto cc join cc.pedidos p join p.credor c "
        + " WHERE c.cpfCnpj = :cpfCnpj "),
        
})
public class CentroCusto implements EntityInterface<CentroCusto> {

    public CentroCusto() {
    }

    public CentroCusto(String codigo, String empresaCod, String filialCod) {
        this.codigo = codigo;
        this.empresaCod = empresaCod;
        this.filialCod = filialCod;
    }

    public CentroCusto(String codigo, String empresaCod, String filialCod, String nucleo, String nome, EnumEspecieCentroCusto especie) {
        this.codigo = codigo;
        this.empresaCod = empresaCod;
        this.filialCod = filialCod;
        this.nucleo = nucleo;
        this.nome = nome;
        this.especie = especie;
    }
    /*
     */
    @Id
    @NotNull
    @Column(name = "Centro_Cod", length = 4)
    private String codigo;
    /*
     */
    @Id
    @NotNull
    @Column(name = "Empresa_Cod", length = 3)
    private String empresaCod;
    /*
     */
    @ManyToOne(targetEntity = Empresa.class)
    @JoinColumn(name = "Empresa_Cod", insertable = false, updatable = false)
    private Empresa empresa;
    /*
     */
    @Id
    @NotNull
    @Column(name = "Filial_Cod", length = 2)
    private String filialCod;
    /*
     */
    @ManyToOne(targetEntity = Filial.class)
    @JoinColumns(value = {
        @JoinColumn(name = "Empresa_Cod", referencedColumnName = "Empresa_Cod", insertable = false, updatable = false),
        @JoinColumn(name = "Filial_Cod", referencedColumnName = "Filial_Cod", insertable = false, updatable = false)
    })
    private Filial filial;
    /*
     */
    @Size(min = 2, max = 2)
    @Column(name = "Nucleo_Cod", length = 2)
    private String nucleo;
    /*
     */
    @Size(min = 1, max = 100)
    @Column(name = "Centro_Nome", length = 100)
    private String nome;
    /*
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "EspCentro_Cod")
    private EnumEspecieCentroCusto especie;
    /*
     */
    @ManyToMany(targetEntity = Funcionario.class, fetch = FetchType.EAGER)
    @JoinTable(name = "PO_Funcionario_Centro",
            joinColumns = {
        @JoinColumn(name = "Centro_Cod", referencedColumnName = "Centro_Cod"),
        @JoinColumn(name = "Empresa_Cod", referencedColumnName = "Empresa_Cod"),
        @JoinColumn(name = "Filial_Cod", referencedColumnName = "Filial_Cod")},
            inverseJoinColumns = {
        @JoinColumn(name = "Fun_Cod", referencedColumnName = "Fun_Cod")
    })
    private List<Funcionario> funcionarios;

    /*
     */
    @Column(name = "UF_Sigla")
    private String estado;
    /*
     */
    @OneToMany(targetEntity = CentroComunicacao.class, mappedBy = "centro")
    private List<CentroComunicacao> contatos;
    /*
     */
    @OneToMany(targetEntity = Pedido.class, mappedBy = "centroCusto")
    private List<Pedido> pedidos;
    /*
     */
    @Transient
    private Integer projCod = 0;
    /*
     */
    @Transient
    private Integer orcCod = 0;
    /*
     */
    @Transient
    private Integer planCod = 0;
    /*
     */
    @Transient
    private boolean obraLinkadaOrcamento = false;
    
    /*
     @Size(min = 1, max = 40)
     @Column(name = "Centro_Endereco", nullable = false, length = 40)
     private String endereco;
    
     @Size(max = 6)
     @Column(name = "Centro_Numero", length = 6)
     private String numero;
    
     @Size(max = 20)
     @Column(name = "Centro_Complto", length = 20)
     private String complemento;
    
     @Size(max = 8)
     @Column(name = "Centro_CEP", nullable = false, length = 8)
     private String CEP;
    
     @Size(max = 30)
     @Column(name = "Centro_Bairro", length = 30)
     private String bairro;
    
     @Size(min = 1, max = 40)
     @Column(name = "Centro_Cidade", length = 40)
     private String cidade;
    
     @Enumerated(EnumType.STRING)
     @Column(name = "Pais_Cod", nullable = false)
     private EnumPais pais;
    
   
     @Enumerated(EnumType.STRING)
     @Column(name = "Centro_EnderecoFaturamento", nullable = false)
     private EnumsGeral enderecoFaturamento;
    
     @Size(max = 2)
     @Column(name = "Centro_FilialEnderecoFaturamen", length = 2)
     private String filialEnderecoFaturamento;


     private String enderecoCobranca;
     private EnumsGeral filialEnderecoCobranca;
     private Date dataEncerramento;
     private Date dataAbertura;
     private String estoqueMesAnoInicial;
     private String municipioCodigo;
     private EnumsGeral titulosAPagar;
     private int minAutoPedido;
     private String spedNaturezaFiscal;
     private String natureza;
     private EnumsGeral autorizaPedidoFaixa;
     private String matCei;
     */

    public CentroComunicacao getComunicacaoAlmoxarifado() {
        for (CentroComunicacao com : contatos) {
            if (com.getDepartamento().startsWith("Almox")) {
                return com;
            }
        }
        return new CentroComunicacao("","","","","");
    }

    public String getTelefoneAlmoxarifado() {
        try {
            return getComunicacaoAlmoxarifado().getTelefone();
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Retorna o nome do primeiro contato da lista de contatos.
     */
    public String getContatoAlmoxarifado() {
        try {
            return getComunicacaoAlmoxarifado().getContato();
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Retorna o e-mail do primeiro contato da lista de contatos.
     */
    public String getEmailAlmoxarifado() {
        try {
            return getComunicacaoAlmoxarifado().getEmail();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEmpresaCod() {
        return empresaCod;
    }

    public void setEmpresaCod(String empresaCod) {
        this.empresaCod = empresaCod;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getFilialCod() {
        return filialCod;
    }

    public void setFilialCod(String filialCod) {
        this.filialCod = filialCod;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public String getNucleo() {
        return nucleo;
    }

    public void setNucleo(String nucleo) {
        this.nucleo = nucleo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public EnumEspecieCentroCusto getEspecie() {
        return especie;
    }

    public void setEspecie(EnumEspecieCentroCusto especie) {
        this.especie = especie;
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<Funcionario> funcionarios) {
        this.funcionarios = funcionarios;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getProjCod() {
        return projCod;
    }

    public void setProjCod(Integer projCod) {
        this.projCod = projCod;
    }

    public Integer getOrcCod() {
        return orcCod;
    }

    public void setOrcCod(Integer orcCod) {
        this.orcCod = orcCod;
    }

    public Integer getPlanCod() {
        return planCod;
    }

    public void setPlanCod(Integer planCod) {
        this.planCod = planCod;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public boolean isObraLinkadaOrcamento() {
        return obraLinkadaOrcamento;
    }

    public void setObraLinkadaOrcamento(boolean obraLinkadaOrcamento) {
        this.obraLinkadaOrcamento = obraLinkadaOrcamento;
    }

    /**
     * Gera o ID composto pela empresa, filial e codigo. Ex.: MSU010002
     *
     * @return ID no formato AAA009999
     */
    public String getIdCompleto() {
        return empresaCod + filialCod + codigo;
    }

    public void setIdCompleto(String idCompleto) {
    }

    public String getNomeCompleto() {
        return empresaCod + "." + filialCod + "." + codigo + " - " + nome;
    }

    @Override
    public Serializable getId() {
        return empresaCod + filialCod + codigo;
    }

    @Override
    public String getLabel() {
        return empresaCod + "." + filialCod + "." + codigo + " - " + nome;
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
    public int compareTo(CentroCusto o) {
        return getId().toString().compareToIgnoreCase(o.getId().toString());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.codigo);
        hash = 83 * hash + Objects.hashCode(this.empresaCod);
        hash = 83 * hash + Objects.hashCode(this.filialCod);
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
        final CentroCusto other = (CentroCusto) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        if (!Objects.equals(this.empresaCod, other.empresaCod)) {
            return false;
        }
        if (!Objects.equals(this.filialCod, other.filialCod)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CentroCusto{" + empresaCod + filialCod + codigo + "}";
    }
}
