package com.practice.jwt1.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Jwts;

@Slf4j
@Component
public class JWTUtil {
   // 객체키를 저장. 시그니쳐키.
    private SecretKey secretKey;

    // @Value 는 lombok 불러오는게 아니라 org.springframework.beans.factory.annotation.Value 임을 주의
    // 시크릿 키 불러오기
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        // application.prperties 의 시크릿키값을 기반으로 객체타입의 키 생성
        log.info("secret key:"+secret);
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    //검증 > 토큰을 전달 받아 내부의 Jwts 의 parser 를 이용해 분석
    //verifyWith( 저장하고있는객체키 ) >> 토큰이 우리 서버에서 생성되었는지 검사
    // parseSignedClaims( 클라이언트가준토큰 ) >> 클레임확인
    // getPayload().get("키명", 키의값타입.class)  >> 페이로드에서 특정데이터가져옴


    //username 뽑아냄 . 검증.
    public String getUsername(String token){ //JWT 토큰은 . 구분자를 사용하는 문자열
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username",String.class);

    }


// role 뽑아냄. 검증
    public String getRole(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role",String.class);
    }

    // 토큰완료여부확인. 소멸되면 true 아니면 false
    //.getExpiration().before(new Date() ) >> 현재를 기준으로 만료되었는지 확인
        // new Date( ) == 현재시간값
    public boolean isExpired(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());    }

    //토큰생성 
    // 로그인이 성공했을 떄 successfulHandelr 에 의해 실행됨
        // Jwts.builder() >> 토큰생성
        // claim( "키명", 값 ) 을 사용하여 토큰에 값을 저장 가능
        // .issuedAt(new Date(System.currentTimeMillis())) : 토큰의 발행시간을 현재로 저장
        // .expiration(new Date(System.currentTimeMillis() + ms단위의커스텀시간 )) : 토큰의 소멸시간을 저장
        //  .signWith(secretKey) : 시그니쳐키를 이용하여 암호화진행
        // .compact(); : 컴팩트 시킴
    public String createJwt(String username , String role, Long expiredMs){
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();    }


}
