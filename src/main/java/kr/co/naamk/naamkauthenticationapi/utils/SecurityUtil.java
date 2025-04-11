package kr.co.naamk.naamkauthenticationapi.utils;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    final List<String> TOKEN_EXCLUDE_URLS = List.of(
            "/api/v1/login",
            "/api/v1/auth/refresh"
            // 여기에 제외할 URI 추가
    );

    final List<String> ACCESS_EXCLUDE_URLS = List.of(
            "/api/v1/auth/me",
            "/api/v1/logout",
            "/api/v1/menus/display-menutree",
            "/api/v1/penalty-hist/"
            // 여기에 제외할 URI 추가
    );

    public boolean containsTokenExcludeUrl( String url) {
        List< String > containsList = TOKEN_EXCLUDE_URLS.stream().filter( url::startsWith ).toList();

        return !containsList.isEmpty();

    }

    public boolean containsAccessExcludeUrl(String url) {
        List< String > containsList = ACCESS_EXCLUDE_URLS.stream().filter( url::startsWith ).toList();

        return !containsList.isEmpty();
    }


    public List< GrantedAuthority > getAuthorities( List< TbAdminRoles > roles ) {
        return roles.stream()
                .map( role -> new SimpleGrantedAuthority( role.getName() ) )
                .collect( toList() );
    }

    public List< String > getAuthorityNames( List< GrantedAuthority > roles ) {
        return roles.stream()
                .map( GrantedAuthority::getAuthority )
                .toList();
    }

    /// AuthenticationManager를 이용한 로그인 인증 시 사용
    public Authentication generateLoginAuthentication( String username, String rawPassword, List<GrantedAuthority> authorities) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken( username, rawPassword, authorities );

        AuthenticationManager authenticationManager = authenticationManagerBuilder.getObject();
        if (authenticationManager == null) {
            throw new IllegalStateException("AuthenticationManager is not properly configured.");
        }

        return authenticationManager.authenticate( authenticationToken );
    }


    /// JWT 인증 후 SecurityContextHolder 인증 객체 저장 시 사용
    public UsernamePasswordAuthenticationToken generateJWTAuthentication( HttpServletRequest request,
                                                                          String username,
                                                                          List< SimpleGrantedAuthority > authorities) {
        User principal = new User(username, "", authorities);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return authentication;
    }

    // 로그인 유저의 role 조회
    public List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = new ArrayList<>();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            log.info( "✅ 로그인 사용자: {}", username );
            log.info( "✅ 역할: {}", roles );
        }

        return roles;
    }

    // 로그인 유저의 username 조회
    public String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
        } else {
            username = "system";
        }

        return username;
    }

    /// context holder 저장값
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /// context holder 비우기
    public void clearContextHolder() {
        SecurityContextHolder.clearContext();
    }
}
