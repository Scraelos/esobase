/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.GoogleDocsService;
import org.esn.esobase.data.LocationsDiff;
import org.esn.esobase.data.NpcNameDiff;
import org.esn.esobase.data.NpcPhraseDiff;
import org.esn.esobase.data.PlayerPhraseDiff;
import org.esn.esobase.data.QuestDescriptionsDiff;
import org.esn.esobase.data.QuestNamesDiff;
import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.GSpreadSheetsQuestDescription;
import org.esn.esobase.model.GSpreadSheetsQuestName;

/**
 *
 * @author scraelos
 */
public class SynchronizationTab extends VerticalLayout {

    private TabSheet tabs;
    private VerticalLayout playerLayout;
    private HorizontalLayout syncPlayerPhrasesActions;
    private Button syncPlayerPhrasesButton;
    private Button savePlayerPhrasesButton;
    private Table playerPhraseDiffTable;
    private HierarchicalContainer playerPhraseDiffContainer;
    private VerticalLayout npcLayout;
    private HorizontalLayout syncNpcPhrasesActions;
    private Button syncNpcPhrasesButton;
    private Button saveNpcPhrasesButton;
    private Table npcPhraseDiffTable;
    private HierarchicalContainer npcPhraseDiffContainer;
    private VerticalLayout npcNamesLayout;
    private HorizontalLayout syncNpcNamesActions;
    private Button syncNpcNamesButton;
    private Button saveNpcNamesButton;
    private Table npcNamesDiffTable;
    private HierarchicalContainer npcNamesDiffContainer;
    
    private VerticalLayout locationsLayout;
    private HorizontalLayout syncLocationsActions;
    private Button syncLocationsButton;
    private Button saveLocationsButton;
    private Table locationsDiffTable;
    private HierarchicalContainer locationsDiffContainer;
    
    private VerticalLayout questNamesLayout;
    private HorizontalLayout syncQuestNamesActions;
    private Button syncQuestNamesButton;
    private Button saveQuestNamesButton;
    private Table questNamesDiffTable;
    private HierarchicalContainer questNamesDiffContainer;
    
    private VerticalLayout questDescriptionsLayout;
    private HorizontalLayout syncQuestDescriptionsActions;
    private Button syncQuestDescriptionsButton;
    private Button saveQuestDescriptionsButton;
    private Table questDescriptionsDiffTable;
    private HierarchicalContainer questDescriptionsDiffContainer;
    
    private static final String[] columnHeaders = {"Перевод в таблицах", "Переводчик в таблицах", "Дата в таблицах", "Перевод в базе", "Переводчик в базе", "Дата в базе", "Действие"};
    private static final Object[] columns = {"shText", "shNic", "shDate", "dbText", "dbNic", "dbDate", "syncType"};

    private final DBService service;

