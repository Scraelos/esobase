/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.Cell;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.DateUtil;
import org.esn.esobase.model.EsoRawString;
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
import org.esn.esobase.model.NPC_SEX;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author scraelos
 */
public class GoogleDocsService {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private static final String PLAYER_PHRASES_SPREADSHEET_ID = "1baAruBJhdijtneDx_iwhfXxdeYdc7vLCRvEcX50FZm4";
    private static final String NPC_PHRASES_SPREADSHEET_ID = "1fJSgh3HDzmTCsf4ZUArmGNehRTHYAItxgGLIczXnZ7o";
    private static final String NPC_NAMES_SPREADSHEET_ID = "169HZSBDhlkY6cmqxU7MBBXQVWeYo-ZqEBtnSwhGIpHs";
    private static final String LOCATION_NAMES_SPREADSHEET_ID = "1e7J6QX-SyrF5aDkc4cBBbhztR-Es2FzXu548oiRa3Ro";
    private static final String QUEST_NAMES_SPREADSHEET_ID = "1ybqrErb9bSjt1NOufI4RyWcbzjnB6zSV7wj06ibCICk";
    private static final String QUEST_DESCRIPTIONS_SPREADSHEET_ID = "1-yWHcJioMLoqQs6eO0ReeCwEEhcqFTEgeRkLk69vziM";
    private static final String QUEST_DIRECTIONS_SPREADSHEET_ID = "1WOcZk2M03vzMDjCFAAVdRLtCGPxevOo9fal4IbfP_8s";
    private static final String QUEST_START_SPREADSHEET_ID = "1E_ea9HwphEw-GMjy8n1c5OEdqlCot1U_lRlDmL_KrLI";
    private static final String QUEST_END_SPREADSHEET_ID = "1pH6-EWa2siMK2LQNxrv_U6X3wZzJoVjtSVterZMFjsk";
    private static final String ACTIVATORS_SPREADSHEET_ID = "1iQBUR0nGm5gnMiGU3e2LJHrcu2aNEpBrhjnvW9nU-QU";
    private static final String JOURNAL_ENTRIES_SPREADSHEET_ID = "1-3aJBoI6hinOuV2TLXzEFAeIumO-9FRmoFai7Uh-oy0";
    private static final String ITEM_NAMES_SPREADSHEET_ID = "16uiBH-wH2UWMz7LpqenZiuCTJwNjq3RFPIRdNlBKEwY";
    private static final String ITEM_DESCRIPTIONS_SPREADSHEET_ID = "1cgRJBbXOVTwsbHXd2Ri-BdE1Von96HsROewbCMWCZes";
    private static final String ACHIEVEMENTS_SPREADSHEET_ID = "1OywcE3kHgyW5jp40Dw2TGIVG5tvwU8QNBOqxWfbIvs0";
    private static final String ACHIEVEMENTS_DESCRIPTIONS_SPREADSHEET_ID = "1lPmQ6RNsc3KaeNwYRJH0IW0ZMEoO5oMWUZ2f3LLDdtA";
    private static final String NOTES_SPREADSHEET_ID = "1uqj8yZdqGbqOv-2F0bj6K7QiDEc9vQ2U7i1S3Jb_ZdI";
    private static final String ABILTY_DESCRIPTION_SPREADSHEET_ID = "1oaQVbLHvUxb6nwCpHm5HAr-pDZXNARzKjnfqhfImHX0";
    private static final String COLLECTIBLE_SPREADSHEET_ID = "1XhOTundmS_K0MtFQXsFnFj1HNDny3EKxcVq4nufbIwY";
    private static final String COLLECTIBLE_DESCRIPTION_SPREADSHEET_ID = "1KA8lECC8ZkzSHMYWtgGSMaJZYV-1LD2lmEA3JY6FyfE";
    private static final String LOADSCREEN_SPREADSHEET_ID = "1D4chSmA19Uk-Dnyk3gcxAb9mvJOkcI9X_RyIJirPobw";
    private static final String TAMRIELTRADECENTRE_SPREADSHEET_ID = "17KOYWHbFTQvl7g7hdhuHK7Zy8PWOjZaqXMTw0Dzo2yU";

    private static final Pattern npcNameRowPattern = Pattern.compile("^TTC_NPC_.*$");
    private static final Pattern itemSetRowPattern = Pattern.compile("^ItemSet.+$");
    private static final Pattern traderLocationRowPattern = Pattern.compile("^TraderLocation.+$");

