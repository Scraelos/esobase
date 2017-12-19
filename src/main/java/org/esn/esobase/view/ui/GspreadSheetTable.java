/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.ui;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TableFieldFactory;
import com.vaadin.v7.ui.TextArea;
import org.esn.esobase.data.repository.GSpreadSheetsWithDeprecated;
import org.esn.esobase.data.specification.GSpreadSheetEntitySpecification;
import org.esn.esobase.model.GSpreadSheetEntity;
import org.esn.esobase.security.SpringSecurityHelper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.viritin.v7.SortableLazyList;
import org.vaadin.viritin.v7.fields.MTable;
import org.vaadin.viritin.v7.grid.GeneratedPropertyListContainer;

/**
 *
 * @author scraelos
 */
public class GspreadSheetTable extends MTable implements RefreshableGrid {

    private final GeneratedPropertyListContainer container;
    private final int pageSize;
    private final GSpreadSheetsWithDeprecated repository;
    private final Specification<GSpreadSheetEntity> specification;

    public GspreadSheetTable(GeneratedPropertyListContainer container_, int pageSize_, GSpreadSheetsWithDeprecated repository_, Specification<GSpreadSheetEntity> specification_) {
        this.container = container_;
        this.pageSize = pageSize_;
        this.repository = repository_;
        this.specification = specification_;

    }

    public void build() {
        setSizeFull();
        setPageLength(pageSize);
        setContainerDataSource(container);
        setSortEnabled(false);
        setColumnWidth("rowNum", 100);
        setColumnHeader("rowNum", "№");

        setColumnHeader("textEn", "Текст");
        setColumnExpandRatio("textEn", 1);

        setColumnHeader("textRu", "Перевод");
        setColumnExpandRatio("textRu", 1);

        setColumnHeader("translateColumn", "№");
        setColumnExpandRatio("translateColumn", 1);

        setColumnWidth("infoColumn", 95);
        setColumnHeader("infoColumn", "");
        setTableFieldFactory(new TranslateTableFieldFactory());
        setEditable(true);

    }

    public void Load() {
        SortableLazyList lazyList = new SortableLazyList<>((int firstRow, boolean sortAscending, String property) -> repository.findAll(specification, new PageRequest(
                firstRow / pageSize,
                pageSize,
                sortAscending ? Sort.Direction.ASC : Sort.Direction.DESC,
                property == null ? "rowNum" : property
        )).getContent(),
                () -> (int) repository.count(specification),
                pageSize);
        container.setCollection(lazyList);
    }

    @Override
    public void Refresh() {
        Load();
    }

    @Override
    public void scrollToRow(int row, Object itemId, ScrollDestination destination) {
        setCurrentPageFirstItemIndex(row);
        select(itemId);
    }

    private class TranslateTableFieldFactory implements TableFieldFactory {

        @Override
        public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
            Field result = null;
            if (propertyId.equals("textRu")) {
                TextArea area = new TextArea();
                area.setSizeFull();
                if (!SpringSecurityHelper.hasRole("ROLE_DIRECT_ACCESS")) {
                    area.setReadOnly(true);
                }
                result = area;
            } else if (propertyId.equals("textEn")) {
                TextArea area = new TextArea();
                area.setSizeFull();
                area.setReadOnly(true);
                result = area;
            }
            return result;
        }

    }

}
