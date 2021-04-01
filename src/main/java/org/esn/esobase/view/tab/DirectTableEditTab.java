/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.HasValue;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.event.FieldEvents;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.shared.ui.grid.ScrollDestination;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.SearchService;
import org.esn.esobase.data.specification.GSpreadSheetEntitySpecification;
import org.esn.esobase.data.specification.GSpreadSheetItemNameSpecification;
import org.esn.esobase.data.specification.TranslatedTextSpecification;
import org.esn.esobase.model.EsoInterfaceVariable;
import org.esn.esobase.model.GSpreadSheetEntity;
import org.esn.esobase.model.GSpreadSheetsAbilityDescription;
import org.esn.esobase.model.GSpreadSheetsAchievement;
import org.esn.esobase.model.GSpreadSheetsAchievementDescription;
import org.esn.esobase.model.GSpreadSheetsActivator;
import org.esn.esobase.model.GSpreadSheetsCollectible;
import org.esn.esobase.model.GSpreadSheetsCollectibleDescription;
import org.esn.esobase.model.GSpreadSheetsItemDescription;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.GSpreadSheetsJournalEntry;
import org.esn.esobase.model.GSpreadSheetsLoadscreen;
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNote;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.GSpreadSheetsQuestDescription;
import org.esn.esobase.model.GSpreadSheetsQuestDirection;
import org.esn.esobase.model.GSpreadSheetsQuestEndTip;
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.GSpreadSheetsQuestStartTip;
import org.esn.esobase.model.ItemSubType;
import org.esn.esobase.model.ItemType;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.model.lib.DAO;
import org.esn.esobase.security.SpringSecurityHelper;
import org.esn.esobase.tools.GSpreadSheetLinkRouter;
import org.esn.esobase.view.ui.GspreadSheetTable;
import org.esn.esobase.view.ui.RefreshableGrid;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.addons.comboboxmultiselect.ComboBoxMultiselect;
import org.vaadin.viritin.v7.SortableLazyList;
import org.vaadin.viritin.v7.grid.GeneratedPropertyListContainer;
import org.vaadin.viritin.v7.grid.MGrid;

/**
 *
 * @author scraelos
 */
public class DirectTableEditTab extends VerticalLayout {

    private DBService service;
    private SearchService searchService;
    private TextField searchField;
    private ComboBoxMultiselect tableNames;
    private CheckBox withTranslatedNeighbours;
    private CheckBox regularExpression;
    private Slider neighboursCount;
    private Button searchButton;
    private Table resultTable;
    private TabSheet searchTabs;
    private HierarchicalContainer hc = new HierarchicalContainer();
    private TabSheet tableTabs;
    private ComboBox itemType;
    private ComboBox itemSubType;
    private CheckBox notTranslated;

    private List<String> tableNamesList = new ArrayList<>();

    static final int PAGESIZE = 10;

    private GeneratedPropertyListContainer<GSpreadSheetsNpcName> gSpreadSheetsNpcNameContainer;
    private GspreadSheetTable npcNameTable;

    private GeneratedPropertyListContainer<GSpreadSheetsLocationName> locationNameContainer;
    private GspreadSheetTable locationNameTable;

    private GspreadSheetTable activatorTable;
    private GeneratedPropertyListContainer<GSpreadSheetsActivator> activatorContainer;

    private GspreadSheetTable playerPhraseTable;
    private GeneratedPropertyListContainer<GSpreadSheetsPlayerPhrase> playerPhraseContainer;

    private GspreadSheetTable npcPhraseTable;
    private GeneratedPropertyListContainer<GSpreadSheetsNpcPhrase> npcPhraseContainer;

    private GspreadSheetTable questNameTable;
    private GeneratedPropertyListContainer<GSpreadSheetsQuestName> questNameContainer;

    private GspreadSheetTable questDescriptionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsQuestDescription> questDescriptionContainer;

    private GspreadSheetTable questDirectionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsQuestDirection> questDirectionContainer;

    private GspreadSheetTable questStartTipTable;
    private GeneratedPropertyListContainer<GSpreadSheetsQuestStartTip> questStartTipContainer;

    private GspreadSheetTable questEndTipTable;
    private GeneratedPropertyListContainer<GSpreadSheetsQuestEndTip> questEndTipContainer;

    private VerticalLayout itemNameLayout;
    private GspreadSheetTable itemNameTable;
    private GeneratedPropertyListContainer<GSpreadSheetsItemName> itemNameContainer;

    private GspreadSheetTable itemDescriptionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsItemDescription> itemDescriptionContainer;

    private GspreadSheetTable journalEntryTable;
    private GeneratedPropertyListContainer<GSpreadSheetsJournalEntry> journalEntryContainer;

    private GspreadSheetTable achievementTable;
    private GeneratedPropertyListContainer<GSpreadSheetsAchievement> achievementContainer;

    private GspreadSheetTable achievementDescriptionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsAchievementDescription> achievementDescriptionContainer;

    private GspreadSheetTable abilityDescriptionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsAbilityDescription> abilityDescriptionContainer;

    private GspreadSheetTable noteTable;
    private GeneratedPropertyListContainer<GSpreadSheetsNote> noteContainer;

    private GspreadSheetTable collectibleTable;
    private GeneratedPropertyListContainer<GSpreadSheetsCollectible> collectibleContainer;

    private GspreadSheetTable collectibleDescriptionTable;
    private GeneratedPropertyListContainer<GSpreadSheetsCollectibleDescription> collectibleDescriptionContainer;

    private GspreadSheetTable loadscreenTable;
    private GeneratedPropertyListContainer<GSpreadSheetsLoadscreen> loadscreenContainer;

