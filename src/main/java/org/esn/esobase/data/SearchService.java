/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.esn.esobase.model.EsoRawString;
import org.esn.esobase.model.GSpreadSheetEntity;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.NPC_SEX;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author scraelos
 */
@Service
public class SearchService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public List<GSpreadSheetEntity> searchInCatalogs(List<String> tableNames, String searchString, Boolean withTranslatedNeighbours, Integer numberOfNeighbours, Boolean isRegexp) {
        List<GSpreadSheetEntity> result = new ArrayList<>();
        if ((searchString != null && searchString.length() > 2) || withTranslatedNeighbours) {
            for (String tableName : tableNames) {
                boolean firstWhere = true;
                StringBuilder sqlBuilder = new StringBuilder();
                if (withTranslatedNeighbours) {
                    sqlBuilder.append("select * from (");
                }
                sqlBuilder.append("select g.id, g.texten,g.textru,g.rownum,g.translator");
                if (tableName.equals("GSpreadSheetsNpcName")) {
                    sqlBuilder.append(",g.sex");
                }
                if (withTranslatedNeighbours) {
                    sqlBuilder.append(", (select count(*) from(select pg.id,pg.texten,pg.textru from ").append(tableName).append(" pg where pg.rownum<g.rownum order by pg.rownum DESC LIMIT ").append(numberOfNeighbours).append(" ) as prev where texten!=textru) as cnt_prev, (select count(*) from(select ng.id,ng.texten,ng.textru from ").append(tableName).append(" ng where ng.rownum>g.rownum order by ng.rownum ASC LIMIT ").append(numberOfNeighbours).append(" ) as nnext where texten!=textru) as cnt_next");
                }
                sqlBuilder.append(" from ").append(tableName).append(" g");
                if (searchString != null && searchString.length() > 2) {
                    if (isRegexp) {
                        addPredicate(sqlBuilder, " (g.texten ~ :searchString or g.textru ~ :searchString)", firstWhere);
                        firstWhere = false;
                    } else {
                        addPredicate(sqlBuilder, " (g.texten ilike :searchString or g.textru ilike :searchString)", firstWhere);
                        firstWhere = false;
                    }

                }
                if (withTranslatedNeighbours) {
                    addPredicate(sqlBuilder, " g.texten=g.textru", firstWhere);
                    firstWhere = false;
                }
                addPredicate(sqlBuilder, " g.deprecated=false", firstWhere);
                sqlBuilder.append(" order by rownum");
                if (withTranslatedNeighbours) {
                    sqlBuilder.append(") as countneighbours where cnt_prev=").append(numberOfNeighbours.toString()).append(" and cnt_next=").append(numberOfNeighbours.toString()).append(";");

                }
                Query nativeQuery = em.createNativeQuery(sqlBuilder.toString());
                if (searchString != null && searchString.length() > 2) {
                    if (isRegexp) {
                        nativeQuery.setParameter("searchString", searchString);
                    } else {
                        nativeQuery.setParameter("searchString", "%" + searchString + "%");
                    }
                }
                List<Object[]> resultList = nativeQuery.getResultList();
                for (Object[] o : resultList) {
                    BigInteger id = (BigInteger) o[0];
                    String textEn = (String) o[1];
                    String textRu = (String) o[2];
                    BigInteger rowNum = (BigInteger) o[3];
                    String translator = (String) o[4];
                    try {
                        Class<?> clazz = Class.forName("org.esn.esobase.model." + tableName);
                        Constructor<?> ctor = clazz.getConstructor();
                        GSpreadSheetEntity entity = (GSpreadSheetEntity) ctor.newInstance(new Object[]{});
                        entity.setId(id.longValue());
                        entity.setRowNum(rowNum.longValue());
                        entity.setTextEn(textEn);
                        entity.setTextRu(textRu);
                        entity.setTranslator(translator);
                        if (tableName.equals("GSpreadSheetsNpcName")) {
                            String sexString = (String) o[5];
                            if (sexString != null && !sexString.isEmpty()) {
                                NPC_SEX sex = NPC_SEX.valueOf(sexString);
                                GSpreadSheetsNpcName nameEntity = (GSpreadSheetsNpcName) entity;
                                nameEntity.setSex(sex);
                            }
                        }
                        result.add(entity);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchMethodException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SecurityException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InstantiationException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        return result;
    }

    @Transactional
    public List<EsoRawString> searchInRawRuoff(String searchString, Boolean isRegexp) {
        List<EsoRawString> result = new ArrayList<>();
        if ((searchString != null && searchString.length() > 2)) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select g.aid,regexp_replace(g.texten,'\\^[FfMmNn].*','') as texten,regexp_replace(g.textru,'\\^[FfMmNn].*','') as textru,regexp_replace(g.textruoff,'\\^[FfMmNn].*','') as textruoff ");
            sqlBuilder.append(" from EsoRawString g where g.id in(");
            sqlBuilder.append(" select min(gg.id) from EsoRawString gg");
            sqlBuilder.append(" where gg.aid in("
                    + "198758357,17915077,"//abilities
                    + "124362421,242841733,267697733,"//items
                    + "10860933, 146361138, 162946485, 162658389, 164009093, 267200725, 28666901, 81344020, 268015829, 111863941, 157886597,"//locations
                    + "52420949,"//quests
                    + "51188213,"//books
                    + "12529189,172030117,"//achievements
                    + "18173141,70328405,160914197,245765621,213229525,204530069,42041397,"//collectibles
                    + "8290981,51188660,191999749,33425332"//npc
                    + ")");
            if (isRegexp) {
                sqlBuilder.append(" and (gg.texten ~ :searchString or gg.textruoff ~ :searchString or gg.textru ~ :searchString)");
            } else {
                sqlBuilder.append(" and (gg.texten ilike :searchString or gg.textruoff ilike :searchString or gg.textru ilike :searchString)");
            }
            sqlBuilder.append(" group by gg.texten) order by g.aid,g.texten");

            Query nativeQuery = em.createNativeQuery(sqlBuilder.toString());
            if (isRegexp) {
                nativeQuery.setParameter("searchString", searchString);
            } else {
                nativeQuery.setParameter("searchString", "%" + searchString + "%");
            }
            List<Object[]> resultList = nativeQuery.getResultList();
            for (Object[] o : resultList) {
                BigInteger aid = (BigInteger) o[0];
                String textEn = (String) o[1];
                String textRu = (String) o[2];
                String textRuOff = (String) o[3];
                EsoRawString s = new EsoRawString();
                s.setaId(aid.longValue());
                s.setTextEn(textEn);
                s.setTextRu(textRu);
                s.setTextRuoff(textRuOff);
                result.add(s);
            }
        }

        return result;
    }

    private void addPredicate(StringBuilder sqlBuilder, String predicate, boolean firstWhere) {
        if (firstWhere) {
            sqlBuilder.append(" where");
        } else {
            sqlBuilder.append(" and");
        }
        sqlBuilder.append(predicate);
    }
}
