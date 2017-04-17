/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.filter.Like;
import com.vaadin.v7.data.util.filter.Or;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;
import com.vaadin.v7.event.FieldEvents;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.server.Page;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.TwinColSelect;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import org.esn.esobase.data.DBService;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Quest;

/**
 *
 * @author scraelos
 */
public class QuestsTab extends VerticalLayout {

    private HorizontalLayout actions;
    private Button refreshButton;
    private Button addButton;
    private HorizontalLayout tableAndForm;
    private Table table;
    private BeanItemContainer<Quest> container;

    private FormLayout form;
    private FieldGroup fieldGroup;
    private TextField name;
    private TextField nameRu;
    private TextField npcFilter;
    private TwinColSelect npcs;
    BeanItemContainer<Npc> npcsContainer;
    private Button saveButton;
    private Button cancelButton;

    private BeanItem currentItem;

    private final DBService service;

    public QuestsTab(DBService service) {
        this.service = service;
        actions = new HorizontalLayout();
        refreshButton = new Button("Обновить");
        refreshButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                LoadTable();
                CloseForm();
            }
        });
        actions.addComponent(refreshButton);
        addButton = new Button("Создать");
        addButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                AddItem();
            }
        });
        actions.addComponent(addButton);
        this.addComponent(actions);
        tableAndForm = new HorizontalLayout();
        tableAndForm.setSizeFull();
        table = new Table();
        table.setSizeFull();
        container = new BeanItemContainer<>(Quest.class);
        table.setContainerDataSource(container);

        table.setVisibleColumns(new Object[]{"name", "nameRu", "progress"});
        table.setColumnHeaders(new String[]{"Название", "Перевод названия", "Готовность диалогов"});
        table.setConverter("progress", new Converter<String, BigDecimal>() {

            @Override
            public BigDecimal convertToModel(String value, Class<? extends BigDecimal> targetType, Locale locale) throws Converter.ConversionException {
                return null;
            }

            @Override
            public String convertToPresentation(BigDecimal value, Class<? extends String> targetType, Locale locale) throws Converter.ConversionException {
                String r = "";
                if (value != null) {
                    r = value.multiply(BigDecimal.valueOf(100L).setScale(2, RoundingMode.HALF_DOWN)).setScale(0, RoundingMode.HALF_UP).toString() + "%";
                }
                return r;
            }

            @Override
            public Class<BigDecimal> getModelType() {
                return BigDecimal.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }

        });
        table.addItemClickListener(new TableClickListener());
        LoadTable();
        table.sort(new Object[]{"name"}, new boolean[]{true});
        tableAndForm.addComponent(table);
        tableAndForm.setExpandRatio(table, 0.2f);
        form = new FormLayout();
        form.setSizeFull();
        name = new TextField("Название");
        name.setNullRepresentation("");
        name.setRequired(true);
        form.addComponent(name);
        nameRu = new TextField("Перевод названия");
        nameRu.setNullRepresentation("");
        nameRu.setRequired(false);
        form.addComponent(nameRu);

        npcs = new TwinColSelect("NPC");
        npcsContainer = new BeanItemContainer<>(Npc.class);
        npcsContainer = service.loadBeanItems(npcsContainer);
        npcsContainer.sort(new Object[]{"name"}, new boolean[]{true});
        npcsContainer.addNestedContainerProperty("location.name");
        npcsContainer.addNestedContainerProperty("location.nameRu");
        npcs.setContainerDataSource(npcsContainer);
        npcs.setWidth(900f, Unit.PIXELS);
        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add(".v-font-size {\n"
                + "    font-size: 11px;\n"
                + "}");
        npcs.addStyleName("v-font-size");
        npcFilter = new TextField("Фильтр списка NPC");
        npcFilter.setNullRepresentation("");
        npcFilter.setImmediate(true);
        npcFilter.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.TIMEOUT);
        npcFilter.setTextChangeTimeout(500);
        npcFilter.addTextChangeListener(new FieldEvents.TextChangeListener() {

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                npcsContainer.removeAllContainerFilters();
                if (event.getText() != null && !event.getText().isEmpty()) {
                    try {
                        npcsContainer.addContainerFilter(new Or(new Like("name", "%" + event.getText() + "%", false),
                                new Like("nameRu", "%" + event.getText() + "%", false),
                                new Like("location.name", "%" + event.getText() + "%", false),
                                new Like("location.nameRu", "%" + event.getText() + "%", false)));
                    } catch (UnsupportedFilterException | PatternSyntaxException ex) {

                    }
                }
            }
        });
        form.addComponent(npcFilter);
        form.addComponent(npcs);
        saveButton = new Button("Сохранить");
        saveButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                SaveForm();
            }
        });
        cancelButton = new Button("Отмена");
        cancelButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                CloseForm();
                LoadTable();
            }
        });
        HorizontalLayout actions = new HorizontalLayout(cancelButton, saveButton);
        form.addComponent(actions);
        form.setVisible(false);
        tableAndForm.addComponent(form);
        tableAndForm.setExpandRatio(form, 0.75f);
        this.addComponent(tableAndForm);
    }

    private void LoadTable() {
        container = service.loadBeanItems(container);
    }

    private void AddItem() {
        Quest item = new Quest();
        item.setProgress(BigDecimal.ZERO);
        BeanItem<Quest> beanItem = new BeanItem<>(item);
        currentItem = beanItem;
        OpenForm();
    }

    private void OpenForm() {
        form.setVisible(true);
        fieldGroup = new FieldGroup(currentItem);
        fieldGroup.bind(name, "name");
        fieldGroup.bind(nameRu, "nameRu");
        fieldGroup.bind(npcs, "npcs");

    }

    private void CloseForm() {
        form.setVisible(false);
    }

    private void SaveForm() {
        try {
            fieldGroup.commit();
            Quest entity = (Quest) currentItem.getBean();
            service.saveEntity(entity);
            CloseForm();
            LoadTable();
            Notification notification = new Notification("Квест сохранён", Notification.Type.HUMANIZED_MESSAGE);
            notification.setDelayMsec(2000);
            notification.show(this.getUI().getPage());
        } catch (FieldGroup.CommitException ex) {
            Logger.getLogger(QuestsTab.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class TableClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            currentItem = (BeanItem) event.getItem();
            OpenForm();
        }

    }

}
