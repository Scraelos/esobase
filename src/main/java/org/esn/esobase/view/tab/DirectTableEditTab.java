/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.esn.esobase.data.DBService;
import org.esn.esobase.model.EsoInterfaceVariable;
import org.esn.esobase.model.GSpreadSheetEntity;
import org.esn.esobase.model.GSpreadSheetsAbilityDescription;
import org.esn.esobase.model.GSpreadSheetsAchievement;
import org.esn.esobase.model.GSpreadSheetsAchievementDescription;
import org.esn.esobase.model.GSpreadSheetsActivator;
import org.esn.esobase.model.GSpreadSheetsItemDescription;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.GSpreadSheetsJournalEntry;
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNote;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.GSpreadSheetsQuestDescription;
import org.esn.esobase.model.GSpreadSheetsQuestDirection;
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.model.lib.DAO;
import org.esn.esobase.security.SpringSecurityHelper;
import org.esn.esobase.tools.GSpreadSheetLinkRouter;

/**
 *
 * @author scraelos
 */
public class DirectTableEditTab extends VerticalLayout {

    private final DBService service;
    private final TextField searchField;
    private final Button searchButton;
    private final Table resultTable;
    private TabSheet searchTabs;
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

    private Table achievementTable;
    private JPAContainer<GSpreadSheetsAchievement> achievementContainer;

    private Table achievementDescriptionTable;
    private JPAContainer<GSpreadSheetsAchievementDescription> achievementDescriptionContainer;

    private Table abilityDescriptionTable;
    private JPAContainer<GSpreadSheetsAbilityDescription> abilityDescriptionContainer;

    private Table noteTable;
    private JPAContainer<GSpreadSheetsNote> noteContainer;

    private Table esoInterfaceTable;
    private JPAContainer<EsoInterfaceVariable> esoInterfaceContainer;

    private LinkedItemColumnGenerator linkedItemColumnGenerator;
    private LinkedItemClickListener linkedItemClickListener;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private ComboBox statusFilter;
    private ComboBox translatorBox;
    private ComboBox translateTypeBox;
    private Button filterTranslationButton;
    private Table newTranslationsTable;
    private JPAContainer<TranslatedText> newTranslationsContainer;
    private BeanItemContainer<SysAccount> sysAccountContainer = new BeanItemContainer<>(SysAccount.class);

