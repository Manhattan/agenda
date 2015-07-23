/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.dao.pedido;

import br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans;
import br.com.grupopibb.portalobra.model.pedido.Pedido;
import br.com.grupopibb.portalobra.model.pedido.PedidoAutorizacoes;
import br.com.grupopibb.portalobra.utils.UtilBeans;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author tone.lima
 */
@Stateless
@LocalBean 
//@Interceptors({LogTime.class})
public class PedidoAutorizacoesFacade extends AbstractEntityBeans<PedidoAutorizacoes, Long> {

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PedidoAutorizacoesFacade() {
        super(PedidoAutorizacoes.class, PedidoAutorizacoesFacade.class);
    }

    public List<PedidoAutorizacoes> findNotEmpty(final Pedido pedido) { 
        Map<String, Object> params = getMapParams();
        paramsNotEmpty(params, pedido);
        return listPesqParam("PedidoAutorizacoes.findNotEmpty", params);
    }

    private void paramsNotEmpty(Map<String, Object> params, final Pedido pedido) {
        params.put("pedido", pedido);
    }
}
