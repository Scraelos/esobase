/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.xpoft.vaadin.DiscoveryNavigator;

/**
 *
 * @author scraelos
 */
@Component
@Scope("prototype")
@Theme("tests-valo-reindeer")
@PreserveOnRefresh
public class EsoBaseUI extends UI {

    @Autowired
    private transient ApplicationContext applicationContext;

    @Override
    protected void init(final VaadinRequest request) {
        setSizeFull();

        DiscoveryNavigator navigator = new DiscoveryNavigator(this, this);

    }

}
