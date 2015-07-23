/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.business.materiais;

import br.com.grupopibb.portalobra.dao.materiais.MateriaisEstoqueFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialEntradaItensFacade;
import br.com.grupopibb.portalobra.dao.materiais.MaterialSaidaItensFacade;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.model.materiais.MateriaisEstoque;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntrada;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntradaItens;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntradasESaidas;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaida;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaidaItens;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.UtilBeans;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author administrator
 */
@Stateless
@LocalBean
public class EstoqueBusiness {

    @EJB
    MateriaisEstoqueFacade materiaisEstoqueFacade;
    @EJB
    MaterialEntradaItensFacade materialEntradaItensFacade;
    @EJB
    MaterialSaidaItensFacade materialSaidaItensFacade;
    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }


    /**
     * Atualiza os dados de estoque que são exibidos no FollowUp.
     *
     * @param centro
     * @param insumoCod
     * @throws SQLException
     */
    public void atualizaEstoqueFollowUp(CentroCusto centro, InsumoSub insumoSub) throws SQLException {

        Query q = getEntityManager().createNativeQuery("exec sp_PO_UpdateEstoqueFollowUp ?,?,?,?,?,?,?");
        q.setParameter(1, centro.getEmpresaCod());
        q.setParameter(2, centro.getFilialCod());
        q.setParameter(3, centro.getCodigo());
        q.setParameter(4, null);
        q.setParameter(5, null);
        q.setParameter(6, insumoSub.getInsumoCod());
        q.setParameter(7, insumoSub.getCodigo());
        q.executeUpdate();
        q = null;
    }

    /**
     * Atualiza apenas o saldo de movimentações do material atual ou do centro
     * de custo completo no mês atual.
     *
     * @param centroOrigem
     * @param centroDestino
     * @param dataRef
     * @param insumoCod
     * @throws SQLException
     */
    public void atualizaSaldoMaterial(CentroCusto centroOrigem, Date dataRef, InsumoSub insumoSub) throws SQLException {
        try {
            Date dataInicial = DateUtils.toFirstDate(dataRef);
            Date dataFinal = DateUtils.toLastDate(dataRef);

            Query q = getEntityManager().createNativeQuery("exec PO_spAtualizaEstoque ?,?,?,?,?,?,?");
            q.setParameter(1, centroOrigem.getEmpresaCod());
            q.setParameter(2, centroOrigem.getFilialCod());
            q.setParameter(3, centroOrigem.getCodigo());
            q.setParameter(4, new java.sql.Date(dataInicial.getTime()));
            q.setParameter(5, new java.sql.Date(dataFinal.getTime()));
            q.setParameter(6, insumoSub == null ? null : insumoSub.getInsumoCod());
            q.setParameter(7, insumoSub == null ? null : insumoSub.getCodigo());
            q.executeUpdate();
            q = null;
        } catch (RuntimeException e) {
        }
    }

    /**
     * Atualiza o saldo de material completo de um determinado Centro de Custo.
     *
     * @param centroOrigem
     * @param centroDestino
     * @throws SQLException
     */
    public void atualizaSaldoMaterialTodosMeses(CentroCusto centroOrigem) throws SQLException {
        Query q = getEntityManager().createNativeQuery("exec PO_spAtualizaEstoque_TodosMeses ?,?,?,?");
        q.setParameter(1, centroOrigem.getEmpresaCod());
        q.setParameter(2, centroOrigem.getFilialCod());
        q.setParameter(3, centroOrigem.getCodigo());
        q.setParameter(4, null);
        q.executeUpdate();
        q = null;
    }

    /**
     * Recebe uma lista de itens de saida e atualiza o estoque de cada item.
     *
     * @param itens
     * @param centroSelecionado
     * @param materialSaida
     */
    public void atualizaEstoqueItensSaida(List<MaterialSaidaItens> itens, CentroCusto centroSelecionado, MaterialSaida materialSaida) {
        for (MaterialSaidaItens item : itens) {
            try {
                atualizaSaldoMaterial(centroSelecionado, materialSaida.getDataSaida(), item.getInsumoSub());
                atualizaEstoqueFollowUp(centroSelecionado, item.getInsumoSub());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Recebe uma lista de itens de entrada e atualiza o estoque de cada item.
     *
     * @param itens
     * @param centroSelecionado
     * @param materialEntrada
     */
    public void atualizaEstoqueItensEntrada(List<MaterialEntradaItens> itens, CentroCusto centroSelecionado, MaterialEntrada materialEntrada) {
        for (MaterialEntradaItens item : itens) {
            try {
                atualizaSaldoMaterial(centroSelecionado, materialEntrada.getDataEntrada(), item.getInsumoSub());
                atualizaEstoqueFollowUp(centroSelecionado, item.getInsumoSub());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public List<MaterialEntradasESaidas> mountEntradasESaidas(List<MaterialEntradaItens> entradas, List<MaterialSaidaItens> saidas) {
        List<MaterialEntradasESaidas> materiais = new ArrayList();
        for (MaterialEntradaItens mat : entradas) {
            materiais.add(new MaterialEntradasESaidas(mat.getDataEntrada(), "E", mat.getMaterialEntrada().getTipoMovimento().name(), mat.getMaterialEntrada().getTipoDocumento(), mat.getMaterialEntrada().getNumeroDocumento(), mat.getMaterialEntrada().getNumeroEntrada(), mat.getItemItem(), mat.getQuantidade()));
        }
        for (MaterialSaidaItens mat : saidas) {
            materiais.add(new MaterialEntradasESaidas(mat.getDataSaida(), "S", mat.getMaterialSaida().getTipoMovimento(), mat.getMaterialSaida().getTipoDocumento(), mat.getMaterialSaida().getNumeroDocumento(), mat.getMaterialSaida().getNumeroSaida(), mat.getItemItem(), mat.getQuantidade()));
        }
        Collections.sort(materiais);
        return materiais;
    }

    public void mountKardex(MateriaisEstoque current, Date refDate) {
        String dataSaldoInicial = DateUtils.getYearMonth(DateUtils.incrementar(refDate, -1, Calendar.MONTH));
        String dataSaldoFinal = DateUtils.getYearMonth(refDate);
        current.setSaldoInicial(materiaisEstoqueFacade.findSaldo(current.getCentro(), current.getInsumoSub(), dataSaldoInicial));
        current.setSaldoFinal(materiaisEstoqueFacade.findSaldo(current.getCentro(), current.getInsumoSub(), dataSaldoFinal));
        current.setMaterialEntradaItens(materialEntradaItensFacade.findParam(current.getCentro().getEmpresaCod(), current.getCentro().getFilialCod(), current.getCentro().getCodigo(), current.getInsumoSub(), refDate));
        current.setMaterialSaidaItens(materialSaidaItensFacade.findParam(current.getCentro().getEmpresaCod(), current.getCentro().getFilialCod(), current.getCentro().getCodigo(), current.getInsumoSub(), refDate));
        current.setMaterialEntradasESaidas(mountEntradasESaidas(current.getMaterialEntradaItens(), current.getMaterialSaidaItens()));
        current.getMaterialEntradaItens().clear();
        current.getMaterialSaidaItens().clear();
        current.setMaterialEntradaItens(null);
        current.setMaterialSaidaItens(null);
    }
}
