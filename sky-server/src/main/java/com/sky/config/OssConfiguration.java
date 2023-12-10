package com.sky.config;


import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  配置类用于创建 AliOssUtil 对象
 */
@Slf4j
@Configuration
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info(" 使用配置类中的BEAN 传入AliOssProperties 创建AliOssUtil对象 并返回");
        return new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
    }
}

/**
 * /@ConditionalOnMissingBean，它是修饰bean的一个注解，主要实现的是，
 * 当你的bean被注册之后，如果而注册相同类型的bean，就不会成功，它会保证你的bean只有一个，即你的实例只有一个。
 *
 *
 @Configuration
 public class UserConfigure {

 @Bean
 public MyTestInjection createBean1(){
 return new MyTestInjection();
 }

 @Bean
 public MyTestInjection createBean2(){
 return new MyTestInjection();
 }

 */
