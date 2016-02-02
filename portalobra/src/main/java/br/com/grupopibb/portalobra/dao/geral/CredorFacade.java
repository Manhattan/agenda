/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.dao.geral;

import br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans;
import static br.com.grupopibb.portalobra.dao.commons.AbstractEntityBeans.getMapParams;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.geral.Credor;
import br.com.grupopibb.portalobra.model.geral.EspecieCredor;
import br.com.grupopibb.portalobra.utils.StringBeanUtils;
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
public class CredorFacade extends AbstractEntityBeans<Credor, String> {

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CredorFacade() {
        super(Credor.class, CredorFacade.class);
    }
    
    public List<Credor> listCredorByPedido(CentroCusto centro, Credor cre) {
        Map<String, Object> params = getMapParams();
        paramsPedido(params, centro, cre);
        return listPesqParam("Credor.findCredorByPedido", params);
    }

    public List<Credor> listPesqParamRange(final String codigo, final String cpfCnpj, final String nomeFantasia, final String razaoSocial, final String cidade, final String estado, final boolean semEspecificidade, final int[] range) {
        Map<String, Object> params = getMapParams();
        paramsPaginacao(params, codigo, cpfCnpj, nomeFantasia, razaoSocial, cidade, estado, semEspecificidade);
        return listPesqParamRange("Credor.selectRange", params, range[1] - range[0], range[0]);
    }

    public Long pesqCount(final String codigo, final String cpfCnpj, final String nomeFantasia, final String razaoSocial, final String cidade, final String estado, final boolean semEspecificidade) {
        Map<String, Object> params = getMapParams();
        paramsPaginacao(params, codigo, cpfCnpj, nomeFantasia, razaoSocial, cidade, estado, semEspecificidade);
        return pesqCount("Credor.countRange", params);
    }

    private void paramsPaginacao(Map<String, Object> params, final String codigo, final String cpfCnpj, final String nomeFantasia, final String razaoSocial, final String cidade, final String estado, final boolean semEspecificidade) {
        params.put("codigo", StringBeanUtils.acertaNomeParaLike(codigo, StringBeanUtils.LIKE_END));
        params.put("cpfCnpj", StringBeanUtils.acertaNomeParaLike(cpfCnpj, StringBeanUtils.LIKE_END));
        params.put("nomeFantasia", StringBeanUtils.acertaNomeParaLike(nomeFantasia, StringBeanUtils.LIKE_END));
        params.put("razaoSocial", StringBeanUtils.acertaNomeParaLike(razaoSocial, StringBeanUtils.LIKE_END));
        params.put("cidade", StringBeanUtils.acertaNomeParaLike(cidade, StringBeanUtils.LIKE_END));
        params.put("estado", estado);

        params.put("codigo2", codigo == null ? "todos" : "filtro");
        params.put("cpfCnpj2", cpfCnpj == null ? "todos" : "filtro");
        params.put("nomeFantasia2", nomeFantasia == null ? "todos" : "filtro");
        params.put("razaoSocial2", razaoSocial == null ? "todos" : "filtro");
        params.put("cidade2", cidade == null ? "todos" : "filtro");
        params.put("estado2", estado == null ? "todos" : "filtro");
        params.put("semEspecificidade", semEspecificidade ? "filtro" : "todos");
    }

    private void paramsPedido(Map<String, Object> params, CentroCusto centro, Credor cre) {

        params.put("centro_cod", centro.getCodigo());
        params.put("filial_cod", centro.getFilialCod());
        params.put("empresa_cod", centro.getEmpresaCod());
        params.put("cpfcnpj", cre.getCpfCnpj());
    }
}
