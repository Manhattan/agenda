/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.followup;

import br.com.grupopibb.portalobra.model.ar.DocumentoEntrada;
import br.com.grupopibb.portalobra.model.coleta.ColetaPreco;
import br.com.grupopibb.portalobra.model.coleta.ColetaPrecoItem;
import br.com.grupopibb.portalobra.model.common.EntityInterface;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.Insumo;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.model.materiais.MateriaisEstoque;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntradaItens;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntradasESaidas;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaidaItens;
import br.com.grupopibb.portalobra.model.pagamento.TituloAPagar;
import br.com.grupopibb.portalobra.model.pedido.Pedido;
import br.com.grupopibb.portalobra.model.pedido.PedidoItem;
import br.com.grupopibb.portalobra.model.solicitacaocompra.SolicitacaoCompraItem;
import br.com.grupopibb.portalobra.model.tipos.EnumAutorizacao;
import br.com.grupopibb.portalobra.model.tipos.EnumSituacaoSolicitacao;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import java.io.Serializable;
import java.util.Date;
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
 * @author tone.limama
 */
@Entity
@Table(name = "PO_FollowUp_Solic")
@NamedQueries({
    @NamedQuery(name = "FollowUpSolicitacoes.selectRangeEstoque",
            query = " SELECT DISTINCT f FROM FollowUpSolicitacoes f "
            + " WHERE (f.dataRegistroSolicitacao BETWEEN :dataInicial AND :dataFinal OR f.dataRegistroSolicitacao is null) "
            + " AND (f.centro = :centro) "
            + " AND (f.situacao IN :situacaoFiltro OR f.situacao is null) "
            + " AND (f.itemSolicitado in (0,1)) "
            + " AND (:insumoCod2 = 'todos' OR f.insumoCod like :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR f.insumoSubCod like :insumoSubCod) "
            + " AND (:insumoEspecificacao2 = 'todos' OR f.insumoEspecificacao like :insumoEspecificacao) "
            + " AND (:solicId2 = 'todos' OR f.solicitacaoCompraIDSis LIKE :solicId) "
            + " AND (:solicMaiorEstoque = 'todos' OR f.quantidadeSolicitada <= f.estoqueAtual) "
            + " ORDER BY f.solicitacaoCompraIDSis DESC"),
    @NamedQuery(name = "FollowUpSolicitacoes.countRangeEstoque",
            query = " SELECT COUNT(DISTINCT f) FROM FollowUpSolicitacoes f "
            + " WHERE (f.dataRegistroSolicitacao BETWEEN :dataInicial AND :dataFinal OR f.dataRegistroSolicitacao is null) "
            + " AND (f.centro = :centro)"
            + " AND (f.situacao IN :situacaoFiltro OR f.situacao is null) "
            + " AND (f.itemSolicitado in (0,1))"
            + " AND (:insumoCod2 = 'todos' OR f.insumoCod like :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR f.insumoSubCod like :insumoSubCod) "
            + " AND (:insumoEspecificacao2 = 'todos' OR f.insumoEspecificacao like :insumoEspecificacao) "
            + " AND (:solicId2 = 'todos' OR f.solicitacaoCompraIDSis LIKE :solicId) "
            + " AND (:solicMaiorEstoque = 'todos' OR f.quantidadeSolicitada <= f.estoqueAtual) "),
    /**/
    @NamedQuery(name = "FollowUpSolicitacoes.selectRange",
            query = " SELECT DISTINCT f FROM FollowUpSolicitacoes f LEFT JOIN f.pedido p LEFT JOIN f.coletaPrecoItem cp LEFT JOIN cp.credor cre "
            + " WHERE (f.dataRegistroSolicitacao BETWEEN :dataInicial AND :dataFinal) "
            + " AND (f.centro = :centro) "
            + " AND (f.situacao IN :situacaoFiltro) "
            + " AND (f.itemSolicitado = 0) "
            + " AND (:insumoCod2 = 'todos' OR f.insumoCod like :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR f.insumoSubCod like :insumoSubCod) "
            + " AND (:insumoEspecificacao2 = 'todos' OR f.insumoEspecificacao like :insumoEspecificacao) "
            + " AND (:solicitante2 = 'todos' OR f.solicitacaoCompraItem.solicitacao.solicitante = :solicitante) "
            + " AND (:codigoCredor2 = 'todos' OR cre.codigo = :codigoCredor) "
            + " AND (:razaoSocialCredor2 = 'todos' OR cre.razaoSocial LIKE :razaoSocialCredor) "
            + " AND (:nomeFantasiaCredor2 = 'todos' OR cre.nomeFantasia LIKE :nomeFantasiaCredor) "
            + " AND (:cpfCnpjCredor2 = 'todos' OR cre.cpfCnpj LIKE :cpfCnpjCredor) "
            + " AND (:numeroPedido2 = 'todos' OR p.idSistema = :numeroPedido) "
            + " AND (:solicId2 = 'todos' OR f.solicitacaoCompraIDSis LIKE :solicId) "
            + " AND (:solicMaiorEstoque = 'todos' OR f.quantidadeSolicitada <= f.estoqueAtual) "
            + " ORDER BY f.solicitacaoCompraIDSis DESC"),
    @NamedQuery(name = "FollowUpSolicitacoes.countRange",
            query = " SELECT COUNT(DISTINCT f) FROM FollowUpSolicitacoes f LEFT JOIN f.pedido p LEFT JOIN f.coletaPrecoItem cp LEFT JOIN cp.credor cre "
            + " WHERE (f.dataRegistroSolicitacao BETWEEN :dataInicial AND :dataFinal) "
            + " AND (f.centro = :centro) "
            + " AND (f.situacao IN :situacaoFiltro) "
            + " AND (f.itemSolicitado = 0)"
            + " AND (:insumoCod2 = 'todos' OR f.insumoCod like :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR f.insumoSubCod like :insumoSubCod) "
            + " AND (:insumoEspecificacao2 = 'todos' OR f.insumoEspecificacao like :insumoEspecificacao) "
            + " AND (:solicitante2 = 'todos' OR f.solicitacaoCompraItem.solicitacao.solicitante = :solicitante) "
            + " AND (:codigoCredor2 = 'todos' OR cre.codigo = :codigoCredor) "
            + " AND (:razaoSocialCredor2 = 'todos' OR cre.razaoSocial LIKE :razaoSocialCredor) "
            + " AND (:nomeFantasiaCredor2 = 'todos' OR cre.nomeFantasia LIKE :nomeFantasiaCredor) "
            + " AND (:cpfCnpjCredor2 = 'todos' OR cre.cpfCnpj LIKE :cpfCnpjCredor) "
            + " AND (:numeroPedido2 = 'todos' OR p.idSistema = :numeroPedido) "
            + " AND (:solicId2 = 'todos' OR f.solicitacaoCompraIDSis LIKE :solicId)"
            + " AND (:solicMaiorEstoque = 'todos' OR f.quantidadeSolicitada <= f.estoqueAtual) "),
    /**/
    @NamedQuery(name = "FollowUpSolicitacoes.findEstoqueByInsumo",
            query = " SELECT MAX(f.estoqueAtual) FROM FollowUpSolicitacoes f "
            + " WHERE f.centro = :centro and f.insumoSub = :insumoSub"),
    /**/
    @NamedQuery(name = "FollowUpSolicitacoes.findEstoqueUsinaByInsumo",
            query = " SELECT MAX(f.estoqueUsina) FROM FollowUpSolicitacoes f "
            + " WHERE f.centro = :centro and f.insumoSub = :insumoSub"),
    /**/
    @NamedQuery(name = "FollowUpSolicitacoes.findQuantidadeOrcamento",
            query = " SELECT MAX(f.quantidadeOrcamento) FROM FollowUpSolicitacoes f "
            + " WHERE f.centro = :centro and f.insumoCod = :insumoCod"),
    /**/
    @NamedQuery(name = "FollowUpSolicitacoes.selectRangeEstoqueUsina",
            query = " SELECT DISTINCT f FROM FollowUpSolicitacoes f "
            + " WHERE (f.dataRegistroSolicitacao BETWEEN :dataInicial AND :dataFinal OR f.dataRegistroSolicitacao is null) "
            + " AND (f.centro = :centro) "
            + " AND (f.situacao IN :situacaoFiltro OR f.situacao is null) "
            + " AND (f.itemSolicitado in (0,1,2)) "
            + " AND (:insumoCod2 = 'todos' OR f.insumoCod like :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR f.insumoSubCod like :insumoSubCod) "
            + " AND (:insumoEspecificacao2 = 'todos' OR f.insumoEspecificacao like :insumoEspecificacao) "
            + " AND (:solicId2 = 'todos' OR f.solicitacaoCompraIDSis LIKE :solicId)"
            + " AND (:solicMaiorEstoque = 'todos' OR f.quantidadeSolicitada <= f.estoqueAtual) "
            + " ORDER BY f.solicitacaoCompraIDSis DESC "),
    @NamedQuery(name = "FollowUpSolicitacoes.countRangeEstoqueUsina",
            query = " SELECT COUNT(DISTINCT f) FROM FollowUpSolicitacoes f "
            + " WHERE (f.dataRegistroSolicitacao BETWEEN :dataInicial AND :dataFinal OR f.dataRegistroSolicitacao is null) "
            + " AND (f.centro = :centro)"
            + " AND (f.situacao IN :situacaoFiltro OR f.situacao is null) "
            + " AND (f.itemSolicitado in (0,1,2))"
            + " AND (:insumoCod2 = 'todos' OR f.insumoCod like :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR f.insumoSubCod like :insumoSubCod) "
            + " AND (:insumoEspecificacao2 = 'todos' OR f.insumoEspecificacao like :insumoEspecificacao) "
            + " AND (:solicId2 = 'todos' OR f.solicitacaoCompraIDSis LIKE :solicId) "
            + " AND (:solicMaiorEstoque = 'todos' OR f.quantidadeSolicitada <= f.estoqueAtual) "),
    /**/
    @NamedQuery(name = "FollowUpSolicitacoes.selectRangeUsina",
            query = " SELECT DISTINCT f FROM FollowUpSolicitacoes f "
            + " WHERE (f.dataRegistroSolicitacao BETWEEN :dataInicial AND :dataFinal OR f.dataRegistroSolicitacao is null) "
            + " AND (f.centro = :centro) "
            + " AND (f.situacao IN :situacaoFiltro OR f.situacao is null) "
            + " AND (f.itemSolicitado in (0,1,2)) "
            + " AND (:insumoCod2 = 'todos' OR f.insumoCod like :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR f.insumoSubCod like :insumoSubCod) "
            + " AND (:insumoEspecificacao2 = 'todos' OR f.insumoEspecificacao like :insumoEspecificacao) "
            + " AND (f.estoqueUsina > 0 OR f.itemSolicitado = 0) "
            + " AND (:solicId2 = 'todos' OR f.solicitacaoCompraIDSis LIKE :solicId)"
            + " AND (:solicMaiorEstoque = 'todos' OR f.quantidadeSolicitada <= f.estoqueAtual) "
            + " ORDER BY f.solicitacaoCompraIDSis DESC "),
    @NamedQuery(name = "FollowUpSolicitacoes.countRangeUsina",
            query = " SELECT COUNT(DISTINCT f) FROM FollowUpSolicitacoes f "
            + " WHERE (f.dataRegistroSolicitacao BETWEEN :dataInicial AND :dataFinal OR f.dataRegistroSolicitacao is null) "
            + " AND (f.centro = :centro)"
            + " AND (f.situacao IN :situacaoFiltro OR f.situacao is null) "
            + " AND (f.itemSolicitado in (0,1,2))"
            + " AND (:insumoCod2 = 'todos' OR f.insumoCod like :insumoCod) "
            + " AND (:insumoSubCod2 = 'todos' OR f.insumoSubCod like :insumoSubCod) "
            + " AND (:insumoEspecificacao2 = 'todos' OR f.insumoEspecificacao like :insumoEspecificacao) "
            + " AND (f.estoqueUsina > 0 OR f.itemSolicitado = 0) "
            + " AND (:solicId2 = 'todos' OR f.solicitacaoCompraIDSis LIKE :solicId) "
            + " AND (:solicMaiorEstoque = 'todos' OR f.quantidadeSolicitada <= f.estoqueAtual) "),
    /**/
    @NamedQuery(name = "FollowUpSolicitacoes.findBySolicitacao",
            query = " SELECT DISTINCT f FROM FollowUpSolicitacoes f "
            + " WHERE (f.solicitacaoCompraItem.solicitacao = :solicitacaoCompra) "),
    /**/
    @NamedQuery(name = "FollowUpSolicitacoes.findBySolicItem",
            query = " SELECT DISTINCT f FROM FollowUpSolicitacoes f "
            + " WHERE (f.solicitacaoCompraItem = :solicitacaoCompraItem) ")
})
public class FollowUpSolicitacoes implements EntityInterface<FollowUpSolicitacoes> {

