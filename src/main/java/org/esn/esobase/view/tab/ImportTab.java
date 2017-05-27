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
import java.util.ArrayList;
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
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Npc;
import org.esn.esobase.security.SpringSecurityHelper;
import org.esn.esobase.tools.EsnDecoder;
import org.esn.esobase.tools.LuaDecoder;
import org.json.JSONObject;

/**
 *
 * @author scraelos
 */
public class ImportTab extends VerticalLayout {

    private DBService service;
    private Upload upload;
    private Upload uploadNewFormat;
    private Button importPlayerPhrasesFromG;
    private Button importNpcPhrasesFromG;
    private Button importLocationNamesFromG;
    private Button importQuestNamesFromG;
    private Button importQuestDescriptionsFromG;
    private Button importQuestDirectionsFromG;
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
    private Upload uploadInterfaceLua;
    private Upload uploadRuInterfaceLua;
    private Button updateGspreadSheetsWithRawText;
    private Button assignActivatorsWithItems;
    private Button transferGreetingsToTopicsButton;

    public ImportTab(DBService service_) {
        this.service = service_;
        /*ConversationsReceiver receiver = new ConversationsReceiver(service);

        if (SpringSecurityHelper.hasRole("ROLE_ADMIN")) {
            upload = new Upload("Загрузите файл Conversations.lua", receiver);
            upload.addSucceededListener(receiver);
            upload.setImmediate(true);
            this.addComponent(upload);
        }*/

        NewConversationsReceiver newReceiver = new NewConversationsReceiver(service);
        uploadNewFormat = new Upload("Загрузите файл ConversationsQQ.lua", newReceiver);
        uploadNewFormat.addSucceededListener(newReceiver);
        uploadNewFormat.setImmediate(true);
        this.addComponent(uploadNewFormat);
        if (SpringSecurityHelper.hasRole("ROLE_ADMIN")) {
            importPlayerPhrasesFromG = new Button("Импорт фраз игрока из гугл-таблиц");
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
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsNpcPhrase> npcPhrases = docsService.getNpcPhrases();
                    service.loadNpcPhrasesFromSpreadSheet(npcPhrases);
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
                    service.assignToSpreadSheetPhrases();
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
            transferGreetingsToTopicsButton = new Button("Перенос приветствий в диалоги");
            transferGreetingsToTopicsButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    service.transferGreetingsToTopics();
                }
            });
            this.addComponent(transferGreetingsToTopicsButton);

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
                            service.insertEnRawStrings(rows);
                            rows.clear();
                        }

                    }
                }
                if (rows.size() > 0) {
                    service.insertEnRawStrings(rows);
                    rows.clear();
                }
            } catch (IOException ex) {
                Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
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
                            service.updateFrRawStrings(rows);
                            rows.clear();
                        }

                    }
                }
                if (rows.size() > 0) {
                    service.updateFrRawStrings(rows);
                    rows.clear();
                }
            } catch (IOException ex) {
                Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
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
                            service.updateDeRawStrings(rows);
                            rows.clear();
                        }

                    }
                }
                if (rows.size() > 0) {
                    service.updateDeRawStrings(rows);
                    rows.clear();
                }
            } catch (IOException ex) {
                Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class ConversationsReceiver implements Receiver, SucceededListener {

        public ConversationsReceiver(DBService service) {
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
            try {
                byte[] toByteArray = baos.toByteArray();
                String text = new String(toByteArray);
                List<Location> locations = LuaDecoder.decode(text);
                service.importFromLua(locations);
            } catch (IOException ex) {
                Logger.getLogger(ImportTab.class.getName()).log(Level.SEVERE, null, ex);
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
            byte[] toByteArray = baos.toByteArray();
            String text = new String(toByteArray);
            JSONObject jsonFromLua = LuaDecoder.getJsonFromLua(text);
            if (LuaDecoder.getFileheader(text).equals("ConversationsQQ_SavedVariables_v13 =") || LuaDecoder.getFileheader(text).equals("ConversationsQQ_SavedVariables_v14 =")) {
                service.newFormatImportNpcsWithSublocations(jsonFromLua);
                service.newFormatImportSubtitlesWithSublocations(jsonFromLua);
                if (LuaDecoder.getFileheader(text).equals("ConversationsQQ_SavedVariables_v13 =")) {
                    service.newFormatImportQuestsWithSublocations(jsonFromLua);
                }
                if (LuaDecoder.getFileheader(text).equals("ConversationsQQ_SavedVariables_v14 =")) {
                    service.newFormatImportQuestsWithSteps(jsonFromLua);
                }
            } else if (LuaDecoder.getFileheader(text).equals("ConversationsQ_SavedVariables =") || LuaDecoder.getFileheader(text).equals("ConversationsQQ_SavedVariables =")) {
                service.newFormatImportNpcs(jsonFromLua);
                service.newFormatImportSubtitles(jsonFromLua);
            }
            service.assignToSpreadSheetPhrases();

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
