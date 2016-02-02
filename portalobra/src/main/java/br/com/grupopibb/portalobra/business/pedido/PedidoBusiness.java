/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.business.pedido;

import br.com.grupopibb.portalobra.model.tipos.EnumPrazoInicioPedido;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author administrator
 */
public class PedidoBusiness {

    /**
     * Formata as condições de pagamentos a serem exibidas no relatório do
     * Pedido de Compras.
     *
     * @param prazoDias String com prazo em dias. Esse dado vem diretamente do
     * Pedido.
     * @param prazoPercentual String com percentual para cada prazo. Esse dado
     * vem diretamente do Pedido.
     * @param prazoInicio Enum com o tipo de inicio de pagamento.
     * @return Texto completo descrevendo as formas de pagamento.
     */
    public static String getFormaPagamento(String prazoDias, String prazoPercentual, EnumPrazoInicioPedido prazoInicio) {
        try{
        int size = prazoDias.length();
        String texto = "";
        try {
            for (int i = 0; i < size;) {
                float percent = StringUtils.isBlank(prazoPercentual) ? 0f : Float.parseFloat(prazoPercentual.substring(i, i + 3));
                int prazo = Integer.parseInt(prazoDias.substring(i, i + 3));

                texto += StringUtils.isBlank(prazoPercentual) ? "" : NumberUtils.formatPercent(percent) + " com ";
                texto += prazo + " dias ";
                i += 3;
            }
            texto += prazoInicio.getLabel();
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            texto = "";
        }
        return texto;
        } catch(NullPointerException e){
            return "";
        }
    }
}
