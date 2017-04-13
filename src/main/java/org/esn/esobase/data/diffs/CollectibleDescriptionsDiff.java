/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsCollectibleDescription;

/**
 *
 * @author scraelos
 */
public class CollectibleDescriptionsDiff {

    private final GSpreadSheetsCollectibleDescription spreadsheetsName;
    private final GSpreadSheetsCollectibleDescription dbName;
    private final SYNC_TYPE syncType;

    public CollectibleDescriptionsDiff(GSpreadSheetsCollectibleDescription spreadsheetsPhrase, GSpreadSheetsCollectibleDescription dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsCollectibleDescription getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsCollectibleDescription getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
