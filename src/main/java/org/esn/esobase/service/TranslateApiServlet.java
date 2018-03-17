/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.service;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 *
 * @author scraelos
 */
public class TranslateApiServlet extends HttpServlet {

    @Autowired
    private SysAccountService sysAccountService;
    @Autowired
    private GSpreadSheetsNpcPhraseRepository npcPhraseRepository;
    @Autowired
    private GSpreadSheetsPlayerPhraseRepository playerPhraseRepository;
    @Autowired
    private TranslatedTextRepository translatedTextRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setCharacterEncoding("UTF-8");
            JSONObject result = new JSONObject();
            String apiKey = req.getParameter("apikey");
            SysAccount user = sysAccountService.getAccountByApi(apiKey);
            if (!user.getRoles().contains(new SysAccountRole(3L))) {
                throw new Exception("access denied");
            }
            String reqTypeString = req.getParameter("req");
            REQUEST_TYPE requestType = REQUEST_TYPE.valueOf(reqTypeString);

            String npcPhraseIdString;
            Long npcPhraseId;
            GSpreadSheetsNpcPhrase npcPhrase;
            String text;
            String playerPhraseIdString;
            Long playerPhraseId;
            GSpreadSheetsPlayerPhrase playerPhrase;
            switch (requestType) {
                case getPlayerPhraseInfo:
                    playerPhraseIdString = req.getParameter("id");
                    playerPhraseId = Long.valueOf(playerPhraseIdString);
                    playerPhrase = playerPhraseRepository.findOne(playerPhraseId);
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
                    npcPhraseIdString = req.getParameter("id");
                    npcPhraseId = Long.valueOf(npcPhraseIdString);
                    npcPhrase = npcPhraseRepository.findOne(npcPhraseId);
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
                    String translationIdString = req.getParameter("id");
                    Long translationId = Long.valueOf(translationIdString);
                    TranslatedText tt = translatedTextRepository.findOne(translationId);
                    text = req.getParameter("text");
                    tt.setText(text);
                    tt.setChangeTime(new Date());
                    tt.setStatus(TRANSLATE_STATUS.EDITED);
                    translatedTextRepository.saveAndFlush(tt);
                    result.put("result", Boolean.TRUE);
                    break;
                case newNpcPhraseTranslation:
                    npcPhraseIdString = req.getParameter("id");
                    npcPhraseId = Long.valueOf(npcPhraseIdString);
                    npcPhrase = npcPhraseRepository.findOne(npcPhraseId);
                    TranslatedText npcTranslatedText = new TranslatedText();
                    text = req.getParameter("text");
                    npcTranslatedText.setText(text);
                    npcTranslatedText.setAuthor(user);
                    npcTranslatedText.setStatus(TRANSLATE_STATUS.EDITED);
                    npcTranslatedText.setSpreadSheetsNpcPhrase(npcPhrase);
                    npcTranslatedText.setCreateTime(new Date());
                    translatedTextRepository.saveAndFlush(npcTranslatedText);
                    result.put("result", Boolean.TRUE);
                    break;
                case newPlayerPhraseTranslation:
                    playerPhraseIdString = req.getParameter("id");
                    playerPhraseId = Long.valueOf(playerPhraseIdString);
                    playerPhrase = playerPhraseRepository.findOne(playerPhraseId);
                    TranslatedText playerTranslatedText = new TranslatedText();
                    text = req.getParameter("text");
                    playerTranslatedText.setText(text);
                    playerTranslatedText.setAuthor(user);
                    playerTranslatedText.setStatus(TRANSLATE_STATUS.EDITED);
                    playerTranslatedText.setSpreadSheetsPlayerPhrase(playerPhrase);
                    playerTranslatedText.setCreateTime(new Date());
                    translatedTextRepository.saveAndFlush(playerTranslatedText);
                    result.put("result", Boolean.TRUE);
                    break;
            }

            resp.setContentType("text/plain; charset=UTF-8");
            resp.getWriter().print(result.toString());
        } catch (Exception ex) {
            Logger.getLogger(GetBooksServlet.class.getName()).log(Level.SEVERE, null, ex);
            resp.sendError(502, "Bad Request");
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
