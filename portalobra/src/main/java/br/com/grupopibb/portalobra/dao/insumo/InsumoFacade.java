/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.dao.insumo;

import br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans;
import static br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans.getMapParams;
import br.com.grupopibb.portalobra.dao.projeto.ProjetoPlanejamentoFacade;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.insumo.Insumo;
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
//@Interceptors({LogTime.class})
public class InsumoFacade extends AbstractEntityBeans<Insumo, Long> {

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;
    @EJB
    private ProjetoPlanejamentoFacade projPlanFacade;

    public InsumoFacade() {
        super(Insumo.class, InsumoFacade.class);
    }
    
    

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Insumo find(final String id) {
        
        Map<String, Object> params = getMapParams();
        paramsPaginacao(params, id);
        return pesqParam("Insumo.find", params);
    }

    public List<Insumo> findParam(final String codigo, final String especificacao, final String caracterizacao, final String classe, final String grupo) {
        Map<String, Object> params = getMapParams();
        paramsFiltros(params, codigo, especificacao, caracterizacao, classe, grupo);
        return listPesqParam("Insumo.findParam", params);
    }

    public Long countParam(final String codigo, final String especificacao, final String caracterizacao, final String classe, final String grupo, final boolean obraLinkadaOrcamento, final boolean desconsideraLinkOrcamento, final Integer planCod) {
        Map<String, Object> params = getMapParams();
        paramsFiltros(params, codigo, especificacao, caracterizacao, classe, grupo);
        if (obraLinkadaOrcamento && !desconsideraLinkOrcamento) {
            paramsOrc(params, planCod);
        }
        return pesqCount(obraLinkadaOrcamento && !desconsideraLinkOrcamento ? "Insumo.countParamOrc" : "Insumo.countParam", params);
    }

    public List<Insumo> findRangeParam(final CentroCusto centro, final String codigo, final String especificacao,
            final String caracterizacao, final String classe, final String grupo, final boolean obraLinkadaOrcamento,
            final boolean desconsideraLinkOrcamento, final Integer planCod, final int[] range) {
        Map<String, Object> params = getMapParams();
        paramsFiltros(params, codigo, especificacao, caracterizacao, classe, grupo);
        if (obraLinkadaOrcamento && !desconsideraLinkOrcamento && centro != null) {
            paramsOrc(params, planCod);
            List<Insumo> lista = listPesqParamRange("Insumo.findParamOrc", params, range[1] - range[0], range[0]);
            for (Insumo insumo : lista) {
                insumo.setValorOrcado(projPlanFacade.getValorOrc(centro.getProjCod(), centro.getOrcCod(), centro.getPlanCod(), insumo.getCodigo().intValue()));
            }
            return lista;
        } else if (obraLinkadaOrcamento && desconsideraLinkOrcamento && centro != null) {
            List<Insumo> lista = listPesqParamRange("Insumo.findParam", params, range[1] - range[0], range[0]);
            for (Insumo insumo : lista) {
                insumo.setValorOrcado(projPlanFacade.getValorOrc(centro.getProjCod(), centro.getOrcCod(), centro.getPlanCod(), insumo.getCodigo().intValue()));
            }
            return lista;
        } else {
            return listPesqParamRange("Insumo.findParam", params, range[1] - range[0], range[0]);

        }
    }

    @Override
    public List<Insumo> findAll() {
        return listPesq("Insumo.findAll");
    }

    /**
     * Retorna a lista de Insumos filtrada pelo Código e Especificação do Insumo
     *
     * @param codigo
     * @param especificacao
     * @return
     */
    public List<Insumo> findByInsumo(final String codigo, final String especificacao) {
        Map<String, Object> params = getMapParams();
        paramsFiltroInsumo(params, codigo, especificacao);
        return listPesqParam("Insumo.findByInsumo", params);
    }

    /**
     * Retorna a lista de Insumos filtrada pela Caracterização, Classe e Grupo
     * do Insumo.
     *
     * @param caracterizacao
     * @param classe
     * @param grupo
     * @return
     */
    public List<Insumo> findByOutros(final String caracterizacao, final String classe, final String grupo) {
        Map<String, Object> params = getMapParams();
        paramsFiltroOutros(params, caracterizacao, classe, grupo);
        return listPesqParam("Insumo.findByOutros", params);
    }

    //pesqParam
    private void paramsPaginacao(Map<String, Object> params, final String id) {
        params.put("id", id);
    }

    private void paramsFiltros(Map<String, Object> params, final String codigo, final String especificacao, final String caracterizacao, final String classe, final String grupo) {
        params.put("codigo", StringBeanUtils.acertaNomeParaLike(codigo, StringBeanUtils.LIKE_END));
        params.put("especificacao", StringBeanUtils.acertaNomeParaLike(especificacao, StringBeanUtils.LIKE_END));

        params.put("caracterizacao", caracterizacao);
        params.put("classe", classe);
        params.put("grupo", grupo);

        params.put("codigo2", StringUtils.isBlank(codigo) ? "todos" : "filtro");
        params.put("especificacao2", StringUtils.isBlank(especificacao) ? "todos" : "filtro");

        params.put("caracterizacao2", StringUtils.isBlank(caracterizacao) ? "todos" : "filtro");
        params.put("classe2", StringUtils.isBlank(classe) ? "todos" : "filtro");
        params.put("grupo2", StringUtils.isBlank(grupo) ? "todos" : "filtro");

    }

    private void paramsOrc(Map<String, Object> params, final int planCod) {
        params.put("planCod", planCod);
    }

    /**
     * Preenche o Map<String, Object> params com os filtros Código e
     * Especificação do Insumo.
     *
     * @param params
     * @param codigo
     * @param especificacao
     */
    public void paramsFiltroInsumo(Map<String, Object> params, final String codigo, final String especificacao) {
        params.put("codigo", StringBeanUtils.acertaNomeParaLike(codigo, StringBeanUtils.LIKE_END));
        params.put("especificacao", especificacao);

        params.put("codigo2", StringUtils.isBlank(codigo) ? "todos" : "filtro");
        params.put("especificacao2", StringUtils.isBlank(especificacao) ? "todos" : "filtro");
    }

    /**
     * Preenche o Map<String, Object> params com os filtros Classe, Grupo e
     * Caracterização do Insumo.
     *
     * @param params
     * @param caracterizacao
     * @param classe
     * @param grupo
     */
    public void paramsFiltroOutros(Map<String, Object> params, final String caracterizacao, final String classe, final String grupo) {
        params.put("caracterizacao", caracterizacao);
        params.put("classe", classe);
        params.put("grupo", grupo);

        params.put("caracterizacao2", StringUtils.isBlank(caracterizacao) ? "todos" : "filtro");
        params.put("classe2", StringUtils.isBlank(classe) ? "todos" : "filtro");
        params.put("grupo2", StringUtils.isBlank(grupo) ? "todos" : "filtro");
    }
}