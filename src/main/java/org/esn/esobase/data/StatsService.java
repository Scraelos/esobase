/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author scraelos
 */
public class StatsService {

    @PersistenceContext
    private EntityManager em;

    public void getStats(List<StatItem> result, LocalDate startDate, LocalDate endDate) {
        if (result == null) {
            result = new ArrayList<>();
        } else {
            result.clear();
        }
        endDate = endDate.plusDays(1);
        Query q = em.createNativeQuery("select login, sum(translated) as sum1, sum(corrected) as sum2,sum(approved) as sum3 from (select login,count(spacer) as translated,null\\:\\:bigint as corrected,null\\:\\:bigint as approved from (SELECT s.login, regexp_matches(t.text, '[A-Za-zА-Яа-я0-9\\-''^]+', 'g') as spacer FROM translatedtext t join sysaccount s on s.id=t.author_id where t.createtime>:dateStart and t.createtime<:dateEnd) as rr group by login union all\n"
                + "select login,null\\:\\:bigint as translated,count(spacer) as corrected,null\\:\\:bigint as approved from (SELECT s.login, regexp_matches(t.text, '[A-Za-zА-Яа-я0-9\\-''^]+', 'g') as spacer FROM translatedtext t join sysaccount s on s.id=t.correctedby_id where t.status='ACCEPTED' and t.apptovedtime>:dateStart and t.apptovedtime<:dateEnd) as rr group by login union all\n"
                + "select login,null\\:\\:bigint as translated,null\\:\\:bigint as corrected,count(spacer) as approved from (SELECT s.login, regexp_matches(t.text, '[A-Za-zА-Яа-я0-9\\-''^]+', 'g') as spacer FROM translatedtext t join sysaccount s on s.id=t.approvedby_id where t.status='ACCEPTED' and t.apptovedtime>:dateStart and t.apptovedtime<:dateEnd) as rr group by login) as rrr group by login order by login;");
        q.setParameter("dateStart", startDate);
        q.setParameter("dateEnd", endDate);
        List<Object[]> resultList = q.getResultList();
        for (Object[] row : resultList) {
            result.add(new StatItem((String) row[0], (BigDecimal) row[1], (BigDecimal) row[2], (BigDecimal) row[3]));
        }
    }

    public class StatItem {

        private final String login;
        private final BigDecimal translatedCount;
        private final BigDecimal corectedCount;
        private final BigDecimal approvedCount;

        public StatItem(String login, BigDecimal translatedCount, BigDecimal corectedCount, BigDecimal approvedCount) {
            this.login = login;
            this.translatedCount = translatedCount;
            this.corectedCount = corectedCount;
            this.approvedCount = approvedCount;
        }

        public String getLogin() {
            return login;
        }

        public BigDecimal getTranslatedCount() {
            return translatedCount;
        }

        public BigDecimal getCorectedCount() {
            return corectedCount;
        }

        public BigDecimal getApprovedCount() {
            return approvedCount;
        }

    }
}
