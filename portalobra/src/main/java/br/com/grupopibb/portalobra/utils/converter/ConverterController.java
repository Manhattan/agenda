/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupopibb.portalobra.utils.converter;

import br.com.grupopibb.portalobra.acesso.PerfilAcesso;
import br.com.grupopibb.portalobra.dao.acesso.PerfilAcessoFacade;
import br.com.grupopibb.portalobra.dao.ar.DocumentoEntradaTipoFacade;
import br.com.grupopibb.portalobra.dao.geral.CentroCustoFacade;
import br.com.grupopibb.portalobra.dao.geral.CredorFacade;
import br.com.grupopibb.portalobra.dao.geral.EmpresaFacade;
import br.com.grupopibb.portalobra.dao.geral.EspecieCredorFacade;
import br.com.grupopibb.portalobra.dao.geral.NaturezaFiscalFacade;
import br.com.grupopibb.portalobra.dao.geral.NucleoNegocioFacade;
import br.com.grupopibb.portalobra.dao.insumo.CaracterizacaoInsumosFacade;
import br.com.grupopibb.portalobra.dao.insumo.ClasseInsumosFacade;
import br.com.grupopibb.portalobra.dao.insumo.GrupoInsumosFacade;
import br.com.grupopibb.portalobra.dao.pedido.PedidoFacade;
import br.com.grupopibb.portalobra.dao.solicitacaocompra.SolicitanteFacade;
import br.com.grupopibb.portalobra.model.ar.DocumentoEntradaTipo;
import br.com.grupopibb.portalobra.model.geral.CentroCusto;
import br.com.grupopibb.portalobra.model.geral.Credor;
import br.com.grupopibb.portalobra.model.geral.Empresa;
import br.com.grupopibb.portalobra.model.geral.EspecieCredor;
import br.com.grupopibb.portalobra.model.geral.NaturezaFiscal;
import br.com.grupopibb.portalobra.model.geral.NucleoNegocio;
import br.com.grupopibb.portalobra.model.insumo.CaracterizacaoInsumos;
import br.com.grupopibb.portalobra.model.insumo.ClasseInsumos;
import br.com.grupopibb.portalobra.model.insumo.GrupoInsumos;
import br.com.grupopibb.portalobra.model.pedido.Pedido;
import br.com.grupopibb.portalobra.model.solicitacaocompra.Solicitante;
import br.com.grupopibb.portalobra.model.tipos.EnumRecolhimentoImposto;
import br.com.grupopibb.portalobra.utils.JsfUtil;
import java.io.Serializable;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.commons.lang3.StringUtils;

/**
 *
 *
 */
@ManagedBean
@RequestScoped
public class ConverterController {

    @EJB
    private ClasseInsumosFacade classeInsumosFacade;
    @EJB
    private GrupoInsumosFacade grupoInsumosFacade;
    @EJB
    private CaracterizacaoInsumosFacade caracterizacaoInsumosFacade;
    @EJB
    private CentroCustoFacade centroCustoFacade;
    @EJB
    private EspecieCredorFacade especieCredorFacade;
    @EJB
    private NaturezaFiscalFacade naturezaFiscalFacade;
    @EJB
    private DocumentoEntradaTipoFacade documentoEntradaTipoFacade;
    @EJB
    private CredorFacade credorFacade;
    @EJB
    private SolicitanteFacade solicitanteFacade;
    @EJB
    private PerfilAcessoFacade perfilAcessoFacade;
    @EJB
    private EmpresaFacade empresaFacade;
    @EJB
    private NucleoNegocioFacade nucleoFacade;
    @EJB
    private PedidoFacade pedidoFacade;
    //====================
    // Conversor (ClasseInsumos)
    //====================

