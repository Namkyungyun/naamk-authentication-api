package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.type.SearchCommon;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.PostMapper;
import kr.co.naamk.naamkauthenticationapi.web.dto.PostDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public PostDto.SearchOption getSearch( ) {
        SearchCommon searchCommon = new SearchCommon();

        return PostDto.SearchOption.builder()
                .penaltyStatus( searchCommon.penaltyStatus )
                .build();
    }

    @Transactional(readOnly = true)
    public Page< PostDto > findPostList(PostDto.SearchRequest searchRequest, Pageable pageable ) {
        Timestamp startDate = null;
        Timestamp endDate = null;
        if ( searchRequest.getStartDate() != null && searchRequest.getEndDate() != null ) {
            startDate = Timestamp.from( searchRequest.getStartDate().toInstant() );
            endDate = Timestamp.from( searchRequest.getEndDate().plusDays( 1 ).toInstant() );
        }

        pageable = PageRequest.of( Math.max( pageable.getPageNumber(), 0 ), pageable.getPageSize(), pageable.getSort() );

        Page< Map<String, Object> > pagedList = postRepository.findPostsWithPenalty(
                searchRequest.getPenaltyStatus(),
                searchRequest.getUserName(),
                searchRequest.getChannelName(),
                startDate,
                endDate,
                pageable
        );

        List< PostDto > postDtoList = PostMapper.INSTANCE.toPostDtoList( pagedList.getContent() );


        return new PageImpl<>(postDtoList, pageable, pagedList.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PostDto.PostDetailResponse findPostById(Long postId) {
        Map< String, Object > map = postRepository.findPostById( postId )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "not found post" ) );

        return PostMapper.INSTANCE.toPostDetailResponse( map );
    }


}
