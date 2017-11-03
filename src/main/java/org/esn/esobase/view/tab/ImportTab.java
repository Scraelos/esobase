/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.GoogleDocsService;
import org.esn.esobase.data.InsertExecutor;
import org.esn.esobase.data.ItemInfoImportService;
import org.esn.esobase.data.TableUpdateService;
import org.esn.esobase.model.EsoInterfaceVariable;
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
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Npc;
import org.esn.esobase.security.SpringSecurityHelper;
import org.esn.esobase.tools.EsnDecoder;
import org.esn.esobase.tools.LuaDecoder;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author scraelos
 */
@Component
@Scope(value = "prototype")
public class ImportTab extends VerticalLayout {

    @Autowired
    private InsertExecutor executor;
    @Autowired
    private DBService service;
    @Autowired
    private TableUpdateService tableUpdateService;
    @Autowired
    private ItemInfoImportService itemInfoImportService;
    private Button updateAbilityDescriptions;
    private Button updateAchievements;
    private Button updateAchievementDescriptions;
    private Button updateActivators;
    private Button updateCollectibles;
    private Button updateCollectibleDescriptions;
    private Button updateItemDescriptions;
    private Button updateItemNames;
    private Button updateJournalEntrys;
    private Button updateLoadscreens;
    private Button updateLocationNames;
    private Button updateNotes;
    private Button updateNpcNames;
    private Button updateNpcPhrases;
    private Button updatePlayerPhrases;
    private Button updateQuestDescriptions;
    private Button updateQuestDirections;
    private Button updateQuestNames;
    private Button updateQuestStartTips;
    private Button updateQuestEndTips;
    private Upload uploadNewFormat;
    private Button importPlayerPhrasesFromG;
    private Button importNpcPhrasesFromG;
    private Button importLocationNamesFromG;
    private Button importQuestNamesFromG;
    private Button importQuestDescriptionsFromG;
    private Button importQuestDirectionsFromG;
    private Button importQuestStartTipsFromG;
    private Button importQuestEndTipsFromG;
    private Button importItemNamesFromG;
    private Button importItemDescriptionsFromG;
    private Button importJournalEntriesFromG;
    private Button importNpcNamesFromG;
    private Button importActiivatorsFromG;
    private Button importAchievementsFromG;
    private Button importAchievementDescriptionsFromG;
    private Button importNotesFromG;
    private Button importAbilityDescriptionsFromG;
    private Button importCollectiblesFromG;
    private Button importCollectibleDescriptionsFromG;
    private Button importLoadscreensFromG;
    private Button assignPhrases;
    private Button fillLocationsAndNpc;
    private Button gatherQuestStatistics;
    private Button calculateNpcStatistics;
    private Button calculateLocationStatistics;
    private Button assignTablesToRaw;
    private Upload uploadXlsEn;
    private Upload uploadXlsFr;
    private Upload uploadXlsDe;
    private Upload uploadXlsJp;
    private Upload uploadXlsRu;
    private Upload uploadInterfaceLua;
    private Upload uploadRuInterfaceLua;
    private Button updateGspreadSheetsWithRawText;
    private Button assignActivatorsWithItems;
    private Button loadAllBooks;
    private Button updateTTCNpcNames;
    private Upload uploadItemInfo;
    private static final Logger LOG = Logger.getLogger(ImportTab.class.getName());

    public ImportTab() {

    }

