/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.ContextLoaderListener;

/**
 *
 * @author scraelos
 */
@Scope("prototype")
@Theme("tests-valo-reindeer")
@SpringUI
@PreserveOnRefresh
public class EsoBaseUI extends UI {

    @WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    }

    @Configuration
    @EnableVaadin
    public static class MyConfiguration {
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = EsoBaseUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

    @Autowired
    SpringViewProvider viewProvider;

    @Autowired
    private transient ApplicationContext applicationContext;
    Navigator navigator;

    @Override
    protected void init(final VaadinRequest request) {
        setSizeFull();
        // Create a navigator to control the views
        navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);

        // Create and register the views
        navigator.navigateTo("");

    }

}
