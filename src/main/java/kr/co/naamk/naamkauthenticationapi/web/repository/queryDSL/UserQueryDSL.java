package kr.co.naamk.naamkauthenticationapi.web.repository.queryDSL;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.naamk.naamkauthenticationapi.domain.common.QTbFiles;
import kr.co.naamk.naamkauthenticationapi.domain.common.QTbUsers;
import kr.co.naamk.naamkauthenticationapi.domain.community.QTbPenaltyHist;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbPenaltyHist;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserQueryDSL {

    private final JPAQueryFactory queryFactory;

    public Page< UserDto > findList( UserDto.SearchRequest searchRequest, Pageable pageable ) {
        QTbUsers users = QTbUsers.tbUsers;
        QTbPenaltyHist penaltyHist = QTbPenaltyHist.tbPenaltyHist;
        QTbPenaltyHist subPenalty = new QTbPenaltyHist( "subPenalty" );

        // 검색 조건
        BooleanExpression userStatusFilter = searchRequest.getUserStatus() != null ? users.role.startsWith( searchRequest.getUserStatus() ) : null;
        BooleanExpression penaltyStatusFilter = searchRequest.getPenaltyStatus() != null ? penaltyHist.isActive.eq( searchRequest.getPenaltyStatus() ) : null;
        BooleanExpression nameFilter = searchRequest.getName() != null ? users.name.contains( searchRequest.getName() ) : null;
        BooleanExpression nicknameFilter = searchRequest.getNickname() != null ? users.nickname.contains( searchRequest.getNickname() ) : null;
        BooleanExpression emailFilter = searchRequest.getEmail() != null ? users.email.contains( searchRequest.getEmail() ) : null;
        BooleanExpression dateFilter = null;
        if ( searchRequest.getStartDate() != null && searchRequest.getEndDate() != null ) {
            Timestamp startDate = Timestamp.from( searchRequest.getStartDate().toInstant() );
            Timestamp endDate = Timestamp.from( searchRequest.getEndDate().plusDays( 1 ).toInstant() );

            dateFilter = users.createdAt.goe( startDate ).and( users.createdAt.lt( endDate ) );
        }

        // 📌 가장 최신 패널티 1건을 서브쿼리로 구성
        JPQLQuery< TbPenaltyHist > latestPenaltySubquery = JPAExpressions
                .selectFrom( subPenalty )
                .where( subPenalty.linkedId.eq( users.id ) )
                .orderBy( subPenalty.createdAt.desc() )
                .limit( 1 );

        // 📄 실제 콘텐츠 조회
        List< UserDto > contents = queryFactory
                .select( Projections.constructor( UserDto.class,
                        users.id,
                        users.name,
                        users.nickname,
                        users.email,
                        users.role,  // 별칭 사용!
                        Expressions.cases()
                                .when( penaltyHist.isActive.isNull() )
                                .then( true )
                                .otherwise( penaltyHist.isActive ).as( "penalty" ),
                        users.createdAt
                ) )
                .from( users )
                .leftJoin( penaltyHist )
                .on( penaltyHist.id.eq( latestPenaltySubquery.select( subPenalty.id ) ) )
                .where(
                        userStatusFilter,
                        penaltyStatusFilter,
                        nicknameFilter,
                        nameFilter,
                        emailFilter,
                        dateFilter
                )
                .offset( pageable.getOffset() )
                .limit( pageable.getPageSize() )
                .fetch();

        // 📊 total count (페이징을 위한 카운트 쿼리 — penaltyHist join 없이!)
        Long total = Optional.ofNullable(
                queryFactory
                        .select( users.count() )
                        .from( users )
                        .where(
                                userStatusFilter,
                                nameFilter,
                                emailFilter,
                                dateFilter
                        )
                        .fetchOne()
        ).orElse( 0L );

        return new PageImpl<>( contents, pageable, total );
    }

    public Optional<UserDto.UserDetailResponse> findById( Long id ) {
        QTbUsers users = QTbUsers.tbUsers;
        QTbPenaltyHist penaltyHist = QTbPenaltyHist.tbPenaltyHist;
        QTbPenaltyHist subPenaltyHist = QTbPenaltyHist.tbPenaltyHist;
        QTbFiles files = QTbFiles.tbFiles;

        // 📌 가장 최신 패널티 1건을 서브쿼리로 구성
        JPQLQuery< TbPenaltyHist > latestPenaltySubquery = JPAExpressions
                .selectFrom( subPenaltyHist )
                .where( subPenaltyHist.linkedId.eq( users.id ),
                        subPenaltyHist.type.eq( "user" )
                )
                .orderBy( subPenaltyHist.createdAt.desc() )
                .limit( 1 );

        UserDto.UserDetailResponse user = queryFactory
                .select( Projections.constructor( UserDto.UserDetailResponse.class,
                        users.id,
                        users.name,
                        users.nickname,
                        users.email,
                        users.intro,
                        users.role,
                        users.createdAt,
                        files.thumbSUrl,
                        Expressions.cases()
                                .when( penaltyHist.isActive.isNull() )
                                .then( true )
                                .otherwise( penaltyHist.isActive ).as( "penalty" )
                ) )
                .from( users )
                .leftJoin( files )
                .on(files.linkedId.eq( users.id ).and( files.type.eq( "profile" ) ))

                .leftJoin( penaltyHist )
                .on( penaltyHist.id.eq( latestPenaltySubquery.select( subPenaltyHist.id ) ) )
                .where( users.id.eq( id ) )
                .fetchOne();

        return (user != null) ? Optional.of( user ) : Optional.empty();
    }
}
