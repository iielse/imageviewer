package github.iielse.imageviewer.demo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun e() {
    val page1 = "[{\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/20190715/me_0-1563200961.618469\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"(◕ H ◕)\",\n" +
            "    \"roomNo\": 511257,\n" +
            "    \"roomOwnerId\": 410128,\n" +
            "    \"roomOwnerNickName\": \"(◕ H ◕)\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/20190715/me_0-1563200961.618469\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/20190921/me_0-1569009105.980272\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"sini kuy\",\n" +
            "    \"roomNo\": 114128,\n" +
            "    \"roomOwnerId\": 14127,\n" +
            "    \"roomOwnerNickName\": \"ifitt\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/20190921/me_0-1569009105.980272\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/20190909/me_0-1568015233.561832\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"Cà khịa mọi người ui \uD83E\uDD70\uD83D\uDE18\",\n" +
            "    \"roomNo\": 1807011,\n" +
            "    \"roomOwnerId\": 1346930,\n" +
            "    \"roomOwnerNickName\": \"\uD83C\uDF1FHS.Pon Cưng\uD83C\uDF1F\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/20190909/me_0-1568015233.561832\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/20190901/register_0-1567351873.8413858\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"hello mn✌\uD83C\uDFFC\",\n" +
            "    \"roomNo\": 1737997,\n" +
            "    \"roomOwnerId\": 1278009,\n" +
            "    \"roomOwnerNickName\": \"[ND] CÚNN\uD83C\uDF53\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/20190901/register_0-1567351873.8413858\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/18201-1568118760945\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"[JD]N\",\n" +
            "    \"roomNo\": 118204,\n" +
            "    \"roomOwnerId\": 18201,\n" +
            "    \"roomOwnerNickName\": \"[JD]N\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/18201-1568118760945\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/18972-1563897100975.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"\uD83D\uDE18\",\n" +
            "    \"roomNo\": 118975,\n" +
            "    \"roomOwnerId\": 18972,\n" +
            "    \"roomOwnerNickName\": \"DoniA\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/18972-1563897100975.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/716256-1565635947518.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"مساء الخيير \uD83D\uDC9C\",\n" +
            "    \"roomNo\": 1175247,\n" +
            "    \"roomOwnerId\": 716256,\n" +
            "    \"roomOwnerNickName\": \"NoNy\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/716256-1565635947518.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/19863-1563799135564.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"bntu supportny  \uD83D\uDE4F\",\n" +
            "    \"roomNo\": 119866,\n" +
            "    \"roomOwnerId\": 19863,\n" +
            "    \"roomOwnerNickName\": \"[KOB] ᴅᴇʏs•ʳᵃ̈́ˣ̈́\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/19863-1563799135564.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/1008883-1565012543776.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"اسماء\",\n" +
            "    \"roomNo\": 1468481,\n" +
            "    \"roomOwnerId\": 1008883,\n" +
            "    \"roomOwnerNickName\": \"اسماء\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/1008883-1565012543776.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/20190907/me_0-1567852815.8663669\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"use ơi\",\n" +
            "    \"roomNo\": 1665038,\n" +
            "    \"roomOwnerId\": 1205158,\n" +
            "    \"roomOwnerNickName\": \"\uD83C\uDF38Duyên Baby\uD83C\uDF38\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/20190907/me_0-1567852815.8663669\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/16197-1568829846572\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"يارب\",\n" +
            "    \"roomNo\": 116199,\n" +
            "    \"roomOwnerId\": 16197,\n" +
            "    \"roomOwnerNickName\": \"bebo\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/16197-1568829846572\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/483490-1567823381482\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"music\",\n" +
            "    \"roomNo\": 584871,\n" +
            "    \"roomOwnerId\": 483490,\n" +
            "    \"roomOwnerNickName\": \"Vivielle\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/483490-1567823381482\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/30305-1558239762430.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"ياااااارب التارجت بقااا\uD83D\uDE2D\uD83D\uDE2D\uD83D\uDE2D\",\n" +
            "    \"roomNo\": 130315,\n" +
            "    \"roomOwnerId\": 30305,\n" +
            "    \"roomOwnerNickName\": \"\uD83D\uDC85\uD83D\uDC84loliiiii\uD83C\uDF3C\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/30305-1558239762430.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/19377-1564675511867.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"[McD] Gracella\",\n" +
            "    \"roomNo\": 119380,\n" +
            "    \"roomOwnerId\": 19377,\n" +
            "    \"roomOwnerNickName\": \"[McD] Gracella\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/19377-1564675511867.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/22363-1562250897238.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"hai\",\n" +
            "    \"roomNo\": 122366,\n" +
            "    \"roomOwnerId\": 22363,\n" +
            "    \"roomOwnerNickName\": \"[KOB] Christy\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/22363-1562250897238.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/20190924/me_0-1569294296.057159\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"MIMI [MCD]\",\n" +
            "    \"roomNo\": 581573,\n" +
            "    \"roomOwnerId\": 480199,\n" +
            "    \"roomOwnerNickName\": \"MIMI [MCD]\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/20190924/me_0-1569294296.057159\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/20190828/me_0-1566970123.035431\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"Hôm nay Sồ lại live \",\n" +
            "    \"roomNo\": 1702281,\n" +
            "    \"roomOwnerId\": 1242351,\n" +
            "    \"roomOwnerNickName\": \"\uD83C\uDF1FHS.Hansel\uD83C\uDF1F\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/20190828/me_0-1566970123.035431\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/20190901/register_0-1567354441.867106\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"a ăn cơm chưa vậy\uD83D\uDE02\uD83D\uDE02\uD83D\uDE02\",\n" +
            "    \"roomNo\": 1738379,\n" +
            "    \"roomOwnerId\": 1278391,\n" +
            "    \"roomOwnerNickName\": \"\uD83C\uDF1FHS•zin\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/20190901/register_0-1567354441.867106\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/469085-1561535283130.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"Suport Event Musik\uD83D\uDE18\",\n" +
            "    \"roomNo\": 570429,\n" +
            "    \"roomOwnerId\": 469085,\n" +
            "    \"roomOwnerNickName\": \"{KOB} Manies \",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/469085-1561535283130.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/473352-1566034586120.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"hellow\",\n" +
            "    \"roomNo\": 574704,\n" +
            "    \"roomOwnerId\": 473352,\n" +
            "    \"roomOwnerNickName\": \"[FM] Tiffany\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/473352-1566034586120.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://cdn.unicolive.com/cover%2F91.png\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"siang\",\n" +
            "    \"roomNo\": 1424670,\n" +
            "    \"roomOwnerId\": 965136,\n" +
            "    \"roomOwnerNickName\": \"FM Lala\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://graph.facebook.com/2383563368431574/picture?width\\u003d512\\u0026height\\u003d512\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/566930-1568146220282\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"@Britzone English Club\",\n" +
            "    \"roomNo\": 668571,\n" +
            "    \"roomOwnerId\": 566930,\n" +
            "    \"roomOwnerNickName\": \"[KM]Haphemoon\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/566930-1568146220282\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/195240-1568321854765\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"1jam\",\n" +
            "    \"roomNo\": 295751,\n" +
            "    \"roomOwnerId\": 195240,\n" +
            "    \"roomOwnerNickName\": \"[MCD]DedeMiongᶠᴶ\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/195240-1568321854765\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/19821-1567760875040\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"[MCD] sunflower\",\n" +
            "    \"roomNo\": 119824,\n" +
            "    \"roomOwnerId\": 19821,\n" +
            "    \"roomOwnerNickName\": \"[MCD] sunflower\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/19821-1567760875040\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/20190508/19234-1557318000834.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"Temanin \",\n" +
            "    \"roomNo\": 119237,\n" +
            "    \"roomOwnerId\": 19234,\n" +
            "    \"roomOwnerNickName\": \"[FM] Minnie Mō \",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/20190508/19234-1557318000834.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/474969-1568501179732\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"sejam lagi\",\n" +
            "    \"roomNo\": 576333,\n" +
            "    \"roomOwnerId\": 474969,\n" +
            "    \"roomOwnerNickName\": \"[FM] Zena \",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/474969-1568501179732\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/22891-1569138425374\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"\uD83C\uDF37LIAHAN2\uD83C\uDF37\",\n" +
            "    \"roomNo\": 122894,\n" +
            "    \"roomOwnerId\": 22891,\n" +
            "    \"roomOwnerNickName\": \"\uD83C\uDF37LIAHAN2\uD83C\uDF37\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/22891-1569138425374\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/36869-1565149139932.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"support me pliss\",\n" +
            "    \"roomNo\": 136884,\n" +
            "    \"roomOwnerId\": 36869,\n" +
            "    \"roomOwnerNickName\": \"[KOB]Agathano\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/36869-1565149139932.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://unic-check-img.uae-dubai.ufileos.com/20190913/me_0-1568327674.688939\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"challenge Old Age MakeUp\uD83E\uDD36\uD83C\uDFFB\",\n" +
            "    \"roomNo\": 1782829,\n" +
            "    \"roomOwnerId\": 1322783,\n" +
            "    \"roomOwnerNickName\": \"[MBA] Gigi\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/20190913/me_0-1568327674.688939\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}, {\n" +
            "    \"roomImageUrl\": \"http://static.unicolive.com/834819-1565349279690.jpg\",\n" +
            "    \"roomIndustryTypeId\": 0,\n" +
            "    \"roomName\": \"coins for camilla\",\n" +
            "    \"roomNo\": 1293848,\n" +
            "    \"roomOwnerId\": 834819,\n" +
            "    \"roomOwnerNickName\": \"FM Camilla\",\n" +
            "    \"roomOwnerProfilePicture\": \"http://static.unicolive.com/834819-1565349279690.jpg\",\n" +
            "    \"roomTypeId\": 0,\n" +
            "    \"specialTagId\": 0,\n" +
            "    \"status\": 0,\n" +
            "    \"updateTime\": 0,\n" +
            "    \"vipLevel\": 0\n" +
            "}]"


    val list = Gson().fromJson<List<C>>(page1, object : TypeToken<List<C>>() {}.rawType)
    val result =  list.map { S(it.roomImageUrl, it.roomName, it.roomNo) }
    println(Gson().toJson(result))

}

data class C(
        val roomImageUrl: String,
        val roomName: String,
        val roomNo: Int
)

data class S(
        val imageUrl: String,
        val title: String,
        val id: Int
)