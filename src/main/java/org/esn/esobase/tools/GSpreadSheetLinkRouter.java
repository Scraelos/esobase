/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.tools;

import java.util.HashMap;
import java.util.Map;
import org.esn.esobase.model.GSpreadSheetsAchievement;
import org.esn.esobase.model.GSpreadSheetsAchievementDescription;
import org.esn.esobase.model.GSpreadSheetsActivator;
import org.esn.esobase.model.GSpreadSheetsItemDescription;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.GSpreadSheetsQuestDescription;
import org.esn.esobase.model.GSpreadSheetsQuestDirection;
import org.esn.esobase.model.GSpreadSheetsQuestName;

/**
 *
 * @author scraelos
 */
public class GSpreadSheetLinkRouter {

    private final static Map<Long, RouteEntry> routeMap;

    static {
        routeMap = new HashMap<>();
        ////активаторы: названия - действия
        //links[87370069] = 74865733;
        //routeMap.put(null, new RouteEntry(null, null));
        //links[19398485] = 70307621;
        //routeMap.put(null, new RouteEntry(null, null));
        //links[207758933] = 70307621;
        //routeMap.put(null, new RouteEntry(null, null));
        //
        ////достижения: названия - описания
        //links[12529189] = 188155806;
        routeMap.put(12529189L, new RouteEntry(188155806L, GSpreadSheetsAchievementDescription.class));
        //
        ////достижения: описания - названия
        //links[188155806] = 12529189;
        routeMap.put(188155806L, new RouteEntry(12529189L, GSpreadSheetsAchievement.class));
        //
        ////загрузочные экраны: описания - названия
        //links[70901198] = 162658389;
        //routeMap.put(null, new RouteEntry(null, null));
        //
        ////задания: названия - описания
        //links[52420949] = 265851556;
        routeMap.put(52420949L, new RouteEntry(265851556L, GSpreadSheetsQuestDescription.class));
        //
        ////задания: описания - названия
        //links[265851556] = 52420949;
        routeMap.put(265851556L, new RouteEntry(52420949L, GSpreadSheetsQuestName.class));
        //
        ////коллекционные предметы: названия - описания
        //links[18173141] = 211640654;
        //routeMap.put(null, new RouteEntry(null, null));
        //links[70328405] = 263796174;
        //routeMap.put(null, new RouteEntry(null, null));
        //links[160914197] = 69169806;
        //routeMap.put(null, new RouteEntry(null, null));
        //links[245765621] = 86917166;
        //routeMap.put(null, new RouteEntry(null, null));
        //links[213229525] = 121484878;
        //routeMap.put(null, new RouteEntry(null, null));
        //
        ////коллекционные предметы: описания - названия
        //links[211640654] = 18173141;
        //routeMap.put(null, new RouteEntry(null, null));
        //links[263796174] = 70328405;
        //routeMap.put(null, new RouteEntry(null, null));
        //links[69169806] = 160914197;
        //routeMap.put(null, new RouteEntry(null, null));
        //links[86917166] = 245765621;
        //routeMap.put(null, new RouteEntry(null, null));
        //
        ////локации: названия - описания
        //links[162658389] = 70901198;
        //routeMap.put(null, new RouteEntry(null, null));
        //
        ////письма: описания - названия
        //links[219317028] = 191189508;
        //routeMap.put(null, new RouteEntry(null, null));
        //
        ////предметы: названия - описания
        //links[242841733] = 228378404;
        routeMap.put(242841733L, new RouteEntry(228378404L, GSpreadSheetsItemDescription.class));
        //links[267697733] = 139139780;
        routeMap.put(267697733L, new RouteEntry(139139780L, GSpreadSheetsItemDescription.class));
        //
        ////предметы: описания - названия
        //links[228378404] = 242841733;
        routeMap.put(228378404L, new RouteEntry(242841733L, GSpreadSheetsItemName.class));
        //links[139139780] = 267697733;
        routeMap.put(139139780L, new RouteEntry(267697733L, GSpreadSheetsItemName.class));
        //
        ////реплики игрока:
        ////-игрок - персонаж (фраза игрока предшествует фразе персонажа)
        //links[228103012] = 200879108;
        routeMap.put(228103012L, new RouteEntry(200879108L, GSpreadSheetsNpcPhrase.class));
        //links[249936564] = 3952276;
        routeMap.put(249936564L, new RouteEntry(3952276L, GSpreadSheetsNpcPhrase.class));
        //links[232026500] = 116521668;
        routeMap.put(232026500L, new RouteEntry(116521668L, GSpreadSheetsNpcPhrase.class));
        ////-игрок - цель задания
        //links[150525940] = 7949764;
        routeMap.put(150525940L, new RouteEntry(7949764L, GSpreadSheetsQuestDirection.class));
        //
        ////реплики персонажа:
        ////-персонаж - игрок (фраза игрока предшествует фразе персонажа)
        //links[200879108] = 228103012;
        routeMap.put(200879108L, new RouteEntry(228103012L, GSpreadSheetsPlayerPhrase.class));
        //links[3952276] = 249936564;
        routeMap.put(3952276L, new RouteEntry(249936564L, GSpreadSheetsPlayerPhrase.class));
        //links[116521668] = 232026500;
        routeMap.put(116521668L, new RouteEntry(232026500L, GSpreadSheetsPlayerPhrase.class));
        ////-персонаж - активатор
        //links[211899940] = 87370069;
        routeMap.put(211899940L, new RouteEntry(87370069L, GSpreadSheetsActivator.class));
        //
        ////способности: описания - названия
        //links[132143172] = 198758357;
        //routeMap.put(null, new RouteEntry(null, null));
        //
        ////цепочки заданий (завершенные): описания - названия
        //links[108566804] = 10860933;
        //routeMap.put(null, new RouteEntry(null, null));

        //
        ////цепочки заданий (начатые): описания - названия
        //links[129979412] = 10860933;
        //routeMap.put(null, new RouteEntry(null, null));
    }

    public static RouteEntry getRoute(Long aId) {
        RouteEntry result;
        result = routeMap.get(aId);
        return result;
    }

    public static class RouteEntry {

        public RouteEntry(Long targetId, Class targetClass) {
            this.targetId = targetId;
            this.targetClass = targetClass;
        }

        public Long getTargetId() {
            return targetId;
        }

        public Class getTargetClass() {
            return targetClass;
        }

        private final Long targetId;
        private final Class targetClass;
    }
}