    public DirectTableEditTab(DBService service_) {
        this.service = service_;
        this.setWidth(100f, Unit.PERCENTAGE);
        this.setHeight(100f, Unit.PERCENTAGE);
        linkedItemColumnGenerator = new LinkedItemColumnGenerator();
        linkedItemClickListener = new LinkedItemClickListener();
        searchTabs = new TabSheet();
        searchTabs.setSizeFull();
        searchTabs.setHeight(250f, Unit.PIXELS);
        VerticalLayout searchInCatalogsLayout = new VerticalLayout();
        searchInCatalogsLayout.setSizeFull();
        HorizontalLayout hl = new HorizontalLayout();
        searchField = new TextField();
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
        searchInCatalogsLayout.addComponent(hl);
        resultTable = new Table("Результаты поиска");
        resultTable.addStyleName(ValoTheme.TABLE_SMALL);
        resultTable.addStyleName(ValoTheme.TABLE_COMPACT);
        resultTable.setSizeFull();
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

        searchInCatalogsLayout.addComponent(resultTable);
        searchInCatalogsLayout.setExpandRatio(resultTable, 5f);

        searchTabs.addTab(searchInCatalogsLayout, "Поиск");
        VerticalLayout translationsLayout = new VerticalLayout();
        translationsLayout.setSizeFull();
        HorizontalLayout traslationsFilterslayout = new HorizontalLayout();
        statusFilter = new ComboBox("Статус", Arrays.asList(TRANSLATE_STATUS.values()));
        statusFilter.setNullSelectionAllowed(false);
        statusFilter.setValue(TRANSLATE_STATUS.NEW);
        traslationsFilterslayout.addComponent(statusFilter);
        translatorBox = new ComboBox("Переводчик");
        translatorBox.setPageLength(15);
        sysAccountContainer = service.loadBeanItems(sysAccountContainer);
        translatorBox.setContainerDataSource(sysAccountContainer);
        translatorBox.setFilteringMode(FilteringMode.CONTAINS);
        traslationsFilterslayout.addComponent(translatorBox);
        translateTypeBox = new ComboBox("Таблица");
        translateTypeBox.setNullSelectionAllowed(true);
        translateTypeBox.setPageLength(15);
        translateTypeBox.addItem("GSpreadSheetsActivator");
        translateTypeBox.setItemCaption("GSpreadSheetsActivator", "Активаторы");
        translateTypeBox.addItem("GSpreadSheetsAchievement");
        translateTypeBox.setItemCaption("GSpreadSheetsAchievement", "Достижения");
        translateTypeBox.addItem("GSpreadSheetsAchievementDescription");
        translateTypeBox.setItemCaption("GSpreadSheetsAchievementDescription", "Описания достижений");
        translateTypeBox.addItem("GSpreadSheetsNote");
        translateTypeBox.setItemCaption("GSpreadSheetsNote", "Письма");
        translateTypeBox.addItem("GSpreadSheetsItemDescription");
        translateTypeBox.setItemCaption("GSpreadSheetsItemDescription", "Описания предметов");
        translateTypeBox.addItem("GSpreadSheetsItemName");
        translateTypeBox.setItemCaption("GSpreadSheetsItemName", "Названия предметов");
        translateTypeBox.addItem("GSpreadSheetsJournalEntry");
        translateTypeBox.setItemCaption("GSpreadSheetsJournalEntry", "Записи в журнале");
        translateTypeBox.addItem("GSpreadSheetsLocationName");
        translateTypeBox.setItemCaption("GSpreadSheetsLocationName", "Названия локаций");
        translateTypeBox.addItem("GSpreadSheetsNpcName");
        translateTypeBox.setItemCaption("GSpreadSheetsNpcName", "Имена NPC");
        translateTypeBox.addItem("GSpreadSheetsNpcPhrase");
        translateTypeBox.setItemCaption("GSpreadSheetsNpcPhrase", "Фразы NPC");
        translateTypeBox.addItem("GSpreadSheetsPlayerPhrase");
        translateTypeBox.setItemCaption("GSpreadSheetsPlayerPhrase", "Фразы игрока");
        translateTypeBox.addItem("GSpreadSheetsQuestDescription");
        translateTypeBox.setItemCaption("GSpreadSheetsQuestDescription", "Описания квестов");
        translateTypeBox.addItem("GSpreadSheetsQuestDirection");
        translateTypeBox.setItemCaption("GSpreadSheetsQuestDirection", "Цели квестов");
        translateTypeBox.addItem("GSpreadSheetsQuestName");
        translateTypeBox.setItemCaption("GSpreadSheetsQuestName", "Названия квестов");
        translateTypeBox.addItem("EsoInterfaceVariable");
        translateTypeBox.setItemCaption("EsoInterfaceVariable", "Строки интерфейса");
        translateTypeBox.setFilteringMode(FilteringMode.CONTAINS);
        traslationsFilterslayout.addComponent(translateTypeBox);
        filterTranslationButton = new Button("Поиск");
        filterTranslationButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                filterTranslations();
            }
        });
        traslationsFilterslayout.addComponent(filterTranslationButton);
        traslationsFilterslayout.setComponentAlignment(filterTranslationButton, Alignment.BOTTOM_LEFT);
        translationsLayout.addComponent(traslationsFilterslayout);
        newTranslationsTable = new Table();
        newTranslationsTable.addStyleName(ValoTheme.TABLE_SMALL);
        newTranslationsTable.addStyleName(ValoTheme.TABLE_COMPACT);
        newTranslationsTable.setSizeFull();
        newTranslationsContainer = service.getJPAContainerContainerForClass(TranslatedText.class);
        filterTranslations();
        newTranslationsContainer.sort(new Object[]{"id"}, new boolean[]{true});
        newTranslationsTable.setContainerDataSource(newTranslationsContainer);
        newTranslationsTable.setVisibleColumns(new Object[]{"author", "createTime", "text"});
        newTranslationsTable.setColumnHeaders(new String[]{"Автор", "Дата", "Перевод"});
        newTranslationsTable.addItemClickListener(new TranslationsTableRowClickListener());
        translationsLayout.addComponent(newTranslationsTable);
        translationsLayout.setExpandRatio(newTranslationsTable, 5f);

        searchTabs.addTab(translationsLayout, "Переводы");
        this.addComponent(searchTabs);
        tableTabs = new TabSheet();
        tableTabs.setSizeFull();
        npcNameTable = new Table();
        npcNameTable.setSizeFull();
        npcNameContainer = service.getJPAContainerContainerForClass(GSpreadSheetsNpcName.class);
        npcNameContainer.setBuffered(true);
        npcNameTable.setContainerDataSource(npcNameContainer);
        npcNameTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        npcNameTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_NPC_NAMES"));
        npcNameTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        npcNameTable.setVisibleColumns(new Object[]{"rowNum", "sex", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        npcNameTable.setColumnHeaders(new String[]{"Номер строки", "Пол", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        npcNameTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        npcNameTable.setColumnExpandRatio("rowNum", 1.5f);
        npcNameTable.setColumnWidth("rowNum", 100);
        npcNameTable.setColumnExpandRatio("sex", 1.5f);
        npcNameTable.setColumnWidth("sex", 110);
        npcNameTable.setColumnExpandRatio("textEn", 5f);
        npcNameTable.setColumnExpandRatio("textRu", 5f);
        npcNameTable.setColumnExpandRatio("translateColumn", 5f);
        npcNameTable.setColumnExpandRatio("translator", 1f);
        npcNameTable.setColumnWidth("translator", 131);
        npcNameTable.setColumnExpandRatio("changeTime", 1.7f);
        npcNameTable.setColumnWidth("changeTime", 190);
        npcNameTable.setColumnExpandRatio("saveColumn", 1.1f);
        npcNameTable.setColumnWidth("saveColumn", 115);
        npcNameTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        npcNameTable.setColumnWidth("linkedItemCoumn", 47);
        npcNameTable.setEditable(true);
        npcNameTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_NPC_NAMES"));
        npcNameTable.setSortEnabled(false);
        npcNameTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(npcNameTable, "NPC");

        locationNameTable = new Table();
        locationNameTable.setPageLength(10);
        locationNameTable.setSizeFull();
        locationNameContainer = service.getJPAContainerContainerForClass(GSpreadSheetsLocationName.class);
        locationNameContainer.setBuffered(true);
        locationNameTable.setContainerDataSource(locationNameContainer);
        locationNameTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        locationNameTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_LOCATION_NAMES"));
        locationNameTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        locationNameTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        locationNameTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        locationNameTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        locationNameTable.setColumnExpandRatio("rowNum", 1.5f);
        locationNameTable.setColumnWidth("rowNum", 100);
        locationNameTable.setColumnExpandRatio("textEn", 5f);
        locationNameTable.setColumnExpandRatio("textRu", 5f);
        locationNameTable.setColumnExpandRatio("translateColumn", 5f);
        locationNameTable.setColumnExpandRatio("translator", 1f);
        locationNameTable.setColumnWidth("translator", 131);
        locationNameTable.setColumnExpandRatio("changeTime", 1.7f);
        locationNameTable.setColumnWidth("changeTime", 190);
        locationNameTable.setColumnExpandRatio("saveColumn", 1.1f);
        locationNameTable.setColumnWidth("saveColumn", 115);
        locationNameTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        locationNameTable.setColumnWidth("linkedItemCoumn", 47);
        locationNameTable.setEditable(true);
        locationNameTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_LOCATION_NAMES"));
        locationNameTable.setSortEnabled(false);
        locationNameTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(locationNameTable, "Локации");

        activatorTable = new Table();
        activatorTable.setSizeFull();
        activatorContainer = service.getJPAContainerContainerForClass(GSpreadSheetsActivator.class);
        activatorContainer.setBuffered(true);
        activatorTable.setContainerDataSource(activatorContainer);
        activatorTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        activatorTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_ACTIVATORS"));
        activatorTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        activatorTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        activatorTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        activatorTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        activatorTable.setColumnExpandRatio("rowNum", 1.5f);
        activatorTable.setColumnWidth("rowNum", 100);
        activatorTable.setColumnExpandRatio("textEn", 5f);
        activatorTable.setColumnExpandRatio("textRu", 5f);
        activatorTable.setColumnExpandRatio("translateColumn", 5f);
        activatorTable.setColumnExpandRatio("translator", 1f);
        activatorTable.setColumnWidth("translator", 131);
        activatorTable.setColumnExpandRatio("changeTime", 1.7f);
        activatorTable.setColumnWidth("changeTime", 190);
        activatorTable.setColumnExpandRatio("saveColumn", 1.1f);
        activatorTable.setColumnWidth("saveColumn", 115);
        activatorTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        activatorTable.setColumnWidth("linkedItemCoumn", 47);
        activatorTable.setEditable(true);
        activatorTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_ACTIVATORS"));
        activatorTable.setSortEnabled(false);
        activatorTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(activatorTable, "Активаторы");

        playerPhraseTable = new Table();
        playerPhraseTable.setSizeFull();
        playerPhraseContainer = service.getJPAContainerContainerForClass(GSpreadSheetsPlayerPhrase.class);
        playerPhraseContainer.setBuffered(true);
        playerPhraseTable.setContainerDataSource(playerPhraseContainer);
        playerPhraseTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        playerPhraseTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_PLAYER_PHRASES"));
        playerPhraseTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        playerPhraseTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        playerPhraseTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        playerPhraseTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        playerPhraseTable.setColumnExpandRatio("rowNum", 1.5f);
        playerPhraseTable.setColumnWidth("rowNum", 100);
        playerPhraseTable.setColumnExpandRatio("textEn", 5f);
        playerPhraseTable.setColumnExpandRatio("textRu", 5f);
        playerPhraseTable.setColumnExpandRatio("translateColumn", 5f);
        playerPhraseTable.setColumnExpandRatio("translator", 1f);
        playerPhraseTable.setColumnWidth("translator", 131);
        playerPhraseTable.setColumnExpandRatio("changeTime", 1.7f);
        playerPhraseTable.setColumnWidth("changeTime", 190);
        playerPhraseTable.setColumnExpandRatio("saveColumn", 1.1f);
        playerPhraseTable.setColumnWidth("saveColumn", 115);
        playerPhraseTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        playerPhraseTable.setColumnWidth("linkedItemCoumn", 47);
        playerPhraseTable.setEditable(true);
        playerPhraseTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_PLAYER_PHRASES"));
        playerPhraseTable.setSortEnabled(false);
        playerPhraseTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(playerPhraseTable, "Фразы игрока");

        npcPhraseTable = new Table();
        npcPhraseTable.setSizeFull();
        npcPhraseContainer = service.getJPAContainerContainerForClass(GSpreadSheetsNpcPhrase.class);
        npcPhraseContainer.setBuffered(true);
        npcPhraseTable.setContainerDataSource(npcPhraseContainer);
        npcPhraseTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        npcPhraseTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_NPC_PHRASES"));
        npcPhraseTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        npcPhraseTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        npcPhraseTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        npcPhraseTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        npcPhraseTable.setColumnExpandRatio("rowNum", 1.5f);
        npcPhraseTable.setColumnWidth("rowNum", 100);
        npcPhraseTable.setColumnExpandRatio("textEn", 5f);
        npcPhraseTable.setColumnExpandRatio("textRu", 5f);
        npcPhraseTable.setColumnExpandRatio("translateColumn", 5f);
        npcPhraseTable.setColumnExpandRatio("translator", 1f);
        npcPhraseTable.setColumnWidth("translator", 131);
        npcPhraseTable.setColumnExpandRatio("changeTime", 1.7f);
        npcPhraseTable.setColumnWidth("changeTime", 190);
        npcPhraseTable.setColumnExpandRatio("saveColumn", 1.1f);
        npcPhraseTable.setColumnWidth("saveColumn", 115);
        npcPhraseTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        npcPhraseTable.setColumnWidth("linkedItemCoumn", 47);
        npcPhraseTable.setEditable(true);
        npcPhraseTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_NPC_PHRASES"));
        npcPhraseTable.setSortEnabled(false);
        npcPhraseTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(npcPhraseTable, "Фразы NPC");

        questNameTable = new Table();
        questNameTable.setSizeFull();
        questNameContainer = service.getJPAContainerContainerForClass(GSpreadSheetsQuestName.class);
        questNameContainer.setBuffered(true);
        questNameTable.setContainerDataSource(questNameContainer);
        questNameTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        questNameTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_QUEST_NAMES"));
        questNameTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        questNameTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        questNameTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        questNameTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        questNameTable.setColumnExpandRatio("rowNum", 1.5f);
        questNameTable.setColumnWidth("rowNum", 100);
        questNameTable.setColumnExpandRatio("textEn", 5f);
        questNameTable.setColumnExpandRatio("textRu", 5f);
        questNameTable.setColumnExpandRatio("translateColumn", 5f);
        questNameTable.setColumnExpandRatio("translator", 1f);
        questNameTable.setColumnWidth("translator", 131);
        questNameTable.setColumnExpandRatio("changeTime", 1.7f);
        questNameTable.setColumnWidth("changeTime", 190);
        questNameTable.setColumnExpandRatio("saveColumn", 1.1f);
        questNameTable.setColumnWidth("saveColumn", 115);
        questNameTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        questNameTable.setColumnWidth("linkedItemCoumn", 47);
        questNameTable.setEditable(true);
        questNameTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_QUEST_NAMES"));
        questNameTable.setSortEnabled(false);
        questNameTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(questNameTable, "Названия квестов");

        questDescriptionTable = new Table();
        questDescriptionTable.setSizeFull();
        questDescriptionContainer = service.getJPAContainerContainerForClass(GSpreadSheetsQuestDescription.class);
        questDescriptionContainer.setBuffered(true);
        questDescriptionTable.setContainerDataSource(questDescriptionContainer);
        questDescriptionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        questDescriptionTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_QUEST_DESCRIPTIONS"));
        questDescriptionTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        questDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        questDescriptionTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        questDescriptionTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        questDescriptionTable.setColumnExpandRatio("rowNum", 1.5f);
        questDescriptionTable.setColumnWidth("rowNum", 100);
        questDescriptionTable.setColumnExpandRatio("textEn", 5f);
        questDescriptionTable.setColumnExpandRatio("textRu", 5f);
        questDescriptionTable.setColumnExpandRatio("translateColumn", 5f);
        questDescriptionTable.setColumnExpandRatio("translator", 1f);
        questDescriptionTable.setColumnWidth("translator", 131);
        questDescriptionTable.setColumnExpandRatio("changeTime", 1.7f);
        questDescriptionTable.setColumnWidth("changeTime", 190);
        questDescriptionTable.setColumnExpandRatio("saveColumn", 1.1f);
        questDescriptionTable.setColumnWidth("saveColumn", 115);
        questDescriptionTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        questDescriptionTable.setColumnWidth("linkedItemCoumn", 47);
        questDescriptionTable.setEditable(true);
        questDescriptionTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_QUEST_DESCRIPTIONS"));
        questDescriptionTable.setSortEnabled(false);
        questDescriptionTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(questDescriptionTable, "Описания квестов");

        questDirectionTable = new Table();
        questDirectionTable.setSizeFull();
        questDirectionContainer = service.getJPAContainerContainerForClass(GSpreadSheetsQuestDirection.class);
        questDirectionContainer.setBuffered(true);
        questDirectionTable.setContainerDataSource(questDirectionContainer);
        questDirectionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        questDirectionTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_QUEST_DIRECTIONS"));
        questDirectionTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        questDirectionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        questDirectionTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        questDirectionTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        questDirectionTable.setColumnExpandRatio("rowNum", 1.5f);
        questDirectionTable.setColumnWidth("rowNum", 100);
        questDirectionTable.setColumnExpandRatio("textEn", 5f);
        questDirectionTable.setColumnExpandRatio("textRu", 5f);
        questDirectionTable.setColumnExpandRatio("translateColumn", 5f);
        questDirectionTable.setColumnExpandRatio("translator", 1f);
        questDirectionTable.setColumnWidth("translator", 131);
        questDirectionTable.setColumnExpandRatio("changeTime", 1.7f);
        questDirectionTable.setColumnWidth("changeTime", 190);
        questDirectionTable.setColumnExpandRatio("saveColumn", 1.1f);
        questDirectionTable.setColumnWidth("saveColumn", 115);
        questDirectionTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        questDirectionTable.setColumnWidth("linkedItemCoumn", 47);
        questDirectionTable.setEditable(true);
        questDirectionTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_QUEST_DIRECTIONS"));
        questDirectionTable.setSortEnabled(false);
        questDirectionTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(questDirectionTable, "Цели квестов");

        itemNameLayout = new VerticalLayout();
        Label itemNameLabel = new Label("ВНИМАНИЕ! В этой таблице НЕЛЬЗЯ:  переводить односложные слова, особенно написанные со строчной буквы.");
        itemNameLabel.setStyleName(ValoTheme.LABEL_COLORED);
        itemNameTable = new Table();
        itemNameTable.setSizeFull();
        itemNameContainer = service.getJPAContainerContainerForClass(GSpreadSheetsItemName.class);
        itemNameContainer.setBuffered(true);
        itemNameTable.setContainerDataSource(itemNameContainer);
        itemNameTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        itemNameTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_ITEM_NAMES"));
        itemNameTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        itemNameTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        itemNameTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        itemNameTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        itemNameTable.setColumnExpandRatio("rowNum", 1.5f);
        itemNameTable.setColumnWidth("rowNum", 100);
        itemNameTable.setColumnExpandRatio("textEn", 5f);
        itemNameTable.setColumnExpandRatio("textRu", 5f);
        itemNameTable.setColumnExpandRatio("translateColumn", 5f);
        itemNameTable.setColumnExpandRatio("translator", 1f);
        itemNameTable.setColumnWidth("translator", 131);
        itemNameTable.setColumnExpandRatio("changeTime", 1.7f);
        itemNameTable.setColumnWidth("changeTime", 190);
        itemNameTable.setColumnExpandRatio("saveColumn", 1.1f);
        itemNameTable.setColumnWidth("saveColumn", 115);
        itemNameTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        itemNameTable.setColumnWidth("linkedItemCoumn", 47);
        itemNameTable.setEditable(true);
        itemNameTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_ITEM_NAMES"));
        itemNameTable.setSortEnabled(false);
        itemNameLayout.addComponent(itemNameLabel);
        itemNameLayout.addComponent(itemNameTable);
        itemNameTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(itemNameLayout, "Названия предметов");

        itemDescriptionTable = new Table();
        itemDescriptionTable.setSizeFull();
        itemDescriptionContainer = service.getJPAContainerContainerForClass(GSpreadSheetsItemDescription.class);
        itemDescriptionContainer.setBuffered(true);
        itemDescriptionTable.setContainerDataSource(itemDescriptionContainer);
        itemDescriptionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        itemDescriptionTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_ITEM_DESCRIPTIONS"));
        itemDescriptionTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        itemDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        itemDescriptionTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        itemDescriptionTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        itemDescriptionTable.setColumnExpandRatio("rowNum", 1.5f);
        itemDescriptionTable.setColumnWidth("rowNum", 100);
        itemDescriptionTable.setColumnExpandRatio("textEn", 5f);
        itemDescriptionTable.setColumnExpandRatio("textRu", 5f);
        itemDescriptionTable.setColumnExpandRatio("translateColumn", 5f);
        itemDescriptionTable.setColumnExpandRatio("translator", 1f);
        itemDescriptionTable.setColumnWidth("translator", 131);
        itemDescriptionTable.setColumnExpandRatio("changeTime", 1.7f);
        itemDescriptionTable.setColumnWidth("changeTime", 190);
        itemDescriptionTable.setColumnExpandRatio("saveColumn", 1.1f);
        itemDescriptionTable.setColumnWidth("saveColumn", 115);
        itemDescriptionTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        itemDescriptionTable.setColumnWidth("linkedItemCoumn", 47);
        itemDescriptionTable.setEditable(true);
        itemDescriptionTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_ITEM_DESCRIPTIONS"));
        itemDescriptionTable.setSortEnabled(false);
        itemDescriptionTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(itemDescriptionTable, "Описания предметов");

        journalEntryTable = new Table();
        journalEntryTable.setSizeFull();
        journalEntryContainer = service.getJPAContainerContainerForClass(GSpreadSheetsJournalEntry.class);
        journalEntryContainer.setBuffered(true);
        journalEntryTable.setContainerDataSource(journalEntryContainer);
        journalEntryTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        journalEntryTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_JOURNAL_ENTRIES"));
        journalEntryTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        journalEntryTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        journalEntryTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        journalEntryTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        journalEntryTable.setColumnExpandRatio("rowNum", 1.5f);
        journalEntryTable.setColumnWidth("rowNum", 100);
        journalEntryTable.setColumnExpandRatio("textEn", 5f);
        journalEntryTable.setColumnExpandRatio("textRu", 5f);
        journalEntryTable.setColumnExpandRatio("translateColumn", 5f);
        journalEntryTable.setColumnExpandRatio("translator", 1f);
        journalEntryTable.setColumnWidth("translator", 131);
        journalEntryTable.setColumnExpandRatio("changeTime", 1.7f);
        journalEntryTable.setColumnWidth("changeTime", 190);
        journalEntryTable.setColumnExpandRatio("saveColumn", 1.1f);
        journalEntryTable.setColumnWidth("saveColumn", 115);
        journalEntryTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        journalEntryTable.setColumnWidth("linkedItemCoumn", 47);
        journalEntryTable.setEditable(true);
        journalEntryTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_JOURNAL_ENTRIES"));
        journalEntryTable.setSortEnabled(false);
        journalEntryTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(journalEntryTable, "Записи журнала");

        achievementTable = new Table();
        achievementTable.setSizeFull();
        achievementContainer = service.getJPAContainerContainerForClass(GSpreadSheetsAchievement.class);
        achievementContainer.setBuffered(true);
        achievementTable.setContainerDataSource(achievementContainer);
        achievementTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        achievementTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_ACHIEVEMENTS"));
        achievementTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        achievementTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        achievementTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        achievementTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        achievementTable.setColumnExpandRatio("rowNum", 1.5f);
        achievementTable.setColumnWidth("rowNum", 100);
        achievementTable.setColumnExpandRatio("textEn", 5f);
        achievementTable.setColumnExpandRatio("textRu", 5f);
        achievementTable.setColumnExpandRatio("translateColumn", 5f);
        achievementTable.setColumnExpandRatio("translator", 1f);
        achievementTable.setColumnWidth("translator", 131);
        achievementTable.setColumnExpandRatio("changeTime", 1.7f);
        achievementTable.setColumnWidth("changeTime", 190);
        achievementTable.setColumnExpandRatio("saveColumn", 1.1f);
        achievementTable.setColumnWidth("saveColumn", 115);
        achievementTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        achievementTable.setColumnWidth("linkedItemCoumn", 47);
        achievementTable.setEditable(true);
        achievementTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_ACHIEVEMENTS"));
        achievementTable.setSortEnabled(false);
        achievementTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(achievementTable, "Достижения");

        achievementDescriptionTable = new Table();
        achievementDescriptionTable.setSizeFull();
        achievementDescriptionContainer = service.getJPAContainerContainerForClass(GSpreadSheetsAchievementDescription.class);
        achievementDescriptionContainer.setBuffered(true);
        achievementDescriptionTable.setContainerDataSource(achievementDescriptionContainer);
        achievementDescriptionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        achievementDescriptionTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_ACHIEVEMENT_DESCRIPTIONS"));
        achievementDescriptionTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        achievementDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        achievementDescriptionTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        achievementDescriptionTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        achievementDescriptionTable.setColumnExpandRatio("rowNum", 1.5f);
        achievementDescriptionTable.setColumnWidth("rowNum", 100);
        achievementDescriptionTable.setColumnExpandRatio("textEn", 5f);
        achievementDescriptionTable.setColumnExpandRatio("textRu", 5f);
        achievementDescriptionTable.setColumnExpandRatio("translateColumn", 5f);
        achievementDescriptionTable.setColumnExpandRatio("translator", 1f);
        achievementDescriptionTable.setColumnWidth("translator", 131);
        achievementDescriptionTable.setColumnExpandRatio("changeTime", 1.7f);
        achievementDescriptionTable.setColumnWidth("changeTime", 190);
        achievementDescriptionTable.setColumnExpandRatio("saveColumn", 1.1f);
        achievementDescriptionTable.setColumnWidth("saveColumn", 115);
        achievementDescriptionTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        achievementDescriptionTable.setColumnWidth("linkedItemCoumn", 47);
        achievementDescriptionTable.setEditable(true);
        achievementDescriptionTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_ACHIEVEMENT_DESCRIPTIONS"));
        achievementDescriptionTable.setSortEnabled(false);
        achievementDescriptionTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(achievementDescriptionTable, "Описания достижений");

        abilityDescriptionTable = new Table();
        abilityDescriptionTable.setSizeFull();
        abilityDescriptionContainer = service.getJPAContainerContainerForClass(GSpreadSheetsAbilityDescription.class);
        abilityDescriptionContainer.setBuffered(true);
        abilityDescriptionTable.setContainerDataSource(abilityDescriptionContainer);
        abilityDescriptionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        abilityDescriptionTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_ABILITY_DESCRIPTIONS"));
        abilityDescriptionTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        abilityDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        abilityDescriptionTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        abilityDescriptionTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        abilityDescriptionTable.setColumnExpandRatio("rowNum", 1.5f);
        abilityDescriptionTable.setColumnWidth("rowNum", 100);
        abilityDescriptionTable.setColumnExpandRatio("textEn", 5f);
        abilityDescriptionTable.setColumnExpandRatio("textRu", 5f);
        abilityDescriptionTable.setColumnExpandRatio("translateColumn", 5f);
        abilityDescriptionTable.setColumnExpandRatio("translator", 1f);
        abilityDescriptionTable.setColumnWidth("translator", 131);
        abilityDescriptionTable.setColumnExpandRatio("changeTime", 1.7f);
        abilityDescriptionTable.setColumnWidth("changeTime", 190);
        abilityDescriptionTable.setColumnExpandRatio("saveColumn", 1.1f);
        abilityDescriptionTable.setColumnWidth("saveColumn", 115);
        abilityDescriptionTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        abilityDescriptionTable.setColumnWidth("linkedItemCoumn", 47);
        abilityDescriptionTable.setEditable(true);
        abilityDescriptionTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_ABILITY_DESCRIPTIONS"));
        abilityDescriptionTable.setSortEnabled(false);
        abilityDescriptionTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(abilityDescriptionTable, "Описания способностей");

        noteTable = new Table();
        noteTable.setSizeFull();
        noteContainer = service.getJPAContainerContainerForClass(GSpreadSheetsNote.class);
        noteContainer.setBuffered(true);
        noteTable.setContainerDataSource(noteContainer);
        noteTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        noteTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_NOTES"));
        noteTable.addGeneratedColumn("linkedItemCoumn", linkedItemColumnGenerator);
        noteTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "saveColumn", "linkedItemCoumn", "weight", "translator", "changeTime", "translateColumn"});
        noteTable.setColumnHeaders(new String[]{"Номер строки", "Текст", "Перевод", "", "", "Порядок", "Переводчик", "Время", ""});
        noteTable.sort(new Object[]{"rowNum"}, new boolean[]{true});
        noteTable.setColumnExpandRatio("rowNum", 1.5f);
        noteTable.setColumnWidth("rowNum", 100);
        noteTable.setColumnExpandRatio("textEn", 5f);
        noteTable.setColumnExpandRatio("textRu", 5f);
        noteTable.setColumnExpandRatio("translateColumn", 5f);
        noteTable.setColumnExpandRatio("translator", 1f);
        noteTable.setColumnWidth("translator", 131);
        noteTable.setColumnExpandRatio("changeTime", 1.7f);
        noteTable.setColumnWidth("changeTime", 190);
        noteTable.setColumnExpandRatio("saveColumn", 1.1f);
        noteTable.setColumnWidth("saveColumn", 115);
        noteTable.setColumnExpandRatio("linkedItemCoumn", 1.1f);
        noteTable.setColumnWidth("linkedItemCoumn", 47);
        noteTable.setEditable(true);
        noteTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_NOTES"));
        noteTable.setSortEnabled(false);
        noteTable.setConverter("weight", new WeightConverter());
        tableTabs.addTab(noteTable, "Письма");

        esoInterfaceTable = new Table();
        esoInterfaceTable.setSizeFull();
        esoInterfaceContainer = service.getJPAContainerContainerForClass(EsoInterfaceVariable.class);
        esoInterfaceContainer.setBuffered(true);
        esoInterfaceTable.setContainerDataSource(esoInterfaceContainer);
        esoInterfaceTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
        esoInterfaceTable.addGeneratedColumn("saveColumn", new SaveColumnGenerator("ROLE_DIRECT_ACCESS_INTERFACE_VARIABLES"));
        esoInterfaceTable.setVisibleColumns(new Object[]{"name", "textEn", "textRu", "saveColumn", "translator", "changeTime", "translateColumn"});
        esoInterfaceTable.setColumnHeaders(new String[]{"Переменная", "Текст", "Перевод", "", "Переводчик", "Время", ""});
        esoInterfaceTable.sort(new Object[]{"id"}, new boolean[]{true});
        esoInterfaceTable.setColumnExpandRatio("name", 3f);
        esoInterfaceTable.setColumnExpandRatio("textEn", 4f);
        esoInterfaceTable.setColumnExpandRatio("textRu", 4f);
        esoInterfaceTable.setColumnExpandRatio("translateColumn", 5f);
        esoInterfaceTable.setColumnExpandRatio("translator", 1f);
        esoInterfaceTable.setColumnWidth("translator", 131);
        esoInterfaceTable.setColumnExpandRatio("changeTime", 1.7f);
        esoInterfaceTable.setColumnWidth("changeTime", 190);
        esoInterfaceTable.setColumnExpandRatio("saveColumn", 1.1f);
        esoInterfaceTable.setColumnWidth("saveColumn", 115);
        esoInterfaceTable.setEditable(true);
        esoInterfaceTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_INTERFACE_VARIABLES"));
        esoInterfaceTable.setSortEnabled(false);
        tableTabs.addTab(esoInterfaceTable, "Строки интерфейса");

        this.addComponent(tableTabs);
        this.setExpandRatio(tableTabs, 20f);

    }

    private void filterTranslations() {
        try {
            newTranslationsContainer.refresh();
        } catch (Exception ex) {

        }
        newTranslationsContainer.removeAllContainerFilters();
        newTranslationsContainer.addContainerFilter(new IsNull("playerTopic"));
        newTranslationsContainer.addContainerFilter(new IsNull("npcTopic"));
        newTranslationsContainer.addContainerFilter(new IsNull("greeting"));
        newTranslationsContainer.addContainerFilter(new IsNull("subtitle"));
        newTranslationsContainer.addContainerFilter(new Compare.Equal("status", statusFilter.getValue()));
        if (translatorBox.getValue() != null) {
            newTranslationsContainer.addContainerFilter(new Compare.Equal("author", translatorBox.getValue()));
        }
        if (translateTypeBox.getValue() != null) {
            if (translateTypeBox.getValue().equals("GSpreadSheetsActivator")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsActivator")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsAchievement")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsAchievement")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsAchievementDescription")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsAchievementDescription")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsNote")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsNote")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsItemDescription")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsItemDescription")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsItemName")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsItemName")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsJournalEntry")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsJournalEntry")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsLocationName")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsLocationName")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsNpcName")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsNpcName")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsNpcPhrase")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsNpcPhrase")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsPlayerPhrase")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsPlayerPhrase")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsQuestDescription")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsQuestDescription")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsQuestDirection")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsQuestDirection")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsQuestName")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("spreadSheetsQuestName")));
            }
            if (translateTypeBox.getValue().equals("GSpreadSheetsAbilityDescription")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("sheetsAbilityDescription")));
            }
            if (translateTypeBox.getValue().equals("EsoInterfaceVariable")) {
                newTranslationsContainer.addContainerFilter(new com.vaadin.data.util.filter.Not(new com.vaadin.data.util.filter.IsNull("esoInterfaceVariable")));
            }
        }
    }

    private void search() {
        if (searchField.getValue() != null && searchField.getValue().length() > 0) {
            hc = service.searchInCatalogs(searchField.getValue(), hc);
        }
    }

    public void setWidth() {
        resultTable.setWidth(this.getUI().getWidth() - 5f, this.getUI().getWidthUnits());
    }

    private class AddTranslationClickListener implements ClickListener {

        private final EntityItem item;
        private final VerticalLayout vl;
        private final JPAContainer container;
        private final Table table;

        public AddTranslationClickListener(EntityItem item_, VerticalLayout vl_, JPAContainer container_, Table table_) {
            this.item = item_;
            this.vl = vl_;
            this.container = container_;
            this.table = table_;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            Object entity = item.getEntity();
            TranslatedText tt = new TranslatedText();
            tt.setAuthor(SpringSecurityHelper.getSysAccount());
            if (entity instanceof GSpreadSheetsActivator) {
                tt.setSpreadSheetsActivator((GSpreadSheetsActivator) entity);
            } else if (entity instanceof GSpreadSheetsAchievement) {
                tt.setSpreadSheetsAchievement((GSpreadSheetsAchievement) entity);
            } else if (entity instanceof GSpreadSheetsAchievementDescription) {
                tt.setSpreadSheetsAchievementDescription((GSpreadSheetsAchievementDescription) entity);
            } else if (entity instanceof GSpreadSheetsNote) {
                tt.setSpreadSheetsNote((GSpreadSheetsNote) entity);
            } else if (entity instanceof GSpreadSheetsItemDescription) {
                tt.setSpreadSheetsItemDescription((GSpreadSheetsItemDescription) entity);
            } else if (entity instanceof GSpreadSheetsItemName) {
                tt.setSpreadSheetsItemName((GSpreadSheetsItemName) entity);
            } else if (entity instanceof GSpreadSheetsJournalEntry) {
                tt.setSpreadSheetsJournalEntry((GSpreadSheetsJournalEntry) entity);
            } else if (entity instanceof GSpreadSheetsLocationName) {
                tt.setSpreadSheetsLocationName((GSpreadSheetsLocationName) entity);
            } else if (entity instanceof GSpreadSheetsNpcName) {
                tt.setSpreadSheetsNpcName((GSpreadSheetsNpcName) entity);
            } else if (entity instanceof GSpreadSheetsNpcPhrase) {
                tt.setSpreadSheetsNpcPhrase((GSpreadSheetsNpcPhrase) entity);
            } else if (entity instanceof GSpreadSheetsPlayerPhrase) {
                tt.setSpreadSheetsPlayerPhrase((GSpreadSheetsPlayerPhrase) entity);
            } else if (entity instanceof GSpreadSheetsQuestDescription) {
                tt.setSpreadSheetsQuestDescription((GSpreadSheetsQuestDescription) entity);
            } else if (entity instanceof GSpreadSheetsQuestDirection) {
                tt.setSpreadSheetsQuestDirection((GSpreadSheetsQuestDirection) entity);
            } else if (entity instanceof GSpreadSheetsQuestName) {
                tt.setSpreadSheetsQuestName((GSpreadSheetsQuestName) entity);
            } else if (entity instanceof GSpreadSheetsAbilityDescription) {
                tt.setSheetsAbilityDescription((GSpreadSheetsAbilityDescription) entity);
            } else if (entity instanceof EsoInterfaceVariable) {
                tt.setEsoInterfaceVariable((EsoInterfaceVariable) entity);
            }
            vl.addComponent(new TranslationCell(tt, container, item, table));
            event.getButton().setVisible(false);
        }

    }

    private class TranslateColumnGenerator implements Table.ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            final VerticalLayout result = new VerticalLayout();
            result.setSizeFull();
            EntityItem item = (EntityItem) source.getItem(itemId);
            Set<TranslatedText> list = (Set<TranslatedText>) item.getItemProperty("translatedTexts").getValue();
            List<SysAccount> accounts = new ArrayList<>();
            if (list != null) {
                for (TranslatedText tt : list) {
                    result.addComponent(new TranslationCell(tt, (JPAContainer) source.getContainerDataSource(), item, source));
                    accounts.add(tt.getAuthor());
                }
            }
            if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
                Button addTranslation = new Button("Добавить");
                addTranslation.addClickListener(new AddTranslationClickListener(item, result, (JPAContainer) source.getContainerDataSource(), source));
                if (list != null && !list.isEmpty()) {
                    TranslationCell component = (TranslationCell) result.getComponent(result.getComponentCount() - 1);
                    component.getActionLayout().addComponent(addTranslation);
                } else {
                    result.addComponent(addTranslation);
                }

            }
            return result;
        }

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
                if (!SpringSecurityHelper.hasRole("ROLE_DIRECT_ACCESS") && !SpringSecurityHelper.hasRole(tableEditRole)) {
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

    private class LinkedItemColumnGenerator implements Table.ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            EntityItem item = (EntityItem) source.getContainerDataSource().getItem(itemId);
            GSpreadSheetEntity entity = (GSpreadSheetEntity) item.getEntity();
            GSpreadSheetLinkRouter.RouteEntry route = GSpreadSheetLinkRouter.getRoute(entity.getaId());
            if (route != null) {
                Button b = new Button("->");
                b.setData(entity);
                b.addClickListener(linkedItemClickListener);
                return b;
            }
            return null;
        }

    }

    private class LinkedItemClickListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            GSpreadSheetEntity entity = (GSpreadSheetEntity) event.getButton().getData();
            DAO linkedItem = service.getLinkedItem(entity);
            goToItem(linkedItem);
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

    private void goToItem(DAO entity) {
        Component targetTabId = null;
        Table targetTable = null;
        Integer rowNum = 1;
        Long itemId = null;
        if (entity instanceof GSpreadSheetsNpcName) {
            targetTabId = npcNameTable;
            targetTable = npcNameTable;
            rowNum = ((GSpreadSheetsNpcName) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsNpcName) entity).getId();
        } else if (entity instanceof GSpreadSheetsLocationName) {
            targetTabId = locationNameTable;
            targetTable = locationNameTable;
            rowNum = ((GSpreadSheetsLocationName) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsLocationName) entity).getId();
        } else if (entity instanceof GSpreadSheetsPlayerPhrase) {
            targetTabId = playerPhraseTable;
            targetTable = playerPhraseTable;
            rowNum = ((GSpreadSheetsPlayerPhrase) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsPlayerPhrase) entity).getId();
        } else if (entity instanceof GSpreadSheetsNpcPhrase) {
            targetTabId = npcPhraseTable;
            targetTable = npcPhraseTable;
            rowNum = ((GSpreadSheetsNpcPhrase) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsNpcPhrase) entity).getId();
        } else if (entity instanceof GSpreadSheetsQuestName) {
            targetTabId = questNameTable;
            targetTable = questNameTable;
            rowNum = ((GSpreadSheetsQuestName) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsQuestName) entity).getId();
        } else if (entity instanceof GSpreadSheetsQuestDescription) {
            targetTabId = questDescriptionTable;
            targetTable = questDescriptionTable;
            rowNum = ((GSpreadSheetsQuestDescription) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsQuestDescription) entity).getId();
        } else if (entity instanceof GSpreadSheetsQuestDirection) {
            targetTabId = questDirectionTable;
            targetTable = questDirectionTable;
            rowNum = ((GSpreadSheetsQuestDirection) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsQuestDirection) entity).getId();
        } else if (entity instanceof GSpreadSheetsActivator) {
            targetTabId = activatorTable;
            targetTable = activatorTable;
            rowNum = ((GSpreadSheetsActivator) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsActivator) entity).getId();
        } else if (entity instanceof GSpreadSheetsAchievement) {
            targetTabId = achievementTable;
            targetTable = achievementTable;
            rowNum = ((GSpreadSheetsAchievement) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsAchievement) entity).getId();
        } else if (entity instanceof GSpreadSheetsAchievementDescription) {
            targetTabId = achievementDescriptionTable;
            targetTable = achievementDescriptionTable;
            rowNum = ((GSpreadSheetsAchievementDescription) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsAchievementDescription) entity).getId();
        } else if (entity instanceof GSpreadSheetsNote) {
            targetTabId = noteTable;
            targetTable = noteTable;
            rowNum = ((GSpreadSheetsNote) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsNote) entity).getId();
        } else if (entity instanceof GSpreadSheetsJournalEntry) {
            targetTabId = journalEntryTable;
            targetTable = journalEntryTable;
            rowNum = ((GSpreadSheetsJournalEntry) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsJournalEntry) entity).getId();
        } else if (entity instanceof GSpreadSheetsItemName) {
            targetTabId = itemNameLayout;
            targetTable = itemNameTable;
            rowNum = ((GSpreadSheetsItemName) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsItemName) entity).getId();
        } else if (entity instanceof GSpreadSheetsItemDescription) {
            targetTabId = itemDescriptionTable;
            targetTable = itemDescriptionTable;
            rowNum = ((GSpreadSheetsItemDescription) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsItemDescription) entity).getId();
        } else if (entity instanceof GSpreadSheetsAbilityDescription) {
            targetTabId = abilityDescriptionTable;
            targetTable = abilityDescriptionTable;
            rowNum = ((GSpreadSheetsAbilityDescription) entity).getRowNum().intValue();
            itemId = ((GSpreadSheetsAbilityDescription) entity).getId();
        } else if (entity instanceof EsoInterfaceVariable) {
            targetTabId = esoInterfaceTable;
            targetTable = esoInterfaceTable;
            rowNum = null;
            itemId = ((EsoInterfaceVariable) entity).getId();
        }
        if (rowNum != null) {
            rowNum--;
            if (rowNum > 0) {
                rowNum--;
            }
            if (targetTabId != null) {
                tableTabs.setSelectedTab(targetTabId);
                targetTable.setCurrentPageFirstItemIndex(rowNum);
                targetTable.select(itemId);
            }
        } else {
            if (targetTabId != null) {
                tableTabs.setSelectedTab(targetTabId);
                targetTable.select(itemId);
                targetTable.setCurrentPageFirstItemId(itemId);
            }
        }
    }

    private class SearchTableRowClickListener implements ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            DAO entity = (DAO) event.getItemId();
            goToItem(entity);
        }

    }

    private class TranslationsTableRowClickListener implements ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            EntityItem item = (EntityItem) event.getItem();
            TranslatedText tt = (TranslatedText) item.getEntity();
            Component targetTabId = null;
            Table targetTable = null;
            Integer rowNum = 1;
            Long itemId = null;
            if (tt.getSpreadSheetsNpcName() != null) {
                targetTabId = npcNameTable;
                targetTable = npcNameTable;
                rowNum = tt.getSpreadSheetsNpcName().getRowNum().intValue();
                itemId = tt.getSpreadSheetsNpcName().getId();
            } else if (tt.getSpreadSheetsLocationName() != null) {
                targetTabId = locationNameTable;
                targetTable = locationNameTable;
                rowNum = tt.getSpreadSheetsLocationName().getRowNum().intValue();
                itemId = tt.getSpreadSheetsLocationName().getId();
            } else if (tt.getSpreadSheetsPlayerPhrase() != null) {
                targetTabId = playerPhraseTable;
                targetTable = playerPhraseTable;
                rowNum = tt.getSpreadSheetsPlayerPhrase().getRowNum().intValue();
                itemId = tt.getSpreadSheetsPlayerPhrase().getId();
            } else if (tt.getSpreadSheetsNpcPhrase() != null) {
                targetTabId = npcPhraseTable;
                targetTable = npcPhraseTable;
                rowNum = tt.getSpreadSheetsNpcPhrase().getRowNum().intValue();
                itemId = tt.getSpreadSheetsNpcPhrase().getId();
            } else if (tt.getSpreadSheetsQuestName() != null) {
                targetTabId = questNameTable;
                targetTable = questNameTable;
                rowNum = tt.getSpreadSheetsQuestName().getRowNum().intValue();
                itemId = tt.getSpreadSheetsQuestName().getId();
            } else if (tt.getSpreadSheetsQuestDescription() != null) {
                targetTabId = questDescriptionTable;
                targetTable = questDescriptionTable;
                rowNum = tt.getSpreadSheetsQuestDescription().getRowNum().intValue();
                itemId = tt.getSpreadSheetsQuestDescription().getId();
            } else if (tt.getSpreadSheetsQuestDirection() != null) {
                targetTabId = questDirectionTable;
                targetTable = questDirectionTable;
                rowNum = tt.getSpreadSheetsQuestDirection().getRowNum().intValue();
                itemId = tt.getSpreadSheetsQuestDirection().getId();
            } else if (tt.getSpreadSheetsActivator() != null) {
                targetTabId = activatorTable;
                targetTable = activatorTable;
                rowNum = tt.getSpreadSheetsActivator().getRowNum().intValue();
                itemId = tt.getSpreadSheetsActivator().getId();
            } else if (tt.getSpreadSheetsAchievement() != null) {
                targetTabId = achievementTable;
                targetTable = achievementTable;
                rowNum = tt.getSpreadSheetsAchievement().getRowNum().intValue();
                itemId = tt.getSpreadSheetsAchievement().getId();
            } else if (tt.getSpreadSheetsAchievementDescription() != null) {
                targetTabId = achievementDescriptionTable;
                targetTable = achievementDescriptionTable;
                rowNum = tt.getSpreadSheetsAchievementDescription().getRowNum().intValue();
                itemId = tt.getSpreadSheetsAchievementDescription().getId();
            } else if (tt.getSpreadSheetsNote() != null) {
                targetTabId = noteTable;
                targetTable = noteTable;
                rowNum = tt.getSpreadSheetsNote().getRowNum().intValue();
                itemId = tt.getSpreadSheetsNote().getId();
            } else if (tt.getSpreadSheetsJournalEntry() != null) {
                targetTabId = journalEntryTable;
                targetTable = journalEntryTable;
                rowNum = tt.getSpreadSheetsJournalEntry().getRowNum().intValue();
                itemId = tt.getSpreadSheetsJournalEntry().getId();
            } else if (tt.getSpreadSheetsItemName() != null) {
                targetTabId = itemNameLayout;
                targetTable = itemNameTable;
                rowNum = tt.getSpreadSheetsItemName().getRowNum().intValue();
                itemId = tt.getSpreadSheetsItemName().getId();
            } else if (tt.getSpreadSheetsItemDescription() != null) {
                targetTabId = itemDescriptionTable;
                targetTable = itemDescriptionTable;
                rowNum = tt.getSpreadSheetsItemDescription().getRowNum().intValue();
                itemId = tt.getSpreadSheetsItemDescription().getId();
            } else if (tt.getSheetsAbilityDescription() != null) {
                targetTabId = abilityDescriptionTable;
                targetTable = abilityDescriptionTable;
                rowNum = tt.getSheetsAbilityDescription().getRowNum().intValue();
                itemId = tt.getSheetsAbilityDescription().getId();
            } else if (tt.getEsoInterfaceVariable() != null) {
                targetTabId = esoInterfaceTable;
                targetTable = esoInterfaceTable;
                rowNum = null;
                itemId = tt.getEsoInterfaceVariable().getId();
            }
            if (rowNum != null) {
                rowNum--;
                if (rowNum > 0) {
                    rowNum--;
                }
                if (targetTabId != null) {
                    tableTabs.setSelectedTab(targetTabId);
                    targetTable.setCurrentPageFirstItemIndex(rowNum);
                    targetTable.select(itemId);
                }
            } else {
                if (targetTabId != null) {
                    tableTabs.setSelectedTab(targetTabId);
                    targetTable.setCurrentPageFirstItemId(itemId);
                    targetTable.select(itemId);
                }
            }
        }

    }

    private class WeightConverter implements Converter<String, Integer> {

        @Override
        public Integer convertToModel(String value, Class<? extends Integer> targetType, Locale locale) throws ConversionException {
            Integer result = null;
            try {
                result = new Integer(value);
            } catch (NumberFormatException ex) {

            }
            return result;
        }

        @Override
        public String convertToPresentation(Integer value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            if (value != null) {
                return value.toString();
            } else {
                return "";
            }
        }

        @Override
        public Class<Integer> getModelType() {
            return Integer.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }

    }

    private class TranslationCell extends HorizontalLayout {

        private TextArea translation;
        private Button save;
        private Button accept;
        private Button reject;
        private final TranslatedText translatedText;
        private final JPAContainer container;
        private final EntityItem item;
        private final Table table;
        private VerticalLayout actionLayout;

        public TranslationCell(TranslatedText translatedText_, JPAContainer container_, EntityItem item_, Table table_) {
            this.setSizeFull();
            this.translatedText = translatedText_;
            this.container = container_;
            this.item = item_;
            this.table = table_;
            String translatedStatus = "нет";
            if (translatedText.getStatus() != null) {
                translatedStatus = translatedText.getStatus().toString();
            }
            StringBuilder caption = new StringBuilder();
            caption.append("Статус: ").append(translatedStatus).append(", автор: ").append(translatedText.getAuthor().getLogin());
            if (translatedText.getStatus() == TRANSLATE_STATUS.ACCEPTED && (translatedText.getApprovedBy() != null) && (translatedText.getApptovedTime() != null)) {
                caption.append(", кто принял: ").append(translatedText.getApprovedBy().getLogin());
            }
            if (translatedText.getCreateTime() != null) {
                caption.append(", создано: ").append(sdf.format(translatedText.getCreateTime()));
            }
            if (translatedText.getChangeTime() != null) {
                caption.append(", изменено: ").append(sdf.format(translatedText.getChangeTime()));
            }
            actionLayout = new VerticalLayout();
            actionLayout.setSizeFull();

            translation = new TextArea();
            translation.setDescription(caption.toString());
            translation.setSizeFull();
            translation.setNullRepresentation("");
            translation.setImmediate(true);
            translation.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.TIMEOUT);
            translation.setTextChangeTimeout(5000);
            translation.setValue(translatedText_.getText());
            translation.addTextChangeListener(new FieldEvents.TextChangeListener() {

                @Override
                public void textChange(FieldEvents.TextChangeEvent event) {
                    save.setVisible(true);

                    if (event.getText() == null || event.getText().isEmpty()) {
                        save.setCaption("Удалить");
                    } else {
                        translatedText.setText(event.getText());
                        save.setCaption("Сохранить");
                    }
                    String status = "нет";
                    if (translatedText.getStatus() != null) {

                        status = translatedText.getStatus().toString();
                    }
                    StringBuilder caption = new StringBuilder();
                    caption.append("Статус: ").append(status).append(", автор: ").append(translatedText.getAuthor().getLogin());
                    if (translatedText.getStatus() == TRANSLATE_STATUS.ACCEPTED && (translatedText.getApprovedBy() != null) && (translatedText.getApptovedTime() != null)) {
                        caption.append(", кто принял: ").append(translatedText.getApprovedBy().getLogin());
                    }
                    if (translatedText.getCreateTime() != null) {
                        caption.append(", создано: ").append(sdf.format(translatedText.getCreateTime()));
                    }
                    if (translatedText.getChangeTime() != null) {
                        caption.append(", изменено: ").append(sdf.format(translatedText.getChangeTime()));
                    }
                    translation.setDescription(caption.toString());
                }
            });
            if (SpringSecurityHelper.getSysAccount().equals(translatedText_.getAuthor())) {
                translation.setReadOnly(false);
            } else if (SpringSecurityHelper.hasRole("ROLE_ADMIN") || SpringSecurityHelper.hasRole("ROLE_APPROVE")) {
                translation.setReadOnly(false);
            } else {
                translation.setReadOnly(true);
            }
            this.addComponent(translation);
            save = new Button("Сохранить");
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    translatedText.setText(translation.getValue());
                    service.saveTranslatedText(translatedText);
                    try {
                        container.refresh();
                    } catch (Exception ex) {

                    }
                }
            });

            actionLayout.addComponent(save);
            save.setVisible(false);
            if (SpringSecurityHelper.hasRole("ROLE_ADMIN") || SpringSecurityHelper.hasRole("ROLE_APPROVE")) {
                if (translatedText.getId() != null && translatedText.getStatus() == TRANSLATE_STATUS.NEW) {
                    accept = new Button("Принять");
                    accept.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            translatedText.setText(translation.getValue());
                            service.acceptTranslatedText(translatedText);
                            try {
                                container.refresh();

                            } catch (Exception ex) {

                            }
                        }
                    });
                    actionLayout.addComponent(accept);
                    reject = new Button("Отклонить");
                    reject.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            translatedText.setText(translation.getValue());
                            service.rejectTranslatedText(translatedText);
                            try {
                                container.refresh();
                            } catch (Exception ex) {

                            }
                        }
                    });
                    actionLayout.addComponent(reject);

                }

            }
            this.addComponent(actionLayout);
            this.setExpandRatio(translation, 8f);
            this.setExpandRatio(actionLayout, 2f);
        }

        public VerticalLayout getActionLayout() {
            return actionLayout;
        }

    }

}
