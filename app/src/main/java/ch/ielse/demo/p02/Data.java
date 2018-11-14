package ch.ielse.demo.p02;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Data {

    private String avatar;
    private String nickname;
    private String createTime;
    private String content;
    private List<Uri> pictureList;
    private List<Uri> pictureThumbList;

    public static List<Data> get() {
        List<Data> dataList = new ArrayList<>();

        Data data1 = new Data();
        data1.avatar = "http://b162.photo.store.qq.com/psb?/V14EhGon4cZvmh/z2WukT5EhNE76WtOcbqPIgwM2Wxz4Tb7Nub.rDpsDgo!/b/dOaanmAaKQAA";
        data1.nickname = "萌新-lpe";
        data1.createTime = "昨天 11:21";
        data1.content = "开司还是那么帅";
        data1.pictureList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483664940_9893.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483664940_3308.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483664927_3920.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483664926_8360.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483664926_6184.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483664925_8382.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483664925_2087.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483664777_5730.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483664741_1378.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483671689_9534.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483671689_2126.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483671703_7890.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201707/27/1501118577_9169.jpg") // 超出屏幕宽度大尺寸图测试
        );
        data1.pictureThumbList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647899_2806.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647798_4500.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647897_1367.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484650736_2101.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647701_9893.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484650700_2514.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647930_5139.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647929_8108.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647897_1978.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647898_4474.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647930_7735.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647929_9591.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201707/27/1501118720_9504.jpg") // 超出屏幕宽度大尺寸图测试
        );


        Data data2 = new Data();
        data2.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data2.nickname = "苦涩";
        data2.createTime = "昨天 09:59";
        data2.content = "唐僧还是厉害啊。为什么？有宝马";
        data2.pictureList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483664741_7475.png")
        );
        data2.pictureThumbList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647799_1689.png")
        );

        Data data3 = new Data();
        data3.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data3.nickname = "empty";
        data3.createTime = "昨天 08:12";
        data3.content = "各种眼神";
        data3.pictureList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483671690_1970.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483671690_6902.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483671702_6499.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/06/1483671702_2352.jpg")
        );
        data3.pictureThumbList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484650701_4150.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484650719_9275.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647702_8420.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647930_4474.jpg")
        );

        Data data4 = new Data();
        data4.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data4.nickname = "empty";
        data4.createTime = "昨天 06:00";
        data4.content = "人与人间的信任，就像是纸片，一旦破损，就不会再回到原来的样子。";
        data4.pictureList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/13/1484296303_7395.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/13/1484296122_9613.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/13/1484296303_6984.png")
        );
        data4.pictureThumbList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647817_3557.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647818_9583.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647817_7305.png")
        );

        Data data5 = new Data();
        data5.avatar = "http://qlogo3.store.qq.com/qzone/383559698/383559698/100?1416542262";
        data5.nickname = "越线龙马";
        data5.createTime = "前天 14:61";
        data5.content = "雪.触之即化..愿永久飘零";
        data5.pictureList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/13/1484296288_3031.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/13/1484296303_5044.jpg")
        );
        data5.pictureThumbList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647278_8869.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647702_1117.jpg")
        );

        Data data6 = new Data();
        data6.avatar = "http://b162.photo.store.qq.com/psb?/V14EhGon4cZvmh/z2WukT5EhNE76WtOcbqPIgwM2Wxz4Tb7Nub.rDpsDgo!/b/dOaanmAaKQAA";
        data6.nickname = "顺子要不起";
        data6.createTime = "圣诞节";
        data6.content = "颜宇扬的期末总结\n1、有本事冲我来，别再家长会上吓唬我爸\n2、期末考试成绩出来了，我觉得我妈生二胎是非常明智的选择\n3、这场考试对于我的意义就是知道了班上到底有多少人\n4、期末考试不给老师们露一手，他们还真以为自己教的好";
        data6.pictureList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/13/1484296287_2190.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/13/1484296286_7908.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/13/1484296286_7013.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/13/1484296286_6401.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/13/1484296106_5671.png")
        );
        data6.pictureThumbList = Arrays.asList(
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647898_9300.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647278_2143.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647816_4929.png"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647817_5319.jpg"),
                Uri.parse("http://img.my.csdn.net/uploads/201701/17/1484647818_3369.png")
        );


        if (System.currentTimeMillis() % 3 == 0) {
            dataList.add(data1);
            dataList.add(data2);
            dataList.add(data3);
            dataList.add(data4);
            dataList.add(data6);
        } else if (System.currentTimeMillis() % 3 == 1) {
            dataList.add(data5);
            dataList.add(data6);
            dataList.add(data2);
            dataList.add(data4);
            dataList.add(data3);
        } else {
            dataList.add(data1);
            dataList.add(data3);
            dataList.add(data4);
            dataList.add(data5);
            dataList.add(data6);
        }

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

    public List<Uri> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<Uri> pictureList) {
        this.pictureList = pictureList;
    }

    public List<Uri> getPictureThumbList() {
        return pictureThumbList;
    }

    public void setPictureThumbList(List<Uri> pictureThumbList) {
        this.pictureThumbList = pictureThumbList;
    }


    public static List<Data> getGif() {
        List<Data> dataList = new ArrayList<>();

        Data data1 = new Data();
        data1.avatar = "http://b162.photo.store.qq.com/psb?/V14EhGon4cZvmh/z2WukT5EhNE76WtOcbqPIgwM2Wxz4Tb7Nub.rDpsDgo!/b/dOaanmAaKQAA";
        data1.nickname = "萌新-lpe";
        data1.createTime = "昨天 11:21";
        data1.content = "开司还是那么帅";
        data1.pictureList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168009921&di=fe2e9e5bc508130558a9954c2a8bd28f&imgtype=0&src=http%3A%2F%2Fxuexi.leawo.cn%2Fuploads%2Fallimg%2F160926%2F134225K60-2.gif"),
                Uri.parse("https://b-ssl.duitang.com/uploads/item/201206/29/20120629140234_QWAsX.thumb.700_0.gif"),
                Uri.parse("http://pic2.52pk.com/files/allimg/150324/104923F49-12.jpg"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168074673&di=45acc0bf8fcc3859ab7760a27d0d034a&imgtype=0&src=http%3A%2F%2Fclubimg.dbankcdn.com%2Fdata%2Fattachment%2Fforum%2F201803%2F31%2F144641gaag2f225idc7yyy.gif"),
                Uri.parse("http://upfile.asqql.com/2009pasdfasdfic2009s305985-ts/2014-9/201491520352362114.gif"),
                Uri.parse("http://imgsrc.baidu.com/forum/w=580/sign=852e30338435e5dd902ca5d746c7a7f5/bd3eb13533fa828b6cf24d82fc1f4134960a5afa.jpg"),
                Uri.parse("http://pic.3h3.com/up/2014-3/20143314140858312456.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168118542&di=437ba348dfe4bd91afa5e5761f318cee&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201410%2F17%2F20141017094107_VdNJu.gif"),
                Uri.parse("http://img0.ph.126.net/deotqlphOfncBJA8SnbWUQ==/2008605433907336946.gif"),
                Uri.parse("http://imglf3.ph.126.net/0oF3rJPVSPm2ugnX6xSMXw==/1106478133466928383.gif"),
                Uri.parse("http://s4.sinaimg.cn/bmiddle/b4d49f27tdfe1a9907393&690"),
                Uri.parse("http://media.giphy.com/media/4a6cioskxhvuU/giphy-tumblr.gif"),
                Uri.parse("https://f12.baidu.com/it/u=3294379970,949120920&fm=72")
        );
        data1.pictureThumbList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168009921&di=fe2e9e5bc508130558a9954c2a8bd28f&imgtype=0&src=http%3A%2F%2Fxuexi.leawo.cn%2Fuploads%2Fallimg%2F160926%2F134225K60-2.gif"),
                Uri.parse("https://b-ssl.duitang.com/uploads/item/201206/29/20120629140234_QWAsX.thumb.700_0.gif"),
                Uri.parse("http://pic2.52pk.com/files/allimg/150324/104923F49-12.jpg"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168074673&di=45acc0bf8fcc3859ab7760a27d0d034a&imgtype=0&src=http%3A%2F%2Fclubimg.dbankcdn.com%2Fdata%2Fattachment%2Fforum%2F201803%2F31%2F144641gaag2f225idc7yyy.gif"),
                Uri.parse("http://upfile.asqql.com/2009pasdfasdfic2009s305985-ts/2014-9/201491520352362114.gif"),
                Uri.parse("http://imgsrc.baidu.com/forum/w=580/sign=852e30338435e5dd902ca5d746c7a7f5/bd3eb13533fa828b6cf24d82fc1f4134960a5afa.jpg"),
                Uri.parse("http://pic.3h3.com/up/2014-3/20143314140858312456.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168118542&di=437ba348dfe4bd91afa5e5761f318cee&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201410%2F17%2F20141017094107_VdNJu.gif"),
                Uri.parse("http://img0.ph.126.net/deotqlphOfncBJA8SnbWUQ==/2008605433907336946.gif"),
                Uri.parse("http://imglf3.ph.126.net/0oF3rJPVSPm2ugnX6xSMXw==/1106478133466928383.gif"),
                Uri.parse("http://s4.sinaimg.cn/bmiddle/b4d49f27tdfe1a9907393&690"),
                Uri.parse("http://media.giphy.com/media/4a6cioskxhvuU/giphy-tumblr.gif"),
                Uri.parse("https://f12.baidu.com/it/u=3294379970,949120920&fm=72")
        );


        Data data2 = new Data();
        data2.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data2.nickname = "苦涩";
        data2.createTime = "昨天 09:59";
        data2.content = "唐僧还是厉害啊。为什么？有宝马";
        data2.pictureList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168240132&di=226605ee54960b3135cbeebf12d1e219&imgtype=0&src=http%3A%2F%2Fphotocdn.sohu.com%2F20150928%2Fmp33561543_1443397655340_1.gif")
        );
        data2.pictureThumbList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168240132&di=226605ee54960b3135cbeebf12d1e219&imgtype=0&src=http%3A%2F%2Fphotocdn.sohu.com%2F20150928%2Fmp33561543_1443397655340_1.gif")
        );

        Data data3 = new Data();
        data3.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data3.nickname = "empty";
        data3.createTime = "昨天 08:12";
        data3.content = "各种眼神";
        data3.pictureList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168792516&di=b3ae4a05909fd4c6b903851553649fb0&imgtype=0&src=http%3A%2F%2Fwww.people.com.cn%2Fmediafile%2Fpic%2F20171013%2F33%2F16097621616938606825.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168265734&di=6dbf0daade2a0126fa6118ec3a185205&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20160917%2Fb8b605c1f286482b8e748f37528ccfd5.jpg"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168279697&di=dcd2b62878ad6c2c92e5bd7facfe6c3c&imgtype=0&src=http%3A%2F%2Fphotocdn.sohu.com%2F20151126%2Fmp44425938_1448498418499_2.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168288781&di=ae03ef1c5b30330f0fb8b8f0e955a737&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20161101%2F8f0f344ee011413297830dfd3dbb18f1.jpg")
        );
        data3.pictureThumbList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168792516&di=b3ae4a05909fd4c6b903851553649fb0&imgtype=0&src=http%3A%2F%2Fwww.people.com.cn%2Fmediafile%2Fpic%2F20171013%2F33%2F16097621616938606825.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168265734&di=6dbf0daade2a0126fa6118ec3a185205&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20160917%2Fb8b605c1f286482b8e748f37528ccfd5.jpg"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168279697&di=dcd2b62878ad6c2c92e5bd7facfe6c3c&imgtype=0&src=http%3A%2F%2Fphotocdn.sohu.com%2F20151126%2Fmp44425938_1448498418499_2.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168288781&di=ae03ef1c5b30330f0fb8b8f0e955a737&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20161101%2F8f0f344ee011413297830dfd3dbb18f1.jpg")
        );

        Data data4 = new Data();
        data4.avatar = "http://b167.photo.store.qq.com/psb?/V14EhGon2OmAUI/hQN450lNoDPF.dO82PVKEdFw0Qj5qyGeyN9fByKgWd0!/m/dJWKmWNZEwAAnull";
        data4.nickname = "empty";
        data4.createTime = "昨天 06:00";
        data4.content = "人与人间的信任，就像是纸片，一旦破损，就不会再回到原来的样子。";
        data4.pictureList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168299317&di=620d6bcb76de1d70e00ca4ed9aca58db&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F20164_12_16%2Fa8e9ix85375805036596.jpg"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168316681&di=840b4d0440543ae3f72c6c81270556b6&imgtype=0&src=http%3A%2F%2Fupfile.asqql.com%2F2009pasdfasdfic2009s305985-ts%2F2015-9%2F20159202352355149.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168810214&di=24078bdf9e747555965ead78f40f38af&imgtype=0&src=http%3A%2F%2Fs1.trueart.com%2F20180110%2F223107721_640.gif")
        );
        data4.pictureThumbList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168299317&di=620d6bcb76de1d70e00ca4ed9aca58db&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F20164_12_16%2Fa8e9ix85375805036596.jpg"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168316681&di=840b4d0440543ae3f72c6c81270556b6&imgtype=0&src=http%3A%2F%2Fupfile.asqql.com%2F2009pasdfasdfic2009s305985-ts%2F2015-9%2F20159202352355149.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168810214&di=24078bdf9e747555965ead78f40f38af&imgtype=0&src=http%3A%2F%2Fs1.trueart.com%2F20180110%2F223107721_640.gif")
        );

        Data data5 = new Data();
        data5.avatar = "http://qlogo3.store.qq.com/qzone/383559698/383559698/100?1416542262";
        data5.nickname = "越线龙马";
        data5.createTime = "前天 14:61";
        data5.content = "雪.触之即化..愿永久飘零";
        data5.pictureList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168338032&di=498a6e41472a6f49e3f95aa16a3c2402&imgtype=0&src=http%3A%2F%2Fwww.dahepiao.com%2Fuploads%2Fallimg%2F170630%2F26008-1F6301543125B.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168353920&di=ac499e09eec05c86871d6df539748445&imgtype=0&src=http%3A%2F%2Fimg1.ph.126.net%2FWmNQ-hcRrqIDTYBtms1W3A%3D%3D%2F6619465719002444731.gif")
        );
        data5.pictureThumbList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168338032&di=498a6e41472a6f49e3f95aa16a3c2402&imgtype=0&src=http%3A%2F%2Fwww.dahepiao.com%2Fuploads%2Fallimg%2F170630%2F26008-1F6301543125B.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168353920&di=ac499e09eec05c86871d6df539748445&imgtype=0&src=http%3A%2F%2Fimg1.ph.126.net%2FWmNQ-hcRrqIDTYBtms1W3A%3D%3D%2F6619465719002444731.gif")
        );

        Data data6 = new Data();
        data6.avatar = "http://b162.photo.store.qq.com/psb?/V14EhGon4cZvmh/z2WukT5EhNE76WtOcbqPIgwM2Wxz4Tb7Nub.rDpsDgo!/b/dOaanmAaKQAA";
        data6.nickname = "顺子要不起";
        data6.createTime = "圣诞节";
        data6.content = "颜宇扬的期末总结\n1、有本事冲我来，别再家长会上吓唬我爸\n2、期末考试成绩出来了，我觉得我妈生二胎是非常明智的选择\n3、这场考试对于我的意义就是知道了班上到底有多少人\n4、期末考试不给老师们露一手，他们还真以为自己教的好";
        data6.pictureList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168389150&di=2fd5c826af5394b62777fd132dff7d8f&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201701%2F17%2F20170117112406_zixk5.thumb.700_0.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168397716&di=909dcfb1bf7a7fe37041ec5914c34c4a&imgtype=0&src=http%3A%2F%2Fs7.rr.itc.cn%2Fg%2FwapChange%2F20159_11_19%2Fa4m9779610717481352.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168437965&di=f91b9c858eecf75799af00df525eab9a&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F201510_31_11%2Fa6cjhv9612585370352.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168451881&di=805580bb76614eba5dcc4668253b9749&imgtype=0&src=http%3A%2F%2Fs8.rr.itc.cn%2Fr%2FwapChange%2F201510_31_11%2Fa979a69612629324352.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168465415&di=8c7e9f70a33c4e442427f5e4bd21db1e&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F20162_24_14%2Fa69tdw8577863829596.gif")
        );
        data6.pictureThumbList = Arrays.asList(
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168389150&di=2fd5c826af5394b62777fd132dff7d8f&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201701%2F17%2F20170117112406_zixk5.thumb.700_0.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168397716&di=909dcfb1bf7a7fe37041ec5914c34c4a&imgtype=0&src=http%3A%2F%2Fs7.rr.itc.cn%2Fg%2FwapChange%2F20159_11_19%2Fa4m9779610717481352.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168437965&di=f91b9c858eecf75799af00df525eab9a&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F201510_31_11%2Fa6cjhv9612585370352.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168451881&di=805580bb76614eba5dcc4668253b9749&imgtype=0&src=http%3A%2F%2Fs8.rr.itc.cn%2Fr%2FwapChange%2F201510_31_11%2Fa979a69612629324352.gif"),
                Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168465415&di=8c7e9f70a33c4e442427f5e4bd21db1e&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F20162_24_14%2Fa69tdw8577863829596.gif")
        );

        if (System.currentTimeMillis() % 3 == 0) {
            dataList.add(data1);
            dataList.add(data2);
            dataList.add(data3);
            dataList.add(data4);
            dataList.add(data6);
        } else if (System.currentTimeMillis() % 3 == 1) {
            dataList.add(data5);
            dataList.add(data6);
            dataList.add(data2);
            dataList.add(data4);
            dataList.add(data3);
        } else {
            dataList.add(data1);
            dataList.add(data3);
            dataList.add(data4);
            dataList.add(data5);
            dataList.add(data6);
        }

        return dataList;
    }
}
