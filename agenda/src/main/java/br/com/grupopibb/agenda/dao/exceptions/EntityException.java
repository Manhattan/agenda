/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.dao.exceptions;

import javax.ejb.ApplicationException;

/**
 * Exceção a ser utilizada para erros no FACADES, nos business deve ser
 * utilizada BusinessExceptions.
 *
 * @since v.1 2012/05/14
 * @author Guilerme Braga
 */
@ApplicationException(rollback = true)
public class EntityException extends BusinessException {

    /**
     * Construtor que recebe a mensagem e as variações.
     *
     * @param msg Chave da msg a ser recolhida em um arquivo de propriedades.
     * @param var Informações que são definidas dinâmicamente no aquivo de
     * propriedades, {0}, {1}...
     */
    public EntityException(String msg, String[] var) {
        super(msg, var);
    }

    /**
     * Construtor que recebe somente a mensagem como argumento, não são
     * necessários variações.
     *
     * @param msg Chave da msg a ser recolhida em um arquivo de propriedades.
     */
    public EntityException(final String msg) {
        super(msg);
    }
}
