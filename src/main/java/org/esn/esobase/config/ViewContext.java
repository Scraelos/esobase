/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.config;

import org.esn.esobase.view.tab.TranslateTab;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author scraelos
 */
@Configuration
public class ViewContext {

    @Bean
    public TranslateTab translateTab() {
        return new TranslateTab();
    }
}
