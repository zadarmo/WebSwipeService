package com.example.webswipeservice.controller.user;

import com.example.webswipeservice.modal.user.IUserDetails;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.modal.user.controller.RegisterUserInfo;
import com.example.webswipeservice.network.BaseResponse;
import com.example.webswipeservice.network.ResultUtils;
import com.example.webswipeservice.service.user.UserInfoService;
import com.example.webswipeservice.service.user.impl.UserDetailsServiceImpl;
import com.example.webswipeservice.tools.JwtUtil;
import com.example.webswipeservice.tools.QlyTool;
import com.example.webswipeservice.tools.RedisCache;
import com.qiniu.common.QiniuException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Value("${qly-user.access-key}")
    String accessKey;
    @Value("${qly-user.secret-key}")
    String secretKey;

    @Value("${qly-buckets.web-swipe-user-avatar.domain}")
    String avatarDomain;
    @Value("${qly-buckets.web-swipe-user-avatar.bucket}")
    String avatarBucket;

    @Autowired
    UserInfoService userInfoService;

    @Resource
    private UserDetailsServiceImpl userDetailService;

    @Resource
    private RedisCache redisCache;
    /*
     * 该接口被SpringSecurity放行，因此不经过jwt过滤器
     * 不引入RBAC，所以只需要进行 校验->token生成->信息缓存
     */
    @PostMapping("/login")
    public BaseResponse<Map<String,String>> login(@RequestBody UserInfo userInfo){
        HashMap<String, String> token = userInfoService.login(userInfo);
        if (token == null) {
            return ResultUtils.error(-1, "login failed");
        } else {
            System.out.println(token);
            return ResultUtils.success("login success", token);
        }
    }

    /**
     * 注册
     * @param
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Object> register(RegisterUserInfo registerUserInfo) throws QiniuException {
        if (userInfoService.register(registerUserInfo)) {
            return ResultUtils.success("register success", null);
        } else {
            return ResultUtils.error(-1, "Register failed, username exists!");
        }
    }

    /**
     * 用户登出
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Object> logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        long id = userInfo.getId();
        redisCache.deleteObject("userId:"+ id);
        return ResultUtils.success("logout success",null);
    }

    /**
     * 获取登录用户的信息
     * @return
     * @throws QiniuException
     */
    @GetMapping("/current")
    public BaseResponse<Object> getCurrentUser() throws QiniuException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = null;
        try {
            userInfo = (UserInfo) authentication.getPrincipal();
        }catch (Exception e){
            return ResultUtils.success("当前未登录",null);
        }
        if(Objects.isNull(userInfo)){
            return ResultUtils.success("当前未登录",new Object());
        }
        long expireInSeconds = 3600;

        String avatarUrl = QlyTool.buildQlySrcUrl(avatarDomain, false, userInfo.getAvatarKey(),
                expireInSeconds, accessKey, secretKey);
        userInfo.setAvatarUrl(avatarUrl);

        return ResultUtils.success("success",userInfo);
    }

    @PostMapping("/testJwt")
    public void testJwt(){
        //获取当前访问的用户(具体看JwtFilter)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        System.out.println(userInfo);
    }
}
