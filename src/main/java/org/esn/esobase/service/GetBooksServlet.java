/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.esn.esobase.data.DBService;
import org.esn.esobase.model.Book;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 *
 * @author scraelos
 */
public class GetBooksServlet extends HttpServlet {

    @Autowired
    DBService service;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

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
            String requestString = req.getParameter("fromDate");
            Date fromDate = sdf.parse(requestString);
            List<Book> books = service.getBooksFromDate(fromDate);
            JSONArray resultList = new JSONArray();
            for (Book b : books) {
                JSONObject o = new JSONObject();
                o.put("id", b.getcId());
                o.put("bookName", b.getNameRu());
                o.put("bookText", b.getBookText().getTextRu());
                resultList.put(o);
            }
            resp.setContentType("text/plain; charset=UTF-8");
            resp.getWriter().print(resultList.toString());
        } catch (ParseException ex) {
            Logger.getLogger(GetBooksServlet.class.getName()).log(Level.SEVERE, null, ex);
            resp.sendError(502, "Bad Request");
        }
    }
}
