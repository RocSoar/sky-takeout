package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOSSUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 通用接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/common")
@Tag(name = "通用接口")
public class CommonController {
    private final AliOSSUtil aliOSSUtil;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传:{}", file.getOriginalFilename());
        try {
            //  原始文件名
            String originalFilename = file.getOriginalFilename();
            //  截取原始文件名的后缀  123.jpg
            assert originalFilename != null;
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            //  构建新文件名
            String fileName = UUID.randomUUID() + ext;
//            文件的访问url
            String url = aliOSSUtil.upload(file.getBytes(), fileName);
            return Result.success(url);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