    @Id
    @OneToOne(targetEntity = SolicitacaoCompraItem.class, fetch = FetchType.EAGER)
    @JoinColumns(value = {
        @JoinColumn(name = "Solic_Numero", referencedColumnName = "Solic_Numero"),
        @JoinColumn(name = "SolicItem_Numero", referencedColumnName = "SolicItem_Numero")
    })
    private SolicitacaoCompraItem solicitacaoCompraItem;
    /*
     */
    @Id
    @Column(name = "Insumo_Cod")
    private Long insumoCod;
    /*
     */
    @Id
    @ManyToOne(targetEntity = CentroCusto.class, fetch = FetchType.EAGER)
    @JoinColumns(value = {
        @JoinColumn(name = "Centro_Cod", referencedColumnName = "Centro_Cod"),
        @JoinColumn(name = "Empresa_Cod", referencedColumnName = "Empresa_Cod"),
        @JoinColumn(name = "Filial_Cod", referencedColumnName = "Filial_Cod")})
    private CentroCusto centro;
    /*
     */
    @Id
    @Column(name = "Item_Solicitado")
    private Integer itemSolicitado;
    /*
     */
    @Column(name = "SolicItem_Obs")
    private String solicitacaoItemObservacao;
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
    @Column(name = "SubInsumo_Cod", insertable = false, updatable = false)
    private Integer insumoSubCod;
    /*
     */
    @Column(name = "SolicItem_DataEntrega")
    @Temporal(TemporalType.DATE)
    private Date solicitacaoDataEntrega;
    /*
     */
    @Transient
    private EnumAutorizacao autorizacao;
    /*
     */
    @ManyToOne(targetEntity = Insumo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "Insumo", nullable = false)
    private Insumo insumo;
    /*
     */
    @Column(name = "Insumo_Especificacao")
    private String insumoEspecificacao;
    /* 
     * Representa o ID composto N_Solic/N_Item Ex.: 021/01 
     */
    @Column(name = "Solic_ID")
    private String solicitacaoCompraID;
    /* 
     * Numero da solicitação no sistema (sem o item) 
     */
    @Column(name = "Solic_ID_Sistema")
    private Integer solicitacaoCompraIDSis;
    /*
     */
    @OneToOne(targetEntity = ColetaPreco.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "Coleta_Numero")
    private ColetaPreco coletaPreco;
    /*
     */
    @Column(name = "ColetaItem_Numero")
    private Integer coletaPrecoItemNumero;
    /*
     */
    @OneToOne(targetEntity = ColetaPrecoItem.class)
    @JoinColumns({
        @JoinColumn(name = "Coleta_Numero", referencedColumnName = "Coleta_Numero", insertable = false, updatable = false),
        @JoinColumn(name = "ColetaItem_Numero", referencedColumnName = "ColetaItem_Numero", insertable = false, updatable = false)
    })
    private ColetaPrecoItem coletaPrecoItem;
    /*
     */
    @OneToOne(targetEntity = Pedido.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "Pedido_Numero")
    private Pedido pedido;
    /*
     */
    @OneToOne(targetEntity = PedidoItem.class)
    @JoinColumns({
        @JoinColumn(name = "Pedido_Numero", referencedColumnName = "Pedido_Numero", insertable = false, updatable = false),
        @JoinColumn(name = "PedItem_Numero", referencedColumnName = "PedItem_Numero", insertable = false, updatable = false)
    })
    private PedidoItem pedidoItem;
    /*
     */
    @Column(name = "Pedido_ID")
    private String pedidoID;
    /*
     */
    @Column(name = "Pedido_ID_Sistema")
    private Integer pedidoIDSis;
    /*
     */
    @Column(name = "Situacao")
    @Enumerated(EnumType.STRING)
    private EnumSituacaoSolicitacao situacao;
    /*
     */
    @Column(name = "Unid_Cod")
    private String unidade;
    /*
     */
    @Column(name = "SolicItem_Quantidade")
    private Double quantidadeSolicitada;
    /*
     */
    @Column(name = "EntradaItem_QuantRec")
    private Double quantidadeRecebida;
    /*
     */
    @Column(name = "SolicItem_Saldo")
    private Double saldo;
    /*
     */
    @Column(name = "Estoque_Quantidade")
    private Double estoqueAtual;
    /*
     */
    @Column(name = "Estoque_Usina")
    private Double estoqueUsina;
    /*
     */
    @Column(name = "Insumo_ARealizar")
    private Double quantidadeOrcamento;
    /*
     */
    @Column(name = "Conclusao_Solicitacao")
    private Integer diasConclusaoSolicitacao;
    /*
     */
    @Column(name = "Autorizacao_Solicitacao")
    private Integer diasAutorizacaoSolicitacao;
    /*
     */
    @Column(name = "Coleta")
    private Integer diasColeta;
    /*
     */
    @Column(name = "Registro_Pedido")
    private Integer diasRegistroPedido;
    /*
     */
    @Column(name = "Autorizacao_Pedido")
    private Integer diasAutorizacaoPedido;
    /*
     */
    @Column(name = "Emissao_NF")
    private Integer diasEmissaoNF;
    /*
     */
    @Column(name = "Entrada_Material")
    private Integer diasEntradaMaterial;
    /*
     */
    @Column(name = "Pagamento")
    private Integer diasPagamento;
    /*
     */
    @Column(name = "Registro_AR")
    private Integer diasRegistroAR;
    /*
     */
    @Column(name = "Autorizacao_AR")
    private String diasAutorizacaoAR;
    /*
     */
    @Column(name = "Dt_Reg_Solic_Formatada")
    private String dataRegistroSolicitacaoFormatada;
    /*
     */
    @Column(name = "Data_Registro_Solicitacao")
    @Temporal(TemporalType.DATE)
    private Date dataRegistroSolicitacao;
    /*
     */
    @Column(name = "Data_Conclusao_Solicitacao")
    @Temporal(TemporalType.DATE)
    private Date dataConclusaoSolicitacao;
    /*
     */
    @Column(name = "Data_Autorizacao_Solicitacao")
    @Temporal(TemporalType.DATE)
    private Date dataAutorizacaoSolicitacao;
    /*
     */
    @Column(name = "Data_Registro_Coleta")
    @Temporal(TemporalType.DATE)
    private Date dataRegistroColeta;
    /*
     */
    @Column(name = "Data_Conclusao_Coleta")
    @Temporal(TemporalType.DATE)
    private Date dataConclusaoColeta;
    /*
     */
    @Column(name = "Data_Autorizacao_Coleta")
    @Temporal(TemporalType.DATE)
    private Date dataAutorizacaoColeta;
    /*
     */
    @Column(name = "Data_Registro_Pedido")
    @Temporal(TemporalType.DATE)
    private Date dataRegistroPedido;
    /*
     */
    @Column(name = "Data_Autorizacao_Pedido")
    @Temporal(TemporalType.DATE)
    private Date dataAutorizacaoPedido;
    /*
     */
    @Column(name = "Parcelas_a_Pagar")
    private Integer quantidadeParcelasTotal;
    /*
     */
    @Column(name = "Parcelas_Pagas")
    private Integer quantidadeParcelasPagas;
    /*
     */
    @Column(name = "Parcial")
    private Integer pedidoParcial;
    /*
     */
    @Column(name = "Estoque_AnoMes")
    private String estoqueAnoMes;
    /*
     */
    @ManyToOne(targetEntity = MateriaisEstoque.class, fetch = FetchType.EAGER)
    @JoinColumns({
        @JoinColumn(name = "Empresa_Cod", referencedColumnName = "Empresa_Cod", insertable = false, updatable = false),
        @JoinColumn(name = "Filial_Cod", referencedColumnName = "Filial_Cod", insertable = false, updatable = false),
        @JoinColumn(name = "Centro_Cod", referencedColumnName = "Centro_Cod", insertable = false, updatable = false),
        @JoinColumn(name = "Estoque_AnoMes", referencedColumnName = "Estoque_AnoMes", insertable = false, updatable = false),
        @JoinColumn(name = "Insumo_Cod", referencedColumnName = "Insumo_Cod", insertable = false, updatable = false),
        @JoinColumn(name = "SubInsumo_Cod", referencedColumnName = "SubInsumo_Cod", insertable = false, updatable = false)
    })
    private MateriaisEstoque materialEstoque;
    /*
     */
    @Transient
    private List<DocumentoEntrada> documentosEntrada;
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
    private MateriaisEstoque materiaisEstoqueInicial;
    /*
     */
    @Transient
    private MateriaisEstoque materiaisEstoqueFinal;
    /*
     */
    @Transient
    private List<MaterialEntradasESaidas> materialEntradasESaidas;
    /*
     */
    @Transient
    private List<TituloAPagar> titulosAPagar;
    /*
     */
    @Transient
    private boolean marcado;
    /*
     */

