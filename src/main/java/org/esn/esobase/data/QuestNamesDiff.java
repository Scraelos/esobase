/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import org.esn.esobase.model.GSpreadSheetsQuestName;

/**
 *
 * @author scraelos
 */
public class QuestNamesDiff {

    private final GSpreadSheetsQuestName spreadsheetsName;
    private final GSpreadSheetsQuestName dbName;
    private final SYNC_TYPE syncType;

    public QuestNamesDiff(GSpreadSheetsQuestName spreadsheetsPhrase, GSpreadSheetsQuestName dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsQuestName getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsQuestName getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
