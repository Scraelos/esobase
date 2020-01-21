/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Subtitle;
import org.esn.esobase.model.Topic;
import org.json.JSONObject;

/**
 *
 * @author scraelos
 */
public class LuaDecoder {

    public static List<Location> decode(String text) throws IOException {
        List<Location> result = new ArrayList<>();
        String[] lines = text.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < lines.length; i++) {

            String line = lines[i];
            /*int indexOf = line.indexOf("--");
             if (indexOf != -1) {
             line = line.substring(0, indexOf);
             }*/
            sb.append(line.replaceAll("=", ":").replaceAll("[\\[\\]]", "").replaceAll("\" :", "\":").replaceAll(",\\n(.+)\\}", "\n$1}"));
        }
        String luaSource = sb.toString();
        String jsonString = luaSource.replaceAll("=", ":").replaceAll("[\\[\\]]", "").replaceAll("\" :", "\":").replaceAll(",\\n(.+)\\}", "\n$1}");
        try (FileOutputStream fos = new FileOutputStream(new File("/home/scraelos/conversations.json"))) {
            fos.write(jsonString.getBytes());
            fos.flush();
        }
        JSONObject locationsObject = new JSONObject(jsonString);
        Iterator locationsKeys = locationsObject.keys();
        while (locationsKeys.hasNext()) {
            String locationKey = (String) locationsKeys.next();
            String locationName = null;
            String locationNameRu = null;
            if (EsnDecoder.IsEsnEncoding(locationKey)) {
                locationNameRu = EsnDecoder.decode(locationKey);
            } else if (EsnDecoder.IsMostlyRu(locationKey)) {
                locationNameRu = locationKey;
            } else {
                locationName = locationKey;
            }
            Location location = new Location(locationName, locationNameRu);
            JSONObject npcsObject = locationsObject.getJSONObject(locationKey);
            Iterator npcsKeys = npcsObject.keys();
            while (npcsKeys.hasNext()) {
                String npcKey = (String) npcsKeys.next();
                String npcName = null;
                String npcNameRu = null;
                if (EsnDecoder.IsEsnEncoding(npcKey)) {
                    npcNameRu = EsnDecoder.decode(npcKey);
                } else if (EsnDecoder.IsMostlyRu(npcKey)) {
                    npcNameRu = npcKey;
                } else {
                    npcName = npcKey;
                }
                Npc npc = new Npc(npcName, npcNameRu, location);
                location.getNpcs().add(npc);
                JSONObject npcContent = npcsObject.getJSONObject(npcKey);
                try {
                    JSONObject subtitlesObject = npcContent.getJSONObject("subtitle");
                    Iterator subtitlesKeys = subtitlesObject.keys();
                    while (subtitlesKeys.hasNext()) {
                        String subtitlekey = (String) subtitlesKeys.next();
                        String subtitleText = null;
                        String subtitleTextRu = null;
                        if (EsnDecoder.IsEsnEncoding(subtitlekey) || EsnDecoder.IsMostlyRu(subtitlekey)) {
                            subtitleTextRu = EsnDecoder.decode(subtitlekey);
                        } else {
                            subtitleText = subtitlekey;
                        }
                        boolean isDublicate = false;
                        for (Subtitle s : npc.getSubtitles()) {
                            if ((s.getText() != null && subtitleText != null && !s.getText().isEmpty() && s.getText().equals(subtitleText)) || (s.getTextRu() != null && subtitleTextRu != null && !s.getTextRu().isEmpty() && s.getTextRu().equals(subtitleTextRu))) {
                                isDublicate = true;
                            }
                        }
                        if (!isDublicate) {
                            Subtitle subtitle = new Subtitle(subtitleText, subtitleTextRu, npc);
                            npc.getSubtitles().add(subtitle);
                        }
                    }
                } catch (org.json.JSONException ex) {

                }
                try {
                    JSONObject topicsObject = npcContent.getJSONObject("topics");
                    Iterator topicsKeys = topicsObject.keys();
                    while (topicsKeys.hasNext()) {
                        String topickey = (String) topicsKeys.next();
                        String playerText = null;
                        String playerTextRu = null;
                        String npcText = null;
                        String npcTextRu = null;
                        if (EsnDecoder.IsEsnEncoding(topickey) || EsnDecoder.IsMostlyRu(topickey)) {
                            playerTextRu = EsnDecoder.decode(topickey);
                        } else {
                            playerText = topickey.replace("Óàeæäeîèe ", "").replace("Óâpoça ", "");
                        }
                        if (EsnDecoder.IsEsnEncoding(topicsObject.getString(topickey)) || EsnDecoder.IsMostlyRu(topicsObject.getString(topickey))) {
                            npcTextRu = EsnDecoder.decode(topicsObject.getString(topickey));
                        } else {
                            npcText = topicsObject.getString(topickey);
                        }
                        boolean isDublicateNpcText = false;
                        boolean isDublicatePlayerText = false;
                        for (Topic t : npc.getTopics()) {
                            if ((t.getNpcText() != null && npcText != null && !t.getNpcText().isEmpty() && t.getNpcText().equals(npcText)) || (t.getNpcTextRu() != null && npcTextRu != null && !t.getNpcTextRu().isEmpty() && t.getNpcTextRu().equals(npcTextRu))) {
                                isDublicateNpcText = true;
                            }
                            if ((t.getPlayerText() != null && playerText != null && !t.getPlayerText().isEmpty() && t.getPlayerText().equals(playerText)) || (t.getPlayerTextRu() != null && playerTextRu != null && !t.getPlayerTextRu().isEmpty() && t.getPlayerTextRu().equals(playerTextRu))) {
                                isDublicatePlayerText = true;
                            }
                        }
                        if (!(isDublicateNpcText && isDublicatePlayerText)) {
                            Topic topic = new Topic(playerText, npcText, playerTextRu, npcTextRu, npc);
                            npc.getTopics().add(topic);
                        }
                    }
                } catch (org.json.JSONException ex) {

                }
                try {
                    JSONObject greetingsObject = npcContent.getJSONObject("greetings");
                    Iterator greetingsKeys = greetingsObject.keys();
                    while (greetingsKeys.hasNext()) {
                        String greetingskey = (String) greetingsKeys.next();
                        String greetingText = null;
                        String greetingTextRu = null;
                        if (EsnDecoder.IsEsnEncoding(greetingsObject.getString(greetingskey)) || EsnDecoder.IsMostlyRu(greetingsObject.getString(greetingskey))) {
                            greetingTextRu = EsnDecoder.decode(greetingsObject.getString(greetingskey));
                        } else {
                            greetingText = greetingsObject.getString(greetingskey);
                        }
                        boolean isDublicate = false;

                    }
                } catch (org.json.JSONException ex) {

                }
            }
            result.add(location);
        }
        return result;
    }

    public static JSONObject getJsonFromLua(String source) {
        JSONObject result = null;
        source=source.replaceAll("^ConversationsQQ_SavedVariables_v\\d{2}\\s*=", "");
        source=source.replaceAll("^ItemDump_SavedVariables\\s*=", "");
        String[] lines = source.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            sb.append(line.replaceAll("=", ":").replaceAll("[\\[\\]]", "").replaceAll("\" :", "\":").replaceAll(",\\n(.+)\\}", "\n$1}"));
        }
        String luaSource = sb.toString();
        String jsonString = luaSource.replaceAll("=", ":").replaceAll("[\\[\\]]", "").replaceAll("\" :", "\":").replaceAll(",\\n(.+)\\}", "\n$1}");
        result = new JSONObject(jsonString);
        return result;
    }

    public static String getFileheader(String source) {
        Pattern headerPattern = Pattern.compile("^(ConversationsQQ_SavedVariables_v\\d{2})\\s*=");
        String result = "";
        Matcher matcher = headerPattern.matcher(source);
        if (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

}
