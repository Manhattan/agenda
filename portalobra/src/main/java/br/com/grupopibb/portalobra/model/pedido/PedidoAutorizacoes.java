/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.pedido;

import br.com.grupopibb.portalobra.model.common.EntityInterface;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author administrator
 */
@Entity
@Table(name = "Pedido_Autorizacoes")
@NamedQuery(name = "PedidoAutorizacoes.findNotEmpty",
        query = " SELECT DISTINCT pa FROM PedidoAutorizacoes pa "
        + " WHERE ( pa.instrucao <> '' )"
        + " AND ( pa.pedido = :pedido ) ")
public class PedidoAutorizacoes implements EntityInterface<PedidoAutorizacoes> {

    @Id
    @ManyToOne(targetEntity = Pedido.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "Pedido_Numero")
    private Pedido pedido;
    /*
     */
    @Id
    @Column(name = "Usuario_Instrucao")
    private String usuario;
    /*
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Instrucao_Data")
    private Date data;
    /*
     */
    @Column(name = "Instrucao_Motivo")
    private String motivo;
    /*
     */
    @Column(name = "Instrucao_Cod")
    private String instrucao;
    /*
     */

    public String getSituacao() {
        if (instrucao == null) {
            return "";
        }
        switch (instrucao) {
            case "A":
                return "Autorizado";
            case "R":
                return "Rejeitado";
        }
        return "";
    }

    @Override
    public Serializable getId() {
        return pedido;
    }

    @Override
    public String getLabel() {
        return usuario + " - " + getSituacao();
    }

    @Override
    public boolean verificarId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isMarcado() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compareTo(PedidoAutorizacoes o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getInstrucao() {
        return instrucao;
    }

    public void setInstrucao(String instrucao) {
        this.instrucao = instrucao;
    }
}
