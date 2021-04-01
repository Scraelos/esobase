/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.service;

import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;
import org.esn.esobase.data.SysAccountService;
import org.esn.esobase.data.repository.GSpreadSheetsNpcPhraseRepository;
import org.esn.esobase.data.repository.GSpreadSheetsPlayerPhraseRepository;
import org.esn.esobase.data.repository.TranslatedTextRepository;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.SysAccountRole;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.TranslatedText;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author scraelos
 */
@Controller
@RequestMapping("translateapi")
public class TranslateApiServlet {

    private static final Logger LOG = Logger.getLogger(TranslateApiServlet.class.getName());

    @Autowired
    private SysAccountService sysAccountService;
    @Autowired
    private GSpreadSheetsNpcPhraseRepository npcPhraseRepository;
    @Autowired
    private GSpreadSheetsPlayerPhraseRepository playerPhraseRepository;
    @Autowired
    private TranslatedTextRepository translatedTextRepository;

    @GetMapping(produces = "application/json")
    public @ResponseBody
    String req(@RequestParam(name = "apikey") String apiKey,
            @RequestParam(name = "req") String reqTypeString,
            @RequestParam(name = "id") String idString,
            @RequestParam(name = "text", required = false) String text) {
        JSONObject result = new JSONObject();
        try {
            SysAccount user = sysAccountService.getAccountByApi(apiKey);
            if (!user.getRoles().contains(new SysAccountRole(3L))) {
                throw new Exception("access denied");
            }
            REQUEST_TYPE requestType = REQUEST_TYPE.valueOf(reqTypeString);

            Long npcPhraseId;
            GSpreadSheetsNpcPhrase npcPhrase;
            Long playerPhraseId;
            GSpreadSheetsPlayerPhrase playerPhrase;
            switch (requestType) {
                case getPlayerPhraseInfo:
                    playerPhraseId = Long.valueOf(idString);
                    playerPhrase = playerPhraseRepository.getOne(playerPhraseId);
                    result.put("id", playerPhrase.getId());
                    result.put("textEn", playerPhrase.getTextEn());
                    result.put("textRu", playerPhrase.getTextRu());
                    result.put("translator", playerPhrase.getTranslator());
                    result.put("changeTime", playerPhrase.getChangeTime());

                    if (playerPhrase.getTranslatedTexts() != null && !playerPhrase.getTranslatedTexts().isEmpty()) {
                        JSONArray translations = new JSONArray();
                        for (TranslatedText t : playerPhrase.getTranslatedTexts()) {
                            JSONObject translatedText = new JSONObject();
                            translatedText.put("id", t.getId());
                            translatedText.put("text", t.getText());
                            translatedText.put("author", t.getAuthor());
                            translatedText.put("status", t.getStatus().toString());
                            translatedText.put("changeTime", t.getChangeTime());
                            Boolean canEdit = false;
                            if (Objects.equals(t.getAuthor().getId(), user.getId())) {
                                canEdit = true;
                            } else if (user.getRoles().contains(new SysAccountRole(4L))) {
                                canEdit = true;
                            }
                            translatedText.put("canEdit", canEdit);
                            translations.put(translatedText);
                        }
                        result.put("translations", translations);
                    }
                    break;
                case getNpcPhraseInfo:
                    npcPhraseId = Long.valueOf(idString);
                    npcPhrase = npcPhraseRepository.getOne(npcPhraseId);
                    result.put("id", npcPhrase.getId());
                    result.put("textEn", npcPhrase.getTextEn());
                    result.put("textRu", npcPhrase.getTextRu());
                    result.put("translator", npcPhrase.getTranslator());
                    result.put("changeTime", npcPhrase.getChangeTime());

                    if (npcPhrase.getTranslatedTexts() != null && !npcPhrase.getTranslatedTexts().isEmpty()) {
                        JSONArray translations = new JSONArray();
                        for (TranslatedText t : npcPhrase.getTranslatedTexts()) {
                            JSONObject translatedText = new JSONObject();
                            translatedText.put("id", t.getId());
                            translatedText.put("text", t.getText());
                            translatedText.put("author", t.getAuthor());
                            translatedText.put("status", t.getStatus().toString());
                            translatedText.put("changeTime", t.getChangeTime());
                            Boolean canEdit = false;
                            if (Objects.equals(t.getAuthor().getId(), user.getId())) {
                                canEdit = true;
                            } else if (user.getRoles().contains(new SysAccountRole(4L))) {
                                canEdit = true;
                            }
                            translatedText.put("canEdit", canEdit);
                            translations.put(translatedText);
                        }
                        result.put("translations", translations);
                    }
                    break;
                case editTranslation:
                    Long translationId = Long.valueOf(idString);
                    TranslatedText tt = translatedTextRepository.getOne(translationId);
                    tt.setText(text);
                    tt.setChangeTime(new Date());
                    tt.setStatus(TRANSLATE_STATUS.EDITED);
                    translatedTextRepository.saveAndFlush(tt);
                    result.put("result", Boolean.TRUE);
                    break;
                case newNpcPhraseTranslation:
                    npcPhraseId = Long.valueOf(idString);
                    npcPhrase = npcPhraseRepository.getOne(npcPhraseId);
                    TranslatedText npcTranslatedText = new TranslatedText();
                    npcTranslatedText.setText(text);
                    npcTranslatedText.setAuthor(user);
                    npcTranslatedText.setStatus(TRANSLATE_STATUS.EDITED);
                    npcTranslatedText.setSpreadSheetsNpcPhrase(npcPhrase);
                    npcTranslatedText.setCreateTime(new Date());
                    translatedTextRepository.saveAndFlush(npcTranslatedText);
                    result.put("result", Boolean.TRUE);
                    break;
                case newPlayerPhraseTranslation:
                    playerPhraseId = Long.valueOf(idString);
                    playerPhrase = playerPhraseRepository.getOne(playerPhraseId);
                    TranslatedText playerTranslatedText = new TranslatedText();
                    playerTranslatedText.setText(text);
                    playerTranslatedText.setAuthor(user);
                    playerTranslatedText.setStatus(TRANSLATE_STATUS.EDITED);
                    playerTranslatedText.setSpreadSheetsPlayerPhrase(playerPhrase);
                    playerTranslatedText.setCreateTime(new Date());
                    translatedTextRepository.saveAndFlush(playerTranslatedText);
                    result.put("result", Boolean.TRUE);
                    break;
            }

            return result.toString();
        } catch (Exception ex) {
            LOG.info(ex.getMessage());
            return result.toString();
        }
    }

    private enum REQUEST_TYPE {

        getPlayerPhraseInfo,
        getNpcPhraseInfo,
        editTranslation,
        newPlayerPhraseTranslation,
        newNpcPhraseTranslation;

    }
}
