package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.type.SearchCommon;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.utils.AESGCMEncryptionUtil;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AESGCMEncryptionUtil aesGCMEncryptionUtil;

    public UserDto.SearchOption getSearch( ) {
        SearchCommon searchCommon = new SearchCommon();

        return UserDto.SearchOption.builder()
                .userStatus( searchCommon.userStatus )
                .penaltyStatus( searchCommon.penaltyStatus )
                .build();
    }

    @Transactional(readOnly = true)
    public Page< UserDto > findUserList( UserDto.SearchRequest searchRequest, Pageable pageable ) {

        Timestamp startDate = null;
        Timestamp endDate = null;
        if ( searchRequest.getStartDate() != null && searchRequest.getEndDate() != null ) {
            startDate = Timestamp.from( searchRequest.getStartDate().toInstant() );
            endDate = Timestamp.from( searchRequest.getEndDate().plusDays( 1 ).toInstant() );
        }

        Sort sort = Sort.by(
                Sort.Order.desc( "createdAt" ),
                Sort.Order.desc( "id" )
        );

        pageable = PageRequest.of( Math.max( pageable.getPageNumber(), 0 ), pageable.getPageSize(), sort );

        Page< UserDto > object = userRepository.findUsersWithPenalty(
                searchRequest.getName(),
                searchRequest.getNickname(),
                searchRequest.getUserStatus(),
                searchRequest.getEmail(),
                searchRequest.getPenaltyStatus(),
                startDate,
                endDate,
                pageable );

        object.getContent().forEach( el -> {
                    String encryptedEmail = el.getEmail();
                    String decryptedEmail = aesGCMEncryptionUtil.decrypt( encryptedEmail );
                    el.setEmail( decryptedEmail );
                }
        );

        return object;
    }


    @Transactional(readOnly = true)
    public UserDto.UserDetailResponse findUserById( Long userId ) {
        UserDto.UserDetailResponse dto = userRepository.findUserById( userId )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND ) );

        String encryptedEmail = dto.getEmail();
        String decryptedEmail = aesGCMEncryptionUtil.decrypt( encryptedEmail );
        dto.setEmail( decryptedEmail );

        return dto;
    }


}
