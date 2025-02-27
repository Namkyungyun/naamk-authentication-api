package kr.co.naamk.naamkauthenticationapi.utils;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.domain.TbRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public List< GrantedAuthority > getAuthorities( List< TbRoles > roles ) {
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
}
