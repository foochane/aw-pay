package com.foochane.awpay.utils;

import com.foochane.awpay.common.utils.IdWorker;

public class TestIdWorker {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
//            System.out.println(IdWorker.get32UUID());
            System.out.println(IdWorker.getId());
        }
    }
}
