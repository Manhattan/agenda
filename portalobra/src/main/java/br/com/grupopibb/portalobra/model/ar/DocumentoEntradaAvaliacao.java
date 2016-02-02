/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.model.ar;

import br.com.grupopibb.portalobra.model.common.EntityInterface;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author administrator
 */
@Entity
@Table(name = "Documento_de_Entrada_Avaliacao")
public class DocumentoEntradaAvaliacao implements EntityInterface<DocumentoEntradaAvaliacao> {

    @ManyToOne(targetEntity = DocumentoEntrada.class)
    @JoinColumn(name = "Ent_Numero", insertable = false, updatable = false)
    private DocumentoEntrada documentoEntrada;
    /*
     */
    @Id
    @Column(name = "Ent_Numero")
    private Long documentoEntradaNumero;
    /*
     */
    @Id
    @Column(name = "EntAval_ItemCod")
    private Integer codigoItem;
    /*
     */
    @Column(name = "EntAval_ItemValue")
    private String itemValue;
    /*
     */
    @Column(name = "EntAval_ItemObs")
    private String observacao;
    /*
     */
    @Column(name = "EntAval_Pontuacao")
    private Float pontuacao;

    public DocumentoEntrada getDocumentoEntrada() {
        return documentoEntrada;
    }

    public void setDocumentoEntrada(DocumentoEntrada documentoEntrada) {
        this.documentoEntrada = documentoEntrada;
    }

    public Long getDocumentoEntradaNumero() {
        return documentoEntradaNumero;
    }

    public void setDocumentoEntradaNumero(Long documentoEntradaNumero) {
        this.documentoEntradaNumero = documentoEntradaNumero;
    }

    public Integer getCodigoItem() {
        return codigoItem;
    }

    public void setCodigoItem(Integer codigoItem) {
        this.codigoItem = codigoItem;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Float getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Float pontuacao) {
        this.pontuacao = pontuacao;
    }

    @Override
    public Serializable getId() {
        return documentoEntradaNumero.toString() + "_" + codigoItem;
    }

    @Override
    public String getLabel() {
        return documentoEntradaNumero.toString() + "_" + codigoItem;
    }

    @Override
    public boolean verificarId() {
        return false;
    }

    @Override
    public boolean isMarcado() {
        return false;
    }

    @Override
    public int compareTo(DocumentoEntradaAvaliacao o) {
        return this.getId().toString().compareTo(o.getId().toString());
    }

    @Override
    public String toString() {
        return codigoItem.toString();
    }
    
    
}
