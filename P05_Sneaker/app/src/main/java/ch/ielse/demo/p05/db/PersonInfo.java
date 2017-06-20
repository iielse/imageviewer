package ch.ielse.demo.p05.db;

import ch.ielse.demo.p05.api.ApiEntity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

// {"err":0,"errMsg":"","data":{"userObjArr":{"1005002":{"userId":"1005002","nickName":"\u7ea2\u989c\u30fd\u7978\u6c34\uff08\u5bfc\u6e38\uff09","icon":"{\"url\":\"http:\\\/\\\/7xsewn.com1.z0.glb.clouddn.com\\\/FjcmVdpaYYm6xkMhjWdnro5c_WwQ\",\"image_width\":\"\",\"image_height\":\"\"}","lv":"1","integral":"45.00","evaluation":"0.00","myReply":null,"myReplyQuestions":null,"realName":null,"location":null,"gender":"0","birthday":"1990-01-01 00:00:00","legallyNumber":null,"legallyLanguages":null,"legallyPhotos":null,"legallyTime":"2016","canDriver":"0","canProvideLine":"0","canAnswer":"0","familiarPlace":null,"guideKeyword":null,"customTourism":"0","selfEvaluation":null,"lifeMotto":null,"status":"1","myQuestions":"[7]","footMarks":null,"focus":"[\"1005013\"]","blackList":null,"tourTime":"1","pushId":null,"isRecommend":"0","recommendTime":"0","gag":"0","applyTime":"0000-00-00 00:00:00","freeTime":"1","balance":"0.00","myInvitationCode":"21450500","fillInvitationCode":""}}},"selfChanged":[]}
@Entity
public class PersonInfo extends ApiEntity {
    @Transient
    public
    Wrapper data;

    static class Wrapper {
        PersonInfo userObj;
    }

    @Override
    protected Object extraReal() throws Exception {
        return data.userObj;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlatformId() {
        return this.platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    @Id
    public String userId;
    @Property
    public String platformId;

    @Generated(hash = 1367836791)
    public PersonInfo(String userId, String platformId) {
        this.userId = userId;
        this.platformId = platformId;
    }

    @Generated(hash = 1597442618)
    public PersonInfo() {
    }
}
