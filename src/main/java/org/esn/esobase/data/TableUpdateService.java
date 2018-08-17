/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author scraelos
 */
public class TableUpdateService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void updateAbilityDescriptions() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsabilitydescription g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(132143172) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsabilitydescription set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsabilitydescription.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsachievementdescription_id in(select g.id from gspreadsheetsachievementdescription g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsabilitydescription set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsabilitydescription.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsabilitydescription.aid and e.bid=gspreadsheetsabilitydescription.bid and e.cid=gspreadsheetsabilitydescription.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsabilitydescription (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsabilitydescription g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(132143172) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsabilitydescription set deprecated=TRUE where id in(select g.id from gspreadsheetsabilitydescription g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsabilitydescription g1 join gspreadsheetsabilitydescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsachievementdescription_id=newid.gid1 from newid where translatedtext.spreadsheetsachievementdescription_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsabilitydescription where id in (select max(g2.id) as gid2 from gspreadsheetsabilitydescription g1 join gspreadsheetsabilitydescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsabilitydescription) update gspreadsheetsabilitydescription set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsabilitydescription.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateAchievementDescriptions() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsachievementdescription g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(188155806) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsachievementdescription set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsachievementdescription.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsachievementdescription_id in(select g.id from gspreadsheetsachievementdescription g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsachievementdescription set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsachievementdescription.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsachievementdescription.aid and e.bid=gspreadsheetsachievementdescription.bid and e.cid=gspreadsheetsachievementdescription.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsachievementdescription (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsachievementdescription g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(188155806) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsachievementdescription set deprecated=TRUE where id in(select g.id from gspreadsheetsachievementdescription g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsachievementdescription g1 join gspreadsheetsachievementdescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsachievementdescription_id=newid.gid1 from newid where translatedtext.spreadsheetsachievementdescription_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsachievementdescription where id in (select max(g2.id) as gid2 from gspreadsheetsachievementdescription g1 join gspreadsheetsachievementdescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsachievementdescription) update gspreadsheetsachievementdescription set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsachievementdescription.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateAchievements() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsachievement g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(12529189,172030117) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsachievement set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsachievement.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsachievement_id in(select g.id from gspreadsheetsachievement g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsachievement set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsachievement.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsachievement.aid and e.bid=gspreadsheetsachievement.bid and e.cid=gspreadsheetsachievement.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsachievement (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsachievement g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(12529189,172030117) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsachievement set deprecated=TRUE where id in(select g.id from gspreadsheetsachievement g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsachievement g1 join gspreadsheetsachievement g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsachievement_id=newid.gid1 from newid where translatedtext.spreadsheetsachievement_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsachievement where id in (select max(g2.id) as gid2 from gspreadsheetsachievement g1 join gspreadsheetsachievement g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsachievement) update gspreadsheetsachievement set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsachievement.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateActivators() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsactivator g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(14464837,19398485,39619172,77659573,87370069,124318053,207758933,219936053) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsactivator set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsactivator.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsactivator_id in(select g.id from gspreadsheetsactivator g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsactivator set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsactivator.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsactivator.aid and e.bid=gspreadsheetsactivator.bid and e.cid=gspreadsheetsactivator.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsactivator (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsactivator g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(14464837,19398485,39619172,77659573,87370069,124318053,207758933,219936053) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsactivator set deprecated=TRUE where id in(select g.id from gspreadsheetsactivator g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsactivator g1 join gspreadsheetsactivator g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsactivator_id=newid.gid1 from newid where translatedtext.spreadsheetsactivator_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsactivator where id in (select max(g2.id) as gid2 from gspreadsheetsactivator g1 join gspreadsheetsactivator g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsactivator) update gspreadsheetsactivator set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsactivator.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateCollectibles() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetscollectible g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(18173141,70328405,160914197,245765621,213229525,204530069,42041397) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetscollectible set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetscollectible.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where sheetscollectible_id in(select g.id from gspreadsheetscollectible g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetscollectible set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetscollectible.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetscollectible.aid and e.bid=gspreadsheetscollectible.bid and e.cid=gspreadsheetscollectible.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetscollectible (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetscollectible g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(18173141,70328405,160914197,245765621,213229525,204530069,42041397) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetscollectible set deprecated=TRUE where id in(select g.id from gspreadsheetscollectible g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetscollectible g1 join gspreadsheetscollectible g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set sheetscollectible_id=newid.gid1 from newid where translatedtext.sheetscollectible_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetscollectible where id in (select max(g2.id) as gid2 from gspreadsheetscollectible g1 join gspreadsheetscollectible g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetscollectible) update gspreadsheetscollectible set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetscollectible.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateCollectibleDescriptions() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetscollectibledescription g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(211640654,263796174,86917166,69169806,57952500,129382708) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetscollectibledescription set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetscollectibledescription.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where sheetscollectibledescription_id in(select g.id from gspreadsheetscollectibledescription g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetscollectibledescription set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetscollectibledescription.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetscollectibledescription.aid and e.bid=gspreadsheetscollectibledescription.bid and e.cid=gspreadsheetscollectibledescription.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetscollectibledescription (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetscollectibledescription g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(211640654,263796174,86917166,69169806,57952500,129382708) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetscollectibledescription set deprecated=TRUE where id in(select g.id from gspreadsheetscollectibledescription g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetscollectibledescription g1 join gspreadsheetscollectibledescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set sheetscollectibledescription_id=newid.gid1 from newid where translatedtext.sheetscollectibledescription_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetscollectibledescription where id in (select max(g2.id) as gid2 from gspreadsheetscollectibledescription g1 join gspreadsheetscollectibledescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetscollectibledescription) update gspreadsheetscollectibledescription set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetscollectibledescription.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateItemDescriptions() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsitemdescription g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(27704788,139139780,228378404,249673710) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsitemdescription set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsitemdescription.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsitemdescription_id in(select g.id from gspreadsheetsitemdescription g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsitemdescription set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsitemdescription.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsitemdescription.aid and e.bid=gspreadsheetsitemdescription.bid and e.cid=gspreadsheetsitemdescription.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsitemdescription (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsitemdescription g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(27704788,139139780,228378404,249673710) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsitemdescription set deprecated=TRUE where id in(select g.id from gspreadsheetsitemdescription g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsitemdescription g1 join gspreadsheetsitemdescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsitemdescription_id=newid.gid1 from newid where translatedtext.spreadsheetsitemdescription_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsitemdescription where id in (select max(g2.id) as gid2 from gspreadsheetsitemdescription g1 join gspreadsheetsitemdescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsitemdescription) update gspreadsheetsitemdescription set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsitemdescription.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateItemNames() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsitemname g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(124362421,242841733,267697733) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsitemname set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsitemname.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsitemname_id in(select g.id from gspreadsheetsitemname g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsitemname set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsitemname.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsitemname.aid and e.bid=gspreadsheetsitemname.bid and e.cid=gspreadsheetsitemname.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsitemname (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsitemname g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(124362421,242841733,267697733) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsitemname set deprecated=TRUE where id in(select g.id from gspreadsheetsitemname g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsitemname g1 join gspreadsheetsitemname g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsitemname_id=newid.gid1 from newid where translatedtext.spreadsheetsitemname_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsitemname where id in (select max(g2.id) as gid2 from gspreadsheetsitemname g1 join gspreadsheetsitemname g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsitemname) update gspreadsheetsitemname set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsitemname.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateJournalEntrys() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsjournalentry g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(103224356) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsjournalentry set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsjournalentry.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsjournalentry_id in(select g.id from gspreadsheetsjournalentry g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsjournalentry set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsjournalentry.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsjournalentry.aid and e.bid=gspreadsheetsjournalentry.bid and e.cid=gspreadsheetsjournalentry.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsjournalentry (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsjournalentry g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(103224356) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsjournalentry set deprecated=TRUE where id in(select g.id from gspreadsheetsjournalentry g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsjournalentry g1 join gspreadsheetsjournalentry g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsjournalentry_id=newid.gid1 from newid where translatedtext.spreadsheetsjournalentry_id=gid2;");
            q5.executeUpdate();
            Query q6 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsjournalentry g1 join gspreadsheetsjournalentry g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update queststep set sheetsjournalentry_id=newid.gid1 from newid where queststep.sheetsjournalentry_id=gid2;");
            q6.executeUpdate();
            Query q7 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsjournalentry g1 join gspreadsheetsjournalentry g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update questjournalentry set sheetsjournalentry_id=newid.gid1 from newid where questjournalentry.sheetsjournalentry_id=gid2;");
            q7.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsjournalentry where id in (select max(g2.id) as gid2 from gspreadsheetsjournalentry g1 join gspreadsheetsjournalentry g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsjournalentry) update gspreadsheetsjournalentry set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsjournalentry.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateLoadscreens() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsloadscreen g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(4922190,70901198,153349653) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsloadscreen set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsloadscreen.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where sheetsloadscreen_id in(select g.id from gspreadsheetsloadscreen g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsloadscreen set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsloadscreen.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsloadscreen.aid and e.bid=gspreadsheetsloadscreen.bid and e.cid=gspreadsheetsloadscreen.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsloadscreen (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsloadscreen g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(4922190,70901198,153349653) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsloadscreen set deprecated=TRUE where id in(select g.id from gspreadsheetsloadscreen g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsloadscreen g1 join gspreadsheetsloadscreen g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set sheetsloadscreen_id=newid.gid1 from newid where translatedtext.sheetsloadscreen_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsloadscreen where id in (select max(g2.id) as gid2 from gspreadsheetsloadscreen g1 join gspreadsheetsloadscreen g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsloadscreen) update gspreadsheetsloadscreen set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsloadscreen.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateLocationNames() {
        //Обновляем связи с существующими сырыми строками
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetslocationname g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(10860933, 146361138, 162946485, 162658389, 164009093, 267200725, 28666901, 81344020, 268015829, 111863941, 157886597) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetslocationname set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetslocationname.id=newid.id;");
        q0.executeUpdate();
        //Отзываем переводы в тех строках, у которых поменялся оригинальный текст
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetslocationname_id in(select g.id from gspreadsheetslocationname g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        //Обновляем оригинальный текст строк и удаляем перевод, если оригинальный текст поменялся
        Query q2 = em.createNativeQuery("update gspreadsheetslocationname set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetslocationname.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetslocationname.aid and e.bid=gspreadsheetslocationname.bid and e.cid=gspreadsheetslocationname.cid;");
        q2.executeUpdate();
        //Вставляем новые строки
        Query q3 = em.createNativeQuery("insert into gspreadsheetslocationname (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetslocationname g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(10860933, 146361138, 162946485, 162658389, 164009093, 267200725, 28666901, 81344020, 268015829, 111863941, 157886597) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        //Помечаем как устарвешие те строки, которых нет в сырых
        Query q4 = em.createNativeQuery("update gspreadsheetslocationname set deprecated=TRUE where id in(select g.id from gspreadsheetslocationname g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            //Перед удалением дубликата строки, заменяем ссылки на дубликат сслылками на оригинал. Затем удаляем дубликат Несколько прогонов на случай множества дубликатов
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetslocationname g1 join gspreadsheetslocationname g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetslocationname_id=newid.gid1 from newid where translatedtext.spreadsheetslocationname_id=gid2;");
            q5.executeUpdate();
            Query q6 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetslocationname g1 join gspreadsheetslocationname g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update location set sheetslocationname_id=newid.gid1 from newid where location.sheetslocationname_id=gid2;");
            q6.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetslocationname where id in (select max(g2.id) as gid2 from gspreadsheetslocationname g1 join gspreadsheetslocationname g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        //пересчитываем rownum
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetslocationname) update gspreadsheetslocationname set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetslocationname.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateNotes() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsnote g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(219317028) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsnote set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsnote.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsnote_id in(select g.id from gspreadsheetsnote g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsnote set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsnote.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsnote.aid and e.bid=gspreadsheetsnote.bid and e.cid=gspreadsheetsnote.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsnote (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsnote g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(219317028) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsnote set deprecated=TRUE where id in(select g.id from gspreadsheetsnote g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsnote g1 join gspreadsheetsnote g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsnote_id=newid.gid1 from newid where translatedtext.spreadsheetsnote_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsnote where id in (select max(g2.id) as gid2 from gspreadsheetsnote g1 join gspreadsheetsnote g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsnote) update gspreadsheetsnote set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsnote.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateNpcNames() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsnpcname g left join esorawstring e on g.texten=regexp_replace(regexp_replace(e.texten,'\\^[FfMmNn]',''),'\\n','$','g') and ((g.sex!='U' and g.sex=substring(e.texten from '\\^([FfMmNn])')) or (g.sex='U' and substring(e.texten from '\\^([FfMmNn])') is null)) and e.aid in(8290981,51188660,191999749,33425332) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsnpcname set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsnpcname.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsnpcname_id in(select g.id from gspreadsheetsnpcname g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where lower(g.texten)!=lower(regexp_replace(regexp_replace(e.texten,'\\^[FfMmNn]',''),'\\n','$','g')) or (g.sex!='U' and g.sex!=substring(e.texten from '\\^([FfMmNn])')) or (g.sex='U' and substring(e.texten from '\\^([FfMmNn])') is not null));");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsnpcname set texten=regexp_replace(regexp_replace(e.texten,'\\^[FfMmNn]',''),'\\n','$','g'),textru=regexp_replace(regexp_replace(e.texten,'\\^[FfMmNn]',''),'\\n','$','g'),sex=substring(e.texten from '\\^([FfMmNn])'),changetime=null,translator=null,deprecated=false from esorawstring e where (lower(gspreadsheetsnpcname.texten)!=lower(regexp_replace(regexp_replace(e.texten,'\\^[FfMmNn]',''),'\\n','$','g')) or (gspreadsheetsnpcname.sex!='U' and gspreadsheetsnpcname.sex!=substring(e.texten from '\\^([FfMmNn])')) or (gspreadsheetsnpcname.sex='U' and substring(e.texten from '\\^([FfMmNn])') is not null)) and e.aid=gspreadsheetsnpcname.aid and e.bid=gspreadsheetsnpcname.bid and e.cid=gspreadsheetsnpcname.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("update gspreadsheetsnpcname set sex='U' where sex is null or sex='';");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("insert into gspreadsheetsnpcname select nextval('hibernate_sequence'),null,null,ten,ter,null,sex,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(regexp_replace(texten,'\\^[FfMmNn]',''),'\\n','$','g'),substring(texten from '\\^([FfMmNn])') ORDER BY aid,bid,cid asc) as rn,regexp_replace(regexp_replace(texten,'\\^[FfMmNn]',''),'\\n','$','g') as ten,regexp_replace(regexp_replace(texten,'\\^[FfMmNn]',''),'\\n','$','g') as ter,substring(texten from '\\^([FfMmNn])') as sex,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsnpcname g on lower(g.texten) = lower(regexp_replace(regexp_replace(e.texten,'\\^[FfMmNn]',''),'\\n','$','g')) and (lower(g.sex) = lower(substring(e.texten from '\\^([FfMmNn])')) or (g.sex='U' and substring(e.texten from '\\^([FfMmNn])') is null)) where e.aid in(8290981,51188660,191999749,33425332) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q4.executeUpdate();
        Query q5 = em.createNativeQuery("update gspreadsheetsnpcname set sex='U' where sex is null or sex='';");
        q5.executeUpdate();
        Query q6 = em.createNativeQuery("update gspreadsheetsnpcname set deprecated=TRUE where id in(select g.id from gspreadsheetsnpcname g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q6.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q7 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsnpcname g1 join gspreadsheetsnpcname g2 on lower(g1.texten)=lower(g2.texten) and ((g1.sex=g2.sex) or (g2.sex is null and g1.sex is null)) and g1.id!=g2.id group by lower(g1.texten),g1.sex) update translatedtext set spreadsheetsnpcname_id=newid.gid1 from newid where translatedtext.spreadsheetsnpcname_id=gid2;");
            q7.executeUpdate();
            Query q8 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsnpcname g1 join gspreadsheetsnpcname g2 on lower(g1.texten)=lower(g2.texten) and ((g1.sex=g2.sex) or (g2.sex is null and g1.sex is null)) and g1.id!=g2.id group by g1.texten,g1.sex) update npc set sheetsnpcname_id=newid.gid1 from newid where npc.sheetsnpcname_id=gid2;");
            q8.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsnpcname where id in (select max(g2.id) as gid2 from gspreadsheetsnpcname g1 join gspreadsheetsnpcname g2 on lower(g1.texten)=lower(g2.texten) and ((g1.sex=g2.sex) or (g2.sex is null and g1.sex is null)) and g1.id!=g2.id group by g1.texten,g1.sex);");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsnpcname) update gspreadsheetsnpcname set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsnpcname.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateNpcPhrases() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsnpcphrase g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(3952276,55049764,115740052,116521668,149328292,165399380,200879108,211899940,234743124) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsnpcphrase set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsnpcphrase.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsnpcphrase_id in(select g.id from gspreadsheetsnpcphrase g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsnpcphrase set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsnpcphrase.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsnpcphrase.aid and e.bid=gspreadsheetsnpcphrase.bid and e.cid=gspreadsheetsnpcphrase.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsnpcphrase (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsnpcphrase g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(3952276,55049764,115740052,116521668,149328292,165399380,200879108,211899940,234743124) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsnpcphrase set deprecated=TRUE where id in(select g.id from gspreadsheetsnpcphrase g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsnpcphrase g1 join gspreadsheetsnpcphrase g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsnpcphrase_id=newid.gid1 from newid where translatedtext.spreadsheetsnpcphrase_id=gid2;");
            q5.executeUpdate();
            Query q6 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsnpcphrase g1 join gspreadsheetsnpcphrase g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update topic set extnpcphrase_id=newid.gid1 from newid where topic.extnpcphrase_id=gid2;");
            q6.executeUpdate();
            Query q7 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsnpcphrase g1 join gspreadsheetsnpcphrase g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update subtitle set extnpcphrase_id=newid.gid1 from newid where subtitle.extnpcphrase_id=gid2;");
            q7.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsnpcphrase where id in (select max(g2.id) as gid2 from gspreadsheetsnpcphrase g1 join gspreadsheetsnpcphrase g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsnpcphrase) update gspreadsheetsnpcphrase set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsnpcphrase.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updatePlayerPhrases() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsplayerphrase g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(20958740,99155012,109216308,150525940,204987124,228103012,232026500,249936564,188095652) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsplayerphrase set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsplayerphrase.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsplayerphrase_id in(select g.id from gspreadsheetsplayerphrase g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsplayerphrase set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsplayerphrase.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsplayerphrase.aid and e.bid=gspreadsheetsplayerphrase.bid and e.cid=gspreadsheetsplayerphrase.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsplayerphrase (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsplayerphrase g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(20958740,99155012,109216308,150525940,204987124,228103012,232026500,249936564,188095652) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsplayerphrase set deprecated=TRUE where id in(select g.id from gspreadsheetsplayerphrase g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsplayerphrase g1 join gspreadsheetsplayerphrase g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsplayerphrase_id=newid.gid1 from newid where translatedtext.spreadsheetsplayerphrase_id=gid2;");
            q5.executeUpdate();
            Query q6 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsplayerphrase g1 join gspreadsheetsplayerphrase g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update topic set extplayerphrase_id=newid.gid1 from newid where topic.extplayerphrase_id=gid2;");
            q6.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsplayerphrase where id in (select max(g2.id) as gid2 from gspreadsheetsplayerphrase g1 join gspreadsheetsplayerphrase g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsplayerphrase) update gspreadsheetsplayerphrase set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsplayerphrase.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateQuestDescriptions() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsquestdescription g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(265851556,205344756) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsquestdescription set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsquestdescription.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsquestdescription_id in(select g.id from gspreadsheetsquestdescription g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsquestdescription set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsquestdescription.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsquestdescription.aid and e.bid=gspreadsheetsquestdescription.bid and e.cid=gspreadsheetsquestdescription.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsquestdescription (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsquestdescription g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(265851556,205344756) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsquestdescription set deprecated=TRUE where id in(select g.id from gspreadsheetsquestdescription g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsquestdescription g1 join gspreadsheetsquestdescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsquestdescription_id=newid.gid1 from newid where translatedtext.spreadsheetsquestdescription_id=gid2;");
            q5.executeUpdate();
            Query q6 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsquestdescription g1 join gspreadsheetsquestdescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update quest set sheetsquestdescription_id=newid.gid1 from newid where quest.sheetsquestdescription_id=gid2;");
            q6.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsquestdescription where id in (select max(g2.id) as gid2 from gspreadsheetsquestdescription g1 join gspreadsheetsquestdescription g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsquestdescription) update gspreadsheetsquestdescription set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsquestdescription.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateQuestDirections() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsquestdirection g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(7949764,121487972,168415844,256430276) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsquestdirection set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsquestdirection.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsquestdirection_id in(select g.id from gspreadsheetsquestdirection g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsquestdirection set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsquestdirection.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsquestdirection.aid and e.bid=gspreadsheetsquestdirection.bid and e.cid=gspreadsheetsquestdirection.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsquestdirection (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsquestdirection g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(7949764,121487972,168415844,256430276) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsquestdirection set deprecated=TRUE where id in(select g.id from gspreadsheetsquestdirection g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsquestdirection g1 join gspreadsheetsquestdirection g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsquestdirection_id=newid.gid1 from newid where translatedtext.spreadsheetsquestdirection_id=gid2;");
            q5.executeUpdate();
            Query q6 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsquestdirection g1 join gspreadsheetsquestdirection g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update questdirection set sheetsquestdirection_id=newid.gid1 from newid where questdirection.sheetsquestdirection_id=gid2;");
            q6.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsquestdirection where id in (select max(g2.id) as gid2 from gspreadsheetsquestdirection g1 join gspreadsheetsquestdirection g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsquestdirection) update gspreadsheetsquestdirection set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsquestdirection.id;");
        qSort.executeUpdate();
    }
    
    @Transactional
    public void updateQuestStartTips() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsqueststarttip g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(129979412) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsqueststarttip set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsqueststarttip.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsqueststarttip_id in(select g.id from gspreadsheetsqueststarttip g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsqueststarttip set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsqueststarttip.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsqueststarttip.aid and e.bid=gspreadsheetsqueststarttip.bid and e.cid=gspreadsheetsqueststarttip.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsqueststarttip (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsqueststarttip g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(129979412) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsqueststarttip set deprecated=TRUE where id in(select g.id from gspreadsheetsqueststarttip g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsqueststarttip g1 join gspreadsheetsqueststarttip g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsqueststarttip_id=newid.gid1 from newid where translatedtext.spreadsheetsqueststarttip_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsqueststarttip where id in (select max(g2.id) as gid2 from gspreadsheetsqueststarttip g1 join gspreadsheetsqueststarttip g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsqueststarttip) update gspreadsheetsqueststarttip set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsqueststarttip.id;");
        qSort.executeUpdate();
    }
    
    @Transactional
    public void updateQuestEndTips() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsquestendtip g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(108566804) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsquestendtip set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsquestendtip.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsquestendtip_id in(select g.id from gspreadsheetsquestendtip g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsquestendtip set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsquestendtip.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsquestendtip.aid and e.bid=gspreadsheetsquestendtip.bid and e.cid=gspreadsheetsquestendtip.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsquestendtip (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsquestendtip g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(108566804) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsquestendtip set deprecated=TRUE where id in(select g.id from gspreadsheetsquestendtip g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsquestendtip g1 join gspreadsheetsquestendtip g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsquestendtip_id=newid.gid1 from newid where translatedtext.spreadsheetsquestendtip_id=gid2;");
            q5.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsquestendtip where id in (select max(g2.id) as gid2 from gspreadsheetsquestendtip g1 join gspreadsheetsquestendtip g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsquestendtip) update gspreadsheetsquestendtip set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsquestendtip.id;");
        qSort.executeUpdate();
    }

    @Transactional
    public void updateQuestNames() {
        Query q0 = em.createNativeQuery("with newid as (select g.id,e.aid,e.bid,e.cid from gspreadsheetsquestname g join esorawstring e on lower(regexp_replace(e.texten,'\\n','$','g'))=lower(g.texten) and e.aid in(52420949) order by e.aid asc,e.cid asc,e.bid asc) update gspreadsheetsquestname set deprecated=FALSE,aid=newid.aid,bid=newid.bid,cid=newid.cid,weight=newid.cid from newid where gspreadsheetsquestname.id=newid.id;");
        q0.executeUpdate();
        Query q1 = em.createNativeQuery("update translatedtext set status ='REVOKED' where spreadsheetsquestname_id in(select g.id from gspreadsheetsquestname g join esorawstring e on lower(g.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=g.aid and e.bid=g.bid and e.cid=g.cid);");
        q1.executeUpdate();
        Query q2 = em.createNativeQuery("update gspreadsheetsquestname set texten=regexp_replace(e.texten,'\\n','$','g'),textru=regexp_replace(e.texten,'\\n','$','g'),changetime=null,translator=null,deprecated=false from esorawstring e where lower(gspreadsheetsquestname.texten)!=lower(regexp_replace(e.texten,'\\n','$','g')) and e.aid=gspreadsheetsquestname.aid and e.bid=gspreadsheetsquestname.bid and e.cid=gspreadsheetsquestname.cid;");
        q2.executeUpdate();
        Query q3 = em.createNativeQuery("insert into gspreadsheetsquestname (id,changetime,rownum,texten,textru,translator,weight,aid,bid,cid,deprecated) select nextval('hibernate_sequence'),null,null,ten,ter,null,cid,aid,bid,cid,FALSE from(select ROW_NUMBER() OVER(PARTITION BY regexp_replace(texten,'\\n','$','g') ORDER BY aid,bid,cid asc) as rn,regexp_replace(texten,'\\n','$','g') as ten,regexp_replace(texten,'\\n','$','g') as ter,aid,bid,cid from esorawstring where id in(select e.id from esorawstring e left join gspreadsheetsquestname g on lower(g.texten) = lower(regexp_replace(e.texten,'\\n','$','g')) where e.aid in(52420949) and g.id is null order by e.aid asc,e.cid asc,e.bid asc)) as rr where rn=1;");
        q3.executeUpdate();
        Query q4 = em.createNativeQuery("update gspreadsheetsquestname set deprecated=TRUE where id in(select g.id from gspreadsheetsquestname g left join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid where e.texten is null);");
        q4.executeUpdate();
        for (int i = 0; i < 5; i++) {
            Query q5 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsquestname g1 join gspreadsheetsquestname g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update translatedtext set spreadsheetsquestname_id=newid.gid1 from newid where translatedtext.spreadsheetsquestname_id=gid2;");
            q5.executeUpdate();
            Query q6 = em.createNativeQuery("with newid as (select min(g1.id) as gid1,max(g2.id) as gid2 from gspreadsheetsquestname g1 join gspreadsheetsquestname g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten)) update quest set sheetsquestname_id=newid.gid1 from newid where quest.sheetsquestname_id=gid2;");
            q6.executeUpdate();
            Query qDelete = em.createNativeQuery("delete from gspreadsheetsquestname where id in (select max(g2.id) as gid2 from gspreadsheetsquestname g1 join gspreadsheetsquestname g2 on lower(g1.texten)=lower(g2.texten) and g1.id!=g2.id group by lower(g1.texten));");
            qDelete.executeUpdate();
        }
        Query qSort = em.createNativeQuery("with new_numbers as (select row_number() over (order by deprecated asc,aid asc,cid asc,bid asc) as new_nr,rownum, id from gspreadsheetsquestname) update gspreadsheetsquestname set rownum = nn.new_nr from new_numbers nn where nn.id = gspreadsheetsquestname.id;");
        qSort.executeUpdate();
    }

}