    public String getEstoqueAtualFmt() {
        if (estoqueAtual == null) {
            return "0";
        } else {
            return NumberUtils.formatDecimalNoFinalZero(estoqueAtual);
        }
    }

    public String getQuantidadeOrcamentoFmt() {
        if (quantidadeOrcamento == null) {
            return "0";
        } else {
            return NumberUtils.formatDecimalNoFinalZero(quantidadeOrcamento);
        }
    }

    @Override
    public Serializable getId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLabel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean verificarId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isMarcado() {
        return marcado;
    }

    public void setMarcado(boolean marcado) {
        this.marcado = marcado;
    }

    @Override
    public int compareTo(FollowUpSolicitacoes o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getSolicitacaoItemObservacao() {
        return solicitacaoItemObservacao;
    }

    public void setSolicitacaoItemObservacao(String solicitacaoItemObservacao) {
        this.solicitacaoItemObservacao = solicitacaoItemObservacao;
    }

    public Date getSolicitacaoDataEntrega() {
        return solicitacaoDataEntrega;
    }

    public void setSolicitacaoDataEntrega(Date solicitacaoDataEntrega) {
        this.solicitacaoDataEntrega = solicitacaoDataEntrega;
    }

    public CentroCusto getCentro() {
        return centro;
    }

    public void setCentro(CentroCusto centro) {
        this.centro = centro;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public InsumoSub getInsumoSub() {
        return insumoSub;
    }

    public void setInsumoSub(InsumoSub insumoSub) {
        this.insumoSub = insumoSub;
    }

    public Integer getInsumoSubCod() {
        return insumoSubCod;
    }

    public void setInsumoSubCod(Integer insumoSubCod) {
        this.insumoSubCod = insumoSubCod;
    }

    public String getInsumoEspecificacao() {
        return insumoEspecificacao;
    }

    public void setInsumoEspecificacao(String insumoEspecificacao) {
        this.insumoEspecificacao = insumoEspecificacao;
    }

    public String getSolicitacaoCompraID() {
        return solicitacaoCompraID;
    }

    public void setSolicitacaoCompraID(String solicitacaoCompraID) {
        this.solicitacaoCompraID = solicitacaoCompraID;
    }

    public Integer getSolicitacaoCompraIDSis() {
        return solicitacaoCompraIDSis;
    }

    public void setSolicitacaoCompraIDSis(Integer solicitacaoCompraIDSis) {
        this.solicitacaoCompraIDSis = solicitacaoCompraIDSis;
    }

    public Integer getColetaPrecoItemNumero() {
        return coletaPrecoItemNumero;
    }

    public void setColetaPrecoItemNumero(Integer coletaPrecoItemNumero) {
        this.coletaPrecoItemNumero = coletaPrecoItemNumero;
    }

    public ColetaPreco getColetaPreco() {
        return coletaPreco;
    }

    public void setColetaPreco(ColetaPreco coletaPreco) {
        this.coletaPreco = coletaPreco;
    }

    public ColetaPrecoItem getColetaPrecoItem() {
        return coletaPrecoItem;
    }

    public void setColetaPrecoItem(ColetaPrecoItem coletaPrecoItem) {
        this.coletaPrecoItem = coletaPrecoItem;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public PedidoItem getPedidoItem() {
        return pedidoItem;
    }

    public void setPedidoItem(PedidoItem pedidoItem) {
        this.pedidoItem = pedidoItem;
    }

    public String getPedidoID() {
        return pedidoID;
    }

    public void setPedidoID(String pedidoID) {
        this.pedidoID = pedidoID;
    }

    public Integer getPedidoIDSis() {
        return pedidoIDSis;
    }

    public void setPedidoIDSis(Integer pedidoIDSis) {
        this.pedidoIDSis = pedidoIDSis;
    }

    public EnumSituacaoSolicitacao getSituacao() {
        return situacao;
    }

    public void setSituacao(EnumSituacaoSolicitacao situacao) {
        this.situacao = situacao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Double getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public void setQuantidadeSolicitada(Double quantidadeSolicitada) {
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public Double getQuantidadeRecebida() {
        return quantidadeRecebida;
    }

    public void setQuantidadeRecebida(Double quantidadeRecebida) {
        this.quantidadeRecebida = quantidadeRecebida;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Double getEstoqueAtual() {
        return estoqueAtual;
    }

    public void setEstoqueAtual(Double estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }

    public Double getEstoqueUsina() {
        return estoqueUsina;
    }

    public void setEstoqueUsina(Double estoqueUsina) {
        this.estoqueUsina = estoqueUsina;
    }

    public Integer getDiasConclusaoSolicitacao() {
        return diasConclusaoSolicitacao;
    }

    public void setDiasConclusaoSolicitacao(Integer diasConclusaoSolicitacao) {
        this.diasConclusaoSolicitacao = diasConclusaoSolicitacao;
    }

    public Integer getDiasAutorizacaoSolicitacao() {
        return diasAutorizacaoSolicitacao;
    }

    public void setDiasAutorizacaoSolicitacao(Integer diasAutorizacaoSolicitacao) {
        this.diasAutorizacaoSolicitacao = diasAutorizacaoSolicitacao;
    }

    public Integer getDiasColeta() {
        return diasColeta;
    }

    public void setDiasColeta(Integer diasColeta) {
        this.diasColeta = diasColeta;
    }

    public Integer getDiasRegistroPedido() {
        return diasRegistroPedido;
    }

    public void setDiasRegistroPedido(Integer diasRegistroPedido) {
        this.diasRegistroPedido = diasRegistroPedido;
    }

    public Integer getDiasAutorizacaoPedido() {
        return diasAutorizacaoPedido;
    }

    public void setDiasAutorizacaoPedido(Integer diasAutorizacaoPedido) {
        this.diasAutorizacaoPedido = diasAutorizacaoPedido;
    }

    public Integer getDiasRegistroAR() {
        return diasRegistroAR;
    }

    public void setDiasRegistroAR(Integer diasRegistroAR) {
        this.diasRegistroAR = diasRegistroAR;
    }

    public String getDiasAutorizacaoAR() {
        return diasAutorizacaoAR;
    }

    public void setDiasAutorizacaoAR(String diasAutorizacaoAR) {
        this.diasAutorizacaoAR = diasAutorizacaoAR;
    }

    public Date getDataRegistroSolicitacao() {
        return dataRegistroSolicitacao;
    }

    public void setDataRegistroSolicitacao(Date dataRegistroSolicitacao) {
        this.dataRegistroSolicitacao = dataRegistroSolicitacao;
    }

    public String getDataRegistroSolicitacaoFormatada() {
        return dataRegistroSolicitacaoFormatada;
    }

    public void setDataRegistroSolicitacaoFormatada(String dataRegistroSolicitacaoFormatada) {
        this.dataRegistroSolicitacaoFormatada = dataRegistroSolicitacaoFormatada;
    }

    public Date getDataConclusaoSolicitacao() {
        return dataConclusaoSolicitacao;
    }

    public void setDataConclusaoSolicitacao(Date dataConclusaoSolicitacao) {
        this.dataConclusaoSolicitacao = dataConclusaoSolicitacao;
    }

    public Date getDataAutorizacaoSolicitacao() {
        return dataAutorizacaoSolicitacao;
    }

    public void setDataAutorizacaoSolicitacao(Date dataAutorizacaoSolicitacao) {
        this.dataAutorizacaoSolicitacao = dataAutorizacaoSolicitacao;
    }

    public Date getDataRegistroColeta() {
        return dataRegistroColeta;
    }

    public void setDataRegistroColeta(Date dataRegistroColeta) {
        this.dataRegistroColeta = dataRegistroColeta;
    }

    public Date getDataConclusaoColeta() {
        return dataConclusaoColeta;
    }

    public void setDataConclusaoColeta(Date dataConclusaoColeta) {
        this.dataConclusaoColeta = dataConclusaoColeta;
    }

    public Date getDataAutorizacaoColeta() {
        return dataAutorizacaoColeta;
    }

    public void setDataAutorizacaoColeta(Date dataAutorizacaoColeta) {
        this.dataAutorizacaoColeta = dataAutorizacaoColeta;
    }

    public Date getDataRegistroPedido() {
        return dataRegistroPedido;
    }

    public void setDataRegistroPedido(Date dataRegistroPedido) {
        this.dataRegistroPedido = dataRegistroPedido;
    }

    public Date getDataAutorizacaoPedido() {
        return dataAutorizacaoPedido;
    }

    public void setDataAutorizacaoPedido(Date dataAutorizacaoPedido) {
        this.dataAutorizacaoPedido = dataAutorizacaoPedido;
    }

    public Integer getQuantidadeParcelasTotal() {
        return quantidadeParcelasTotal;
    }

    public void setQuantidadeParcelasTotal(Integer quantidadeParcelasTotal) {
        this.quantidadeParcelasTotal = quantidadeParcelasTotal;
    }

    public Integer getQuantidadeParcelasPagas() {
        return quantidadeParcelasPagas;
    }

    public void setQuantidadeParcelasPagas(Integer quantidadeParcelasPagas) {
        this.quantidadeParcelasPagas = quantidadeParcelasPagas;
    }

    public Integer getPedidoParcial() {
        return pedidoParcial;
    }

    public void setPedidoParcial(Integer pedidoParcial) {
        this.pedidoParcial = pedidoParcial;
    }

    public String getEstoqueAnoMes() {
        return estoqueAnoMes;
    }

    public void setEstoqueAnoMes(String estoqueAnoMes) {
        this.estoqueAnoMes = estoqueAnoMes;
    }

    public Long getInsumoCod() {
        return insumoCod;
    }

    public void setInsumoCod(Long insumoCod) {
        this.insumoCod = insumoCod;
    }

    public EnumAutorizacao getAutorizacao() {
        return autorizacao;
    }

    public void setAutorizacao(EnumAutorizacao autorizacao) {
        this.autorizacao = autorizacao;
    }

    public SolicitacaoCompraItem getSolicitacaoCompraItem() {
        return solicitacaoCompraItem;
    }

    public void setSolicitacaoCompraItem(SolicitacaoCompraItem solicitacaoCompraItem) {
        this.solicitacaoCompraItem = solicitacaoCompraItem;
    }

    public Integer getItemSolicitado() {
        return itemSolicitado;
    }

    public void setItemSolicitado(Integer ItemSolicitado) {
        this.itemSolicitado = ItemSolicitado;
    }

    public Double getQuantidadeOrcamento() {
        return quantidadeOrcamento;
    }

    public void setQuantidadeOrcamento(Double quantidadeOrcamento) {
        this.quantidadeOrcamento = quantidadeOrcamento;
    }

    public Integer getDiasEmissaoNF() {
        return diasEmissaoNF;
    }

    public void setDiasEmissaoNF(Integer diasEmissaoNF) {
        this.diasEmissaoNF = diasEmissaoNF;
    }

    public Integer getDiasEntradaMaterial() {
        return diasEntradaMaterial;
    }

    public void setDiasEntradaMaterial(Integer diasEntradaMaterial) {
        this.diasEntradaMaterial = diasEntradaMaterial;
    }

    public Integer getDiasPagamento() {
        return diasPagamento;
    }

    public void setDiasPagamento(Integer diasPagamento) {
        this.diasPagamento = diasPagamento;
    }

    public List<MaterialEntradaItens> getMaterialEntradaItens() {
        return materialEntradaItens;
    }

    public void setMaterialEntradaItens(List<MaterialEntradaItens> materialEntradaItens) {
        this.materialEntradaItens = materialEntradaItens;
    }

    public MateriaisEstoque getMaterialEstoque() {
        return materialEstoque;
    }

    public void setMaterialEstoque(MateriaisEstoque materialEstoque) {
        this.materialEstoque = materialEstoque;
    }

    public List<DocumentoEntrada> getDocumentosEntrada() {
        return documentosEntrada;
    }

    public void setDocumentosEntrada(List<DocumentoEntrada> documentosEntrada) {
        this.documentosEntrada = documentosEntrada;
    }

    public List<MaterialSaidaItens> getMaterialSaidaItens() {
        return materialSaidaItens;
    }

    public void setMaterialSaidaItens(List<MaterialSaidaItens> materialSaidaItens) {
        this.materialSaidaItens = materialSaidaItens;
    }

    public MateriaisEstoque getMateriaisEstoqueInicial() {
        return materiaisEstoqueInicial;
    }

    public void setMateriaisEstoqueInicial(MateriaisEstoque materiaisEstoqueInicial) {
        this.materiaisEstoqueInicial = materiaisEstoqueInicial;
    }

    public MateriaisEstoque getMateriaisEstoqueFinal() {
        return materiaisEstoqueFinal;
    }

    public void setMateriaisEstoqueFinal(MateriaisEstoque materiaisEstoqueFinal) {
        this.materiaisEstoqueFinal = materiaisEstoqueFinal;
    }

    public List<MaterialEntradasESaidas> getMaterialEntradasESaidas() {
        return materialEntradasESaidas;
    }

    public void setMaterialEntradasESaidas(List<MaterialEntradasESaidas> materialEntradasESaidas) {
        this.materialEntradasESaidas = materialEntradasESaidas;
    }

    public List<TituloAPagar> getTitulosAPagar() {
        return titulosAPagar;
    }

    public void setTitulosAPagar(List<TituloAPagar> titulosAPagar) {
        this.titulosAPagar = titulosAPagar;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.solicitacaoCompraItem);
        hash = 83 * hash + Objects.hashCode(this.insumoCod);
        hash = 83 * hash + Objects.hashCode(this.centro);
        hash = 83 * hash + Objects.hashCode(this.itemSolicitado);
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
        final FollowUpSolicitacoes other = (FollowUpSolicitacoes) obj;
        if (!Objects.equals(this.solicitacaoCompraItem, other.solicitacaoCompraItem)) {
            return false;
        }
        if (!Objects.equals(this.insumoCod, other.insumoCod)) {
            return false;
        }
        if (!Objects.equals(this.centro, other.centro)) {
            return false;
        }
        if (!Objects.equals(this.itemSolicitado, other.itemSolicitado)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FollowUpSolicitacoes{" + "insumo=" + insumo + ", item=" + solicitacaoCompraIDSis + '}';
    }
}
