package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.HttpUtil;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties weChatProperties;
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {

        // 调用微信接口服务 进行登录校验
        String openid = getOpenId(userLoginDTO);
        // 对返回的数据进行校验
        if(openid == null){throw new LoginFailedException(MessageConstant.LOGIN_FAILED);}

        // 根据openid查询是否是新用户
        // 若是新用户则完成注册
        User user= userMapper.getByOpenid(openid);
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        // 返回user对象
        return user;
    }

    /**
     * 调用微信接口服务获取openID
     * @param userLoginDTO
     * @return
     */
    private String getOpenId(UserLoginDTO userLoginDTO){
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",userLoginDTO.getCode());
        map.put("grant_type","authorization_code");

        String json = HttpClientUtil.doGet(WX_LOGIN,map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
