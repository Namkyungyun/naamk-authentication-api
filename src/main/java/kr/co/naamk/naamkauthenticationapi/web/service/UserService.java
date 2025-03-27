package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.type.SearchCommon;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.queryDSL.UserQueryDSL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryDSL userQueryDSL;

    public UserDto.SearchOption getSearch() {
        SearchCommon searchCommon = new SearchCommon();

        return UserDto.SearchOption.builder()
                .userStatus(searchCommon.userStatus)
                .penaltyStatus(searchCommon.penaltyStatus)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<UserDto> findList( UserDto.SearchRequest searchRequest, Pageable pageable) {

        Sort sort = Sort.by(
                Sort.Order.desc( "createdAt" )
        );
        pageable = PageRequest.of( Math.max( pageable.getPageNumber(), 0 ), pageable.getPageSize( ), sort );
        return userQueryDSL.findList( searchRequest, pageable );
    }


    @Transactional(readOnly = true)
    public UserDto.UserDetailResponse findById(Long id)  {
        return userQueryDSL.findById( id )
                .orElseThrow(() -> new ServiceException( ServiceMessageType.NOT_FOUND ) );
    }


}
