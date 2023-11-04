package com.example.webswipeservice.config;

import com.example.webswipeservice.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Resource
    private JwtFilter jwtFilter;

    /*
     * 配置Spring Security的过滤器链的统一规则
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        //关闭csrf
        httpSecurity.csrf().disable();
        //将通过Session获取Security Context的功能禁用
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //对接口进行访问限制配置, 这些接口不需要用token进行验证, 用户可以直接访问
        httpSecurity.authorizeRequests().antMatchers(
                "/user/register",
                "/user/login",
                "/video/listall",
                "/video/list",
                "/video/listcategories"
//                "/userinteraction/act",
//                "/userinteraction/cancel"
        ).anonymous().anyRequest().authenticated();
        httpSecurity.cors();
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /*
     * 配置Spring Security中的密码加密方式
     * 由于Spring Security默认的PasswordEncoder需要再数据库字段值中以{encoderType}指定加密类型，涉及过多
     * 因此直接统一使用BCryptPasswordEncoder作为加密方式
     *
     * 注册接口处，存入数据库之前请注入PasswordEncoder并对密码加密
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
