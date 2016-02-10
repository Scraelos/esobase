/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.ui.Button;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.GoogleDocsService;
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.Location;
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
    private Button importNpcNamesFromG;
    private Button assignPhrases;
    private Button fillLocationsAndNpc;
    private Button gatherQuestStatistics;

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
                    List<GSpreadSheetsPlayerPhrase> playerPhrases = docsService.getPlayerPhrases(service);
                    service.loadPlayerPhrasesFromSpreadSheet(playerPhrases);
                }
            });
            this.addComponent(importPlayerPhrasesFromG);
            importNpcPhrasesFromG = new Button("Импорт фраз NPC из гугл-таблиц");
            importNpcPhrasesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsNpcPhrase> npcPhrases = docsService.getNpcPhrases(service);
                    service.loadNpcPhrasesFromSpreadSheet(npcPhrases);
                }
            });
            this.addComponent(importNpcPhrasesFromG);
            importLocationNamesFromG = new Button("Импорт локаций из гугл-таблиц");
            importLocationNamesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsLocationName> locationsNames = docsService.getLocationsNames(service);
                    service.loadLocationNamesFromSpreadSheet(locationsNames);
                }
            });
            this.addComponent(importLocationNamesFromG);
            importNpcNamesFromG = new Button("Импорт NPC из гугл-таблиц");
            importNpcNamesFromG.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GoogleDocsService docsService = new GoogleDocsService();
                    List<GSpreadSheetsNpcName> npcNames = docsService.getNpcNames(service);
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
