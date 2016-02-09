/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Container;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.DBService.NpcTopic;
import org.esn.esobase.model.Greeting;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Subtitle;
import org.esn.esobase.model.Topic;

/**
 *
 * @author scraelos
 */
public class TreeTranslateTab extends VerticalLayout {

    private HorizontalLayout actions;
    private DBService service;
    private HierarchicalContainer container;
    private TreeTable table;
    private Button exportButton;
    private Object[] visibleColumns = {"type", "text", "textRaw", "textRu", "textRawRu"};
    private String[] columnHeaders = {"Тип", "Оригинал", "Оригинал RAW", "Перевод", "Перевод RAW"};

    public TreeTranslateTab(DBService service) {
        this.setHeight(100F, Unit.PERCENTAGE);
        this.setWidth(100f, Unit.PERCENTAGE);
        this.service = service;
        actions = new HorizontalLayout();
        this.addComponent(actions);
        exportButton = new Button("Экспорт в xls");
        exportButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Export();
            }
        });
        actions.addComponent(exportButton);
        table = new TreeTable();
        table.setWidth(100f, Unit.PERCENTAGE);
        table.setHeight(800f, Unit.PIXELS);
        this.addComponent(table);
        InitData();
    }

    private void Export() {
        ExcelExport export = new ExcelExport(table);
        export.setDisplayTotals(false);
        export.export();
    }

    private void InitData() {
        container = service.getLocationsTree(container);
        table.setContainerDataSource(container);
        table.setVisibleColumns(visibleColumns);
        table.setColumnHeaders(columnHeaders);
        table.setEditable(true);
        table.setTableFieldFactory(new TranslateTableFieldFactory());
        table.setColumnWidth("type", 170);
        table.addGeneratedColumn("saveAction", new Table.ColumnGenerator() {

            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                HorizontalLayout hl = new HorizontalLayout();
                Button saveButton = new Button("Сохранить");
                saveButton.addStyleName(ValoTheme.BUTTON_TINY);
                saveButton.addClickListener(new SaveButtonClickListener(service, itemId, source));
                Button assignPhrase = new Button("Искать Raw");
                assignPhrase.addStyleName(ValoTheme.BUTTON_TINY);
                assignPhrase.addClickListener(new AssignButtonClickListener(service, itemId, source));
                hl.addComponent(saveButton);
                hl.addComponent(assignPhrase);
                return hl;
            }

        });
        table.setColumnWidth("saveAction", 150);
        table.setColumnHeader("saveAction", "");

    }

    private void RefreshData() {
        container = service.getLocationsTree(container);
    }

    private class TranslateTableFieldFactory extends DefaultFieldFactory {

        @Override
        public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
            Field result = null;
            if (propertyId.equals("text") || propertyId.equals("textRu") || propertyId.equals("textRaw") || propertyId.equals("textRawRu")) {
                TextArea textArea = new TextArea(container.getItem(itemId).getItemProperty(propertyId));
                textArea.setWidth(100f, Unit.PERCENTAGE);
                textArea.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
                textArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textArea.setNullRepresentation("");
                textArea.setImmediate(true);
                result = textArea;
            } else {
                result = super.createField(container, itemId, propertyId, uiContext);
                result.setReadOnly(true);
            }
            if (result instanceof TextField) {
                ((TextField) result).addStyleName(ValoTheme.TEXTFIELD_TINY);
                ((TextField) result).addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
            }
            return result;
        }

    }

    private class SaveButtonClickListener implements Button.ClickListener {

        private final DBService service;
        private final Object itemId;
        private final Table table;

        public SaveButtonClickListener(DBService service, Object itemId, Table table) {
            this.service = service;
            this.itemId = itemId;
            this.table = table;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            String text = (String) table.getItem(itemId).getItemProperty("text").getValue();
            String textRu = (String) table.getItem(itemId).getItemProperty("textRu").getValue();
            Object entity = itemId;
            if (itemId instanceof Location) {
                ((Location) itemId).setName(text);
                ((Location) itemId).setNameRu(textRu);
            } else if (itemId instanceof Npc) {
                ((Npc) itemId).setName(text);
                ((Npc) itemId).setNameRu(textRu);
            } else if (itemId instanceof Subtitle) {
                ((Subtitle) itemId).setText(text);
                ((Subtitle) itemId).setTextRu(textRu);
            } else if (itemId instanceof Greeting) {
                ((Greeting) itemId).setText(text);
                ((Greeting) itemId).setTextRu(textRu);
            } else if (itemId instanceof Topic) {
                ((Topic) itemId).setPlayerText(text);
                ((Topic) itemId).setPlayerTextRu(textRu);
            } else if (itemId instanceof NpcTopic) {
                ((NpcTopic) itemId).getTopic().setNpcText(text);
                ((NpcTopic) itemId).getTopic().setNpcTextRu(textRu);
                entity = ((NpcTopic) itemId).getTopic();
            }

            service.saveEntity(entity);
            RefreshData();
        }

    }

    private class AssignButtonClickListener implements Button.ClickListener {

        private final DBService service;
        private final Object itemId;
        private final Table table;

        public AssignButtonClickListener(DBService service, Object itemId, Table table) {
            this.service = service;
            this.itemId = itemId;
            this.table = table;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            String text = (String) table.getItem(itemId).getItemProperty("text").getValue();
            String textRu = (String) table.getItem(itemId).getItemProperty("textRu").getValue();
            Object entity = itemId;
            if (itemId instanceof Location) {

            } else if (itemId instanceof Npc) {

            } else if (itemId instanceof Subtitle) {
                service.assignSubtitleToPhrase((Subtitle) itemId);
            } else if (itemId instanceof Greeting) {
                service.assignGreetingToPhrase((Greeting) itemId);
            } else if (itemId instanceof Topic) {
                service.assignTopicToPhrase((Topic) itemId);
            } else if (itemId instanceof NpcTopic) {
                service.assignTopicToPhrase(((NpcTopic) itemId).getTopic());
            }

            RefreshData();
        }

    }

}
