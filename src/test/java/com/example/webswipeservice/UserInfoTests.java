package com.example.webswipeservice;

import com.example.webswipeservice.constant.EnumSex;
import com.example.webswipeservice.mapper.user.UserInfoMapper;
import com.example.webswipeservice.modal.user.UserInfo;
import com.example.webswipeservice.service.user.UserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest(classes = WebSwipeServiceApplication.class)
class UserInfoTests {

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserInfoService userInfoService;

    @Test
    void TestInsertByMapper() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("bbb");
        userInfo.setPassword("ccc");
        userInfo.setAge(20);
        userInfo.setSex(EnumSex.FEMALE);

        Integer result = userInfoMapper.insert(userInfo);
        // 检测插入用户的id
        long key = userInfo.getId();
        System.out.println("key:"+key);

    }

    @Test
    void TestDeleteByService() {
        userInfoService.removeById(2);
    }
}
