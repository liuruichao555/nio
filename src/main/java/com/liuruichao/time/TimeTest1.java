package com.liuruichao.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * java8 new time api
 *
 * @author liuruichao
 * Created on 2015-12-17 16:47
 */
public class TimeTest1 {
    public static void main(String[] args) {
        LocalDateTime curDateTime = LocalDateTime.now();
        LocalDateTime dateTime = curDateTime.plusDays(5);
        System.out.println("curDateTime : " + curDateTime);
        System.out.println("DayOfMonth : " + curDateTime.getDayOfMonth());
        System.out.println("DayOfYear : " + curDateTime.getDayOfYear());
        System.out.println("DayOfWeek : " + curDateTime.getDayOfWeek());

        System.out.println(curDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println(curDateTime.isBefore(dateTime)); // true
        System.out.println(curDateTime.isAfter(dateTime)); // false
    }
}
