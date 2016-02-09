/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;

/**
 *
 * @author scraelos
 */
public class PlayerPhraseDiff {

    private final GSpreadSheetsPlayerPhrase spreadsheetsPhrase;
    private final GSpreadSheetsPlayerPhrase dbPhrase;
    private final SYNC_TYPE syncType;

    public PlayerPhraseDiff(GSpreadSheetsPlayerPhrase spreadsheetsPhrase, GSpreadSheetsPlayerPhrase dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsPhrase = spreadsheetsPhrase;
        this.dbPhrase = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsPlayerPhrase getSpreadsheetsPhrase() {
        return spreadsheetsPhrase;
    }

    public GSpreadSheetsPlayerPhrase getDbPhrase() {
        return dbPhrase;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
