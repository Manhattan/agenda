/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.dao;

import br.com.grupopibb.agenda.dao.common.AbstractEntityBeans;
import br.com.grupopibb.agenda.model.Contact;
import br.com.grupopibb.agenda.utils.StringBeanUtils;
import br.com.grupopibb.agenda.utils.UtilBeans;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author tone.lima
 */
@Stateless
@LocalBean
public class ContactDAO extends AbstractEntityBeans<Contact, Integer> {

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContactDAO() {
        super(Contact.class, ContactDAO.class);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Integer findMaxId() {
        Query q = getEntityManager().createNamedQuery("Contact.findMaxId");
        try {
            Integer t = (Integer) q.getSingleResult();
            return t;
        } catch (NoResultException | ClassCastException e) {
            return null;
        }
    }

    public List<Contact> findRangeParam(final String name, final String caracter, final int[] range) {
        Map<String, Object> params = getMapParams();
        paramsPaginacao(params, name, caracter);
        return listPesqParamRange("Contact.findParam", params, range[1] - range[0], range[0]);
    }

    public Integer countParam(final String name, final String caracter) {
        Map<String, Object> params = getMapParams();
        paramsPaginacao(params, name, caracter);
        return pesqCount("Contact.countParam", params).intValue();
    }

    private void paramsPaginacao(Map<String, Object> params, final String name, final String caracter) {
        params.put("name", StringBeanUtils.changeTextForLike(name, StringBeanUtils.LIKE_MIDDLE));
        params.put("name2", StringUtils.isBlank(name) ? "todos" : "filtro");
        
        params.put("caracter", StringBeanUtils.changeTextForLike(caracter, StringBeanUtils.LIKE_END));
        params.put("caracter2", StringUtils.isBlank(caracter) ? "todos" : "filtro");
    }

    public void clearCache() {
        em.flush();
        em.getEntityManagerFactory().getCache().evictAll();
        em.clear();
    }
}
