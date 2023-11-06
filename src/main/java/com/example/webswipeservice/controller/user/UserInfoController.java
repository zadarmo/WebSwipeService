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
        // spring security的要求需要把UserInfo用IUserDetails封装一层
        IUserDetails userDetails = (IUserDetails) userDetailService.loadUserByUsername(userInfo.getUsername());

        // 根据用户信息生成token
        String jwt = JwtUtil.createJWT(String.valueOf(userDetails.getUserInfo().getId()));
        HashMap<String, String> jwtTokenMap = new HashMap<>();
        jwtTokenMap.put("webSwipeToken",jwt);

        // 存到redis, 用来查询token是否过期. 如果过期, 则说明用户需要重新登录. 用户成功登录则将token存入redis
        redisCache.setCacheObject("userId:"+userDetails.getUserInfo().getId(),
                userDetails.getUserInfo(),3, TimeUnit.HOURS);

        return ResultUtils.success("login success", jwtTokenMap);
    }

    /**
     * 注册
     * @param
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Object> register(RegisterUserInfo registerUserInfo) throws QiniuException {
        userInfoService.register(registerUserInfo);
        return ResultUtils.success("success", null);
    }

    @PostMapping("/logout")
    public BaseResponse<Object> logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        long id = userInfo.getId();
        redisCache.deleteObject("userId:"+ id);
        return ResultUtils.success("logout success",null);
    }

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
