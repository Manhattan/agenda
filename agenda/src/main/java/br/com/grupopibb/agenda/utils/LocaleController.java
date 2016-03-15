/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.agenda.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

    /**
 * Manager Bean jsf responsável por gerenciar a internacionalização
 * da aplicação.
 * @author Guilherme Braga
 * @since 29/04/2012
 */
@ManagedBean
@SessionScoped
public class LocaleController {
    /**
     * Referente ao idioma do usuário.
     */
    private Locale locale;

    /**
     * Após ser construído o bean recupera o Locale atual do usuário.
     */
    @PostConstruct
    public void init() {
        FacesContext jsf = FacesContext.getCurrentInstance();
        locale = jsf.getExternalContext().getRequestLocale();
    }

    /**
     * Getter to Locale.
     * @return Locale atual.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Seter to locale.
     * @param localeDefinido Locale a ser definido.
     */
    public void setLocale(final Locale localeDefinido) {
        this.locale = localeDefinido;
    }

    /**
     * Devolve os locales do facesConfig.xml.
     * @return SelectItem[] com locales.
     */
    public SelectItem[] getLocales() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        FacesContext jsf = FacesContext.getCurrentInstance();
        Application app = jsf.getApplication();
        Iterator<Locale> supportedLocales = app.getSupportedLocales();
        while (supportedLocales.hasNext()) {
            Locale loc = supportedLocales.next();
            items.add(new SelectItem(loc.toString(),
                    loc.getDisplayName()));
        }
        return items.toArray(new SelectItem[] {});
    }

    /**
     * Devolve o Locale atual.
     * @return String locale, pt_BR...
     */
    public String getSelectedLocale() {
        return getLocale().toString();
    }

    /**
     * Recupera o Locale selecionado.
     */
    public void defineLocale() {
        FacesContext jsf = FacesContext.getCurrentInstance();
        ExternalContext exContext = jsf.getExternalContext();
        Map<String, String> map = exContext.getRequestParameterMap();
        setSelectedLocale(map.get("locale"));
    }

    /**
     * Define o locale selecionado.
     * @param localeString Locale selecionado.
     */
   public void setSelectedLocale(final String localeString) {
        FacesContext jsf = FacesContext.getCurrentInstance();
        Application app = jsf.getApplication();
        Iterator<Locale> supportedLocales = app.getSupportedLocales();
        while (supportedLocales.hasNext()) {
            Locale loc = supportedLocales.next();
            if (loc.toString().equals(localeString)) {
                this.locale = loc;
                break;
            }
        }
    }
}


