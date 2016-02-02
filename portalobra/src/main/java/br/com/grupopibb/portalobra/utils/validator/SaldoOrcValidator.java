/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.utils.validator;

import br.com.grupopibb.portalobra.utils.NumberUtils;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author tone.lima
 */
@FacesValidator(value = "saldoOrcValidator")
public class SaldoOrcValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        try {
            if (NumberUtils.isNull((Double) value, 0.0) > Double.parseDouble(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("valorSaldo"))) {
                throw new ValidatorException(new FacesMessage());
            }
        } catch (NullPointerException e) {
        }
    }
}
