package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 阿里云配置属性类
 * /@ConfigurationProperties注解用于将外部配置文件中的属性值绑定到一个JavaBean上。
 * 这样可以方便地在应用程序中使用这些属性值。
 */
@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
