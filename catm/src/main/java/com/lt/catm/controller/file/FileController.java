package com.lt.catm.controller.file;

import cn.hutool.core.io.IoUtil;
import com.lt.catm.response.ResponseModel;
import com.lt.catm.utils.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Tag(name = "File")
@RestController
public class FileController {
    @PostMapping(value = "/fileUpload", consumes = "multipart/form-data")
    public Mono<ResponseModel<String>> fileUpload(FilePart file) throws IOException {
        return Mono.fromSupplier(file::content)
                .flatMap(FileUtil::fileUploader)
                .map(ResponseModel::success);
    }
}
