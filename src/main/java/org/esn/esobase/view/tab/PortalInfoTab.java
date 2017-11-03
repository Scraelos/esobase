/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.VerticalLayout;
import org.esn.esobase.data.DBService;

/**
 *
 * @author Scraelos
 */
public class PortalInfoTab extends VerticalLayout {

    private final DBService service;
    private Table table;

    public PortalInfoTab(DBService service_) {
        this.setSizeFull();
        this.service = service_;
        table = new Table();
        table.setSizeFull();
        table.setWidth(500f,Unit.PIXELS);
        HierarchicalContainer hc = service.getStatistics();
        table.setContainerDataSource(hc);
        table.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        this.addComponent(table);
    }

}
