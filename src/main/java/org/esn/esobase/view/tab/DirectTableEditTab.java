/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.esn.esobase.data.DBService;
import org.esn.esobase.model.GSpreadSheetsActivator;
import org.esn.esobase.model.GSpreadSheetsItemDescription;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.GSpreadSheetsJournalEntry;
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.GSpreadSheetsQuestDescription;
import org.esn.esobase.model.GSpreadSheetsQuestDirection;
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.NPC_SEX;
import org.esn.esobase.model.lib.DAO;
import org.esn.esobase.security.SpringSecurityHelper;

/**
 *
 * @author scraelos
 */
public class DirectTableEditTab extends VerticalLayout {

    private final DBService service;
    private final TextField searchField;
    private final Button searchButton;
    private final Table resultTable;
    private HierarchicalContainer hc = new HierarchicalContainer();
    private TabSheet tableTabs;
    private Table npcNameTable;
    private JPAContainer<GSpreadSheetsNpcName> npcNameContainer;

    private Table locationNameTable;
    private JPAContainer<GSpreadSheetsLocationName> locationNameContainer;

    private Table activatorTable;
    private JPAContainer<GSpreadSheetsActivator> activatorContainer;

    private Table playerPhraseTable;
    private JPAContainer<GSpreadSheetsPlayerPhrase> playerPhraseContainer;

    private Table npcPhraseTable;
    private JPAContainer<GSpreadSheetsNpcPhrase> npcPhraseContainer;

    private Table questNameTable;
    private JPAContainer<GSpreadSheetsQuestName> questNameContainer;

    private Table questDescriptionTable;
    private JPAContainer<GSpreadSheetsQuestDescription> questDescriptionContainer;

    private Table questDirectionTable;
    private JPAContainer<GSpreadSheetsQuestDirection> questDirectionContainer;

    private VerticalLayout itemNameLayout;
    private Table itemNameTable;
    private JPAContainer<GSpreadSheetsItemName> itemNameContainer;

    private Table itemDescriptionTable;
    private JPAContainer<GSpreadSheetsItemDescription> itemDescriptionContainer;

    private Table journalEntryTable;
    private JPAContainer<GSpreadSheetsJournalEntry> journalEntryContainer;

