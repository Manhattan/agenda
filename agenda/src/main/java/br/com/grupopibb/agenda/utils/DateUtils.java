/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

/**
 * Classe auxiliar para trabalhar com Datas.
 */
public final class DateUtils {

    private static final int MILLISECOND_TO_DAY = 1000 * 60 * 60 * 24;
    /**
     * Define a data atual
     */
    public final static Date HOJE = new Date();

    /**
     * Adiciona mes(es) a uma data especifica.
     *
     * @param date
     * @param i
     *
     * @return Date
     */
    public static Date addMonths(Date date, int i) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, i);
        return c.getTime();
    }

    /**
     * Adiciona dia(s) a uma data especifica.
     *
     * @param date
     * @param i
     */
    public static Date addDays(Date date, int i) {
        return set(date, Calendar.DAY_OF_MONTH, getDay(date) + i);
    }

    /**
     * Classe exclusiva de métodos estaticos, não pode ser instânciada.
     */
    private DateUtils() {
    }

    /**
     * Retorna um DateFormat para formartar datas com Locale definido como
     * Brasil.
     *
     * @param pattern dd/MM/yy 31/12/12 dd/MM/yyyy 31/12/2012 dd-MMMM-yy
     * 31/Dezembro/12
     * @return DateFormat pt_BR utilizando a pattern passada.
     */
    public static DateFormat getFormatter(final String pattern) {
        SimpleDateFormat sd = new SimpleDateFormat(pattern,
                new Locale("pt", "BR"));
        return sd;
    }

    /**
     * Retorna uma data no formato de String DD/MM/AAAA HH:mm:ss.
     *
     * @param date java.util.Date
     * @return java.lang.String
     */
    public static String getDateFull(final Date date) {
        DateFormat sd = getFormatter("dd/MM/yyyy HH:mm:ss");
        if (date == null) {
            return sd.format(new Date());
        }
        return sd.format(date);
    }

    /**
     * Formata a data do dia, formato AAAAMMDD, para ser incluida na
     * nomenclatura de um arquivo.
     *
     * @return java.lang.String yyyMMdd
     */
    public static String getDateToNameFile() {
        Date date = new Date();
        DateFormat sd = getFormatter("yyyyMMdd");
        return sd.format(date);
    }

    /**
     * Zera a hora, minuto e segunda de uma data.
     *
     * @param data
     * @return Data zerada nas horas, minutos e segundos ou nulo se parametro
     * for nulo.
     */
    public static Date zerarHora(Date data) {
        if (data != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(data);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            return c.getTime();
        }
        return data;
    }

    /**
     * Maximiza a hora, minuto e segunda de uma data.
     *
     * @param data
     * @return Data 23 nas horas, 59 minutos e 59 segundos ou nulo se parametro
     * for nulo.
     */
    public static Date maximizarHora(Date data) {
        if (data != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(data);
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.MILLISECOND, 999);
            return c.getTime();
        }
        return data;
    }

    /**
     * Incrementa campo solicitado com a quantidade solicitada.
     *
     * @param data Data a ser incrementada
     * @param qtdade Quantidade a ser inserido.
     * @param calendarField Campo a ser incrementado: Calendar.YEAR,
     * Calendar.HOUR_OF_DAY
     * @return Data incrementada, se data null retorna null.
     */
    public static Date incrementar(Date data, int qtdade, int calendarField) {
        if (data == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        c.add(calendarField, qtdade);
        return c.getTime();
    }

    /**
     * Retorna um array com a menor data de um mês, 01/XX/XXXX 00:00:00 000
     * posição 0 e maior data de um mês se Jan: 31/01/2012 23:59:59 posição 1.
     * 30/03/2012 23:59:59
     *
     * @param date
     * @return
     */
    public static Date[] getIntervalo(Date date) {
        Date[] toReturn = new Date[2];
        Calendar ini = Calendar.getInstance();
        Calendar fim = Calendar.getInstance();
        ini.setTime(date);
        fim.setTime(date);
        ini.set(Calendar.DAY_OF_MONTH, 1);
        ini.setTime(zerarHora(ini.getTime()));
        fim.set(Calendar.DAY_OF_MONTH, fim.getActualMaximum(Calendar.DAY_OF_MONTH));
        fim.setTime(maximizarHora(fim.getTime()));
        toReturn[0] = ini.getTime();
        toReturn[1] = fim.getTime();
        return toReturn;
    }

    /**
     * Formarta a data passada com o Pattern passado: pattern = dd/MM/yy =
     * 01/01/13 pattern = dd/MM/yyyy = 01/01/2013
     *
     * @param pattern
     * @param data
     * @return
     */
    public static String getDataFormatada(String pattern, Date data) {
        try {
            return getFormatter(pattern).format(data);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Retorna o calendar da data informada
     *
     * @param date
     * @return
     */
    public static Calendar toCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    /**
     * Retorna o campo da data informado de acordo com os campos do calendar
     *
     * @param date
     * @param calendarField
     * @return
     */
    public static int get(Date date, int calendarField) {
        return toCalendar(date).get(calendarField);
    }

    /**
     * Define o valor informado para o campo informado para a data informada de
     * acordo com o calendar
     *
     * @param date
     * @param calendarField
     * @param value
     * @return
     */
    public static Date set(Date date, int calendarField, int value) {
        Calendar c = toCalendar(date);
        c.set(calendarField, value);
        return c.getTime();
    }

    /**
     * Calcula o primeiro horario do dia (00:00:00) util para comparacoes
     *
     * @param date horario a ser alterado
     * @return Date com o horario alterado
     */
    public static Date toFirstTime(Date date) {
        Calendar cal = toCalendar(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Calcula o ultimo horário do dia (23:59:59). Util para comparacoes
     *
     * @param date horario a ser alterado
     * @return Date com o horário alterado
     */
    public static Date toLastTime(Date date) {
        Calendar cal = toCalendar(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * Calcula o primeiro milisegundo da data
     *
     * @param date
     * @return
     */
    public static Date toFirstMillisecond(Date date) {
        return set(date, Calendar.MILLISECOND, 0);
    }

    /**
     * Calcula o ultimo milisegundo da data
     *
     * @param date
     * @return
     */
    public static Date toLastMillisecond(Date date) {
        return set(date, Calendar.MILLISECOND, 999);
    }

    /**
     * Calcula o primeiro dia do mes e primeira hora
     *
     * @param date
     * @return
     */
    public static Date toFirstDate(Date date) {
        Calendar cal = toCalendar(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return toFirstTime(cal.getTime());
    }

    /**
     * Calcula o ultimo dia do mês e ultima hora
     *
     * @param date
     * @return
     */
    public static Date toLastDate(Date date) {
        Calendar cal = toCalendar(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return toLastTime(cal.getTime());
    }

    /**
     * Calcula a diferenca de dias entre as datas informadas sem levar em
     * consideracao a hora.
     *
     * @param inicio
     * @param fim
     * @return
     */
    public static long daysBetween(Date start, Date stop) {
        return (toFirstTime(stop).getTime() - toFirstTime(start).getTime()) / MILLISECOND_TO_DAY;
    }

    /**
     * Calcula o periodo em dias das datas informadas (inclusive).
     *
     * @param inicio
     * @param fim
     * @return
     */
    public static long period(Date start, Date stop) {
        return daysBetween(start, stop) + 1;
    }

    /**
     * Compara se as datas sao iguais sem levar em consideracao os milisegundos
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean equals(Date date1, Date date2) {
        return toFirstMillisecond(date1).equals(toFirstMillisecond(date2));
    }

    /**
     * Compara se as datas sao iguais sem levar em consideracao a hora
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean equalsIgnoreTime(Date date1, Date date2) {
        return toFirstTime(date1).equals(toFirstTime(date2));
    }

    /**
     * Compara se a data1 e maior do que a data2 sem levar em consideracao os
     * milisegundos
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean after(Date date1, Date date2) {
        return toFirstMillisecond(date1).after(toFirstMillisecond(date2));
    }

    /**
     * Compara se a data1 e maior do que a data2 sem levar em consideracao a
     * hora
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean afterIgnoreTime(Date date1, Date date2) {
        return toFirstTime(date1).after(toFirstTime(date2));
    }

    /**
     * Compara se a data1 e maior ou igual do que a data2 sem levar em
     * consideracao os milisegundos
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean afterOrEqual(Date date1, Date date2) {
        return equals(date1, date2) || toFirstMillisecond(date1).after(toFirstMillisecond(date2));
    }

    /**
     * Compara se a data1 e maior do que a data2 sem levar em consideracao a
     * hora
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean afterOrEqualIgnoreTime(Date date1, Date date2) {
        return equalsIgnoreTime(date1, date2) || toFirstTime(date1).after(toFirstTime(date2));
    }

    /**
     * Compara se a data1 e menor do que a data2 sem levar em consideracao os
     * milisegundos
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean before(Date date1, Date date2) {
        return toFirstMillisecond(date1).before(toFirstMillisecond(date2));
    }

    /**
     * Compara se a data1 e menor do que a data2 sem levar em consideracao a
     * hora
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean beforeIgnoreTime(Date date1, Date date2) {
        return toFirstTime(date1).before(toFirstTime(date2));
    }

    /**
     * Compara se a data1 e menor ou igual do que a data2 sem levar em
     * consideracao os milisegundos
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean beforeOrEqual(Date date1, Date date2) {
        return equals(date1, date2) || toFirstMillisecond(date1).before(toFirstMillisecond(date2));
    }

    /**
     * Compara se a data1 e menor do que a data2 sem levar em consideracao os
     * milisegundos
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean beforeOrEqualIgnoreTime(Date date1, Date date2) {
        return equalsIgnoreTime(date1, date2) || toFirstTime(date1).before(toFirstTime(date2));
    }

    /**
     * Retorna o ano da data
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        return get(date, Calendar.YEAR);
    }

    /**
     * Retorna o mes da data. Obs.: retorna o mes no formato natural e nao o do
     * calendar Ex.: Janeiro e 1 e nao 0
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        return get(date, Calendar.MONTH) + 1;
    }

    /**
     * Retorna o dia do mes
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        return get(date, Calendar.DATE);
    }

    /**
     * Retorna a hora
     *
     * @param date
     * @return
     */
    public static int getHour(Date date) {
        return get(date, Calendar.HOUR);
    }

    /**
     * Retorna o minuto
     *
     * @param date
     * @return
     */
    public static int getMinute(Date date) {
        return get(date, Calendar.MINUTE);
    }

    /**
     * Retorna o segundo
     *
     * @param date
     * @return
     */
    public static int getSecond(Date date) {
        return get(date, Calendar.SECOND);
    }

    /**
     * Retorna o milisegundo
     *
     * @param date
     * @return
     */
    public static int getMillisecond(Date date) {
        return get(date, Calendar.MILLISECOND);
    }

    /**
     * Define o ano da data
     *
     * @param date
     * @param year
     * @return
     */
    public static Date setYear(Date date, int year) {
        return set(date, Calendar.YEAR, year);
    }

    /**
     * Define o mes da data. Obs.: define o mes no formato natural e nao o do
     * calendar Ex.: Janeiro e 1 e nao 0
     *
     * @param date
     * @param month
     * @return
     */
    public static Date setMonth(Date date, int month) {
        return set(date, Calendar.MONTH, month - 1);
    }

    /**
     * Define o dia do mes
     *
     * @param date
     * @param day
     * @return
     */
    public static Date setDay(Date date, int day) {
        return set(date, Calendar.DATE, day);
    }

    /**
     * Define a hora
     *
     * @param date
     * @param hour
     * @return
     */
    public static Date setHour(Date date, int hour) {
        return set(date, Calendar.HOUR, hour);
    }

    /**
     * Define o minuto
     *
     * @param date
     * @param minute
     * @return
     */
    public static Date setMinute(Date date, int minute) {
        return set(date, Calendar.MINUTE, minute);
    }

    /**
     * Define o segundo
     *
     * @param date
     * @param second
     * @return
     */
    public static Date setSecond(Date date, int second) {
        return set(date, Calendar.SECOND, second);
    }

    /**
     * Define o milisegundo
     *
     * @param date
     * @param millisecond
     * @return
     */
    public static Date setMillisecond(Date date, int millisecond) {
        return set(date, Calendar.MILLISECOND, millisecond);
    }

    /**
     * Define a data
     *
     * @param date
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date setDate(Date date, int year, int month, int day) {
        Calendar c = toCalendar(date);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, day);
        return c.getTime();
    }

    /**
     * Define o tempo
     *
     * @param date
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date setTime(Date date, int hour, int minute, int second) {
        Calendar c = toCalendar(date);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c.getTime();
    }

    /**
     * Define o tempo
     *
     * @param date
     * @param hour
     * @param minute
     * @param second
     * @param millisecond
     * @return
     */
    public static Date setTime(Date date, int hour, int minute, int second, int millisecond) {
        Calendar c = toCalendar(date);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, millisecond);
        return c.getTime();
    }

    /**
     * Define a data e o tempo
     *
     * @param year
     * @param month
     * @param day
     * @param date
     * @param hour
     * @param minute
     * @param second
     * @param millisecond
     * @return
     */
    public static Date setDateTime(Date date, int year, int month, int day, int hour, int minute, int second) {
        Calendar c = toCalendar(date);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c.getTime();
    }

    /**
     * Define a data e o tempo
     *
     * @param year
     * @param month
     * @param day
     * @param date
     * @param hour
     * @param minute
     * @param second
     * @param millisecond
     * @return
     */
    public static Date setDateTime(Date date, int year, int month, int day, int hour, int minute, int second, int millisecond) {
        Calendar c = toCalendar(date);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, millisecond);
        return c.getTime();
    }

    /**
     * Cria um date com os parametros passados
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date newDate(int year, int month, int day) {
        return setDate(toFirstTime(new Date()), year, month, day);
    }

    /**
     * Cria um date com os parametros passados
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date newDate(int year, int month, int day, int hour, int minute, int second) {
        return setDateTime(toFirstTime(new Date()), year, month, day, hour, minute, second);
    }

    /**
     * Cria um date com os parametros passados
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @param millisecond
     * @return
     */
    public static Date newDate(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        return setDateTime(new Date(), year, month, day, hour, minute, second, millisecond);
    }

    /**
     * Verifica se a data referenciada pertence ao mês atual.
     *
     * @param mesReferente Mês a ser comparado.
     * @return true or false.
     */
    public static boolean isMesAtual(Date mesReferente) {
        int mesAtual = Integer.valueOf(DateUtils.getDataFormatada("yyyyMM", new Date()));
        int mesRef = Integer.valueOf(DateUtils.getDataFormatada("yyyyMM", mesReferente));
        if (mesAtual == mesRef) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Define a data passada para o formato YYYYMM.
     *
     * @param refDate
     * @return
     *
     * public static String getYearMonth(Date refDate) { if (refDate != null) {
     * String year = Integer.valueOf(getDataFormatada("YYYY", refDate)) + "";
     * int month = getMonth(refDate); if (month == 13) { month = 1; } return
     * year + StringUtils.right("0" + month, 2); } else { return null; } }
     */
    public static String getYearMonth(Date refDate) {
        // refDate = addDays(refDate, 1);
        return getDataFormatada("YYYYMM", refDate);
    }
}
