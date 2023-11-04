package com.example.webswipeservice.controller.video;

import com.example.webswipeservice.modal.user.IUserDetails;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.network.BaseResponse;
import com.example.webswipeservice.network.ResultUtils;
import com.example.webswipeservice.service.user.impl.UserDetailsServiceImpl;
import com.example.webswipeservice.tools.JwtUtil;
import com.example.webswipeservice.tools.RedisCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserInfoController {


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
        IUserDetails userDetails = (IUserDetails) userDetailService.loadUserByUsername(userInfo.getUsername());

        String jwt = JwtUtil.createJWT(String.valueOf(userDetails.getUserInfo().getId()));
        HashMap<String, String> jwtTokenMap = new HashMap<>();
        jwtTokenMap.put("webSwipeToken",jwt);

        redisCache.setCacheObject("userId:"+userDetails.getUserInfo().getId(),
                userDetails.getUserInfo(),3, TimeUnit.HOURS);

        return ResultUtils.success("login success", jwtTokenMap);
    }

    @PostMapping("/testJwt")
    public void testJwt(){
        //获取当前访问的用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        System.out.println(userInfo);
    }
}
