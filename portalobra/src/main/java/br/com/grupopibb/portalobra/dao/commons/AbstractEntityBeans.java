/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.dao.commons;

import br.com.grupopibb.portalobra.exceptions.EntityException;
import br.com.grupopibb.portalobra.model.common.EntityInterface;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.log4j.Logger;


/**
 * Classe abstrata que realiza as tarefas básicas para os beans que a estendem.
 * <br> Métodos sem JavaDoc possuem a documentação na interface
 * InterfaceEntityBeans. <br> T = Entidade que o bean representa, Usuario,
 * Grupo, etc.<br>ID = Tipo de variável que foi anotada com
 *
 * @Id na classe T.
 *
 * @param <T> T Classe de Entidade.
 * @param <ID> ID O tipo de ID da classe de entidade, um Long, Integer,
 * IdServidor...
 */
//@RolesAllowed({"admin", "conveniada", "credenciada", "financeiro", "fornecedor", "operacional", "user", "sys"})
public abstract class AbstractEntityBeans<T extends EntityInterface, ID extends Serializable> {

    /**
     * Registra os eventos para debug em desenvolvimento.
     */
    private Logger logger = null;

    /**
     * Devolve um MAP para inserir os parâmetros nomes com parâmetros valores.
     *
     * @return java.util.HashMap
     */
    public static Map<String, Object> getMapParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        return params;
    }
    /**
     * Classe de entidade que representa a implementação.
     */
    private Class<T> entityClass;

    /**
     * Recebe o tipo de classe de entidade que quem o implementa representa.
     *
     * @param entityClasss Classe de Entidate que o bean irá representar.
     */
    public AbstractEntityBeans(final Class<T> entityClasss,
            final Class<? extends AbstractEntityBeans> facade) {
        this.entityClass = entityClasss;
        logger = Logger.getLogger(facade.getSimpleName());
    }

    /**
     * A classe que implementa deve prover um EntityManager.
     *
     * @return EntityManager
     */
    protected abstract EntityManager getEntityManager();

    /**
     * Fecha o EntityManager da classe implementadora.
     */
    public void close() {
        if (getEntityManager() != null) {
            getEntityManager().close();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(final T entity) throws EntityException {
        if (entity.verificarId()) {
            if (entity.getId() == null) {
                logger.info("Id nulo para: " + entity);
                throw new EntityException("AbstractFacade"
                        + ".entityIdVerificarNull",
                        new String[]{entity.getLabel()});
            }
            if (find((ID) entity.getId()) != null) {
                logger.info("Id duplicado para: " + entity);
                throw new EntityException("AbstractFacade.entityExiste",
                        new String[]{entity.getLabel()});
            }
        }
        try {
            validarEntity(entity);
            getEntityManager().persist(entity);
        } catch (javax.validation.ConstraintViolationException e) {
            logger.info("ConstraintViolation: " + entity);
            this.validadionException(e.getConstraintViolations());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(final T entity) throws EntityException {
        try {
            validarEntity(entity);
            getEntityManager().merge(entity);
        } catch (javax.validation.ConstraintViolationException e) {
            logger.info("ConstraintViolation: " + entity.toString());
            this.validadionException(e.getConstraintViolations());
        } catch (javax.persistence.OptimisticLockException e) {
            logger.info("OptimisticLockException: " + entity);
            throw new EntityException("optimisticLockException");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(final T entity) throws EntityException {
        try {
            getEntityManager().remove(getEntityManager().merge(entity));
            getEntityManager().flush();
        } catch (PersistenceException p) {
            logger.info("Problema ao remover: " + entity, p);
            throw new EntityException("AbstractFacade.entityRemoveErro",
                    new String[]{entity.getLabel(), p.getMessage()});
        } catch (javax.validation.ConstraintViolationException e) {
            this.validadionException(e.getConstraintViolations());
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public T find(final ID id) {
        try {
            return getEntityManager().find(entityClass, id);
        } catch (NoResultException e) {
            logger.info("Sem resultados para: " + id);
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq =
                getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<T> findRange(final int[] range) {
        logger.info("PAGINAÇÃO :: FirstResult: "
                + range[0] + " MaxResults: " + (range[1] - range[0]));
        javax.persistence.criteria.CriteriaQuery cq =
                getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public int count() {
        javax.persistence.criteria.CriteriaQuery cq =
                getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<T> listPesq(final String namedQuery) {
        Query q = getEntityManager().createNamedQuery(namedQuery);
        List<T> list = q.getResultList();
        return list;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<T> listPesqParam(final String namedQuery,
            final Map<String, Object> params) {
        Query q = getEntityManager().createNamedQuery(namedQuery);
        for (String chave : params.keySet()) {
            q.setParameter(chave, params.get(chave));
        }
        List<T> list = q.getResultList();
        params.clear();
        return list;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<T> listPesqParamRange(final String namedQuery,
            final Map<String, Object> params, final int max,
            final int atual) {
        Query q = getEntityManager().createNamedQuery(namedQuery).
                setMaxResults(max).setFirstResult(atual);
        for (String chave : params.keySet()) {
            q.setParameter(chave, params.get(chave));
        }
        List<T> list = q.getResultList();
        params.clear();
        return list;
    }

    public void update(final String namedQuery,
            final Map<String, Object> params)
            throws EntityException {
        Query q = getEntityManager().createNamedQuery(namedQuery);
        if (params != null) {
            for (String chave : params.keySet()) {
                q.setParameter(chave, params.get(chave));
            }
            params.clear();
        }
        q.executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<T> listPesqParam(final String namedQuery,
            final Map<String, Object> params, final int max,
            final int atual) {
        Query q = getEntityManager().createNamedQuery(namedQuery).
                setMaxResults(max).setFirstResult(atual);
        for (String chave : params.keySet()) {
            q.setParameter(chave, params.get(chave));
        }
        List<T> list = q.getResultList();
        params.clear();
        return list;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public T pesqParam(final String namedQuery,
            final Map<String, Object> params) {
        Query q = getEntityManager().createNamedQuery(namedQuery);
        for (String chave : params.keySet()) {
            q.setParameter(chave, params.get(chave));
        }
        try {
            params.clear();
            return (T) q.getSingleResult();
        } catch (NoResultException e) {
            params.clear();
            logger.info("Sem resultados para: " + namedQuery);
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public T pesqParam(final String namedQuery) {
        Query q = getEntityManager().createNamedQuery(namedQuery);
        try {
            T t = (T) q.getSingleResult();
            return t;
        } catch (NoResultException e) {
            logger.info("Sem resultados para: " + namedQuery);
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Long pesqCount(final String namedQuery, final Map<String, Object> params) {
        Query q = getEntityManager().createNamedQuery(namedQuery);
        if (params != null) {
            for (String chave : params.keySet()) {
                q.setParameter(chave, params.get(chave));
            }
            params.clear();
        }
        try {
            return (Long) q.getSingleResult();
        } catch (NoResultException e) {
            logger.info("Sem resultados para: " + namedQuery);
            return null;
        }
    }

    /**
     * Transforma um set de erros em uma Excecao EntityException para ser
     * lancada na camada de negocio.
     *
     * @param erros Set de erros.
     * @throws EntityException ConstraintViolation em EntityException.
     */
    protected void validadionException(final Set<ConstraintViolation<?>> erros)
            throws EntityException {
        String erro = "";
        for (ConstraintViolation cv : erros) {
            erro += cv.getMessage() + " ";
        }
        throw new EntityException(erro);
    }

    /**
     * Valida a entidade com base nas anotações JSR 303.
     * <code>
     *
     * @NotNnull,
     * @Size,
     * @Min,
     * @Max ...</code>
     *
     * @param entity Entidade a ser validada.
     * @throws EntityException Se houver erro nas validações.
     */
    protected void validarEntity(final EntityInterface entity)
            throws EntityException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<EntityInterface>> msgs =
                validator.validate(entity);
        if (!msgs.isEmpty()) {
            validadionToNegocioException(msgs);
        }
    }

    /**
     * Transforma um set de erros em uma Excecao NegocioException para ser
     * lancada na camada de negocio.
     *
     * @param erros Set de erros na validação.
     * @throws EntityException Erros transformados em exceções.
     */
    private void validadionToNegocioException(final Set<ConstraintViolation<EntityInterface>> erros)
            throws EntityException {
        String erro = "";
        for (ConstraintViolation cv : erros) {
            erro += cv.getMessage() + " ";
        }
        throw new EntityException(erro);
    }
    
}
