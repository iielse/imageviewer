package ch.ielse.demo.p04.business.contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LY on 2017/2/22.
 */

public class Datas {


    public static List<Group> getGroupList() {
        Group group0 = new Group("", new ArrayList<Friend>(), Group.TYPE_HEADER);

        Friend friend1 = new Friend("word哥", "http://img.my.csdn.net/uploads/201702/23/1487816886_5202.png",
                Friend.ONLINE_COMPUTER, "有你们 我很满足的。");
        Group group1 = new Group("特别关心", Arrays.asList(friend1)).setHasTopSpace(true);

        Group group2 = new Group("常用群聊", new ArrayList<Friend>());

        Friend friend2 = new Friend("else小号", "http://img.my.csdn.net/uploads/201702/23/1487816886_2701.png",
                Friend.ONLINE_WIFI, "fucking the code.");
        Group group3 = new Group("Tea", Arrays.asList(friend2)).setHasTopSpace(true);

        Friend friend3 = new Friend("plus_llsmp", "http://img.my.csdn.net/uploads/201702/23/1487816886_2290.png",
                Friend.ONLINE_PHONE, "[表情][表情][表情]");
        Friend friend4 = new Friend("悟空-monster", "http://img.my.csdn.net/uploads/201702/23/1487816887_2984.png",
                Friend.ONLINE_WIFI, "网站搬迁至阿里云...");
        Friend friend5 = new Friend("ai", "http://img.my.csdn.net/uploads/201702/23/1487816738_3326.png",
                Friend.ONLINE_PHONE, "");
        Friend friend6 = new Friend("鄙4i0!贾~~啥", "http://img.my.csdn.net/uploads/201702/23/1487816887_4820.png",
                Friend.ONLINE_PHONE, "");
        Friend friend7 = new Friend("df失控", "http://img.my.csdn.net/uploads/201702/23/1487816953_8622.png",
                Friend.ONLINE_4G, "最新分享：英雄杀礼包");
        Friend friend8 = new Friend("Believe you", "http://img.my.csdn.net/uploads/201702/23/1487816953_6098.png",
                Friend.ONLINE_4G, "奔跑吧，不管你有没有伞");
        Friend friend9 = new Friend("bv雯", "http://img.my.csdn.net/uploads/201702/23/1487816954_1674.png",
                Friend.ONLINE_4G, "滚蛋了[表情]");
        Friend friend10 = new Friend("aKeyBoard_c", "http://img.my.csdn.net/uploads/201702/23/1487816954_1621.png",
                Friend.ONLINE_WIFI, "");
        Friend friend11 = new Friend("史强", "http://img.my.csdn.net/uploads/201702/23/1487816806_2174.png",
                Friend.ONLINE_WIFI, "一张图，表达了男人那啥");
        Friend friend12 = new Friend("彭德华", "http://img.my.csdn.net/uploads/201702/23/1487816807_7425.png",
                Friend.ONLINE_WIFI, "更新了说说");
        Group group4 = new Group("Internet", Arrays.asList(friend3, friend4, friend5, friend6, friend7, friend8, friend9, friend10, friend11, friend12));

        Friend friend13 = new Friend("陈思成", "http://img.my.csdn.net/uploads/201702/23/1487816807_5520.png",
                Friend.ONLINE_WIFI, "撸起袖子加油干..");
        Friend friend14 = new Friend("陈威风", "http://img.my.csdn.net/uploads/201702/23/1487816826_9448.png",
                Friend.ONLINE_WIFI, "Nothing seek,nothing fin..");
        Friend friend15 = new Friend("韩宏", "http://img.my.csdn.net/uploads/201702/23/1487816826_6492.png",
                Friend.ONLINE_WIFI, "更新了说说");
        Friend friend16 = new Friend("晨曦", "http://img.my.csdn.net/uploads/201702/23/1487817016_1697.png",
                Friend.ONLINE_PHONE, "我认识很多奋斗在.");
        Friend friend17 = new Friend("菜星星", "http://img.my.csdn.net/uploads/201702/23/1487817017_5109.png",
                Friend.ONLINE_WIFI, "谢谢今天所有祝福的朋友.");
        Friend friend18 = new Friend("成实在", "http://img.my.csdn.net/uploads/201702/23/1487816869_7612.png",
                Friend.ONLINE_WIFI, "Alone。");
        Friend friend19 = new Friend("陈建", "http://img.my.csdn.net/uploads/201702/23/1487817144_6684.png",
                Friend.ONLINE_COMPUTER, "更新了说说");
        Friend friend20 = new Friend("丁小虎", "http://img.my.csdn.net/uploads/201702/23/1487816901_3184.png",
                Friend.ONLINE_WIFI, "");
        Friend friend21 = new Friend("丁葱", "http://img.my.csdn.net/uploads/201702/23/1487816917_3146.png",
                Friend.ONLINE_WIFI, "");
        Friend friend22 = new Friend("关领主", "http://img.my.csdn.net/uploads/201702/23/1487816918_1096.png",
                Friend.ONLINE_WIFI, "");
        Friend friend23 = new Friend("倌大人", "http://img.my.csdn.net/uploads/201702/23/1487816918_4612.png",
                Friend.ONLINE_WIFI, "不困与心，不乱与情，不畏将来，不念过去");
        Friend friend24 = new Friend("胡阿蔓", "http://img.my.csdn.net/uploads/201702/23/1487816931_5397.png",
                Friend.ONLINE_WIFI, "growth hacker");
        Friend friend25 = new Friend("凯子杰", "http://img.my.csdn.net/uploads/201702/23/1487817124_1727.png",
                Friend.ONLINE_WIFI, "残缺不全");
        Friend friend26 = new Friend("filter伤", "http://img.my.csdn.net/uploads/201702/23/1487817124_7206.png",
                Friend.ONLINE_WIFI, "");
        Group group5 = new Group("Maple", Arrays.asList(friend13, friend14, friend15, friend16, friend17, friend18, friend19, friend20, friend21, friend22, friend23, friend24, friend25, friend26));

        Friend friend27 = new Friend("月牙", "http://img.my.csdn.net/uploads/201702/23/1487817143_3541.png",
                Friend.ONLINE_IPHONE, "");
        Friend friend28 = new Friend("7074088841", "http://img.my.csdn.net/uploads/201702/23/1487817144_5442.png",
                Friend.ONLINE_WIFI, "");
        Friend friend29 = new Friend("Bang!", "http://img.my.csdn.net/uploads/201702/23/1487817144_9692.png",
                Friend.ONLINE_WIFI, "补发一下登顶泰山看日出的视频");
        Friend friend30 = new Friend("丹慧捷", "http://img.my.csdn.net/uploads/201702/23/1487816901_8360.png",
                Friend.ONLINE_3G, "");
        Friend friend31 = new Friend("摄鹿p", "http://img.my.csdn.net/uploads/201702/23/1487817145_4126.png",
                Friend.ONLINE_WIFI, "");
        Group group6 = new Group("Business", Arrays.asList(friend27, friend28, friend29, friend30, friend31));


        Friend friend32 = new Friend("赵刚", "http://img.my.csdn.net/uploads/201702/23/1487817161_1225.png",
                Friend.ONLINE_WIFI, "好运气");
        Friend friend33 = new Friend("赵文凯", "http://img.my.csdn.net/uploads/201702/23/1487817161_7722.png",
                Friend.ONLINE_WIFI, "");
        Friend friend34 = new Friend("苏波", "http://img.my.csdn.net/uploads/201702/23/1487817161_9751.png",
                Friend.ONLINE_WIFI, "PIANO VER");
        Friend friend35 = new Friend("koda.吴", "http://img.my.csdn.net/uploads/201702/23/1487817145_4126.png",
                Friend.OFFLINE, "更新了说说");
        Group group7 = new Group("手机通讯录", Arrays.asList(friend32, friend33, friend34, friend35)).setHasTopSpace(true);

        Friend friend36 = new Friend("我的打印机", "",
                "", "将手机文件或照片发到电脑连接的..");
        Friend friend37 = new Friend("我的打印机", "",
                Friend.ONLINE, "无需数据线，手机轻松传文件..");
        Friend friend38 = new Friend("发现新设备", "",
                "", "搜索附近的设备，用QQ惊悚连接设备");
        Group group8 = new Group("我的设备", Arrays.asList(friend36, friend37, friend38));


        return Arrays.asList(group0, group1, group2, group3, group4, group5, group6, group7, group8);
    }
}
