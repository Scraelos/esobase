/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase;

import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author scraelos
 */
@SuppressWarnings("WeakerAccess")
@SpringViewDisplay
public class MainViewDisplay extends Panel {

    public MainViewDisplay() {
        setSizeFull();
        this.setStyleName(ValoTheme.PANEL_BORDERLESS);
    }

}
