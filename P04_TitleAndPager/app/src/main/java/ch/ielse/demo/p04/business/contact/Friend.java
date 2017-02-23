package ch.ielse.demo.p04.business.contact;


public class Friend {
    public static final String ONLINE = "[在线]";
    public static final String ONLINE_COMPUTER = "[电脑在线]";
    public static final String ONLINE_4G = "[4G在线]";
    public static final String ONLINE_3G = "[3G在线]";
    public static final String ONLINE_WIFI = "[Wi-Fi在线]";
    public static final String ONLINE_IPHONE = "[iPhone在线]";
    public static final String ONLINE_PHONE = "[手机在线]";
    public static final String OFFLINE = "[离线请留言在线]";

    private String nickname;
    private String avatar;
    private String state;
    private String motto;

    Group group;

    public Friend(String nickname, String avatar, String state, String motto) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.state = state;
        this.motto = motto;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
