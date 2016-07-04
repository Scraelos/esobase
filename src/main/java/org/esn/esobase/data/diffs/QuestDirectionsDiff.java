/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsQuestDirection;

/**
 *
 * @author scraelos
 */
public class QuestDirectionsDiff {

    private final GSpreadSheetsQuestDirection spreadsheetsName;
    private final GSpreadSheetsQuestDirection dbName;
    private final SYNC_TYPE syncType;

    public QuestDirectionsDiff(GSpreadSheetsQuestDirection spreadsheetsPhrase, GSpreadSheetsQuestDirection dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsQuestDirection getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsQuestDirection getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
