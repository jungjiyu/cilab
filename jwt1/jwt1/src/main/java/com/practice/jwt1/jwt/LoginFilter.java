package com.practice.jwt1.jwt;

import com.practice.jwt1.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {


    // 토큰을 새로 생성시킬 수 있는 대상
    private final JWTUtil jwtUtil;

    // authentication 객체를 관리할 수 있는 대상
    private final AuthenticationManager authenticationManager;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    // attemptAuthentication 필수로 오버라이드 필요
        // attemptAuthentication 은 UsernamePasswordAuthenticationFilter 의 메서드
        //Authentication >> 인증하는 인터페이스
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 요청을 가로채서 username 과 password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //AthenthicationManager 에게 해당 정보를 DTO역할을 하는UsernamePasswordAuthenticationToken이라는 바구니에 담아서 전달해줘야됨

        //UsernamePasswordAuthenticationToken(username값,password값,role값);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,password,null); // 일단 당장은 role 이 없으니 null 값넣었다

        // 생성한 토큰을 AthenthicationManager 에게 넘겨줌 --> authenticationManager 가 알아서 검증해줌
        return authenticationManager.authenticate(authToken);
    }


// successfulAuthentication , unsuccessfulAuthentication 는 UsernamePasswordAuthenticationFilter가 구현하는 인터페이스인 AbstractAuthenticationProcessingFilter 의 추상메서드

    // successfulAuthentication >> authenticationManager 가 성공적으로 검증을 마쳤을 때 실행되는 메서드 . 여기서 JWT 를 발급한다!!!
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        //  authentication.getPrincipal() >> 특정한 유저 확인
            // 반환형이 Object 라서 type cast 필요
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // customUserDetails 객체에서 username 뽑아오기
        String username = customUserDetails.getUsername();

        

        // customUserDetails 객체에서 role 값"들" 뽑아오기
            // customUserDetails 의 getAuthorities 구현 내용 보면 또 이런 Collenction<GrantedAuthority> 같은 거 확인 가능
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();


        // 토큰 받아오기
        String token = jwtUtil.createJwt(username,role,60*60*10L);
        response.addHeader("ffff","hhhh");
        response.addHeader("Authorization","Bearer " + token); // 키명 , "인증방식 "+token . 이때 인증방식 뒤 띄어쓰기 필수.
        log.info("Loginfilter>> "+"Bearer " + token);
//        HTTP 인증 방식은 RFC 7235 정의에 따라 아래 인증 헤더 형태를 가져야 한다.
//        Authorization: 타입 인증토큰
//예시
//        Authorization: Bearer 인증토큰string

        log.info("꜆₍ᐢ˶•ᴗ•˶ᐢ₎꜆!!success!!꜆₍ᐢ˶•ᴗ•˶ᐢ₎꜆");
    }


    // unsuccessfulAuthentication >> authenticationManager 가 검증을 실패했을때 실행되는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("(ꐦ •᷄ࡇ•᷅)!!fail!!(ꐦ •᷄ࡇ•᷅)");

        response.setStatus(400);
    }



}
