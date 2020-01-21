/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.esn.esobase.data.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author scraelos
 */
@Component
@Scope(value = "prototype")
public class UserStatisticsTab extends VerticalLayout {

    @Autowired
    private StatsService statsService;
    private DateField startDate;
    private DateField endDate;
    private Button refresh;
    private Grid<StatsService.StatItem> grid;
    private List<StatsService.StatItem> list;

    public UserStatisticsTab() {
    }

    public void Init() {
        removeAllComponents();
        startDate = new DateField("С");
        FormLayout startDateFl = new FormLayout(startDate);
        startDate.setValue(LocalDate.now().minusMonths(1));
        endDate = new DateField("По");
        FormLayout endDateFl = new FormLayout(endDate);
        endDate.setValue(LocalDate.now());
        grid = new Grid<>(StatsService.StatItem.class);
        list = new ArrayList<>();
        statsService.getStats(list, startDate.getValue(), endDate.getValue());
        grid.setDataProvider(new ListDataProvider<>(list));
        grid.getColumn("login").setCaption("Пользователь");
        grid.getColumn("translatedCount").setCaption("Переведено");
        grid.getColumn("corectedCount").setCaption("Откорректировано");
        grid.getColumn("approvedCount").setCaption("Принято");
        grid.setColumns("login", "translatedCount", "corectedCount", "approvedCount");
        refresh = new Button("Обновить", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                statsService.getStats(list, startDate.getValue(), endDate.getValue());
                grid.getDataProvider().refreshAll();
            }
        });
        FormLayout refreshFl = new FormLayout(refresh);
        HorizontalLayout actions = new HorizontalLayout(startDateFl, endDateFl, refreshFl);
        addComponent(actions);
        addComponent(grid);
    }
}
