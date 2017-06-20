package ch.ielse.demo.p05


import ch.ielse.demo.p05.business.BalanceJournalInfo
import ch.ielse.demo.p05.db.PersonInfo

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    /**
     * 登录
     * @param platform  登录类型(1#手机号;2#微信;3#QQ;4#微博)
     * @param client    终端类型(1#Android客户端)
     * @param username  用户名
     * @param password  密码
     * @param userType  用户类型(1#游客;2#导游)
     */
    @FormUrlEncoded
    @POST("account/login")
    fun login(@Field("platform") platform: String, @Field("client") client: String, @Field("platformId") username: String,
              @Field("password") password: String, @Field("userType") userType: String): Observable<PersonInfo>

    /**
     * 注册
     * @param client        终端类型(1#Android客户端)
     * @param platformId    用户名
     * @param password      密码
     * @param userType      用户类型(1#游客;2#导游)
     * @param smsCode       短信验证码[4位纯数字]
     * @param fillInvitationCode    邀请码
     */
    @FormUrlEncoded
    @POST("account/register")
    fun register(@Field("client") client: String, @Field("platformId") platformId: String, @Field("password") password: String,
                 @Field("userType") userType: String, @Field("smsCode") smsCode: String, @Field("fillInvitationCode") fillInvitationCode: String): Observable<PersonInfo>

    /**
     *
     */
    @FormUrlEncoded
    @POST("client/getUserObjWithUserIds")
    fun queryPersonDetailByIds(@Field("userIds") userIds: String): Observable<String>


    /**
     * 请求资金流水记录
     * @param startIndex 从第几个开始查
     * @param num        每页查多少个
     * @return
     */
    @FormUrlEncoded
    @POST("client/getUserBalanceLog")
    fun queryBalanceJournal(@Field("startIndex") startIndex: Int, @Field("num") num: Int): Observable<BalanceJournalInfo>

}
