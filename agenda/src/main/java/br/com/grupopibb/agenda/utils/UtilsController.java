/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.utils;

import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author administrator
 */
@ManagedBean
@SessionScoped
public class UtilsController {

    private Boolean showTopPanel = true;

    /**
     * Retorna a data atual
     */
    public Date getDate() {
        return new Date();
    }

    public Date getPastMonth() {
        return null;
    }

    public Boolean getShowTopPanel() {
        return showTopPanel;
    }

    public String getMesAno() {
        return DateUtils.getDataFormatada("MMMM/yyyy", new Date());
    }

    public String getMesAno(Date data) {
        return DateUtils.getDataFormatada("MMMM/yyyy", data);
    }

    /**
     * Altera o atributo rendered do topPanel no layout.xhtml
     */
    public void updateShowPanel() {
        if (showTopPanel == false) {
            showTopPanel = true;
        } else {
            showTopPanel = false;
        }
    }

    /**
     * Subtrai dois números informados e formata o resultado para 3 casas
     * decimais.
     *
     * @param num1
     * @param num2
     * @return Subtração com 3 casas decimais
     */
    public Double subtrair(Double num1, Double num2) {
        return NumberUtils.subtractForThreeDecimal(num1, num2);
    }

    /**
     * Soma dois números e retorna zero caso o resultado seja negativo.
     *
     * @param num1
     * @param num2
     * @return Valor sem formatação.
     */
    public Double somarNumerosPositivos(Double num1, Double num2, int decimalPlaces) {
        num1 = (num1 == null || num1 < 0) ? 0.0 : num1;
        num2 = (num2 == null) ? 0.0 : num2;
        return NumberUtils.arredondarHalfUp(NumberUtils.sumPositiveNumbers(num1, num2), decimalPlaces);
    }
    
    public String getRoundedNumber(Double valor){
        return NumberUtils.formatDecimalNoFinalZero(NumberUtils.arredondarHalfUp(valor, 4));
    }
   
    public Double getRoundedNumberDouble(Double valor){
        return NumberUtils.arredondarHalfUp(valor, 4);
    }
    
    public int getSizeOfNumber(Double valor){
        return NumberUtils.sizeOfNumber(valor);
    }
    
    public Double multiply(Double num1, Double num2){
        return NumberUtils.multiply(num1, num2);
    }

    public Double divide(Double num1, Double num2){
        return NumberUtils.divide(num1, num2);
    }
    
    public String getMoneyNoSymbol(Double valor){
        return NumberUtils.formatCurrencyNoSymbol(valor);
    }
    
    public String preencheZeroEsquerda(String value, int qtd){
        return NumberUtils.preencheZeroEsquerda(value, qtd);
    }
}
