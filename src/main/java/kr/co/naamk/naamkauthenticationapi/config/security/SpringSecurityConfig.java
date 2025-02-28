package kr.co.naamk.naamkauthenticationapi.config.security;

import kr.co.naamk.naamkauthenticationapi.config.security.exception.SecurityException;
import kr.co.naamk.naamkauthenticationapi.config.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final SecurityException securityException;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public BCryptPasswordEncoder passwordEncoder( ) {
        return new BCryptPasswordEncoder();
    }


    /// JWT 서버를 만들어서 연결 (Session 사용 x)
    @Bean
    public SecurityFilterChain filterChain( HttpSecurity http ) throws Exception {
        // iframe 허용 여부 (현재 허용 안함)
        http.headers( headers -> headers.frameOptions( HeadersConfigurer.FrameOptionsConfig::disable ) );

        // enabled 이면 postman 작동 안함.
        http.csrf( AbstractHttpConfigurer::disable );

        // cross origin resource (js 요청 거부)
        http.cors( cors -> cors.configurationSource( corsConfigurationSource() ) );

        // jSessionId를 서버쪽에서 관리
        http.sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) );

        // react, 앱에서 로그인 요청 (로그인 폼 안뜨게 설정)
        http.formLogin( AbstractHttpConfigurer::disable );

        // httpBasic은 브라우저가 팝업창을 이용해서 사용자 인증을 진행 (진행 안되게 설정)
        http.httpBasic( AbstractHttpConfigurer::disable );

        // Exception 가로채기
        http.exceptionHandling( handler
                -> handler
                .authenticationEntryPoint( ( request, response, authException ) -> securityException.unAuthorization( request, response ) ) // 401
                .accessDeniedHandler( (request, response, authException ) -> securityException.unAuthentication( request, response ) ) // 403
        );

        http.authorizeHttpRequests( req -> req
                .requestMatchers( "api/v1/login", "api/v1/auth/**", "api/v1/roles/**" ).permitAll()
                .anyRequest().authenticated()
        );

        // filter
        http.addFilterBefore( jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class );


        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource( ) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader( "*" );
        corsConfiguration.addAllowedMethod( "*" ); // GET, POST, PUT, DELETE (js 요청 허용)
        corsConfiguration.addAllowedOriginPattern( "*" ); // 모든 IP 주소 허용 (프론트엔드의 IP만 허용하도록 변경해도 됨.)
        corsConfiguration.setAllowCredentials( true ); // 클라이언트에서 쿠키 요청 허용


        // 주소 요청에 CorsConfiguration 설정 값 넣기 (현재는 모든 주소 요청에 )
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration( "/**", corsConfiguration );

        return source;
    }
}
