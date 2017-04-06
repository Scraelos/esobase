/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.specification.TranslatedTextSpecification;
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
import org.esn.esobase.model.TranslatedEntity;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.model.lib.DAO;
import org.esn.esobase.security.SpringSecurityHelper;
import org.esn.esobase.tools.GSpreadSheetLinkRouter;
import org.esn.esobase.view.ui.GspreadSheetGrid;
import org.esn.esobase.view.ui.RefreshableGrid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.viritin.SortableLazyList;
import org.vaadin.viritin.grid.GeneratedPropertyListContainer;
import org.vaadin.viritin.grid.MGrid;

/**
 *
 * @author scraelos
 */
public class DirectTableEditTab extends VerticalLayout {

    private DBService service;
    private TextField searchField;
    private Button searchButton;
    private Table resultTable;
    private TabSheet searchTabs;
    private HierarchicalContainer hc = new HierarchicalContainer();
    private TabSheet tableTabs;

    static final int PAGESIZE = 20;

    private GeneratedPropertyListContainer<GSpreadSheetsNpcName> gSpreadSheetsNpcNameContainer;
    private GspreadSheetGrid npcNameTable;

    private GeneratedPropertyListContainer<GSpreadSheetsLocationName> locationNameContainer;
    private GspreadSheetGrid locationNameTable;

    private GspreadSheetGrid activatorTable;
    private GeneratedPropertyListContainer<GSpreadSheetsActivator> activatorContainer;

    private GspreadSheetGrid playerPhraseTable;
    private GeneratedPropertyListContainer<GSpreadSheetsPlayerPhrase> playerPhraseContainer;

    private GspreadSheetGrid npcPhraseTable;
    private GeneratedPropertyListContainer<GSpreadSheetsNpcPhrase> npcPhraseContainer;

    private GspreadSheetGrid questNameTable;
    private GeneratedPropertyListContainer<GSpreadSheetsQuestName> questNameContainer;

    private GspreadSheetGrid questDescriptionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsQuestDescription> questDescriptionContainer;

    private GspreadSheetGrid questDirectionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsQuestDirection> questDirectionContainer;

    private VerticalLayout itemNameLayout;
    private GspreadSheetGrid itemNameTable;
    private GeneratedPropertyListContainer<GSpreadSheetsItemName> itemNameContainer;

    private GspreadSheetGrid itemDescriptionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsItemDescription> itemDescriptionContainer;

    private GspreadSheetGrid journalEntryTable;
    private GeneratedPropertyListContainer<GSpreadSheetsJournalEntry> journalEntryContainer;

    private GspreadSheetGrid achievementTable;
    private GeneratedPropertyListContainer<GSpreadSheetsAchievement> achievementContainer;

    private GspreadSheetGrid achievementDescriptionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsAchievementDescription> achievementDescriptionContainer;

    private GspreadSheetGrid abilityDescriptionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsAbilityDescription> abilityDescriptionContainer;

    private GspreadSheetGrid noteTable;
    private GeneratedPropertyListContainer<GSpreadSheetsNote> noteContainer;

    private LinkedItemColumnGenerator linkedItemColumnGenerator;
    private LinkedItemClickListener linkedItemClickListener;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private ComboBox statusFilter;
    private ComboBox translatorBox;
    private ComboBox translateTypeBox;
    private Button filterTranslationButton;
    private MGrid newTranslationsTable;
    private TranslatedTextSpecification translatedTextSpecification;
    private GeneratedPropertyListContainer<TranslatedText> newTranslationsContainer;
    private BeanItemContainer<SysAccount> sysAccountContainer = new BeanItemContainer<>(SysAccount.class);

