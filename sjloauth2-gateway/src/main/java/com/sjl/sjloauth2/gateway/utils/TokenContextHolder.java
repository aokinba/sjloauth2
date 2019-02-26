package com.sjl.sjloauth2.gateway.utils;

public class TokenContextHolder {

    public static ThreadLocal<String> context = new ThreadLocal<String>();

    public static String getToken() {
        return context.get();
    }

    public static void set(String token) {
        context.set(token);
    }

    public static void shutdown() {
        context.remove();
    }
}