    /**
     * Conversor para classe ClasseInsumos.
     */
    @FacesConverter(forClass = ClasseInsumos.class, value = "classeInsumosConverter")
    public static class ClasseInsumosConverter implements Converter {

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
            if (value == null || value.length() == 0) {
                return null;
            }
            ConverterController controller =
                    JsfUtil.getController("converterController", facesContext);
            if (!StringUtils.isNumeric(value)) {
                return null;
            }
            return controller.classeInsumosFacade.find(String.valueOf(value));
        }

        /**
         * Retorna o objeto DetalheProcedimento em formato de String, que
         * representa um DetalheProcedimento.
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
            if (object instanceof ClasseInsumos) {
                ClasseInsumos o = (ClasseInsumos) object;
                return o.getId().toString();
            } else {
                throw new IllegalArgumentException("object " + object
                        + " is of type " + object.getClass().getName()
                        + "; expected type: "
                        + ClasseInsumos.class.getName());
            }
        }
    }
    //====================
    // Conversor (GrupoInsumos)
    //====================

    /**
     * Conversor para classe GrupoInsumos.
     */
    @FacesConverter(forClass = GrupoInsumos.class, value = "grupoInsumosConverter")
    public static class GrupoInsumosConverter implements Converter {

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
            if (value == null || value.length() == 0) {
                return null;
            }
            ConverterController controller =
                    JsfUtil.getController("converterController", facesContext);
            if (!StringUtils.isNumeric(value)) {
                return null;
            }
            return controller.grupoInsumosFacade.findByClasseGrupo(String.valueOf(value));
        }

        /**
         * Retorna o objeto DetalheProcedimento em formato de String, que
         * representa um DetalheProcedimento.
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
            if (object instanceof GrupoInsumos) {
                GrupoInsumos o = (GrupoInsumos) object;
                return o.getId().toString();
            } else {
                throw new IllegalArgumentException("object " + object
                        + " is of type " + object.getClass().getName()
                        + "; expected type: "
                        + ClasseInsumos.class.getName());
            }
        }
    }
    //====================
    // Conversor (CaracterizacaoInsumos)
    //====================

    /**
     * Conversor para classe CaracterizacaoInsumos.
     */
    @FacesConverter(forClass = CaracterizacaoInsumos.class, value = "caracterizacaoInsumosConverter")
    public static class CaracterizacaoInsumosConverter implements Converter {

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
            if (value == null || value.length() == 0) {
                return null;
            }
            ConverterController controller =
                    JsfUtil.getController("converterController", facesContext);
            if (!StringUtils.isNumeric(value)) {
                return null;
            }
            return controller.caracterizacaoInsumosFacade.findByClasseGrupoCarac(String.valueOf(value));
        }

        /**
         * Retorna o objeto DetalheProcedimento em formato de String, que
         * representa um DetalheProcedimento.
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
            if (object instanceof CaracterizacaoInsumos) {
                CaracterizacaoInsumos o = (CaracterizacaoInsumos) object;
                return o.getId().toString();
            } else {
                throw new IllegalArgumentException("object " + object
                        + " is of type " + object.getClass().getName()
                        + "; expected type: "
                        + ClasseInsumos.class.getName());
            }
        }
    }
    //====================
    // Conversor (CentroCusto)
    //====================

    /**
     * Conversor para classe CentroCusto.
     */
    @FacesConverter(forClass = CentroCusto.class, value = "centroCustoConverter")
    public static class CentroCustoConverter implements Converter, Serializable {

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
            if (value != null && value.length() > 0 && !value.isEmpty()) {

                CentroCusto c = (CentroCusto) this.getAttributesFrom(component).get(value);
                return c;
            } else {
                return null;
            }
        }

        /**
         * Retorna o objeto DetalheProcedimento em formato de String, que
         * representa um DetalheProcedimento.
         *
         * @param facesContext Contexto atual.
         * @param component Componente JSF que mostra o Usuário na tela.
         * @param object Um DetalheProcedimento.
         * @return
         */
        @Override
        public String getAsString(final FacesContext facesContext,
                final UIComponent component, final Object object) {

            if (object != null && object instanceof CentroCusto) {
                CentroCusto o = (CentroCusto) object;
                this.addAttribute(component, o);
                return o.getId().toString();
            } else {
                return null;
            }
        }

