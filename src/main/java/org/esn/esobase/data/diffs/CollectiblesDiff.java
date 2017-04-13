/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsCollectible;

/**
 *
 * @author scraelos
 */
public class CollectiblesDiff {

    private final GSpreadSheetsCollectible spreadsheetsName;
    private final GSpreadSheetsCollectible dbName;
    private final SYNC_TYPE syncType;

    public CollectiblesDiff(GSpreadSheetsCollectible spreadsheetsPhrase, GSpreadSheetsCollectible dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsCollectible getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsCollectible getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
