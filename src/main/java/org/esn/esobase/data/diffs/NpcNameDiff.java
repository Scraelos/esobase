/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsNpcName;

/**
 *
 * @author scraelos
 */
public class NpcNameDiff {

    private final GSpreadSheetsNpcName spreadsheetsName;
    private final GSpreadSheetsNpcName dbName;
    private final SYNC_TYPE syncType;

    public NpcNameDiff(GSpreadSheetsNpcName spreadsheetsPhrase, GSpreadSheetsNpcName dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsNpcName getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsNpcName getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
