package com.practice.jwt1.jwt;


import com.practice.jwt1.dto.CustomUserDetails;
import com.practice.jwt1.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
// OncePerRequestFilter >> 요청에 대해서 한번만 동작하는 필터
public class JwtFilter extends OncePerRequestFilter {


    private final JWTUtil jwtUtil;


    public JwtFilter( JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    // 필터 내부 작업에 대한 구현
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // ( "Bearer" 가 접두사로 붙은) authorization 값을 request 에서 뽑아내서 검증 진행
        String authorization= request.getHeader("Authorization");

        log.info("JwtFilter - authoraztaion :"+authorization.toString());

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) { // 토큰이 아예 없거나 jwt 타입의 토큰이 아니면
            log.info("JwtFilter: token null");
            filterChain.doFilter(request, response); // 현재 필터에서 받은 request 와 response 를 다음 필터에게 넘겨주며 현재 필터는 종료함
            return; // 메서드 종료
        }

        log.info("JwtFilter : token get");
        // 순수 토큰 획득
            //  authorization.split(" ") >> 띄어쓰기 단위로 요소로 포함하는 String 타입 배열 반환
            // authorization.split(" ") [1] >> 생성된 문자열 배열에서 1 번째 객체 반환. 즉, bearer 접두사 다음 찐 토큰 부분
        String token = authorization.split( " ")[1];

        // 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            log.info("JwtFilter :token expired");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 유효한게 검증되면 해당 토큰을 기반으로 일시적인 세션을 하나 만들고, security context holder 라는 세션에다가 우리의 user를 일시적으로 저장시켜주면 다른 클래스에서 해당 정보를 뽑아서 권한을 확인 할 수 있게 된다

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        //userEntity를 생성하여 값 set
            // 굳이 dto 가 아니라 userEntity 로 만드는건 앞서 CustomUserDetails 에서 entity 를 기반으로 CustomUserDetails 객체를 생성하게 했었기 때문. 쩄뜬 CustomUserDetails 객체를 만들기 위한 과정
            // 주의 >> 비밀번호 값은 토큰에 담기지 않았었음. 그렇다고 여기서 비밀번호를 db에서 조회 매번 요청이 올때 마다 db 조회하는 꼴이 되버리기 떄문에, 여기선 찐 pw 값을 넣는다기 보다는 일시적인 password 값을 임의로 설정해서 넣어준다. 이미 인증이 되었는데 contex t hodler 에 정확한 비밀번호 입력할 필요가 굳이 없기 떄문ㄴ.
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword"); // 임시적인 비밀번호로 초기화
        userEntity.setRole(role);

        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        // UserDetails 객체를 기반으로 UsernamePasswordAuthenticationToken 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        //세션(SecurityContextHolder)에 사용자 등록 . 해당 user 에 대한 세션 생성
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response); // 다음 필터에 reqeust , response 넘겨주면서 현재 필터를 종료함

    }




}
