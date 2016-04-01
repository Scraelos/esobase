/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import org.esn.esobase.model.GSpreadSheetsItemDescription;

/**
 *
 * @author scraelos
 */
public class ItemDescriptionsDiff {

    private final GSpreadSheetsItemDescription spreadsheetsName;
    private final GSpreadSheetsItemDescription dbName;
    private final SYNC_TYPE syncType;

    public ItemDescriptionsDiff(GSpreadSheetsItemDescription spreadsheetsPhrase, GSpreadSheetsItemDescription dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsItemDescription getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsItemDescription getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
