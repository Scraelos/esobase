/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.jobs;

import com.vaadin.v7.data.util.HierarchicalContainer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.GoogleDocsService;
import org.esn.esobase.data.OriginalTextMismatchException;
import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.data.diffs.AbilityDescriptionsDiff;
import org.esn.esobase.model.GSpreadSheetsAbilityDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author scraelos
 */
public class SyncAbilityDescriptionsJob {

    @Autowired
    private DBService dbService;
    @Autowired
    private GoogleDocsService docsService;
    private static final Logger LOG = Logger.getLogger(SyncAbilityDescriptionsJob.class.getName());
    private static final String TABLE_NAME = "ability descriptions";

    @Scheduled(fixedDelay = 1800000, initialDelay = 2000)
    public void execute() throws OriginalTextMismatchException {
        if (dbService.getIsAutoSynchronizationEnabled()) {
            LOG.info(TABLE_NAME+": automatic sync enabled");
            HierarchicalContainer hc = new HierarchicalContainer();
            hc.addContainerProperty("shText", String.class, null);
            hc.addContainerProperty("shNic", String.class, null);
            hc.addContainerProperty("shDate", Date.class, null);
            hc.addContainerProperty("dbText", String.class, null);
            hc.addContainerProperty("dbNic", String.class, null);
            hc.addContainerProperty("dbDate", Date.class, null);
            hc.addContainerProperty("syncType", String.class, null);
            LOG.info("loading " + TABLE_NAME);
            List<GSpreadSheetsAbilityDescription> items = docsService.getAbilityDescriptions();
            LOG.info("making diff for " + TABLE_NAME);
            hc = dbService.getAbilityDescriptionsDiff(items, hc);
            List<AbilityDescriptionsDiff> diffs = (List<AbilityDescriptionsDiff>) hc.getItemIds();
            List<GSpreadSheetsAbilityDescription> itemsToSh = new ArrayList<>();
            List<GSpreadSheetsAbilityDescription> itemsToDb = new ArrayList<>();
            for (AbilityDescriptionsDiff diff : diffs) {
                if (diff.getSyncType() == SYNC_TYPE.TO_SPREADSHEET) {
                    itemsToSh.add(diff.getDbName());
                } else if (diff.getSyncType() == SYNC_TYPE.TO_DB) {
                    itemsToDb.add(diff.getSpreadsheetsName());
                }
            }
            LOG.log(Level.INFO, "uploading {0}" + " " + TABLE_NAME, itemsToSh.size());
            docsService.uploadAbilityDescriptions(itemsToSh);
            LOG.log(Level.INFO, "saving to db {0}" + " " + TABLE_NAME, itemsToDb.size());
            dbService.saveAbilityDescriptions(itemsToDb);
            LOG.info("sync finished for "+TABLE_NAME);
            hc.removeAllItems();
        } else {
            LOG.info(TABLE_NAME + ": automatic sync disabled");
        }

    }
}
