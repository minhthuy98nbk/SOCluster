package com.zingplay.socket;

public class SocketConst {
    public static final String SEPARATOR = "\\|";
    public static final String PREFIX_KEY = "system_offer_dn";
    public static final String SEPARATOR_TOPIC = "_";
    public static final String BACK_END_TOPIC = "system_offer_dn_backend";
    public static final String BACK_END_TOPIC_TEST = "system_offer_game_country_test";

    public static final String ACTION_CONNECT = "connect";
    public static final String ACTION_OFFER = "offer";
    public static final String ACTION_LOGIN = "user_login";
    public static final String ACTION_TRACKING = "user_tracking";                       // v3: all user param
    public static final String ACTION_STATS_GAME = "stats_game";
    public static final String ACTION_USER_PAYMENT = "user_payment";
    public static final String ACTION_USER_BUY_OFFER = "user_buy_offer";
    public static final String ACTION_USER_REQUEST_OFFERS  = "user_request_offers";
    public static final String ACTION_GET_DATA_CUSTOM = "get_data_custom";              // v3: client request get gift
    public static final String ACTION_RECEIVE_DATA_CUSTOM = "receive_data_custom";      // v3: client tracking after receive gift

    public static final String ACTION_USER_REQUEST_OFFERS_V2 = "user_request_offers_v2";
    public static final String ACTION_GET_OFFER = "offer_id";
    public static final String ACTION_USER_BUY_OFFER_V2 = "buy_offer_v2";
    public static final String ACTION_OFFER_LOCAL_PRICE_V2 = "offer_v2";
    public static final String VERSION_1 = "1";
    public static final String VERSION_2 = "2";
    public static final String VERSION_3 = "3";

    public static final String MAP_STRING = "mapStrParams";
    public static final String MAP_LONG = "mapLongParams";
    public static final String MAP_FLOAT = "mapFloatParams";

}
