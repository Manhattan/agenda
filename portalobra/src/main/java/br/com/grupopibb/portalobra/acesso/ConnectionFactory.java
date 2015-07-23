/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.acesso;

import br.com.grupopibb.portalobra.utils.UtilBeans;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.eclipse.persistence.sessions.Session;

/**
 *
 * @author administrator
 */
@Stateless
@LocalBean
public class ConnectionFactory {

    @PersistenceContext(unitName = UtilBeans.PERSISTENCE_UNIT)
    private EntityManager em;

    public Connection getConnection(String user, String pass) {
        try {
            return DriverManager.getConnection("jdbc:sqlserver://"
                    + UtilBeans.SERVER_NAME + ":"
                    + UtilBeans.PORT
                    + ";databaseName="
                    + getConnection().getCatalog(),//NOME DO BANCO DE DADOS
                    user, pass);
        } catch (SQLException ex) {
            return null;
        }
    }

    @Deprecated
    public Connection getConnectionOpen() {
        return getConnection(UtilBeans.DATABASE_USER, UtilBeans.DATABASE_PASS);
    }

    /**
     * Define a conexão a partir do EntityManager.
     *
     * @return Connection
     */
    public Connection getConnection() {
        Connection con = em.unwrap(Connection.class);
        return con;
    }
}