    public SynchronizationTab(DBService service_) {
        this.setSizeFull();
        this.service = service_;
        TextColumnGenerator textColumnGenerator = new TextColumnGenerator();
        tabs = new TabSheet();
        playerLayout = new VerticalLayout();
        syncPlayerPhrasesActions = new HorizontalLayout();
        syncPlayerPhrasesButton = new Button("Сверка");
        syncPlayerPhrasesButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                GoogleDocsService docsService = new GoogleDocsService();
                List<GSpreadSheetsPlayerPhrase> playerPhrases = docsService.getPlayerPhrases();
                playerPhraseDiffContainer = service.getPlayerPhrasesDiff(playerPhrases, playerPhraseDiffContainer);
            }
        });
        syncPlayerPhrasesActions.addComponent(syncPlayerPhrasesButton);
        savePlayerPhrasesButton = new Button("Синхронизировать");
        savePlayerPhrasesButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                List<PlayerPhraseDiff> diffs = (List<PlayerPhraseDiff>) playerPhraseDiffContainer.getItemIds();
                List<GSpreadSheetsPlayerPhrase> phrasesToSh = new ArrayList<>();
                List<GSpreadSheetsPlayerPhrase> phrasesToDb = new ArrayList<>();
                for (PlayerPhraseDiff diff : diffs) {
                    if (diff.getSyncType() == SYNC_TYPE.TO_SPREADSHEET) {
                        phrasesToSh.add(diff.getDbPhrase());
                    } else if (diff.getSyncType() == SYNC_TYPE.TO_DB) {
                        phrasesToDb.add(diff.getSpreadsheetsPhrase());
                    }
                }
                GoogleDocsService docsService = new GoogleDocsService();
                docsService.uploadPlayerPhrases(phrasesToSh);
                service.savePlayerPhrases(phrasesToDb);
                playerPhraseDiffContainer.removeAllItems();
            }
        });
        syncPlayerPhrasesActions.addComponent(savePlayerPhrasesButton);
        playerLayout.addComponent(syncPlayerPhrasesActions);
        playerPhraseDiffTable = new Table();
        playerPhraseDiffTable.setSizeFull();
        playerPhraseDiffContainer = new HierarchicalContainer();
        playerPhraseDiffContainer.addContainerProperty("shText", String.class, null);
        playerPhraseDiffContainer.addContainerProperty("shNic", String.class, null);
        playerPhraseDiffContainer.addContainerProperty("shDate", Date.class, null);
        playerPhraseDiffContainer.addContainerProperty("dbText", String.class, null);
        playerPhraseDiffContainer.addContainerProperty("dbNic", String.class, null);
        playerPhraseDiffContainer.addContainerProperty("dbDate", Date.class, null);
        playerPhraseDiffContainer.addContainerProperty("syncType", String.class, null);
        playerPhraseDiffTable.setContainerDataSource(playerPhraseDiffContainer);
        playerPhraseDiffTable.removeGeneratedColumn("shText");
        playerPhraseDiffTable.addGeneratedColumn("shText", textColumnGenerator);
        playerPhraseDiffTable.removeGeneratedColumn("dbText");
        playerPhraseDiffTable.addGeneratedColumn("dbText", textColumnGenerator);
        playerPhraseDiffTable.setVisibleColumns(columns);
        playerPhraseDiffTable.setColumnHeaders(columnHeaders);
        playerLayout.addComponent(playerPhraseDiffTable);
        tabs.addTab(playerLayout, "Фразы игрока");
        npcLayout = new VerticalLayout();
        syncNpcPhrasesActions = new HorizontalLayout();
        syncNpcPhrasesButton = new Button("Сверка");
        syncNpcPhrasesButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                GoogleDocsService docsService = new GoogleDocsService();
                List<GSpreadSheetsNpcPhrase> npcPhrases = docsService.getNpcPhrases();
                npcPhraseDiffContainer = service.getNpcPhrasesDiff(npcPhrases, npcPhraseDiffContainer);
            }
        });
        syncNpcPhrasesActions.addComponent(syncNpcPhrasesButton);
        saveNpcPhrasesButton = new Button("Синхронизировать");
        saveNpcPhrasesButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                List<NpcPhraseDiff> diffs = (List<NpcPhraseDiff>) npcPhraseDiffContainer.getItemIds();
                List<GSpreadSheetsNpcPhrase> phrasesToSh = new ArrayList<>();
                List<GSpreadSheetsNpcPhrase> phrasesToDb = new ArrayList<>();
                for (NpcPhraseDiff diff : diffs) {
                    if (diff.getSyncType() == SYNC_TYPE.TO_SPREADSHEET) {
                        phrasesToSh.add(diff.getDbPhrase());
                    } else if (diff.getSyncType() == SYNC_TYPE.TO_DB) {
                        phrasesToDb.add(diff.getSpreadsheetsPhrase());
                    }
                }
                GoogleDocsService docsService = new GoogleDocsService();
                docsService.uploadNpcPhrases(phrasesToSh);
                service.saveNpcPhrases(phrasesToDb);
                npcPhraseDiffContainer.removeAllItems();
            }
        });
        syncNpcPhrasesActions.addComponent(saveNpcPhrasesButton);
        npcLayout.addComponent(syncNpcPhrasesActions);
        npcPhraseDiffTable = new Table();
        npcPhraseDiffTable.setSizeFull();
        npcPhraseDiffContainer = new HierarchicalContainer();
        npcPhraseDiffContainer.addContainerProperty("shText", String.class, null);
        npcPhraseDiffContainer.addContainerProperty("shNic", String.class, null);
        npcPhraseDiffContainer.addContainerProperty("shDate", Date.class, null);
        npcPhraseDiffContainer.addContainerProperty("dbText", String.class, null);
        npcPhraseDiffContainer.addContainerProperty("dbNic", String.class, null);
        npcPhraseDiffContainer.addContainerProperty("dbDate", Date.class, null);
        npcPhraseDiffContainer.addContainerProperty("syncType", String.class, null);
        npcPhraseDiffTable.setContainerDataSource(npcPhraseDiffContainer);
        npcPhraseDiffTable.removeGeneratedColumn("shText");
        npcPhraseDiffTable.addGeneratedColumn("shText", textColumnGenerator);
        npcPhraseDiffTable.removeGeneratedColumn("dbText");
        npcPhraseDiffTable.addGeneratedColumn("dbText", textColumnGenerator);
        npcPhraseDiffTable.setVisibleColumns(columns);
        npcPhraseDiffTable.setColumnHeaders(columnHeaders);
        npcLayout.addComponent(npcPhraseDiffTable);
        tabs.addTab(npcLayout, "Фразы персонажей");
        
        npcNamesLayout = new VerticalLayout();
        syncNpcNamesActions = new HorizontalLayout();
        syncNpcNamesButton = new Button("Сверка");
        syncNpcNamesButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                GoogleDocsService docsService = new GoogleDocsService();
                List<GSpreadSheetsNpcName> npcNames = docsService.getNpcNames();
                npcNamesDiffContainer = service.getNpcnamessDiff(npcNames, npcNamesDiffContainer);
            }
        });
        syncNpcNamesActions.addComponent(syncNpcNamesButton);
        saveNpcNamesButton = new Button("Синхронизировать");
        saveNpcNamesButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                List<NpcNameDiff> diffs = (List<NpcNameDiff>) npcNamesDiffContainer.getItemIds();
                List<GSpreadSheetsNpcName> namesToSh = new ArrayList<>();
                List<GSpreadSheetsNpcName> namesToDb = new ArrayList<>();
                for (NpcNameDiff diff : diffs) {
                    if (diff.getSyncType() == SYNC_TYPE.TO_SPREADSHEET) {
                        namesToSh.add(diff.getDbName());
                    } else if (diff.getSyncType() == SYNC_TYPE.TO_DB) {
                        namesToDb.add(diff.getSpreadsheetsName());
                    }
                }
                GoogleDocsService docsService = new GoogleDocsService();
                docsService.uploadNpcNames(namesToSh);
                service.saveNpcnames(namesToDb);
                npcNamesDiffContainer.removeAllItems();
            }
        });
        syncNpcNamesActions.addComponent(saveNpcNamesButton);
        npcNamesLayout.addComponent(syncNpcNamesActions);
        npcNamesDiffTable = new Table();
        npcNamesDiffTable.setSizeFull();
        npcNamesDiffContainer = new HierarchicalContainer();
        npcNamesDiffContainer.addContainerProperty("shText", String.class, null);
        npcNamesDiffContainer.addContainerProperty("shNic", String.class, null);
        npcNamesDiffContainer.addContainerProperty("shDate", Date.class, null);
        npcNamesDiffContainer.addContainerProperty("dbText", String.class, null);
        npcNamesDiffContainer.addContainerProperty("dbNic", String.class, null);
        npcNamesDiffContainer.addContainerProperty("dbDate", Date.class, null);
        npcNamesDiffContainer.addContainerProperty("syncType", String.class, null);
        npcNamesDiffTable.setContainerDataSource(npcNamesDiffContainer);
        npcNamesDiffTable.removeGeneratedColumn("shText");
        npcNamesDiffTable.addGeneratedColumn("shText", textColumnGenerator);
        npcNamesDiffTable.removeGeneratedColumn("dbText");
        npcNamesDiffTable.addGeneratedColumn("dbText", textColumnGenerator);
        npcNamesDiffTable.setVisibleColumns(columns);
        npcNamesDiffTable.setColumnHeaders(columnHeaders);
        npcNamesLayout.addComponent(npcNamesDiffTable);
        tabs.addTab(npcNamesLayout, "Имена персонажей");
        
        locationsLayout = new VerticalLayout();
        syncLocationsActions = new HorizontalLayout();
        syncLocationsButton = new Button("Сверка");
        syncLocationsButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                GoogleDocsService docsService = new GoogleDocsService();
                List<GSpreadSheetsLocationName> locationNames = docsService.getLocationsNames();
                locationsDiffContainer = service.getLocationNamesDiff(locationNames, locationsDiffContainer);
            }
        });
        syncLocationsActions.addComponent(syncLocationsButton);
        saveLocationsButton = new Button("Синхронизировать");
        saveLocationsButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                List<LocationsDiff> diffs = (List<LocationsDiff>) locationsDiffContainer.getItemIds();
                List<GSpreadSheetsLocationName> namesToSh = new ArrayList<>();
                List<GSpreadSheetsLocationName> namesToDb = new ArrayList<>();
                for (LocationsDiff diff : diffs) {
                    if (diff.getSyncType() == SYNC_TYPE.TO_SPREADSHEET) {
                        namesToSh.add(diff.getDbName());
                    } else if (diff.getSyncType() == SYNC_TYPE.TO_DB) {
                        namesToDb.add(diff.getSpreadsheetsName());
                    }
                }
                GoogleDocsService docsService = new GoogleDocsService();
                docsService.uploadLocationNames(namesToSh);
                service.saveLocationNames(namesToDb);
                locationsDiffContainer.removeAllItems();
            }
        });
        syncLocationsActions.addComponent(saveLocationsButton);
        locationsLayout.addComponent(syncLocationsActions);
        locationsDiffTable = new Table();
        locationsDiffTable.setSizeFull();
        locationsDiffContainer = new HierarchicalContainer();
        locationsDiffContainer.addContainerProperty("shText", String.class, null);
        locationsDiffContainer.addContainerProperty("shNic", String.class, null);
        locationsDiffContainer.addContainerProperty("shDate", Date.class, null);
        locationsDiffContainer.addContainerProperty("dbText", String.class, null);
        locationsDiffContainer.addContainerProperty("dbNic", String.class, null);
        locationsDiffContainer.addContainerProperty("dbDate", Date.class, null);
        locationsDiffContainer.addContainerProperty("syncType", String.class, null);
        locationsDiffTable.setContainerDataSource(locationsDiffContainer);
        locationsDiffTable.removeGeneratedColumn("shText");
        locationsDiffTable.addGeneratedColumn("shText", textColumnGenerator);
        locationsDiffTable.removeGeneratedColumn("dbText");
        locationsDiffTable.addGeneratedColumn("dbText", textColumnGenerator);
        locationsDiffTable.setVisibleColumns(columns);
        locationsDiffTable.setColumnHeaders(columnHeaders);
        locationsLayout.addComponent(locationsDiffTable);
        tabs.addTab(locationsLayout, "Названия локаций");
        
        questNamesLayout = new VerticalLayout();
        syncQuestNamesActions = new HorizontalLayout();
        syncQuestNamesButton = new Button("Сверка");
        syncQuestNamesButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                GoogleDocsService docsService = new GoogleDocsService();
                List<GSpreadSheetsQuestName> items = docsService.getQuestNames();
                questNamesDiffContainer = service.getQuestNamesDiff(items, questNamesDiffContainer);
            }
        });
        syncQuestNamesActions.addComponent(syncQuestNamesButton);
        saveQuestNamesButton = new Button("Синхронизировать");
        saveQuestNamesButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                List<QuestNamesDiff> diffs = (List<QuestNamesDiff>) questNamesDiffContainer.getItemIds();
                List<GSpreadSheetsQuestName> itemsToSh = new ArrayList<>();
                List<GSpreadSheetsQuestName> itemsToDb = new ArrayList<>();
                for (QuestNamesDiff diff : diffs) {
                    if (diff.getSyncType() == SYNC_TYPE.TO_SPREADSHEET) {
                        itemsToSh.add(diff.getDbName());
                    } else if (diff.getSyncType() == SYNC_TYPE.TO_DB) {
                        itemsToDb.add(diff.getSpreadsheetsName());
                    }
                }
                GoogleDocsService docsService = new GoogleDocsService();
                docsService.uploadQuestNames(itemsToSh);
                service.saveQuestNames(itemsToDb);
                questNamesDiffContainer.removeAllItems();
            }
        });
        syncQuestNamesActions.addComponent(saveQuestNamesButton);
        questNamesLayout.addComponent(syncQuestNamesActions);
        questNamesDiffTable = new Table();
        questNamesDiffTable.setSizeFull();
        questNamesDiffContainer = new HierarchicalContainer();
        questNamesDiffContainer.addContainerProperty("shText", String.class, null);
        questNamesDiffContainer.addContainerProperty("shNic", String.class, null);
        questNamesDiffContainer.addContainerProperty("shDate", Date.class, null);
        questNamesDiffContainer.addContainerProperty("dbText", String.class, null);
        questNamesDiffContainer.addContainerProperty("dbNic", String.class, null);
        questNamesDiffContainer.addContainerProperty("dbDate", Date.class, null);
        questNamesDiffContainer.addContainerProperty("syncType", String.class, null);
        questNamesDiffTable.setContainerDataSource(questNamesDiffContainer);
        questNamesDiffTable.removeGeneratedColumn("shText");
        questNamesDiffTable.addGeneratedColumn("shText", textColumnGenerator);
        questNamesDiffTable.removeGeneratedColumn("dbText");
        questNamesDiffTable.addGeneratedColumn("dbText", textColumnGenerator);
        questNamesDiffTable.setVisibleColumns(columns);
        questNamesDiffTable.setColumnHeaders(columnHeaders);
        questNamesLayout.addComponent(questNamesDiffTable);
        tabs.addTab(questNamesLayout, "Названия квестов");
        
        questDescriptionsLayout = new VerticalLayout();
        syncQuestDescriptionsActions = new HorizontalLayout();
        syncQuestDescriptionsButton = new Button("Сверка");
        syncQuestDescriptionsButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                GoogleDocsService docsService = new GoogleDocsService();
                List<GSpreadSheetsQuestDescription> items = docsService.getQuestDescriptions();
                questDescriptionsDiffContainer = service.getQuestDescriptionsDiff(items, questDescriptionsDiffContainer);
            }
        });
        syncQuestDescriptionsActions.addComponent(syncQuestDescriptionsButton);
        saveQuestDescriptionsButton = new Button("Синхронизировать");
        saveQuestDescriptionsButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                List<QuestDescriptionsDiff> diffs = (List<QuestDescriptionsDiff>) questDescriptionsDiffContainer.getItemIds();
                List<GSpreadSheetsQuestDescription> itemsToSh = new ArrayList<>();
                List<GSpreadSheetsQuestDescription> itemsToDb = new ArrayList<>();
                for (QuestDescriptionsDiff diff : diffs) {
                    if (diff.getSyncType() == SYNC_TYPE.TO_SPREADSHEET) {
                        itemsToSh.add(diff.getDbName());
                    } else if (diff.getSyncType() == SYNC_TYPE.TO_DB) {
                        itemsToDb.add(diff.getSpreadsheetsName());
                    }
                }
                GoogleDocsService docsService = new GoogleDocsService();
                docsService.uploadQuestDescriptions(itemsToSh);
                service.saveQuestDescriptions(itemsToDb);
                questDescriptionsDiffContainer.removeAllItems();
            }
        });
        syncQuestDescriptionsActions.addComponent(saveQuestDescriptionsButton);
        questDescriptionsLayout.addComponent(syncQuestDescriptionsActions);
        questDescriptionsDiffTable = new Table();
        questDescriptionsDiffTable.setSizeFull();
        questDescriptionsDiffContainer = new HierarchicalContainer();
        questDescriptionsDiffContainer.addContainerProperty("shText", String.class, null);
        questDescriptionsDiffContainer.addContainerProperty("shNic", String.class, null);
        questDescriptionsDiffContainer.addContainerProperty("shDate", Date.class, null);
        questDescriptionsDiffContainer.addContainerProperty("dbText", String.class, null);
        questDescriptionsDiffContainer.addContainerProperty("dbNic", String.class, null);
        questDescriptionsDiffContainer.addContainerProperty("dbDate", Date.class, null);
        questDescriptionsDiffContainer.addContainerProperty("syncType", String.class, null);
        questDescriptionsDiffTable.setContainerDataSource(questDescriptionsDiffContainer);
        questDescriptionsDiffTable.removeGeneratedColumn("shText");
        questDescriptionsDiffTable.addGeneratedColumn("shText", textColumnGenerator);
        questDescriptionsDiffTable.removeGeneratedColumn("dbText");
        questDescriptionsDiffTable.addGeneratedColumn("dbText", textColumnGenerator);
        questDescriptionsDiffTable.setVisibleColumns(columns);
        questDescriptionsDiffTable.setColumnHeaders(columnHeaders);
        questDescriptionsLayout.addComponent(questDescriptionsDiffTable);
        tabs.addTab(questDescriptionsLayout, "Описания квестов");
        
        this.addComponent(tabs);
        
    }

    private class TextColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            TextField tf = new TextField();
            tf.setSizeFull();
            tf.addStyleName(ValoTheme.TEXTAREA_TINY);
            tf.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
            tf.setValue((String) source.getItem(itemId).getItemProperty(columnId).getValue());
            tf.setReadOnly(true);
            return tf;
        }

    }

}