        protected void addAttribute(UIComponent component, CentroCusto c) {
            String key = String.valueOf(c.getId());
            this.getAttributesFrom(component).put(key, c);
        }

        protected Map<String, Object> getAttributesFrom(UIComponent component) {
            return component.getAttributes();
        }
    }

    //====================
    // Conversor (EspecieCredor)
    //====================
    /**
     * Conversor para classe EspecieCredor.
     */
    @FacesConverter(forClass = EspecieCredor.class, value = "especieCredorConverter")
    public static class EspecieCredorConverter implements Converter {

        @Override
        public Object getAsObject(final FacesContext facesContext,
                final UIComponent component, final String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConverterController controller =
                    JsfUtil.getController("converterController", facesContext);
            return controller.especieCredorFacade.find(value);
        }

        /**
         * Retorna o objeto EspecieCredor em formato de String, que representa
         * um EspecieCredor.
         *
         * @param facesContext Contexto atual.
         * @param component Componente JSF que mostra o Usuário na tela.
         * @param object Um EspecieCredor.
         * @return
         */
        @Override
        public String getAsString(final FacesContext facesContext,
                final UIComponent component, final Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof EspecieCredor) {
                EspecieCredor o = (EspecieCredor) object;
                return o.getId().toString();
            } else {
                throw new IllegalArgumentException("object " + object
                        + " is of type " + object.getClass().getName()
                        + "; expected type: "
                        + EspecieCredor.class.getName());
            }
        }
    }

    //====================
    // Conversor (NaturezaFiscal)
    //====================
    /**
     * Conversor para classe NaturezaFiscal.
     */
    @FacesConverter(forClass = NaturezaFiscal.class, value = "naturezaFiscalConverter")
    public static class NaturezaFiscalConverter implements Converter {

        @Override
        public Object getAsObject(final FacesContext facesContext,
                final UIComponent component, final String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConverterController controller =
                    JsfUtil.getController("converterController", facesContext);
            return controller.naturezaFiscalFacade.find(value);
        }

        /**
         * Retorna o objeto NaturezaFiscal em formato de String, que representa
         * um NaturezaFiscal.
         *
         * @param facesContext Contexto atual.
         * @param component Componente JSF que mostra o Usuário na tela.
         * @param object Um EspecieCredor.
         * @return
         */
        @Override
        public String getAsString(final FacesContext facesContext,
                final UIComponent component, final Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof NaturezaFiscal) {
                NaturezaFiscal o = (NaturezaFiscal) object;
                return o.getId().toString();
            } else {
                throw new IllegalArgumentException("object " + object
                        + " is of type " + object.getClass().getName()
                        + "; expected type: "
                        + NaturezaFiscal.class.getName());
            }
        }
    }

    //====================
    // Conversor (DocumentoEntradaTipo)
    //====================
    /**
     * Conversor para classe DocumentoEntradaTipo.
     */
    @FacesConverter(forClass = DocumentoEntradaTipo.class, value = "documentoEntradaTipoConverter")
    public static class DocumentoEntradaTipoConverter implements Converter {

        @Override
        public Object getAsObject(final FacesContext facesContext,
                final UIComponent component, final String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConverterController controller =
                    JsfUtil.getController("converterController", facesContext);
            return controller.documentoEntradaTipoFacade.find(value);
        }

        /**
         * Retorna o objeto DocumentoEntradaTipo em formato de String, que
         * representa um DocumentoEntradaTipo.
         *
         * @param facesContext Contexto atual.
         * @param component Componente JSF que mostra o Usuário na tela.
         * @param object Um EspecieCredor.
         * @return
         */
        @Override
        public String getAsString(final FacesContext facesContext,
                final UIComponent component, final Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof DocumentoEntradaTipo) {
                DocumentoEntradaTipo o = (DocumentoEntradaTipo) object;
                return o.getId().toString();
            } else {
                throw new IllegalArgumentException("object " + object
                        + " is of type " + object.getClass().getName()
                        + "; expected type: "
                        + DocumentoEntradaTipo.class.getName());
            }
        }
    }

    //====================
    // Conversor (Credor)
    //====================
    /**
     * Conversor para classe Credor.
     */
    @FacesConverter(value = "credorConverter", forClass = Credor.class)
    public static class CredorConverter implements Converter, Serializable {

        @Override
        public Object getAsObject(final FacesContext facesContext,
                final UIComponent component, final String value) {
            if (value != null && !value.isEmpty() && value.length() > 0) {
                Credor c = (Credor) this.getAttributesFrom(component).get(value);
                return c;
            } else {

                return null;
            }
        }

        /**
         * Retorna o objeto Credor em formato de String, que representa um
         * Credor.
         *
         * @param facesContext Contexto atual.
         * @param component Componente JSF que mostra o Usuário na tela.
         * @param object Um EspecieCredor.
         * @return
         */
        @Override
        public String getAsString(final FacesContext facesContext,
                final UIComponent component, final Object object) {

            if (object != null && object instanceof Credor) {

                Credor o = (Credor) object;
                this.addAttribute(component, o);
                return o.getCodigo().toString();

            } else {
                return null;
            }
        }

        protected void addAttribute(UIComponent component, Credor c) {
            String key = String.valueOf(c.getCodigo());
            this.getAttributesFrom(component).put(key, c);
        }

        protected Map<String, Object> getAttributesFrom(UIComponent component) {
            return component.getAttributes();
        }
    }

    //====================
    // Conversor (EnumRecolhimentoImposto)
    //====================
    /**
     * Conversor para classe EnumRecolhimentoImposto.
     */
    @FacesConverter(forClass = EnumRecolhimentoImposto.class, value = "enumRecolhimentoImpostoConverter")
    public static class EnumRecolhimentoImpostoConverter implements Converter {

        @Override
        public Object getAsObject(final FacesContext facesContext,
                final UIComponent component, final String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EnumRecolhimentoImposto enumm = EnumRecolhimentoImposto.getForLabel(value);
            return enumm;
        }

        /**
         * Retorna o objeto EnumRecolhimentoImposto em formato de String, que
         * representa um EnumRecolhimentoImposto.
         *
         * @param facesContext Contexto atual.
         * @param component Componente JSF que mostra o Usuário na tela.
         * @param object Um EspecieCredor.
         * @return
         */
        @Override
        public String getAsString(final FacesContext facesContext,
                final UIComponent component, final Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof String) {
                EnumRecolhimentoImposto o = EnumRecolhimentoImposto.getForLabel(object.toString());
                return o.getLabel();
            } else if (object instanceof EnumRecolhimentoImposto) {
                EnumRecolhimentoImposto o = (EnumRecolhimentoImposto) object;
                return o.getLabel();
            } else {
                throw new IllegalArgumentException("object " + object
                        + " is of type " + object.getClass().getName()
                        + "; expected type: "
                        + String.class.getName());
            }

        }

        //====================
        // Conversor (Solicitante)
        //====================
        /**
         * Conversor para classe Solicitante.
         */
        @FacesConverter(forClass = Solicitante.class, value = "solicitanteConverter")
        public static class SolicitanteConverter implements Converter {

            @Override
            public Object getAsObject(final FacesContext facesContext,
                    final UIComponent component, final String value) {
                if (value == null || value.length() == 0) {
                    return null;
                }
                ConverterController controller =
                        JsfUtil.getController("converterController", facesContext);
                return controller.solicitanteFacade.find(value);
            }

            /**
             * Retorna o objeto Solicitante em formato de String, que representa
             * um Solicitante.
             *
             * @param facesContext Contexto atual.
             * @param component Componente JSF que mostra o Usuário na tela.
             * @param object Um Solicitante.
             * @return
             */
            @Override
            public String getAsString(final FacesContext facesContext,
                    final UIComponent component, final Object object) {
                if (object == null) {
                    return null;
                }
                if (object instanceof Solicitante) {
                    Solicitante o = (Solicitante) object;
                    return o.getId().toString();
                } else {
                    throw new IllegalArgumentException("object " + object
                            + " is of type " + object.getClass().getName()
                            + "; expected type: "
                            + Solicitante.class.getName());
                }
            }
            //====================
            // Conversor (PerfilAcesso)
            //====================

            /**
             * Conversor para classe PerfilAcesso.
             */
            @FacesConverter(forClass = PerfilAcesso.class, value = "perfilAcessoConverter")
            public static class PerfilAcessoConverter implements Converter {

                @Override
                public Object getAsObject(final FacesContext facesContext,
                        final UIComponent component, final String value) {
                    if (value == null || value.length() == 0) {
                        return null;
                    }
                    Integer vl;
                    try {
                        vl = Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        return null;
                    }

                    ConverterController controller =
                            JsfUtil.getController("converterController", facesContext);
                    return controller.perfilAcessoFacade.find(vl);
                }

                /**
                 * Retorna o objeto PerfilAcesso em formato de String, que
                 * representa um PerfilAcesso.
                 *
                 * @param facesContext Contexto atual.
                 * @param component Componente JSF que mostra o Usuário na tela.
                 * @param object Um PerfilAcesso.
                 * @return
                 */
                @Override
                public String getAsString(final FacesContext facesContext,
                        final UIComponent component, final Object object) {
                    if (object == null) {
                        return null;
                    }
                    if (object instanceof PerfilAcesso) {
                        PerfilAcesso o = (PerfilAcesso) object;
                        return o.getId().toString();
                    } else {
                        throw new IllegalArgumentException("object " + object
                                + " is of type " + object.getClass().getName()
                                + "; expected type: "
                                + PerfilAcesso.class.getName());
                    }
                }
            }

            /**
             * Conversor para classe Empresa.
             */
            @FacesConverter(forClass = Empresa.class, value = "empresaConverter")
            public static class EmpresaConverter implements Converter {

                @Override
                public Object getAsObject(final FacesContext facesContext,
                        final UIComponent component, final String value) {
                    if (value == null || StringUtils.isBlank(value)) {
                        return null;
                    }

                    ConverterController controller =
                            JsfUtil.getController("converterController", facesContext);
                    return controller.empresaFacade.find(value);
                }

                /**
                 * Retorna o objeto Empresa em formato de String, que representa
                 * uma Empresa.
                 *
                 * @param facesContext Contexto atual.
                 * @param component Componente JSF que mostra o Usuário na tela.
                 * @param object Um Empresa.
                 * @return
                 */
                @Override
                public String getAsString(final FacesContext facesContext,
                        final UIComponent component, final Object object) {
                    if (object == null) {
                        return null;
                    }
                    if (object instanceof Empresa) {
                        Empresa o = (Empresa) object;
                        return o.getId().toString();
                    } else {
                        throw new IllegalArgumentException("object " + object
                                + " is of type " + object.getClass().getName()
                                + "; expected type: "
                                + Empresa.class.getName());
                    }
                }
            }

            /**
             * Conversor para classe NucleoNegocio.
             */
            @FacesConverter(forClass = NucleoNegocio.class, value = "nucleoConverter")
            public static class NucleoConverter implements Converter {

                @Override
                public Object getAsObject(final FacesContext facesContext,
                        final UIComponent component, final String value) {
                    if (value == null || value.length() == 0) {
                        return null;
                    }

                    ConverterController controller =
                            JsfUtil.getController("converterController", facesContext);
                    return controller.nucleoFacade.find(value);
                }

                /**
                 * Retorna o objeto NucleoNegocio em formato de String, que
                 * representa um NucleoNegocio.
                 *
                 * @param facesContext Contexto atual.
                 * @param component Componente JSF que mostra o Usuário na tela.
                 * @param object Um NucleoNegocio.
                 * @return
                 */
                @Override
                public String getAsString(final FacesContext facesContext,
                        final UIComponent component, final Object object) {
                    if (object == null) {
                        return null;
                    }
                    if (object instanceof NucleoNegocio) {
                        NucleoNegocio o = (NucleoNegocio) object;
                        return o.getId().toString();
                    } else {
                        throw new IllegalArgumentException("object " + object
                                + " is of type " + object.getClass().getName()
                                + "; expected type: "
                                + NucleoNegocio.class.getName());
                    }
                }
            }

            /**
             * Conversor para classe Empresa.
             */
//            @FacesConverter(value = "pedidoConverter", forClass = Pedido.class)
//            public static class PedidoConverter implements Converter {
//
//                @Override
//                public Object getAsObject(final FacesContext facesContext,
//                        final UIComponent component, final String value) {
//                    if (value == null || StringUtils.isBlank(value)) {
//                        return null;
//                    }
//
//                    ConverterController controller =
//                            JsfUtil.getController("converterController", facesContext);
//                    return controller.pedidoFacade.find(Long.parseLong(value));
//                }
//
//                /**
//                 * Retorna o objeto Empresa em formato de String, que representa
//                 * uma Empresa.
//                 *
//                 * @param facesContext Contexto atual.
//                 * @param component Componente JSF que mostra o Usuário na tela.
//                 * @param object Um Empresa.
//                 * @return
//                 */
//                @Override
//                public String getAsString(final FacesContext facesContext,
//                       final UIComponent component, final Object object) {
//                    if (object == null) {
//                        return null;
//                    }
//                    if (object instanceof Pedido) {
//                        Pedido o = (Pedido) object;
//                        return o.getId().toString();
//                    } else {
//                        throw new IllegalArgumentException("object " + object
//                                + " is of type " + object.getClass().getName()
//                                + "; expected type: "
//                                + Pedido.class.getName());
//                    }
//                }
//            }
            //====================
            // Conversor (Pedido)
            //====================
            /**
             * Conversor para classe Pedido.
             */
            @FacesConverter(value = "pedidoConverter", forClass = Pedido.class)
            public static class PedidoConverter implements Converter, Serializable {

                @Override
                public Object getAsObject(final FacesContext facesContext,
                        final UIComponent component, final String value) {
                    if (value != null && !value.isEmpty() && value.length() > 0) {
                        Pedido p = (Pedido) this.getAttributesFrom(component).get(value);
                        return p;

                    } else {
                     
                        return null;
                    
                    }
                }

                /**
                 * Retorna o objeto Pedido em formato de String, que representa
                 * um Pedido.
                 *
                 * @param facesContext Contexto atual.
                 * @param component Componente JSF que mostra o Usuário na tela.
                 * @param object Um Pedido.
                 * @return
                 */
                @Override
                public String getAsString(final FacesContext facesContext,
                        final UIComponent component, final Object object) {

                    if (object != null && object instanceof Pedido) {

                        Pedido p = (Pedido) object;
                        this.addAttribute(component, p);
                        return p.getNumero().toString();

                    } else {
                        return null;
                    }
                }

                protected void addAttribute(UIComponent component, Pedido p) {
                    String key = String.valueOf(p.getNumero());
                    this.getAttributesFrom(component).put(key, p);
                }

                protected Map<String, Object> getAttributesFrom(UIComponent component) {
                    return component.getAttributes();
                }
            }
        }
    }
}