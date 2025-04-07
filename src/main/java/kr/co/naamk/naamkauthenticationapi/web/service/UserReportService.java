package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbReportsHist;
import kr.co.naamk.naamkauthenticationapi.domain.type.SearchCommon;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.UserReportMapper;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserReportDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.ReportHistRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserReportService {

    private final ReportHistRepository reportHistRepository;
    private final UserRepository userRepository;

    public UserReportDto.SearchOption getSearch( ) {
        SearchCommon searchCommon = new SearchCommon();

        return UserReportDto.SearchOption.builder()
                .reportStatus( searchCommon.reportStatus )
                .penaltyStatus( searchCommon.penaltyStatus )
                .build();
    }

    @Transactional(readOnly = true)
    public Page< UserReportDto > findAllUserReport( UserReportDto.SearchRequest searchRequest, Pageable pageable ) {

        Timestamp startDate = null;
        Timestamp endDate = null;
        if ( searchRequest.getStartDate() != null && searchRequest.getEndDate() != null ) {
            startDate = Timestamp.from( searchRequest.getStartDate().toInstant() );
            endDate = Timestamp.from( searchRequest.getEndDate().plusDays( 1 ).toInstant() );
        }

        pageable = PageRequest.of( Math.max( pageable.getPageNumber(), 0 ), pageable.getPageSize() );


        Page< Map< String, Object > > page = reportHistRepository.findAllUserReports(
                searchRequest.getReportStatus(),
                searchRequest.getPenaltyStatus(),
                searchRequest.getReportedName(),
                searchRequest.getPenaltyCreatedBy(),
                startDate,
                endDate,
                pageable
        );

        List< UserReportDto > contents = UserReportMapper.INSTANCE.toUserReportDtoList( page.getContent() );


        return new PageImpl<>( contents, pageable, page.getTotalElements() );
    }


    @Transactional(readOnly = true)
    public UserReportDto.UserDetailResponse findLatestUserReport( Long userId ) throws ServiceException {
        TbUsers user = userRepository.findById( userId )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "not found user" ) );

        Map< String, Object > map = reportHistRepository.findLatestUserReport( user.getId(), "user" )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "not found report" ) );

        if ( map.isEmpty() ) {
            throw new ServiceException( ServiceMessageType.NOT_FOUND, "not found report" );
        }

        UserReportDto.UserDetailResponse dto = UserReportMapper.INSTANCE.toUserReportDetailResponse( map );
        dto.setReportedUserProfileUrl( "http://52.78.70.99:9002/profile/" + userId );

        return dto;
    }


    @Transactional(readOnly = true)
    public Map< String, Object > findUserReportHist( Long userId, Pageable pageable ) {
        TbUsers user = userRepository.findById( userId )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "not found user" ) );


        pageable = PageRequest.of( Math.max( pageable.getPageNumber(), 0 ), pageable.getPageSize() );
        Page< Map< String, Object > > page = reportHistRepository.findUserReportHists(
                user.getId(),
                "user",
                pageable
        );

        List< UserReportDto.ReportHistResponse > contents = UserReportMapper.INSTANCE.toReportHistResponseList( page.getContent() );
        PageImpl< UserReportDto.ReportHistResponse > pageImpl = new PageImpl<>( contents, pageable, contents.size() );

        int newReportCount = reportHistRepository.countByTypeAndIsActiveTrueAndLinkedId( "user", userId );

        Map< String, Object > result = new HashMap<>();
        result.put( "pagenation", pageImpl );
        result.put( "newReportCount", newReportCount );

        return result;
    }

    @Transactional(readOnly = true)
    public Integer countNewReportByLinkedId( Long linkedId ) {
        return reportHistRepository.countByTypeAndIsActiveTrueAndLinkedId( "user", linkedId );
    }


    /// test 용
    @Transactional(rollbackFor = Exception.class)
    public Object createUserReport( UserReportDto.CreateRequest dto ) {
        Long linkedId = dto.getLinkedId();
        Long minId = 74L;

        List< TbReportsHist > entityList = new ArrayList<>();
        for ( Long i = linkedId; i >= minId; i-- ) {
            if ( i.equals( dto.getUserId() ) ) {
                continue;
            }

            Optional< TbUsers > user = userRepository.findById( i );
            if(user.isEmpty()) {
                System.out.println(i);
                continue;
            }
            if ( user.isPresent() ) {
                UserReportDto.CreateRequest testUser = UserReportDto.CreateRequest.builder()
                        .userId( dto.getUserId() )
                        .linkedId( i )
                        .type( "user" )
                        .build();
                ;

                TbReportsHist entity = UserReportMapper.INSTANCE.toEntity( testUser );
                entity.setIsActive( true );
                TbReportsHist save = reportHistRepository.save( entity );

                entityList.add( save );
            }
        }

        return entityList;
    }


}
