package kr.co.naamk.naamkauthenticationapi.domain.audit;

import kr.co.naamk.naamkauthenticationapi.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final SecurityUtil securityUtil;

    @Override
    public Optional<String> getCurrentAuditor() {
        String currentUserName = securityUtil.getCurrentUserName();

        return Optional.ofNullable(currentUserName);
    }
}