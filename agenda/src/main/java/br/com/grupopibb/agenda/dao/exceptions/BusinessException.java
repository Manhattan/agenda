/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.dao.exceptions;

import javax.ejb.ApplicationException;

/**
 * Classe que representa um erros de negócio, quando algo não estiver da forma
 * que se espera o método do bean EJB irá lançar essa exceção com seus
 * atributos informando o que está incorreto.
 *
 * @since 2012/02/22
 * @author Guilherme
 */
@ApplicationException(rollback = true)
public class BusinessException extends Exception {

    /**
     * Atributo que recebe um código inputado pelo desenvolvedor que mapeia
     * uma frase ou varias frases no resouce bundle da aplicação.<br>
     * Argumento obrigatório para instanciação da classe.
     */
    private String message;
    /**
     * Algumas informações são dinâmicas e devem ser inseridas neste Array
     * de String, o que estiver entre chaves {0} {1} {2} será substituído de
     * acordo com a posição no Array.<br> Argumento opcional para
     * instanciação
     * da classe.
     */
    private String[] variations;

    /**
     * Construtor que recebe somente a mensagem como argumento, não são
     * necessários variações.
     *
     * @param msg Chave da msg a ser recolhida em um arquivo de propriedades.
     */
    public BusinessException(final String msg) {
        this.message = msg;
    }

    /**
     * Construtor que recebe a mensagem e as variações.
     *
     * @param msg Chave da msg a ser recolhida em um
     * arquivo de propriedades.
     * @param var Informações que são definidas dinâmicamente no aquivo
     * de propriedades, {0}, {1}...
     */
    public BusinessException(final String msg, final String[] var) {
        super();
        this.message = msg;
        this.variations = var;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Caso seja necessário alterar a mensagem passada como argumento no
     * construtor.
     *
     * @param msg  Chave da msg a ser recolhida em um
     * arquivo de propriedades.
     */
    public void setMessage(final String msg) {
        this.message = msg;
    }

    /**
     *
     * @return Array de String com as variações ou nulo se não houver.
     */
    public String[] getVariations() {
        return variations;
    }

    /**
     * Define ou altera as variações.
     *
     * @param var Informações que são definidas dinâmicamente no aquivo
     * de propriedades, {0}, {1}...
     */
    public void setVariations(final String[] var) {
        this.variations = var;
    }
}