    public DirectTableEditTab(DBService service_) {
        this.service = service_;
        this.setSizeFull();

        HorizontalLayout hl = new HorizontalLayout();
        searchField = new TextField();
        searchField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        searchField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        searchField.addShortcutListener(new ShortcutListener("Search shortcut", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                search();
            }
        });
        hl.addComponent(searchField);
        searchButton = new Button("Поиск");
        searchButton.addStyleName(ValoTheme.BUTTON_SMALL);
        searchButton.addStyleName(ValoTheme.BUTTON_TINY);
        searchButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                search();

            }
        });
        hl.addComponent(searchButton);
        this.addComponent(hl);
        resultTable = new Table("Результаты поиска");
        resultTable.addStyleName(ValoTheme.TABLE_SMALL);
        resultTable.addStyleName(ValoTheme.TABLE_COMPACT);
        resultTable.setSizeFull();
        resultTable.setHeight(200f, Unit.PIXELS);

        resultTable.setPageLength(0);
        hc.addContainerProperty("textEn", String.class, null);
        hc.addContainerProperty("textRu", String.class, null);
        hc.addContainerProperty("catalogType", String.class, null);
        hc.addContainerProperty("translator", String.class, null);
        hc.addContainerProperty("weight", Integer.class, null);
        resultTable.setContainerDataSource(hc);
        resultTable.setVisibleColumns(new Object[]{"textEn", "textRu", "catalogType", "translator"});
        resultTable.setColumnHeaders(new String[]{"Текст", "Русский текст", "Тип", "Переводчик"});
        resultTable.addItemClickListener(new SearchTableRowClickListener());

        this.addComponent(resultTable);

        tableTabs = new TabSheet();
        tableTabs.setSizeFull();
        npcNameTable = new Table();
        npcNameTable.setSizeFull();
        npcNameTable.setHeight(500f, Unit.PIXELS);
        npcNameContainer = service.getJPAContainerContainerForClass(GSpreadSheetsNpcName.class);
        npcNameContainer.setBuffered(true);
        npcNameTable.setContainerDataSource(npcNameContainer);
        npcNameTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_NPC_NAMES"));
        npcNameTable.setVisibleColumns(new Object[]{"rowNum", "sex", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        npcNameTable.setColumnHeaders(new String[]{"Номер строки", "Пол", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        npcNameTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        npcNameTable.setColumnExpandRatio("rowNum", 1.5f);
        npcNameTable.setColumnWidth("rowNum", 100);
        npcNameTable.setColumnExpandRatio("sex", 1.5f);
        npcNameTable.setColumnWidth("sex", 110);
        npcNameTable.setColumnExpandRatio("textEn", 5f);
        npcNameTable.setColumnExpandRatio("textRu", 5f);
        npcNameTable.setColumnExpandRatio("translator", 1f);
        npcNameTable.setColumnWidth("translator", 131);
        npcNameTable.setColumnExpandRatio("changeTime", 1.7f);
        npcNameTable.setColumnWidth("changeTime", 190);
        npcNameTable.setColumnExpandRatio("saveColumn", 1.1f);
        npcNameTable.setColumnWidth("saveColumn", 115);
        npcNameTable.setEditable(true);
        npcNameTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_NPC_NAMES"));
        npcNameTable.setSortEnabled(false);
        tableTabs.addTab(npcNameTable, "NPC");

        locationNameTable = new Table();
        locationNameTable.setSizeFull();
        locationNameTable.setHeight(500f, Unit.PIXELS);
        locationNameContainer = service.getJPAContainerContainerForClass(GSpreadSheetsLocationName.class);
        locationNameContainer.setBuffered(true);
        locationNameTable.setContainerDataSource(locationNameContainer);
        locationNameTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_LOCATION_NAMES"));
        locationNameTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        locationNameTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        locationNameTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        locationNameTable.setColumnExpandRatio("rowNum", 1.5f);
        locationNameTable.setColumnWidth("rowNum", 100);
        locationNameTable.setColumnExpandRatio("textEn", 5f);
        locationNameTable.setColumnExpandRatio("textRu", 5f);
        locationNameTable.setColumnExpandRatio("translator", 1f);
        locationNameTable.setColumnWidth("translator", 131);
        locationNameTable.setColumnExpandRatio("changeTime", 1.7f);
        locationNameTable.setColumnWidth("changeTime", 190);
        locationNameTable.setColumnExpandRatio("saveColumn", 1.1f);
        locationNameTable.setColumnWidth("saveColumn", 115);
        locationNameTable.setEditable(true);
        locationNameTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_LOCATION_NAMES"));
        locationNameTable.setSortEnabled(false);
        tableTabs.addTab(locationNameTable, "Локации");

        activatorTable = new Table();
        activatorTable.setSizeFull();
        activatorTable.setHeight(500f, Unit.PIXELS);
        activatorContainer = service.getJPAContainerContainerForClass(GSpreadSheetsActivator.class);
        activatorContainer.setBuffered(true);
        activatorTable.setContainerDataSource(activatorContainer);
        activatorTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_ACTIVATORS"));
        activatorTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        activatorTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        activatorTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        activatorTable.setColumnExpandRatio("rowNum", 1.5f);
        activatorTable.setColumnWidth("rowNum", 100);
        activatorTable.setColumnExpandRatio("textEn", 5f);
        activatorTable.setColumnExpandRatio("textRu", 5f);
        activatorTable.setColumnExpandRatio("translator", 1f);
        activatorTable.setColumnWidth("translator", 131);
        activatorTable.setColumnExpandRatio("changeTime", 1.7f);
        activatorTable.setColumnWidth("changeTime", 190);
        activatorTable.setColumnExpandRatio("saveColumn", 1.1f);
        activatorTable.setColumnWidth("saveColumn", 115);
        activatorTable.setEditable(true);
        activatorTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_ACTIVATORS"));
        activatorTable.setSortEnabled(false);
        tableTabs.addTab(activatorTable, "Активаторы");

        playerPhraseTable = new Table();
        playerPhraseTable.setSizeFull();
        playerPhraseTable.setHeight(500f, Unit.PIXELS);
        playerPhraseContainer = service.getJPAContainerContainerForClass(GSpreadSheetsPlayerPhrase.class);
        playerPhraseContainer.setBuffered(true);
        playerPhraseTable.setContainerDataSource(playerPhraseContainer);
        playerPhraseTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_PLAYER_PHRASES"));
        playerPhraseTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        playerPhraseTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        playerPhraseTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        playerPhraseTable.setColumnExpandRatio("rowNum", 1.5f);
        playerPhraseTable.setColumnWidth("rowNum", 100);
        playerPhraseTable.setColumnExpandRatio("textEn", 5f);
        playerPhraseTable.setColumnExpandRatio("textRu", 5f);
        playerPhraseTable.setColumnExpandRatio("translator", 1f);
        playerPhraseTable.setColumnWidth("translator", 131);
        playerPhraseTable.setColumnExpandRatio("changeTime", 1.7f);
        playerPhraseTable.setColumnWidth("changeTime", 190);
        playerPhraseTable.setColumnExpandRatio("saveColumn", 1.1f);
        playerPhraseTable.setColumnWidth("saveColumn", 115);
        playerPhraseTable.setEditable(true);
        playerPhraseTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_PLAYER_PHRASES"));
        playerPhraseTable.setSortEnabled(false);
        tableTabs.addTab(playerPhraseTable, "Фразы игрока");

        npcPhraseTable = new Table();
        npcPhraseTable.setSizeFull();
        npcPhraseTable.setHeight(500f, Unit.PIXELS);
        npcPhraseContainer = service.getJPAContainerContainerForClass(GSpreadSheetsNpcPhrase.class);
        npcPhraseContainer.setBuffered(true);
        npcPhraseTable.setContainerDataSource(npcPhraseContainer);
        npcPhraseTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_NPC_PHRASES"));
        npcPhraseTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        npcPhraseTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        npcPhraseTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        npcPhraseTable.setColumnExpandRatio("rowNum", 1.5f);
        npcPhraseTable.setColumnWidth("rowNum", 100);
        npcPhraseTable.setColumnExpandRatio("textEn", 5f);
        npcPhraseTable.setColumnExpandRatio("textRu", 5f);
        npcPhraseTable.setColumnExpandRatio("translator", 1f);
        npcPhraseTable.setColumnWidth("translator", 131);
        npcPhraseTable.setColumnExpandRatio("changeTime", 1.7f);
        npcPhraseTable.setColumnWidth("changeTime", 190);
        npcPhraseTable.setColumnExpandRatio("saveColumn", 1.1f);
        npcPhraseTable.setColumnWidth("saveColumn", 115);
        npcPhraseTable.setEditable(true);
        npcPhraseTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_NPC_PHRASES"));
        npcPhraseTable.setSortEnabled(false);
        tableTabs.addTab(npcPhraseTable, "Фразы NPC");

        questNameTable = new Table();
        questNameTable.setSizeFull();
        questNameTable.setHeight(500f, Unit.PIXELS);
        questNameContainer = service.getJPAContainerContainerForClass(GSpreadSheetsQuestName.class);
        questNameContainer.setBuffered(true);
        questNameTable.setContainerDataSource(questNameContainer);
        questNameTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_QUEST_NAMES"));
        questNameTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        questNameTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        questNameTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        questNameTable.setColumnExpandRatio("rowNum", 1.5f);
        questNameTable.setColumnWidth("rowNum", 100);
        questNameTable.setColumnExpandRatio("textEn", 5f);
        questNameTable.setColumnExpandRatio("textRu", 5f);
        questNameTable.setColumnExpandRatio("translator", 1f);
        questNameTable.setColumnWidth("translator", 131);
        questNameTable.setColumnExpandRatio("changeTime", 1.7f);
        questNameTable.setColumnWidth("changeTime", 190);
        questNameTable.setColumnExpandRatio("saveColumn", 1.1f);
        questNameTable.setColumnWidth("saveColumn", 115);
        questNameTable.setEditable(true);
        questNameTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_QUEST_NAMES"));
        questNameTable.setSortEnabled(false);
        tableTabs.addTab(questNameTable, "Названия квестов");

        questDescriptionTable = new Table();
        questDescriptionTable.setSizeFull();
        questDescriptionTable.setHeight(500f, Unit.PIXELS);
        questDescriptionContainer = service.getJPAContainerContainerForClass(GSpreadSheetsQuestDescription.class);
        questDescriptionContainer.setBuffered(true);
        questDescriptionTable.setContainerDataSource(questDescriptionContainer);
        questDescriptionTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_QUEST_DESCRIPTIONS"));
        questDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        questDescriptionTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        questDescriptionTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        questDescriptionTable.setColumnExpandRatio("rowNum", 1.5f);
        questDescriptionTable.setColumnWidth("rowNum", 100);
        questDescriptionTable.setColumnExpandRatio("textEn", 5f);
        questDescriptionTable.setColumnExpandRatio("textRu", 5f);
        questDescriptionTable.setColumnExpandRatio("translator", 1f);
        questDescriptionTable.setColumnWidth("translator", 131);
        questDescriptionTable.setColumnExpandRatio("changeTime", 1.7f);
        questDescriptionTable.setColumnWidth("changeTime", 190);
        questDescriptionTable.setColumnExpandRatio("saveColumn", 1.1f);
        questDescriptionTable.setColumnWidth("saveColumn", 115);
        questDescriptionTable.setEditable(true);
        questDescriptionTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_QUEST_DESCRIPTIONS"));
        questDescriptionTable.setSortEnabled(false);
        tableTabs.addTab(questDescriptionTable, "Описания квестов");

        questDirectionTable = new Table();
        questDirectionTable.setSizeFull();
        questDirectionTable.setHeight(500f, Unit.PIXELS);
        questDirectionContainer = service.getJPAContainerContainerForClass(GSpreadSheetsQuestDirection.class);
        questDirectionContainer.setBuffered(true);
        questDirectionTable.setContainerDataSource(questDirectionContainer);
        questDirectionTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_QUEST_DIRECTIONS"));
        questDirectionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        questDirectionTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        questDirectionTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        questDirectionTable.setColumnExpandRatio("rowNum", 1.5f);
        questDirectionTable.setColumnWidth("rowNum", 100);
        questDirectionTable.setColumnExpandRatio("textEn", 5f);
        questDirectionTable.setColumnExpandRatio("textRu", 5f);
        questDirectionTable.setColumnExpandRatio("translator", 1f);
        questDirectionTable.setColumnWidth("translator", 131);
        questDirectionTable.setColumnExpandRatio("changeTime", 1.7f);
        questDirectionTable.setColumnWidth("changeTime", 190);
        questDirectionTable.setColumnExpandRatio("saveColumn", 1.1f);
        questDirectionTable.setColumnWidth("saveColumn", 115);
        questDirectionTable.setEditable(true);
        questDirectionTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_QUEST_DIRECTIONS"));
        questDirectionTable.setSortEnabled(false);
        tableTabs.addTab(questDirectionTable, "Цели квестов");

        itemNameLayout = new VerticalLayout();
        Label itemNameLabel = new Label("ВНИМАНИЕ! В этой таблице НЕЛЬЗЯ:  переводить односложные слова, особенно написанные со строчной буквы.");
        itemNameLabel.setStyleName(ValoTheme.LABEL_COLORED);
        itemNameTable = new Table();
        itemNameTable.setSizeFull();
        itemNameTable.setHeight(500f, Unit.PIXELS);
        itemNameContainer = service.getJPAContainerContainerForClass(GSpreadSheetsItemName.class);
        itemNameContainer.setBuffered(true);
        itemNameTable.setContainerDataSource(itemNameContainer);
        itemNameTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_ITEM_NAMES"));
        itemNameTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        itemNameTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        itemNameTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        itemNameTable.setColumnExpandRatio("rowNum", 1.5f);
        itemNameTable.setColumnWidth("rowNum", 100);
        itemNameTable.setColumnExpandRatio("textEn", 5f);
        itemNameTable.setColumnExpandRatio("textRu", 5f);
        itemNameTable.setColumnExpandRatio("translator", 1f);
        itemNameTable.setColumnWidth("translator", 131);
        itemNameTable.setColumnExpandRatio("changeTime", 1.7f);
        itemNameTable.setColumnWidth("changeTime", 190);
        itemNameTable.setColumnExpandRatio("saveColumn", 1.1f);
        itemNameTable.setColumnWidth("saveColumn", 115);
        itemNameTable.setEditable(true);
        itemNameTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_ITEM_NAMES"));
        itemNameTable.setSortEnabled(false);
        itemNameLayout.addComponent(itemNameLabel);
        itemNameLayout.addComponent(itemNameTable);
        tableTabs.addTab(itemNameLayout, "Названия предметов");

        itemDescriptionTable = new Table();
        itemDescriptionTable.setSizeFull();
        itemDescriptionTable.setHeight(500f, Unit.PIXELS);
        itemDescriptionContainer = service.getJPAContainerContainerForClass(GSpreadSheetsItemDescription.class);
        itemDescriptionContainer.setBuffered(true);
        itemDescriptionTable.setContainerDataSource(itemDescriptionContainer);
        itemDescriptionTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_ITEM_DESCRIPTIONS"));
        itemDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        itemDescriptionTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        itemDescriptionTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        itemDescriptionTable.setColumnExpandRatio("rowNum", 1.5f);
        itemDescriptionTable.setColumnWidth("rowNum", 100);
        itemDescriptionTable.setColumnExpandRatio("textEn", 5f);
        itemDescriptionTable.setColumnExpandRatio("textRu", 5f);
        itemDescriptionTable.setColumnExpandRatio("translator", 1f);
        itemDescriptionTable.setColumnWidth("translator", 131);
        itemDescriptionTable.setColumnExpandRatio("changeTime", 1.7f);
        itemDescriptionTable.setColumnWidth("changeTime", 190);
        itemDescriptionTable.setColumnExpandRatio("saveColumn", 1.1f);
        itemDescriptionTable.setColumnWidth("saveColumn", 115);
        itemDescriptionTable.setEditable(true);
        itemDescriptionTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_ITEM_DESCRIPTIONS"));
        itemDescriptionTable.setSortEnabled(false);
        tableTabs.addTab(itemDescriptionTable, "Описания предметов");

        journalEntryTable = new Table();
        journalEntryTable.setSizeFull();
        journalEntryTable.setHeight(500f, Unit.PIXELS);
        journalEntryContainer = service.getJPAContainerContainerForClass(GSpreadSheetsJournalEntry.class);
        journalEntryContainer.setBuffered(true);
        journalEntryTable.setContainerDataSource(journalEntryContainer);
        journalEntryTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_JOURNAL_ENTRIES"));
        journalEntryTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "weight", "translator", "changeTime", "saveColumn"});
        journalEntryTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "Порядок", "Переводчик", "Время", ""});
        journalEntryTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        journalEntryTable.setColumnExpandRatio("rowNum", 1.5f);
        journalEntryTable.setColumnWidth("rowNum", 100);
        journalEntryTable.setColumnExpandRatio("textEn", 5f);
        journalEntryTable.setColumnExpandRatio("textRu", 5f);
        journalEntryTable.setColumnExpandRatio("translator", 1f);
        journalEntryTable.setColumnWidth("translator", 131);
        journalEntryTable.setColumnExpandRatio("changeTime", 1.7f);
        journalEntryTable.setColumnWidth("changeTime", 190);
        journalEntryTable.setColumnExpandRatio("saveColumn", 1.1f);
        journalEntryTable.setColumnWidth("saveColumn", 115);
        journalEntryTable.setEditable(true);
        journalEntryTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_JOURNAL_ENTRIES"));
        journalEntryTable.setSortEnabled(false);
        tableTabs.addTab(journalEntryTable, "Записи журнала");

        this.addComponent(tableTabs);
        this.setExpandRatio(resultTable, 10f);
        this.setExpandRatio(tableTabs, 90f);

    }

    private void search() {
        if (searchField.getValue() != null && searchField.getValue().length() > 0) {
            hc = service.searchInCatalogs(searchField.getValue(), hc);
        }
    }

    public void setWidth() {
        resultTable.setWidth(this.getUI().getWidth() - 5f, this.getUI().getWidthUnits());
    }

    private class TranslateTableFieldFactory implements TableFieldFactory {

        private final String tableEditRole;

        public TranslateTableFieldFactory(String tableEditRole_) {
            this.tableEditRole = tableEditRole_;
        }

        @Override
        public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
            Field result = null;
            if (propertyId.equals("textRu")) {
                TextArea area = new TextArea();
                area.setSizeFull();
                area.addStyleName(ValoTheme.TEXTAREA_SMALL);
                area.addStyleName(ValoTheme.TEXTAREA_TINY);
                if (!SpringSecurityHelper.hasRole("ROLE_DIRECT_ACCESS") && !SpringSecurityHelper.hasRole(tableEditRole)) {
                    area.setReadOnly(true);
                }
                result = area;
            } else if (propertyId.equals("textEn")) {
                TextArea area = new TextArea();
                area.setSizeFull();
                area.addStyleName(ValoTheme.TEXTAREA_SMALL);
                area.addStyleName(ValoTheme.TEXTAREA_TINY);
                area.setReadOnly(true);
                result = area;
            } else if (propertyId.equals("translator") || propertyId.equals("rowNum")) {
                TextField textField = new TextField();
                textField.setSizeFull();
                textField.setReadOnly(true);
                textField.setNullRepresentation("");
                textField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
                textField.addStyleName(ValoTheme.TEXTFIELD_TINY);
                result = textField;

            } else if (propertyId.equals("changeTime")) {
                DateField dateField = new DateField();
                dateField.addStyleName(ValoTheme.DATEFIELD_SMALL);
                dateField.addStyleName(ValoTheme.DATEFIELD_TINY);
                dateField.setResolution(Resolution.SECOND);
                dateField.setReadOnly(true);
                dateField.setWidth(170f, Unit.PIXELS);
                result = dateField;
            } else if (propertyId.equals("sex")) {
                ComboBox comboBox = new ComboBox();
                comboBox.addStyleName(ValoTheme.COMBOBOX_SMALL);
                comboBox.addStyleName(ValoTheme.COMBOBOX_TINY);
                comboBox.addItems(NPC_SEX.values());
                comboBox.setReadOnly(true);
                comboBox.setWidth(100f, Unit.PIXELS);
                result = comboBox;
            }
            return result;
        }

    }

    private class SaveColumnGenerator implements Table.ColumnGenerator {

        private final String tableEditRole;

        public SaveColumnGenerator(String tableEditRole_) {
            this.tableEditRole = tableEditRole_;
        }

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            Button button = new Button("Сохранить");
            button.addClickListener(new SaveButtonClickListener(itemId, (JPAContainer) source.getContainerDataSource()));
            if (!SpringSecurityHelper.hasRole("ROLE_DIRECT_ACCESS") && !SpringSecurityHelper.hasRole(tableEditRole)) {
                button.setEnabled(false);
            }
            button.addStyleName(ValoTheme.BUTTON_SMALL);
            button.addStyleName(ValoTheme.BUTTON_TINY);
            return button;
        }

    }

    private class SaveButtonClickListener implements Button.ClickListener {

        private final Object itemId;
        private final JPAContainer container;

        public SaveButtonClickListener(Object itemId, JPAContainer container) {
            this.itemId = itemId;
            this.container = container;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            try {
                EntityItem item = container.getItem(itemId);
                if (item.isDirty()) {
                    service.commitTableEntityItem(item);
                    container.refresh();
                }
            } catch (Exception ex) {

            }
        }

    }

    private class SearchTableRowClickListener implements ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            DAO entity = (DAO) event.getItemId();
            Component targetTabId = null;
            Table targetTable = null;
            Integer rowNum = 1;
            if (entity instanceof GSpreadSheetsNpcName) {
                targetTabId = npcNameTable;
                targetTable = npcNameTable;
                rowNum = ((GSpreadSheetsNpcName) entity).getRowNum().intValue();
            } else if (entity instanceof GSpreadSheetsLocationName) {
                targetTabId = locationNameTable;
                targetTable = locationNameTable;
                rowNum = ((GSpreadSheetsLocationName) entity).getRowNum().intValue();
            } else if (entity instanceof GSpreadSheetsPlayerPhrase) {
                targetTabId = playerPhraseTable;
                targetTable = playerPhraseTable;
                rowNum = ((GSpreadSheetsPlayerPhrase) entity).getRowNum().intValue();
            } else if (entity instanceof GSpreadSheetsNpcPhrase) {
                targetTabId = npcPhraseTable;
                targetTable = npcPhraseTable;
                rowNum = ((GSpreadSheetsNpcPhrase) entity).getRowNum().intValue();
            } else if (entity instanceof GSpreadSheetsQuestName) {
                targetTabId = questNameTable;
                targetTable = questNameTable;
                rowNum = ((GSpreadSheetsQuestName) entity).getRowNum().intValue();
            } else if (entity instanceof GSpreadSheetsQuestDescription) {
                targetTabId = questDescriptionTable;
                targetTable = questDescriptionTable;
                rowNum = ((GSpreadSheetsQuestDescription) entity).getRowNum().intValue();
            } else if (entity instanceof GSpreadSheetsQuestDirection) {
                targetTabId = questDirectionTable;
                targetTable = questDirectionTable;
                rowNum = ((GSpreadSheetsQuestDirection) entity).getRowNum().intValue();
            } else if (entity instanceof GSpreadSheetsActivator) {
                targetTabId = activatorTable;
                targetTable = activatorTable;
                rowNum = ((GSpreadSheetsActivator) entity).getRowNum().intValue();
            } else if (entity instanceof GSpreadSheetsJournalEntry) {
                targetTabId = journalEntryTable;
                targetTable = journalEntryTable;
                rowNum = ((GSpreadSheetsJournalEntry) entity).getRowNum().intValue();
            } else if (entity instanceof GSpreadSheetsItemName) {
                targetTabId = itemNameLayout;
                targetTable = itemNameTable;
                rowNum = ((GSpreadSheetsItemName) entity).getRowNum().intValue();
            } else if (entity instanceof GSpreadSheetsItemDescription) {
                targetTabId = itemDescriptionTable;
                targetTable = itemDescriptionTable;
                rowNum = ((GSpreadSheetsItemDescription) entity).getRowNum().intValue();
            }
            rowNum--;
            if (targetTabId != null) {
                tableTabs.setSelectedTab(targetTabId);
                targetTable.setCurrentPageFirstItemIndex(rowNum);
            }
        }

    }

}
