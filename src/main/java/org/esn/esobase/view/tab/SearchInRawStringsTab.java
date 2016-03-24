/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.esn.esobase.data.DBService;

/**
 *
 * @author scraelos
 */
public class SearchInRawStringsTab extends VerticalLayout {

    private final DBService service;

    private final TextField searchField;
    private final Button searchButton;
    private final Table resultTable;
    private HierarchicalContainer hc = new HierarchicalContainer();

    public SearchInRawStringsTab(DBService service_) {
        this.service = service_;
        this.setSizeFull();
        GridLayout hl = new GridLayout(2, 1);
        hl.setHeight(100, Unit.PIXELS);
        searchField = new TextField();
        searchField.addShortcutListener(new ShortcutListener("Search shortcut", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                search();
            }
        });
        hl.addComponent(searchField, 0, 0);
        searchButton = new Button("Поиск");
        searchButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                search();

            }
        });
        hl.addComponent(searchButton, 1, 0);
        this.addComponent(hl);
        resultTable = new Table("");
        resultTable.setSizeFull();

        Styles styles = Page.getCurrent().getStyles();
        styles.add(".v-table-cell-wrapper {\n"
                + "    /* Do not specify any margins, paddings or borders here */\n"
                + "    white-space: normal;\n"
                + "    overflow: hidden;\n"
                + "}");
        resultTable.addStyleName("v-table-cell-wrapper");
        resultTable.setPageLength(0);
        hc.addContainerProperty("textEn", String.class, null);
        hc.addContainerProperty("textDe", String.class, null);
        hc.addContainerProperty("textFr", String.class, null);
        resultTable.setContainerDataSource(hc);
        resultTable.setVisibleColumns(new Object[]{"textEn", "textDe", "textFr"});
        resultTable.setColumnHeaders(new String[]{"En", "De", "Fr"});

        this.addComponent(resultTable);
        this.setExpandRatio(hl, 5);
        this.setExpandRatio(resultTable, 95);

    }

    private void search() {
        if (searchField.getValue() != null && searchField.getValue().length() > 2) {
            hc = service.searchInRawStrings(searchField.getValue(), hc);
        }
    }

    public void setWidth() {
        resultTable.setWidth(this.getUI().getWidth() - 5f, this.getUI().getWidthUnits());
    }

}
