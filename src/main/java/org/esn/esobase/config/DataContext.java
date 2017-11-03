/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.config;

import org.esn.esobase.data.DBService;
import org.esn.esobase.data.GoogleDocsService;
import org.esn.esobase.data.InsertExecutor;
import org.esn.esobase.data.ItemInfoImportService;
import org.esn.esobase.data.TableUpdateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author scraelos
 */
@Configuration
public class DataContext {

    @Bean
    public DBService dBService() {
        return new DBService();
    }

    @Bean
    public GoogleDocsService googleDocsService() {
        return new GoogleDocsService();
    }

    @Bean
    public InsertExecutor insertExecutor() {
        return new InsertExecutor();
    }

    @Bean
    public TableUpdateService tableUpdateService() {
        return new TableUpdateService();
    }

    @Bean
    public ItemInfoImportService itemInfoImportService() {
        return new ItemInfoImportService();
    }
}
