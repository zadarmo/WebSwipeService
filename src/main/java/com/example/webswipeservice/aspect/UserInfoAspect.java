package com.example.webswipeservice.aspect;

import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.user.controller.UserInfoHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserInfoAspect {
    @Before("execution(* com.example.webswipeservice.service.user.UserInfoService.logout(..))" +
            "|| execution(* com.example.webswipeservice.service.user.UserInfoService.getCurrentUser(..))" +

            "|| execution(* com.example.webswipeservice.service.video.VideoInfoService.selectByUserId(..))" +
            "|| execution(* com.example.webswipeservice.service.video.VideoInfoService.selectInteractionVideo(..))" +

            "|| execution(* com.example.webswipeservice.service.userinteraction.UserInteractionService.add(..))" +
            "|| execution(* com.example.webswipeservice.service.userinteraction.UserInteractionService.delete(..))"
    )
    public void getUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfo userInfo = (UserInfo) authentication.getPrincipal();
            UserInfoHolder.setUserInfo(userInfo);
        } catch (Exception e){
            System.out.println(e.getMessage());
            UserInfoHolder.setUserInfo(null);
        }
    }
}
