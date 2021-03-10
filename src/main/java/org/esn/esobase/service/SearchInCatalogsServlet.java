/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.service;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanComparator;
import org.esn.esobase.data.SearchService;
import org.esn.esobase.model.EsoRawString;
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
    SearchService service;

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
            List<EsoRawString> searchInCatalogs = service.searchInRawRuoff(requestString, Boolean.FALSE);
            JSONArray resultList = new JSONArray();
            for (EsoRawString e : searchInCatalogs) {
                JSONObject o = new JSONObject();
                o.put("textEn", e.getTextEn());
                o.put("textRu", e.getTextRu());
                o.put("textRuOff", e.getTextRuoff());
                String tableName = null;
                if (e.getaId() == 124362421 || e.getaId() == 242841733 || e.getaId() == 267697733) {
                    tableName = "Предмет";
                } else if (e.getaId() == 8290981 || e.getaId() == 51188660 || e.getaId() == 191999749 || e.getaId() == 33425332) {
                    tableName = "NPC";
                } else if (e.getaId() == 10860933 || e.getaId() == 146361138 || e.getaId() == 162946485 || e.getaId() == 162658389 || e.getaId() == 164009093 || e.getaId() == 267200725 || e.getaId() == 28666901 || e.getaId() == 81344020 || e.getaId() == 268015829 || e.getaId() == 111863941 || e.getaId() == 157886597) {
                    tableName = "Локация";
                } else if (e.getaId() == 52420949) {
                    tableName = "Квест";
                } else if (e.getaId() == 12529189 || e.getaId() == 172030117) {
                    tableName = "Достижение";
                } else if (e.getaId() == 18173141 || e.getaId() == 70328405 || e.getaId() == 160914197 || e.getaId() == 245765621 || e.getaId() == 213229525 || e.getaId() == 204530069 || e.getaId() == 42041397) {
                    tableName = "Коллекционный предмет";
                } else if (e.getaId() == 198758357 || e.getaId() == 17915077) {
                    tableName = "Способность";
                } else if (e.getaId() == 51188213) {
                    tableName = "Книга";
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
