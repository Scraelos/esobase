/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsLoadscreen;

/**
 *
 * @author scraelos
 */
public class LoadscreensDiff {

    private final GSpreadSheetsLoadscreen spreadsheetsName;
    private final GSpreadSheetsLoadscreen dbName;
    private final SYNC_TYPE syncType;

    public LoadscreensDiff(GSpreadSheetsLoadscreen spreadsheetsPhrase, GSpreadSheetsLoadscreen dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsLoadscreen getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsLoadscreen getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
