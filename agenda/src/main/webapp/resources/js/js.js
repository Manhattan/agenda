/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//<! [CDATA [
//onkeypress="return(MascaraMoeda(this,'.',',',event))"
function MascaraMoeda(objTextBox, SeparadorMilesimo, SeparadorDecimal, e)
{
    var sep = 0;
    var key = '';
    var i = j = 0;
    var len = len2 = 0;
    var strCheck = '0123456789';
    var aux = aux2 = '';
    var whichCode = (window.Event) ? e.which : e.keyCode;
    if (whichCode == 13 || whichCode == 8 || whichCode == 0)
        return true;
    key = String.fromCharCode(whichCode); // Valor para o código da Chave
    if (strCheck.indexOf(key) == -1)
        return false; // Chave inválida
    len = objTextBox.value.length;
    for (i = 0; i < len; i++)
        if ((objTextBox.value.charAt(i) != '0') && (objTextBox.value.charAt(i) != SeparadorDecimal))
            break;
    aux = '';
    for (; i < len; i++)
        if (strCheck.indexOf(objTextBox.value.charAt(i)) != -1)
            aux += objTextBox.value.charAt(i);
    aux += key;
    len = aux.length;
    if (len == 0)
        objTextBox.value = '';
    if (len == 1)
        objTextBox.value = '0' + SeparadorDecimal + '0' + aux;
    if (len == 2)
        objTextBox.value = '0' + SeparadorDecimal + aux;
    if (len > 2)
    {
        aux2 = '';
        for (j = 0, i = len - 3; i >= 0; i--)
        {
            if (j == 3) {
                aux2 += SeparadorMilesimo;
                j = 0;
            }
            aux2 += aux.charAt(i);
            j++;
        }
        objTextBox.value = '';
        len2 = aux2.length;
        for (i = len2 - 1; i >= 0; i--)
            objTextBox.value += aux2.charAt(i);
        objTextBox.value += SeparadorDecimal + aux.substr(len - 2, len);
    }
    return false;
}

/**
 * Mascara o valor informado e compara com o valor2, 
 * impedindo a entrada se o valor informado for menor que o valor2.
 **/
//onkeypress="return(MascaraMoedaComparando(this, #{mi.estoqueAtual},'','.', event))"
function MascaraMoedaComparando(objTextBox, valor2, SeparadorMilesimo, SeparadorDecimal, e)
{
    var objFinal = objTextBox.value;
    var sep = 0;
    var key = '';
    var i = j = 0;
    var len = len2 = 0;
    var strCheck = '0123456789';
    var aux = aux2 = '';
    var whichCode = (window.Event) ? e.which : e.keyCode;
    if (whichCode == 13 || whichCode == 8 || whichCode == 0)
        return true;
    key = String.fromCharCode(whichCode); // Valor para o código da Chave
    if (strCheck.indexOf(key) == -1)
        return false; // Chave inválida
    len = objFinal.length;
    for (i = 0; i < len; i++)
        if ((objFinal.charAt(i) != '0') && (objFinal.charAt(i) != SeparadorDecimal))
            break;
    aux = '';
    for (; i < len; i++)
        if (strCheck.indexOf(objFinal.charAt(i)) != -1)
            aux += objFinal.charAt(i);
    aux += key;
    len = aux.length;

    if (len == 0)
        objFinal = '';
    if (objFinal > valor2)
    {
        return false;
    }
    if (len == 1)
        objFinal = '0' + SeparadorDecimal + '0' + aux;
    if (objFinal > valor2)
    {
        return false;
    }
    if (len == 2)
        objFinal = '0' + SeparadorDecimal + aux;
    if (objFinal > valor2)
    {
        return false;
    }
    if (len > 2)
    {
        aux2 = '';
        for (j = 0, i = len - 3; i >= 0; i--)
        {
            if (j == 3)
            {
                aux2 += SeparadorMilesimo;
                j = 0;
            }
            aux2 += aux.charAt(i);
            j++;
        }
        objFinal = '';
        len2 = aux2.length;
        for (i = len2 - 1; i >= 0; i--)
            objFinal += aux2.charAt(i);
        objFinal += SeparadorDecimal + aux.substr(len - 2, len);
        if (objFinal > valor2)
        {
            return false;
        }
    }
    objTextBox.value = objFinal;
    return false;
}


function getInternetExplorer()
{
    if (navigator.appName == 'Microsoft Internet Explorer')
    {
        alert('Sorry, this site is not compatible with Internet Explorer.\nPlease use Google Chrome or Mozilla FireFox.');
        return false;
    }
    return true;
}

function infIE(idmsg)
{
    if (navigator.appName == 'Microsoft Internet Explorer') {
        document.getElementById(idmsg).innerText = "Desculpe, este site não suporta o Internet Explorer.\nPor favor, use o Google Chrome ou Mozilla FireFox";
    }
}

