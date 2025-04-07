package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbPost;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbReportsHist;
import kr.co.naamk.naamkauthenticationapi.domain.type.SearchCommon;
import kr.co.naamk.naamkauthenticationapi.mapstruct.PostReportMapper;
import kr.co.naamk.naamkauthenticationapi.mapstruct.UserReportMapper;
import kr.co.naamk.naamkauthenticationapi.web.dto.PostReportDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserReportDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.PostRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.ReportHistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReportService {
    private final ReportHistRepository reportHistRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public PostReportDto.SearchOption getSearch( ) {
        SearchCommon searchCommon = new SearchCommon();

        return PostReportDto.SearchOption.builder()
                .reportStatus( searchCommon.reportStatus )
                .penaltyStatus( searchCommon.penaltyStatus )
                .build();
    }

    @Transactional(readOnly = true)
    public Page< PostReportDto.ListResponse > findAllPostReport( PostReportDto.SearchRequest searchRequest, Pageable pageable ) {
        Timestamp startDate = null;
        Timestamp endDate = null;
        if ( searchRequest.getStartDate() != null && searchRequest.getEndDate() != null ) {
            startDate = Timestamp.from( searchRequest.getStartDate().toInstant() );
            endDate = Timestamp.from( searchRequest.getEndDate().plusDays( 1 ).toInstant() );
        }

        pageable = PageRequest.of( Math.max( pageable.getPageNumber(), 0 ), pageable.getPageSize() );

        Page< Map< String, Object > > page = reportHistRepository.findAllPostReports(
                searchRequest.getReportStatus(),
                searchRequest.getPenaltyStatus(),
                searchRequest.getReportedUserName(),
                searchRequest.getReportedChannelName(),
                searchRequest.getPenaltyCreatedBy(),
                startDate,
                endDate,
                pageable
        );

        List< PostReportDto.ListResponse > contents = PostReportMapper.INSTANCE.toListResponseDtoList( page.getContent() );


        return new PageImpl<>( contents, pageable, page.getTotalElements() );
    }


    /// test 용
    @Transactional(rollbackFor = Exception.class)
    public Object createPostReport( PostReportDto.CreateRequest dto ) {
        Long linkedId = dto.getLinkedId();
        Long minId = 4L;

        List< TbReportsHist > entityList = new ArrayList<>();
        for ( Long i = linkedId; i >= minId; i-- ) {
            if ( i.equals( dto.getLinkedId() ) ) {
                continue;
            }

            Optional< TbPost > post = postRepository.findById( i );
            if ( post.isEmpty() ) {
                continue;
            }

            if(!post.get().getIsActive()) {
                continue;
            }

            PostReportDto.CreateRequest testPost = PostReportDto.CreateRequest.builder()
                    .userId( dto.getUserId() )
                    .linkedId( i )
                    .type( "post" )
                    .isActive( true )
                    .build();
            ;

            TbReportsHist entity = PostReportMapper.INSTANCE.toEntity( testPost );
            TbReportsHist save = reportHistRepository.save( entity );

            entityList.add( save );
        }

        return entityList;
    }
}
