/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.MaskFormatter;
import org.apache.commons.lang3.StringUtils;

/**
 * Classe auxiliar para trabalhar com Números.
 *
 * @since v.2 09/07/2014
 */
public final class NumberUtils {

    /**
     * Classe exclusiva de métodos estaticos, não pode ser instânciada.
     */
    private NumberUtils() {
    }

    /**
     * Retorna null se nulo ou String do número. Evita nullPointerException
     *
     * @param numero Numero a ser tratado
     * @return null se nulo ou String de um numero: Exp: 2 retorna "2" (Sem
     * aspas).
     */
    public static String numeroString(final Integer numero) {
        String anoString = numero == null ? "" : numero.toString();
        return anoString;
    }

    /**
     * Formata o valor passado para moeda Brasileira. R$ 1.000,00
     *
     * @param valor java.lang.Double a ser formatado.
     * @return java.lang.String valor formatado.
     */
    public static String formatCurrency(final Double valor) {
        NumberFormat nf = getCurencyNumberFormat();
        return nf.format(valor);
    }

    /**
     * Formata o valor passado para moeda Brasileira removendo o "R$". 1.000,00
     *
     * @param valor java.lang.Double a ser formatado.
     * @return java.lang.String valor formatado.
     */
    public static String formatCurrencyNoSymbol(final Double valor) {
        return formatCurrency(valor).replace("R$ ", "");
    }

    /**
     * Sobrecarda do método formatCurrencyNoSymbol(final Double valor).
     *
     * @param valor
     * @return
     */
    public static String formatCurrencyNoSymbol(final float valor) {
        return formatCurrencyNoSymbol(Double.valueOf(valor));
    }

    /**
     * Formata o valor passado para percentual com o símbolo %. Ex: 10.0 ficará
     * 10% .
     *
     * @param valor Float para ser formatado
     * @return String com valor formatado para percentual.
     */
    public static String formatPercent(final Float valor) {
        if (valor == null || valor == 0) {
            return "0 %";
        } else {
            return formatDecimalNoFinalZero(valor.doubleValue()) + "%";
        }
    }

    /**
     * Formata o valor passado arredondando as casas decimais e retirando o
     * ponto. Ex: 3.51 formata para 350
     *
     * @param valor java.math.BigDecimal a ser formatado.
     * @return java.lang.String valor formatado.
     */
    public static String removeDot(BigDecimal valor) {
        return StringUtils.replace(valor.setScale(2, RoundingMode.HALF_EVEN).toString(), ".", "");
    }

    /**
     * Formata o valor passado separando grupo de milhares com ponto e não
     * mostra zeros no final. Se o valor passado for nulo, retorna 0. Ex: 1000.0
     * é formatado para 1.000; Ex: 1000.1 é formatado para 1.000,1.
     *
     * @param valor Double a ser formatado.
     * @return String valor formatado.
     */
    public static String formatDecimalNoFinalZero(Double valor) {
        if (valor == null || valor == 0) {
            return "0";
        } else {
            DecimalFormat df = new DecimalFormat(",##0.####");
            return df.format(valor);
        }
    }

    /**
     * Formata o valor passado separando grupo de milhares com ponto. Se o valor
     * passado for nulo, retorna 0. Ex: 1000.0 é formatado para 1.000; Ex:
     * 1000.1 é formatado para 1.000,1.
     *
     * @param valor Double a ser formatado.
     * @return String valor formatado.
     */
    public static String formatDecimal(Double valor, int decimalPlaces) {
        if (valor == null || valor == 0) {
            return "0," + StringUtils.repeat("0", decimalPlaces);
        } else {

            DecimalFormat df = new DecimalFormat(",##0." + StringUtils.repeat("0", decimalPlaces));
            return df.format(valor);
        }
    }

    /**
     * Retorna um formatador para exibição de números financeiro:<br> Ex:
     * 65.7654 formata para R$ 65,77<br> Altera sua formatação será de acordo
     * com o padrão Brasileiro<br> Ex: 1,000.98 converte para 1.000,98.
     *
     * @return NumberFormat utilize o método format.
     */
    public static NumberFormat getCurencyNumberFormat() {
        NumberFormat nf =
                NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return nf;
    }

    /**
     * Retorna um valor na forma monetária, BR.
     *
     * @param n
     * @return nf.format(n)
     */
    public static String currencyFormat(Double n) {
        if (n == null) {
            n = 0.00;
        }
        NumberFormat nf = getCurencyNumberFormat();
        return nf.format(n);
    }