function confirme(msg)
{
    apagar = confirm(msg);//a variavel apagar aguarda um comando ok ou cancelar retornando assim false ou true
    return apagar;
}


function Contar(Campo, idmsg, limite)
{
    //alert(document.getElementById(idmsg).id);
    var max = parseInt(limite);
    document.getElementById(idmsg).innerText = max - Campo.length
    if ((max - Campo.length) <= 0) {
        document.getElementById(idmsg).style.color = "red";
    } else {
        document.getElementById(idmsg).style.color = "blue";
    }
}

function esconder(id)
{
    document.getElementById(id).style.visibility = "hidden";
    return true;
}

function mostrar(id)
{
    document.getElementById(id).style.visibility = "visible";
    return true;
}

function preencheZeros(param, tamanho)
{
    var contador = param.value.length;
    if (param.value.length != tamanho)
    {
        do
        {
            param.value = "0" + param.value;
            contador += 1;

        } while (contador < tamanho)
    }
}

function formatar(src, mask, e)
{
    var whichCode = (window.Event) ? e.which : e.keyCode;
    if (whichCode == 13 || whichCode == 8 || whichCode == 0)
        return true;
    var i = src.value.length;
    var saida = mask.substring(0, 1);
    var texto = mask.substring(i)
    if (texto.substring(0, 1) != saida)
    {
        src.value += texto.substring(0, 1);
    }
}

//onkeypress='return SomenteNumero(event)'
function SomenteNumero(e)
{
    var tecla = (window.event) ? event.keyCode : e.which;
    if ((tecla > 47 && tecla < 58))
        return true;
    else {
        if (tecla == 8 || tecla == 0 || tecla == 13)
            return true;
        else
            return false;
    }
}


function mudaCor(element, cor)
{
    element.style.backgroundColor = cor;
}

