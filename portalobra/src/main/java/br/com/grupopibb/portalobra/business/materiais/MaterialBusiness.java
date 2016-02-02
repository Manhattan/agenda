/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.business.materiais;

import br.com.grupopibb.portalobra.dao.materiais.MateriaisEstoqueFacade;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.model.materiais.MateriaisEstoque;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntrada;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntradaItens;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaida;
import br.com.grupopibb.portalobra.model.materiais.MaterialSaidaItens;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import br.com.grupopibb.portalobra.utils.UtilBeans;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author administrator
 */
@Stateless
@LocalBean
public class MaterialBusiness {

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;
   

    protected EntityManager getEntityManager() {
        return em;
    }

    private void corrigeDiaMovimento(MaterialEntrada materialEntrada) {
        materialEntrada.setDiaEntrada(NumberUtils.preencheZeroEsquerda(materialEntrada.getDiaEntrada(), 2));
    }

    private void corrigeDiaMovimento(MaterialSaida materialSaida) {
        materialSaida.setDiaSaida(NumberUtils.preencheZeroEsquerda(materialSaida.getDiaSaida(), 2));
    }

    private void corrigeCentroDestino(MaterialSaida materialSaida) {
        if (materialSaida.getTipoMovimento().equals("T")) {
            materialSaida.setCentroCodDest(materialSaida.getCentroDestino().getCodigo());
            materialSaida.setFilialCodDest(materialSaida.getCentroDestino().getFilialCod());
            materialSaida.setEmpresaCodDest(materialSaida.getCentroDestino().getEmpresaCod());
        } else {
            materialSaida.setCentroCodDest("");
            materialSaida.setFilialCodDest("");
            materialSaida.setEmpresaCodDest("");
        }
    }

    /**
     * Prepara o Material de Saida para ser criado ou atualizado.
     *
     * @param materialSaida Material de Saida a ser corrigido.
     */
    public void corrigeMaterialSaida(MaterialSaida materialSaida) {
        corrigeDiaMovimento(materialSaida);
        corrigeCentroDestino(materialSaida);
    }

    /**
     * Prepara o Material de Entrada para ser criado ou atualizado.
     *
     * @param materialEntrada Material de Entrada a ser corrigido.
     */
    public void corrideMaterialEntrada(MaterialEntrada materialEntrada) {
        corrigeDiaMovimento(materialEntrada);
    }

    /**
     * Verifica se a lista de itens do Material de Entrada contém o SubInsumo
     * passado como parâmetro.
     *
     * @param matEnt Material de Entrada que contém a lista de itens.
     * @param insumoSub InsumoSub a ser comparado.
     * @return Verdadeiro ou falso.
     */
    public boolean isContainsInsumoSub(MaterialEntrada matEnt, InsumoSub insumoSub) {
        for (MaterialEntradaItens item : matEnt.getItens()) {
            if (item.getInsumoSub().getId().toString().equals(insumoSub.getId().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converte um MaterialSaidaItens para um MaterialEntradaItens.
     *
     * @param itemSaida Item de saída a ser convertido.
     * @return Item de entrada.
     */
    public MaterialEntradaItens convertItemSaidaToItemEntrada(MaterialSaidaItens itemSaida, MaterialEntrada materialEntrada) {
        if (materialEntrada != null && itemSaida != null) {
            Integer itemNum = incrementItemMatEntrada(materialEntrada);
            String itemItem = "000" + String.valueOf(itemNum);
            itemItem = StringUtils.substring(itemItem, (itemItem.length() - 3));
            return new MaterialEntradaItens(materialEntrada, itemNum, itemItem,
                    itemSaida.getInsumoSub(), itemSaida.getQuantidade(), itemSaida.getValor(),
                    materialEntrada.getDataEntrada(), materialEntrada.getCentro().getEmpresaCod(),
                    materialEntrada.getCentro().getFilialCod(), materialEntrada.getCentro().getCodigo(),
                    itemSaida.getObservacao(), itemSaida.getMaterialSaida().getNumeroSaida(), itemSaida.getNumero());
        } else {
            return null;
        }
    }

    /**
     * Retorna o número do item a ser incrementado nos itens do Material de
     * Entrada.
     *
     * @param materialEntrada
     * @return
     */
    public Integer incrementItemMatEntrada(MaterialEntrada entrada) {
        try {
            Integer itemMaior = 1;
            for (MaterialEntradaItens item : entrada.getItens()) {
                while (item.getNumero() >= itemMaior) {
                    itemMaior++;
                }
            }
            return itemMaior;
        } catch (NullPointerException e) {
            return 1;
        }
    }

    /**
     * Reorganiza o código dos itens de entrada;
     *
     * @param entrada
     */
    public void decrementItemMaterialEntrada(MaterialEntrada entrada) {
        try {
            Integer itemAtual = 1;
            for (MaterialEntradaItens item : entrada.getItens()) {
                if (item.getNumero() >= itemAtual) {
                    int index = entrada.getItens().indexOf(item);
                    item.setNumero(itemAtual);
                    item.setItemItem(StringUtils.right("000" + itemAtual.toString(), 3));
                    entrada.getItens().set(index, item);
                    itemAtual++;
                }
            }
        } catch (NullPointerException e) {
        }
    }

    public Double getEntradaUltimoPreco(CentroCusto centro, InsumoSub insumoSub) {
        try {
            Query q = getEntityManager().createNativeQuery("exec sp_PO_EntradaUltimoPreco ?, ?, ?, ?, ?");
            q.setParameter(1, centro.getEmpresaCod());
            q.setParameter(2, centro.getFilialCod());
            q.setParameter(3, centro.getCodigo());
            q.setParameter(4, insumoSub.getInsumoCod().intValue());
            q.setParameter(5, insumoSub.getCodigo().intValue());
            Object value = q.getSingleResult();
            if (value == null) {
                return 0.0;
            }
            return ((BigDecimal) value).doubleValue();
        } catch (NullPointerException | NumberFormatException | NoResultException e) {
            return 0.0;
        }
    }

//    public boolean isValidaMaterialSaida(List<MaterialSaidaItens> itens, CentroCusto centroCusto) {;
//
//        for (MaterialSaidaItens item : itens) {
//
//            materiaisEstoque = materiaisEstoqueFacade.findParam(centroCusto, item.getInsumoSub(), DateUtils.getDataFormatada("yyyy/MM", item.getDataSaida()).replace("/", ""));
//            double estoqueAtual = materiaisEstoque.getEstoqueQuantidade();
//            double somaQuantItem = 0;
//
//            for (MaterialSaidaItens item2 : itens) {
//
//                if (item.getId() == item2.getId()) {
//                    somaQuantItem = item.getQuantidade() + item2.getQuantidade();
//                    if (somaQuantItem > estoqueAtual) {
//
//                        return false;
//
//                    }
//                }
//
//            }
//
//
//        }
//        return true;
//    }
}
