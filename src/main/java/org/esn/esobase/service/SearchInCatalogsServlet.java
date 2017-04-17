/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.service;

import com.vaadin.v7.data.util.HierarchicalContainer;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.esn.esobase.data.DBService;
import org.esn.esobase.model.EsoInterfaceVariable;
import org.esn.esobase.model.GSpreadSheetsAbilityDescription;
import org.esn.esobase.model.GSpreadSheetsAchievement;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.TranslatedEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 *
 * @author scraelos
 */
public class SearchInCatalogsServlet extends HttpServlet {

    @Autowired
    DBService service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String requestString = req.getParameter("searchtext");
        if (requestString != null && requestString.trim().length() > 2) {
            HierarchicalContainer hc = new HierarchicalContainer();
            List<TranslatedEntity> searchInCatalogs = service.searchInCatalogs(requestString);
            JSONArray resultList = new JSONArray();
            for (TranslatedEntity e : searchInCatalogs) {
                JSONObject o = new JSONObject();
                o.put("textEn", e.getTextEn());
                o.put("textRu", e.getTextRu());
                String tableName = null;
                if (e instanceof GSpreadSheetsItemName) {
                    tableName = "Предмет";
                } else if (e instanceof GSpreadSheetsNpcName) {
                    tableName = "NPC";
                } else if (e instanceof GSpreadSheetsLocationName) {
                    tableName = "Локация";
                } else if (e instanceof GSpreadSheetsQuestName) {
                    tableName = "Квест";
                } else if (e instanceof GSpreadSheetsAchievement) {
                    tableName = "Достижение";
                } else if (e instanceof GSpreadSheetsAbilityDescription) {
                    tableName = "Описание способности";
                } else if (e instanceof EsoInterfaceVariable) {
                    tableName = "Строка интерфейса";
                }
                o.put("tableName", tableName);
                resultList.put(o);
            }
            resp.setContentType("text/plain; charset=UTF-8");
            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("parseResponse(").append(resultList.toString()).append(");");
            resp.getWriter().print(responseBuilder.toString());
        } else {
            resp.sendError(502, "Bad Request");
        }

    }

}