    private LinkedItemClickListener linkedItemClickListener;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private ComboBox statusFilter;
    private ComboBox translatorBox;
    private ComboBox translateTypeBox;
    private Button filterTranslationButton;
    private MGrid newTranslationsTable;
    private HorizontalLayout filterLayout;
    private TranslatedTextSpecification translatedTextSpecification;
    private GeneratedPropertyListContainer<TranslatedText> newTranslationsContainer;
    private BeanItemContainer<SysAccount> sysAccountContainer = new BeanItemContainer<>(SysAccount.class);

    private final GSpreadSheetEntitySpecification specification = new GSpreadSheetEntitySpecification();
    private final GSpreadSheetItemNameSpecification itemNameSpecification = new GSpreadSheetItemNameSpecification();

    public DirectTableEditTab(DBService service_, SearchService searchService_) {
        this.service = service_;
        this.searchService = searchService_;
        this.setSizeFull();
        this.setMargin(false);
        linkedItemClickListener = new LinkedItemClickListener();
        searchTabs = new TabSheet();
        searchTabs.setSizeFull();
        searchTabs.setHeight(250f, Unit.PIXELS);
        VerticalLayout searchInCatalogsLayout = new VerticalLayout();
        searchInCatalogsLayout.setSizeFull();
        searchInCatalogsLayout.setMargin(false);
        HorizontalLayout hl = new HorizontalLayout();
        hl.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        searchField = new TextField();
        searchField.setWidth(500, Unit.PIXELS);
        searchField.addShortcutListener(new ShortcutListener("Search shortcut", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                search();
            }
        });
        hl.addComponent(searchField);
        searchButton = new Button("Поиск");
        searchButton.setIcon(FontAwesome.SEARCH);
        searchButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                search();

            }
        });
        hl.addComponent(searchButton);
        hl.addComponent(new Label(""));

        tableNamesList.add("GSpreadSheetsNpcName");
        tableNamesList.add("GSpreadSheetsNpcPhrase");
        tableNamesList.add("GSpreadSheetsPlayerPhrase");
        tableNamesList.add("GSpreadSheetsAbilityDescription");
        tableNamesList.add("GSpreadSheetsAchievement");
        tableNamesList.add("GSpreadSheetsAchievementDescription");
        tableNamesList.add("GSpreadSheetsActivator");
        tableNamesList.add("GSpreadSheetsCollectible");
        tableNamesList.add("GSpreadSheetsCollectibleDescription");
        tableNamesList.add("GSpreadSheetsItemDescription");
        tableNamesList.add("GSpreadSheetsItemName");
        tableNamesList.add("GSpreadSheetsJournalEntry");
        tableNamesList.add("GSpreadSheetsLoadscreen");
        tableNamesList.add("GSpreadSheetsLocationName");
        tableNamesList.add("GSpreadSheetsNote");
        tableNamesList.add("GSpreadSheetsQuestDescription");
        tableNamesList.add("GSpreadSheetsQuestDirection");
        tableNamesList.add("GSpreadSheetsQuestName");
        tableNamesList.add("GSpreadSheetsQuestStartTip");
        tableNamesList.add("GSpreadSheetsQuestEndTip");
        tableNames = new ComboBoxMultiselect(null);
        tableNames.setSelectAllButtonCaption("Выбрать все");
        tableNames.setShowSelectAllButton(new ComboBoxMultiselect.ShowButton() {
            @Override
            public boolean isShow(String filter, int page) {
                return true;
            }
        });
        tableNames.setClearButtonCaption("Очистить выбор");
        tableNames.setPageLength(0);
        tableNames.addItems(tableNamesList);
        tableNames.selectAll();
        tableNames.setItemCaption("GSpreadSheetsAbilityDescription", "Описания способностей");
        tableNames.setItemCaption("GSpreadSheetsAchievement", "Достижения");
        tableNames.setItemCaption("GSpreadSheetsAchievementDescription", "Описания достижений");
        tableNames.setItemCaption("GSpreadSheetsActivator", "Активаторы");
        tableNames.setItemCaption("GSpreadSheetsCollectible", "Коллекционные предметы");
        tableNames.setItemCaption("GSpreadSheetsCollectibleDescription", "Описания коллекционных предметов");
        tableNames.setItemCaption("GSpreadSheetsItemDescription", "Описания предметов");
        tableNames.setItemCaption("GSpreadSheetsItemName", "Названия предметов");
        tableNames.setItemCaption("GSpreadSheetsJournalEntry", "Записи журнала");
        tableNames.setItemCaption("GSpreadSheetsLoadscreen", "Загрузочные экраны");
        tableNames.setItemCaption("GSpreadSheetsLocationName", "Локации");
        tableNames.setItemCaption("GSpreadSheetsNote", "Письма");
        tableNames.setItemCaption("GSpreadSheetsNpcName", "NPC");
        tableNames.setItemCaption("GSpreadSheetsNpcPhrase", "Фразы NPC");
        tableNames.setItemCaption("GSpreadSheetsPlayerPhrase", "Фразы игрока");
        tableNames.setItemCaption("GSpreadSheetsQuestDescription", "Описания квестов");
        tableNames.setItemCaption("GSpreadSheetsQuestDirection", "Цели квестов");
        tableNames.setItemCaption("GSpreadSheetsQuestEndTip", "Завершённые цепочки");
        tableNames.setItemCaption("GSpreadSheetsQuestName", "Названия квестов");
        tableNames.setItemCaption("GSpreadSheetsQuestStartTip", "Начатые цепочки");
        hl.addComponent(tableNames);
        regularExpression = new CheckBox("regexp", false);
        hl.addComponent(regularExpression);
        withTranslatedNeighbours = new CheckBox("Непереведённые с переведёнными соседями", false);
        hl.addComponent(withTranslatedNeighbours);
        neighboursCount = new Slider(null, 1, 200);
        neighboursCount.setValue(20d);
        hl.addComponent(neighboursCount);
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
        translationsLayout.setMargin(false);
        translationsLayout.setSpacing(false);
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
        translateTypeBox.addItem("spreadSheetsQuestStartTip");
        translateTypeBox.setItemCaption("spreadSheetsQuestStartTip", "Начатые цепочки");
        translateTypeBox.addItem("spreadSheetsQuestEndTip");
        translateTypeBox.setItemCaption("spreadSheetsQuestEndTip", "Завершённые цепочки");
        translateTypeBox.addItem("sheetsCollectible");
        translateTypeBox.setItemCaption("sheetsCollectible", "Названия коллекционных предметов");
        translateTypeBox.addItem("sheetsCollectibleDescription");
        translateTypeBox.setItemCaption("sheetsCollectibleDescription", "Описания коллекционных предметов");
        translateTypeBox.addItem("sheetsLoadscreen");
        translateTypeBox.setItemCaption("sheetsLoadscreen", "Загрузочные экраны");
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
        newTranslationsTable.getColumn("author").setHeaderCaption("Автор").setMaximumWidth(200);
        newTranslationsTable.getColumn("createTime").setHeaderCaption("Дата").setWidth(140);
        newTranslationsTable.getColumn("text").setHeaderCaption("Перевод").setMaximumWidth(800).setMinimumWidth(600);
        newTranslationsTable.addItemClickListener(new TranslationsTableRowClickListener());
        translationsLayout.addComponent(newTranslationsTable);
        translationsLayout.setExpandRatio(newTranslationsTable, 5f);
        searchTabs.addTab(translationsLayout, "Переводы");
        this.addComponent(searchTabs);
        filterLayout = new HorizontalLayout();
        Label itemTypeLabel = new Label("Тип");
        itemType = new ComboBox();
        itemType.addItems(service.getTypes());
        itemType.setNullSelectionAllowed(true);
        itemType.setFilteringMode(FilteringMode.CONTAINS);
        filterLayout.addComponent(itemTypeLabel);
        filterLayout.addComponent(itemType);
        Label itemSubTypeLabel = new Label("Подтип");
        itemSubType = new ComboBox();
        itemSubType.addItems(service.getSubTypes());
        itemSubType.setNullSelectionAllowed(true);
        itemSubType.setFilteringMode(FilteringMode.CONTAINS);
        filterLayout.addComponent(itemSubTypeLabel);
        filterLayout.addComponent(itemSubType);
        notTranslated = new CheckBox("Не переведено");
        notTranslated.setValue(Boolean.FALSE);
        filterLayout.addComponent(notTranslated);
        filterLayout.setComponentAlignment(notTranslated, Alignment.MIDDLE_LEFT);
        this.addComponent(filterLayout);
        tableTabs = new TabSheet();
        tableTabs.setSizeFull();
        gSpreadSheetsNpcNameContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsNpcName.class);
        npcNameTable = new GspreadSheetTable(gSpreadSheetsNpcNameContainer, PAGESIZE, service.getgSpreadSheetsNpcNameRepository(), specification);
        npcNameTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        npcNameTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(npcNameTable));
        tableTabs.addTab(npcNameTable, "NPC");
        npcNameTable.build();
        npcNameTable.setVisibleColumns(new Object[]{"rowNum", "sex", "textEn", "textRu", "infoColumn", "translateColumn"});
        npcNameTable.setColumnWidth("sex", 87);
        npcNameTable.setColumnHeader("sex", "Пол");

        locationNameContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsLocationName.class);
        locationNameTable = new GspreadSheetTable(locationNameContainer, PAGESIZE, service.getgSpreadSheetsLocationNameRepository(), specification);
        locationNameTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        locationNameTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(locationNameTable));
        tableTabs.addTab(locationNameTable, "Локации");
        locationNameTable.build();
        locationNameTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        activatorContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsActivator.class);
        activatorTable = new GspreadSheetTable(activatorContainer, PAGESIZE, service.getgSpreadSheetsActivatorRepository(), specification);
        activatorTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        activatorTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(activatorTable));
        tableTabs.addTab(activatorTable, "Активаторы");
        activatorTable.build();
        activatorTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        playerPhraseContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsPlayerPhrase.class);
        playerPhraseTable = new GspreadSheetTable(playerPhraseContainer, PAGESIZE, service.getgSpreadSheetsPlayerPhraseRepository(), specification);
        playerPhraseTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        playerPhraseTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(playerPhraseTable));
        tableTabs.addTab(playerPhraseTable, "Фразы игрока");
        playerPhraseTable.build();
        playerPhraseTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add(".wrapped-text {\n"
                + "    white-space: normal;\n"
                + "    overflow: hidden;\n"
                + "}");
        npcPhraseContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsNpcPhrase.class);
        npcPhraseTable = new GspreadSheetTable(npcPhraseContainer, PAGESIZE, service.getgSpreadSheetsNpcPhraseRepository(), specification);
        npcPhraseTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        npcPhraseTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(npcPhraseTable));
        tableTabs.addTab(npcPhraseTable, "Фразы NPC");
        npcPhraseTable.build();
        npcPhraseTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        questNameContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsQuestName.class);
        questNameTable = new GspreadSheetTable(questNameContainer, PAGESIZE, service.getgSpreadSheetsQuestNameRepository(), specification);
        questNameTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        questNameTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(questNameTable));
        tableTabs.addTab(questNameTable, "Названия квестов");
        questNameTable.build();
        questNameTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        questDescriptionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsQuestDescription.class);
        questDescriptionTable = new GspreadSheetTable(questDescriptionContainer, PAGESIZE, service.getgSpreadSheetsQuestDescriptionRepository(), specification);
        questDescriptionTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        questDescriptionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(questDescriptionTable));
        tableTabs.addTab(questDescriptionTable, "Описания квестов");
        questDescriptionTable.build();
        questDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        questDirectionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsQuestDirection.class);
        questDirectionTable = new GspreadSheetTable(questDirectionContainer, PAGESIZE, service.getgSpreadSheetsQuestDirectionRepository(), specification);
        questDirectionTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        questDirectionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(questDirectionTable));
        tableTabs.addTab(questDirectionTable, "Цели квестов");
        questDirectionTable.build();
        questDirectionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        questStartTipContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsQuestStartTip.class);
        questStartTipTable = new GspreadSheetTable(questStartTipContainer, PAGESIZE, service.getgSpreadSheetsQuestStartTipRepository(), specification);
        questStartTipTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        questStartTipTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(questStartTipTable));
        tableTabs.addTab(questStartTipTable, "Начатые цепочки");
        questStartTipTable.build();
        questStartTipTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        questEndTipContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsQuestEndTip.class);
        questEndTipTable = new GspreadSheetTable(questEndTipContainer, PAGESIZE, service.getgSpreadSheetsQuestEndTipRepository(), specification);
        questEndTipTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        questEndTipTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(questEndTipTable));
        tableTabs.addTab(questEndTipTable, "Завершённые цепочки");
        questEndTipTable.build();
        questEndTipTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        itemNameLayout = new VerticalLayout();
        itemNameLayout.setMargin(false);
        itemNameLayout.setSpacing(false);
        itemNameLayout.setSizeFull();
        Label itemNameLabel = new Label("ВНИМАНИЕ! В этой таблице НЕЛЬЗЯ:  переводить односложные слова, особенно написанные со строчной буквы.");
        itemNameLabel.setStyleName(ValoTheme.LABEL_COLORED);
        itemNameContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsItemName.class);
        itemNameTable = new GspreadSheetTable(itemNameContainer, PAGESIZE, service.getgSpreadSheetsItemNameRepository(), itemNameSpecification);
        itemNameTable.addGeneratedColumn("itemTypeColumn", new ItemInfoColumnGenerator());
        itemNameTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        itemNameTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(itemNameTable));
        itemNameLayout.addComponent(itemNameLabel);
        itemNameLayout.addComponent(itemNameTable);
        itemNameLayout.setExpandRatio(itemNameTable, 1f);
        tableTabs.addTab(itemNameLayout, "Названия предметов");
        itemNameTable.build();
        itemNameTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "itemTypeColumn", "infoColumn", "translateColumn"});
        itemNameTable.setColumnHeader("itemTypeColumn", "");
        itemNameTable.setColumnExpandRatio("itemTypeColumn", 0.5f);

        itemDescriptionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsItemDescription.class);
        itemDescriptionTable = new GspreadSheetTable(itemDescriptionContainer, PAGESIZE, service.getgSpreadSheetsItemDescriptionRepository(), specification);
        itemDescriptionTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        itemDescriptionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(itemDescriptionTable));
        tableTabs.addTab(itemDescriptionTable, "Описания предметов");
        itemDescriptionTable.build();
        itemDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        journalEntryContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsJournalEntry.class);
        journalEntryTable = new GspreadSheetTable(journalEntryContainer, PAGESIZE, service.getgSpreadSheetsJournalEntryRepository(), specification);
        journalEntryTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        journalEntryTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(journalEntryTable));
        tableTabs.addTab(journalEntryTable, "Записи журнала");
        journalEntryTable.build();
        journalEntryTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        achievementContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsAchievement.class);
        achievementTable = new GspreadSheetTable(achievementContainer, PAGESIZE, service.getgSpreadSheetsAchievementRepository(), specification);
        achievementTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        achievementTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(achievementTable));
        tableTabs.addTab(achievementTable, "Достижения");
        achievementTable.build();
        achievementTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        achievementDescriptionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsAchievementDescription.class);
        achievementDescriptionTable = new GspreadSheetTable(achievementDescriptionContainer, PAGESIZE, service.getgSpreadSheetsAchievementDescriptionRepository(), specification);
        achievementDescriptionTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        achievementDescriptionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(achievementDescriptionTable));
        tableTabs.addTab(achievementDescriptionTable, "Описания достижений");
        achievementDescriptionTable.build();
        achievementDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        abilityDescriptionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsAbilityDescription.class);
        abilityDescriptionTable = new GspreadSheetTable(abilityDescriptionContainer, PAGESIZE, service.getgSpreadSheetsAbilityDescriptionRepository(), specification);
        abilityDescriptionTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        abilityDescriptionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(abilityDescriptionTable));
        tableTabs.addTab(abilityDescriptionTable, "Описания способностей");
        abilityDescriptionTable.build();
        abilityDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        noteContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsNote.class);
        noteTable = new GspreadSheetTable(noteContainer, PAGESIZE, service.getgSpreadSheetsNoteRepository(), specification);
        noteTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        noteTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(noteTable));
        tableTabs.addTab(noteTable, "Письма");
        noteTable.build();
        noteTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        collectibleContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsCollectible.class);
        collectibleTable = new GspreadSheetTable(collectibleContainer, PAGESIZE, service.getgSpreadSheetsCollectibleRepository(), specification);
        collectibleTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        collectibleTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(collectibleTable));
        tableTabs.addTab(collectibleTable, "Коллекционные предметы");
        collectibleTable.build();
        collectibleTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        collectibleDescriptionContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsCollectibleDescription.class);
        collectibleDescriptionTable = new GspreadSheetTable(collectibleDescriptionContainer, PAGESIZE, service.getgSpreadSheetsCollectibleDescriptionRepository(), specification);
        collectibleDescriptionTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        collectibleDescriptionTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(collectibleDescriptionTable));
        tableTabs.addTab(collectibleDescriptionTable, "Описания коллекционных предметов");
        collectibleDescriptionTable.build();
        collectibleDescriptionTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});

        loadscreenContainer = new GeneratedPropertyListContainer<>(GSpreadSheetsLoadscreen.class);
        loadscreenTable = new GspreadSheetTable(loadscreenContainer, PAGESIZE, service.getgSpreadSheetsLoadscreenRepository(), specification);
        loadscreenTable.addGeneratedColumn("infoColumn", new InfoColumnGenerator());
        loadscreenTable.addGeneratedColumn("translateColumn", new TranslateColumnGenerator(loadscreenTable));
        tableTabs.addTab(loadscreenTable, "Загрузочные экраны");
        loadscreenTable.build();
        loadscreenTable.setVisibleColumns(new Object[]{"rowNum", "textEn", "textRu", "infoColumn", "translateColumn"});
        LoadTables();
        itemType.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LoadTables();
            }
        });
        itemSubType.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LoadTables();
            }
        });
        notTranslated.addValueChangeListener(new HasValue.ValueChangeListener<Boolean>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<Boolean> event) {
                LoadTables();
            }
        });

        this.addComponent(tableTabs);
        this.setExpandRatio(tableTabs, 20f);
    }

    private void LoadTables() {
        if (itemType.getValue() != null) {
            itemNameSpecification.setItemType((ItemType) itemType.getValue());
        } else {
            itemNameSpecification.setItemType(null);
        }
        if (itemSubType.getValue() != null) {
            itemNameSpecification.setItemSubType((ItemSubType) itemSubType.getValue());
        } else {
            itemNameSpecification.setItemSubType(null);
        }
        specification.setNotTranslated(notTranslated.getValue());
        itemNameSpecification.setNotTranslated(notTranslated.getValue());
        npcNameTable.Load();
        locationNameTable.Load();
        activatorTable.Load();
        playerPhraseTable.Load();
        npcPhraseTable.Load();
        questNameTable.Load();
        questDescriptionTable.Load();
        questDirectionTable.Load();
        questStartTipTable.Load();
        questEndTipTable.Load();
        itemNameTable.Load();
        itemDescriptionTable.Load();
        journalEntryTable.Load();
        achievementTable.Load();
        achievementDescriptionTable.Load();
        abilityDescriptionTable.Load();
        noteTable.Load();
        collectibleTable.Load();
        collectibleDescriptionTable.Load();
        loadscreenTable.Load();
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
        SortableLazyList lazyList = new SortableLazyList<>((int firstRow, boolean sortAscending, String property) -> service.getTranslatedTextRepository().findAll(translatedTextSpecification, PageRequest.of(
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
        hc.removeAllItems();
        if ((searchField.getValue() != null && searchField.getValue().length() > 0) || withTranslatedNeighbours.getValue()) {
            List<String> selectedNames = new ArrayList<>();
            Set<String> selectedValues = (Set<String>) tableNames.getValue();
            for (String t : tableNamesList) {
                if (selectedValues.contains(t)) {
                    selectedNames.add(t);
                }
            }
            List<GSpreadSheetEntity> searchInCatalogs = searchService.searchInCatalogs(selectedNames, searchField.getValue(), withTranslatedNeighbours.getValue(), neighboursCount.getValue().intValue(), regularExpression.getValue());
            for (GSpreadSheetEntity e : searchInCatalogs) {
                Item item = hc.addItem(e);
                item.getItemProperty("textEn").setValue(e.getTextEn());
                if (e instanceof GSpreadSheetsNpcName) {
                    GSpreadSheetsNpcName npc = (GSpreadSheetsNpcName) e;
                    if (npc.getSex() != null) {
                        item.getItemProperty("textEn").setValue(npc.getTextEn() + "(" + npc.getSex().toString().substring(0, 1) + ")");
                    }
                }
                item.getItemProperty("textRu").setValue(e.getTextRu());
                item.getItemProperty("weight").setValue(e.getWeight());
                item.getItemProperty("translator").setValue(e.getTranslator());
                item.getItemProperty("catalogType").setValue(tableNames.getItemCaption(e.getClass().getSimpleName()));
            }
            resultTable.setCaption("Результаты поиска (" + Integer.toString(searchInCatalogs.size()) + ")");
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
            tt.setText("");
            tt.setAuthor(SpringSecurityHelper.getSysAccount());
            if (entity instanceof GSpreadSheetsActivator) {
                tt.setSpreadSheetsActivator((GSpreadSheetsActivator) entity);
                tt.getSpreadSheetsActivator().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsAchievement) {
                tt.setSpreadSheetsAchievement((GSpreadSheetsAchievement) entity);
                tt.getSpreadSheetsAchievement().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsAchievementDescription) {
                tt.setSpreadSheetsAchievementDescription((GSpreadSheetsAchievementDescription) entity);
                tt.getSpreadSheetsAchievementDescription().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsNote) {
                tt.setSpreadSheetsNote((GSpreadSheetsNote) entity);
                tt.getSpreadSheetsNote().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsItemDescription) {
                tt.setSpreadSheetsItemDescription((GSpreadSheetsItemDescription) entity);
                tt.getSpreadSheetsItemDescription().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsItemName) {
                tt.setSpreadSheetsItemName((GSpreadSheetsItemName) entity);
                tt.getSpreadSheetsItemName().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsJournalEntry) {
                tt.setSpreadSheetsJournalEntry((GSpreadSheetsJournalEntry) entity);
                tt.getSpreadSheetsJournalEntry().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsLocationName) {
                tt.setSpreadSheetsLocationName((GSpreadSheetsLocationName) entity);
                tt.getSpreadSheetsLocationName().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsNpcName) {
                tt.setSpreadSheetsNpcName((GSpreadSheetsNpcName) entity);
                tt.getSpreadSheetsNpcName().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsNpcPhrase) {
                tt.setSpreadSheetsNpcPhrase((GSpreadSheetsNpcPhrase) entity);
                tt.getSpreadSheetsNpcPhrase().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsPlayerPhrase) {
                tt.setSpreadSheetsPlayerPhrase((GSpreadSheetsPlayerPhrase) entity);
                tt.getSpreadSheetsPlayerPhrase().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsQuestDescription) {
                tt.setSpreadSheetsQuestDescription((GSpreadSheetsQuestDescription) entity);
                tt.getSpreadSheetsQuestDescription().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsQuestDirection) {
                tt.setSpreadSheetsQuestDirection((GSpreadSheetsQuestDirection) entity);
                tt.getSpreadSheetsQuestDirection().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsQuestName) {
                tt.setSpreadSheetsQuestName((GSpreadSheetsQuestName) entity);
                tt.getSpreadSheetsQuestName().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsAbilityDescription) {
                tt.setSheetsAbilityDescription((GSpreadSheetsAbilityDescription) entity);
                tt.getSheetsAbilityDescription().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsLoadscreen) {
                tt.setSheetsLoadscreen((GSpreadSheetsLoadscreen) entity);
                tt.getSheetsLoadscreen().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsCollectible) {
                tt.setSheetsCollectible((GSpreadSheetsCollectible) entity);
                tt.getSheetsCollectible().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsCollectibleDescription) {
                tt.setSheetsCollectibleDescription((GSpreadSheetsCollectibleDescription) entity);
                tt.getSheetsCollectibleDescription().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsQuestStartTip) {
                tt.setSpreadSheetsQuestStartTip((GSpreadSheetsQuestStartTip) entity);
                tt.getSpreadSheetsQuestStartTip().getTranslatedTexts().add(tt);
            } else if (entity instanceof GSpreadSheetsQuestEndTip) {
                tt.setSpreadSheetsQuestEndTip((GSpreadSheetsQuestEndTip) entity);
                tt.getSpreadSheetsQuestEndTip().getTranslatedTexts().add(tt);
            } else if (entity instanceof EsoInterfaceVariable) {
                tt.setEsoInterfaceVariable((EsoInterfaceVariable) entity);
                tt.getEsoInterfaceVariable().getTranslatedTexts().add(tt);
            }
            vl.addComponent(new GridTranslationCell(tt, grid, entity));
            event.getButton().setVisible(false);
        }

    }

    private class TranslateColumnGenerator implements Table.ColumnGenerator {

        private final RefreshableGrid grid;

        public TranslateColumnGenerator(RefreshableGrid grid) {
            this.grid = grid;
        }

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            final TranslateLayout result = new TranslateLayout(source, itemId, columnId, grid);
            result.build();
            return result;
        }

    }

    private class TranslateLayout extends VerticalLayout {

        private final Table source;
        private final Object itemId;
        private final Object columnId;
        private final RefreshableGrid grid;

        public TranslateLayout(Table source, Object itemId, Object columnId, RefreshableGrid grid) {
            this.source = source;
            this.itemId = itemId;
            this.columnId = columnId;
            this.grid = grid;
            this.setSpacing(false);
            this.setMargin(false);
        }

        public void build() {
            removeAllComponents();
            Item item = source.getItem(itemId);
            Set<TranslatedText> list = (Set<TranslatedText>) item.getItemProperty("translatedTexts").getValue();
            List<SysAccount> accounts = new ArrayList<>();
            if (list != null) {
                for (TranslatedText tt : list) {
                    addComponent(new GridTranslationCell(tt, grid, itemId));
                    accounts.add(tt.getAuthor());
                }
            }
            if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
                Button addTranslation = new Button();
                addTranslation.setIcon(FontAwesome.PLUS_SQUARE);
                addTranslation.setDescription("Добавить перевод");
                addTranslation.addClickListener(new AddGridTranslationClickListener(itemId, this, grid));
                if (list != null && !list.isEmpty()) {
                    GridTranslationCell component = (GridTranslationCell) this.getComponent(this.getComponentCount() - 1);
                    component.getActionLayout().addComponent(addTranslation);
                } else {
                    addComponent(addTranslation);
                }

            }
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

    private class ItemInfoColumnGenerator implements Table.ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout result = new VerticalLayout();
            result.setMargin(false);
            result.setSpacing(false);
            GSpreadSheetsItemName itemName = (GSpreadSheetsItemName) itemId;
            if (itemName.getIcon() != null) {
                Image image = new Image(null, new ExternalResource("https://elderscrolls.net" + itemName.getIcon().replaceAll(".dds", ".png")));
                result.addComponent(image);
            }
            if (itemName.getItemType() != null) {
                result.addComponent(new Label(itemName.getItemType().getTextRu()));
            }
            if (itemName.getItemSubType() != null && ((itemName.getItemType() != null && !itemName.getItemType().getTextRu().equals(itemName.getItemSubType().getTextRu())) || itemName.getItemType() == null)) {
                result.addComponent(new Label(itemName.getItemSubType().getTextRu()));
            }

            return result;
        }

    }

    private class InfoColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout result = new VerticalLayout();
            result.setMargin(false);
            result.setSpacing(false);
            result.setSizeFull();

            GSpreadSheetEntity entity = (GSpreadSheetEntity) itemId;
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
            actions.setMargin(false);
            actions.setSpacing(false);
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

    }

    private class InfoPropertyGenerator extends PropertyValueGenerator<Component> {

        public InfoPropertyGenerator() {
        }

        @Override
        public Component getValue(Item item, Object itemId, Object propertyId) {
            VerticalLayout result = new VerticalLayout();
            result.setMargin(false);
            result.setSpacing(false);
            result.setSizeFull();

            GSpreadSheetEntity entity = (GSpreadSheetEntity) itemId;
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
            actions.setMargin(false);
            actions.setSpacing(false);
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
        Object itemId = entity;
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
        } else if (entity instanceof GSpreadSheetsAchievement) {
            targetTabId = achievementTable;
            targetTable = achievementTable;
            rowNum = ((GSpreadSheetsAchievement) entity).getRowNum().intValue();
        } else if (entity instanceof GSpreadSheetsAchievementDescription) {
            targetTabId = achievementDescriptionTable;
            targetTable = achievementDescriptionTable;
            rowNum = ((GSpreadSheetsAchievementDescription) entity).getRowNum().intValue();
        } else if (entity instanceof GSpreadSheetsNote) {
            targetTabId = noteTable;
            targetTable = noteTable;
            rowNum = ((GSpreadSheetsNote) entity).getRowNum().intValue();
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
        } else if (entity instanceof GSpreadSheetsAbilityDescription) {
            targetTabId = abilityDescriptionTable;
            targetTable = abilityDescriptionTable;
            rowNum = ((GSpreadSheetsAbilityDescription) entity).getRowNum().intValue();
        } else if (entity instanceof GSpreadSheetsCollectible) {
            targetTabId = collectibleTable;
            targetTable = collectibleTable;
            rowNum = ((GSpreadSheetsCollectible) entity).getRowNum().intValue();
        } else if (entity instanceof GSpreadSheetsCollectibleDescription) {
            targetTabId = collectibleDescriptionTable;
            targetTable = collectibleDescriptionTable;
            rowNum = ((GSpreadSheetsCollectibleDescription) entity).getRowNum().intValue();
        } else if (entity instanceof GSpreadSheetsLoadscreen) {
            targetTabId = loadscreenTable;
            targetTable = loadscreenTable;
            rowNum = ((GSpreadSheetsLoadscreen) entity).getRowNum().intValue();
        } else if (entity instanceof GSpreadSheetsQuestStartTip) {
            targetTabId = questStartTipTable;
            targetTable = questStartTipTable;
            rowNum = ((GSpreadSheetsQuestStartTip) entity).getRowNum().intValue();
        } else if (entity instanceof GSpreadSheetsQuestEndTip) {
            targetTabId = questEndTipTable;
            targetTable = questEndTipTable;
            rowNum = ((GSpreadSheetsQuestEndTip) entity).getRowNum().intValue();
        }
        if (rowNum != null) {
            rowNum = rowNum - 2;
            if (rowNum < 1) {
                rowNum = 1;
            }
            if (targetTabId != null) {
                tableTabs.setSelectedTab(targetTabId);
                if (entity instanceof HibernateProxy) {
                    itemId = ((HibernateProxy) itemId).getHibernateLazyInitializer().getImplementation();
                }
                targetTable.scrollToRow(rowNum, itemId, ScrollDestination.MIDDLE);
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
            DAO itemId = null;
            if (tt.getSpreadSheetsNpcName() != null) {
                itemId = tt.getSpreadSheetsNpcName();
            } else if (tt.getSpreadSheetsLocationName() != null) {
                itemId = tt.getSpreadSheetsLocationName();
            } else if (tt.getSpreadSheetsPlayerPhrase() != null) {
                itemId = tt.getSpreadSheetsPlayerPhrase();
            } else if (tt.getSpreadSheetsNpcPhrase() != null) {
                itemId = tt.getSpreadSheetsNpcPhrase();
            } else if (tt.getSpreadSheetsQuestName() != null) {
                itemId = tt.getSpreadSheetsQuestName();
            } else if (tt.getSpreadSheetsQuestDescription() != null) {
                itemId = tt.getSpreadSheetsQuestDescription();
            } else if (tt.getSpreadSheetsQuestDirection() != null) {
                itemId = tt.getSpreadSheetsQuestDirection();
            } else if (tt.getSpreadSheetsActivator() != null) {
                itemId = tt.getSpreadSheetsActivator();
            } else if (tt.getSpreadSheetsAchievement() != null) {
                itemId = tt.getSpreadSheetsAchievement();
            } else if (tt.getSpreadSheetsAchievementDescription() != null) {
                itemId = tt.getSpreadSheetsAchievementDescription();
            } else if (tt.getSpreadSheetsNote() != null) {
                itemId = tt.getSpreadSheetsNote();
            } else if (tt.getSpreadSheetsJournalEntry() != null) {
                itemId = tt.getSpreadSheetsJournalEntry();
            } else if (tt.getSpreadSheetsItemName() != null) {
                itemId = tt.getSpreadSheetsItemName();
            } else if (tt.getSpreadSheetsItemDescription() != null) {
                itemId = tt.getSpreadSheetsItemDescription();
            } else if (tt.getSheetsAbilityDescription() != null) {
                itemId = tt.getSheetsAbilityDescription();
            } else if (tt.getSheetsCollectible() != null) {
                itemId = tt.getSheetsCollectible();
            } else if (tt.getSheetsCollectibleDescription() != null) {
                itemId = tt.getSheetsCollectibleDescription();
            } else if (tt.getSheetsLoadscreen() != null) {
                itemId = tt.getSheetsLoadscreen();
            } else if (tt.getSpreadSheetsQuestStartTip() != null) {
                itemId = tt.getSpreadSheetsQuestStartTip();
            } else if (tt.getSpreadSheetsQuestEndTip() != null) {
                itemId = tt.getSpreadSheetsQuestStartTip();
            }
            goToItem(itemId);
        }
    }

    private class GridTranslationCell extends HorizontalLayout {

        private TextArea translation;
        private Button save;
        private Button accept;
        private Button preaccept;
        private Button correct;
        private Button reject;
        private Button npc;
        private Button player;
        private boolean isPlayerAllowed;
        private boolean isNpcAllowed;
        private final TranslatedText translatedText;
        private VerticalLayout actionLayout;
        private RefreshableGrid grid;
        private Object itemId;

        public GridTranslationCell(TranslatedText translatedText_, RefreshableGrid grid_, Object itemId_) {
            if (translatedText_.getSpreadSheetsNpcPhrase() != null || translatedText_.getSpreadSheetsPlayerPhrase() != null || translatedText_.getSpreadSheetsJournalEntry() != null || translatedText_.getSpreadSheetsQuestDescription() != null) {
                isPlayerAllowed = true;
            } else {
                isPlayerAllowed = false;
            }
            if (translatedText_.getSpreadSheetsNpcPhrase() != null || translatedText_.getSpreadSheetsPlayerPhrase() != null) {
                isNpcAllowed = true;
            } else {
                isNpcAllowed = false;
            }
            this.setMargin(false);
            this.setSpacing(false);
            this.setSizeFull();
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
            actionLayout.setMargin(false);
            actionLayout.setSpacing(false);
            actionLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
            actionLayout.setWidth(40f, Unit.PIXELS);
            actionLayout.setHeight(100f, Unit.PIXELS);

            translation = new TextArea();
            translation.setDescription(caption.toString());
            translation.setSizeFull();
            translation.setNullRepresentation("");
            translation.setImmediate(true);
            translation.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.TIMEOUT);
            translation.setTextChangeTimeout(1000);
            translation.setValue(translatedText_.getText());
            translation.addTextChangeListener(new FieldEvents.TextChangeListener() {

                @Override
                public void textChange(FieldEvents.TextChangeEvent event) {
                    save.setVisible(true);
                    if (isPlayerAllowed) {
                        player.setVisible(true);
                    }
                    if (isNpcAllowed) {
                        npc.setVisible(true);
                    }
                    if (event.getText() == null || event.getText().isEmpty()) {
                        save.setIcon(FontAwesome.RECYCLE);
                        save.setDescription("Удалить");
                        npc.setVisible(false);
                        player.setVisible(false);
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
            } else {
                translation.setReadOnly(true);
            }
            this.addComponent(translation);
            save = new Button();
            save.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_TINY);
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
            npc = new Button("N{}");
            npc.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_TINY);
            npc.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    translation.setValue(translation.getValue() + "<<npc{/}>>");
                }
            });
            actionLayout.addComponent(npc);
            npc.setVisible(false);
            player = new Button("P{}");
            player.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_TINY);
            player.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    translation.setValue(translation.getValue() + "<<player{/}>>");
                }
            });
            actionLayout.addComponent(player);
            player.setVisible(false);
            if (translatedText.getStatus() == TRANSLATE_STATUS.DIRTY && (SpringSecurityHelper.hasRole("ROLE_APPROVE") || SpringSecurityHelper.hasRole("ROLE_CORRECTOR") || SpringSecurityHelper.hasRole("ROLE_PREAPPROVE") || translatedText.getAuthor().equals(SpringSecurityHelper.getSysAccount()))) {
                save.setVisible(true);
                if (isPlayerAllowed) {
                    player.setVisible(true);
                }
                if (isNpcAllowed) {
                    npc.setVisible(true);
                }
            }
            if (translatedText.getStatus() == null && isPlayerAllowed) {
                player.setVisible(true);
            }
            if (translatedText.getStatus() == null && isNpcAllowed) {
                npc.setVisible(true);
            }
            if (translatedText.getId() != null && ((translatedText.getStatus() == TRANSLATE_STATUS.NEW) || (translatedText.getStatus() == TRANSLATE_STATUS.EDITED)) && SpringSecurityHelper.hasRole("ROLE_PREAPPROVE")) {
                translation.setReadOnly(false);
                preaccept = new Button();
                preaccept.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_TINY);
                preaccept.setIcon(FontAwesome.CHECK);
                preaccept.setDescription("Перевод верен");
                preaccept.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.preAcceptTranslatedText(translatedText);
                        grid.Refresh();
                    }
                });
                actionLayout.addComponent(preaccept);
            }
            if (translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.NEW || translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED || translatedText.getStatus() == TRANSLATE_STATUS.EDITED) && SpringSecurityHelper.hasRole("ROLE_CORRECTOR")) {
                translation.setReadOnly(false);
                correct = new Button();
                correct.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_TINY);
                correct.setIcon(FontAwesome.PENCIL);
                correct.setDescription("Текст корректен");
                correct.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.correctTranslatedText(translatedText);
                        grid.Refresh();
                    }
                });
                actionLayout.addComponent(correct);
            }
            if (translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.NEW || translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED || translatedText.getStatus() == TRANSLATE_STATUS.EDITED) && SpringSecurityHelper.hasRole("ROLE_APPROVE")) {
                translation.setReadOnly(false);
                accept = new Button();
                accept.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_TINY);
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
            }
            if (translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.NEW || translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED || translatedText.getStatus() == TRANSLATE_STATUS.EDITED) && (SpringSecurityHelper.hasRole("ROLE_APPROVE") || SpringSecurityHelper.hasRole("ROLE_PREAPPROVE") || SpringSecurityHelper.hasRole("ROLE_CORRECTOR"))) {
                reject = new Button();
                reject.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_TINY);
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
            if (SpringSecurityHelper.hasRole("ROLE_APPROVE") && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.ACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.REJECTED || translatedText.getStatus() == TRANSLATE_STATUS.REVOKED)) {
                translation.setReadOnly(false);
            }
            this.addComponent(actionLayout);
            this.setExpandRatio(translation, 1f);
        }

        public VerticalLayout getActionLayout() {
            return actionLayout;
        }

    }

}
