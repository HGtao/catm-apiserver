package com.lt.catm.controller.file;

import com.lt.catm.response.ResponseModel;
import com.lt.catm.utils.FileUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Tag(name = "File")
@RestController
public class FileController {
    @PostMapping(value = "/fileUpload", consumes = "multipart/form-data")
    public Mono<ResponseModel<String>> fileUpload(FilePart file) {
        return Mono.fromSupplier(file::content)
                .flatMap(dataBufferFlux->FileUtil.fileUploader(dataBufferFlux,
                        Objects.requireNonNull(file.headers().getContentType()).toString(),
                        file.filename()))
                .map(ResponseModel::success);
    }
}
