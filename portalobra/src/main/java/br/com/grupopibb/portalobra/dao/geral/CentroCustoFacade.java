/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.dao.geral;

import br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans;
import static br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans.getMapParams;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
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
public class CentroCustoFacade extends AbstractEntityBeans<CentroCusto, String> {

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;

    public CentroCustoFacade() {
        super(CentroCusto.class, CentroCustoFacade.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CentroCusto findOne(final String codigo, final String empresa) {
        Map<String, Object> params = getMapParams();
        paramsPaginacao(params, codigo, empresa);
        return pesqParam("CentroCusto.selectOne", params);
    }

    public CentroCusto findByEmpresaCodigo(final String empresaCodigo) {
        Map<String, Object> params = getMapParams();
        paramsPaginacao1(params, empresaCodigo);
        return pesqParam("CentroCusto.findByEmpresaCodigo", params);
    }
    
    public List<CentroCusto> findCentrosWithPedidosByForn(final String cpfCnpj) {
        Map<String, Object> params = getMapParams();
        paramsPedidoFornecedor(params, cpfCnpj);
        return listPesqParam("CentroCusto.findCentrosWithPedidosByForn", params);
    }
    
    public List<CentroCusto> findNames(){
        
        return (List<CentroCusto>) pesqParam("CentroCusto.Nome");
        
    }
    
    

  /*  @Override
    public List<CentroCusto> findAll() {
        Query q = getEntityManager().createNamedQuery("CentroCusto.findAll");
        List<CentroCusto> list = q.getResultList();
        return list;
    }
*/
    private void paramsPaginacao(Map<String, Object> params, final String codigo, final String empresa) {
        params.put("centro", codigo);
        params.put("empresa", empresa);
    }

    /**
     * Pesquisa Centro pela concatenação empresa+filial+centro
     *
     * @param params
     * @param empresaCodigo
     */
    private void paramsPaginacao1(Map<String, Object> params, final String empresaCodigo) {
        params.put("empresaCodigo", empresaCodigo);
    }
    
    private void paramsPedidoFornecedor(Map<String, Object> params, final String cpfCnpj){
        params.put("cpfCnpj", cpfCnpj);
    }
    
}
