/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.utils;

import java.text.Normalizer;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author tone.lima
 */
public final class StringBeanUtils {

    /**
     * Define a variação da String para LIKE fixa no final e variavel no inicio.
     * Ex: "%aria"
     */
    public static final int LIKE_START = 0;
    /**
     * Define a variação da String para LIKE fixa no inicio e variavel no final.
     * Ex: "Mar%"
     */
    public static final int LIKE_END = 1;
    /**
     * Define a variação da String para LIKE variavel no inicio e variavel no
     * final. Ex: "%ari%"
     */
    public static final int LIKE_MIDDLE = 2;
    /**
     * Define a variação da String para LIKE em cada espaço em branco da
     * variável, incluindo início e fim. Ex: %zul%ver%
     */
    public static final int LIKE_WHITESPACE = 3;

    /**
     * Classe exclusiva de métodos estaticos, não pode ser instânciada.
     */
    private StringBeanUtils() {
    }

    /**
     * Acerta o parametro para null ou concatena com % para ser incluido em
     * consultas do tipo: <i>(:nome is null or upper(o.nome) like
     * upper(:nome))</i>.
     *
     * @param string
     * @param likeType Utilizar as constantes:<br>
     * UtilBeans.StringBeanUtils.LIKE_START<br>
     * UtilBeans.StringBeanUtils.LIKE_END<br>
     * UtilBeans.StringBeanUtils.LIKE_MIDDLE<br>
     * @return java.lang.String ou null
     */
    public static String changeTextForLike(String string,
            final int likeType) {
        if (StringUtils.isBlank(string)) {
            string = null;
        } else {
            switch (likeType) {
                case LIKE_START:
                    string = "%" + string;
                    break;
                case LIKE_MIDDLE:
                    string = "%" + string + "%";
                    break;
                case LIKE_END:
                    string = string + "%";
                    break;
                case LIKE_WHITESPACE:
                    string = StringUtils.replace(string, " ", "%");
                    string = "%" + string + "%";
                    break;
            }

        }
        return string;
    }

    /**
     * Valida se um IP v.4 é valido.
     *
     * @param ip
     * @return true se o IP for válido.
     */
    public static boolean validarIPV4(String ip) {
        if (ip == null) {
            return false;
        }
        if (ip.trim().equals("")) {
            return false;
        }
        if (ip.indexOf("-") >= 0) {
            return false;
        }
        String[] strPartes = ip.replace('.', '-').split("-");
        if (strPartes.length != 4) {
            return false;
        }
        for (int i = 0; i < strPartes.length; i++) {
            String strPedaco = strPartes[i];
            if (strPedaco == null) {
                return false;
            }
            if (strPedaco.trim().equals("")) {
                return false;
            }
            try {
                int intPedaco = Integer.parseInt(strPedaco);
                if ((i == 0 && intPedaco == 0) || (intPedaco >= 254)) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Metodo validarCPF - Responsavel em validar o CPF
     *
     * @return Boolean
     * @since 29/12/2006
     */
    public static boolean validarCPF(String cpf) {
        cpf = cpf.replace(".", "").replace("-", "").replace(" ", "");
        if (cpf.length() != 11) {
            return false;
        }
        int d1, d2;
        int digito1, digito2, resto;
        int digitoCPF;
        String nDigResult;
        d1 = d2 = 0;
        digito1 = digito2 = resto = 0;
        for (int nCount = 1; nCount < cpf.length() - 1; nCount++) {
            digitoCPF = Integer.valueOf(cpf.substring(nCount - 1, nCount)).intValue();
            //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
            d1 = d1 + (11 - nCount) * digitoCPF;
            //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
            d2 = d2 + (12 - nCount) * digitoCPF;
        }
        //Primeiro resto da divisão por 11.
        resto = (d1 % 11);
        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
        if (resto < 2) {
            digito1 = 0;
        } else {
            digito1 = 11 - resto;
        }
        d2 += 2 * digito1;
        //Segundo resto da divisão por 11.
        resto = (d2 % 11);
        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
        if (resto < 2) {
            digito2 = 0;
        } else {
            digito2 = 11 - resto;
        }
        //Digito verificador do CPF que está sendo validado.
        String nDigVerific = cpf.substring(cpf.length() - 2, cpf.length());
        //Concatenando o primeiro resto com o segundo.
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
        //comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
        return nDigVerific.equals(nDigResult);
    }

    /**
     * Código Java de uma classe com os métodos de validação de CNPJ de acordo
     * com as regras da Receita Federal.
     *
     * @param str_cnpj
     * @return retorna verdadeiro (true) para CNPJ válido e falso (false) para
     * CNPJ inválido
     */
    public static boolean validarCNPJ(String str_cnpj) {
        if (str_cnpj == null) {
            return false;
        }
        if (str_cnpj.length() != 14) {
            return false;
        }
        int soma = 0, aux, dig;
        String cnpj_calc = str_cnpj.substring(0, 12);
        char[] chr_cnpj = str_cnpj.toCharArray();

        /*
         * Primeira parte
         */
        for (int i = 0; i < 4; i++) {
            if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
            }
        }
        for (int i = 0; i < 8; i++) {
            if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9) {
                soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
            }
        }
        dig = 11 - (soma % 11);

        cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);

        /*
         * Segunda parte
         */
        soma = 0;
        for (int i = 0; i < 5; i++) {
            if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
            }
        }
        for (int i = 0; i < 8; i++) {
            if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9) {
                soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
            }
        }
        dig = 11 - (soma % 11);
        cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);

        return str_cnpj.equals(cnpj_calc);
    }

    /**
     * Formata o CPF passado para ###.###.###-##.
     *
     * @param value CPF a ser formatado.
     * @return CPF formatado.
     */
    public static String formatCPF(String value) {
        if (value != null && !value.equals("")) {
            String cpf1 = value.toString().substring(0, 3);
            String cpf2 = value.toString().substring(3, 6);
            String cpf3 = value.toString().substring(6, 9);
            String cpf4 = value.toString().substring(9);
            return cpf1 + "." + cpf2 + "." + cpf3 + "-" + cpf4;
        }
        return value == null ? null : value.toString();
    }

    /**
     * http://javafree.uol.com.br/topic-871337-Remover-acentuacao.html
     *
     * @param value
     * @return
     */
    public static String normalize(String value) {
        value = Normalizer.normalize(value, Normalizer.Form.NFD);
        value = value.replaceAll("[^\\p{ASCII}]", "");
        return value;
    }
}
