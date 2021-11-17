package com.zingplay.helpers;

import com.google.gson.Gson;
import com.zingplay.models.*;
import com.zingplay.models.Object;
import com.zingplay.module.objects.ConditionGame;
import com.zingplay.module.objects.ConditionConfig;
import com.zingplay.security.services.UserDetailsImpl;
import com.zingplay.socket.v3.TrackingCommon;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class Helpers {

    public String getGame(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        java.lang.Object principal = authentication.getPrincipal();
        if(principal instanceof UserDetailsImpl){
            return ((UserDetailsImpl) principal).getGame();
        }
        return null;
    }

    public String getCountry(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        java.lang.Object principal = authentication.getPrincipal();
        if(principal instanceof UserDetailsImpl){
            return ((UserDetailsImpl) principal).getCountry();
        }
        return null;
    }

    public String getUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        java.lang.Object principal = authentication.getPrincipal();
        if(principal instanceof UserDetailsImpl){
            return ((UserDetailsImpl) principal).getUsername();
        }
        return null;
    }
    private boolean isABiggerB(int start, int end){
        if(start == -1) return false;
        if(end == -1) return false;
        return start > end;
    }

    private boolean isABiggerB(float start, float end){
        if(start == -1) return false;
        if(end == -1) return false;
        return start > end;
    }
    private boolean isABiggerB(long start, long end){
        if(start == -1) return false;
        if(end == -1) return false;
        return start > end;
    }
    private boolean isNotMiddle(int start, int middle, int end){
        return isABiggerB(start, middle) || isABiggerB(middle, end);
    }
    private boolean isNotMiddle(long start, long middle, long end){
        return isABiggerB(start, middle) || isABiggerB(middle, end);
        // return (start <= middle) && (middle <= end);
    }
    private boolean isNotMiddle(float start, float middle, float end){
        return isABiggerB(start, middle) || isABiggerB(middle, end);
        // return (start <= middle) && (middle <= end);
    }
    public boolean isEnoughCondition(User user, ConditionObject condition, ConditionGame conditionGame){
        AtomicBoolean isEnoughCondition = new AtomicBoolean(true);
        if(conditionGame != null){
            _checkInListLong(user, condition, isEnoughCondition);
            _checkInListFloat(user, condition, isEnoughCondition);
            _checkInListObject(user, condition, isEnoughCondition);
            _checkInListStr(user, condition, isEnoughCondition);
            _checkRangeLong(user, condition, isEnoughCondition);
            _checkRangeFloat(user, condition, isEnoughCondition);
        }else{
            isEnoughCondition.set(false);
        }
        return isEnoughCondition.get();
    }
    private void _checkRangeFloat(User user, ConditionObject condition, AtomicBoolean isEnoughCondition) {
        HashMap<String, List<Float>> inRangeLong = condition.getInRangeFloat();
        if(inRangeLong != null){
            inRangeLong.forEach((key, value) -> {
                if(value.size() == 2){
                    Float min = value.get(0);
                    Float max = value.get(1);
                    Float userValue = user.getTrackingFloat(key);
                    if(isNotMiddle(min,userValue,max)){
                        isEnoughCondition.set(false);
                    }else{
                        System.out.println("user|" + user.getUserId() +"|pass|" + key +"|" + min +"|"+userValue  +"|" + max);
                    }
                }else{
                    isEnoughCondition.set(false);
                }
            });
        }
    }

    private void _checkRangeLong(User user, ConditionObject condition, AtomicBoolean isEnoughCondition) {
        HashMap<String, List<Long>> inRangeLong = condition.getInRangeLong();
        if(inRangeLong != null){
            inRangeLong.forEach((key, value) -> {
                if(value.size() == 2){
                    Long min = value.get(0);
                    Long max = value.get(1);
                    Long userValue = user.getTrackingLong(key);
                    if (min == null || max == null || userValue == null) {
                        isEnoughCondition.set(false);
                    } else if(isNotMiddle(min,userValue,max)){
                        isEnoughCondition.set(false);
                    } else{
                        System.out.println("user|" + user.getUserId() +"|pass|" + key +"|" + min +"|"+userValue  +"|" + max);
                    }
                }else{
                    isEnoughCondition.set(false);
                }
            });
        }
    }

    private void _checkInListStr(User user, ConditionObject condition, AtomicBoolean isEnoughCondition) {
        HashMap<String, List<String>> inListStr = condition.getInListStr();
        if(inListStr != null){
            inListStr.forEach((key, value) -> {
                String userValue = user.getTrackingStr(key);
                if (userValue != null) {
                    if(!value.contains(userValue)){
                        isEnoughCondition.set(false);
                    }else{
                        System.out.println("user|" + user.getUserId() +"|pass|" + key +"|" + userValue );
                    }
                } else {
                    isEnoughCondition.set(false);
                }
            });
        }
    }

    private void _checkInListObject(User user, ConditionObject condition, AtomicBoolean isEnoughCondition) {
        HashMap<String, List<ValueCondition>> inListObject = condition.getInListObject();
        if(inListObject != null){
            inListObject.forEach((key, value) -> {
                ValueCondition userValue = user.getTrackingObject(key);
                if (userValue != null) {
                    if(!listObjectContain(value, userValue)){
                        isEnoughCondition.set(false);
                    }else{
                        System.out.println("user|" + user.getUserId() +"|pass|" + key +"|" + userValue );
                    }
                } else {
                    isEnoughCondition.set(false);
                }
            });
        }
    }

    private boolean listObjectContain(List<ValueCondition> list, ValueCondition userValue) {
        for (ValueCondition valueCondition : list) {
            if (valueCondition.equals(userValue)) {
                return true;
            }
        }
        return false;
    }

    private void _checkInListFloat(User user, ConditionObject condition, AtomicBoolean isEnoughCondition) {
        HashMap<String, List<Float>> inListFloat = condition.getInListFloat();
        if(inListFloat != null){
            inListFloat.forEach((key, value) -> {
                Float userValue = user.getTrackingFloat(key);
                if (userValue != null) {
                    if(!value.contains(userValue)){
                        isEnoughCondition.set(false);
                    }else{
                        System.out.println("user|" + user.getUserId() +"|pass|" + key +"|" + userValue );
                    }
                } else {
                    isEnoughCondition.set(false);
                }
            });
        }
    }

    private void _checkInListLong(User user, ConditionObject condition, AtomicBoolean isEnoughCondition) {
        HashMap<String, List<Long>> inListLong = condition.getInListLong();
        if(inListLong != null){
            inListLong.forEach((key, value) -> {
                Long userValue = user.getTrackingLong(key);
                if (userValue != null) {
                    if(!value.contains(userValue)){
                        isEnoughCondition.set(false);
                    }else{
                        System.out.println("user|" + user.getUserId() +"|pass|" + key +"|" + userValue );
                    }
                } else {
                    isEnoughCondition.set(false);
                }
            });
        }
    }

    private void _checkRangeDuration(User user, ConditionObject condition, AtomicBoolean isEnoughCondition) {
        HashMap<String, List<Long>> inRangeDuration = condition.getInRangeDuration();
        if(inRangeDuration != null){
            inRangeDuration.forEach((key, value) -> {
                if(value.size() == 2){
                    Long min = value.get(0);
                    Long max = value.get(1);
                    Long userValue = user.getTrackingLong(key);
                    long now = System.currentTimeMillis()/1000;
                    userValue = Long.parseLong(String.valueOf(Math.floor(userValue - now)/86400));
                    if (min == null || max == null || userValue == null) {
                        isEnoughCondition.set(false);
                    } else if(isNotMiddle(min,userValue,max)){
                        isEnoughCondition.set(false);
                    } else{
                        System.out.println("user|" + user.getUserId() +"|pass|" + key +"|" + min +"|"+userValue  +"|" + max);
                    }
                }else{
                    isEnoughCondition.set(false);
                }
            });
        }
    }

    private void _checkInListDuration(User user, ConditionObject condition, AtomicBoolean isEnoughCondition) {
        HashMap<String, List<Long>> inListDuration = condition.getInListDuration();
        if(inListDuration != null){
            inListDuration.forEach((key, value) -> {
                Long userValue = user.getTrackingLong(key);
                long now = System.currentTimeMillis()/1000;
                userValue = Long.parseLong(String.valueOf(Math.floor(userValue - now)/86400));
                if (userValue != null) {
                    if(!value.contains(userValue)){
                        isEnoughCondition.set(false);
                    }else{
                        System.out.println("user|" + user.getUserId() +"|pass|" + key +"|" + userValue );
                    }
                } else {
                    isEnoughCondition.set(false);
                }
            });
        }
    }

    public void updateUserVer2ToVer3(User user, Object object, ConditionGame conditionGame) {

    }

    public boolean isEnoughCondition(User user, Object object, ConditionGame conditionGame){
        //auto convert condition to list
        Long trackingLong;
        if (user.getTimeOnline() != null) {
            trackingLong = user.getTrackingLong(TrackingCommon.TimeOnline);
            if(trackingLong == null){
                user.setTracking(TrackingCommon.TimeOnline, user.getTimeOnline().getTime(), ConditionConfig.DURATION);
            }
        }
        if (user.getChannelGame() != null) {
            trackingLong = user.getTrackingLong(TrackingCommon.ChannelIdx);
            if(trackingLong == null){
                user.setTracking(TrackingCommon.ChannelIdx, user.getChannelGame());
            }
        }
        trackingLong = user.getTrackingLong(TrackingCommon.TotalGame);
        if(trackingLong == null){
            user.setTracking(TrackingCommon.TotalGame, user.getTotalGame(), ConditionConfig.LONG);
        }

        ConditionObject condition = object.getCondition();
        if(condition != null){
            return isEnoughCondition(user,condition,conditionGame);
        }
        //LogLogic.getInstance().error("checkCondition|{}|{}", user.toString(), object.toString());
        //object.getChannelPayments();
        //object.getLastPaidAmounts();
        //
        //object.getAgeUserMin();
        //object.getAgeUserMax();
        //object.getTimeOnlineMin();
        //object.getTimeOnlineMax();
        //
        //object.getTotalGameMin();
        //object.getTotalGameMax();
        //object.getPaidTimesMin();
        //object.getPaidTimesMax();
        //object.getChannelMin();
        //object.getChannelMax();
        //object.getPaidTotalMin();
        //object.getPaidTotalMax();


        if(isNotMiddle(object.getChannelMin(), user.getChannelGame(), object.getChannelMax())) return false;
        if(isNotMiddle(object.getPaidTotalMin(), user.getTotalPaid(), object.getPaidTotalMax())) return false;
        if(isNotMiddle(object.getPaidTimesMin(), user.getTotalTimesPaid(), object.getPaidTimesMax())) return false;
        if(isNotMiddle(object.getTotalGameMin(), user.getTotalGame(), object.getTotalGameMax())) return false;

        Set<String> channelPayments = object.getChannelPayments();
        if(channelPayments != null && channelPayments.size()>0){
            //if(channelPayments.stream().noneMatch(s -> user.getChannelPayments().stream().anyMatch(s::equals))) return false;
            if(channelPayments.stream().noneMatch(s -> {
                Set<String> channelPayments1 = user.getChannelPayments();
                if(channelPayments1 != null){
                    return channelPayments1.stream().anyMatch(s::equals);
                }
                return false;
            })) return false;
        }
        Set<String> lastPaidAmounts = object.getLastPaidAmounts();
        if(lastPaidAmounts != null && lastPaidAmounts.size() > 0){
            if(lastPaidAmounts.stream().noneMatch(s -> {
                String lastPaidPack = user.getLastPaidPack();
                try {
                    if(lastPaidPack != null){
                        float xNum = Float.parseFloat(s);
                        float v = Float.parseFloat(lastPaidPack);
                        return xNum == v;
                    }
                }catch (NumberFormatException ignored){ }
                return s.equals(lastPaidPack) || (s.equals("0") && lastPaidPack == null);
            })) return false;
        }


        //-> chuyen sang mui gio so 7
        Date timeCreate = user.getTimeCreate();
        Date timeOnline = user.getTimeOnline();

        ZonedDateTime zCreate = timeCreate.toInstant().atZone(ZoneId.of("+7"));
        ZonedDateTime zOnline = timeOnline.toInstant().atZone(ZoneId.of("+7"));
        ZonedDateTime zNow = new Date().toInstant().atZone(ZoneId.of("+7"));

        int year = zNow.getYear();
        int dayOfYear = zNow.getDayOfYear();
        //Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("+7")));
        long daysCreated = (year - zCreate.getYear()) * 365  + (dayOfYear -  zCreate.getDayOfYear());
        long daysOnline = (year - zOnline.getYear()) * 365 + (dayOfYear -  zOnline.getDayOfYear());
        if(isNotMiddle(object.getAgeUserMin(), daysCreated, object.getAgeUserMax())) return false;
        if(isNotMiddle(object.getTimeOnlineMin(), daysOnline, object.getTimeOnlineMax())) return false;

        //long now = System.currentTimeMillis();
        //ZonedDateTime z1 = user.getTimeCreate().toInstant().atZone(ZoneId.of("+10"));
        //ZonedDateTime z2 = (new Date()).toInstant().atZone(ZoneId.of("+10"));
        //long between = ChronoUnit.DAYS.between(z1, z2);
        //
        //long timeCreate = user.getTimeCreate().getTime();
        //long ageUser = (now - timeCreate)/86_400_000;//24*60*60*1000
        //if(isNotMiddle(object.getAgeUserMin(), ageUser, object.getAgeUserMax())) return false;
        //
        //long timeOnline = user.getTimeOnline().getTime();
        //long online = (now - timeOnline)/86_400_000;//24*60*60*1000
        //if(isNotMiddle(object.getTimeOnlineMin(), online, object.getTimeOnlineMax())) return false;


        // LogLoggic.getInstance().error("checkCondition|{}|{}", user.getUserId(), "Pass");
        return true;
    }
    public static String getCurrencyFrom(Offer offer,String country){
        if(offer == null) return "";
        if(country == null || country.isEmpty()){
            country = "all";
        }
        String region = offer.getRegion();
        if(region != null && region.equals(country)){
            return offer.getCurrency();
        }
        Set<Price> prices = offer.getPrices();
        if(prices != null){
            for (Price price : prices) {
                if(price.getCountry().equals(country)){
                    return price.getCurrency();
                }
            }
        }
        return offer.getCurrency();
    }
    public static float getPriceFrom(Offer offer, String country){
        if(offer == null) return 0;
        if(country == null || country.isEmpty()){ country = "all"; }

        /*Set<Price> prices = offer.getPrices();
        float priceResult = 0;
        if(prices != null){
            for (Price price : prices) {
                String country1 = price.getCountry();
                if(country1.equals(country)){
                    return price.getPrice();
                }
                if("all".equals(country1)){
                    priceResult = price.getPrice();
                }
            }
        }
        if(priceResult != 0){ return priceResult; }

        String region = offer.getRegion();
        if(region != null && region.equals(country) || "all".equals(region)){
            return offer.getPrice();
        }

        return offer.getPrice();*/
        /* Một số ví dụ:
         * Nếu set giá default: ph(50php) + custom: all(100php) => user ở ph trả về giá 50php, user không phải ph trả về 100php
         * Nếu set giá default: all(100php) + custom: ph(50php) => user ở ph trả về giá 50php, user không phải ph trả về 100php
         * Nếu set giá default: ph(20php) + custom: ph(50php), all(100php) => user ở ph trả về giá 50php, user không phải ph trả về 100php
         * Nếu set giá default: all(20php) + custom: ph(50php), all(100php) => user ở ph trả về giá 50php, user không phải ph trả về 100php
         */

        //1. tìm country theo giá custom nếu có
        //2. tìm country theo giá default
        //3. tìm country all theo giá custom
        //4. tìm country all theo giá default

        com.zingplay.socket.v2.response.Price orDefault = null;
        Set<com.zingplay.models.Price> prices = offer.getPrices();
        //1.
        if(prices != null && !prices.isEmpty()){
            for (com.zingplay.models.Price price : prices) {
                if(country.equals(price.getCountry())){
                    return price.getPrice();
                }
            }
        }
        //2.
        String region = offer.getRegion();
        if(country.equals(region)){
            return offer.getPrice();
        }

        String countryAll = "all";
        //3.
        if(prices != null && !prices.isEmpty()) {
            for (com.zingplay.models.Price price : prices) {
                if (countryAll.equals(price.getCountry())) {
                    return price.getPrice();
                }
            }
        }
        //4.
        if(countryAll.equals(region)){
            return offer.getPrice();
        }
        return 0;
    }
    public static void main(String[] args) {
        Gson gson = new Gson();
        User u = gson.fromJson("{\"id\":\"6062dea93db7d3538d2a0213\",\"userId\":\"40548627\",\"channelGame\":3000,\"totalGame\":1932,\"timeCreate\":\"2020-07-08T07:15:33.000+00:00\",\"timeOnline\":\"2021-05-11T02:34:10.380+00:00\",\"game\":\"p4p\",\"country\":\"ph\",\"totalTimesPaid\":5,\"totalPaid\":300,\"lastPaidPack\":\"100\",\"channelPayments\":[\"IAP\"]}",User.class);
        Object o = gson.fromJson("{\"id\":\"609895b51ea73202ee568c7e\",\"idObject\":\"FRP1_4_P100\",\"game\":\"p4p\",\"country\":\"ph\",\"nameObject\":\"FRP1_4_P100\",\"note\":null,\"channelPayments\":null,\"lastPaidAmounts\":[\"100\"],\"paidTotalMin\":1,\"paidTotalMax\":-1,\"paidTimesMin\":1,\"paidTimesMax\":-1,\"totalGameMin\":0,\"totalGameMax\":-1,\"ageUserMin\":0,\"ageUserMax\":-1,\"timeOnlineMin\":0,\"timeOnlineMax\":14,\"channelMin\":0,\"channelMax\":-1,\"status\":1,\"totalUser\":287,\"timeCreate\":\"2021-05-10T02:08:53.228+00:00\",\"timeUpdate\":\"2021-05-10T02:14:07.225+00:00\"}",Object.class);

        Helpers h = new Helpers();
        boolean c = h.isEnoughCondition(u,o, null);
        Date date = new Date();
        Date date1 = new Date();
        date.setHours(20);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("+9"));
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("+9"));
        calendar.setTime(date);


        System.out.println(date);
        System.out.println(date1);
        long now = System.currentTimeMillis();

        long l = calendar1.getTime().getTime() - date.getTime();
        System.out.println(l);
        System.out.println(Math.ceil(l/86_400_000));

        ZonedDateTime z1 = date.toInstant().atZone(ZoneId.of("+7"));
        ZonedDateTime z2 = date1.toInstant().atZone(ZoneId.of("+7"));
        long days = (z2.getYear() - z1.getYear() + 1) * (z2.getDayOfYear() -  z1.getDayOfYear());
        long between = ChronoUnit.DAYS.between(z1, z2);
        System.out.println(days);
    }

    public static long getLongTime(String s){
        try {
            long l = Long.parseLong(s);
            return getLongTime(l);
        }catch (NumberFormatException e){
            throw e;
        }
    }
    public static long getLongTime(Long l){
        if(l < 99999999999L){
            return l * 1000;
        }
        return l;
    }
}