    public List<GSpreadSheetsPlayerPhrase> getPlayerPhrases() {
        List<GSpreadSheetsPlayerPhrase> phrases = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(PLAYER_PHRASES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsPlayerPhrase phrase = new GSpreadSheetsPlayerPhrase(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        phrase.setChangeTime(editTime);
                        phrases.add(phrase);
                        textEn = null;
                        textRu = null;
                        translator = null;
                        editTime = null;
                        weight = null;
                    }
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsPlayerPhrase phrase = new GSpreadSheetsPlayerPhrase(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            phrase.setChangeTime(editTime);
            phrases.add(phrase);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", phrases.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return phrases;
    }

    public List<GSpreadSheetsLocationName> getLocationsNames() {
        List<GSpreadSheetsLocationName> locations = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(LOCATION_NAMES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsLocationName location = new GSpreadSheetsLocationName(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        location.setChangeTime(editTime);
                        locations.add(location);
                        textEn = null;
                        textRu = null;
                        translator = null;
                        editTime = null;
                        weight = null;
                    }
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsLocationName location = new GSpreadSheetsLocationName(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            location.setChangeTime(editTime);
            locations.add(location);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", locations.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return locations;
    }

    public List<GSpreadSheetsQuestName> getQuestNames() {
        List<GSpreadSheetsQuestName> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(QUEST_NAMES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsQuestName item = new GSpreadSheetsQuestName(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                        textEn = null;
                        textRu = null;
                        translator = null;
                        editTime = null;
                        weight = null;
                    }
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsQuestName item = new GSpreadSheetsQuestName(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsQuestDescription> getQuestDescriptions() {
        List<GSpreadSheetsQuestDescription> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(QUEST_DESCRIPTIONS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsQuestDescription item = new GSpreadSheetsQuestDescription(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                        textEn = null;
                        textRu = null;
                        translator = null;
                        editTime = null;
                        weight = null;
                    }
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsQuestDescription item = new GSpreadSheetsQuestDescription(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsQuestStartTip> getQuestStartTips() {
        List<GSpreadSheetsQuestStartTip> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(QUEST_START_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsQuestStartTip item = new GSpreadSheetsQuestStartTip(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                        textEn = null;
                        textRu = null;
                        translator = null;
                        editTime = null;
                        weight = null;
                    }
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsQuestStartTip item = new GSpreadSheetsQuestStartTip(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsQuestEndTip> getQuestEndTips() {
        List<GSpreadSheetsQuestEndTip> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(QUEST_END_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsQuestEndTip item = new GSpreadSheetsQuestEndTip(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                        textEn = null;
                        textRu = null;
                        translator = null;
                        editTime = null;
                        weight = null;
                    }
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsQuestEndTip item = new GSpreadSheetsQuestEndTip(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsQuestDirection> getQuestDirections() {
        List<GSpreadSheetsQuestDirection> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(QUEST_DIRECTIONS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsQuestDirection item = new GSpreadSheetsQuestDirection(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                    }
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsQuestDirection item = new GSpreadSheetsQuestDirection(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsItemName> getItemNames() {
        List<GSpreadSheetsItemName> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ITEM_NAMES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsItemName item = new GSpreadSheetsItemName(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                    }
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsItemName item = new GSpreadSheetsItemName(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsItemDescription> getItemDescriptions() {
        List<GSpreadSheetsItemDescription> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ITEM_DESCRIPTIONS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsItemDescription item = new GSpreadSheetsItemDescription(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                    }
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsItemDescription item = new GSpreadSheetsItemDescription(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsJournalEntry> getJournaleEntries() {
        List<GSpreadSheetsJournalEntry> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(JOURNAL_ENTRIES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsJournalEntry item = new GSpreadSheetsJournalEntry(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                    }
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsJournalEntry item = new GSpreadSheetsJournalEntry(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsActivator> getActivators() {
        List<GSpreadSheetsActivator> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ACTIVATORS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsActivator item = new GSpreadSheetsActivator(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                    }
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsActivator item = new GSpreadSheetsActivator(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsAchievement> getAchievements() {
        List<GSpreadSheetsAchievement> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ACHIEVEMENTS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsAchievement item = new GSpreadSheetsAchievement(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                    }
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsAchievement item = new GSpreadSheetsAchievement(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsAchievementDescription> getAchievementDescriptions() {
        List<GSpreadSheetsAchievementDescription> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ACHIEVEMENTS_DESCRIPTIONS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsAchievementDescription item = new GSpreadSheetsAchievementDescription(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                    }
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsAchievementDescription item = new GSpreadSheetsAchievementDescription(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsNote> getNotes() {
        List<GSpreadSheetsNote> items = new ArrayList<>();
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(NOTES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsNote item = new GSpreadSheetsNote(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                    }
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsNote item = new GSpreadSheetsNote(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsAbilityDescription> getAbilityDescriptions() {
        List<GSpreadSheetsAbilityDescription> items = new ArrayList<>();
        try {
            Credential authorize = authorize();
            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ABILTY_DESCRIPTION_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsAbilityDescription item = new GSpreadSheetsAbilityDescription(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        items.add(item);
                    }
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsAbilityDescription item = new GSpreadSheetsAbilityDescription(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            items.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", items.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<GSpreadSheetsNpcName> getNpcNames() {
        List<GSpreadSheetsNpcName> names = new ArrayList<>();
        Pattern MalePattern = Pattern.compile("\\^[M]");
        Pattern malePattern = Pattern.compile("\\^[m]");
        Pattern FemalePattern = Pattern.compile("\\^[F]");
        Pattern femalePattern = Pattern.compile("\\^[f]");
        Pattern NPattern = Pattern.compile("\\^[N]");
        Pattern nPattern = Pattern.compile("\\^[n]");
        try {
            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(NPC_NAMES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            NPC_SEX sex = NPC_SEX.U;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;

                    if (lastRow > -1) {
                        Matcher MaleMatcher = MalePattern.matcher(textEn);
                        if (MaleMatcher.find()) {
                            sex = NPC_SEX.M;
                            if (textEn != null) {
                                textEn = textEn.replaceAll("\\^M", "").replaceAll("\\^m", "");
                            }
                            if (textRu != null) {
                                textRu = textRu.replaceAll("\\^M", "").replaceAll("\\^m", "");
                            }
                        }
                        Matcher maleMatcher = malePattern.matcher(textEn);
                        if (maleMatcher.find()) {
                            sex = NPC_SEX.m;
                            if (textEn != null) {
                                textEn = textEn.replaceAll("\\^M", "").replaceAll("\\^m", "");
                            }
                            if (textRu != null) {
                                textRu = textRu.replaceAll("\\^M", "").replaceAll("\\^m", "");
                            }
                        }
                        Matcher FemaleMatcher = FemalePattern.matcher(textEn);
                        if (FemaleMatcher.find()) {
                            sex = NPC_SEX.F;
                            if (textEn != null) {
                                textEn = textEn.replaceAll("\\^F", "").replaceAll("\\^f", "");
                            }
                            if (textRu != null) {
                                textRu = textRu.replaceAll("\\^F", "").replaceAll("\\^f", "");
                            }
                        }
                        Matcher femaleMatcher = femalePattern.matcher(textEn);
                        if (femaleMatcher.find()) {
                            sex = NPC_SEX.f;
                            if (textEn != null) {
                                textEn = textEn.replaceAll("\\^F", "").replaceAll("\\^f", "");
                            }
                            if (textRu != null) {
                                textRu = textRu.replaceAll("\\^F", "").replaceAll("\\^f", "");
                            }
                        }
                        Matcher NMatcher = NPattern.matcher(textEn);
                        if (NMatcher.find()) {
                            sex = NPC_SEX.N;
                            if (textEn != null) {
                                textEn = textEn.replaceAll("\\^N", "").replaceAll("\\^n", "");
                            }
                            if (textRu != null) {
                                textRu = textRu.replaceAll("\\^N", "").replaceAll("\\^n", "");
                            }
                        }
                        Matcher nMatcher = nPattern.matcher(textEn);
                        if (nMatcher.find()) {
                            sex = NPC_SEX.n;
                            if (textEn != null) {
                                textEn = textEn.replaceAll("\\^N", "").replaceAll("\\^n", "");
                            }
                            if (textRu != null) {
                                textRu = textRu.replaceAll("\\^N", "").replaceAll("\\^n", "");
                            }
                        }
                        GSpreadSheetsNpcName name = new GSpreadSheetsNpcName(Long.valueOf(lastRow), textEn, textRu, translator, weight, sex);
                        name.setChangeTime(editTime);
                        names.add(name);
                    }
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                    sex = NPC_SEX.U;
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            textEn = textEn.replaceAll("\\^N", "").replaceAll("\\^n", "");
            textRu = textRu.replaceAll("\\^N", "").replaceAll("\\^n", "");
            Matcher maleMatcher = malePattern.matcher(textEn);
            if (maleMatcher.find()) {
                sex = NPC_SEX.M;
                if (textEn != null) {
                    textEn = textEn.replaceAll("\\^M", "").replaceAll("\\^m", "");
                }
                if (textRu != null) {
                    textRu = textRu.replaceAll("\\^M", "").replaceAll("\\^m", "");
                }
            }
            Matcher femaleMatcher = femalePattern.matcher(textEn);
            if (femaleMatcher.find()) {
                sex = NPC_SEX.F;
                if (textEn != null) {
                    textEn = textEn.replaceAll("\\^F", "").replaceAll("\\^f", "");
                }
                if (textRu != null) {
                    textRu = textRu.replaceAll("\\^F", "").replaceAll("\\^f", "");
                }
            }
            Matcher nMatcher = nPattern.matcher(textEn);
            if (nMatcher.find()) {
                sex = NPC_SEX.N;
                if (textEn != null) {
                    textEn = textEn.replaceAll("\\^N", "").replaceAll("\\^n", "");
                }
                if (textRu != null) {
                    textRu = textRu.replaceAll("\\^N", "").replaceAll("\\^n", "");
                }
            }
            GSpreadSheetsNpcName name = new GSpreadSheetsNpcName(Long.valueOf(lastRow), textEn, textRu, translator, weight, sex);
            name.setChangeTime(editTime);
            names.add(name);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", names.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return names;
    }

    public List<GSpreadSheetsNpcPhrase> getNpcPhrases() throws Exception {
        List<GSpreadSheetsNpcPhrase> phrases = new ArrayList<>();
        Credential authorize = authorize();

        SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
        spreadsheetService.setOAuth2Credentials(authorize);
        SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
        List<SpreadsheetEntry> feedEntries = feed.getEntries();
        SpreadsheetEntry entry = null;
        for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
            if (spreadsheetEntry.getKey().equals(NPC_PHRASES_SPREADSHEET_ID)) {
                entry = spreadsheetEntry;
            }
        }

        WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
        CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
        List<CellEntry> entries = feedc.getEntries();
        int counter = 0;
        String textEn = null;
        String textRu = null;
        String translator = null;
        Date editTime = null;
        Integer weight = null;
        int lastRow = -1;
        for (CellEntry cellEntry : entries) {
            String value = cellEntry.getCell().getValue();
            int row = cellEntry.getCell().getRow();
            int col = cellEntry.getCell().getCol();
            if (row > lastRow) {
                counter++;
                if (lastRow > -1) {
                    GSpreadSheetsNpcPhrase phrase = new GSpreadSheetsNpcPhrase(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                    phrase.setChangeTime(editTime);
                    phrases.add(phrase);
                    textEn = null;
                    textRu = null;
                    translator = null;
                    editTime = null;
                    weight = null;
                }
                lastRow = row;
            }
            switch (col) {
                case 1:
                    textEn = value;
                    break;
                case 2:
                    textRu = value;
                    break;
                case 3:
                    translator = value;
                    break;
                case 6:
                    if (value != null && !value.isEmpty()) {
                        try {
                            weight = Integer.parseInt(value);
                        } catch (NumberFormatException ex) {

                        }
                    }
                    break;
                case 7:
                    editTime = getDateFromCell(cellEntry);
                    break;
                default:

            }
        }
        GSpreadSheetsNpcPhrase phrase = new GSpreadSheetsNpcPhrase(Long.valueOf(lastRow), textEn, textRu, translator, weight);
        phrase.setChangeTime(editTime);
        phrases.add(phrase);
        Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", phrases.size());
        return phrases;
    }

    public List<GSpreadSheetsCollectible> getCollectibles() {
        List<GSpreadSheetsCollectible> result = new ArrayList<>();
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(COLLECTIBLE_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsCollectible item = new GSpreadSheetsCollectible(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        result.add(item);
                        textEn = null;
                        textRu = null;
                        translator = null;
                        editTime = null;
                        weight = null;
                    }
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsCollectible item = new GSpreadSheetsCollectible(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            result.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", result.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public List<GSpreadSheetsCollectibleDescription> getCollectibleDescriptions() {
        List<GSpreadSheetsCollectibleDescription> result = new ArrayList<>();
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(COLLECTIBLE_DESCRIPTION_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsCollectibleDescription item = new GSpreadSheetsCollectibleDescription(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        result.add(item);
                        textEn = null;
                        textRu = null;
                        translator = null;
                        editTime = null;
                        weight = null;
                    }
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsCollectibleDescription item = new GSpreadSheetsCollectibleDescription(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            result.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", result.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public List<GSpreadSheetsLoadscreen> getLoadscreens() {
        List<GSpreadSheetsLoadscreen> result = new ArrayList<>();
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(LOADSCREEN_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            CellFeed feedc = spreadsheetService.getFeed(defaultWorksheet.getCellFeedUrl(), CellFeed.class);
            List<CellEntry> entries = feedc.getEntries();
            int counter = 0;
            String textEn = null;
            String textRu = null;
            String translator = null;
            Date editTime = null;
            Integer weight = null;
            int lastRow = -1;
            for (CellEntry cellEntry : entries) {
                String value = cellEntry.getCell().getValue();
                int row = cellEntry.getCell().getRow();
                int col = cellEntry.getCell().getCol();
                if (row > lastRow) {
                    counter++;
                    if (lastRow > -1) {
                        GSpreadSheetsLoadscreen item = new GSpreadSheetsLoadscreen(Long.valueOf(lastRow), textEn, textRu, translator, weight);
                        item.setChangeTime(editTime);
                        result.add(item);
                        textEn = null;
                        textRu = null;
                        translator = null;
                        editTime = null;
                        weight = null;
                    }
                    lastRow = row;
                }
                switch (col) {
                    case 1:
                        textEn = value;
                        break;
                    case 2:
                        textRu = value;
                        break;
                    case 3:
                        translator = value;
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 7:
                        editTime = getDateFromCell(cellEntry);
                        break;
                    default:

                }
            }
            GSpreadSheetsLoadscreen item = new GSpreadSheetsLoadscreen(Long.valueOf(lastRow), textEn, textRu, translator, weight);
            item.setChangeTime(editTime);
            result.add(item);
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "Fetched {0} entries", result.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public void uploadPlayerPhrases(List<GSpreadSheetsPlayerPhrase> phrases) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(PLAYER_PHRASES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsPlayerPhrase phrase : phrases) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + phrase.getRowNum().intValue() + "&max-row=" + phrase.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            cellEntry.changeInputValueLocal(phrase.getTextRu());
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(phrase.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (phrase.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(phrase.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(phrase.getRowNum().intValue(), 3, phrase.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (entries.size() < 7 && phrase.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(phrase.getRowNum().intValue(), 7, dateFormat.format(phrase.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadNpcPhrases(List<GSpreadSheetsNpcPhrase> phrases) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(NPC_PHRASES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsNpcPhrase phrase : phrases) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + phrase.getRowNum().intValue() + "&max-row=" + phrase.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            cellEntry.changeInputValueLocal(phrase.getTextRu());
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(phrase.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (phrase.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(phrase.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(phrase.getRowNum().intValue(), 3, phrase.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (entries.size() < 7 && phrase.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(phrase.getRowNum().intValue(), 7, dateFormat.format(phrase.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadNpcNames(List<GSpreadSheetsNpcName> names) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(NPC_NAMES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsNpcName name : names) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + name.getRowNum().intValue() + "&max-row=" + name.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = name.getTextRu();
                            switch (name.getSex()) {
                                case F:
                                    textRu += "^F";
                                    break;
                                case f:
                                    textRu += "^f";
                                    break;
                                case M:
                                    textRu += "^M";
                                    break;
                                case m:
                                    textRu += "^m";
                                    break;
                                case N:
                                    textRu += "^N";
                                    break;
                                case n:
                                    textRu += "^n";
                                    break;
                            }
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(name.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (name.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(name.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(name.getRowNum().intValue(), 3, name.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (entries.size() < 7 && name.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(name.getRowNum().intValue(), 7, dateFormat.format(name.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadLocationNames(List<GSpreadSheetsLocationName> names) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(LOCATION_NAMES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsLocationName name : names) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + name.getRowNum().intValue() + "&max-row=" + name.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = name.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(name.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (name.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(name.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(name.getRowNum().intValue(), 3, name.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (entries.size() < 7 && name.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(name.getRowNum().intValue(), 7, dateFormat.format(name.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadQuestNames(List<GSpreadSheetsQuestName> items) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(QUEST_NAMES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsQuestName item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadQuestDescriptions(List<GSpreadSheetsQuestDescription> items) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(QUEST_DESCRIPTIONS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsQuestDescription item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadQuestDirections(List<GSpreadSheetsQuestDirection> items) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(QUEST_DIRECTIONS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsQuestDirection item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadItemNames(List<GSpreadSheetsItemName> items) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ITEM_NAMES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsItemName item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadItemDescriptions(List<GSpreadSheetsItemDescription> items) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ITEM_DESCRIPTIONS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsItemDescription item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadJournalEntries(List<GSpreadSheetsJournalEntry> items) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(JOURNAL_ENTRIES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsJournalEntry item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadActivators(List<GSpreadSheetsActivator> items) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ACTIVATORS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsActivator item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadAchievements(List<GSpreadSheetsAchievement> items) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ACHIEVEMENTS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsAchievement item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadAchievementDescriptions(List<GSpreadSheetsAchievementDescription> items) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ACHIEVEMENTS_DESCRIPTIONS_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsAchievementDescription item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadNotes(List<GSpreadSheetsNote> items) {
        try {

            Credential authorize = authorize();

            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(NOTES_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsNote item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:

                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadAbilityDescriptions(List<GSpreadSheetsAbilityDescription> items) {
        try {

            Credential authorize = authorize();
            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(ABILTY_DESCRIPTION_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsAbilityDescription item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:
                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadCollectibles(List<GSpreadSheetsCollectible> items) {
        try {

            Credential authorize = authorize();
            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(COLLECTIBLE_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsCollectible item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:
                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadCollectibleDescriptions(List<GSpreadSheetsCollectibleDescription> items) {
        try {

            Credential authorize = authorize();
            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(COLLECTIBLE_DESCRIPTION_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsCollectibleDescription item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:
                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadLoadscreens(List<GSpreadSheetsLoadscreen> items) {
        try {

            Credential authorize = authorize();
            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                if (spreadsheetEntry.getKey().equals(LOADSCREEN_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            WorksheetEntry defaultWorksheet = entry.getDefaultWorksheet();
            for (GSpreadSheetsLoadscreen item : items) {
                URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                        + "?min-row=" + item.getRowNum().intValue() + "&max-row=" + item.getRowNum().intValue() + "&min-col=1&max-col=4").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                boolean hasTranslator = false;
                boolean hasChangeTime = false;
                for (CellEntry cellEntry : entries) {
                    Cell cell = cellEntry.getCell();

                    switch (cell.getCol()) {
                        case 1:
                            break;
                        case 2:
                            String textRu = item.getTextRu();
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(item.getTranslator());
                            cellEntry.update();
                            hasTranslator = true;
                            break;
                        case 7:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            hasChangeTime = true;
                            break;
                    }
                }
                if (!hasTranslator) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (!hasChangeTime && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 7, dateFormat.format(item.getChangeTime()));
                    feedc.insert(cellEntry);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateTTCTranslations(DBService service) {
        try {
            Credential authorize = authorize();
            SpreadsheetService spreadsheetService = new SpreadsheetService("esn-eso-base");
            spreadsheetService.setOAuth2Credentials(authorize);
            SpreadsheetFeed feed = spreadsheetService.getFeed(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"), SpreadsheetFeed.class);
            List<SpreadsheetEntry> feedEntries = feed.getEntries();
            SpreadsheetEntry entry = null;
            for (SpreadsheetEntry spreadsheetEntry : feedEntries) {
                Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "sheet: " + spreadsheetEntry.getTitle().getPlainText());
                if (spreadsheetEntry.getKey().equals(TAMRIELTRADECENTRE_SPREADSHEET_ID)) {
                    entry = spreadsheetEntry;
                }
            }

            List<WorksheetEntry> worksheets = entry.getWorksheets();
            for (WorksheetEntry wse : worksheets) {
                ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
                taskExecutor.initialize();
                taskExecutor.setCorePoolSize(2);
                taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
//                if (wse.getTitle().getPlainText().equals("Addon Strings")) {
//                    for (int i = 2; i < wse.getRowCount(); i++) {
//                        taskExecutor.execute(new UpdateNpcNameTask(wse, i, spreadsheetService, service));
//                    }
//                }
                if (wse.getTitle().getPlainText().equals("Web Site Strings")) {
//                    for (int i = 251; i < wse.getRowCount(); i++) {
//                        taskExecutor.execute(new UpdateItemSetNameTask(wse, i, spreadsheetService, service));
//                    }
                    for (int i = 856; i < wse.getRowCount(); i++) {
                        taskExecutor.execute(new UpdateTraderLocationTask(wse, i, spreadsheetService, service));
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class UpdateNpcNameTask implements Runnable {

        private final WorksheetEntry wse;
        private final int i;
        private final SpreadsheetService spreadsheetService;
        private final DBService service;

        public UpdateNpcNameTask(WorksheetEntry wse, int i, SpreadsheetService spreadsheetService, DBService service) {
            this.wse = wse;
            this.i = i;
            this.spreadsheetService = spreadsheetService;
            this.service = service;
        }

        @Override
        public void run() {
            try {
                URL cellFeedUrl = new URI(wse.getCellFeedUrl().toString()
                        + "?min-row=" + i + "&max-row=" + i + "&min-col=1&max-col=7").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                String nameEn = null;
                EsoRawString npcNameRaw = null;
                boolean hasDe = false;
                boolean hasFr = false;
                boolean hasRu = false;
                boolean hasJp = false;
                for (CellEntry cellEntry : entries) {
                    try {
                        Cell cell = cellEntry.getCell();
                        String value = cell.getValue();
                        if (cell.getCol() == 1) {
                            Matcher npcNameMatcher = npcNameRowPattern.matcher(value);
                            if (npcNameMatcher.matches()) {
                                Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "npc match");
                            } else {
                                break;
                            }
                        } else if (cell.getCol() == 2) {
                            nameEn = value;
                            npcNameRaw = service.getNpcRaw(nameEn);
                            Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "npc: " + nameEn);
                            if (npcNameRaw == null) {
                                break;
                            }
                        } else if (cell.getCol() == 7) {
                            hasRu = true;
                            cellEntry.changeInputValueLocal(npcNameRaw.getTextRuoff().toLowerCase().replace("^m", "").replace("^f", "").replace("^u", "").replace("^n", ""));
                            cellEntry.update();
                        } else if (cell.getCol() == 4) {
                            hasDe = true;
                            cellEntry.changeInputValueLocal(npcNameRaw.getTextDe().toLowerCase().replace("^m", "").replace("^f", "").replace("^u", "").replace("^n", ""));
                            cellEntry.update();
                        } else if (cell.getCol() == 5) {
                            hasFr = true;
                            cellEntry.changeInputValueLocal(npcNameRaw.getTextFr().toLowerCase().replace("^m", "").replace("^f", "").replace("^u", "").replace("^n", ""));
                            cellEntry.update();
                        } else if (cell.getCol() == 6) {
                            hasJp = true;
                            cellEntry.changeInputValueLocal(npcNameRaw.getTextJp().toLowerCase().replace("^m", "").replace("^f", "").replace("^u", "").replace("^n", ""));
                            cellEntry.update();
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    if (!hasRu && npcNameRaw != null) {

                        CellEntry cellEntry = new CellEntry(i, 7, npcNameRaw.getTextRuoff().toLowerCase().replace("^m", "").replace("^f", "").replace("^u", "").replace("^n", ""));
                        feedc.insert(cellEntry);
                    }
                    if (!hasDe && npcNameRaw != null) {

                        CellEntry cellEntry = new CellEntry(i, 4, npcNameRaw.getTextDe().toLowerCase().replace("^m", "").replace("^f", "").replace("^u", "").replace("^n", ""));
                        feedc.insert(cellEntry);
                    }
                    if (!hasFr && npcNameRaw != null) {

                        CellEntry cellEntry = new CellEntry(i, 5, npcNameRaw.getTextFr().toLowerCase().replace("^m", "").replace("^f", "").replace("^u", "").replace("^n", ""));
                        feedc.insert(cellEntry);
                    }
                    if (!hasJp && npcNameRaw != null) {

                        CellEntry cellEntry = new CellEntry(i, 6, npcNameRaw.getTextJp().toLowerCase().replace("^m", "").replace("^f", "").replace("^u", "").replace("^n", ""));
                        feedc.insert(cellEntry);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception ex) {
                Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private class UpdateItemSetNameTask implements Runnable {

        private final WorksheetEntry wse;
        private final int i;
        private final SpreadsheetService spreadsheetService;
        private final DBService service;

        public UpdateItemSetNameTask(WorksheetEntry wse, int i, SpreadsheetService spreadsheetService, DBService service) {
            this.wse = wse;
            this.i = i;
            this.spreadsheetService = spreadsheetService;
            this.service = service;
        }

        @Override
        public void run() {
            try {
                URL cellFeedUrl = new URI(wse.getCellFeedUrl().toString()
                        + "?min-row=" + i + "&max-row=" + i + "&min-col=1&max-col=7").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                String nameEn = null;
                EsoRawString itemSetRaw = null;
                boolean hasDe = false;
                boolean hasFr = false;
                boolean hasRu = false;
                boolean hasJp = false;
                for (CellEntry cellEntry : entries) {
                    try {
                        Cell cell = cellEntry.getCell();
                        String value = cell.getValue();
                        if (cell.getCol() == 1) {
                            Matcher npcNameMatcher = itemSetRowPattern.matcher(value);
                            if (npcNameMatcher.matches()) {
                                Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "itemSetMatch: '" + value + "'");
                            } else {
                                break;
                            }
                        } else if (cell.getCol() == 2) {
                            nameEn = value;
                            itemSetRaw = service.getItemSetRaw(nameEn.trim());

                            if (itemSetRaw == null) {
                                Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "not found: '" + nameEn + "'");
                                break;
                            } else {
                                Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "found: " + nameEn);
                            }
                        } else if (cell.getCol() == 7 && !cell.getValue().equals(rs(itemSetRaw.getTextRuoff()))) {
                            hasRu = true;
                            cellEntry.changeInputValueLocal(rs(itemSetRaw.getTextRuoff()));
                            cellEntry.update();
                        } else if (cell.getCol() == 4 && !cell.getValue().equals(rs(itemSetRaw.getTextDe()))) {
                            hasDe = true;
                            cellEntry.changeInputValueLocal(rs(itemSetRaw.getTextDe()));
                            cellEntry.update();
                        } else if (cell.getCol() == 5 && !cell.getValue().equals(rs(itemSetRaw.getTextFr()))) {
                            hasFr = true;
                            cellEntry.changeInputValueLocal(rs(itemSetRaw.getTextFr()));
                            cellEntry.update();
                        } else if (cell.getCol() == 6 && !cell.getValue().equals(rs(itemSetRaw.getTextJp()))) {
                            hasJp = true;
                            cellEntry.changeInputValueLocal(rs(itemSetRaw.getTextJp()));
                            cellEntry.update();
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    if (!hasRu && itemSetRaw != null) {

                        CellEntry cellEntry = new CellEntry(i, 7, rs(itemSetRaw.getTextRuoff()));
                        feedc.insert(cellEntry);
                    }
                    if (!hasDe && itemSetRaw != null) {

                        CellEntry cellEntry = new CellEntry(i, 4, rs(itemSetRaw.getTextDe()));
                        feedc.insert(cellEntry);
                    }
                    if (!hasFr && itemSetRaw != null) {

                        CellEntry cellEntry = new CellEntry(i, 5, rs(itemSetRaw.getTextFr()));
                        feedc.insert(cellEntry);
                    }
                    if (!hasJp && itemSetRaw != null) {

                        CellEntry cellEntry = new CellEntry(i, 6, rs(itemSetRaw.getTextJp()));
                        feedc.insert(cellEntry);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception ex) {
                Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private class UpdateTraderLocationTask implements Runnable {

        private final WorksheetEntry wse;
        private final int i;
        private final SpreadsheetService spreadsheetService;
        private final DBService service;

        public UpdateTraderLocationTask(WorksheetEntry wse, int i, SpreadsheetService spreadsheetService, DBService service) {
            this.wse = wse;
            this.i = i;
            this.spreadsheetService = spreadsheetService;
            this.service = service;
        }

        @Override
        public void run() {
            try {
                URL cellFeedUrl = new URI(wse.getCellFeedUrl().toString()
                        + "?min-row=" + i + "&max-row=" + i + "&min-col=1&max-col=7").toURL();
                CellFeed feedc = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> entries = feedc.getEntries();
                String name = null;
                EsoRawString location1 = null;
                EsoRawString location2 = null;
                boolean hasDe = false;
                boolean hasFr = false;
                boolean hasRu = false;
                boolean hasJp = false;
                for (CellEntry cellEntry : entries) {
                    try {
                        Cell cell = cellEntry.getCell();
                        String value = cell.getValue();
                        if (cell.getCol() == 1) {
                            Matcher npcNameMatcher = traderLocationRowPattern.matcher(value);
                            if (npcNameMatcher.matches()) {
                                //Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "traderLocationMatch: '" + value + "'");
                            } else {
                                break;
                            }
                        } else if (cell.getCol() == 2) {
                            name = value;
                            String n[] = value.split(":");
                            location1 = service.getLocationRaw(n[0].trim());
                            location2 = service.getLocationRaw(n[1].trim());

                            if (location1 == null || location2 == null) {
                                Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "not found: '" + name + "'");
                                break;
                            } else {
                                //Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "found: " + name);
                                //Logger.getLogger(GoogleDocsService.class.getName()).log(Level.INFO, "will be: " + rs(location1.getTextRuoff()) + ": " + rs(location2.getTextRuoff()));

                                break;
                            }
                        } else if (cell.getCol() == 7 && !cell.getValue().equals(rs(location1.getTextRuoff()) + ": " + rs(location2.getTextRuoff()))) {
                            hasRu = true;
                            cellEntry.changeInputValueLocal(rs(location1.getTextRuoff()) + ": " + rs(location2.getTextRuoff()));
                            cellEntry.update();
                        }
//                        else if (cell.getCol() == 4 && !cell.getValue().equals(rs(location1.getTextDe()) + ": " + rs(location2.getTextDe()))) {
//                            hasDe = true;
//                            cellEntry.changeInputValueLocal(rs(location1.getTextDe()) + ": " + rs(location2.getTextDe()));
//                            cellEntry.update();
//                        } else if (cell.getCol() == 5 && !cell.getValue().equals(rs(location1.getTextFr()) + ": " + rs(location2.getTextFr()))) {
//                            hasFr = true;
//                            cellEntry.changeInputValueLocal(rs(location1.getTextFr()) + ": " + rs(location2.getTextFr()));
//                            cellEntry.update();
//                        } else if (cell.getCol() == 6 && !cell.getValue().equals(rs(location1.getTextJp()) + ": " + rs(location2.getTextJp()))) {
//                            hasJp = true;
//                            cellEntry.changeInputValueLocal(rs(location1.getTextJp()) + ": " + rs(location2.getTextJp()));
//                            cellEntry.update();
//                        }
                    } catch (Exception ex) {
                        Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    if (!hasRu && location1 != null && location2 != null) {

                        CellEntry cellEntry = new CellEntry(i, 7, rs(location1.getTextRuoff()) + ": " + rs(location2.getTextRuoff()));
                        feedc.insert(cellEntry);
                    }
                    if (!hasDe && location1 != null && location2 != null) {

                        CellEntry cellEntry = new CellEntry(i, 4, rs(location1.getTextDe()) + ": " + rs(location2.getTextDe()));
                        feedc.insert(cellEntry);
                    }
                    if (!hasFr && location1 != null && location2 != null) {

                        CellEntry cellEntry = new CellEntry(i, 5, rs(location1.getTextFr()) + ": " + rs(location2.getTextFr()));
                        feedc.insert(cellEntry);
                    }
                    if (!hasJp && location1 != null && location2 != null) {

                        CellEntry cellEntry = new CellEntry(i, 6, rs(rs(location1.getTextJp()) + ": " + rs(location2.getTextJp())));
                        feedc.insert(cellEntry);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception ex) {
                Logger.getLogger(GoogleDocsService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private String rs(String in) {
        return in.replace("^Nn{1}x{2}", "").replace("^Mla{1}n{2}", "").replace("^Ma{1}n{2}", "").replace("^Fn{1}x{2}", "").replace("^Mx{1}n{2}", "").replace("^Fa{1}n{2}", "").replace("^m", "").replace("^f", "").replace("^u", "").replace("^n", "").replace("^pfd", "").replace("^pm", "").replace("^M", "").replace("^F", "");
    }

    private static Credential authorize() throws Exception {
        // load client secrets
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File("/home/scraelos/"));
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(new FileInputStream("/home/scraelos/client_secret_218230677489-0f8al27el5nvfc6iguhrlop2c17oqf6r.apps.googleusercontent.com.1.json")));

        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Collections.singleton("https://spreadsheets.google.com/feeds")).setAccessType("offline").setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    private static Credential authorize2() throws Exception {
        // load client secrets
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File("/home/scraelos/"));
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(new FileInputStream("/home/scraelos/client_secret_949902383727-vijtpirvq0i6q4lnsua6bn44v9441dpa.apps.googleusercontent.com.json")));

        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Collections.singleton("https://spreadsheets.google.com/feeds")).setAccessType("offline").setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    private Date getDateFromCell(CellEntry cell) {
        Date result = null;
        if (cell != null && cell.getCell() != null && cell.getCell().getNumericValue() != null) {
            result = DateUtil.getJavaDate(cell.getCell().getNumericValue().doubleValue());
            Date now = new Date();
            if (result.after(now)) {
                result = null;
            }
        }
        return result;
    }

}
