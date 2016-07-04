/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsAchievementDescription;

/**
 *
 * @author scraelos
 */
public class AchievementDescriptionsDiff {

    private final GSpreadSheetsAchievementDescription spreadsheetsName;
    private final GSpreadSheetsAchievementDescription dbName;
    private final SYNC_TYPE syncType;

    public AchievementDescriptionsDiff(GSpreadSheetsAchievementDescription spreadsheetsPhrase, GSpreadSheetsAchievementDescription dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsAchievementDescription getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsAchievementDescription getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
