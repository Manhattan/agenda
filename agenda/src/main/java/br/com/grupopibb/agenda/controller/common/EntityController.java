/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.controller.common;

import br.com.grupopibb.agenda.model.common.EntityInterface;
import br.com.grupopibb.agenda.utils.JsfUtil;
import javax.faces.model.DataModel;

/**
 * Classe que tenta reduzir a quantidade de código repetido nos controles CRUD
 * JSF.
 *
 * @since 2012/03/02
 * @param <T> Entidade que o Controller representa.
 */
public abstract class EntityController<T extends EntityInterface> {

    /**
     * DataModel que contém os elementos Entidade que serão exibidos na
     * tabela.
     */
    private DataModel<T> items = null;
    /**
     * Controlador da paginação da tabela.
     */
    protected EntityPagination pagination;
    /**
     * Controla se a Entidade deve ser atualizado ou criado.
     */
    protected boolean newEntity = false;
    
    protected int itensCount = 0;

    /**
     * Construtor padrão.
     */
    public EntityController() {
    }

    /**
     * Deve atualizar a Entidade a partir do objeto passado, <strong>irá
     * receber valores nulos.</strong>
     *
     * @param t Entidade passada ou null.
     */
    protected abstract void setEntity(T t);

    /**
     * Retorna uma nova entidade para ser cadastrada pelo usuário e inserida
     * no banco de dados.
     *
     * @return Um objeto anotado com
     * @Entity.
     */
    protected abstract T getNewEntity();

    /**
     * Apaga a entidade no banco de dados e avisa o usuário do resultado da
     * operação.<br> <strong>Não chamar diretamente na página JSF</strong><br>
     * Chamar o método destroy() de EntityController.
     */
    protected abstract void performDestroy();

    /**
     * Responsável por salvar a entidade no banco de dados e avisar o usuário
     * do resultado da operação.<br> <strong>Não chamar diretamente na página
     * JSF</strong><br> Chamar o método createOrEdit() de EntityController.
     *
     * @return java.lang.String Normalmente fica na mesma página.
     */
    protected abstract String create();

    /**
     * Atualiza a entidade no banco de dados e avisa o usuário do resultado da
     * operação.<br> <strong>Não chamar diretamente na página JSF</strong><br>
     * Chamar o método createOrEdit() de EntityController.
     *
     * @return java.lang.String Normalmente fica na mesma página.
     */
    protected abstract String update();

    /**
     * Método que deverá definir como a paginação do DataModel será realizada.
     *
     * @return EntityPagination implementado com a regra de negócio
     * especifica.
     */
    public abstract EntityPagination getPagination();

    /**
     * Método que deve definir o que deve ser limpo quando o usuário cancela o
     * formulário da entidade.
     *
     * @return java.lang.String Normalmente retorna contante MANTEM.
     */
    public String clean() {
        setEntity(null);
        return JsfUtil.MANTEM;
    }
    
    public void search(){
        itensCount = 0;
        recreateTable();
    }

    /**
     * Quando o usuário clica em apagar um registro da tabela esse método deve
     * implementar a lógica.
     */
    public void destroy() {
        setEntity(getItems().getRowData());
        performDestroy();
        recreateTable();
    }

    /**
     * Quando o usuário cria em novo, esse método deve prepara o ambiente para
     * criar um novo objeto.
     *
     * @return java.lang.String Normalmente retorna contante MANTEM.
     */
    public String prepareCreate() {
        setEntity(getNewEntity());
        this.newEntity = true;
        return JsfUtil.MANTEM;
    }

    /**
     * Quando o usuário cria em editar, esse método deve prepara o ambiente
     * para editar objeto.
     *
     * @return java.lang.String Normalmente retorna contante MANTEM.
     */
    public String prepareEdit() {
        setEntity(getItems().getRowData());
        newEntity = false;
        return JsfUtil.MANTEM;
    }

    /**
     * Quando o usário salvar o formulário esse método definie se deve salvar
     * ou atualizar os dados da entidade.
     */
    public void createOrEdit() {
        if (newEntity) {
            create();
        } else {
            update();
        }
    }

    /**
     * Deve retornar um objeto DataModel não nulo.
     *
     * @return javax.faces.model.DataModel não nulo.
     */
    public DataModel<T> getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    /**
     * Quando alguma ação de atualização/deleção/criação é realizada esse
     * método atualiza a tabela.
     */
    public void recreateTable() {
        recreatePagination();
        recreateModel();
    }

    /**
     * Avança a tabela para a próxima página, se tiver dados a frente.
     *
     * @return java.lang.String Normalmente retorna contante MANTEM.
     */
    public String next() {
        getPagination().nextPage();
        recreateModel();
        return JsfUtil.MANTEM;
    }

    /**
     * Retorna a tabela para a página anterior, se tiver dados a frente.
     *
     * @return java.lang.String Normalmente retorna contante MANTEM.
     */
    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return JsfUtil.MANTEM;
    }

    /**
     * Anula o model para que o mesma seja recriado no método getItems().
     */
    protected void recreateModel() {
        items = null;
    }

    /**
     * Anula o Pagination para que o mesmo seja recriado no método
     * getPagination().
     */
    protected void recreatePagination() {
        pagination = null;
    }
}
