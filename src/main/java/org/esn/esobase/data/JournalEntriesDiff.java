/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import org.esn.esobase.model.GSpreadSheetsJournalEntry;

/**
 *
 * @author scraelos
 */
public class JournalEntriesDiff {

    private final GSpreadSheetsJournalEntry spreadsheetsName;
    private final GSpreadSheetsJournalEntry dbName;
    private final SYNC_TYPE syncType;

    public JournalEntriesDiff(GSpreadSheetsJournalEntry spreadsheetsPhrase, GSpreadSheetsJournalEntry dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsJournalEntry getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsJournalEntry getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
