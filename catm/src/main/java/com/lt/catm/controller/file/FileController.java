package com.lt.catm.controller.file;

import cn.hutool.core.io.IoUtil;
import com.lt.catm.response.ResponseModel;
import com.lt.catm.utils.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "File")
@RestController
public class FileController {
    @Data
    @AllArgsConstructor
    public static class FileEntity {
        MultipartFile file;
    }

    @PostMapping(value = "/file",consumes = "multipart/form-data")
    @Operation(description = "获取随机公钥")
    public Mono<ResponseModel<String>> file(@RequestParam("file") MultipartFile file) throws IOException {
        FileUtil.fileUploader(IoUtil.readBytes(file.getInputStream()),"/test","text/plain");
        return Mono.just(ResponseModel.success(UUID.randomUUID().toString()));
    }
}
