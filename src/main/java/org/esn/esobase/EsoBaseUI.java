/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.EnableVaadinNavigation;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoaderListener;

/**
 *
 * @author scraelos
 */
@Component
@Scope("prototype")
@Theme("tests-valo-reindeer")
//@Theme("valo")
@PreserveOnRefresh
@SpringUI
public class EsoBaseUI extends UI {

    @Autowired
    private transient ApplicationContext applicationContext;

    @Override
    protected void init(final VaadinRequest request) {
        setSizeFull();
        setContent(springViewDisplay);

    }

    @WebListener
    public static class SpringContextLoaderListener extends ContextLoaderListener {
    }

    @Configuration
    @EnableVaadin
    @EnableVaadinNavigation
    public static class MyConfiguration {
    }

    @WebServlet(urlPatterns = "/*", name = "ESOBase", asyncSupported = true)
    @VaadinServletConfiguration(ui = EsoBaseUI.class, productionMode = true)
    public static class MyUIServlet extends SpringVaadinServlet {
    }

    @Autowired
    private MainViewDisplay springViewDisplay;

}
