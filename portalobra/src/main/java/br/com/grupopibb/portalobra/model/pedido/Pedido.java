/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.pedido;

import br.com.grupopibb.portalobra.business.pedido.PedidoBusiness;
import br.com.grupopibb.portalobra.business.pedido.PedidoItemBusiness;
import br.com.grupopibb.portalobra.model.ar.DocumentoEntrada;
import br.com.grupopibb.portalobra.model.coleta.ColetaPreco;
import br.com.grupopibb.portalobra.model.common.EntityInterface;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.geral.Credor;
import br.com.grupopibb.portalobra.model.tipos.EnumCTR;
import br.com.grupopibb.portalobra.model.tipos.EnumPrazoInicioPedido;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 *
 * @author tone.lima
 */
@Entity
@Table(name = "Pedido")
@NamedQueries({
    @NamedQuery(name = "Pedido.find",
            query = " SELECT p FROM Pedido p "
            + " WHERE p.numero = :id")
})
public class Pedido implements EntityInterface<Pedido> {

    public Pedido() {
    }

    public Pedido(Long numero) {
        this.numero = numero;
    }

    public Pedido(Long numero, Integer idSistema, CentroCusto centroCusto, ColetaPreco coletaPreco, List<PedidoAutorizacoes> autorizantes, Credor credor, Date dataPedido, String auditUserName, String observacao, Date dataAutorizacao, String usuarioAutorizacao, String situacao) {
        this.numero = numero;
        this.idSistema = idSistema;
        this.centroCusto = centroCusto;
        this.coletaPreco = coletaPreco;
        this.autorizantes = autorizantes;
        this.credor = credor;
        this.dataPedido = dataPedido;
        this.auditUserName = auditUserName;
        this.observacao = observacao;
        this.dataAutorizacao = dataAutorizacao;
        this.usuarioAutorizacao = usuarioAutorizacao;
        this.situacao = situacao;
    }
    @Id
    @NotNull
    @Column(name = "Pedido_Numero", nullable = false)
    private Long numero;
    /**/
    @Column(name = "Pedido_CentroNumero")
    private Integer idSistema;
    /**/
    @OneToOne(targetEntity = DocumentoEntrada.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "Ent_Numero")
    private DocumentoEntrada documentoEntrada;
    /**/
    @ManyToOne(targetEntity = CentroCusto.class, fetch = FetchType.EAGER)
    @JoinColumns(value = {
        @JoinColumn(name = "Centro_Cod", referencedColumnName = "Centro_Cod"),
        @JoinColumn(name = "Empresa_Cod", referencedColumnName = "Empresa_Cod"),
        @JoinColumn(name = "Filial_Cod", referencedColumnName = "Filial_Cod")})
    private CentroCusto centroCusto;
    /**/
    @OneToOne(targetEntity = ColetaPreco.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "Coleta_Numero")
    private ColetaPreco coletaPreco;
    /*
     */
    @OneToMany(targetEntity = PedidoAutorizacoes.class)
    @JoinColumn(name = "Pedido_Numero")
    @OrderBy("data DESC")
    private List<PedidoAutorizacoes> autorizantes;
    /*
     */
    @ManyToOne(targetEntity = Credor.class)
    @JoinColumn(name = "Cre_Cod")
    private Credor credor;
    /**/
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Pedido_Data")
    private Date dataPedido;
    /*
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Pedido_PrevDataEnt")
    private Date dataPrevisaoEntrega;
    /*
     */
    @OneToMany(mappedBy = "pedido", targetEntity = PedidoItem.class, fetch = FetchType.EAGER)
    private List<PedidoItem> itens;
    /*
     */
    @Column(name = "Audit_UserName")
    private String auditUserName;
    /*
     */
    @Column(name = "Pedido_Observacao")
    private String observacao;
    /*
     */
    @Column(name = "Instrucao_Data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAutorizacao;
    /*
     */
    @Column(name = "Usuario_Instrucao")
    private String usuarioAutorizacao;
    /*
     */
    @Column(name = "Instrucao_Motivo")
    private String motivo;
    /*
     */
    @Column(name = "Instrucao_Cod")
    private String situacao;
    /*
     */
    @Column(name = "Pedido_Encaminhado")
    private String pedidoEncaminhado;
    /*
     */
    @Column(name = "Pedido_DescPed")
    private Float descontoPedido;
    /*
     */
    @Column(name = "PedForn_PrazoDias")
    private String prazoDias;
    /*
     */
    @Column(name = "PedForn_PrazoPerc")
    private String prazoPercentual;
    /*
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "PedForn_PrazoInicio")
    private EnumPrazoInicioPedido prazoInicio;
    /*
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "PedForn_CTR")
    private EnumCTR ctr;
    /*
     */
    @Transient
    private boolean encaminhado;
    /*
     */
    @Transient
    private Float valorTotal;
    /*
     */
    @Transient
    private Float valorComDesconto;

    public Float getValorTotal() {
        if (valorTotal == null || valorTotal == 0) {
            valorTotal = PedidoItemBusiness.somaTotalItens(itens);
        }
        return valorTotal;
    }

    public Float getValorTotalComIPI() {
        if (valorTotal == null || valorTotal == 0) {
            valorTotal = PedidoItemBusiness.somaTotalItensComIPI(itens);
        }
        return valorTotal;
    }

    public Float getValorComDesconto() {
        if (valorComDesconto == null) {
            valorComDesconto = getValorTotal() - descontoPedido;
        }
        return valorComDesconto;
    }

    public String getValorTotalFormatado() {
        return NumberUtils.formatCurrencyNoSymbol(getValorTotal().doubleValue());
    }

    public String getFormaPagamento() {
        return PedidoBusiness.getFormaPagamento(prazoDias, prazoPercentual, prazoInicio);
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Integer getIdSistema() {
        return idSistema;
    }

    public void setIdSistema(Integer idSistema) {
        this.idSistema = idSistema;
    }

    public DocumentoEntrada getDocumentoEntrada() {
        return documentoEntrada;
    }

    public void setDocumentoEntrada(DocumentoEntrada documentoEntrada) {
        this.documentoEntrada = documentoEntrada;
    }

    public CentroCusto getCentroCusto() {
        return centroCusto;
    }

    public void setCentroCusto(CentroCusto centroCusto) {
        this.centroCusto = centroCusto;
    }

    public ColetaPreco getColetaPreco() {
        return coletaPreco;
    }

    public void setColetaPreco(ColetaPreco coletaPreco) {
        this.coletaPreco = coletaPreco;
    }

    public Credor getCredor() {
        return credor;
    }

    public void setCredor(Credor credor) {
        this.credor = credor;
    }

    public Date getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(Date dataPedido) {
        this.dataPedido = dataPedido;
    }

    public Date getDataPrevisaoEntrega() {
        return dataPrevisaoEntrega;
    }

    public void setDataPrevisaoEntrega(Date dataPrevisaoEntrega) {
        this.dataPrevisaoEntrega = dataPrevisaoEntrega;
    }

    public List<PedidoItem> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItem> itens) {
        this.itens = itens;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataAutorizacao() {
        return dataAutorizacao;
    }

    public void setDataAutorizacao(Date dataAutorizacao) {
        this.dataAutorizacao = dataAutorizacao;
    }

    public String getUsuarioAutorizacao() {
        return usuarioAutorizacao;
    }

    public void setUsuarioAutorizacao(String usuarioAutorizacao) {
        this.usuarioAutorizacao = usuarioAutorizacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public boolean isEncaminhado() {
        encaminhado = pedidoEncaminhado != null && pedidoEncaminhado.equals("E");
        return encaminhado;
    }

    public void setEncaminhado(boolean encaminhado) {
        pedidoEncaminhado = encaminhado ? "E" : null;
        this.encaminhado = encaminhado;
    }

    public List<PedidoAutorizacoes> getAutorizantes() {
        return autorizantes;
    }

    public void setAutorizantes(List<PedidoAutorizacoes> autorizantes) {
        this.autorizantes = autorizantes;
    }

    public Float getDescontoPedido() {
        return descontoPedido;
    }

    public void setDescontoPedido(Float descontoPedido) {
        this.descontoPedido = descontoPedido;
    }

    public String getPrazoDias() {
        return prazoDias;
    }

    public void setPrazoDias(String prazoDias) {
        this.prazoDias = prazoDias;
    }

    public String getPrazoPercentual() {
        return prazoPercentual;
    }

    public void setPrazoPercentual(String prazoPercentual) {
        this.prazoPercentual = prazoPercentual;
    }

    public EnumPrazoInicioPedido getPrazoInicio() {
        return prazoInicio;
    }

    public void setPrazoInicio(EnumPrazoInicioPedido prazoInicio) {
        this.prazoInicio = prazoInicio;
    }

    public EnumCTR getCtr() {
        return ctr;
    }

    public void setCtr(EnumCTR ctr) {
        this.ctr = ctr;
    }

    @Override
    public Serializable getId() {
        return this.numero;
    }

    @Override
    public String getLabel() {
        return this.idSistema.toString();
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
    public int compareTo(Pedido o) {
        return this.getNumero().compareTo(o.getNumero());
    }
}