    /**
     * Para exibir informações do progresso é necessário um calculo do atual
     * pelo total, como os valores são inteiros é perdido as casas decimais,
     * esse método é para tornar essa tarefa transparente no negócio.
     *
     * @param atual
     * @param total
     * @return java.lang.Integer
     */
    public static Integer calcProgress(Integer atual, Integer total) {
        return atual * 100 / total;
    }

    /**
     * Retorna o valor passado por extenso em Português BR.
     *
     * @param vlr
     * @return
     */
    public static String valorPorExtenso(double vlr) {
        if (vlr == 0) {
            return ("zero");
        }

        long inteiro = (long) Math.abs(vlr); // parte inteira do valor
        double resto = vlr - inteiro;       // parte fracionária do valor

        String vlrS = String.valueOf(inteiro);
        if (vlrS.length() > 15) {
            return ("Erro: valor superior a 999 trilhões.");
        }

        String s = "", saux, vlrP;
        String centavos = String.valueOf((int) Math.round(resto * 100));

        String[] unidade = {"", "um", "dois", "três", "quatro", "cinco",
            "seis", "sete", "oito", "nove", "dez", "onze",
            "doze", "treze", "quatorze", "quinze", "dezesseis",
            "dezessete", "dezoito", "dezenove"};
        String[] centena = {"", "cento", "duzentos", "trezentos",
            "quatrocentos", "quinhentos", "seiscentos",
            "setecentos", "oitocentos", "novecentos"};
        String[] dezena = {"", "", "vinte", "trinta", "quarenta", "cinquenta",
            "sessenta", "setenta", "oitenta", "noventa"};
        String[] qualificaS = {"", "mil", "milhão", "bilhão", "trilhão"};
        String[] qualificaP = {"", "mil", "milhões", "bilhões", "trilhões"};

// definindo o extenso da parte inteira do valor
        int n, unid, dez, cent, tam, i = 0;
        boolean umReal = false, tem = false;
        while (!vlrS.equals("0")) {
            tam = vlrS.length();
// retira do valor a 1a. parte, 2a. parte, por exemplo, para 123456789:
// 1a. parte = 789 (centena)
// 2a. parte = 456 (mil)
// 3a. parte = 123 (milhões)
            if (tam > 3) {
                vlrP = vlrS.substring(tam - 3, tam);
                vlrS = vlrS.substring(0, tam - 3);
            } else { // última parte do valor
                vlrP = vlrS;
                vlrS = "0";
            }
            if (!vlrP.equals("000")) {
                saux = "";
                if (vlrP.equals("100")) {
                    saux = "cem";
                } else {
                    n = Integer.parseInt(vlrP, 10);  // para n = 371, tem-se:
                    cent = n / 100;                  // cent = 3 (centena trezentos)
                    dez = (n % 100) / 10;            // dez  = 7 (dezena setenta)
                    unid = (n % 100) % 10;           // unid = 1 (unidade um)
                    if (cent != 0) {
                        saux = centena[cent];
                    }
                    if ((n % 100) <= 19) {
                        if (saux.length() != 0) {
                            saux = saux + " e " + unidade[n % 100];
                        } else {
                            saux = unidade[n % 100];
                        }
                    } else {
                        if (saux.length() != 0) {
                            saux = saux + " e " + dezena[dez];
                        } else {
                            saux = dezena[dez];
                        }
                        if (unid != 0) {
                            if (saux.length() != 0) {
                                saux = saux + " e " + unidade[unid];
                            } else {
                                saux = unidade[unid];
                            }
                        }
                    }
                }
                if (vlrP.equals("1") || vlrP.equals("001")) {
                    if (i == 0) // 1a. parte do valor (um real)
                    {
                        umReal = true;
                    } else {
                        saux = saux + " " + qualificaS[i];
                    }
                } else if (i != 0) {
                    saux = saux + " " + qualificaP[i];
                }
                if (s.length() != 0) {
                    s = saux + ", " + s;
                } else {
                    s = saux;
                }
            }
            if (((i == 0) || (i == 1)) && s.length() != 0) {
                tem = true; // tem centena ou mil no valor
            }
            i = i + 1; // próximo qualificador: 1- mil, 2- milhão, 3- bilhão, ...
        }

        if (s.length() != 0) {
            if (umReal) {
                s = s + " real";
            } else if (tem) {
                s = s + " reais";
            } else {
                s = s + " de reais";
            }
        }

// definindo o extenso dos centavos do valor
        if (!centavos.equals("0")) { // valor com centavos
            if (s.length() != 0) // se não é valor somente com centavos
            {
                s = s + " e ";
            }
            if (centavos.equals("1")) {
                s = s + "um centavo";
            } else {
                n = Integer.parseInt(centavos, 10);
                if (n <= 19) {
                    s = s + unidade[n];
                } else {             // para n = 37, tem-se:
                    unid = n % 10;   // unid = 37 % 10 = 7 (unidade sete)
                    dez = n / 10;    // dez  = 37 / 10 = 3 (dezena trinta)
                    s = s + dezena[dez];
                    if (unid != 0) {
                        s = s + " e " + unidade[unid];
                    }
                }
                s = s + " centavos";
            }
        }
        return (s);
    }

