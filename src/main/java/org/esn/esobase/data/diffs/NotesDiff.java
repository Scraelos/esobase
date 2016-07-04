/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsNote;

/**
 *
 * @author scraelos
 */
public class NotesDiff {

    private final GSpreadSheetsNote spreadsheetsName;
    private final GSpreadSheetsNote dbName;
    private final SYNC_TYPE syncType;

    public NotesDiff(GSpreadSheetsNote spreadsheetsPhrase, GSpreadSheetsNote dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsNote getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsNote getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
