package com.liuruichao.time;

import java.time.Clock;

/**
 * java8 时钟类
 *
 * @author liuruichao
 * Created on 2015-12-17 17:06
 */
public class ClockTest1 {
    public static void main(String[] args) {
        //Clock clock = Clock.systemUTC();
        //System.out.println("utc clock : " + clock);

        //Clock clock = Clock.systemDefaultZone();
        //System.out.println(clock.instant().getNano());
        //System.out.println(clock.instant().getEpochSecond());
        //System.out.println(clock.instant());


        System.out.println(System.currentTimeMillis());
        System.out.println(System.nanoTime());
    }
}
