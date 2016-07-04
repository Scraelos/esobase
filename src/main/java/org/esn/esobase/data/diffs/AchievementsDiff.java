/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.diffs;

import org.esn.esobase.data.SYNC_TYPE;
import org.esn.esobase.model.GSpreadSheetsAchievement;

/**
 *
 * @author scraelos
 */
public class AchievementsDiff {

    private final GSpreadSheetsAchievement spreadsheetsName;
    private final GSpreadSheetsAchievement dbName;
    private final SYNC_TYPE syncType;

    public AchievementsDiff(GSpreadSheetsAchievement spreadsheetsPhrase, GSpreadSheetsAchievement dbPhrase, SYNC_TYPE syncType) {
        this.spreadsheetsName = spreadsheetsPhrase;
        this.dbName = dbPhrase;
        this.syncType = syncType;
    }

    public GSpreadSheetsAchievement getSpreadsheetsName() {
        return spreadsheetsName;
    }

    public GSpreadSheetsAchievement getDbName() {
        return dbName;
    }

    public SYNC_TYPE getSyncType() {
        return syncType;
    }

}
