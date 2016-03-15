/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.utils;

import java.util.logging.Level;

/**
 *
 * @author tone.lima
 */
public class UtilBeans {
    /**
     * Definie o nível de log para toda aplicação
     */
    public static final Level LEVEL_LOG = Level.INFO;
    /**
     * Define qual Unidade de Persistência a aplicação está utilizando.
     */
    public static final String PERSISTENCE_UNIT = "agendaPU";
    /**
     * Define qual Banco de Dados aplicação está utilizando.
     */
    public static final String DATABASE_NAME = "agenda";
    /**
     * Define qual o nome do servidor de Banco de Dados a aplicação está utilizando.
     */
    public static final String SERVER_NAME = "10.1.1.65";
    /**
     * Define qual Usuário tem acesso ao banco de dados.
     */
    public static final String DATABASE_USER = "tone.lima";
    /**
     * Define qual a porta de conexão do Banco de Dados.
     */
    public static final String PORT = "1433";
    /**
     * Define a Senha para conexão com o banco de dados. Senha referente ao
     * DATABASE_USER.
     */ 
    public static final String DATABASE_PASS = "Mn160391";
}
