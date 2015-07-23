/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.utils.converter;

import br.com.grupopibb.portalobra.model.tipos.EnumSituacaoSolicitacao;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;

/**
 *
 * @author administrator
 */
//====================
// Conversor (EnumSituacaoSolicitacao)
//====================
/**
 * Conversor para classe EnumSituacaoSolicitacao em SelectItem.
 */
@FacesConverter(forClass = EnumSituacaoSolicitacao.class, value = "situacaoSolicitacaoConverter")
public class SituacaoSolicitacaoConverter implements Converter {

    /**
     * Devolve um Objeto DetalheProcedimento com base nos parâmetros.
     *
     * @param facesContext Contexto atual.
     * @param component Componente SelectMany, SelectOne do JSF.
     * @param value Usuário em forma de String que deve ser convertido em
     * objeto. A string aqui é o login do usuário.
     * @return java.lang.Object que pode ser feito cast para
     * DetalheProcedimento.
     */
    @Override
    public Object getAsObject(final FacesContext facesContext,
            final UIComponent component, final String value) {
        return EnumSituacaoSolicitacao.getForLabel(value);
    }

    /**
     * Retorna o objeto DetalheProcedimento em formato de String, que representa
     * um DetalheProcedimento.
     *
     * @param facesContext Contexto atual.
     * @param component Componente JSF que mostra o Usuário na tela.
     * @param object Um DetalheProcedimento.
     * @return
     */
    @Override
    public String getAsString(final FacesContext facesContext,
            final UIComponent component, final Object object) {
        if (object == null) {
            return null;
        }
        return EnumSituacaoSolicitacao.valueOf(object.toString()).getLabel();
    }
}
