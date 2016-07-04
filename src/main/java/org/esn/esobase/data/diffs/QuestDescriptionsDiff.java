/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsQuestDescription;

/**
 *
 * @author scraelos
 */
public class QuestDescriptionsDiff {

    private final GSpreadSheetsQuestDescription spreadsheetsName;
    private final GSpreadSheetsQuestDescription dbName;
    private final SYNC_TYPE syncType;

    public QuestDescriptionsDiff(GSpreadSheetsQuestDescription spreadsheetsPhrase, GSpreadSheetsQuestDescription dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsQuestDescription getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsQuestDescription getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
