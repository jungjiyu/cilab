package com.practice.jwt1.config;


import com.practice.jwt1.jwt.JWTUtil;
import com.practice.jwt1.jwt.JwtFilter;
import com.practice.jwt1.jwt.LoginFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    private final JWTUtil jwtUtil;

    @Autowired
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,  JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil =jwtUtil;
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }



    // 암호화 위해 BCryptPaasswordEncoder 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception {

        //csrf 해제
        //세션 방식의 Spring security 의 경우 세션 방식이 고정되있어 csrf 공격에 필수적으로 대비해줘야되지만 jwt 는 stateless 하게 관리하기 떄문에 별다른 대비가 필요없다
        http.csrf(auth->auth.disable());


// jwt 로 로그인 할거라 FormLogin 방식, HttpBasic 모두 해제
        //From 로그인 방식 disable
        http.formLogin((auth) -> auth.disable());

        //http basic 인증방식 disable
        http.httpBasic((auth) -> auth.disable());


        // 경로별 인가 작업
        http.authorizeHttpRequests(auth->auth
                .requestMatchers("/login","/main","/join").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        // JWT를 통한 인증/인가를 위해서 세션을 STATELESS 상태로 설정
        http.sessionManagement(session->session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        // 직접 구현한 필터를 등록
            //addFilterAt() >> 딱 그자리에 필터를 등록. 즉, 특정원본필터를 대체
            //addFilterBefore( ) >> 특정필터 앞에 해당 필터 추가 
            //addFilterAfter( ) >> 특정필터 뒤에 해당 필터 추가





//        http.addFilterAt(new LoginFilter(), UsernamePasswordAuthenticationFilter.class); 하면 일단 안됨

        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);


        //JWTFilter 등록 >> LoginFilter 앞에다가
//        http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);



        return http.build(); // 빌더 타입으로 리턴
    }
}
