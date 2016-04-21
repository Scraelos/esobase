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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.GoogleDocsService;
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
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Npc;
import org.esn.esobase.security.SpringSecurityHelper;
import org.esn.esobase.tools.LuaDecoder;

/**
 *
 * @author scraelos
 */
public class ImportTab extends VerticalLayout {

    private DBService service;
    private Upload upload;
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
    private Button assignPhrases;
    private Button fillLocationsAndNpc;
    private Button gatherQuestStatistics;
    private Button calculateNpcStatistics;
    private Upload uploadXlsEn;
    private Upload uploadXlsFr;
    private Upload uploadXlsDe;

    public ImportTab(DBService service_) {
        this.service = service_;
        ConversationsReceiver receiver = new ConversationsReceiver(service);

        upload = new Upload("Загрузите файл Conversations.lua", receiver);
        upload.addSucceededListener(receiver);
        upload.setImmediate(true);
        this.addComponent(upload);
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
                    BeanItemContainer<Npc> c=new BeanItemContainer<>(Npc.class);
                    c=service.getNpcs(c, false, null);
                    for(Npc n:c.getItemIds()) {
                        service.calculateNpcProgress(n);
                    }
                }
            });
            this.addComponent(calculateNpcStatistics);
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
        }
    }

    private String getStringFromCell(Cell c) {
        String result = null;
        switch (c.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                result = c.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                result = Double.toString(c.getNumericCellValue());
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

}
