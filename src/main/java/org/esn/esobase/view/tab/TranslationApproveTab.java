/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.v7.ui.VerticalLayout;
import org.esn.esobase.data.DBService;

/**
 *
 * @author scraelos
 */
public class TranslationApproveTab extends VerticalLayout {

    private final DBService service;

    public TranslationApproveTab(DBService service_) {
        this.service = service_;
    }

}
