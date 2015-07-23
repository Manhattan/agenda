package br.com.grupopibb.portalobra.controller.common;

import javax.faces.model.DataModel;

/**
 *  Classe utilitária para paginação em tabelas.
 */
public abstract class EntityPagination {

    /**
     * Armazena a quantidade de itens na grid por página.
     */
    private int pageSize;
    /**
     * Armazena a página atual.
     */
    private int page;
    /**
     * Define a quantidade de objetos exibidos na tabela pode página.
     */
    protected static final Integer QUANTIDADE_ITENS_PAGINA = 20;

    /**
     *
     * Default, determina 5 itens por página.
     */
    public EntityPagination() {
        this(QUANTIDADE_ITENS_PAGINA);
    }

    /**
     * Utilize para alterar o padrão de 5 linhas.
     * @param size Define a quantidade de itens por página.
     */
    public EntityPagination(final int size) {
        this.pageSize = size;
    }

    /**
     * Deve informar a quantidade de itens da tabela, se houver filtros
     * estes devem ser utilizados na consulta de contagem também.
     * @return Quantidade de itens total.
     */
    public abstract int getItemsCount();

    /**
     * Cria o DataModel que terá os item da página exibida.
     * @return DataModel com itens da página atual.
     */
    public abstract DataModel createPageDataModel();

    /**
     * Determina a posição do primeiro item da página atual.
     * Uma pagina com até 5 itens a terceira página seria 3 * 5, o
     * primero item seria a 15ª linha da tabela.
     * @return A posição do primeiro item da página.
     */
    public int getPageFirstItem() {
        return page * pageSize;
    }

    /**
     * Deterina o ultimo item da tabela, considerando que a página pode
     * ir até 5, ele garante que se houver menos de 5 a contagem será
     * correta.
     * @return A posição do ultimo item na tabela.
     */
    public int getPageLastItem() {
        int i = getPageFirstItem() + pageSize - 1;
        int count = getItemsCount() - 1;
        if (i > count) {
            i = count;
        }
        if (i < 0) {
            i = 0;
        }
        return i;
    }

    /**
     * Verifica se exitem mais itens que podem ser exibidos na grid,
     * liberando o botão próximo se houver.
     * @return true para exite proxima página, false para não existe.
     */
    public boolean isHasNextPage() {
        return (page + 1) * pageSize + 1 <= getItemsCount();
    }

    /**
     * Avança para proxima página.
     */
    public void nextPage() {
        if (isHasNextPage()) {
            page++;
        }
    }

    /**
     * Informa se existe página anterior.
     * @return True para existe e false para não.
     */
    public boolean isHasPreviousPage() {
        return page > 0;
    }

    /**
     * Retorna para a página anterior.
     */
    public void previousPage() {
        if (isHasPreviousPage()) {
            page--;
        }
    }

    /**
     * Quantidade de itens na página atual.
     * @return Itens da página.
     */
    public int getPageSize() {
        return pageSize;
    }
}
