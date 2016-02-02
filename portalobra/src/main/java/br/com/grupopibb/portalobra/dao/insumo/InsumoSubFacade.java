/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.dao.insumo;

import br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans;
import static br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans.getMapParams;
import br.com.grupopibb.portalobra.dao.projeto.ProjetoPlanejamentoFacade;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.InsumoSub;
import br.com.grupopibb.portalobra.utils.NumberUtils;
import br.com.grupopibb.portalobra.utils.StringBeanUtils;
import br.com.grupopibb.portalobra.utils.UtilBeans;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author tone.lima
 */
@Stateless
@LocalBean
public class InsumoSubFacade extends AbstractEntityBeans<InsumoSub, Long> {

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;
    @EJB
    private ProjetoPlanejamentoFacade projPlanFacade;

    public InsumoSubFacade() {
        super(InsumoSub.class, InsumoSubFacade.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Long countParam(final String insumoCod, final String insumoSubCod, final String especificacao, final String caracterizacao, final String classe, final String grupo, final boolean obraLinkadaOrcamento, final boolean desconsideraLinkOrcamento, final Integer planCod) {
        Map<String, Object> params = getMapParams();
        paramsFiltros(params, insumoCod, insumoSubCod, especificacao, caracterizacao, classe, grupo);
        if (obraLinkadaOrcamento && !desconsideraLinkOrcamento) {
            paramsOrc(params, planCod);
        }
        return pesqCount(obraLinkadaOrcamento && !desconsideraLinkOrcamento ? "InsumoSub.countParamOrc" : "InsumoSub.countParam", params);
    }

    public List<InsumoSub> findRangeParam(final CentroCusto centro, final String insumoCod, final String insumoSubCod, final String especificacao,
            final String caracterizacao, final String classe, final String grupo, final boolean obraLinkadaOrcamento,
            final boolean desconsideraLinkOrcamento, final Integer planCod, final int[] range) {
        Map<String, Object> params = getMapParams();
        paramsFiltros(params, insumoCod, insumoSubCod, especificacao, caracterizacao, classe, grupo);

        if (obraLinkadaOrcamento && !desconsideraLinkOrcamento && centro != null) {
            paramsOrc(params, planCod);
            List<InsumoSub> lista = listPesqParamRange("InsumoSub.findParamOrc", params, range[1] - range[0], range[0]);
            for (InsumoSub insumoSub : lista) {
                insumoSub.getInsumo().setValorOrcado(projPlanFacade.getValorOrc(centro.getProjCod(), centro.getOrcCod(), centro.getPlanCod(), insumoSub.getInsumoCod().intValue()));
            }
            return lista;
        } else if (obraLinkadaOrcamento && desconsideraLinkOrcamento && centro != null) {
            List<InsumoSub> lista = listPesqParamRange("InsumoSub.findParam", params, range[1] - range[0], range[0]);
            for (InsumoSub insumoSub : lista) {
                insumoSub.getInsumo().setValorOrcado(projPlanFacade.getValorOrc(centro.getProjCod(), centro.getOrcCod(), centro.getPlanCod(), insumoSub.getInsumoCod().intValue()));
            }
            return lista;
        } else {
            return listPesqParamRange("InsumoSub.findParam", params, range[1] - range[0], range[0]);
        }
    }

    /**
     * Encontra um InsumoSub pelo ID passado como parâmetro.
     *
     * @param insumoSubId no formato CodigoInsumo_CodigoInsumoSub (Ex: 3451_0)
     * @return InsumoSub encontrado ou nulo.
     */
    public InsumoSub find(final String insumoSubId) {
        Map<String, Object> params = getMapParams();
        Integer idx = StringUtils.indexOf(insumoSubId, "_");
        Long insumoCod = Long.parseLong(insumoSubId.substring(0, idx));
        Integer insumoSubCod = Integer.parseInt(insumoSubId.substring(idx + 1, insumoSubId.length()));
        paramsFind(params, insumoCod, insumoSubCod);
        return pesqParam("InsumoSub.find", params);
    }

    private void paramsFiltros(Map<String, Object> params, final String insumoCod, final String insumoSubCod, final String especificacao, final String caracterizacao, final String classe, final String grupo) {
        // params.put("insumoCod", StringBeanUtils.acertaNomeParaLike(NumberUtils.removeZeroEsquerda(insumoCod), StringBeanUtils.LIKE_END));
        params.put("insumoCod", NumberUtils.removeZeroEsquerda(insumoCod));
        params.put("insumoSubCod", NumberUtils.removeZeroEsquerda(insumoSubCod, "0"));
        params.put("especificacao", StringBeanUtils.acertaNomeParaLike(especificacao, StringBeanUtils.LIKE_END));

        params.put("caracterizacao", caracterizacao);
        params.put("classe", classe);
        params.put("grupo", grupo);

        params.put("insumoCod2", StringUtils.isBlank(insumoCod) ? "todos" : "filtro");
        params.put("insumoSubCod2", StringUtils.isBlank(insumoSubCod) ? "todos" : "filtro");
        params.put("especificacao2", StringUtils.isBlank(especificacao) ? "todos" : "filtro");

        params.put("caracterizacao2", StringUtils.isBlank(caracterizacao) ? "todos" : "filtro");
        params.put("classe2", StringUtils.isBlank(classe) ? "todos" : "filtro");
        params.put("grupo2", StringUtils.isBlank(grupo) ? "todos" : "filtro");

    }

    private void paramsFind(Map<String, Object> params, final Long insumoCod, final Integer insumoSubCod) {
        params.put("insumoCod", insumoCod);
        params.put("insumoSubCod", insumoSubCod);
    }

    private void paramsOrc(Map<String, Object> params, final int planCod) {
        params.put("planCod", planCod);
    }

    public void clearCache() {
        em.flush();
        em.getEntityManagerFactory().getCache().evictAll();
        em.clear();
    }
}
