/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.HasValue;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
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
    private final CheckBox isJp;
    private final Table resultTable;
    private HierarchicalContainer hc = new HierarchicalContainer();

    public SearchInRawStringsTab(DBService service_) {
        this.service = service_;
        this.setSizeFull();
        GridLayout hl = new GridLayout(3, 1);
        hl.setHeight(100, Unit.PIXELS);
        searchField = new TextField();
        searchField.setWidth(500, Unit.PIXELS);
        searchField.addShortcutListener(new ShortcutListener("Search shortcut", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                search();
            }
        });
        hl.addComponent(searchField, 0, 0);
        isJp = new CheckBox("Японский");
        isJp.setValue(Boolean.FALSE);
        isJp.addValueChangeListener(new HasValue.ValueChangeListener<Boolean>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<Boolean> event) {
                setColumns();
            }
        });
        hl.addComponent(isJp, 1, 0);
        searchButton = new Button("Поиск");
        searchButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                search();

            }
        });
        hl.addComponent(searchButton, 2, 0);
        this.addComponent(hl);
        resultTable = new Table("");
        resultTable.setSizeFull();

        Styles styles = Page.getCurrent().getStyles();
        styles.add(".v-table-cell-wrapper {\n"
                + "    /* Do not specify any margins, paddings or borders here */\n"
                + "    white-space: pre-line;\n"
                + "    overflow: hidden;\n"
                + "}");
        resultTable.addStyleName("v-table-cell-wrapper");
        resultTable.setPageLength(0);
        hc.addContainerProperty("textEn", String.class, null);
        hc.addContainerProperty("textDe", String.class, null);
        hc.addContainerProperty("textFr", String.class, null);
        hc.addContainerProperty("textJp", String.class, null);
        hc.addContainerProperty("textRu", String.class, null);
        hc.addContainerProperty("textRuoff", String.class, null);
        resultTable.setContainerDataSource(hc);
        setColumns();

        this.addComponent(resultTable);
        this.setExpandRatio(hl, 5);
        this.setExpandRatio(resultTable, 95);

    }

    private void setColumns() {
        if (isJp.getValue()) {
            resultTable.setVisibleColumns(new Object[]{"textEn", "textDe", "textFr", "textJp", "textRu", "textRuoff"});
            resultTable.setColumnHeaders(new String[]{"En", "De", "Fr", "Jp", "RuESO", "RuOff"});
            resultTable.setColumnExpandRatio("textJp", 1f);
        } else {
            resultTable.setVisibleColumns(new Object[]{"textEn", "textDe", "textFr", "textRu", "textRuoff"});
            resultTable.setColumnHeaders(new String[]{"En", "De", "Fr", "RuESO", "RuOff"});
        }
        resultTable.setColumnExpandRatio("textEn", 1f);
        resultTable.setColumnExpandRatio("textFr", 1f);
        resultTable.setColumnExpandRatio("textDe", 1f);
        resultTable.setColumnExpandRatio("textRu", 1f);
        resultTable.setColumnExpandRatio("textRuoff", 1f);
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
