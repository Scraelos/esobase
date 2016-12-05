/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsAbilityDescription;

/**
 *
 * @author scraelos
 */
public class AbilityDescriptionsDiff {

    private final GSpreadSheetsAbilityDescription spreadsheetsName;
    private final GSpreadSheetsAbilityDescription dbName;
    private final SYNC_TYPE syncType;

    public AbilityDescriptionsDiff(GSpreadSheetsAbilityDescription spreadsheetsPhrase, GSpreadSheetsAbilityDescription dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsAbilityDescription getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsAbilityDescription getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
