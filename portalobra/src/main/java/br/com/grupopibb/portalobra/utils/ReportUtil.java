/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.utils;

import br.com.grupopibb.portalobra.acesso.ConnectionFactory;
import br.com.grupopibb.portalobra.business.pedido.QualidadeBusiness;
import br.com.grupopibb.portalobra.model.ar.DocumentoEntradaAvaliacao;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.geral.Credor;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaida;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaidaItens;
import br.com.grupopibb.portalobra.model.pedido.Pedido;
import br.com.grupopibb.portalobra.model.pedido.PedidoItem;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import org.apache.log4j.Logger;

/**
 *
 * @author arthur.diego
 */
@Stateless
@LocalBean
public class ReportUtil {

    JasperPrint print;
    @EJB
    private ConnectionFactory connFactory;
    private Logger logger = Logger.getLogger(net.sf.jasperreports.extensions.ExtensionsEnvironment.class);
    @EJB
    private QualidadeBusiness qualiBusiness;

    /**
     * Metodo reponsavel pela construção do objeto print(Jasper Print) e com as
     * informações de como será exportado para pdf
     *
     * @param inputStream local do arquivo jasper
     * @param parametros paremetros que o relatório precise ou não para poder
     * ser gerado
     * @param dataSource fonte de dados no qual o relatório será preenchido
     * nesse caso um JRDataSource
     */
    private void openPDFDataSource(InputStream inputStream,
            Map parametros,
            JRDataSource dataSource) {

        ServletOutputStream responseStream = null;
        try {
            this.print = JasperFillManager.fillReport(inputStream, parametros, dataSource);
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.addHeader("Content-disposition", "inline;filename=relatorio.pdf");
            response.setHeader("RELATÓRIO", "public");
            response.setContentType("application/pdf");
            responseStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(print, responseStream);
            FacesContext.getCurrentInstance().responseComplete();
          
        } catch (IOException | JRException ex) {
            //Logger.getLogger(ReportUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                responseStream.close();
            } catch (IOException ex) {
                // Logger.getLogger(ReportUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Metodo reponsavel pela construção do objeto print(Jasper Print) e com as
     * informações de como será exportado para pdf
     *
     * @param inputStream local do arquivo jasper
     * @param parametros paremetros que o relatório precise ou não para poder
     * ser gerado
     * @param conn fonte de dados no qual o relatório será preenchido nesse caso
     * uma Conexão com o Banco
     */
    private void openPDFConnection(InputStream inputStream,
            Map parametros,
            Connection conn) {

        ServletOutputStream responseStream = null;
        try {
            this.print = JasperFillManager.fillReport(inputStream, parametros, conn);
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.addHeader("Content-disposition", "inline;filename=relatorio.pdf");
            response.setContentType("application/pdf");
            responseStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(print, responseStream);
            FacesContext.getCurrentInstance().responseComplete();
            
        } catch (IOException | JRException ex) {
            // Logger.getLogger(ReportUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                responseStream.close();
            } catch (IOException ex) {
                //  Logger.getLogger(ReportUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Metodo reponsavel pela construção do objeto print(Jasper Print) e com as
     * informações de como será exportado para excel
     *
     * @param inputStream local do arquivo jasper
     * @param parametros paremetros que o relatório precise ou não para poder
     * ser gerado
     * @param conn fonte de dados no qual o relatório será preenchido nesse caso
     * um JRDataSource
     */
    private void exportExcel(InputStream inputStream,
            Map parametros,
            Connection conn) {
        ServletOutputStream responseStream = null;
        try {

            this.print = JasperFillManager.fillReport(inputStream, parametros, conn);
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.addHeader("Content-disposition", "attachment; filename=relatorio.xlsx");
            responseStream = response.getOutputStream();
            JRXlsxExporter docxExporter = new JRXlsxExporter();
            docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            docxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, responseStream);
            docxExporter.exportReport();
            FacesContext.getCurrentInstance().responseComplete();

        } catch (JRException | IOException ex) {
            // Logger.getLogger(ReportUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Metodo que passa os paremetros corretos e chama o metodo exportExcel para
     * que seja gerado o relatório apartir das informações inseridas
     *
     * @param insSub código do insumo (String)
     * @param centro objeto Centro no qual podemos acessar seus atributos e
     * passar como parametros para o MAP
     */
    public void exportReportExcel(String insSub, CentroCusto centro) {

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/UltimasSolicitacoes.jasper");
        URL image = getClass().getResource("/relatorios/");
        
        Map parametros = new HashMap();
        parametros.put("emp_cod", centro.getEmpresaCod() + "");
        parametros.put("centro_cod", centro.getCodigo() + "");
        parametros.put("fil_cod", centro.getFilialCod() + "");
        parametros.put("cod_insumo", insSub);
        parametros.put("IMAGE_DIR", image.toString());

        this.exportExcel(inputStream, parametros, connFactory.getConnection());

    }

    public void abrirRelatorioPedidosDS(Pedido pe) {
        InputStream inputStream = getClass().getResourceAsStream("/relatorios/layouPedido.jasper");
        URL image = getClass().getResource("/relatorios/");
        
        Map parametros = new HashMap();
        parametros.put("emp_nome", pe.getCentroCusto().getEmpresa().getNome() + "");
        parametros.put("numeroPedido", pe.getIdSistema() + "");
        parametros.put("campo_Fornecedor", pe.getCredor().getRazaoSocial() + "");
        parametros.put("ender_fornec", pe.getCredor().getEndereco() + "");
        parametros.put("bairro_fornec", pe.getCredor().getBairro() + "");
        parametros.put("cidadeUf_fornec", pe.getCredor().getCidade() + "/" + pe.getCredor().getUf() + "");
        parametros.put("cep_fornec", pe.getCredor().getCep() + "");
        parametros.put("cnpj_fornec", pe.getCredor().getCpfCnpj() + "");
        parametros.put("contato_fornec", pe.getCredor().getContato() + "");
        parametros.put("fone_fornec", pe.getCredor().getTelefone() + "");
        parametros.put("email_fornec", pe.getCredor().getEmail() + "");
        parametros.put("emresa_faturamento", pe.getCentroCusto().getFilial().getNome() + "");
        parametros.put("ender_emp",
                pe.getCentroCusto().getFilial().getEndereco()
                + ", "
                + pe.getCentroCusto().getFilial().getNumero()
                + " - "
                + pe.getCentroCusto().getFilial().getComplemento() + "");
        parametros.put("bairro_emp", pe.getCentroCusto().getFilial().getBairro() + "");
        parametros.put("cidadeUf_emp",
                pe.getCentroCusto().getFilial().getCidade() + "/"
                + pe.getCentroCusto().getFilial().getUf() + "");
        parametros.put("cep_emp", pe.getCentroCusto().getFilial().getCep() + "");
        parametros.put("insc_emp", pe.getCentroCusto().getFilial().getInscricaoEstadual() + "");
        parametros.put("cnpj_emp", pe.getCentroCusto().getFilial().getCnpj() + "");
        parametros.put("contato_emp", pe.getCentroCusto().getContatoAlmoxarifado() + "");
        parametros.put("fone_emp", pe.getCentroCusto().getTelefoneAlmoxarifado() + "");
        parametros.put("email_emp", pe.getCentroCusto().getEmailAlmoxarifado() + "");
        parametros.put("condicoes_pagamento", pe.getFormaPagamento() + "");
        parametros.put("observacao", pe.getObservacao() + "");
        parametros.put("data_entrega", DateUtils.getFormatter("dd/MM/yyyy").format(pe.getDataPrevisaoEntrega()) + "");
        parametros.put("data_aprovacao", DateUtils.getFormatter("dd/MM/yyyy").format(pe.getDataAutorizacao()) + "");
        parametros.put("valor_pedido", String.format("%.2f", pe.getValorTotalComIPI()));
        parametros.put("desconto", String.format("%.2f", pe.getDescontoPedido()));
        parametros.put("total_pedido", String.format("%.2f", pe.getValorComDesconto()));


        List<DocumentoEntradaAvaliacao> itensAvaliacao = null;

        DocumentoEntradaAvaliacao pontualidade = null;
        DocumentoEntradaAvaliacao especPedido = null;
        DocumentoEntradaAvaliacao qualidadeIntegridade = null;
        DocumentoEntradaAvaliacao dadosNf = null;
        DocumentoEntradaAvaliacao qualidadeIntrinseca = null;
        DocumentoEntradaAvaliacao requisitosComerciais = null;
        DocumentoEntradaAvaliacao requisitosSgq = null;

        try {
            itensAvaliacao = pe.getItens().get(0).getDocumentoEntradaItem().getDocumentoEntrada().getItensAvaliacao();
            pontualidade = qualiBusiness.getPontualidade(itensAvaliacao);
            especPedido = qualiBusiness.getEspecPedido(itensAvaliacao);
            qualidadeIntegridade = qualiBusiness.getQualidadeIntegridade(itensAvaliacao);
            dadosNf = qualiBusiness.getDadosNf(itensAvaliacao);
            qualidadeIntrinseca = qualiBusiness.getQualidadeIntrinseca(itensAvaliacao);
            requisitosComerciais = qualiBusiness.getRequisitosComerciais(itensAvaliacao);
            requisitosSgq = qualiBusiness.getRequisitosSgq(itensAvaliacao);
        } catch (NullPointerException e) {
        }


        //Pontuacao
        parametros.put("pont_pont", pontualidade == null ? null : pontualidade.getPontuacao() + "");
        parametros.put("pont_espec", especPedido == null ? null : especPedido.getPontuacao() + "");
        parametros.put("pont_qualid", qualidadeIntegridade == null ? null : qualidadeIntegridade.getPontuacao() + "");
        parametros.put("pont_dados", dadosNf == null ? null : dadosNf.getPontuacao() + "");
        parametros.put("pont_qualid_intrin", qualidadeIntrinseca == null ? null : qualidadeIntrinseca.getPontuacao() + "");
        parametros.put("pont_req_comerc", requisitosComerciais == null ? null : requisitosComerciais.getPontuacao() + "");
        parametros.put("pont_req_SGQ", requisitosSgq == null ? null : requisitosSgq.getPontuacao() + "");
        //Observacao
        parametros.put("obs_pont", pontualidade == null ? null : pontualidade.getObservacao() + "");
        parametros.put("obs_espec", especPedido == null ? null : especPedido.getObservacao() + "");
        parametros.put("obs_qualid", qualidadeIntegridade == null ? null : qualidadeIntegridade.getObservacao() + "");
        parametros.put("obs_dados", dadosNf == null ? null : dadosNf.getObservacao() + "");
        parametros.put("obs_qualid_intrin", qualidadeIntrinseca == null ? null : qualidadeIntrinseca.getObservacao() + "");
        parametros.put("obs_req_comerc", requisitosComerciais == null ? null : requisitosComerciais.getObservacao() + "");
        parametros.put("obs_req_SGQ", requisitosSgq == null ? null : requisitosSgq.getObservacao() + "");
        parametros.put("IMAGE_DIR", image.toString());

        List<PedidoItem> dados = pe.getItens();
        List<ListaPedidos> listPedidos = new ArrayList<>();
        /*
         * For para preencher a banda detail com os dados da consulta do JPA
         */
        for (int i = 0; i < dados.size(); i++) {

            ListaPedidos p = new ListaPedidos();

            p.setItemItem(dados.get(i).getItemItem());
            p.setIdSistema(String.valueOf(dados.get(i).getSolicitacaoCompraItem().getSolicitacao().getIdSistema()));
            p.setInsumoCod(String.valueOf(dados.get(i).getSolicitacaoCompraItem().getInsumoSub().getInsumoCod()));
            p.setEspecificaoObsersavao("" + dados.get(i).getSolicitacaoCompraItem().getEspecificacaoCompleta() + " - " + dados.get(i).getSolicitacaoCompraItem().getObservacao());
            p.setCodigo(String.valueOf(dados.get(i).getSolicitacaoCompraItem().getInsumoSub().getInsumo().getUnidade().getCodigo()));
            p.setQuantidade(String.valueOf(dados.get(i).getQuantidade()));
            p.setPrecoInsumo(String.valueOf(dados.get(i).getPrecoInsumo()));
            p.setAliqIpi(String.valueOf(dados.get(i).getAliqIpi()));
            p.setValorTotalItemComIPI(String.valueOf(dados.get(i).getValorTotalItemComIPI()));
            listPedidos.add(p);
        }
        JRDataSource ds = new JRBeanCollectionDataSource(listPedidos);

        this.openPDFDataSource(inputStream, parametros, ds);

    }

    public void abrirRelatorioUltimasSolicitacoes(String insSub, CentroCusto centro) {

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/UltimasSolicitacoes.jasper");
        URL image = getClass().getResource("/relatorios/");
        
        Map parametros = new HashMap();
        parametros.put("emp_cod", centro == null ? null : centro.getEmpresaCod() + "");
        parametros.put("centro_cod", centro == null ? null : centro.getCodigo() + "");
        parametros.put("fil_cod", centro == null ? null : centro.getFilialCod() + "");
        parametros.put("cod_insumo", insSub);
        parametros.put("IMAGE_DIR", image.toString());
        
        this.openPDFConnection(inputStream, parametros, connFactory.getConnection());

    }

    public void abrirRelatorioEstoqueMaterais(Date data) {
        InputStream inputStream = getClass().getResourceAsStream("/relatorios/estoque/MateriaisEstoque.jasper");
        URL image = getClass().getResource("/relatorios/");
        
        Map parametros = new HashMap();
        parametros.put("mes_ano", DateUtils.getFormatter("yyyy/MM").format(data).replace("/", "") + "");
        parametros.put("IMAGE_DIR", image.toString());
        
        this.openPDFConnection(inputStream, parametros, connFactory.getConnection());
    }
    
    public void abrirRelatorioEstoqueMateraisPorCentro(Date data, CentroCusto centro) {
        InputStream inputStream = getClass().getResourceAsStream("/relatorios/estoque/materiais_estoque_por_centro.jasper");
        URL image = getClass().getResource("/relatorios/");
        
        Map parametros = new HashMap();
        parametros.put("mes_ano", DateUtils.getFormatter("yyyy/MM").format(data).replace("/", "") + "");
        parametros.put("Empresa_Cod", centro == null ? null : centro.getEmpresaCod());
        parametros.put("Filial_Cod", centro == null ? null : centro.getFilialCod());
        parametros.put("Centro_Cod", centro == null ? null : centro.getCodigo());
        parametros.put("IMAGE_DIR", image.toString());

        this.openPDFConnection(inputStream, parametros, connFactory.getConnection());
    }

    public void abrirRelatorioAutorizacao(MaterialSaida m) {

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/AutorizacaoMateriais.jasper");
        URL image = getClass().getResource("/relatorios/");
        
        List<MaterialSaidaItens> listMatItens = m.getItens();
        List<ListaAutorizacao> listAutorizacao = new ArrayList<>();

        Map parametros = new HashMap();
        parametros.put("centroNome", m.getCentro().getNomeCompleto());
        parametros.put("centroDestinoNome", m.getCentroDestino().getNomeCompleto());
        parametros.put("preencheZeroEsquerda", NumberUtils.preencheZeroEsquerda(String.valueOf(m.getNumeroDeposito()), 6) + "");
        parametros.put("dataSaida", DateUtils.getDataFormatada("dd/MM/yyyy HH.mm.ss", m.getDataSaida()));
        parametros.put("numeroDocumento", m.getNumeroDocumento());
        parametros.put("custoTotal", String.format("%.2f", m.getCustoTotal()));
        parametros.put("IMAGE_DIR", image.toString());
         
        for (int i = 0; i < listMatItens.size(); i++) {
            ListaAutorizacao a = new ListaAutorizacao();
            a.setCodigo(NumberUtils.preencheZeroEsquerda(listMatItens.get(i).getInsumoSub().getIdCompleto(), 5));
            a.setDiscriminacao(listMatItens.get(i).getInsumoSub().getEspecificacao());
            a.setUnidade(listMatItens.get(i).getInsumoSub().getInsumo().getUnidade().getCodigo());
            a.setQuantidade(listMatItens.get(i).getQuantidade() + "");
            a.setPrecoUnitAbrev(NumberUtils.divide(listMatItens.get(i).getValor(), listMatItens.get(i).getQuantidade()) + "");
            a.setCusto(listMatItens.get(i).getValor() + "");
            listAutorizacao.add(a);

        }
        JRDataSource ds = new JRBeanCollectionDataSource(listAutorizacao);
        this.openPDFDataSource(inputStream, parametros, ds);
    }

    public void abrirRelatorioPosicaoPedidos(Pedido p, Credor c, CentroCusto cc) {

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/Posicao_Pedidos/Posicao_Pedidos_Suprimentos.jasper");
        URL web = getClass().getResource("/relatorios/Posicao_Pedidos/");
        URL image = getClass().getResource("/relatorios/");
        
        Map parametros = new HashMap();
        
        long pedidoNumero = 0;
        
        parametros.put("Empresa_Cod", cc == null ? null : cc.getEmpresaCod());
        parametros.put("Filial_Cod", cc == null ? null : cc.getFilialCod());
        parametros.put("Centro_Cod", cc == null ? null : cc.getCodigo());
        for (int i = 0; i < c.getPedido().size(); i++) {
            if (p.getIdSistema().intValue() == c.getPedido().get(i).getIdSistema().intValue()) {
                pedidoNumero = c.getPedido().get(i).getNumero();
            }
        }
        parametros.put("Pedido_Numero", pedidoNumero == 0 ? "" : pedidoNumero+"");
        parametros.put("Cre_CGCCPF", c == null ? null : c.getCpfCnpj());
        parametros.put("Emp_Nome", cc.getEmpresa().getNome() + "");
        parametros.put("Pedido_CentroNumero", p == null ? null : p.getIdSistema() + "");
        parametros.put("Cre_Cod_RazSoc", c == null ? null : c.getLabelReport() + "");
        parametros.put("Centro_Nome", c == null ? null : cc.getNome() + "");
        parametros.put("SUBREPORT_DIR", web.toString());
        parametros.put("REPORT_CONNECTION", connFactory.getConnection());
        parametros.put("IMAGE_DIR", image.toString());
        
        this.openPDFConnection(inputStream, parametros, connFactory.getConnection());

    }

    public void abrirRelatorioPosicaoContrato(Pedido p, Credor c, CentroCusto cc) {

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/Posicao_Contratos/1_Master_Contratos.jasper");
        URL web = getClass().getResource("/relatorios/Posicao_Contratos/");
        URL image = getClass().getResource("/relatorios/");
        
        Map parametros = new HashMap();
        parametros.put("Empresa_Cod", cc == null ? null : cc.getEmpresaCod());
        parametros.put("Filial_Cod", cc == null ? null : cc.getFilialCod());
        parametros.put("Centro_Cod", cc == null ? null : cc.getCodigo());
        for (int i = 0; i < c.getPedido().size(); i++) {
            if (p.getIdSistema().intValue() == c.getPedido().get(i).getIdSistema().intValue()) {
                parametros.put(p == null ? null : "Pedido_Numero", c.getPedido().get(i).getNumero() + "");
            }
        }
        parametros.put("Cre_CGCCPF", c == null ? null : c.getCpfCnpj());
        parametros.put("Emp_Nome", cc.getEmpresa().getNome() + "");
        parametros.put("Pedido_CentroNumero", p == null ? null : p.getIdSistema() + "");
        parametros.put("Cre_Cod_RazSoc", c == null ? null : c.getLabelReport() + "");
        parametros.put("Centro_Nome", c == null ? null : cc.getNome() + "");
        parametros.put("SUBREPORT_DIR", web.toString());
        parametros.put("REPORT_CONNECTION", connFactory.getConnection());
        parametros.put("IMAGE_DIR", image.toString());
        
        this.openPDFConnection(inputStream, parametros, connFactory.getConnection());

    }
}
