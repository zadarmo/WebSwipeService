package com.example.webswipeservice.modal.user.controller;

import com.example.webswipeservice.modal.user.UserInfo;

public class UserInfoHolder {
    private static ThreadLocal<UserInfo> userIdThreadLocal = new ThreadLocal<>();

    public static void setUserInfo(UserInfo userInfo) {
        userIdThreadLocal.set(userInfo);
    }

    public static UserInfo getUserInfo() {
        return userIdThreadLocal.get();
    }

    public static void clearUserInfo() {
        userIdThreadLocal.remove();
    }
}

