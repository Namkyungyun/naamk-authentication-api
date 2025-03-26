package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto paginationUsers( Pageable pageable) {

        Sort sort = Sort.by(
                Sort.Order.desc( "updatedAt" )
        );

        pageable = PageRequest.of( pageable.getPageNumber( ) <= 0 ? 0 : pageable.getPageNumber( ), pageable.getPageSize( ), sort );

        log.info( "pageable === {}", pageable.toString( ) );

//        Page< TbUsers > users = userRepository.

//        List< ContentViewDto > contentViewList = ContentMapper.INSTANCE.toContentViewList( contents.getContent( ) );
//        follwerLikeRepostFlagAdd( contentViewList, member.getId( ) );
//
//        return new PageImpl< ContentViewDto >( contentViewList, pageable, contents.getTotalElements( ) );
        return UserDto.builder().build();
    }


}
