/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.geral;

import br.com.grupopibb.portalobra.model.common.EntityInterface;
import br.com.grupopibb.portalobra.model.pedido.Pedido;
import br.com.grupopibb.portalobra.model.tipos.EnumNatureza;
import br.com.grupopibb.portalobra.model.tipos.EnumPais;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.swing.text.MaskFormatter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author tone.lima
 */
@Entity
        @Table(name = "Credor")
        @NamedQueries({
    @NamedQuery(name = "Credor.selectRange",
            query = " SELECT DISTINCT c FROM Credor c "
            + " WHERE (:codigo2 = 'todos' OR c.codigo LIKE :codigo) "
            + " AND (:cpfCnpj2 = 'todos' OR c.cpfCnpj LIKE :cpfCnpj) "
            + " AND (:nomeFantasia2 = 'todos' OR c.nomeFantasia LIKE :nomeFantasia) "
            + " AND (:razaoSocial2 = 'todos' OR c.razaoSocial LIKE :razaoSocial) "
            + " AND (:cidade2 = 'todos' OR c.cidade LIKE :cidade) "
            + " AND (:estado2 = 'todos' OR c.uf = :estado) "
            + " AND (:semEspecificidade = 'todos' OR c.especieAvaliacao is null) "),
    @NamedQuery(name = "Credor.countRange",
            query = " SELECT COUNT(DISTINCT c) FROM Credor c "
            + " WHERE (:codigo2 = 'todos' OR c.codigo LIKE :codigo) "
            + " AND (:cpfCnpj2 = 'todos' OR c.cpfCnpj LIKE :cpfCnpj) "
            + " AND (:nomeFantasia2 = 'todos' OR c.nomeFantasia LIKE :nomeFantasia) "
            + " AND (:razaoSocial2 = 'todos' OR c.razaoSocial LIKE :razaoSocial) "
            + " AND (:cidade2 = 'todos' OR c.cidade LIKE :cidade) "
            + " AND (:estado2 = 'todos' OR c.uf = :estado) "
            + " AND (:semEspecificidade = 'todos' OR c.especieAvaliacao is null) "),
    @NamedQuery(name = "Credor.findCredorInnerPedido", query = " SELECT c FROM Credor c INNER JOIN c.pedido p "
            + " WHERE p.centroCusto.codigo = :centro_cod "
            + " AND p.centroCusto.empresaCod = :empresa_cod "
            + " AND p.centroCusto.filialCod = :filtro_cod "
            + " AND c.cpfCnpj = :cpfcnpj ")
})
public class Credor implements EntityInterface<Credor> {

    @Id
    @NotNull
    @Column(name = "Cre_Cod", nullable = false)
    private String codigo;
    /*
     */
    @Size(min = 1, max = 50)
    @Column(name = "Cre_RazSoc")
    private String razaoSocial;
    /*
     */
    @OneToOne(targetEntity = EspecieCredor.class)
    @JoinColumn(name = "Esp_Cod", nullable = false)
    private EspecieCredor especie;
    /*
     */
    @Size(max = 30)
    @Column(name = "Cre_Fantasia")
    private String nomeFantasia;
    /*
     */
    @OneToMany(targetEntity = Pedido.class, mappedBy = "credor")
    private List<Pedido> pedido;
    @Size(max = 14)
    @Column(name = "Cre_CGCCPF", nullable = false)
    private String cpfCnpj;
    /*
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "Pais_Cod")
    private EnumPais pais;
    /*
     */
    @Column(name = "UF_Sigla")
    private String uf;
    /*
     */
    @Column(name = "Cre_Endereco")
    private String endereco;
    /*
     */
    @Column(name = "Cre_Complto")
    private String complemento;
    /*
     */
    @Column(name = "Cre_CEP")
    private String cep;
    /*
     */
    @Column(name = "Cre_Bairro")
    private String bairro;
    /*
     */
    @Column(name = "Cre_Cidade")
    private String cidade;
    /*
     */
    @Column(name = "Cre_Numero")
    private String numero;
    /*
     */
    @Column(name = "Cre_InscricaoEstadual")
    private String inscricaoEstadual;
    /*
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "Cre_Natureza")
    private EnumNatureza natureza;
    /*
     */
    @Column(name = "AvaliEspec_Cod")
    private String especieAvaliacao;
    /*
     */
    @OneToMany(targetEntity = CredorComunicacao.class, mappedBy = "credor")
    private List<CredorComunicacao> contatos;

    /**
     * Retorna o telefone do primeiro contato da lista de contatos.
     */
    public String getTelefone() {
        String telefone;
        try {
            if (contatos != null && !contatos.isEmpty()) {
                telefone = contatos.get(0).getTelefone();
            } else {
                telefone = "";
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            telefone = "";
        }
        return telefone;
    }

    /**
     * Retorna o nome do primeiro contato da lista de contatos.
     */
    public String getContato() {
        String contato;
        try {
            if (contatos != null && !contatos.isEmpty()) {
                contato = contatos.get(0).getContato();
            } else {
                contato = "";
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            contato = "";
        }
        return contato;
    }

    /**
     * Retorna o e-mail do primeiro contato da lista de contatos.
     */
    public String getEmail() {
        String email;
        try {
            if (contatos != null && !contatos.isEmpty()) {
                email = contatos.get(0).getEmail();
            } else {
                email = "";
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            email = "";
        }
        return email;
    }

    public String getEnderecoENumero() {
        if (StringUtils.isNotBlank(this.numero)) {
            return this.endereco + ", " + this.numero;
        }
        return this.endereco;
    }

    /**
     *
     * @return
     */
    public String getEnderecoNumEComp() {
        String endNumEComp = this.endereco;
        if (StringUtils.isNotBlank(this.numero)) {
            endNumEComp += ", " + this.numero;
        }
        if (StringUtils.isNotBlank(this.complemento)) {
            endNumEComp += " - " + this.complemento;
        }
        return endNumEComp;
    }
    /*
     */
    @Transient
    private boolean marcado = false;

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

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public EnumPais getPais() {
        return pais;
    }

    public void setPais(EnumPais pais) {
        this.pais = pais;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public EnumNatureza getNatureza() {
        return natureza;
    }

    public void setNatureza(EnumNatureza natureza) {
        this.natureza = natureza;
    }

    public String getEspecieAvaliacao() {
        return especieAvaliacao;
    }

    public void setEspecieAvaliacao(String especieAvaliacao) {
        this.especieAvaliacao = especieAvaliacao;
    }

    public List<CredorComunicacao> getContatos() {
        return contatos;
    }

    public void setContatos(List<CredorComunicacao> contatos) {
        this.contatos = contatos;
    }

    public void setMarcado(boolean marcado) {
        this.marcado = marcado;
    }

    public List<Pedido> getPedido() {
        return pedido;
    }

    public void setPedido(List<Pedido> pedido) {
        this.pedido = pedido;
    }

    @Override
    public Serializable getId() {
        return codigo;
    }
    
    public String getLabelReport() {
        return codigo + " - " + razaoSocial;
    }

    @Override
    public String getLabel() {
        return razaoSocial;
    }

    @Override
    public boolean verificarId() {
        return false;
    }

    @Override
    public boolean isMarcado() {
        return marcado;
    }

    @Override
    public int compareTo(Credor o) {
        return this.getCodigo().compareTo(o.getCodigo());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.codigo);
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
        final Credor other = (Credor) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return razaoSocial;
    }
}