PrimeFaces.locales['pt'] = {
    closeText: 'Fechar',
    prevText: 'Anterior',
    nextText: 'Próximo',
    currentText: 'Começo',
    monthNames: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
    monthNamesShort: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
    dayNames: ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'],
    dayNamesShort: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'],
    dayNamesMin: ['D', 'S', 'T', 'Q', 'Q', 'S', 'S'],
    weekHeader: 'Semana',
    firstDay: 0,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Só Horas',
    timeText: 'Tempo',
    hourText: 'Hora',
    minuteText: 'Minuto',
    secondText: 'Segundo',
    ampm: false,
    month: 'Mês',
    week: 'Semana',
    day: 'Dia',
    allDayText: 'Todo o Dia'
};
PrimeFaces.locales['es'] = {
    closeText: 'Cerrar',
    prevText: 'Anterior',
    nextText: 'Siguiente',
    currentText: 'Inicio',
    monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
    monthNamesShort: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
    dayNames: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
    dayNamesShort: ['Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'],
    dayNamesMin: ['D', 'L', 'M', 'X', 'J', 'V', 'S'],
    weekHeader: 'Semana',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Sólo hora',
    timeText: 'Tempo',
    hourText: 'Hora',
    minuteText: 'Minuto',
    secondText: 'Segundo',
    currentText: 'Fecha actual',
            ampm: false,
    month: 'Mes',
    week: 'Semana',
    day: 'Día',
    allDayText: 'Todo el día'
};
PrimeFaces.locales['de'] = {
    closeText: 'Schließen',
    prevText: 'Zurück',
    nextText: 'Weiter',
    currentText: 'Start',
    monthNames: ['Januar', 'Februar', 'MÃ¤rz', 'April', 'Mai', 'Juni', 'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'],
    monthNamesShort: ['Jan', 'Feb', 'MÃ¤r', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'],
    dayNames: ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag'],
    dayNamesShort: ['Son', 'Mon', 'Die', 'Mit', 'Don', 'Fre', 'Sam'],
    dayNamesMin: ['S', 'M', 'D', 'M ', 'D', 'F ', 'S'],
    weekHeader: 'Woche',
    FirstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Nur Zeit',
    timeText: 'Zeit',
    hourText: 'Stunde',
    minuteText: 'Minute',
    secondText: 'Sekunde',
    currentText: 'Aktuelles Datum',
            ampm: false,
    month: 'Monat',
    week: 'Woche',
    day: 'Tag',
    allDayText: 'Ganzer Tag'
};

//onkeyup="mascara(this,soNumeros)"
function mascara(o, f)
{
    v_obj = o
    v_fun = f
    setTimeout("execmascara()", 1)
}
function execmascara()
{
    v_obj.value = v_fun(v_obj.value)
}
function soNumeros(v)
{
    return v.replace(/\D/g, "")
}

function telefone(v)
{
    v = v.replace(/\D/g, "")                 //Remove tudo o que não é dígito
    v = v.replace(/^(\d\d)(\d)/g, "($1) $2") //Coloca parênteses em volta dos dois primeiros dígitos
    v = v.replace(/(\d{4})(\d)/, "$1-$2")    //Coloca hífen entre o quarto e o quinto dígitos
    return v
}

function cpf(v)
{
    v = v.replace(/\D/g, "")                    //Remove tudo o que não é dígito
    v = v.replace(/(\d{3})(\d)/, "$1.$2")       //Coloca um ponto entre o terceiro e o quarto dígitos
    v = v.replace(/(\d{3})(\d)/, "$1.$2")       //Coloca um ponto entre o terceiro e o quarto dígitos
    //de novo (para o segundo bloco de números)
    v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2") //Coloca um hífen entre o terceiro e o quarto dígitos
    return v
}

function cep(v)
{
    v = v.replace(/D/g, "")                //Remove tudo o que não é dígito
    v = v.replace(/^(\d{5})(\d)/, "$1-$2") //Esse é tão fácil que não merece explicações
    return v
}

function cnpj(v)
{
    v = v.replace(/\D/g, "")                           //Remove tudo o que não é dígito
    v = v.replace(/^(\d{2})(\d)/, "$1.$2")             //Coloca ponto entre o segundo e o terceiro dígitos
    v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3") //Coloca ponto entre o quinto e o sexto dígitos
    v = v.replace(/\.(\d{3})(\d)/, ".$1/$2")           //Coloca uma barra entre o oitavo e o nono dígitos
    v = v.replace(/(\d{4})(\d)/, "$1-$2")              //Coloca um hífen depois do bloco de quatro dígitos
    return v
}

function romanos(v)
{
    v = v.toUpperCase()             //Maiúsculas
    v = v.replace(/[^IVXLCDM]/g, "") //Remove tudo o que não for I, V, X, L, C, D ou M
    //Essa é complicada! Copiei daqui: http://www.diveintopython.org/refactoring/refactoring.html
    while (v.replace(/^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$/, "") != "")
        v = v.replace(/.$/, "")
    return v
}

function site(v)
{
    //Esse sem comentarios para que você entenda sozinho ;-)
    v = v.replace(/^http:\/\/?/, "")
    dominio = v
    caminho = ""
    if (v.indexOf("/") > -1)
        dominio = v.split("/")[0]
    caminho = v.replace(/[^\/]*/, "")
    dominio = dominio.replace(/[^\w\.\+-:@]/g, "")
    caminho = caminho.replace(/[^\w\d\+-@:\?&=%\(\)\.]/g, "")
    caminho = caminho.replace(/([\?&])=/, "$1")
    if (caminho != "")
        dominio = dominio.replace(/\.+$/, "")
    v = "http://" + dominio + caminho
    return v
}


function AtalhoData(e, element)
{
    var data = new Date();
    var tecla = (window.Event) ? e.which : e.keyCode;
    //104 - tecla: h (HOJE)
    if (tecla === 104)
    {
        element.value = DataFormatoDMA(data);
    }
    else
    //97 - tecla: a (AMANHA)
    if (tecla === 97)
    {
        data = new Date(data.setDate(data.getDate() + 1));
        element.value = DataFormatoDMA(data);
    }
    else
    //111 - tecla: o (ONTEM)
    if (tecla === 111)
    {
        data = new Date(data.setDate(data.getDate() - 1));
        element.value = DataFormatoDMA(data);
    }
    else
    if (SomenteNumero(e) && tecla !== 8)
    {
        mascaraData(element);
    }
}

function DataFormatoDMA(data)
{
    dia = data.getDate();
    dia = ('0' + dia).substr(('0' + dia).length - 2, 2);
    mes = data.getMonth() + 1;
    mes = ('0' + mes).substr(('0' + mes).length - 2, 2);
    ano = data.getFullYear();
    return (dia + '/' + mes + '/' + ano);
}

function mascaraData(campoData)
{
    var data = campoData.value;
    if (data.length === 2) {
        data = data + '/';
        campoData.value = data;
        return true;
    }
    if (data.length === 5) {
        data = data + '/';
        campoData.value = data;
        return true;
    }
}

function AtualizaData(campoData)
{
    if (campoData.value.length === 8)
    {
        var yy = parseInt(new Date().getFullYear().toString().substr(2, 2));
        var a = parseInt(campoData.value.substr(6, 2)) >= yy + 6 ? "19" : "20";
        campoData.value = campoData.value.substr(0, 6) + a + campoData.value.substr(6, 2);
    }
}

function solicitadoIgualSaldo(element)
{
    element.value = document.getElementById(element.id.substr(0, 46) + "saldoSolic").innerHTML;
    if (parseFloat(element.value) < 0){
        element.value = 0;
    }
}