package kr.co.naamk.naamkauthenticationapi.web.repository;

import kr.co.naamk.naamkauthenticationapi.domain.community.TbPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository< TbPost , Long > {

    @Query(nativeQuery = true, value = """
            SELECT
            	(COUNT(*) OVER()) - ROW_NUMBER() OVER (ORDER BY p.created_at DESC) + 1 AS rowNum,
            	 p.id,
            	 CASE
            	    WHEN p."type" = 'reply' THEN '댓글'
            	    WHEN p."type" = 'post' THEN '원글'
            	    ELSE p."type"
            	 END as type,
            	 p."content" as content,
            	 u."name" as userName,
            	 c."name" as channelName,
            	 COALESCE(ph.is_active, true) AS penalty, -- [true:정상 / false:차단]
            	 p.created_at as createdAt
            FROM community.posts p
            INNER JOIN public.users u on u.id = p.user_id
            INNER JOIN community.channels c on c.id = p.channel_id
            LEFT JOIN LATERAL (
            	SELECT ph1.is_active
            	FROM community.penalty_hist ph1
            	WHERE ph1.linked_id = p.id
            	  AND ph1.type = 'post'
            	ORDER BY ph1.created_at DESC
            	LIMIT 1
            	) ph ON true
             WHERE (:penaltyStatus IS NULL OR COALESCE(ph.is_active, true) = CAST(:penaltyStatus AS boolean))
              AND (:userName IS NULL OR u."name" LIKE CONCAT(:userName, '%'))
              AND (:channelName IS NULL OR c."name" LIKE CONCAT(:channelName, '%'))
              AND (CAST(:startDate AS timestamp) IS NULL OR p.created_at >= CAST(:startDate AS TIMESTAMP))
              AND (CAST(:endDate AS timestamp) IS NULL OR p.created_at < CAST(:endDate AS TIMESTAMP))
            ORDER BY p.created_at DESC
            """, countQuery = """
            SELECT count(*)
            FROM community.posts p
            INNER JOIN public.users u on u.id = p.user_id
            INNER JOIN community.channels c on c.id = p.channel_id
            LEFT JOIN LATERAL (
            	SELECT ph1.is_active
            	FROM community.penalty_hist ph1
            	WHERE ph1.linked_id = p.id
            	  AND ph1.type = 'post'
            	ORDER BY ph1.created_at DESC
            	LIMIT 1
            	) ph ON true
             WHERE (:penaltyStatus IS NULL OR COALESCE(ph.is_active, true) = CAST(:penaltyStatus AS boolean))
              AND (:userName IS NULL OR u."name" LIKE CONCAT(:userName, '%'))
              AND (:channelName IS NULL OR c."name" LIKE CONCAT(:channelName, '%'))
              AND (CAST(:startDate AS timestamp) IS NULL OR p.created_at >= CAST(:startDate AS TIMESTAMP))
              AND (CAST(:endDate AS timestamp) IS NULL OR p.created_at < CAST(:endDate AS TIMESTAMP))
            """)
    Page< Map<String, Object> > findPostsWithPenalty(
            @Param( "penaltyStatus" ) Boolean penaltyStatus,
            @Param( "userName" ) String userName,
            @Param( "channelName" ) String channelName,
            @Param( "startDate" )Timestamp startDate,
            @Param( "endDate" ) Timestamp endDate,
            Pageable pageable
            );


    @Query(nativeQuery = true, value= """
            SELECT
                p.id AS postId,                             -- 게시글 ID
                p.created_at AS createdAt,                  -- 등록 일시
                p.content AS content,                       -- 게시글 내용
                                                            -- 글/채널 노출 상태 에서 글 노출 상태 [true: 등록 / false:삭제]
                CASE WHEN p.is_active THEN '등록' ELSE '삭제' END AS postStatus,
                                                            -- 글/채널 노출 상태 에서 채널 제상태 [true: 노출 / false:미노출]
                CASE WHEN COALESCE(ph_channel.is_active, true) THEN '노출' ELSE '미노출' END AS channelPenaltyStatus,
            
                ph_admin.is_active AS penalty,              -- 어드민 제재 상태
            
                c.name AS channelName,                      -- 채널 ID
                c.nick_nm AS channelNickName,               -- 채널명
                u.name AS userName,                         -- 작성자 ID
                u.id as userId,                             -- 작성자 SEQ
            
                COALESCE(pph.totalSum, 0) AS popScore,      -- POP SCORE 합계
                COALESCE(l.totalCount, 0) AS likeCount,     -- like 총수
                reply_stats.reply_count AS replyCount,      -- 댓글 총수
                f.thumbs AS thumbs                          -- 첨부 파일 list
            
            FROM community.posts p
            INNER JOIN community.channels c ON p.channel_id = c.id
            INNER JOIN public.users u ON p.user_id = u.id
            
            -- POP 합계
            LEFT JOIN (
                SELECT post_id, SUM(point_qty) AS totalSum
                FROM community.popping_hist
                WHERE is_active = true
                GROUP BY post_id
            ) pph ON pph.post_id = p.id
            
            -- 좋아요 수
            LEFT JOIN (
                SELECT post_id, COUNT(*) AS totalCount
                FROM community.likes
                WHERE is_active = true
                GROUP BY post_id
            ) l ON l.post_id = p.id
            
            -- 최신 어드민 제재
            LEFT JOIN LATERAL (
                SELECT ph1.is_active
                FROM community.penalty_hist ph1
                WHERE ph1.linked_id = p.id
                  AND ph1.type = 'post'
                ORDER BY ph1.created_at DESC
                LIMIT 1
            ) ph_admin ON true
            
            -- 최신 채널 제재
            LEFT JOIN LATERAL (
                SELECT ph.is_active
                FROM community.penalty_hist ph
                JOIN public.users u1 ON u1.name = p.created_by
                WHERE ph.linked_id = p.id
                  AND ph.type = 'post'
                ORDER BY ph.created_at DESC
                LIMIT 1
            ) ph_channel ON true
            
            -- 첨부파일 리스트
            LEFT JOIN LATERAL (
                SELECT array_agg(f1.thumb_s_url) AS thumbs
                FROM public.files f1
                WHERE f1.type = 'post'
                  AND f1.linked_id = p.id
            ) f ON true
            
            -- 댓글 수 (재귀)
            LEFT JOIN LATERAL (
                WITH RECURSIVE replies AS (
                    SELECT p1.id, p1.parent_id
                    FROM community.posts p1
                    WHERE p1.parent_id = p.id AND p1.type = 'reply'
            
                    UNION ALL
            
                    SELECT p2.id, p2.parent_id
                    FROM community.posts p2
                    INNER JOIN replies r ON p2.parent_id = r.id
                    WHERE p2.type = 'reply'
                )
                SELECT COUNT(*) AS reply_count FROM replies
            ) reply_stats ON true
            WHERE p.id = :postId
            """)
    Optional<Map<String, Object>> findPostById(@Param( "postId" ) Long postId);
}
