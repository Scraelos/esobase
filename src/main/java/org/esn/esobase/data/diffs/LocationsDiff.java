/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsLocationName;

/**
 *
 * @author scraelos
 */
public class LocationsDiff {

    private final GSpreadSheetsLocationName spreadsheetsName;
    private final GSpreadSheetsLocationName dbName;
    private final SYNC_TYPE syncType;

    public LocationsDiff(GSpreadSheetsLocationName spreadsheetsPhrase, GSpreadSheetsLocationName dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsLocationName getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsLocationName getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
