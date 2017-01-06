package ch.ielse.demo.p02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LY on 2017/1/4.
 */

public class Data {

    private String avatar;
    private String nickname;
    private String createTime;
    private String content;
    private List<String> pictureList;

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

        Data data2 = new Data();
        data2.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data2.nickname = "苦涩";
        data2.createTime = "昨天 09:59";
        data2.content = "开司还是那么帅";
        data2.pictureList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/06/1483664741_7475.png"
        );

        Data data3 = new Data();
        data3.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data3.nickname = "苦涩";
        data3.createTime = "昨天 08:12";
        data3.content = "开司还是那么帅";
        data3.pictureList = Arrays.asList(
                "http://img.my.csdn.net/uploads/201701/06/1483671690_1970.png",
                "http://img.my.csdn.net/uploads/201701/06/1483671690_6902.png",
                "http://img.my.csdn.net/uploads/201701/06/1483671702_6499.png",
                "http://img.my.csdn.net/uploads/201701/06/1483671702_2352.jpg"
        );
        dataList.add(data1);
        dataList.add(data2);
        dataList.add(data3);
        dataList.add(data2);
        dataList.add(data3);
        dataList.add(data2);
        dataList.add(data3);
        dataList.add(data2);
        dataList.add(data3);
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
}
