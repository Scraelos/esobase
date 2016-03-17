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
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.GSpreadSheetsQuestDescription;
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.NPC_SEX;

/**
 *
 * @author scraelos
 */
public class GoogleDocsService {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private static final String PLAYER_PHRASES_SPREADSHEET_ID = "1baAruBJhdijtneDx_iwhfXxdeYdc7vLCRvEcX50FZm4";
    private static final String NPC_PHRASES_SPREADSHEET_ID = "11Frf4jXtB0gN4r-RaVUDhMikk1SQKwjRUbZWy9gqaj4";
    private static final String NPC_NAMES_SPREADSHEET_ID = "169HZSBDhlkY6cmqxU7MBBXQVWeYo-ZqEBtnSwhGIpHs";
    private static final String LOCATION_NAMES_SPREADSHEET_ID = "1e7J6QX-SyrF5aDkc4cBBbhztR-Es2FzXu548oiRa3Ro";
    private static final String QUEST_NAMES_SPREADSHEET_ID = "1ybqrErb9bSjt1NOufI4RyWcbzjnB6zSV7wj06ibCICk";
    private static final String QUEST_DESCRIPTIONS_SPREADSHEET_ID = "1-yWHcJioMLoqQs6eO0ReeCwEEhcqFTEgeRkLk69vziM";

    public List<GSpreadSheetsPlayerPhrase> getPlayerPhrases(DBService service) {
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
                    case 5:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                editTime = dateFormat.parse(value);
                            } catch (java.text.ParseException ex) {

                            }
                        }
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

    public List<GSpreadSheetsLocationName> getLocationsNames(DBService service) {
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
                    case 5:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                editTime = dateFormat.parse(value);
                            } catch (java.text.ParseException ex) {

                            }
                        }
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

    public List<GSpreadSheetsQuestName> getQuestNames(DBService service) {
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
                    case 5:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                editTime = dateFormat.parse(value);
                            } catch (java.text.ParseException ex) {

                            }
                        }
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

    public List<GSpreadSheetsQuestDescription> getQuestDescriptions(DBService service) {
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
                    case 5:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                editTime = dateFormat.parse(value);
                            } catch (java.text.ParseException ex) {

                            }
                        }
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

    public List<GSpreadSheetsNpcName> getNpcNames(DBService service) {
        List<GSpreadSheetsNpcName> names = new ArrayList<>();
        Pattern malePattern = Pattern.compile("\\^[Mm]");
        Pattern femalePattern = Pattern.compile("\\^[Ff]");
        Pattern nPattern = Pattern.compile("\\^[Nn]");
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
                    case 5:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                editTime = dateFormat.parse(value);
                            } catch (java.text.ParseException ex) {

                            }
                        }
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

    public List<GSpreadSheetsNpcPhrase> getNpcPhrases(DBService service) {
        List<GSpreadSheetsNpcPhrase> phrases = new ArrayList<>();
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
                    case 5:
                        if (value != null && !value.isEmpty()) {
                            try {
                                weight = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {

                            }
                        }
                        break;
                    case 6:
                        if (value != null && !value.isEmpty()) {
                            try {
                                editTime = dateFormat.parse(value);
                            } catch (java.text.ParseException ex) {

                            }
                        }
                        break;
                    default:

                }
            }
            GSpreadSheetsNpcPhrase phrase = new GSpreadSheetsNpcPhrase(Long.valueOf(lastRow), textEn, textRu, translator, weight);
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
                            break;
                        case 6:
                            if (phrase.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(phrase.getChangeTime()));
                                cellEntry.update();
                            }
                            break;
                    }
                }
                if (entries.size() < 3) {
                    CellEntry cellEntry = new CellEntry(phrase.getRowNum().intValue(), 3, phrase.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (entries.size() < 6 && phrase.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(phrase.getRowNum().intValue(), 6, dateFormat.format(phrase.getChangeTime()));
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
                            break;
                        case 6:
                            if (phrase.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(phrase.getChangeTime()));
                                cellEntry.update();
                            }
                            break;
                    }
                }
                if (entries.size() < 3) {
                    CellEntry cellEntry = new CellEntry(phrase.getRowNum().intValue(), 3, phrase.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (entries.size() < 6 && phrase.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(phrase.getRowNum().intValue(), 6, dateFormat.format(phrase.getChangeTime()));
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
                                case M:
                                    textRu += "^M";
                                    break;
                                case N:
                                    textRu += "^N";
                                    break;
                            }
                            cellEntry.changeInputValueLocal(textRu);
                            cellEntry.update();
                            break;
                        case 3:
                            cellEntry.changeInputValueLocal(name.getTranslator());
                            cellEntry.update();
                            break;
                        case 6:
                            if (name.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(name.getChangeTime()));
                                cellEntry.update();
                            }
                            break;
                    }
                }
                if (entries.size() < 3) {
                    CellEntry cellEntry = new CellEntry(name.getRowNum().intValue(), 3, name.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (entries.size() < 6 && name.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(name.getRowNum().intValue(), 6, dateFormat.format(name.getChangeTime()));
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
                            break;
                        case 6:
                            if (name.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(name.getChangeTime()));
                                cellEntry.update();
                            }
                            break;
                    }
                }
                if (entries.size() < 3) {
                    CellEntry cellEntry = new CellEntry(name.getRowNum().intValue(), 3, name.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (entries.size() < 6 && name.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(name.getRowNum().intValue(), 6, dateFormat.format(name.getChangeTime()));
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
                            break;
                        case 6:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            break;
                    }
                }
                if (entries.size() < 3) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (entries.size() < 6 && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 6, dateFormat.format(item.getChangeTime()));
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
                            break;
                        case 6:
                            if (item.getChangeTime() != null) {
                                cellEntry.changeInputValueLocal(dateFormat.format(item.getChangeTime()));
                                cellEntry.update();
                            }
                            break;
                    }
                }
                if (entries.size() < 3) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 3, item.getTranslator());
                    feedc.insert(cellEntry);
                }
                if (entries.size() < 6 && item.getChangeTime() != null) {
                    CellEntry cellEntry = new CellEntry(item.getRowNum().intValue(), 6, dateFormat.format(item.getChangeTime()));
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

}
