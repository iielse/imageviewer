package ch.ielse.demo.p02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Data {

    private String avatar;
    private String nickname;
    private String createTime;
    private String content;
    private List<String> pictureList;
    private List<String> pictureThumbList;

    public static List<Data> get() {
        List<Data> dataList = new ArrayList<>();

        Data data1 = new Data();
        data1.avatar = "http://b162.photo.store.qq.com/psb?/V14EhGon4cZvmh/z2WukT5EhNE76WtOcbqPIgwM2Wxz4Tb7Nub.rDpsDgo!/b/dOaanmAaKQAA";
        data1.nickname = "萌新-lpe";
        data1.createTime = "昨天 11:21";
        data1.content = "开司还是那么帅";
        data1.pictureList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/06/1483664940_9893.jpg",
                "http://img.my.csdn.net/uploads/201701/06/1483664940_3308.jpg",
                "http://img.my.csdn.net/uploads/201701/06/1483664927_3920.png",
                "http://img.my.csdn.net/uploads/201701/06/1483664926_8360.png",
                "http://img.my.csdn.net/uploads/201701/06/1483664926_6184.png",
                "http://img.my.csdn.net/uploads/201701/06/1483664925_8382.png",
                "http://img.my.csdn.net/uploads/201701/06/1483664925_2087.jpg",
                "http://img.my.csdn.net/uploads/201701/06/1483664777_5730.png",
                "http://img.my.csdn.net/uploads/201701/06/1483664741_1378.jpg",
                "http://img.my.csdn.net/uploads/201701/06/1483671689_9534.png",
                "http://img.my.csdn.net/uploads/201701/06/1483671689_2126.png",
                "http://img.my.csdn.net/uploads/201701/06/1483671703_7890.png"
        );
        data1.pictureThumbList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/17/1484647899_2806.jpg",
                "http://img.my.csdn.net/uploads/201701/17/1484647798_4500.jpg",
                "http://img.my.csdn.net/uploads/201701/17/1484647897_1367.png",
                "http://img.my.csdn.net/uploads/201701/17/1484650736_2101.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647701_9893.png",
                "http://img.my.csdn.net/uploads/201701/17/1484650700_2514.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647930_5139.jpg",
                "http://img.my.csdn.net/uploads/201701/17/1484647929_8108.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647897_1978.jpg",
                "http://img.my.csdn.net/uploads/201701/17/1484647898_4474.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647930_7735.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647929_9591.png"
        );


        Data data2 = new Data();
        data2.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data2.nickname = "苦涩";
        data2.createTime = "昨天 09:59";
        data2.content = "唐僧还是厉害啊。为什么？有宝马";
        data2.pictureList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/06/1483664741_7475.png"
        );
        data2.pictureThumbList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/17/1484647799_1689.png"
        );

        Data data3 = new Data();
        data3.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data3.nickname = "empty";
        data3.createTime = "昨天 08:12";
        data3.content = "各种眼神";
        data3.pictureList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/06/1483671690_1970.png",
                "http://img.my.csdn.net/uploads/201701/06/1483671690_6902.png",
                "http://img.my.csdn.net/uploads/201701/06/1483671702_6499.png",
                "http://img.my.csdn.net/uploads/201701/06/1483671702_2352.jpg"
        );
        data3.pictureThumbList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/17/1484650701_4150.png",
                "http://img.my.csdn.net/uploads/201701/17/1484650719_9275.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647702_8420.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647930_4474.jpg"
        );

        Data data4 = new Data();
        data4.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data4.nickname = "empty";
        data4.createTime = "昨天 06:00";
        data4.content = "人与人间的信任，就像是纸片，一旦破损，就不会再回到原来的样子。";
        data4.pictureList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/13/1484296303_7395.png",
                "http://img.my.csdn.net/uploads/201701/13/1484296122_9613.jpg",
                "http://img.my.csdn.net/uploads/201701/13/1484296303_6984.png"
        );
        data4.pictureThumbList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/17/1484647817_3557.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647818_9583.jpg",
                "http://img.my.csdn.net/uploads/201701/17/1484647817_7305.png"
        );

        Data data5 = new Data();
        data5.avatar = "http://qlogo3.store.qq.com/qzone/383559698/383559698/100?1416542262";
        data5.nickname = "越线龙马";
        data5.createTime = "前天 14:61";
        data5.content = "雪.触之即化..愿永久飘零";
        data5.pictureList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/13/1484296288_3031.png",
                "http://img.my.csdn.net/uploads/201701/13/1484296303_5044.jpg"
        );
        data5.pictureThumbList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/17/1484647278_8869.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647702_1117.jpg"
        );

        Data data6 = new Data();
        data6.avatar = "http://b162.photo.store.qq.com/psb?/V14EhGon4cZvmh/z2WukT5EhNE76WtOcbqPIgwM2Wxz4Tb7Nub.rDpsDgo!/b/dOaanmAaKQAA";
        data6.nickname = "顺子要不起";
        data6.createTime = "圣诞节";
        data6.content = "颜宇扬的期末总结\n1、有本事冲我来，别再家长会上吓唬我爸\n2、期末考试成绩出来了，我觉得我妈生二胎是非常明智的选择\n3、这场考试对于我的意义就是知道了班上到底有多少人\n4、期末考试不给老师们露一手，他们还真以为自己教的好";
        data6.pictureList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/13/1484296287_2190.png",
                "http://img.my.csdn.net/uploads/201701/13/1484296286_7908.png",
                "http://img.my.csdn.net/uploads/201701/13/1484296286_7013.png",
                "http://img.my.csdn.net/uploads/201701/13/1484296286_6401.jpg",
                "http://img.my.csdn.net/uploads/201701/13/1484296106_5671.png"
        );
        data6.pictureThumbList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/17/1484647898_9300.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647278_2143.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647816_4929.png",
                "http://img.my.csdn.net/uploads/201701/17/1484647817_5319.jpg",
                "http://img.my.csdn.net/uploads/201701/17/1484647818_3369.png"
        );

        dataList.add(data1);
        dataList.add(data2);
        dataList.add(data3);
        dataList.add(data4);
        dataList.add(data5);
        dataList.add(data6);
        return dataList;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<String> pictureList) {
        this.pictureList = pictureList;
    }

    public List<String> getPictureThumbList() {
        return pictureThumbList;
    }

    public void setPictureThumbList(List<String> pictureThumbList) {
        this.pictureThumbList = pictureThumbList;
    }
}
