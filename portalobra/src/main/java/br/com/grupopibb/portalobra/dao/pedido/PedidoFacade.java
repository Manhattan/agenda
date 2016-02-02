/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.dao.pedido;

import br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans;
import static br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans.getMapParams;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.geral.Credor;
import br.com.grupopibb.portalobra.model.pedido.Pedido;
import br.com.grupopibb.portalobra.utils.UtilBeans;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author tone.lima
 */
@Stateless
@LocalBean 
//@Interceptors({LogTime.class})
public class PedidoFacade extends AbstractEntityBeans<Pedido, Long>{

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PedidoFacade() {
        super(Pedido.class, PedidoFacade.class);
    }
    
    public List<Pedido> findByCentroAndCredor(CentroCusto centro, Credor credor){
        
        Map<String, Object> params = getMapParams();
        paramsFiltro(params, centro, credor);
        List<Pedido> listPedido = this.listPesqParam("Pedido.EmpFornec", params);
        
        return listPedido;
    }
    
    public List<Pedido> findCentroByCredor(Credor credor){
        
        Map<String, Object> params = getMapParams();
        paramsFiltroCentroCusto(params, credor);
        List<Pedido> listPedidoCentroCusto = this.listPesqParam("Pedido.CentroCusto", params);
        
        return listPedidoCentroCusto;
    }
    
    public void paramsFiltro(Map<String, Object> params, final CentroCusto centro, final Credor credor){
        
        params.put("empresa_cod", centro.getEmpresaCod());
        params.put("filial_cod", centro.getFilialCod());
        params.put("centro_cod", centro.getCodigo());
        params.put("cpfCnpj", credor.getCpfCnpj());
        
    }
    
    public void paramsFiltroCentroCusto(Map<String, Object> params, final Credor credor){
        
        params.put("cpfCnpj", credor.getCpfCnpj());
    }
    

}
