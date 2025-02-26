package com.tpgr3;

public class MySleep {
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Sleep was interrupted");
        }
    }
}
