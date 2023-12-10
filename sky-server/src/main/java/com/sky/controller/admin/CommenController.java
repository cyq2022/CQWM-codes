package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Api("通用接口")
@RestController
@RequestMapping("/admin/common")
public class CommenController {


    @Autowired
    private AliOssUtil aliOssUtil;
    @ApiOperation("图片文件上传接口")
    @PostMapping("/upload")
    /**
     *  MultipartFile file 参数名称要与前端提交的参数名称相同
     */
    public Result<String> upload(MultipartFile file) {
        //返回图片url
        log.info("文件上传: {}",file);
        //  xxxx.jpg
        String extension = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString()  + extension.substring(extension.lastIndexOf("."));
        try {
            String url = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(url);

        } catch (IOException e) {
            log.info(" 图片上传失败异常：{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