    /**
     * Arredonda o valor Double passado para 2 (duas) casas decimais acima. Ex:
     * 2.585 = 2.6
     *
     * @param valor Double a ser arredondado
     * @return Float arredondado em 2 casas decimais.
     */
    public static Double arredondarHalfUp(Double valor) {
        try {
            BigDecimal valorExato = new BigDecimal(valor).setScale(3, BigDecimal.ROUND_HALF_UP);
            valor = valorExato.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            return valor;
        } catch (NullPointerException | NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Arredonda o valor Double passado para a quantidade de casas decimais
     * informadas. Ex: valor 10,544; casas decimais 2; resultado = 10,55.
     *
     * @param valor Double a ser arredondado.
     * @param decimalPlaces Quantidade de casas decimais.
     * @return Float arredondado.
     */
    public static Double arredondarHalfUp(Double valor, int decimalPlaces) {
        BigDecimal valorExato = new BigDecimal(valor).setScale(decimalPlaces + 1, BigDecimal.ROUND_HALF_UP);
        valor = valorExato.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP).doubleValue();
        return valor;
    }

    /**
     * Arredonda o valor Float passado para 2 (duas) casas decimais acima. Ex:
     * 2.585 = 2.6
     *
     * @param valor Float a ser arredondado
     * @return Float arredondado em 2 casas decimais.
     */
    public static Float arredondarHalfUp(Float valor) {
        BigDecimal valorExato = new BigDecimal(valor).setScale(3, BigDecimal.ROUND_HALF_UP);
        valor = valorExato.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return valor;
    }

    /**
     * Substitui o resultado se o valor passado for nulo. Caso contrário retorna
     * o valor passado.
     *
     * @param value Valor a ser verificado.
     * @param replacement Valor de substituição.
     * @return Float
     */
    public static Float isNull(Float value, Float replacement) {
        if (value == null) {
            return replacement;
        } else {
            return value;
        }
    }

    /**
     * Substitui o resultado se o valor original for nulo. Caso contrário
     * retorna o valor original.
     *
     * @param value Valor original a ser verificado.
     * @param replacement Valor de substituição.
     * @return Double
     */
    public static Double isNull(Double value, Double replacement) {
        if (value == null) {
            return replacement;
        } else {
            return value;
        }
    }

    /**
     * Substitui o resultado se o valor original for nulo. Caso contrário
     * retorna o valor original.
     *
     * @param value Valor original a ser verificado.
     * @param replacement Valor de substituição.
     * @return Integer
     */
    public static Integer isNull(Integer value, Integer replacement) {
        if (value == null) {
            return replacement;
        } else {
            return value;
        }
    }

    /**
     * Substitui o resultado se o valor original for nulo. Caso contrário
     * retorna o valor original.
     *
     * @param value Valor original a ser verificado.
     * @param replacement Valor de substituição.
     * @return Integer
     */
    public static Long isNull(Long value, Long replacement) {
        if (value == null) {
            return replacement;
        } else {
            return value;
        }
    }

    /**
     * Substitui o resultado se o valor original for nulo. Caso contrário
     * retorna o valor original.
     *
     * @param value Valor original a ser verificado.
     * @param replacement Valor de substituição.
     * @return Integer
     */
    public static BigDecimal isNull(BigDecimal value, BigDecimal replacement) {
        if (value == null) {
            return replacement;
        } else {
            return value;
        }
    }

    /**
     *
     *
     * @param value
     * @param quantity
     */
    public static String preencheZeroEsquerda(String value, int quantity) {
        int size = value.length();
        value = StringUtils.repeat("0", quantity) + value;
        value = StringUtils.right(value, quantity > size ? quantity : size);
        return value;
    }

    /**
     * Remove zeros à esquerda de uma String. Ex: "0000021445a" ficará "21445a"
     *
     * @param value
     * @return
     */
    public static String removeZeroEsquerda(String value) {
        try {
            while (value.startsWith("0")) {
                value = value.substring(1, value.length());
            }
        } catch (NullPointerException e) {
        }
        return value;
    }

    /**
     * Remove zeros à esquerda de uma String e retorna String default caso
     * resultado final seja vazio. Ex: "0000021445a" ficará "21445a"; Ex:
     * removeZeroEsquerda("0000", "a") ficará "a".
     *
     * @param value String que sejá verificada.
     * @param ifEmpty String default.
     * @return
     */
    public static String removeZeroEsquerda(String value, String ifEmpty) {
        try {
            value = removeZeroEsquerda(value);
            value = value.isEmpty() ? ifEmpty : value;
        } catch (NullPointerException e) {
        }
        return value;
    }

    /**
     * Subtrai dois números informados e formata o resultado para 3 casas
     * decimais.
     *
     * @param num1
     * @param num2
     * @return Subtração com 3 casas decimais
     */
    public static Double subtractForThreeDecimal(Double num1, Double num2) {
        if (num2 == null) {
            num2 = 0.0;
        }
        Double resto = num1 - num2;
        String result;

        result = String.format("%.3f", resto);
        result = StringUtils.replace(result, ",", ".");
        resto = Double.parseDouble(result);

        return resto;
    }

    /**
     * Soma dois números e retorna zero caso o resultado seja negativo.
     *
     * @param num1
     * @param num2
     * @return Valor sem formatação.
     */
    public static Double sumPositiveNumbers(Double num1, Double num2) {
        Double result = num1 + num2;
        return result < 0 ? 0.0 : result;
    }

    /**
     * Multiplica dois números arredondando para 2 casas decimais e retorna 0
     * caso um deles seja nulo.
     *
     * @param num1 Multiplicando
     * @param num2 Multiplicador
     * @return Produto da multiplicação.
     */
    public static Double multiply(Double num1, Double num2) {
        if (num1 == null || num2 == null) {
            return 0.0;
        }
        return arredondarHalfUp(num1 * num2);
    }

    /**
     * Divide dois números arredondando para 2 casas decimais e retorna 0 caso
     * um deles seja nulo.
     *
     * @param num1 Dividendo
     * @param num2 Divisor
     * @return Quociente da divisão.
     */
    public static Double divide(Double num1, Double num2) {
        try {
            if (num1 == null || num2 == null) {
                return 0.0;
            }
            return arredondarHalfUp(num1 / num2);
        } catch (NumberFormatException | NullPointerException e) {
            return 0.0;
        }
    }

    /**
     * Verifica se um dado valor é vazio (nulo ou zero).
     *
     * @param value Valor a ser analisado.
     * @return Verdadeiro para vazio ou zero, falso para o contrário.
     */
    public static boolean isEmpty(Double value) {
        return (value == null || value == 0);
    }

    /**
     * Verifica se um dado valor é positivo e maior que zero.
     *
     * @param value Valor a ser analisado.
     * @return Verdadeiro para maior que zero, falso menor ou igual a zero ou
     * nulo.
     */
    public static boolean isGreaterThanZero(Double value) {
        return (value != null && value > 0);
    }

    /**
     * Verifica se um dado valor é positivo maior que zero.
     *
     * @param value Valor a ser analisado.
     * @return Verdadeiro para maior que zero, falso menor ou igual a zero ou
     * nulo.
     */
    public static boolean isGreaterThanZero(Integer value) {
        return (value != null && value > 0);
    }

    public static Integer toInteger(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return null;
        }
    }

    public static Long toLong(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Long) {
            return (Long) value;
        } else {
            return null;
        }
    }

    public static Double toDouble(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Double) {
            return (Double) value;
        } else {
            return null;
        }
    }

    public static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else {
            return null;
        }
    }

    public static String toString(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return value.toString();
        } else {
            return null;
        }
    }

    /**
     * Determina o tamanho de um número de acordo com a quantidade de
     * caracteres.
     *
     * @param value Valor a ser determinado o "lengh" do seu número em formato
     * String.
     * @return Quantidade de caracteres literais do número.
     */
    public static int sizeOfNumber(Double value) {
        if (value == null) {
            return 1;
        }
        return value.toString().length();
    }
}