    public void Init() {
        NewConversationsReceiver newReceiver = new NewConversationsReceiver(service);
        ItemInfoReceiver itemInfoReceiver = new ItemInfoReceiver();
        uploadNewFormat = new Upload("Загрузите файл ConversationsQQ.lua", newReceiver);
        uploadNewFormat.addSucceededListener(newReceiver);
        uploadNewFormat.setImmediate(true);
        this.addComponent(uploadNewFormat);
        if (SpringSecurityHelper.hasRole("ROLE_ADMIN")) {
            updateAbilityDescriptions = new Button("Обновить описания способностей");
            updateAbilityDescriptions.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateAbilityDescriptions();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateAbilityDescriptions);
            updateAchievements = new Button("Обновить достижения");
            updateAchievements.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateAchievements();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateAchievements);
            updateAchievementDescriptions = new Button("Обновить описания достижений");
            updateAchievementDescriptions.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateAchievementDescriptions();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateAchievementDescriptions);
            updateActivators = new Button("Обновить активаторы");
            updateActivators.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateActivators();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateActivators);
            updateCollectibles = new Button("Обновить коллекционные предметы");
            updateCollectibles.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateCollectibles();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateCollectibles);
            updateCollectibleDescriptions = new Button("Обновить описания коллекционных предметов");
            updateCollectibleDescriptions.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateCollectibleDescriptions();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateCollectibleDescriptions);
            updateItemDescriptions = new Button("Обновить описания предметов");
            updateItemDescriptions.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateItemDescriptions();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateItemDescriptions);
            updateItemNames = new Button("Обновить названия предметов");
            updateItemNames.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateItemNames();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateItemNames);
            updateJournalEntrys = new Button("Обновить записи журнала");
            updateJournalEntrys.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateJournalEntrys();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateJournalEntrys);
            updateLoadscreens = new Button("Обновить загрузочные экраны");
            updateLoadscreens.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateLoadscreens();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateLoadscreens);
            updateLocationNames = new Button("Обновить названия локаций");
            updateLocationNames.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateLocationNames();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateLocationNames);
            updateNotes = new Button("Обновить письма");
            updateNotes.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateNotes();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateNotes);
            updateNpcNames = new Button("Обновить имена NPC");
            updateNpcNames.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateNpcNames();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateNpcNames);
            updateNpcPhrases = new Button("Обновить реплики NPC");
            updateNpcPhrases.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateNpcPhrases();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateNpcPhrases);
            updatePlayerPhrases = new Button("Обновить реплики игрока");
            updatePlayerPhrases.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updatePlayerPhrases();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updatePlayerPhrases);
            updateQuestDescriptions = new Button("Обновить описания квестов");
            updateQuestDescriptions.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateQuestDescriptions();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateQuestDescriptions);
            updateQuestDirections = new Button("Обновить цели квестов");
            updateQuestDirections.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateQuestDirections();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateQuestDirections);
            updateQuestNames = new Button("Обновить названия квестов");
            updateQuestNames.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateQuestNames();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateQuestNames);
            updateQuestStartTips = new Button("Обновить начатые цепочки");
            updateQuestStartTips.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateQuestStartTips();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateQuestStartTips);
            updateQuestEndTips = new Button("Обновить завершённые цепочки");
            updateQuestEndTips.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        tableUpdateService.updateQuestEndTips();
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(updateQuestEndTips);
            /*importPlayerPhrasesFromG = new Button("Импорт фраз игрока из гугл-таблиц");
            importPlayerPhrasesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsPlayerPhrase> playerPhrases = docsService.getPlayerPhrases();
                    service.loadPlayerPhrasesFromSpreadSheet(playerPhrases);
                }
            });
            this.addComponent(importPlayerPhrasesFromG);
            
            importNpcPhrasesFromG = new Button("Импорт фраз NPC из гугл-таблиц");
            importNpcPhrasesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        GoogleDocsService docsService = new GoogleDocsService();
                        List<GSpreadSheetsNpcPhrase> npcPhrases = docsService.getNpcPhrases();
                        service.loadNpcPhrasesFromSpreadSheet(npcPhrases);
                    } catch (Exception ex) {
                        Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.addComponent(importNpcPhrasesFromG);
            importLocationNamesFromG = new Button("Импорт локаций из гугл-таблиц");
            importLocationNamesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsLocationName> locationsNames = docsService.getLocationsNames();
                    service.loadLocationNamesFromSpreadSheet(locationsNames);
                }
            });
            this.addComponent(importLocationNamesFromG);

            importQuestNamesFromG = new Button("Импорт названий квестов из гугл-таблиц");
            importQuestNamesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsQuestName> items = docsService.getQuestNames();
                    service.loadQuestNamesFromSpreadSheet(items);
                }
            });
            this.addComponent(importQuestNamesFromG);

            importQuestDescriptionsFromG = new Button("Импорт описаний квестов из гугл-таблиц");
            importQuestDescriptionsFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsQuestDescription> items = docsService.getQuestDescriptions();
                    service.loadQuestDesciptionsFromSpreadSheet(items);
                }
            });
            this.addComponent(importQuestDescriptionsFromG);

            importQuestDirectionsFromG = new Button("Импорт целей квестов из гугл-таблиц");
            importQuestDirectionsFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsQuestDirection> items = docsService.getQuestDirections();
                    service.loadQuestDirectionsFromSpreadSheet(items);
                }
            });
            this.addComponent(importQuestDirectionsFromG);

            importItemNamesFromG = new Button("Импорт названий предметов из гугл-таблиц");
            importItemNamesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsItemName> items = docsService.getItemNames();
                    service.loadItemNamesFromSpreadSheet(items);
                }
            });
            this.addComponent(importItemNamesFromG);

            importItemDescriptionsFromG = new Button("Импорт описаний предметов из гугл-таблиц");
            importItemDescriptionsFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsItemDescription> items = docsService.getItemDescriptions();
                    service.loadItemDesciptionsFromSpreadSheet(items);
                }
            });
            this.addComponent(importItemDescriptionsFromG);

            importJournalEntriesFromG = new Button("Импорт записей журнала из гугл-таблиц");
            importJournalEntriesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsJournalEntry> items = docsService.getJournaleEntries();
                    service.loadJournalEntriesFromSpreadSheet(items);
                }
            });
            this.addComponent(importJournalEntriesFromG);

            importActiivatorsFromG = new Button("Импорт активаторов из гугл-таблиц");
            importActiivatorsFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsActivator> items = docsService.getActivators();
                    service.loadActivatorsFromSpreadSheet(items);
                }
            });
            this.addComponent(importActiivatorsFromG);

            importAchievementsFromG = new Button("Импорт достижений из гугл-таблиц");
            importAchievementsFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsAchievement> items = docsService.getAchievements();
                    service.loadAchievementsFromSpreadSheet(items);
                }
            });
            this.addComponent(importAchievementsFromG);

            importAchievementDescriptionsFromG = new Button("Импорт описаний достижений из гугл-таблиц");
            importAchievementDescriptionsFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsAchievementDescription> items = docsService.getAchievementDescriptions();
                    service.loadAchievementDescriptionsFromSpreadSheet(items);
                }
            });
            this.addComponent(importAchievementDescriptionsFromG);

            importNotesFromG = new Button("Импорт записок из гугл-таблиц");
            importNotesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsNote> items = docsService.getNotes();
                    service.loadNotesFromSpreadSheet(items);
                }
            });
            this.addComponent(importNotesFromG);
            importAbilityDescriptionsFromG = new Button("Импорт описаний способностей из гугл-таблиц");
            importAbilityDescriptionsFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsAbilityDescription> items = docsService.getAbilityDescriptions();
                    service.loadAbilityDescriptionsFromSpreadSheet(items);
                }
            });
            this.addComponent(importAbilityDescriptionsFromG);

            importCollectiblesFromG = new Button("Импорт коллекционных предметов из гугл-таблиц");
            importCollectiblesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsCollectible> items = docsService.getCollectibles();
                    service.loadCollectiblesFromSpreadSheet(items);
                }
            });
            this.addComponent(importCollectiblesFromG);

            importCollectibleDescriptionsFromG = new Button("Импорт описаний коллекционных предметов из гугл-таблиц");
            importCollectibleDescriptionsFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsCollectibleDescription> items = docsService.getCollectibleDescriptions();
                    service.loadCollectibleDescriptionsFromSpreadSheet(items);
                }
            });
            this.addComponent(importCollectibleDescriptionsFromG);

            importLoadscreensFromG = new Button("Импорт загрузочных экранов из гугл-таблиц");
            importLoadscreensFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsLoadscreen> items = docsService.getLoadscreens();
                    service.loadLoadscreensFromSpreadSheet(items);
                }
            });
            this.addComponent(importLoadscreensFromG);

            importNpcNamesFromG = new Button("Импорт NPC из гугл-таблиц");
            importNpcNamesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsNpcName> npcNames = docsService.getNpcNames();
                    service.loadNpcNamesFromSpreadSheet(npcNames);
                }
            });
            this.addComponent(importNpcNamesFromG);
            
            importQuestStartTipsFromG = new Button("Импорт стартовых цепочек заданий");
            importQuestStartTipsFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsQuestStartTip> items = docsService.getQuestStartTips();
                    service.loadQuestStartTipsFromSpreadSheet(items);
                }
            });
            this.addComponent(importQuestStartTipsFromG);
            importQuestEndTipsFromG = new Button("Импорт конечных цепочек заданий");
            importQuestEndTipsFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsQuestEndTip> items = docsService.getQuestEndTips();
                    service.loadQuestEndTipsFromSpreadSheet(items);
                }
            });
            this.addComponent(importQuestEndTipsFromG); */
            fillLocationsAndNpc = new Button("Заполнить имена локаций и NPC");
            fillLocationsAndNpc.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    service.fillLocationsAndNpcs();
                }
            });
            this.addComponent(fillLocationsAndNpc);
            assignPhrases = new Button("Привязка фраз");
            assignPhrases.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    service.mergeLocations();
                    service.mergeNpcs();
                    //service.generateSearchIndex();
                    service.assignToSpreadSheetPhrases();
                    service.mergeSubtitles();
                    service.mergeTopics();
                }
            });
            this.addComponent(assignPhrases);
            gatherQuestStatistics = new Button("Пересчитать счётчики квестов");
            gatherQuestStatistics.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    service.gatherQuestStatistics();
                }
            });
            this.addComponent(gatherQuestStatistics);
            calculateNpcStatistics = new Button("Пересчитать счётчики NPC");
            calculateNpcStatistics.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    BeanItemContainer<Npc> c = new BeanItemContainer<>(Npc.class);
                    c = service.getNpcs(c, null, null, false);
                    for (Npc n : c.getItemIds()) {
                        service.calculateNpcProgress(n);
                    }
                }
            });
            this.addComponent(calculateNpcStatistics);
            calculateLocationStatistics = new Button("Пересчитать счётчики Локаций");
            calculateLocationStatistics.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    List<Location> locs = service.getLocations();
                    for (Location l : locs) {
                        if (l.getNpcs() != null && !l.getNpcs().isEmpty()) {
                            service.calculateLocationProgress(l);
                        }
                    }
                }
            });
            this.addComponent(calculateLocationStatistics);
            RaswStringReceiverEn raswStringReceiverEn = new RaswStringReceiverEn(service);
            uploadXlsEn = new Upload("Загрузите en-файл xlsx", raswStringReceiverEn);
            uploadXlsEn.addSucceededListener(raswStringReceiverEn);
            uploadXlsEn.setImmediate(true);
            this.addComponent(uploadXlsEn);
            RaswStringReceiverFr raswStringReceiverFr = new RaswStringReceiverFr(service);
            uploadXlsFr = new Upload("Загрузите f-файл xlsx", raswStringReceiverFr);
            uploadXlsFr.addSucceededListener(raswStringReceiverFr);
            uploadXlsFr.setImmediate(true);
            this.addComponent(uploadXlsFr);
            RaswStringReceiverDe raswStringReceiverDe = new RaswStringReceiverDe(service);
            uploadXlsDe = new Upload("Загрузите de-файл xlsx", raswStringReceiverDe);
            uploadXlsDe.addSucceededListener(raswStringReceiverDe);
            uploadXlsDe.setImmediate(true);
            this.addComponent(uploadXlsDe);
            RaswStringReceiverJp raswStringReceiverJp = new RaswStringReceiverJp(service);
            uploadXlsJp = new Upload("Загрузите jp-файл xlsx", raswStringReceiverJp);
            uploadXlsJp.addSucceededListener(raswStringReceiverJp);
            uploadXlsJp.setImmediate(true);
            this.addComponent(uploadXlsJp);
            RaswStringReceiverRu raswStringReceiverRu = new RaswStringReceiverRu(service);
            uploadXlsRu = new Upload("Загрузите ru-файл xlsx", raswStringReceiverRu);
            uploadXlsRu.addSucceededListener(raswStringReceiverRu);
            uploadXlsRu.setImmediate(true);
            this.addComponent(uploadXlsRu);
            assignTablesToRaw = new Button("Привязать строки таблиц к строкам raw");
            assignTablesToRaw.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    for (int i = 0; i < 1; i++) {
                        service.assignSpreadSheetRowsToRawStrings();
                    }

                }
            });
            this.addComponent(assignTablesToRaw);
            updateGspreadSheetsWithRawText = new Button("Обновить текст строк таблиц из raw");
            updateGspreadSheetsWithRawText.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    for (int i = 0; i < 1; i++) {
                        service.updateGspreadSheetTextEn();
                    }

                }
            });
            this.addComponent(updateGspreadSheetsWithRawText);
            InterfaceLuaReceiver interfaceLuaReceiver = new InterfaceLuaReceiver(service);
            uploadInterfaceLua = new Upload("Загрузите lua-файл интерфейса", interfaceLuaReceiver);
            uploadInterfaceLua.addSucceededListener(interfaceLuaReceiver);
            uploadInterfaceLua.setImmediate(true);
            this.addComponent(uploadInterfaceLua);
            InterfaceRuLuaReceiver interfaceRuLuaReceiver = new InterfaceRuLuaReceiver(service);
            uploadRuInterfaceLua = new Upload("Загрузите файл с русскими строчками интерфейса", interfaceRuLuaReceiver);
            uploadRuInterfaceLua.addSucceededListener(interfaceRuLuaReceiver);
            uploadRuInterfaceLua.setImmediate(true);
            this.addComponent(uploadRuInterfaceLua);

            assignActivatorsWithItems = new Button("Заполнить непереведённые активаторы и названия предметов");
            assignActivatorsWithItems.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    service.assignActivatorsToItems();
                }
            });
            this.addComponent(assignActivatorsWithItems);
            loadAllBooks = new Button("Импорт книг из сырых строк");
            loadAllBooks.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    service.loadBooks();
                }
            });
            this.addComponent(loadAllBooks);
            updateTTCNpcNames = new Button("Обновление NPC в TTC");
            updateTTCNpcNames.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    docsService.updateTTCNpcTranslations(service);
                }
            });
            this.addComponent(updateTTCNpcNames);
            uploadItemInfo = new Upload("Загрузите файл ItemDump.lua", itemInfoReceiver);
            uploadItemInfo.addSucceededListener(itemInfoReceiver);
            uploadItemInfo.setImmediate(true);
            this.addComponent(uploadItemInfo);

        }
    }

    private String getStringFromCell(Cell c) {
        String result = null;
        switch (c.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                result = c.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                Double numValue = c.getNumericCellValue();
                result = Integer.toString(numValue.intValue());
        }
        return result;
    }

    private Long getLongFromCell(Cell c) {
        Long result = null;
        switch (c.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                result = Long.valueOf(c.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                Double d = c.getNumericCellValue();
                result = d.longValue();
        }
        return result;
    }

    private class RaswStringReceiverEn implements Receiver, SucceededListener {

        private final DBService service;

        private ByteArrayOutputStream baos;

        public RaswStringReceiverEn(DBService service) {
            this.service = service;
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            try {

                ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                XSSFWorkbook wb = new XSSFWorkbook(bais);
                Iterator<Sheet> sheetIterator = wb.sheetIterator();
                List<Object[]> rows = new ArrayList<>();
                while (sheetIterator.hasNext()) {
                    Sheet s = sheetIterator.next();
                    Long aId = Long.valueOf(s.getSheetName());
                    Iterator<Row> rowIterator = s.rowIterator();
                    while (rowIterator.hasNext()) {
                        Row r = rowIterator.next();
                        Cell bIdCell = r.getCell(1);
                        Cell cIdCell = r.getCell(2);
                        Cell textCell = r.getCell(3);
                        Object[] row = new Object[]{aId, getLongFromCell(bIdCell), getLongFromCell(cIdCell), getStringFromCell(textCell)};
                        rows.add(row);
                        if (rows.size() > 5000) {
                            InsertTask task = new InsertTask(rows, service);
                            taskExecutor.execute(task);
                            rows = new ArrayList<>();
                        }

                    }
                }
                if (rows.size() > 0) {
                    InsertTask task = new InsertTask(rows, service);
                    taskExecutor.execute(task);
                    rows = new ArrayList<>();
                }
                wb.close();
                for (;;) {
                    int count = taskExecutor.getActiveCount();
                    LOG.log(Level.INFO, "Active Threads : {0} Queue size:{1}", new Object[]{count, taskExecutor.getThreadPoolExecutor().getQueue().size()});
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    if (count == 0) {
                        break;
                    }
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }

        @Component
        @Scope("prototype")
        private class InsertTask implements Runnable {

            private final List<Object[]> rows;
            private final DBService service;

            public InsertTask(List<Object[]> rows, DBService service) {
                this.rows = rows;
                this.service = service;
            }

            @Override
            public void run() {
                service.insertEnRawStrings(rows);
            }

        }

    }

    private class RaswStringReceiverFr implements Receiver, SucceededListener {

        private final DBService service;

        private ByteArrayOutputStream baos;

        public RaswStringReceiverFr(DBService service) {
            this.service = service;
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            try {
                ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                XSSFWorkbook wb = new XSSFWorkbook(bais);
                Iterator<Sheet> sheetIterator = wb.sheetIterator();
                List<Object[]> rows = new ArrayList<>();
                while (sheetIterator.hasNext()) {
                    Sheet s = sheetIterator.next();
                    Long aId = Long.valueOf(s.getSheetName());
                    Iterator<Row> rowIterator = s.rowIterator();
                    while (rowIterator.hasNext()) {
                        Row r = rowIterator.next();
                        Cell bIdCell = r.getCell(1);
                        Cell cIdCell = r.getCell(2);
                        Cell textCell = r.getCell(3);
                        Object[] row = new Object[]{aId, getLongFromCell(bIdCell), getLongFromCell(cIdCell), getStringFromCell(textCell)};
                        rows.add(row);
                        if (rows.size() > 5000) {
                            InsertTask task = new InsertTask(rows, service);
                            taskExecutor.execute(task);
                            rows = new ArrayList<>();
                        }

                    }
                }
                if (rows.size() > 0) {
                    InsertTask task = new InsertTask(rows, service);
                    taskExecutor.execute(task);
                    rows = new ArrayList<>();
                }
                wb.close();
                for (;;) {
                    int count = taskExecutor.getActiveCount();
                    LOG.log(Level.INFO, "Active Threads : {0} Queue size:{1}", new Object[]{count, taskExecutor.getThreadPoolExecutor().getQueue().size()});
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    if (count == 0) {
                        break;
                    }
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }

        @Component
        @Scope("prototype")
        private class InsertTask implements Runnable {

            private final List<Object[]> rows;
            private final DBService service;

            public InsertTask(List<Object[]> rows, DBService service) {
                this.rows = rows;
                this.service = service;
            }

            @Override
            public void run() {
                service.updateFrRawStrings(rows);
            }

        }
    }

    private class RaswStringReceiverDe implements Receiver, SucceededListener {

        private final DBService service;

        private ByteArrayOutputStream baos;

        public RaswStringReceiverDe(DBService service) {
            this.service = service;
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            try {
                ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                XSSFWorkbook wb = new XSSFWorkbook(bais);
                Iterator<Sheet> sheetIterator = wb.sheetIterator();
                List<Object[]> rows = new ArrayList<>();
                while (sheetIterator.hasNext()) {
                    Sheet s = sheetIterator.next();
                    Long aId = Long.valueOf(s.getSheetName());
                    Iterator<Row> rowIterator = s.rowIterator();
                    while (rowIterator.hasNext()) {
                        Row r = rowIterator.next();
                        Cell bIdCell = r.getCell(1);
                        Cell cIdCell = r.getCell(2);
                        Cell textCell = r.getCell(3);
                        Object[] row = new Object[]{aId, getLongFromCell(bIdCell), getLongFromCell(cIdCell), getStringFromCell(textCell)};
                        rows.add(row);
                        if (rows.size() > 5000) {
                            InsertTask task = new InsertTask(rows, service);
                            taskExecutor.execute(task);
                            rows = new ArrayList<>();
                        }

                    }
                }
                if (rows.size() > 0) {
                    InsertTask task = new InsertTask(rows, service);
                    taskExecutor.execute(task);
                    rows = new ArrayList<>();
                }
                wb.close();
                for (;;) {
                    int count = taskExecutor.getActiveCount();
                    LOG.log(Level.INFO, "Active Threads : {0} Queue size:{1}", new Object[]{count, taskExecutor.getThreadPoolExecutor().getQueue().size()});
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    if (count == 0) {
                        break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Component
        @Scope("prototype")
        private class InsertTask implements Runnable {

            private final List<Object[]> rows;
            private final DBService service;

            public InsertTask(List<Object[]> rows, DBService service) {
                this.rows = rows;
                this.service = service;
            }

            @Override
            public void run() {
                service.updateDeRawStrings(rows);
            }

        }
    }

    private class RaswStringReceiverJp implements Receiver, SucceededListener {

        private final DBService service;

        private ByteArrayOutputStream baos;

        public RaswStringReceiverJp(DBService service) {
            this.service = service;
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            try {
                ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                XSSFWorkbook wb = new XSSFWorkbook(bais);
                Iterator<Sheet> sheetIterator = wb.sheetIterator();
                List<Object[]> rows = new ArrayList<>();
                while (sheetIterator.hasNext()) {
                    Sheet s = sheetIterator.next();
                    Long aId = Long.valueOf(s.getSheetName());
                    Iterator<Row> rowIterator = s.rowIterator();
                    while (rowIterator.hasNext()) {
                        Row r = rowIterator.next();
                        Cell bIdCell = r.getCell(1);
                        Cell cIdCell = r.getCell(2);
                        Cell textCell = r.getCell(3);
                        Object[] row = new Object[]{aId, getLongFromCell(bIdCell), getLongFromCell(cIdCell), getStringFromCell(textCell)};
                        rows.add(row);
                        if (rows.size() > 5000) {
                            InsertTask task = new InsertTask(rows, service);
                            taskExecutor.execute(task);
                            rows = new ArrayList<>();
                        }
                    }
                }
                if (rows.size() > 0) {
                    InsertTask task = new InsertTask(rows, service);
                    taskExecutor.execute(task);
                    rows = new ArrayList<>();
                }
                wb.close();
                for (;;) {
                    int count = taskExecutor.getActiveCount();
                    LOG.log(Level.INFO, "Active Threads : {0} Queue size:{1}", new Object[]{count, taskExecutor.getThreadPoolExecutor().getQueue().size()});
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    if (count == 0) {
                        break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Component
        @Scope("prototype")
        private class InsertTask implements Runnable {

            private final List<Object[]> rows;
            private final DBService service;

            public InsertTask(List<Object[]> rows, DBService service) {
                this.rows = rows;
                this.service = service;
            }

            @Override
            public void run() {
                service.updateJpRawStrings(rows);
            }

        }
    }

    private class RaswStringReceiverRu implements Receiver, SucceededListener {

        private final DBService service;

        private ByteArrayOutputStream baos;

        public RaswStringReceiverRu(DBService service) {
            this.service = service;
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            try {
                ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
                SimpleDateFormat versdf = new SimpleDateFormat("yyyyMMddHHmm");
                String ver = versdf.format(new Date());
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                XSSFWorkbook wb = new XSSFWorkbook(bais);
                Iterator<Sheet> sheetIterator = wb.sheetIterator();
                List<Object[]> rows = new ArrayList<>();
                while (sheetIterator.hasNext()) {
                    Sheet s = sheetIterator.next();
                    Long aId = Long.valueOf(s.getSheetName());
                    Iterator<Row> rowIterator = s.rowIterator();
                    while (rowIterator.hasNext()) {
                        Row r = rowIterator.next();
                        Cell bIdCell = r.getCell(1);
                        Cell cIdCell = r.getCell(2);
                        Cell textCell = r.getCell(3);
                        Object[] row = new Object[]{aId, getLongFromCell(bIdCell), getLongFromCell(cIdCell), getStringFromCell(textCell)};
                        rows.add(row);
                        if (rows.size() > 5000) {
                            InsertTask task = new InsertTask(rows, ver, service);
                            taskExecutor.execute(task);
                            rows = new ArrayList<>();
                        }
                    }
                }
                if (rows.size() > 0) {
                    InsertTask task = new InsertTask(rows, ver, service);
                    taskExecutor.execute(task);
                    rows = new ArrayList<>();
                }
                wb.close();
                for (;;) {
                    int count = taskExecutor.getActiveCount();
                    LOG.log(Level.INFO, "Active Threads : {0} Queue size:{1}", new Object[]{count, taskExecutor.getThreadPoolExecutor().getQueue().size()});
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    if (count == 0) {
                        break;
                    }
                }
                Logger.getLogger(ImportTab.class.getName()).log(Level.INFO, "cleanup raw with null ver");
                service.cleanupRawWithNullVer();
                Logger.getLogger(ImportTab.class.getName()).log(Level.INFO, "cleanup raw with wrong ver");
                service.cleanupRawWithWrongVer(ver);
            } catch (IOException ex) {
                Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Component
        @Scope("prototype")
        private class InsertTask implements Runnable {

            private final List<Object[]> rows;
            private final DBService service;
            private final String ver;

            public InsertTask(List<Object[]> rows, String ver, DBService service) {
                this.rows = rows;
                this.service = service;
                this.ver = ver;
            }

            @Override
            public void run() {
                service.updateRuRawStrings(rows, ver);
            }

        }
    }

    private class NewConversationsReceiver implements Receiver, SucceededListener {

        public NewConversationsReceiver(DBService service) {
            this.service = service;
        }

        private final DBService service;

        private ByteArrayOutputStream baos;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            Date startTime = new Date();
            LOG.info("Search index...");
            service.generateSearchIndex();
            service.generateJournalEntrySearchIndex();
            service.generateQuestDirectionSearchIndex();
            LOG.info("Search index complete");
            byte[] toByteArray = baos.toByteArray();
            String text = new String(toByteArray);
            JSONObject jsonFromLua = LuaDecoder.getJsonFromLua(text);
            if (LuaDecoder.getFileheader(text).equals("ConversationsQQ_SavedVariables_v14 =")) {
                newFormatImportNpcsWithSublocations(jsonFromLua);
                newFormatImportSubtitlesWithSublocations(jsonFromLua);
                newFormatImportQuestsWithSteps(jsonFromLua);
                importBooksWithSublocations(jsonFromLua);
            }
            for (;;) {
                int count = executor.getActiveCount();
                LOG.log(Level.INFO, "Active Threads : {0} Queue size:{1}", new Object[]{count, executor.getThreadPoolExecutor().getQueue().size()});
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                if (count == 0) {
                    break;
                }
            }
            Date endTime = new Date();
            long totalTime = endTime.getTime() - startTime.getTime();
            Date completeTime = new Date(totalTime);
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            LOG.log(Level.INFO, "Completed in {0}", sdf.format(completeTime));
        }

        private void newFormatImportQuestsWithSteps(JSONObject source) {
            JSONObject locationObject = null;
            try {
                locationObject = source.getJSONObject("quest");
                Iterator locationsKeys = locationObject.keys();
                while (locationsKeys.hasNext()) {
                    String locationName = (String) locationsKeys.next();
                    ImportQuestsLocationTask task = new ImportQuestsLocationTask(locationName, locationObject);
                    executor.execute(task);
                }
            } catch (JSONException ex) {

            }
        }

        private void newFormatImportSubtitlesWithSublocations(JSONObject source) {
            JSONObject npcLocationObject = null;
            try {
                npcLocationObject = source.getJSONObject("subtitles");
                Iterator locationsKeys = npcLocationObject.keys();
                while (locationsKeys.hasNext()) {
                    String locationName = (String) locationsKeys.next();
                    ImportSubtitleLocationTask importSubtitleLocationTask = new ImportSubtitleLocationTask(locationName, npcLocationObject);
                    executor.execute(importSubtitleLocationTask);
                }
            } catch (JSONException ex) {

            }
        }

        private void newFormatImportNpcsWithSublocations(JSONObject source) {
            JSONObject npcLocationObject = source.getJSONObject("npc");
            Iterator locationsKeys = npcLocationObject.keys();
            while (locationsKeys.hasNext()) {
                String locationKey = (String) locationsKeys.next();
                ImportNpcLocationTask importNpcLocationTask = new ImportNpcLocationTask(locationKey, npcLocationObject);
                executor.execute(importNpcLocationTask);
            }

        }

        private void importBooksWithSublocations(JSONObject source) {
            LOG.info("Books import queue");
            JSONObject bookLocationObject = null;
            try {
                bookLocationObject = source.getJSONObject("books");
                Iterator locationsKeys = bookLocationObject.keys();
                while (locationsKeys.hasNext()) {
                    String locationKey = (String) locationsKeys.next();
                    Location location = service.getLocation(locationKey);
                    if (location != null) {
                        JSONObject subLocationObject = bookLocationObject.getJSONObject(locationKey);
                        Iterator subLocationKeys = subLocationObject.keys();
                        while (subLocationKeys.hasNext()) {
                            String subLocationKey = (String) subLocationKeys.next();
                            Location subLocation = service.getSubLocation(subLocationKey, locationKey, location);
                            if (subLocation != null) {
                                JSONObject locationBooksObject = subLocationObject.getJSONObject(subLocationKey);
                                Iterator locationBooksObjectIterator = locationBooksObject.keys();
                                while (locationBooksObjectIterator.hasNext()) {
                                    String bookKeyString = (String) locationBooksObjectIterator.next();
                                    ImportBookTask task = new ImportBookTask(bookKeyString, subLocation);
                                    executor.execute(task);
                                }
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                LOG.log(Level.WARNING, null, ex);
            }
            LOG.info("Books queued");
        }

        @Component
        @Scope("prototype")
        private class ImportQuestsLocationTask implements Runnable {

            private final String locationName;
            private final JSONObject locationObject;

            public ImportQuestsLocationTask(String locationName, JSONObject locationObject) {
                this.locationName = locationName;
                this.locationObject = locationObject;
            }

            @Override
            public void run() {
                Location location = service.getLocation(locationName);
                if (location != null) {
                    JSONObject locationQuestsObject = locationObject.getJSONObject(locationName);
                    Iterator locationQuestsObjectIterator = locationQuestsObject.keys();
                    while (locationQuestsObjectIterator.hasNext()) {
                        String questKey = (String) locationQuestsObjectIterator.next();
                        JSONObject questObject = locationQuestsObject.getJSONObject(questKey);
                        ImportQuestTask task = new ImportQuestTask(questKey, questObject, location);
                        executor.execute(task);
                    }
                }
            }

        }

        @Component
        @Scope("prototype")
        private class ImportQuestTask implements Runnable {

            private final String questKey;
            private final JSONObject questObject;
            private final Location location;

            public ImportQuestTask(String questKey, JSONObject questObject, Location location) {
                this.questKey = questKey;
                this.questObject = questObject;
                this.location = location;
            }

            @Override
            public void run() {
                service.newFormatImportQuestWithSteps(questKey, questObject, location);
            }

        }

        @Component
        @Scope("prototype")
        private class ImportBookTask implements Runnable {

            private final String bookKeyString;
            private final Location subLocation;

            public ImportBookTask(String bookKeyString, Location subLocation) {
                this.bookKeyString = bookKeyString;
                this.subLocation = subLocation;
            }

            @Override
            public void run() {
                service.importBook(bookKeyString, subLocation);
            }

        }

        @Component
        @Scope("prototype")
        private class ImportNpcLocationTask implements Runnable {

            private final String locationKey;
            private final JSONObject npcLocationObject;

            public ImportNpcLocationTask(String locationKey, JSONObject npcLocationObject) {
                this.locationKey = locationKey;
                this.npcLocationObject = npcLocationObject;
            }

            @Override
            public void run() {
                Location location = service.getLocation(locationKey);
                if (location != null) {
                    JSONObject subLocationObject = npcLocationObject.getJSONObject(locationKey);
                    Iterator subLocationKeys = subLocationObject.keys();
                    while (subLocationKeys.hasNext()) {
                        String subLocationKey = (String) subLocationKeys.next();
                        ImportNpcSublocationTask task = new ImportNpcSublocationTask(subLocationKey, locationKey, subLocationObject, location);
                        executor.execute(task);
                    }
                }
            }

        }

        @Component
        @Scope("prototype")
        private class ImportNpcSublocationTask implements Runnable {

            private final String subLocationKey;
            private final String locationKey;
            private final JSONObject subLocationObject;
            private final Location location;

            public ImportNpcSublocationTask(String subLocationKey, String locationKey, JSONObject subLocationObject, Location location) {
                this.subLocationKey = subLocationKey;
                this.locationKey = locationKey;
                this.subLocationObject = subLocationObject;
                this.location = location;
            }

            @Override
            public void run() {
                Location subLocation = service.getSubLocation(subLocationKey, locationKey, location);
                if (subLocation != null) {
                    JSONObject npcsObject = subLocationObject.getJSONObject(subLocationKey);
                    Iterator npcsKeys = npcsObject.keys();
                    while (npcsKeys.hasNext()) {
                        String npcKey = (String) npcsKeys.next();
                        ImportNpcWithSublocationsTask task = new ImportNpcWithSublocationsTask(npcKey, subLocation, npcsObject);
                        executor.execute(task);
                    }
                }
            }
        }

        @Component
        @Scope("prototype")
        private class ImportNpcWithSublocationsTask implements Runnable {

            private final String npcKey;
            private final Location subLocation;
            private final JSONObject npcsObject;

            public ImportNpcWithSublocationsTask(String npcKey, Location subLocation, JSONObject npcsObject) {
                this.npcKey = npcKey;
                this.subLocation = subLocation;
                this.npcsObject = npcsObject;
            }

            @Override
            public void run() {
                Npc currentNpc = service.getNpc(npcKey, subLocation);
                JSONObject npcContent = npcsObject.getJSONObject(npcKey);
                service.newFormatImportNpcWithSublocations(currentNpc, npcContent);
                service.calculateNpcProgress(currentNpc);
            }

        }

        @Component
        @Scope("prototype")
        private class ImportSubtitleLocationTask implements Runnable {

            private final String locationName;
            private final JSONObject npcLocationObject;

            public ImportSubtitleLocationTask(String locationName, JSONObject npcLocationObject) {
                this.locationName = locationName;
                this.npcLocationObject = npcLocationObject;
            }

            @Override
            public void run() {
                Location location = service.getLocation(locationName);
                if (location != null) {
                    JSONObject subLocationObject = npcLocationObject.getJSONObject(locationName);
                    Iterator subLocationKeys = subLocationObject.keys();
                    while (subLocationKeys.hasNext()) {
                        String subLocationKey = (String) subLocationKeys.next();
                        ImportSubtitleSubLocationTask task = new ImportSubtitleSubLocationTask(subLocationKey, locationName, subLocationObject, location);
                        executor.execute(task);
                    }
                }
            }

        }

        @Component
        @Scope("prototype")
        private class ImportSubtitleSubLocationTask implements Runnable {

            private final String subLocationKey;
            private final String locationName;
            private final JSONObject subLocationObject;
            private final Location location;

            public ImportSubtitleSubLocationTask(String subLocationKey, String locationName, JSONObject subLocationObject, Location location) {
                this.subLocationKey = subLocationKey;
                this.locationName = locationName;
                this.subLocationObject = subLocationObject;
                this.location = location;
            }

            @Override
            public void run() {
                Location subLocation = service.getSubLocation(subLocationKey, locationName, location);
                if (subLocation != null) {
                    JSONObject locationSubtitlesObject = subLocationObject.getJSONObject(subLocationKey);
                    Iterator locationSubtitlesObjectIterator = locationSubtitlesObject.keys();
                    while (locationSubtitlesObjectIterator.hasNext()) {
                        JSONObject subtitleSet = locationSubtitlesObject.getJSONObject((String) locationSubtitlesObjectIterator.next());
                        ImportSubtitlesTask task = new ImportSubtitlesTask(subtitleSet, subLocation);
                        executor.execute(task);
                    }
                }
            }

        }

        @Component
        @Scope("prototype")
        private class ImportSubtitlesTask implements Runnable {

            private final JSONObject subtitleSet;
            private final Location subLocation;

            public ImportSubtitlesTask(JSONObject subtitleSet, Location subLocation) {
                this.subtitleSet = subtitleSet;
                this.subLocation = subLocation;
            }

            @Override
            public void run() {
                service.newFormatImportSubtitleWithSublocations(subtitleSet, subLocation);
            }

        }

    }

    private class ItemInfoReceiver implements Receiver, SucceededListener {

        private ByteArrayOutputStream baos;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            byte[] toByteArray = baos.toByteArray();
            String text = new String(toByteArray);
            JSONObject jsonFromLua = LuaDecoder.getJsonFromLua(text);
            Iterator keys = jsonFromLua.keys();
            while (keys.hasNext()) {
                String itemName = (String) keys.next();
                String itemIcon = null;
                Long itemType = null;
                Long itemSubType = null;
                JSONObject itemObject = jsonFromLua.getJSONObject(itemName);
                if (itemObject != null) {
                    try {
                        itemIcon = itemObject.getString("1");
                    } catch (JSONException ex) {

                    }
                    try {
                        JSONObject itemTypesObject = itemObject.getJSONObject("0");
                        try {
                            itemType = itemTypesObject.getLong("0");
                        } catch (JSONException ex) {

                        }
                        try {
                            itemSubType = itemTypesObject.getLong("1");
                        } catch (JSONException ex) {

                        }
                    } catch (JSONException ex) {

                    }
                }
                executor.execute(new UpdateItemTask(itemName, itemIcon, itemType, itemSubType));
                LOG.log(Level.INFO, "{0} {1} {2} {3}", new Object[]{itemName, itemIcon, itemType, itemSubType});

            }
            for (;;) {
                int count = executor.getActiveCount();
                LOG.log(Level.INFO, "Active Threads : {0} Queue size:{1}", new Object[]{count, executor.getThreadPoolExecutor().getQueue().size()});
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                if (count == 0) {
                    break;
                }
            }
        }

        @Component
        @Scope("prototype")
        private class UpdateItemTask implements Runnable {

            private final String name;
            private final String icon;
            private final Long type;
            private final Long subType;

            public UpdateItemTask(String name, String icon, Long type, Long subType) {
                this.name = name;
                this.icon = icon;
                this.type = type;
                this.subType = subType;
            }

            @Override
            public void run() {
                itemInfoImportService.updateItem(name, icon, type, subType);
            }

        }

    }

    private class InterfaceLuaReceiver implements Receiver, SucceededListener {

        private final DBService service;
        private ByteArrayOutputStream baos;

        public InterfaceLuaReceiver(DBService service) {
            this.service = service;
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            try {
                List<EsoInterfaceVariable> list = new ArrayList<>();
                Pattern p = Pattern.compile("^SafeAddString\\((.*)\\,\\s+\"(.*)\"\\,\\s\\d+\\)$");
                Reader r = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
                BufferedReader br = new BufferedReader(r);
                String line = null;
                while ((line = br.readLine()) != null) {
                    Matcher m = p.matcher(line);
                    if (m.matches()) {
                        EsoInterfaceVariable i = new EsoInterfaceVariable();
                        i.setName(m.group(1));
                        i.setTextEn(m.group(2));
                        list.add(i);
                    }
                }
                service.importInterfaceStrings(list);
            } catch (IOException ex) {
                Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private class InterfaceRuLuaReceiver implements Receiver, SucceededListener {

        private final DBService service;
        private ByteArrayOutputStream baos;

        public InterfaceRuLuaReceiver(DBService service) {
            this.service = service;
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            try {
                List<EsoInterfaceVariable> list = new ArrayList<>();
                Pattern p = Pattern.compile("^\\[(.*)\\]\\s=\\s\"(.*)\"$");
                Reader r = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
                BufferedReader br = new BufferedReader(r);
                String line = null;
                while ((line = br.readLine()) != null) {
                    Matcher m = p.matcher(line);
                    if (m.matches()) {
                        EsoInterfaceVariable i = new EsoInterfaceVariable();
                        i.setName(m.group(1));
                        i.setTextRu(EsnDecoder.decode(m.group(2)));
                        list.add(i);
                    }
                }
                service.importRuInterfaceStrings(list);
            } catch (IOException ex) {
                Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
