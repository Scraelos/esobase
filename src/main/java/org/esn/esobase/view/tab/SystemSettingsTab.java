/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import org.esn.esobase.data.DBService;

/**
 *
 * @author scraelos
 */
public class SystemSettingsTab extends VerticalLayout {

    private final DBService service;
    private CheckBox isAutoSyncEnabledBox;

    public SystemSettingsTab(DBService service_) {
        this.service = service_;

        FormLayout fl = new FormLayout();
        isAutoSyncEnabledBox = new CheckBox("Автосинхронизация включена");
        fl.addComponent(isAutoSyncEnabledBox);
        HorizontalLayout hl = new HorizontalLayout();
        Button saveButton = new Button("Сохранить");
        saveButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                service.setIsAutoSynchronizationEnabled(isAutoSyncEnabledBox.getValue());
                LoadData();
                Notification notification=new Notification("Настройки успешно сохранены", Notification.Type.HUMANIZED_MESSAGE);
                notification.setDelayMsec(2000);
                notification.show(isAutoSyncEnabledBox.getUI().getPage());
            }
        });
        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                LoadData();
            }
        });
        hl.addComponent(cancelButton);
        hl.addComponent(saveButton);
        fl.addComponent(hl);
        this.addComponent(fl);
        LoadData();
    }

    private void LoadData() {
        isAutoSyncEnabledBox.setValue(service.getIsAutoSynchronizationEnabled());
    }

}
