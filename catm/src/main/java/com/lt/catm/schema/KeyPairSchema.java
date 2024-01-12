package com.lt.catm.schema;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class KeyPairSchema {
    // kid查找密钥对
    String kid;
    // 公钥内容
    String publicKey;
}
