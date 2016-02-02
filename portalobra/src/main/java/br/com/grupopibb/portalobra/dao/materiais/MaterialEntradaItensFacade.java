/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.dao.materiais;

import br.com.grupopibb.portalobra.business.materiais.EstoqueBusiness;
import br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans;
import static br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans.getMapParams;
import br.com.grupopibb.portalobra.exceptions.EntityException;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntrada;
import br.com.grupopibb.portalobra.model.materiais.MaterialEntradaItens;
import br.com.grupopibb.portalobra.utils.DateUtils;
import br.com.grupopibb.portalobra.utils.StringBeanUtils;
import br.com.grupopibb.portalobra.utils.UtilBeans;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author administrator
 */
@Stateless
@LocalBean
//@Interceptors({LogTime.class})
public class MaterialEntradaItensFacade extends AbstractEntityBeans<MaterialEntradaItens, Long> {

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    /**/
    @EJB
    private EstoqueBusiness estoqueBusiness;

    public MaterialEntradaItensFacade() {
        super(MaterialEntradaItens.class, MaterialEntradaItensFacade.class);
    }

    /**
     * Procura pelos filtros empresaCod, filialCod, centroCod, insumoCod
     *
     * @return List<MaterialEntradaItens>
     */
    public List<MaterialEntradaItens> findParam(final String empresaCod, final String filialCod, final String centroCod, final InsumoSub insumoSub, final Date dataMesRef) {
        Map<String, Object> params = getMapParams();
        paramsPaginacao(params, empresaCod, filialCod, centroCod, insumoSub, dataMesRef);
        return listPesqParam("MaterialEntradaItens.find", params);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Long> findEntradaNumeroByInsumo(final Long insumoCod, final String insumoEspecificacao, final String empresaCod, final String filialCod, final String centroCod) {
        Map<String, Object> params = getMapParams();
        paramsEntradaNumero(params, insumoCod, insumoEspecificacao, empresaCod, filialCod, centroCod);
        Query q = getEntityManager().createNamedQuery("MaterialEntradaItens.findEntradaNumeroByInsumo");
        for (String chave : params.keySet()) {
            q.setParameter(chave, params.get(chave));
        }
        return q.getResultList();
    }

    public void createItens(List<MaterialEntradaItens> itens, CentroCusto centro, MaterialEntrada materialEntrada) {
        if (itens != null && !itens.isEmpty()) {
            for (MaterialEntradaItens item : itens) {
                try {
                    create(item);
                    estoqueBusiness.atualizaSaldoMaterial(centro, materialEntrada.getDataEntrada(), item.getInsumoSub());
                    estoqueBusiness.atualizaEstoqueFollowUp(centro, item.getInsumoSub());
                } catch (EntityException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        itens = null;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Double findEstoqueEntradas(final Long insumoCod, final CentroCusto centro) {
        Map<String, Object> params = getMapParams();
        paramsValorEntradas(params, insumoCod, centro.getEmpresaCod(), centro.getFilialCod(), centro.getCodigo());
        Query q = getEntityManager().createNamedQuery("MaterialEntradaItens.findEstoqueEntradas");
        for (String chave : params.keySet()) {
            q.setParameter(chave, params.get(chave));
        }
        return (Double) q.getSingleResult();
    }

    private void paramsValorEntradas(Map<String, Object> params, final Long insumoCod, final String empresaCod, final String filialCod, final String centroCod) {
        params.put("insumoCod", insumoCod);
        params.put("empresaCod", empresaCod);
        params.put("filialCod", filialCod);
        params.put("centroCod", centroCod);
        params.put("dataInicial", DateUtils.toFirstDate(new Date()));
        params.put("dataFinal", new Date());
    }

    private void paramsPaginacao(Map<String, Object> params, final String empresaCod, final String filialCod, final String centroCod, final InsumoSub insumoSub, final Date dataMesRef) {
        params.put("empresaCod", empresaCod);
        params.put("filialCod", filialCod);
        params.put("centroCod", centroCod);
        params.put("insumoSub", insumoSub);
        params.put("dataInicial", DateUtils.toFirstDate(dataMesRef));
        params.put("dataFinal", DateUtils.toLastDate(dataMesRef));
    }

    private void paramsEntradaNumero(Map<String, Object> params, final Long insumoCod, final String insumoEspecificacao, final String empresaCod, final String filialCod, final String centroCod) {
        params.put("insumoCod", insumoCod);
        params.put("insumoEspecificacao", StringBeanUtils.acertaNomeParaLike(insumoEspecificacao, StringBeanUtils.LIKE_MIDDLE));
        params.put("empresaCod", empresaCod);
        params.put("filialCod", filialCod);
        params.put("centroCod", centroCod);
        params.put("insumoCod2", insumoCod == null ? "todos" : "filtro");
        params.put("insumoEspecificacao2", StringUtils.isBlank(insumoEspecificacao) ? "todos" : "filtro");
    }

    public void clearCache() {
        em.flush();
        em.getEntityManagerFactory().getCache().evictAll();
        em.clear();
    }
}
