/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.ui;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.shared.ui.grid.ColumnResizeMode;
import com.vaadin.v7.shared.ui.grid.GridClientRpc;
import com.vaadin.v7.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.DetailsGenerator;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.viritin.v7.SortableLazyList;
import org.vaadin.viritin.v7.grid.GeneratedPropertyListContainer;
import org.vaadin.viritin.grid.MGrid;

/**
 *
 * @author scraelos
 */
public class GspreadSheetGrid extends Grid implements RefreshableGrid, DetailsGenerator, ItemClickEvent.ItemClickListener {

    private final GeneratedPropertyListContainer container;
    private final int pageSize;
    private final JpaRepository repository;

    public GspreadSheetGrid(GeneratedPropertyListContainer container_, int pageSize_, JpaRepository repository_) {
        this.container = container_;
        this.pageSize = pageSize_;
        this.repository = repository_;

    }

    public void build() {
        setSizeFull();
        setStyleName("my-grid");
        setCellStyleGenerator(new CustomCellStyleGenerator());
        setSelectionMode(Grid.SelectionMode.NONE);
        setContainerDataSource(container);
        setDetailsGenerator(this);
        addItemClickListener(this);
        getColumn("rowNum").setWidth(100).setHeaderCaption("№").setExpandRatio(1).setSortable(false);
        getColumn("textEn").setMinimumWidth(100).setMaximumWidth(610).setExpandRatio(1).setHeaderCaption("Текст").setSortable(false);
        getColumn("textRu").setMinimumWidth(100).setMaximumWidth(610).setExpandRatio(1).setHeaderCaption("Перевод").setSortable(false);
        getColumn("translateColumn").setRenderer(new ComponentRenderer()).setWidth(470).setExpandRatio(2).setHeaderCaption("").setSortable(false);
        getColumn("infoColumn").setRenderer(new ComponentRenderer()).setWidth(95).setHeaderCaption("").setSortable(false);
        setColumnResizeMode(ColumnResizeMode.SIMPLE);
    }
    
    @Override
    public void Refresh() {
        SortableLazyList lazyList = new SortableLazyList<>((int firstRow, boolean sortAscending, String property) -> repository.findAll(new PageRequest(
                firstRow / pageSize,
                pageSize,
                sortAscending ? Sort.Direction.ASC : Sort.Direction.DESC,
                property == null ? "rowNum" : property
        )).getContent(),
                () -> (int) repository.count(),
                pageSize);
        container.setCollection(lazyList);
        this.setSizeFull();
    }

    @Override
    public void scrollToRow(int row, ScrollDestination destination) {
        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToRow(row, destination);
    }

    @Override
    public Component getDetails(RowReference rowReference) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth(100f, Unit.PERCENTAGE);
        hl.setHeight(100f, Unit.PIXELS);
        TextArea textEn = new TextArea();
        textEn.setSizeFull();
        textEn.setValue((String) rowReference.getItem().getItemProperty("textEn").getValue());
        textEn.setReadOnly(true);
        textEn.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
        TextArea textRu = new TextArea();
        textRu.setValue((String) rowReference.getItem().getItemProperty("textRu").getValue());
        textRu.setSizeFull();
        textRu.setReadOnly(true);
        textRu.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
        hl.addComponent(textEn);
        hl.addComponent(textRu);
        return hl;
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        Object itemId = event.getItemId();
        setDetailsVisible(itemId, !isDetailsVisible(itemId));
    }

    private class CustomCellStyleGenerator implements Grid.CellStyleGenerator {

        @Override
        public String getStyle(Grid.CellReference cell) {
            Object propertyId = cell.getPropertyId();
            if (propertyId != null && (propertyId.equals("textEn") || propertyId.equals("textRu"))) {
                return "wrapped-text";
            }

            return null;
        }

    }

}
