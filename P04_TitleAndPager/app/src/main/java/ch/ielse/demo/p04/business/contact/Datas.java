package ch.ielse.demo.p04.business.contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LY on 2017/2/22.
 */

public class Datas {


    public static List<Group> getGroupList() {
        Friend friend1 = new Friend("word哥", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/ipTRBYbB54aSh3GTWpVJ*nN3AF.Cqw1oTGoX7Z8GbCQ!/b/dG4BAAAAAAAA",
                Friend.ONLINE_COMPUTER, "有你们 我很满足的。");

        Group group1 = new Group("特别关心", Arrays.asList(friend1));

        Group group2 = new Group("常用群聊", new ArrayList<Friend>());

        Friend friend2 = new Friend("else小号", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/bGliWnENm6QIfHdXMe43ohViZY2ciL7GoH2K7fz8d*0!/b/dG4BAAAAAAAA",
                Friend.ONLINE_WIFI, "fucking the code.");

        Group group3 = new Group("Tea", Arrays.asList(friend2));

        Friend friend3 = new Friend("plus_llsmp", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/rox42yBaNzjcaXqj*me7oLZcMigKkhieMCdz7IPsCm8!/b/dG4BAAAAAAAA",
                Friend.ONLINE_PHONE, "[表情][表情][表情]");
        Friend friend4 = new Friend("悟空-monster", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/c1N9V5MR5MJ.NSbX*il8kcRqulbve3LMatJgOkyYRAw!/c/dG4BAAAAAAAA",
                Friend.ONLINE_WIFI, "网站搬迁至阿里云...");
        Friend friend5 = new Friend("ai", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/ILPpHObVhz1Fb6a8VXGC4qfhkE63HxhgJ8HM1rMf9IQ!/b/dN8AAAAAAAAA",
                Friend.ONLINE_PHONE, "");
        Friend friend6 = new Friend("鄙4i0!贾~~啥", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/NX8rgK.Ilx6lPGypAJ0BoUCPTdu4XtBb3pGp4RBa.kI!/b/dN8AAAAAAAAA&bo=aAGAAgAAAAAFAMg!&rf=viewer_4",
                Friend.ONLINE_PHONE, "");
        Friend friend7 = new Friend("df失控", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/6UzmbQk7yMvn1IgvdCIia3VTLQ5Yuw2eqeSPI4g2iJs!/b/dCUAAAAAAAAA&bo=OAI4AgAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_4G, "最新分享：英雄杀礼包");
        Friend friend8 = new Friend("Believe you", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/6UzmbQk7yMvn1IgvdCIia3VTLQ5Yuw2eqeSPI4g2iJs!/b/dCUAAAAAAAAA&bo=OAI4AgAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_4G, "奔跑吧，不管你有没有伞");
        Friend friend9 = new Friend("bv雯", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/cxUOkZRyjM0BiZMzkIRQvgAL7AIss59Oiad9bg966oo!/b/dG4BAAAAAAAA&bo=bgBuAAAAAAADACU!&rf=viewer_4",
                Friend.ONLINE_4G, "滚蛋了[表情]");
        Friend friend10 = new Friend("aKeyBoard_c", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/DhTUN0SZ7x1csa6P8W0WVIxInLNnXWIyDRdztai0ae4!/b/dBYAAAAAAAAA&bo=cAEQAQAAAAAFAEM!&rf=viewer_4",
                Friend.ONLINE_WIFI, "");
        Friend friend11 = new Friend("史强", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/yhmHlR0qpKSVbShjfLI6VPdKO.dtteUotVjXBs7Y6.0!/b/dHoBAAAAAAAA&bo=egB6AAAAAAADACU!&rf=viewer_4",
                Friend.ONLINE_WIFI, "一张图，表达了男人那啥");
        Friend friend12 = new Friend("彭德华", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/.l5.OXC4lKSGBy*u*K1rFxDj1bFlHWa78J*wsrj67zo!/b/dG4BAAAAAAAA&bo=egB6AAAAAAADACU!&rf=viewer_4",
                Friend.ONLINE_WIFI, "更新了说说");

        Group group4 = new Group("Internet", Arrays.asList(friend3, friend4, friend5, friend6, friend7, friend8, friend9, friend10, friend11, friend12));


        Friend friend13 = new Friend("陈思成", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/V1GqUdM8kwyY9R9vDl.jvNDCIL3T08O0Urqmv9jsnGg!/b/dAsBAAAAAAAA&bo=aAFoAQAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_WIFI, "撸起袖子加油干..");
        Friend friend14 = new Friend("陈威风", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/BEEyQASWDX8sEonH5yiMVtRB4BELC.pRxTpfuVAk*0c!/b/dG4BAAAAAAAA&bo=QAEsAgAAAAAFAEw!&rf=viewer_4",
                Friend.ONLINE_WIFI, "Nothing seek,nothing fin..");
        Friend friend15 = new Friend("韩宏", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/MJVStbykZoM86BS6rINlCGSI63hr5CyrLuGSc0w0Bms!/b/dCUAAAAAAAAA&bo=QAHwAAAAAAAFAJI!&rf=viewer_4",
                Friend.ONLINE_WIFI, "更新了说说");
        Friend friend16 = new Friend("晨曦", "http://a3.qpic.cn/psb?/V12sGKh24gZRjl/HD4FSnCGsWUKUcQCOV6ggrFOUDuIQbTDIZJLZgLVUUk!/b/dHkBAAAAAAAA&bo=QAHwAAAAAAAFAJI!&rf=viewer_4",
                Friend.ONLINE_PHONE, "我认识很多奋斗在.");
        Friend friend17 = new Friend("菜星星", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/ldM.cMtBr4o4WWs*iD1Yyv6b*b7CbltSVsUz559lShY!/b/dG4BAAAAAAAA&bo=4QCqAAAAAAADAG4!&rf=viewer_4",
                Friend.ONLINE_WIFI, "谢谢今天所有祝福的朋友.");
        Friend friend18 = new Friend("成实在", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/tnn01IaSEnSNa87Jfwh7zbSgxpbUNyMiKp4UT0f.DJM!/b/dBYAAAAAAAAA&bo=UwBjAAAAAAADABU!&rf=viewer_4",
                Friend.ONLINE_WIFI, "Alone。");
        Friend friend19 = new Friend("陈建", "http://a3.qpic.cn/psb?/V12sGKh24gZRjl/mOQrIcAbUynLjocqMDhA1TkB8gNfLHM6u7PiFavXwwU!/b/dAoBAAAAAAAA&bo=lgCTAAAAAAADACA!&rf=viewer_4",
                Friend.ONLINE_COMPUTER, "更新了说说");
        Friend friend20 = new Friend("丁小虎", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/wXvGkBiJsIQGU9O*9uuH9i8zOh1d3ZY5azG2yedo*Oc!/b/dG4BAAAAAAAA&bo=UABQAAAAAAADACU!&rf=viewer_4",
                Friend.ONLINE_WIFI, "");
        Friend friend21 = new Friend("丁葱", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/MU4j8goyS6y0KKeHEJjlTPUUDSSKUOBLTzFQKnnTJWM!/b/dHcBAAAAAAAA&bo=UABQAAAAAAADACU!&rf=viewer_4",
                Friend.ONLINE_WIFI, "");
        Friend friend22 = new Friend("关领主", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/iCUxBTXoELXJbHOj54xqVX7q5CRKA1mfTFbJsElCfVQ!/b/dG4BAAAAAAAA&bo=UABQAAAAAAADACU!&rf=viewer_4",
                Friend.ONLINE_WIFI, "");
        Friend friend23 = new Friend("倌大人", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/DGUy1VAK2u8ZOQTKUG9nqYKbHy2TtJgP5DHhkFSSXTQ!/b/dBYAAAAAAAAA&bo=UABQAAAAAAADACU!&rf=viewer_4",
                Friend.ONLINE_WIFI, "不困与心，不乱与情，不畏将来，不念过去");
        Friend friend24 = new Friend("胡阿蔓", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/sUrpKgDVHOLXb7A6epEJvzIu6mImz1ZQxprF4qT3gkg!/b/dCUAAAAAAAAA&bo=bgBuAAAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_WIFI, "growth hacker");
        Friend friend25 = new Friend("凯子杰", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/LGxBTbaHB63xUdeVFkYJj3aLaxbqJNyfT6J29CHuhu4!/b/dG4BAAAAAAAA&bo=bgBuAAAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_WIFI, "残缺不全");
        Friend friend26 = new Friend("filter伤", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/Y6gNc4xvYFr63SGQ2cCgRrCmWKRMfPgHMA1s4itiRFE!/b/dG4BAAAAAAAA&bo=bgBuAAAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_WIFI, "");

        Group group5 = new Group("Maple", Arrays.asList(friend13, friend14, friend15, friend16, friend17, friend18, friend19, friend20, friend21, friend22, friend23, friend24, friend25, friend26));

        Friend friend27 = new Friend("月牙", "http://a3.qpic.cn/psb?/V12sGKh24gZRjl/Ep4XJ0L05YpApyW6DVgRxUrErG0IaVfQRjjP8x.DMBw!/b/dAoBAAAAAAAA&bo=bgBuAAAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_IPHONE, "");
        Friend friend28 = new Friend("7074088841", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/iqBkvrdZVrUA*7dO9L6t2K4TYCu.80fcUeQJTdWW2OI!/b/dHgBAAAAAAAA&bo=bgBuAAAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_WIFI, "");
        Friend friend29 = new Friend("Bang!", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/bGliWnENm6QIfHdXMe43ohViZY2ciL7GoH2K7fz8d*0!/b/dG4BAAAAAAAA&bo=bgBuAAAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_WIFI, "补发一下登顶泰山看日出的视频");
        Friend friend30 = new Friend("丹慧捷", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/nby5Ob*yE1vrrWRx86W2cmEarvYi3phTdwt2G8*zyiE!/b/dAsBAAAAAAAA&bo=bgBuAAAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_3G, "");
        Friend friend31 = new Friend("摄鹿p", "http://a3.qpic.cn/psb?/V12sGKh24gZRjl/3.F09JUh.AIk0aqPwO3QBmkhk3hdE8VkZcnpR1j83CI!/b/dN0AAAAAAAAA&bo=bgBuAAAAAAAFACM!&rf=viewer_4",
                Friend.ONLINE_WIFI, "");

        Group group6 = new Group("Business", Arrays.asList(friend27, friend28, friend29, friend30, friend31));


        Friend friend32 = new Friend("赵刚", "http://a2.qpic.cn/psb?/V12sGKh24gZRjl/tnHsmxzBRy3nGD9MMF*.ANW*8ytMPJ3gbt.squV7YYA!/b/dCUAAAAAAAAA&bo=UABQAAAAAAADACU!&rf=viewer_4",
                Friend.ONLINE_WIFI, "好运气");
        Friend friend33 = new Friend("赵文凯", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/o1TJZl1QCc8*nzcXP3AYfkVFIJwlk.h8V4TsEuh8WG0!/b/dG4BAAAAAAAA&bo=UwBmAAAAAAADABA!&rf=viewer_4",
                Friend.ONLINE_WIFI, "");
        Friend friend34 = new Friend("苏波", "http://a1.qpic.cn/psb?/V12sGKh24gZRjl/JLz5ApcquAbci5lHwYzLIzPN2YfEveUUidAHEt2ONJg!/b/dG4BAAAAAAAA&bo=UABQAAAAAAADACU!&rf=viewer_4",
                Friend.ONLINE_WIFI, "PIANO VER");
        Friend friend35 = new Friend("koda.吴", "http://a3.qpic.cn/psb?/V12sGKh24gZRjl/7r05svLh7D85tYBI4k*OLCwKHDKV4nhem2748OfoE9o!/b/dN0AAAAAAAAA&bo=PAA0AAAAAAAFACs!&rf=viewer_4",
                Friend.OFFLINE, "更新了说说");

        Group group7 = new Group("手机通讯录", Arrays.asList(friend32, friend33, friend34, friend35));

        return Arrays.asList(group1, group2, group3, group4, group5, group6, group7);
    }
}