    public DirectTableEditTab(DBService service_) {
        this.service = service_;
        this.setSizeFull();
        linkedItemColumnGenerator = new LinkedItemColumnGenerator();
        linkedItemClickListener = new LinkedItemClickListener();
        searchTabs = new TabSheet();
        searchTabs.setSizeFull();
        searchTabs.setHeight(250f, Unit.PIXELS);
        VerticalLayout searchInCatalogsLayout = new VerticalLayout();
        searchInCatalogsLayout.setSizeFull();
        HorizontalLayout hl = new HorizontalLayout();
        searchField = new TextField();
        searchField.setWidth(300, Unit.PIXELS);
        searchField.addShortcutListener(new ShortcutListener("Search shortcut", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                search();
            }
        });
        hl.addComponent(searchField);
        searchButton = new Button("Поиск");
        searchButton.setIcon(FontAwesome.SEARCH);
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
        translatedTextSpecification = new TranslatedTextSpecification();
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
        translateTypeBox.addItem("spreadSheetsActivator");
        translateTypeBox.setItemCaption("spreadSheetsActivator", "Активаторы");
        translateTypeBox.addItem("spreadSheetsAchievement");
        translateTypeBox.setItemCaption("spreadSheetsAchievement", "Достижения");
        translateTypeBox.addItem("spreadSheetsAchievementDescription");
        translateTypeBox.setItemCaption("spreadSheetsAchievementDescription", "Описания достижений");
        translateTypeBox.addItem("spreadSheetsNote");
        translateTypeBox.setItemCaption("spreadSheetsNote", "Письма");
        translateTypeBox.addItem("spreadSheetsItemDescription");
        translateTypeBox.setItemCaption("spreadSheetsItemDescription", "Описания предметов");
        translateTypeBox.addItem("spreadSheetsItemName");
        translateTypeBox.setItemCaption("spreadSheetsItemName", "Названия предметов");
        translateTypeBox.addItem("spreadSheetsJournalEntry");
        translateTypeBox.setItemCaption("spreadSheetsJournalEntry", "Записи в журнале");
        translateTypeBox.addItem("spreadSheetsLocationName");
        translateTypeBox.setItemCaption("spreadSheetsLocationName", "Названия локаций");
        translateTypeBox.addItem("spreadSheetsNpcName");
        translateTypeBox.setItemCaption("spreadSheetsNpcName", "Имена NPC");
        translateTypeBox.addItem("spreadSheetsNpcPhrase");
        translateTypeBox.setItemCaption("spreadSheetsNpcPhrase", "Фразы NPC");
        translateTypeBox.addItem("spreadSheetsPlayerPhrase");
        translateTypeBox.setItemCaption("spreadSheetsPlayerPhrase", "Фразы игрока");
        translateTypeBox.addItem("spreadSheetsQuestDescription");
        translateTypeBox.setItemCaption("spreadSheetsQuestDescription", "Описания квестов");
        translateTypeBox.addItem("spreadSheetsQuestDirection");
        translateTypeBox.setItemCaption("spreadSheetsQuestDirection", "Цели квестов");
        translateTypeBox.addItem("spreadSheetsQuestName");
        translateTypeBox.setItemCaption("spreadSheetsQuestName", "Названия квестов");
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
        newTranslationsTable = new MGrid();
        newTranslationsTable.setSizeFull();
        newTranslationsContainer = new GeneratedPropertyListContainer<>(TranslatedText.class);
        newTranslationsTable.setContainerDataSource(newTranslationsContainer);
        filterTranslations();
        newTranslationsContainer.sort(new Object[]{"id"}, new boolean[]{true});
        newTranslationsTable.setContainerDataSource(newTranslationsContainer);
        newTranslationsTable.setColumns("author", "createTime", "text");
        newTranslationsTable.getColumn("author").setHeaderCaption("Автор");
        newTranslationsTable.getColumn("createTime").setHeaderCaption("Дата");
        newTranslationsTable.getColumn("text").setHeaderCaption("Перевод");
        newTranslationsTable.addItemClickListener(new TranslationsTableRowClickListener());
        translationsLayout.addComponent(newTranslationsTable);
        translationsLayout.setExpandRatio(newTranslationsTable, 5f);
        searchTabs.addTab(translationsLayout, "Переводы");
        this.addComponent(searchTabs);
        tableTabs = new TabSheet();
        tableTabs.setSizeFull();
        gSpreadSheetsNpcNameContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsNpcName.class);
        npcNameTable = new GspreadSheetGrid(gSpreadSheetsNpcNameContainer, PAGESIZE, service.getgSpreadSheetsNpcNameRepository());
        gSpreadSheetsNpcNameContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        gSpreadSheetsNpcNameContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(npcNameTable));
        tableTabs.addTab(npcNameTable, "NPC");
        npcNameTable.build();
        npcNameTable.setColumns(new Object[]{"rowNum", "sex", "textEn", "textRu", "infoColumn", "translateColumn"});
        npcNameTable.getColumn("sex").setMinimumWidth(87).setHeaderCaption("Пол").setSortable(false);
        npcNameTable.Refresh();

        locationNameContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsLocationName.class);
        locationNameTable = new GspreadSheetGrid(locationNameContainer, PAGESIZE, service.getgSpreadSheetsLocationNameRepository());
        locationNameContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        locationNameContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(locationNameTable));
        tableTabs.addTab(locationNameTable, "Локации");
        locationNameTable.build();
        locationNameTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        locationNameTable.Refresh();

        activatorContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsActivator.class);
        activatorTable = new GspreadSheetGrid(activatorContainer, PAGESIZE, service.getgSpreadSheetsActivatorRepository());
        activatorContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        activatorContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(activatorTable));
        tableTabs.addTab(activatorTable, "Активаторы");
        activatorTable.build();
        activatorTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        activatorTable.Refresh();

        playerPhraseContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsPlayerPhrase.class);
        playerPhraseTable = new GspreadSheetGrid(playerPhraseContainer, PAGESIZE, service.getgSpreadSheetsPlayerPhraseRepository());
        playerPhraseContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        playerPhraseContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(playerPhraseTable));
        tableTabs.addTab(playerPhraseTable, "Фразы игрока");
        playerPhraseTable.build();
        playerPhraseTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        playerPhraseTable.Refresh();

        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add(".wrapped-text {\n"
                + "    white-space: normal;\n"
                + "    overflow: hidden;\n"
                + "}");
        npcPhraseContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsNpcPhrase.class);
        npcPhraseTable = new GspreadSheetGrid(npcPhraseContainer, PAGESIZE, service.getgSpreadSheetsNpcPhraseRepository());
        npcPhraseContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        npcPhraseContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(npcPhraseTable));
        tableTabs.addTab(npcPhraseTable, "Фразы NPC");
        npcPhraseTable.build();
        npcPhraseTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        npcPhraseTable.Refresh();

        questNameContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsQuestName.class);
        questNameTable = new GspreadSheetGrid(questNameContainer, PAGESIZE, service.getgSpreadSheetsQuestNameRepository());
        questNameContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        questNameContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(questNameTable));
        tableTabs.addTab(questNameTable, "Названия квестов");
        questNameTable.build();
        questNameTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        questNameTable.Refresh();

        questDescriptionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsQuestDescription.class);
        questDescriptionTable = new GspreadSheetGrid(questDescriptionContainer, PAGESIZE, service.getgSpreadSheetsQuestDescriptionRepository());
        questDescriptionContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        questDescriptionContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(questDescriptionTable));
        tableTabs.addTab(questDescriptionTable, "Описания квестов");
        questDescriptionTable.build();
        questDescriptionTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        questDescriptionTable.Refresh();

        questDirectionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsQuestDirection.class);
        questDirectionTable = new GspreadSheetGrid(questDirectionContainer, PAGESIZE, service.getgSpreadSheetsQuestDirectionRepository());
        questDirectionContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        questDirectionContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(questDirectionTable));
        tableTabs.addTab(questDirectionTable, "Цели квестов");
        questDirectionTable.build();
        questDirectionTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        questDirectionTable.Refresh();

        itemNameLayout = new VerticalLayout();
        itemNameLayout.setSizeFull();
        Label itemNameLabel = new Label("ВНИМАНИЕ! В этой таблице НЕЛЬЗЯ:  переводить односложные слова, особенно написанные со строчной буквы.");
        itemNameLabel.setStyleName(ValoTheme.LABEL_COLORED);
        itemNameContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsItemName.class);
        itemNameTable = new GspreadSheetGrid(itemNameContainer, PAGESIZE, service.getgSpreadSheetsItemNameRepository());
        itemNameContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        itemNameContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(itemNameTable));
        itemNameLayout.addComponent(itemNameLabel);
        itemNameLayout.addComponent(itemNameTable);
        itemNameLayout.setExpandRatio(itemNameTable, 1f);
        tableTabs.addTab(itemNameLayout, "Названия предметов");
        itemNameTable.build();
        itemNameTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        itemNameTable.Refresh();

        itemDescriptionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsItemDescription.class);
        itemDescriptionTable = new GspreadSheetGrid(itemDescriptionContainer, PAGESIZE, service.getgSpreadSheetsItemDescriptionRepository());
        itemDescriptionContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        itemDescriptionContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(itemDescriptionTable));
        tableTabs.addTab(itemDescriptionTable, "Описания предметов");
        itemDescriptionTable.build();
        itemDescriptionTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        itemDescriptionTable.Refresh();

        journalEntryContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsJournalEntry.class);
        journalEntryTable = new GspreadSheetGrid(journalEntryContainer, PAGESIZE, service.getgSpreadSheetsJournalEntryRepository());
        journalEntryContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        journalEntryContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(journalEntryTable));
        tableTabs.addTab(journalEntryTable, "Записи журнала");
        journalEntryTable.build();
        journalEntryTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        journalEntryTable.Refresh();

        achievementContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsAchievement.class);
        achievementTable = new GspreadSheetGrid(achievementContainer, PAGESIZE, service.getgSpreadSheetsAchievementRepository());
        achievementContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        achievementContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(achievementTable));
        tableTabs.addTab(achievementTable, "Достижения");
        achievementTable.build();
        achievementTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        achievementTable.Refresh();

        achievementDescriptionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsAchievementDescription.class);
        achievementDescriptionTable = new GspreadSheetGrid(achievementDescriptionContainer, PAGESIZE, service.getgSpreadSheetsAchievementDescriptionRepository());
        achievementDescriptionContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        achievementDescriptionContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(achievementDescriptionTable));
        tableTabs.addTab(achievementDescriptionTable, "Описания достижений");
        achievementDescriptionTable.build();
        achievementDescriptionTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        achievementDescriptionTable.Refresh();

        abilityDescriptionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsAbilityDescription.class);
        abilityDescriptionTable = new GspreadSheetGrid(abilityDescriptionContainer, PAGESIZE, service.getgSpreadSheetsAbilityDescriptionRepository());
        abilityDescriptionContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        abilityDescriptionContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(abilityDescriptionTable));
        tableTabs.addTab(abilityDescriptionTable, "Описания способностей");
        abilityDescriptionTable.build();
        abilityDescriptionTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        abilityDescriptionTable.Refresh();

        noteContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsNote.class);
        noteTable = new GspreadSheetGrid(noteContainer, PAGESIZE, service.getgSpreadSheetsNoteRepository());
        noteContainer.addGeneratedProperty("infoColumn", new InfoPropertyGenerator());
        noteContainer.addGeneratedProperty("translateColumn", new TranslatePropertyGenerator(noteTable));
        tableTabs.addTab(noteTable, "Письма");
        noteTable.build();
        noteTable.setColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        noteTable.Refresh();

        /*esoInterfaceTable = new Table();
         esoInterfaceTable.setSizeFull();
         esoInterfaceContainer = service.getJPAContainerContainerForClass(EsoInterfaceVariable.class);
         esoInterfaceContainer.setBuffered(true);
         esoInterfaceTable.setContainerDataSource(esoInterfaceContainer);
         esoInterfaceTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator());
         esoInterfaceTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator("ROLE_DIRECT_ACCESS_INTERFACE_VARIABLES"));
         esoInterfaceTable.setColumnCollapsingAllowed(true);
         esoInterfaceTable.setVisibleColumns(new Object[]{"name", "textEn", "textRu", "infoColumn", "translateColumn"});
         esoInterfaceTable.setColumnHeaders(new String[]{"Переменная", "Текст", "Перевод", "", ""});
         esoInterfaceTable.sort(new Object[]{"id"}, new boolean[]{true});
         esoInterfaceTable.setColumnExpandRatio("name", 1.5f);
         esoInterfaceTable.setColumnWidth("name", 61);
         esoInterfaceTable.setColumnExpandRatio("textEn", 7f);
         esoInterfaceTable.setColumnExpandRatio("textRu", 7f);
         esoInterfaceTable.setColumnExpandRatio("translateColumn", 7f);
         esoInterfaceTable.setColumnExpandRatio("infoColumn", 1.7f);
         esoInterfaceTable.setColumnWidth("infoColumn", 87);
         esoInterfaceTable.setEditable(true);
         esoInterfaceTable.setTableFieldFactory(new TranslateTableFieldFactory("ROLE_DIRECT_ACCESS_INTERFACE_VARIABLES"));
         esoInterfaceTable.setSortEnabled(false);
         tableTabs.addTab(esoInterfaceTable, "Строки интерфейса");*/
        this.addComponent(tableTabs);
        this.setExpandRatio(tableTabs, 20f);
    }

    private void filterTranslations() {
        if (translatorBox.getValue() != null) {
            translatedTextSpecification.setAuthor((SysAccount) translatorBox.getValue());
        } else {
            translatedTextSpecification.setAuthor(null);
        }
        if (statusFilter.getValue() != null) {
            translatedTextSpecification.setStatus((TRANSLATE_STATUS) statusFilter.getValue());
        } else {
            translatedTextSpecification.setStatus(null);
        }
        if (translateTypeBox.getValue() != null) {
            translatedTextSpecification.setTranslationType((String) translateTypeBox.getValue());
        } else {
            translatedTextSpecification.setTranslationType(null);
        }
        SortableLazyList lazyList = new SortableLazyList<>((int firstRow, boolean sortAscending, String property) -> service.getTranslatedTextRepository().findAll(translatedTextSpecification, new PageRequest(
                firstRow / PAGESIZE,
                PAGESIZE,
                sortAscending ? Sort.Direction.ASC : Sort.Direction.DESC,
                property == null ? "createTime" : property
        )).getContent(),
                () -> (int) service.getTranslatedTextRepository().count(translatedTextSpecification),
                PAGESIZE);
        newTranslationsContainer.setCollection(lazyList);

    }

    private void search() {
        if (searchField.getValue() != null && searchField.getValue().length() > 0) {
            hc = service.searchInCatalogs(searchField.getValue(), hc);
        }
    }

    public void setWidth() {
        resultTable.setWidth(this.getUI().getWidth() - 5f, this.getUI().getWidthUnits());
    }

    private class AddGridTranslationClickListener implements ClickListener {

        private final Object entity;
        private final VerticalLayout vl;
        private final RefreshableGrid grid;

        public AddGridTranslationClickListener(Object entity_, VerticalLayout vl_, RefreshableGrid grid_) {
            this.entity = entity_;
            this.vl = vl_;
            this.grid = grid_;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
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
            vl.addComponent(new GridTranslationCell(tt, grid, entity));
            event.getButton().setVisible(false);
        }

    }

    private class TranslatePropertyGenerator extends PropertyValueGenerator<Component> {

        private final RefreshableGrid grid;

        public TranslatePropertyGenerator(RefreshableGrid grid) {
            this.grid = grid;
        }

        @Override
        public Component getValue(Item item, Object itemId, Object propertyId) {
            Panel panel = new Panel();
            panel.setHeight(98f, Unit.PIXELS);
            panel.setWidth(460f, Unit.PIXELS);
            panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
            final VerticalLayout tabSheet = new VerticalLayout();

            Set<TranslatedText> list = (Set<TranslatedText>) item.getItemProperty("translatedTexts").getValue();
            List<SysAccount> accounts = new ArrayList<>();
            if (list != null) {
                for (TranslatedText tt : list) {
                    GridTranslationCell cell = new GridTranslationCell(tt, grid, itemId);
                    tabSheet.addComponent(cell);
                    accounts.add(tt.getAuthor());
                }
            }
            if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
                Button addTranslation = new Button();
                addTranslation.setIcon(FontAwesome.PLUS_SQUARE);
                addTranslation.setDescription("Добавить перевод");
                addTranslation.addClickListener(new AddGridTranslationClickListener(itemId, tabSheet, grid));
                if (list != null && !list.isEmpty()) {
                    GridTranslationCell component = (GridTranslationCell) tabSheet.getComponent(tabSheet.getComponentCount() - 1);
                    component.getActionLayout().addComponent(addTranslation);
                } else {
                    tabSheet.addComponent(addTranslation);
                }

            }
            panel.setContent(tabSheet);
            return panel;
        }

        @Override
        public Class<Component> getType() {
            return Component.class;
        }

    }

    private class LinkedItemColumnGenerator implements Table.ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            EntityItem item = (EntityItem) source.getContainerDataSource().getItem(itemId);
            GSpreadSheetEntity entity = (GSpreadSheetEntity) item.getEntity();
            GSpreadSheetLinkRouter.RouteEntry route = GSpreadSheetLinkRouter.getRoute(entity.getaId());
            if (route != null) {
                Button b = new Button();
                b.setIcon(FontAwesome.LINK);
                b.setDescription("Перейти к связанной записи");
                b.setData(entity);
                b.addClickListener(linkedItemClickListener);
                return b;
            }
            return null;
        }

    }

    private class InfoPropertyGenerator extends PropertyValueGenerator<Component> {

        public InfoPropertyGenerator() {
        }

        @Override
        public Component getValue(Item item, Object itemId, Object propertyId) {
            VerticalLayout result = new VerticalLayout();
            result.setSizeFull();

            TranslatedEntity entity = (TranslatedEntity) itemId;
            StringBuilder sb = new StringBuilder("");
            if (itemId instanceof GSpreadSheetEntity) {
                sb.append(((GSpreadSheetEntity) itemId).getWeight().toString());
                sb.append("\n");
            }
            if (entity.getTranslator() != null) {
                sb.append(entity.getTranslator());
            }
            if (entity.getChangeTime() != null) {
                sb.append("\n");
                sb.append(sdf.format(entity.getChangeTime()));
            }
            Label l = new Label(sb.toString());
            l.addStyleName("v-label");
            result.addComponent(l);
            HorizontalLayout actions = new HorizontalLayout();
            if (itemId instanceof GSpreadSheetEntity) {
                GSpreadSheetEntity e = (GSpreadSheetEntity) itemId;
                GSpreadSheetLinkRouter.RouteEntry route = GSpreadSheetLinkRouter.getRoute(e.getaId());
                if (route != null) {
                    Button b = new Button();
                    b.setIcon(FontAwesome.LINK);
                    b.setDescription("Перейти к связанной записи");
                    b.setData(e);
                    b.addClickListener(linkedItemClickListener);
                    actions.addComponent(b);
                }
            }
            result.addComponent(actions);
            return result;
        }

        @Override
        public Class<Component> getType() {
            return Component.class;
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

    private void goToItem(DAO entity) {
        Component targetTabId = null;
        RefreshableGrid targetTable = null;
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
        }
        if (rowNum != null) {
            rowNum--;
            if (targetTabId != null) {
                tableTabs.setSelectedTab(targetTabId);
                targetTable.scrollToRow(rowNum, ScrollDestination.START);
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
            TranslatedText tt = (TranslatedText) event.getItemId();
            Component targetTabId = null;
            RefreshableGrid targetTable = null;
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
            }
            if (rowNum != null) {
                rowNum--;
                if (targetTabId != null && targetTable != null) {
                    tableTabs.setSelectedTab(targetTabId);
                    targetTable.scrollToRow(rowNum, ScrollDestination.START);
                }
            }
        }

    }

    private class GridTranslationCell extends HorizontalLayout {

        private TextArea translation;
        private Button save;
        private Button accept;
        private Button reject;
        private final TranslatedText translatedText;
        private VerticalLayout actionLayout;
        private RefreshableGrid grid;
        private Object itemId;

        public GridTranslationCell(TranslatedText translatedText_, RefreshableGrid grid_, Object itemId_) {
            this.setHeight(95f, Unit.PIXELS);
            this.setWidth(460f, Unit.PIXELS);
            this.translatedText = translatedText_;
            this.grid = grid_;
            this.itemId = itemId_;
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
            actionLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
            actionLayout.setWidth(40f, Unit.PIXELS);
            actionLayout.setHeight(80f, Unit.PIXELS);

            translation = new TextArea();
            translation.setDescription(caption.toString());
            translation.setWidth(400f, Unit.PIXELS);
            translation.setHeight(95f, Unit.PIXELS);
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
                        save.setIcon(FontAwesome.RECYCLE);
                        save.setDescription("Удалить");
                    } else {
                        translatedText.setText(event.getText());
                        save.setIcon(FontAwesome.SAVE);
                        save.setDescription("Сохранить");
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
            save = new Button();
            save.setIcon(FontAwesome.SAVE);
            save.setDescription("Сохранить");
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    translatedText.setText(translation.getValue());
                    service.saveTranslatedText(translatedText);
                    grid.Refresh();
                }
            });

            actionLayout.addComponent(save);
            save.setVisible(false);
            if (SpringSecurityHelper.hasRole("ROLE_ADMIN") || SpringSecurityHelper.hasRole("ROLE_APPROVE")) {
                if (translatedText.getId() != null && translatedText.getStatus() == TRANSLATE_STATUS.NEW) {
                    accept = new Button();
                    accept.setIcon(FontAwesome.THUMBS_UP);
                    accept.setDescription("Принять");
                    accept.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            translatedText.setText(translation.getValue());
                            service.acceptTranslatedText(translatedText);
                            grid.Refresh();
                        }
                    });
                    actionLayout.addComponent(accept);
                    reject = new Button();
                    reject.setIcon(FontAwesome.THUMBS_DOWN);
                    reject.setDescription("Отклонить");
                    reject.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            translatedText.setText(translation.getValue());
                            service.rejectTranslatedText(translatedText);
                            grid.Refresh();
                        }
                    });
                    actionLayout.addComponent(reject);

                }

            }
            this.addComponent(actionLayout);
            this.setExpandRatio(translation, 1f);
        }

        public VerticalLayout getActionLayout() {
            return actionLayout;
        }

    }

}
