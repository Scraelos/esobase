/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import org.esn.esobase.model.GSpreadSheetsNpcPhrase;

/**
 *
 * @author scraelos
 */
public class NpcPhraseDiff {

    private final GSpreadSheetsNpcPhrase spreadsheetsPhrase;
    private final GSpreadSheetsNpcPhrase dbPhrase;
    private final SYNC_TYPE syncType;

    public NpcPhraseDiff(GSpreadSheetsNpcPhrase spreadsheetsPhrase, GSpreadSheetsNpcPhrase dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsPhrase = spreadsheetsPhrase;
        this.dbPhrase = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsNpcPhrase getSpreadsheetsPhrase() {
        return spreadsheetsPhrase;
    }

    public GSpreadSheetsNpcPhrase getDbPhrase() {
        return dbPhrase;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
