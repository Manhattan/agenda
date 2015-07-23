/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.business.orcamento;

import br.com.grupopibb.portalobra.acesso.ConnectionFactory;
import br.com.grupopibb.portalobra.business.insumo.InsumoBusiness;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.Insumo;
import br.com.grupopibb.portalobra.model.solicitacaocompra.SolicitacaoCompraItem;
import br.com.grupopibb.portalobra.model.solicitacaocompra.SolicitacaoCompraItemOrcPlan;
import br.com.grupopibb.portalobra.model.tipos.EnumOpeEvtOrcamento;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import br.com.grupopibb.portalobra.utils.UtilBeans;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author administrator
 */
@Stateless
@LocalBean
public class OrcamentoBusiness {
    
    @EJB
    private ConnectionFactory connectionFactory;
    
    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Pesquisa
     *
     * @param centro
     * @param insumo
     * @throws SQLException
     */
    private TreeNode buildGradeOrcamento(CentroCusto centro, Insumo insumo, TreeNode root, SolicitacaoCompraItem solicItem) throws SQLException {
        if (insumo != null) {
            String sql = "exec dbo.spORC_Select_ItemOrcPlanViewSolic ?,?";
            try {
                Query q = getEntityManager().createNativeQuery(sql);
                q.setParameter(1, centro.getIdCompleto());
                q.setParameter(2, insumo.getCodigo().intValue());
                Object[] resultList = q.getResultList().toArray();

                mountTreeTable(resultList, root, solicItem);
                return root;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }

    private void mountTreeTable(Object[] resultList, TreeNode root, SolicitacaoCompraItem solicItem) throws SQLException {

        int nivelAtual = 0;
        int i = -1;
        for (int cont = 0; cont < resultList.length; cont++) {
            Object[] obj = (Object[]) resultList[cont];
            SolicitacaoCompraItemOrcPlan item;
            if (StringUtils.replace(NumberUtils.toString(obj[15]), " ", "").equals("IN")) {
                // Se for um insumo, popula todos os campos.
                item = new SolicitacaoCompraItemOrcPlan(NumberUtils.toInteger(obj[1]), NumberUtils.toInteger(obj[16]), solicItem, null, NumberUtils.toInteger(obj[6]), NumberUtils.isNull(NumberUtils.toInteger(obj[10]), 0).longValue(), NumberUtils.isNull(NumberUtils.toBigDecimal(obj[26]), new BigDecimal(0)).doubleValue(), NumberUtils.toInteger(obj[5]), 0, NumberUtils.toInteger(obj[17]), NumberUtils.toBigDecimal(obj[31]).doubleValue(), NumberUtils.toBigDecimal(obj[31]).doubleValue(), NumberUtils.toString(obj[11]), NumberUtils.toString(obj[15]), NumberUtils.toString(obj[14]), NumberUtils.isNull(NumberUtils.toBigDecimal(obj[23]), new BigDecimal(0)).doubleValue(), NumberUtils.toString(obj[12]));
            } else {
                // Se NÃO for insumo, popula apenas os campos necessários.
                item = new SolicitacaoCompraItemOrcPlan(NumberUtils.toInteger(obj[1]), NumberUtils.toInteger(obj[16]), NumberUtils.isNull(NumberUtils.toInteger(obj[10]), 0).longValue(), NumberUtils.toInteger(obj[17]), NumberUtils.toString(obj[11]), NumberUtils.toString(obj[15]), NumberUtils.toString(obj[14]), NumberUtils.toString(obj[12]));
            }

            boolean incrementa = true;
            if (item.getItemPlanCod() == nivelAtual) {
                root.getChildren().get(i).getChildren().add(new DefaultTreeNode(item));
            } else {
                while (item.getItemPlanCod() != nivelAtual) {

                    if (incrementa) {
                        nivelAtual++;

                        if (nivelAtual - item.getItemPlanCod() > 30) {
                            incrementa = false;
                        }
                    } else {
                        nivelAtual--;
                    }
                    if (nivelAtual < 0) {
                        break;
                    }
                }
                root.getChildren().add(new DefaultTreeNode(item));
                i++;
            }
            item = null;
        }
    }

    /**
     * Determina o incremento a ser definido no item de orçamento.
     *
     * @param solicItem Item da solicitação com itens de orçamento.
     * @return int Incremento;
     */
    public int incrementItemPlan(SolicitacaoCompraItem solicItem) {
        if (solicItem != null && solicItem.getItensPlanoOrcamento() != null) {
            int itemMaior = 1;
            for (SolicitacaoCompraItemOrcPlan itemPlan : solicItem.getItensPlanoOrcamento()) {
                while (NumberUtils.isNull(itemPlan.getNumero(), 0) >= itemMaior) {
                    itemMaior++;
                }
            }
            return itemMaior;
        } else {
            return 1;
        }
    }

    /**
     * Atualiza a TreeTable com os itens de orçamento em um
     * SolicitacaoCompraItem.
     *
     * @param solicItem Item com List<SolicitacaoCompraItemOrcPlan>.
     * @param root TreeTable atual.
     */
    private static void updatedTreeTable(SolicitacaoCompraItem solicItem, TreeNode root) {

        if (root != null && root.getChildren() != null) {
            for (TreeNode node : root.getChildren()) {
                for (TreeNode item : node.getChildren()) {
                    if (isItemOrcamentoInsumo(item)) {
                        SolicitacaoCompraItemOrcPlan itemPlan = (SolicitacaoCompraItemOrcPlan) item.getData();
                        // int indexIn = getIndexItemPlan(insumo.getItensPlanoOrcamento(), itemPlan);
                        int indexSol = getIndexItemPlan(solicItem.getItensPlanoOrcamento(), itemPlan);
                        int indexNode = getIndexItemPlanTree(root, node);
                        int indexItem = getIndexItemPlanTree(root.getChildren().get(indexNode), item);

                        Double valorSomaSolicOriginal = null;
                        Double valorSomaSolic = 0.0;
                        Double valorSaldoOriginal = null;

                        for (SolicitacaoCompraItem currentItem : solicItem.getSolicitacao().getItens()) {
                            if (currentItem.getItensPlanoOrcamento() != null) {
                                for (SolicitacaoCompraItemOrcPlan orcSolic : currentItem.getItensPlanoOrcamento()) {
                                    if (itemPlan.getCompareId().equals(orcSolic.getCompareId())) {
                                        orcSolic.setValorSolic(NumberUtils.isNull(orcSolic.getValorSolic(), 0.0));
                                        valorSomaSolic = NumberUtils.isNull(valorSomaSolic, 0.0) + NumberUtils.isNull(orcSolic.getValorSolic(), 0.0);

                                        valorSomaSolicOriginal = NumberUtils.isNull(valorSomaSolicOriginal, 0.0) + NumberUtils.isNull(orcSolic.getValorSolicOriginal(), 0.0);

                                        if (solicItem.getNumero() == orcSolic.getSolicitacaoCompraItem().getNumero()) {
                                            itemPlan.setNumeroEvento(orcSolic.getNumeroEvento());
                                            itemPlan.setValorSolicOriginal(orcSolic.getValorSolicOriginal());

                                            if (orcSolic.getValorSolic() != null && orcSolic.getValorSolic() > 0 && indexNode >= 0) {
                                                root.getChildren().get(indexNode).setExpanded(true);
                                            }
                                        }
                                        itemPlan.setNumero(orcSolic.getNumero());
                                    }
                                }
                            }
                        }
                        valorSaldoOriginal = NumberUtils.isNull(itemPlan.getValorSaldoOriginal(), 0.0) + NumberUtils.isNull(valorSomaSolicOriginal, 0.0);
                        itemPlan.setValorSaldo(NumberUtils.isNull(valorSaldoOriginal, 0.0) - valorSomaSolic);

                        if (indexSol != -1) {
                            itemPlan.setValorSolic(solicItem.getItensPlanoOrcamento().get(indexSol).getValorSolic());
                        }

                        if (indexNode >= 0 && indexItem >= 0) {
                            root.getChildren().get(indexNode).getChildren().set(indexItem, new DefaultTreeNode(itemPlan));
                        }
                    }
                }
            }
        }
    }

    /**
     * Inicia o TreeNode passado com os valores de orçamento de um insumo em um
     * determinado centro.
     *
     * @param solicItem SolicitacaoCompraItem que contém CentroCusto e Insumos
     * que servirão como parâmetros para criação da TreeNode.
     *
     */
    public void initDefaultTreeOrcamento(SolicitacaoCompraItem solicItem, TreeNode root) {
        try {
            buildGradeOrcamento(
                    solicItem.getSolicitacao().getCentro(),
                    solicItem.getInsumoSub().getInsumo(),
                    root,
                    solicItem);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Inicia o TreeNode de acordo com os valores de orçamento já preenchidos de
     * determinado item.
     *
     * @param solicItem SolicitacaoCompraItem que contém CentroCusto e Insumos
     * que servirão como parâmetros para criação da TreeNode.
     *
     */
    public void initUpdatedTreeOrcamento(SolicitacaoCompraItem solicItem, TreeNode root) {
        try {
            if (root == null || root.getChildren() == null || root.getChildren().isEmpty()) {
                buildGradeOrcamento(
                        solicItem.getSolicitacao().getCentro(),
                        solicItem.getInsumoSub().getInsumo(),
                        root,
                        solicItem);
            }
            updatedTreeTable(solicItem, root);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Percorre os filhos de um TreeNode e transforma em lista apenas aqueles
     * que forem itens de insumo solicitáveis.
     *
     * @param root TreeNode a ser percorrido.
     *
     * @return Lista com apenas os SolicitacaoCompraItemOrcPlan solicitáveis.
     */
    public static List<SolicitacaoCompraItemOrcPlan> getItensPlanoOrcamento(TreeNode root, boolean saldoOriginalNulo) {
        List<SolicitacaoCompraItemOrcPlan> itensPlan = new ArrayList<>();
        if (root != null) {
            for (TreeNode node : root.getChildren()) {
                for (TreeNode item : node.getChildren()) {
                    if (isItemOrcamentoInsumo(item)) {
                        SolicitacaoCompraItemOrcPlan itemPlan = new SolicitacaoCompraItemOrcPlan((SolicitacaoCompraItemOrcPlan) item.getData());
                        if (saldoOriginalNulo) {
                            itemPlan.setValorSaldoOriginal(null);
                        }
                        itensPlan.add(itemPlan);
                    }
                }
            }
        }
        return itensPlan;
    }

    /**
     * Preenche os itens de orçamento da SolicitacaoCompraItem.
     *
     * @param solicItem Item que servirá de referência para inicialização do
     * currentItem.
     *
     * @return SolicitaçãoCompraItem com os itens de orçamento.
     */
    public void initSolicItemOrcamento(SolicitacaoCompraItem solicItem, TreeNode root) {
        solicItem.setItensPlanoOrcamento(getItensPlanoOrcamento(root, false));
    }

    /**
     * Preenche os itens de orçamento do Insumo informado na lista de insumos.
     *
     * @param insumo Insumo distinto na lista.
     *
     * @return Insumo com os itens de orçamento.
     */
    public static void initInsumoItemOrcamento(Insumo insumo, List<Insumo> insumos, TreeNode root) {
        int i = InsumoBusiness.getIndexInsumo(insumos, insumo);
        if (i >= 0) {
            insumos.get(i).setItensPlanoOrcamento(getItensPlanoOrcamento(root, true));
        }
    }

    /**
     * Atualiza os itens de orçamento em um item solicitado de acordo com o
     * TreeNode root.
     *
     * @param solicItem Item a ter os itens de orçamento atualizados.
     * @param root TreeNode default.
     *
     * @return solicItem Item com os valores atualizados.
     */
    public void updateSolicItemOrcamento(SolicitacaoCompraItem solicItem, TreeNode root) {
        if (root != null) {
            for (TreeNode node : root.getChildren()) {
                for (TreeNode item : node.getChildren()) {
                    if (isItemOrcamentoInsumo(item)) {
                        if (getIndexItemPlan(solicItem.getItensPlanoOrcamento(), (SolicitacaoCompraItemOrcPlan) item.getData()) == -1 && ((SolicitacaoCompraItemOrcPlan) item.getData()).getValorSolic() > 0) {
                            solicItem.getItensPlanoOrcamento().add((SolicitacaoCompraItemOrcPlan) item.getData());
                        }
                        for (SolicitacaoCompraItemOrcPlan itemPlan : solicItem.getItensPlanoOrcamento()) {
                            if (itemPlan.getCompareId().equals(((SolicitacaoCompraItemOrcPlan) item.getData()).getCompareId())) {
                                int i = getIndexItemPlan(solicItem.getItensPlanoOrcamento(), itemPlan);
                                solicItem.getItensPlanoOrcamento().set(i, (SolicitacaoCompraItemOrcPlan) item.getData());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Percorre os filhos de um TreeNode e soma os valores solicitados de cara
     * OrcamentoItem solicitável.
     *
     * @param root TreeNode com a grade de orçamento.
     *
     * @return Soma de todos os valores solicitados.
     */
    public Double getSomaValores(TreeNode root) {
        Double soma = 0.0;
        if (root != null) {
            for (TreeNode node : root.getChildren()) {
                for (TreeNode item : node.getChildren()) {
                    if (isItemOrcamentoInsumo(item)) {
                        if (item != null && item.getData() != null) {
                            soma += NumberUtils.isNull(((SolicitacaoCompraItemOrcPlan) item.getData()).getValorSolic(), 0.0);
                        }
                    }
                }
            }
        }
        return soma;
    }

    public static int getIndexItemPlan(List<SolicitacaoCompraItemOrcPlan> itensPlan, SolicitacaoCompraItemOrcPlan itemPlan) {
        if (itensPlan == null || itemPlan == null) {
            return -1;
        }
        for (SolicitacaoCompraItemOrcPlan item : itensPlan) {
            if (item.getCompareId().equals(itemPlan.getCompareId())) {
                return itensPlan.indexOf(item);
            }
        }
        return -1;
    }

    public static int getIndexItemPlanTree(TreeNode root, TreeNode node) {
        if (root != null && node != null && root.getChildren() != null) {
            for (TreeNode item : root.getChildren()) {
                if (compareSolicItemOrcPlan((SolicitacaoCompraItemOrcPlan) item.getData(), (SolicitacaoCompraItemOrcPlan) node.getData())) {
                    return root.getChildren().indexOf(item);
                }
            }
        }
        return -1;
    }

    public static List<SolicitacaoCompraItemOrcPlan> substituteItemPlan(List<SolicitacaoCompraItemOrcPlan> itensPlan, SolicitacaoCompraItemOrcPlan itemPlan) {
        int i = getIndexItemPlan(itensPlan, itemPlan);
        itensPlan.set(i, itemPlan);
        return itensPlan;
    }

    /**
     * Verifica se o objeto TreeNode é um item de orçamento com o insumo a ser
     * alterado.
     *
     * @param tree TreeNode a ser verificado.
     * @return true ou false.
     */
    public static boolean isItemOrcamentoInsumo(TreeNode tree) {
        if (tree == null || tree.getData() == null || StringUtils.isEmpty(((SolicitacaoCompraItemOrcPlan) tree.getData()).getClasseItem())) {
            return false;
        }
        if ((tree.getData() instanceof SolicitacaoCompraItemOrcPlan) && (((SolicitacaoCompraItemOrcPlan) tree.getData()).getClasseItem().equals("IN"))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Preenche o campo número dos itens de orçamento.
     *
     * @param solicItem SolicitacaoCompraItem com List<SolicitacaoCompraItem>
     * populado.
     */
    public void pupulateItenPlanNumero(SolicitacaoCompraItem solicItem) {
        List<SolicitacaoCompraItemOrcPlan> itensPlan = solicItem.getItensPlanoOrcamento();

        for (SolicitacaoCompraItemOrcPlan itemPlan : itensPlan) {
            int indexIPlan = itensPlan.indexOf(itemPlan);
            if (NumberUtils.isGreaterThanZero(itemPlan.getValorSolic())) {
                itemPlan.setNumero(incrementItemPlan(solicItem));
                itensPlan.set(indexIPlan, itemPlan);
            }
        }
        solicItem.setItensPlanoOrcamento(itensPlan);
    }

    /**
     * Converte os itens de orçamento do SolicitacaoCompraItem informado para
     * uma lista de SolicitacaoCompraItemOrcPlan. Considerando apenas os
     * OrcamentoItem's que tem o valor solicitado maior que zero.
     *
     * Utilizado para inserir nova solicitação de compra.
     *
     * @param solicItem SolicitacaoCompraItem com List<OrcamentoItem> populado.
     * @return Lista de SolicitacaoCompraItemOrcPlan para ser persistida no
     * banco.
     */
    public List<SolicitacaoCompraItemOrcPlan> insertItemOrcPlan(SolicitacaoCompraItem solicItem, EnumOpeEvtOrcamento ope) {
        List<SolicitacaoCompraItemOrcPlan> itensPlan = new ArrayList<>();


        for (SolicitacaoCompraItemOrcPlan itemPlan : solicItem.getItensPlanoOrcamento()) {
            if (NumberUtils.isGreaterThanZero(itemPlan.getValorSolic())) {

                int numeroEvento = itemPlan.getNumeroEvento();
                if (numeroEvento == 0 && ope != EnumOpeEvtOrcamento.E) {
                    ope = EnumOpeEvtOrcamento.I;
                }
                numeroEvento = updateItemOrcPlanEvt(itemPlan, ope, numeroEvento);
                itemPlan.setNumeroEvento(numeroEvento);

                if (itemPlan.getNumero() == null) {
                    itemPlan.setNumero(itensPlan.size() + 1);
                }
                itensPlan.add(itemPlan);
            }
        }
        return itensPlan;
    }

    public void removeItemOrcPlan(List<SolicitacaoCompraItem> itens) throws SQLException {
        for (SolicitacaoCompraItem solicItem : itens) {
            if (solicItem.getItensPlanoOrcamento() != null) {
                for (SolicitacaoCompraItemOrcPlan itemPlan : solicItem.getItensPlanoOrcamento()) {
                    updateItemOrcPlanEvt(itemPlan, EnumOpeEvtOrcamento.E, itemPlan.getNumeroEvento());
                }
            }
        }
    }

    public void updateItemOrcPlan(List<SolicitacaoCompraItem> itens) throws SQLException {
        for (SolicitacaoCompraItem solicItem : itens) {

            if (solicItem.getItensPlanoOrcamento() != null) {
                List<SolicitacaoCompraItemOrcPlan> itensPlan = new ArrayList<>();

                for (SolicitacaoCompraItemOrcPlan itemPlan : solicItem.getItensPlanoOrcamento()) {

                    if (!NumberUtils.isGreaterThanZero(itemPlan.getNumero())) {
                        itemPlan.setNumero(itensPlan.size() + 1);
                    }

                    int numeroEvento = itemPlan.getNumeroEvento();
                    EnumOpeEvtOrcamento ope = EnumOpeEvtOrcamento.A;

                    if (numeroEvento == 0 && ope != EnumOpeEvtOrcamento.E) {
                        ope = EnumOpeEvtOrcamento.I;
                    }

                    numeroEvento = updateItemOrcPlanEvt(itemPlan, ope, numeroEvento);
                    itemPlan.setNumeroEvento(numeroEvento);

                    if (NumberUtils.isNull(itemPlan.getValorSolic(), 0.0) > 0) {
                        itensPlan.add(itemPlan);
                    }
                }
                solicItem.setItensPlanoOrcamento(itensPlan);
            }
        }

    }

    /**
     * Executa a procedure spORC_UpdateItemOrcPlanEvt que gera um código de
     * evento.
     *
     * @param orcItem
     */
    private Integer updateItemOrcPlanEvt(SolicitacaoCompraItemOrcPlan itemPlan, EnumOpeEvtOrcamento ope, int numeroEvento) {
        String opCod = ope.toString();
        Integer numeroEvt = numeroEvento;
        Integer codigoPlanoOrc = itemPlan.getPlanCodOrc();
        Integer codigoPlanoItemOrc = itemPlan.getItemPlanCodOrc();
        Integer insumoCod = itemPlan.getInsumoCod().intValue();
        String tipo = itemPlan.getClasseItem();
        String classe = "SO";
        Date data = new Date();
        Date dataRegistro = new Date();
        Double quantidade = NumberUtils.isNull(itemPlan.getValorSolic(), 0.0);
        Double valor = 0.0;

        if (quantidade == 0) {
            ope = EnumOpeEvtOrcamento.E;
        }

        Connection con = connectionFactory.getConnection();

        String sql = "exec spORC_UpdateItemOrcPlanEvt ?,?,?,?,?,?,?,?,?,?,?";
        CallableStatement stmt = null;
        try {
            stmt = con.prepareCall(sql);
            stmt.setEscapeProcessing(true);
            stmt.setQueryTimeout(200);

            stmt.setString(1, opCod);

            if (ope != EnumOpeEvtOrcamento.I) {
                stmt.setInt(2, numeroEvento);
            } else {
                stmt.registerOutParameter(2, java.sql.Types.INTEGER);
            }
            stmt.setInt(3, codigoPlanoOrc);
            stmt.setInt(4, codigoPlanoItemOrc);
            stmt.setInt(5, insumoCod);
            stmt.setString(6, tipo);
            stmt.setString(7, classe);
            stmt.setDate(8, new java.sql.Date(DateUtils.zerarHora(data).getTime()));
            stmt.setDate(9, new java.sql.Date(DateUtils.zerarHora(dataRegistro).getTime()));
            stmt.setDouble(10, quantidade);
            stmt.setDouble(11, valor);
            stmt.executeUpdate();

            if (ope == EnumOpeEvtOrcamento.I) {
                numeroEvt = stmt.getInt(2);
            }
            stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return numeroEvt;
    }

    /**
     * Compara dois itens de orçamento através dos campos itemPlanCodOrc ou
     * keyField.
     *
     * @param itemPlan1
     * @param itemPlan2
     * @return Verdadeiro ou falso para a igualdade.
     */
    private static boolean compareSolicItemOrcPlan(SolicitacaoCompraItemOrcPlan itemPlan1, SolicitacaoCompraItemOrcPlan itemPlan2) {
        if (itemPlan1 == null || itemPlan2 == null) {
            return false;
        }
        if (NumberUtils.isNull(itemPlan1.getItemPlanCodOrc(), 0) == 0 || NumberUtils.isNull(itemPlan2.getItemPlanCodOrc(), 0) == 0) {
            return itemPlan1.getCompareIdKey().equals(itemPlan2.getCompareIdKey());
        } else {
            return itemPlan1.getCompareId().equals(itemPlan2.getCompareId());
        }
    }
}
