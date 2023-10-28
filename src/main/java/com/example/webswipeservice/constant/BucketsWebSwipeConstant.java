package com.example.webswipeservice.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
// @PropertySource指定加载的那个属性文件，如是默认的application.properties 则不用指定
@PropertySource("classpath:application.yaml")
// prefix代表属性文件中的前缀
@ConfigurationProperties(prefix = "qly-buckets.web-swipe")
@Data
public class BucketsWebSwipeConstant {
    String domain;
    String bucket;
}
