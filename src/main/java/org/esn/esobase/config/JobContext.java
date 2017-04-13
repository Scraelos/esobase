package org.esn.esobase.config;

import org.esn.esobase.jobs.SyncAbilityDescriptionsJob;
import org.esn.esobase.jobs.SyncAchievementDescriptionsJob;
import org.esn.esobase.jobs.SyncAchievementsJob;
import org.esn.esobase.jobs.SyncActivatorsJob;
import org.esn.esobase.jobs.SyncCollectibleDescriptionsJob;
import org.esn.esobase.jobs.SyncCollectiblesJob;
import org.esn.esobase.jobs.SyncItemDescriptionsJob;
import org.esn.esobase.jobs.SyncItemNamesJob;
import org.esn.esobase.jobs.SyncJournalEntriesJob;
import org.esn.esobase.jobs.SyncLoadscreensJob;
import org.esn.esobase.jobs.SyncLocationNamesJob;
import org.esn.esobase.jobs.SyncNotesJob;
import org.esn.esobase.jobs.SyncNpcNamesJob;
import org.esn.esobase.jobs.SyncNpcPhrasesJob;
import org.esn.esobase.jobs.SyncPlayerPhrasesJob;
import org.esn.esobase.jobs.SyncQuestDescriptionsJob;
import org.esn.esobase.jobs.SyncQuestDirectionsJob;
import org.esn.esobase.jobs.SyncQuestNamesJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA. User: Solverit Date: 10.05.13 Time: 23:31
 */
@Configuration
public class JobContext {

    @Bean
    public SyncQuestNamesJob syncQuestNamesJob() {
        return new SyncQuestNamesJob();
    }

    @Bean
    public SyncQuestDescriptionsJob syncQuestDescriptionsJob() {
        return new SyncQuestDescriptionsJob();
    }

    @Bean
    public SyncNpcNamesJob syncNpcNamesJob() {
        return new SyncNpcNamesJob();
    }

    @Bean
    public SyncLocationNamesJob syncLocationNamesJob() {
        return new SyncLocationNamesJob();
    }

    @Bean
    public SyncPlayerPhrasesJob syncPlayerPhrasesJob() {
        return new SyncPlayerPhrasesJob();
    }

    @Bean
    public SyncNpcPhrasesJob syncNpcPhrasesJob() {
        return new SyncNpcPhrasesJob();
    }

    @Bean
    public SyncActivatorsJob syncActivatorsJob() {
        return new SyncActivatorsJob();
    }

    @Bean
    public SyncJournalEntriesJob syncJournalEntriesJob() {
        return new SyncJournalEntriesJob();
    }

    @Bean
    public SyncItemNamesJob syncItemNamesJob() {
        return new SyncItemNamesJob();
    }

    @Bean
    public SyncItemDescriptionsJob syncItemDescriptionsJob() {
        return new SyncItemDescriptionsJob();
    }

    @Bean
    public SyncQuestDirectionsJob syncQuestDirectionsJob() {
        return new SyncQuestDirectionsJob();
    }

    @Bean
    public SyncAchievementsJob syncAchievementsJob() {
        return new SyncAchievementsJob();
    }

    @Bean
    public SyncAchievementDescriptionsJob syncAchievementDescriptionsJob() {
        return new SyncAchievementDescriptionsJob();
    }

    @Bean
    public SyncNotesJob syncNotesJob() {
        return new SyncNotesJob();
    }

    @Bean
    public SyncAbilityDescriptionsJob syncAbilityDescriptionsJob() {
        return new SyncAbilityDescriptionsJob();
    }

    @Bean
    public SyncCollectiblesJob syncCollectiblesJob() {
        return new SyncCollectiblesJob();
    }

    @Bean
    public SyncCollectibleDescriptionsJob syncCollectibleDescriptionsJob() {
        return new SyncCollectibleDescriptionsJob();
    }

    @Bean
    public SyncLoadscreensJob syncLoadscreensJob() {
        return new SyncLoadscreensJob();
    }
}
