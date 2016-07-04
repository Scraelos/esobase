/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsItemName;

/**
 *
 * @author scraelos
 */
public class ItemNamesDiff {

    private final GSpreadSheetsItemName spreadsheetsName;
    private final GSpreadSheetsItemName dbName;
    private final SYNC_TYPE syncType;

    public ItemNamesDiff(GSpreadSheetsItemName spreadsheetsPhrase, GSpreadSheetsItemName dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsItemName getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsItemName getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
