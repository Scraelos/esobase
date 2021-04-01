/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.esn.esobase.data.DBService;
import org.esn.esobase.model.Book;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author scraelos
 */
@Controller
@RequestMapping("getbooks")
public class GetBooksServlet {

    @Autowired
    DBService service;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @GetMapping(produces = "application/json")
    public @ResponseBody
    String getBook(@RequestParam(name = "fromDate") String requestString) {
        JSONArray resultList = new JSONArray();
        try {
            Date fromDate = sdf.parse(requestString);
            List<Book> books = service.getBooksFromDate(fromDate);

            for (Book b : books) {
                JSONObject o = new JSONObject();
                o.put("id", b.getcId());
                o.put("bookName", b.getNameRu());
                o.put("bookText", b.getBookText().getTextRu());
                resultList.put(o);
            }

        } catch (ParseException ex) {
            Logger.getLogger(GetBooksServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultList.toString();
    }
}
