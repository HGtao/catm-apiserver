package com.lt.catm.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lt.catm.exceptions.HttpException;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;

import java.util.Date;

public class Jwt {
    // 7天过期
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;
    private static final String SECRET = "q2bogyQFlXCSV81h";
    // HttpStatus httpStatus, int code, String msg
    private static final HttpException JwtAuthError = new HttpException(HttpStatus.UNAUTHORIZED, 1000, "jwt error");

    public static String create(AuthUser user) {
        // 使用给定的算法和密钥创建签名
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        // 添加自定义的 payload 信息
        JWTCreator.Builder builder =
                JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME));
        builder.withClaim("id", user.id);
        return builder.sign(algorithm);
    }

    public static AuthUser verify(HttpCookie jwtToken) throws HttpException {
        if (jwtToken != null) {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            try {
                DecodedJWT jwt = verifier.verify(jwtToken.getValue());
                return new AuthUser(jwt.getClaims().get("id").asInt());
            } catch (JWTVerificationException ignore) {
                // ignore
            }
        }
        throw JwtAuthError;
    }
}
