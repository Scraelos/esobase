/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import org.esn.esobase.model.GSpreadSheetsActivator;

/**
 *
 * @author scraelos
 */
public class ActivatorsDiff {

    private final GSpreadSheetsActivator spreadsheetsName;
    private final GSpreadSheetsActivator dbName;
    private final SYNC_TYPE syncType;

    public ActivatorsDiff(GSpreadSheetsActivator spreadsheetsPhrase, GSpreadSheetsActivator dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsActivator getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsActivator getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
