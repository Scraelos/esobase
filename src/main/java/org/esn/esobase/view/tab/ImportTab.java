/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.github.pjfanning.xlsx.StreamingReader;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.Upload;
import com.vaadin.v7.ui.Upload.Receiver;
import com.vaadin.v7.ui.Upload.SucceededListener;
import com.vaadin.v7.ui.VerticalLayout;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.DictionaryService;
import org.esn.esobase.data.GoogleDocsService;
import org.esn.esobase.data.InsertExecutor;
import org.esn.esobase.data.ItemInfoImportService;
import org.esn.esobase.data.TableUpdateService;
import org.esn.esobase.data.repository.TopicRepository;
import org.esn.esobase.model.EsoInterfaceVariable;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Topic;
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
    private DictionaryService dictionaryService;
    @Autowired
    private TableUpdateService tableUpdateService;
    @Autowired
    private ItemInfoImportService itemInfoImportService;
    @Autowired
    private TopicRepository topicRepository;
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
    private Upload uploadXlsRuoff;
    private Upload uploadInterfaceLua;
    private Upload uploadRuInterfaceLua;
    private Upload uploadDictionary;
    private Button updateGspreadSheetsWithRawText;
    private Button assignActivatorsWithItems;
    private Button loadAllBooks;
    private Button updateTTCNpcNames;
    private Upload uploadItemInfo;
    private Button updateIndexes;
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
            updateIndexes = new Button("Обновить индексы", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    LOG.info("Search index...");
                    service.generateSearchIndex();
                    service.generateJournalEntrySearchIndex();
                    service.generateQuestDirectionSearchIndex();
                    service.generateQuestDescriptionSearchIndex();
                    LOG.info("Search index complete");
                }
            });
            this.addComponent(updateIndexes);
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
                    List<Object[]> topicToMerge = service.getTopicToMerge();
                    List<Topic> topicsToDelete = new ArrayList<>();
                    List<Topic> undeletable = new ArrayList<>();
                    for (Object[] o : topicToMerge) {
                        BigInteger sId = (BigInteger) o[0];
                        BigInteger s1Id = (BigInteger) o[1];
                        executor.execute(new MergeTopicsTask(sId, s1Id, topicsToDelete, undeletable));

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
                    for (Topic t : topicsToDelete) {
                        executor.execute(new DeleteTopicTask(t));
                    }
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
                    ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
                    for (Npc n : c.getItemIds()) {
                        taskExecutor.execute(new CalculateNpcProgressTask(n));
                        LOG.log(Level.INFO, "Active Threads : {0} Queue size:{1}", new Object[]{executor.getActiveCount(), executor.getThreadPoolExecutor().getQueue().size()});
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
            RawStringReceiverRuoff rawStringReceiverRuoff = new RawStringReceiverRuoff(service);
            uploadXlsRuoff = new Upload("Загрузите ruoff-файл xlsx", rawStringReceiverRuoff);
            uploadXlsRuoff.addSucceededListener(rawStringReceiverRuoff);
            uploadXlsRuoff.setImmediate(true);
            this.addComponent(uploadXlsRuoff);
            DictionaryReceiver dictionaryReceiver = new DictionaryReceiver();
            uploadDictionary = new Upload("Загрузка словаря TES", dictionaryReceiver);
            uploadDictionary.addSucceededListener(dictionaryReceiver);
            uploadDictionary.setImmediate(true);
            this.addComponent(uploadDictionary);
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
                    docsService.updateTTCTranslations(service);
                }
            });
            this.addComponent(updateTTCNpcNames);
            uploadItemInfo = new Upload("Загрузите файл ItemDump.lua", itemInfoReceiver);
            uploadItemInfo.addSucceededListener(itemInfoReceiver);
            uploadItemInfo.setImmediate(true);
            this.addComponent(uploadItemInfo);

        }
    }

    public static String getStringFromCell(Cell cell) {
        String result = null;
        if (cell == null) {
            return null;
        }
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case BLANK:
                result = "";
                break;
            case BOOLEAN:
                boolean booleanValue = cell.getBooleanCellValue();
                result = Boolean.toString(booleanValue);
                break;
            case ERROR:
                byte errorCode = cell.getErrorCellValue();
                result = Byte.toString(errorCode);
                break;
            case FORMULA:
                result = cell.getCellFormula();
                break;
            case NUMERIC:
                Double doubleValue = cell.getNumericCellValue();
                if (doubleValue % 1 != 0) {
                    BigDecimal decimalValue = BigDecimal.valueOf(doubleValue).stripTrailingZeros();
                    result = decimalValue.toString();
                } else {
                    result = Long.toString(doubleValue.longValue());
                }
                break;
            case STRING:
                result = cell.getStringCellValue();
                break;
        }
        return result;
    }

    public static Long getLongFromCell(Cell c) {
        Long result = null;
        if (c != null) {
            switch (c.getCellType()) {
                case STRING:
                    result = Long.valueOf(c.getStringCellValue());
                    break;
                case NUMERIC:
                    Double d = c.getNumericCellValue();
                    result = d.longValue();
                    break;
                case FORMULA:
                    switch (c.getCachedFormulaResultType()) {
                        case NUMERIC:
                            Double dd = c.getNumericCellValue();
                            result = dd.longValue();
                            break;
                        case STRING:
                            result = Long.valueOf(c.getStringCellValue());
                            break;
                    }
                    break;
            }
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
                Workbook wb = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(4096)
                        .open(bais);
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
                        if (rows.size() > 1000) {
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

    private class DictionaryReceiver implements Receiver, SucceededListener {

        private ByteArrayOutputStream baos;

        public DictionaryReceiver() {
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
                Workbook wb = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(4096)
                        .open(bais);
                Iterator<Sheet> sheetIterator = wb.sheetIterator();
                dictionaryService.cleanupDictionaryStrings();
                List<Object[]> rows = new ArrayList<>();
                while (sheetIterator.hasNext()) {
                    Sheet s = sheetIterator.next();
                    Iterator<Row> rowIterator = s.rowIterator();
                    while (rowIterator.hasNext()) {
                        Row r = rowIterator.next();
                        Cell textEnCell = r.getCell(0);
                        Cell textRuCell = r.getCell(1);
                        Cell descriptionCell = r.getCell(2);
                        Cell gameCell = r.getCell(3);
                        Object[] row = new Object[]{getStringFromCell(textEnCell), getStringFromCell(textRuCell), getStringFromCell(descriptionCell), getStringFromCell(gameCell)};
                        rows.add(row);
                        if (rows.size() > 1000) {
                            DictionaryInsertTask task = new DictionaryInsertTask(rows, service);
                            taskExecutor.execute(task);
                            rows = new ArrayList<>();
                        }

                    }
                }
                if (rows.size() > 0) {
                    DictionaryInsertTask task = new DictionaryInsertTask(rows, service);
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
        private class DictionaryInsertTask implements Runnable {

            private final List<Object[]> rows;

            public DictionaryInsertTask(List<Object[]> rows, DBService service) {
                this.rows = rows;
            }

            @Override
            public void run() {
                dictionaryService.insertDictionaryStrings(rows);
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
                Workbook wb = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(4096)
                        .open(bais);
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
                        if (rows.size() > 1000) {
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
                Workbook wb = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(4096)
                        .open(bais);
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
                        if (rows.size() > 1000) {
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
                Workbook wb = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(4096)
                        .open(bais);
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
                        if (rows.size() > 1000) {
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
                Workbook wb = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(4096)
                        .open(bais);
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
                        if (rows.size() > 1000) {
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
                //Logger.getLogger(ImportTab.class.getName()).log(Level.INFO, "cleanup raw with null ver");
                //service.cleanupRawWithNullVer();
                //Logger.getLogger(ImportTab.class.getName()).log(Level.INFO, "cleanup raw with wrong ver");
                //service.cleanupRawWithWrongVer(ver);
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

    private class RawStringReceiverRuoff implements Receiver, SucceededListener {

        private final DBService service;

        private ByteArrayOutputStream baos;

        public RawStringReceiverRuoff(DBService service) {
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
                Workbook wb = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(4096)
                        .open(bais);
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
                        if (rows.size() > 100) {
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
                //Logger.getLogger(ImportTab.class.getName()).log(Level.INFO, "cleanup raw with null ver");
                //service.cleanupRawWithNullVer();
                //Logger.getLogger(ImportTab.class.getName()).log(Level.INFO, "cleanup raw with wrong ver");
                //service.cleanupRawWithWrongVer(ver);
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
                service.updateRuoffRawStrings(rows, ver);
            }

        }
    }

    private class NewConversationsReceiver implements Receiver, SucceededListener {

        public NewConversationsReceiver(DBService service) {
            this.service = service;
        }

        private final DBService service;

        private ImportStatsCallBack cb;

        private ByteArrayOutputStream baos;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            baos = new ByteArrayOutputStream();
            return baos;
        }

        private void waitExecutorToComplete() {
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

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            cb = new ImportStatsCallBack();
            Date startTime = new Date();
            byte[] toByteArray = baos.toByteArray();
            String text = new String(toByteArray);
            JSONObject jsonFromLua = LuaDecoder.getJsonFromLua(text);
            if (LuaDecoder.getFileheader(text).equals("ConversationsQQ_SavedVariables_v14")) {
                newFormatImportNpcsWithSublocations(jsonFromLua);
                waitExecutorToComplete();
                newFormatImportSubtitlesWithSublocations(jsonFromLua);
                waitExecutorToComplete();
                newFormatImportQuestsWithSteps(jsonFromLua);
                waitExecutorToComplete();
                importBooksWithSublocations(jsonFromLua);
            } else if (LuaDecoder.getFileheader(text).equals("ConversationsQQ_SavedVariables_v15")) {
                V15ImportNpcsWithSublocations(jsonFromLua);
                waitExecutorToComplete();
                newFormatImportSubtitlesWithSublocations(jsonFromLua);
                waitExecutorToComplete();
                newFormatImportQuestsWithSteps(jsonFromLua);
                waitExecutorToComplete();
                importBooksWithSublocations(jsonFromLua);
            }
            waitExecutorToComplete();
            Date endTime = new Date();
            long totalTime = endTime.getTime() - startTime.getTime();
            Date completeTime = new Date(totalTime);
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            LOG.log(Level.INFO, "Completed in {0}", sdf.format(completeTime));
            Notification n = new Notification("Загрузка выполнена");
            StringBuilder description = new StringBuilder();
            description.append("Время загрузки: ").append(sdf.format(completeTime));
            description.append("Добавлено:").append("\n");
            description.append("Локаций — ").append(Integer.toString(cb.getNewLocations())).append("\n");
            description.append("NPC — ").append(Integer.toString(cb.getNewNpcs())).append("\n");
            description.append("Диалогов — ").append(Integer.toString(cb.getNewTopics())).append("\n");
            description.append("Субтитров — ").append(Integer.toString(cb.getNewSubtitles())).append("\n");
            description.append("Квестов — ").append(Integer.toString(cb.getNewQuests())).append("\n");
            description.append("Стадий квестов — ").append(Integer.toString(cb.getNewQuestSteps())).append("\n");
            description.append("Целей квестов — ").append(Integer.toString(cb.getNewQuestDirections())).append("\n");
            description.append("Квестовых предметов — ").append(Integer.toString(cb.getNewQuestItems())).append("\n");

            n.setDescription(description.toString());
            n.setDelayMsec(30000);
            n.show(getUI().getPage());
        }

        private void newFormatImportQuestsWithSteps(JSONObject source) {
            JSONObject locationObject = null;
            try {
                locationObject = source.getJSONObject("quest");
                Iterator locationsKeys = locationObject.keys();
                while (locationsKeys.hasNext()) {
                    String locationName = (String) locationsKeys.next();
                    ImportQuestsLocationTask task = new ImportQuestsLocationTask(locationName, locationObject, cb);
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
                    ImportSubtitleLocationTask importSubtitleLocationTask = new ImportSubtitleLocationTask(locationName, npcLocationObject, cb);
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
                ImportNpcLocationTask importNpcLocationTask = new ImportNpcLocationTask(locationKey, npcLocationObject, cb);
                executor.execute(importNpcLocationTask);
            }

        }

        private void V15ImportNpcsWithSublocations(JSONObject source) {
            JSONObject npcLocationObject = source.getJSONObject("npc");
            Iterator locationsKeys = npcLocationObject.keys();
            while (locationsKeys.hasNext()) {
                String locationKey = (String) locationsKeys.next();
                V15ImportNpcLocationTask importNpcLocationTask = new V15ImportNpcLocationTask(locationKey, npcLocationObject, cb);
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
                    Location location = service.getLocation(locationKey, cb);
                    if (location != null) {
                        JSONObject subLocationObject = bookLocationObject.getJSONObject(locationKey);
                        Iterator subLocationKeys = subLocationObject.keys();
                        while (subLocationKeys.hasNext()) {
                            String subLocationKey = (String) subLocationKeys.next();
                            Location subLocation = service.getSubLocation(subLocationKey, locationKey, location, cb);
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
            }
            LOG.info("Books queued");
        }

        @Component
        @Scope("prototype")
        private class ImportQuestsLocationTask implements Runnable {

            private String locationName;
            private final JSONObject locationObject;
            private final ImportStatsCallBack cb;

            public ImportQuestsLocationTask(String locationName, JSONObject locationObject, ImportStatsCallBack cb) {
                this.locationName = locationName;
                this.locationObject = locationObject;
                this.cb = cb;
            }

            @Override
            public void run() {
                JSONObject locationQuestsObject = locationObject.getJSONObject(locationName);
                if (locationName.equals("Северный Эльсвейр")) {
                    locationName = "Эльсвейр";
                } else if (locationName.equals("Northern Elsweyr")) {
                    locationName = "Elsweyr";
                }
                Location location = service.getLocation(locationName, cb);
                if (location != null) {
                    Iterator locationQuestsObjectIterator = locationQuestsObject.keys();
                    while (locationQuestsObjectIterator.hasNext()) {
                        String questKey = (String) locationQuestsObjectIterator.next();
                        JSONObject questObject = locationQuestsObject.getJSONObject(questKey);
                        ImportQuestTask task = new ImportQuestTask(questKey, questObject, location, cb);
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
            private final ImportStatsCallBack cb;

            public ImportQuestTask(String questKey, JSONObject questObject, Location location, ImportStatsCallBack cb) {
                this.questKey = questKey;
                this.questObject = questObject;
                this.location = location;
                this.cb = cb;
            }

            @Override
            public void run() {
                service.newFormatImportQuestWithSteps(questKey, questObject, location, cb);
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
            private final ImportStatsCallBack cb;

            public ImportNpcLocationTask(String locationKey, JSONObject npcLocationObject, ImportStatsCallBack cb) {
                this.locationKey = locationKey;
                this.npcLocationObject = npcLocationObject;
                this.cb = cb;
            }

            @Override
            public void run() {
                Location location = service.getLocation(locationKey, cb);
                if (location != null) {
                    JSONObject subLocationObject = npcLocationObject.getJSONObject(locationKey);
                    Iterator subLocationKeys = subLocationObject.keys();
                    while (subLocationKeys.hasNext()) {
                        String subLocationKey = (String) subLocationKeys.next();
                        ImportNpcSublocationTask task = new ImportNpcSublocationTask(subLocationKey, locationKey, subLocationObject, location, cb);
                        executor.execute(task);
                    }
                }
            }

        }

        @Component
        @Scope("prototype")
        private class V15ImportNpcLocationTask implements Runnable {

            private final String locationKey;
            private final JSONObject npcLocationObject;
            private final ImportStatsCallBack cb;

            public V15ImportNpcLocationTask(String locationKey, JSONObject npcLocationObject, ImportStatsCallBack cb) {
                this.locationKey = locationKey;
                this.npcLocationObject = npcLocationObject;
                this.cb = cb;
            }

            @Override
            public void run() {
                Location location = service.getLocation(locationKey, cb);
                if (location != null) {
                    JSONObject subLocationObject = npcLocationObject.getJSONObject(locationKey);
                    Iterator subLocationKeys = subLocationObject.keys();
                    while (subLocationKeys.hasNext()) {
                        String subLocationKey = (String) subLocationKeys.next();
                        V15ImportNpcSublocationTask task = new V15ImportNpcSublocationTask(subLocationKey, locationKey, subLocationObject, location, cb);
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
            private final ImportStatsCallBack cb;

            public ImportNpcSublocationTask(String subLocationKey, String locationKey, JSONObject subLocationObject, Location location, ImportStatsCallBack cb) {
                this.subLocationKey = subLocationKey;
                this.locationKey = locationKey;
                this.subLocationObject = subLocationObject;
                this.location = location;
                this.cb = cb;
            }

            @Override
            public void run() {
                Location subLocation = service.getSubLocation(subLocationKey, locationKey, location, cb);
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
        private class V15ImportNpcSublocationTask implements Runnable {

            private final String subLocationKey;
            private final String locationKey;
            private final JSONObject subLocationObject;
            private final Location location;
            private final ImportStatsCallBack cb;

            public V15ImportNpcSublocationTask(String subLocationKey, String locationKey, JSONObject subLocationObject, Location location, ImportStatsCallBack cb) {
                this.subLocationKey = subLocationKey;
                this.locationKey = locationKey;
                this.subLocationObject = subLocationObject;
                this.location = location;
                this.cb = cb;
            }

            @Override
            public void run() {
                Location subLocation = service.getSubLocation(subLocationKey, locationKey, location, cb);
                if (subLocation != null) {
                    JSONObject npcsObject = subLocationObject.getJSONObject(subLocationKey);
                    Iterator npcsKeys = npcsObject.keys();
                    while (npcsKeys.hasNext()) {
                        String npcKey = (String) npcsKeys.next();
                        V15ImportNpcWithSublocationsTask task = new V15ImportNpcWithSublocationsTask(npcKey, subLocation, npcsObject, cb);
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
                service.calculateLocationProgress(currentNpc.getLocation());
                if (currentNpc.getLocation().getParentLocation() != null) {
                    service.calculateLocationProgress(currentNpc.getLocation().getParentLocation());
                }
            }

        }

        @Component
        @Scope("prototype")
        private class V15ImportNpcWithSublocationsTask implements Runnable {

            private final String npcKey;
            private final Location subLocation;
            private final JSONObject npcsObject;
            private final ImportStatsCallBack cb;

            public V15ImportNpcWithSublocationsTask(String npcKey, Location subLocation, JSONObject npcsObject, ImportStatsCallBack cb) {
                this.npcKey = npcKey;
                this.subLocation = subLocation;
                this.npcsObject = npcsObject;
                this.cb = cb;
            }

            @Override
            public void run() {
                Npc currentNpc = service.getNpc(npcKey, subLocation);
                JSONObject npcContent = npcsObject.getJSONObject(npcKey);
                service.v15importNpcWithSublocations(currentNpc, npcContent, cb);
                service.calculateNpcProgress(currentNpc);

            }

        }

        @Component
        @Scope("prototype")
        private class ImportSubtitleLocationTask implements Runnable {

            private final String locationName;
            private final JSONObject npcLocationObject;
            private final ImportStatsCallBack cb;

            public ImportSubtitleLocationTask(String locationName, JSONObject npcLocationObject, ImportStatsCallBack cb) {
                this.locationName = locationName;
                this.npcLocationObject = npcLocationObject;
                this.cb = cb;
            }

            @Override
            public void run() {
                Location location = service.getLocation(locationName, cb);
                if (location != null) {
                    JSONObject subLocationObject = npcLocationObject.getJSONObject(locationName);
                    Iterator subLocationKeys = subLocationObject.keys();
                    while (subLocationKeys.hasNext()) {
                        String subLocationKey = (String) subLocationKeys.next();
                        ImportSubtitleSubLocationTask task = new ImportSubtitleSubLocationTask(subLocationKey, locationName, subLocationObject, location, cb);
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
            private final ImportStatsCallBack cb;

            public ImportSubtitleSubLocationTask(String subLocationKey, String locationName, JSONObject subLocationObject, Location location, ImportStatsCallBack cb) {
                this.subLocationKey = subLocationKey;
                this.locationName = locationName;
                this.subLocationObject = subLocationObject;
                this.location = location;
                this.cb = cb;
            }

            @Override
            public void run() {
                Location subLocation = service.getSubLocation(subLocationKey, locationName, location, cb);
                if (subLocation != null) {
                    JSONObject locationSubtitlesObject = subLocationObject.getJSONObject(subLocationKey);
                    Iterator locationSubtitlesObjectIterator = locationSubtitlesObject.keys();
                    while (locationSubtitlesObjectIterator.hasNext()) {
                        JSONObject subtitleSet = locationSubtitlesObject.getJSONObject((String) locationSubtitlesObjectIterator.next());
                        ImportSubtitlesTask task = new ImportSubtitlesTask(subtitleSet, subLocation, cb);
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
            private final ImportStatsCallBack cb;

            public ImportSubtitlesTask(JSONObject subtitleSet, Location subLocation, ImportStatsCallBack cb) {
                this.subtitleSet = subtitleSet;
                this.subLocation = subLocation;
                this.cb = cb;
            }

            @Override
            public void run() {
                service.newFormatImportSubtitleWithSublocations(subtitleSet, subLocation, cb);
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

    @Component
    @Scope("prototype")
    private class MergeTopicsTask implements Runnable {

        private final BigInteger sId;
        private final BigInteger s1Id;
        private final List<Topic> topicsToDelete;
        private final List<Topic> undeletable;

        public MergeTopicsTask(BigInteger sId, BigInteger s1Id, List<Topic> topicsToDelete, List<Topic> undeletable) {
            this.sId = sId;
            this.s1Id = s1Id;
            this.topicsToDelete = topicsToDelete;
            this.undeletable = undeletable;
        }

        @Override
        public void run() {
            service.mergeTopics(sId, s1Id, topicsToDelete, undeletable);
        }

    }

    @Component
    @Scope("prototype")
    private class DeleteTopicTask implements Runnable {

        private final Topic topicToDelete;

        public DeleteTopicTask(Topic topicToDelete) {
            this.topicToDelete = topicToDelete;
        }

        @Override
        public void run() {
            topicRepository.delete(topicToDelete);
        }

    }

    @Component
    @Scope("prototype")
    private class CalculateNpcProgressTask implements Runnable {

        private final Npc npc;

        public CalculateNpcProgressTask(Npc npc) {
            this.npc = npc;
        }

        @Override
        public void run() {
            service.calculateNpcProgress(npc);
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

    public class ImportStatsCallBack {

        private int newLocations = 0;
        private int newNpcs = 0;
        private int newTopics = 0;
        private int newSubtitles = 0;
        private int newQuests = 0;
        private int newQuestSteps = 0;
        private int newQuestDirections = 0;
        private int newQuestItems = 0;

        public synchronized void newLocation() {
            newLocations++;
        }

        public synchronized void newNpc() {
            newNpcs++;
        }

        public synchronized void newTopic() {
            newTopics++;
        }

        public synchronized void newSubtitle() {
            newSubtitles++;
        }

        public synchronized void newQuest() {
            newQuests++;
        }

        public synchronized void newQuestSteps() {
            newQuestSteps++;
        }

        public synchronized void newQuestDirections() {
            newQuestDirections++;
        }

        public synchronized void newQuestItems() {
            newQuestItems++;
        }

        public int getNewLocations() {
            return newLocations;
        }

        public int getNewNpcs() {
            return newNpcs;
        }

        public int getNewTopics() {
            return newTopics;
        }

        public int getNewSubtitles() {
            return newSubtitles;
        }

        public int getNewQuests() {
            return newQuests;
        }

        public int getNewQuestSteps() {
            return newQuestSteps;
        }

        public int getNewQuestDirections() {
            return newQuestDirections;
        }

        public int getNewQuestItems() {
            return newQuestItems;
        }

    }

}
