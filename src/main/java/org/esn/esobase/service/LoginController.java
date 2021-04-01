/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author scraelos
 */
@Controller
@RequestMapping(value = "/login", method = RequestMethod.GET)
public class LoginController {

    @GetMapping(produces = "text/html")
    public @ResponseBody
    String loginPage() {
        return "<html>\n"
                + "<head>\n"
                + "    <style type=\"text/css\">\n"
                + "        body {\n"
                + "            height: 100%;\n"
                + "        }\n"
                + "\n"
                + "        body > table {\n"
                + "            width: 100%;\n"
                + "            height: 100%;\n"
                + "        }\n"
                + "\n"
                + "        body > table > tbody > tr > td\n"
                + "        {\n"
                + "            text-align: center;\n"
                + "        }\n"
                + "\n"
                + "        form > table\n"
                + "        {\n"
                + "            margin-left:auto;\n"
                + "            margin-right:auto;\n"
                + "        }\n"
                + "\n"
                + "        .error\n"
                + "        {\n"
                + "            font-weight: bold;\n"
                + "            color: red;\n"
                + "        }\n"
                + "    </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "<table>\n"
                + "    <tr>\n"
                + "        <td>\n"
                + "            <form method=\"post\" action=\"../login\">\n"
                + "                <table>\n"
                + "                    <tr>\n"
                + "                        <td>Логин</td>\n"
                + "                        <td><input type=\"text\" name=\"username\"/></td>\n"
                + "                    </tr>\n"
                + "                    <tr>\n"
                + "                        <td>Пароль</td>\n"
                + "                        <td><input type=\"password\" name=\"password\"/></td>\n"
                + "                    </tr>\n"
                + "                    <tr>\n"
                + "                        <td>Запомнить</td>\n"
                + "                        <td><input type=\"checkbox\" name=\"remember-me\"/></td>\n"
                + "                    </tr>\n"
                + "                    <tr>\n"
                + "                        <td>&nbsp;</td>\n"
                + "                        <td><input type=\"submit\" value=\"Вход\"></td>\n"
                + "                    </tr>\n"
                + "                </table>\n"
                + "            </form>\n"
                + "\n"
                + "            \n"
                + "        </td>\n"
                + "    </tr>\n"
                + "</table>\n"
                + "</body>\n"
                + "</html>";
    }
}
