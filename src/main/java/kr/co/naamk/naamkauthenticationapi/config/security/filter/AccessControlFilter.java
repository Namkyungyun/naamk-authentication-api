package kr.co.naamk.naamkauthenticationapi.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisRoleEntity;
import kr.co.naamk.naamkauthenticationapi.redis.service.RedisAccessService;
import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessControlFilter extends OncePerRequestFilter {

    private final RedisAccessService accessService;
    private final SecurityUtil securityUtil;

    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {

        /// 요청온 uri & http method
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 로그인은 제외 되도록 함.
        if (securityUtil.containsTokenExcludeUrl( uri ) || securityUtil.containsAccessExcludeUrl(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        /// 로그인 유저의 권한
        List< String > currentUserRoles = securityUtil.getCurrentUserRoles();

        /// 레디스에 저장된 전체 권한 정보 ( role 기반의 permissions(crud), menus )
        List<RedisRoleEntity> redisRoleAccess = StreamSupport
                .stream(accessService.getRedisRoleAuthorities().spliterator(), false)
                .toList();

        /// 유저의 권한을 필터링
        List<RedisRoleEntity> userRoleAccess = redisRoleAccess.stream()
                .filter( roleEntity ->  currentUserRoles.contains( roleEntity.getRoleName() ))
                .toList();


        boolean allowed = isAllowed(userRoleAccess, uri, method);

        if (!allowed) {
            throw new AccessDeniedException("check no access at access control filter.");
        }

        filterChain.doFilter(request, response);
    }


    private  boolean isAllowed(List<RedisRoleEntity> accessList, String uri, String method) {
        String menu = resolveMenu(uri);
        String requiredPerm = resolvePerm(uri, method); // e.g. GET -> R, POST -> C

        return accessList.stream().anyMatch(role ->
                role.getMenus() != null && role.getMenus().contains(menu) &&
                        role.getPerms() != null && role.getPerms().contains(requiredPerm)
        );
    }

    private static String resolveMenu(String uri) {
        // URI to menu mapping logic (customizable)
        if (uri.contains("/api/v1/users")) return "USERS_LIST";
        if(uri.contains( "api/v1/user-reports" )) return "USERS_REPORT";
        if (uri.contains("/api/v1/posts")) return "POST_LIST";
        if (uri.contains("/api/v1/post-reports")) return "POST_REPORT";
        return null;
    }

    private static String resolvePerm(String uri, String method) {
        if (method.equals("GET")) return "R";

        // 예외적으로 조회인 POST
        if (method.equals("POST") && uri.contains("/list")) return "R";

        return switch (method) {
            case "POST" -> "C";
            case "PUT", "PATCH" -> "U";
            case "DELETE" -> "D";
            default -> "";
        };
    }

}
