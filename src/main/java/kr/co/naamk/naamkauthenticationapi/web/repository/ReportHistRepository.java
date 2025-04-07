package kr.co.naamk.naamkauthenticationapi.web.repository;

import kr.co.naamk.naamkauthenticationapi.domain.community.TbReportsHist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ReportHistRepository extends JpaRepository< TbReportsHist, Long > {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM community.reports_hist
            WHERE linked_id = :linkedId
            AND type = :type
            AND is_active = true
            """)
    List< TbReportsHist > findActiveReportsByLinkedIdAndType( @Param("linkedId") Long linkedId, @Param("type") String type );


    @Query(nativeQuery = true, value = """
            SELECT
                (COUNT(*) OVER()) - ROW_NUMBER() OVER (ORDER BY rh.created_at DESC) + 1 AS rowNum,
                rh.id,
                rh.created_at AS latestCreatedAt,
                rh.linked_id AS reportedUserId,
                u.name AS reportedUserName,
                (SELECT COUNT(*) from community.reports_hist where linked_id = rh.linked_id and type ='user') AS reportCount,
                CAST(rh.is_active AS boolean) AS report,
                CASE
                    WHEN rh.is_active = true THEN null
                    ELSE CAST(ph.is_active AS boolean)
                END AS penalty,
                -- 처리자
                CASE
                    WHEN rh.is_active = true THEN null
                    ELSE ph.created_by
                END AS penaltyCreatedBy,
                -- 처리 일시
                CASE
                    WHEN rh.is_active = true THEN null
                    ELSE ph.created_at
                END AS penaltyCreatedAt
            FROM (
                SELECT DISTINCT ON (linked_id) *
                FROM community.reports_hist
                WHERE type = 'user'
                ORDER BY linked_id, created_at desc) as rh
            LEFT JOIN LATERAL (
                  SELECT ph1.linked_id, ph1.is_active, ph1.created_at, ph1.created_by
                  FROM community.penalty_hist ph1
                  WHERE ph1.type = 'user'
                    AND ph1.linked_id = rh.linked_id
                  ORDER BY ph1.created_at DESC
                  LIMIT 1
              ) ph ON true
            LEFT JOIN public.users u ON u.id = rh.linked_id
            WHERE
                (:reportStatus IS NULL OR rh.is_active = CAST(:reportStatus AS BOOLEAN))
                AND (:penaltyStatus IS NULL OR (rh.is_active =false AND ph.is_active = CAST(:penaltyStatus AS boolean)))
                AND (:reportedName IS NULL OR u.name LIKE CONCAT(:reportedName, '%') )
                AND (:penaltyCreatedBy IS NULL OR (rh.is_active = false AND  ph.created_by LIKE CONCAT(:penaltyCreatedBy, '%')) )
                AND (CAST(:startDate AS timestamp) IS NULL OR rh.created_at >= CAST(:startDate AS TIMESTAMP))
                AND (CAST(:endDate AS timestamp) IS NULL OR rh.created_at < CAST(:endDate AS TIMESTAMP))
            ORDER BY rh.created_at DESC, rh.id DESC
            """,
            countQuery = """
                     SELECT COUNT(*)
                     FROM (
                         SELECT DISTINCT ON (linked_id) *
                         FROM community.reports_hist
                         WHERE type = 'user'
                         ORDER BY linked_id, created_at desc) as rh
                     LEFT JOIN LATERAL (
                           SELECT ph1.linked_id, ph1.is_active, ph1.created_at, ph1.created_by
                           FROM community.penalty_hist ph1
                           WHERE ph1.type = 'user'
                             AND ph1.linked_id = rh.linked_id
                           ORDER BY ph1.created_at DESC
                           LIMIT 1
                       ) ph ON true
                     LEFT JOIN public.users u ON u.id = rh.linked_id
                     WHERE
                         (:reportStatus IS NULL OR rh.is_active = CAST(:reportStatus AS BOOLEAN))
                         AND (:penaltyStatus IS NULL OR (rh.is_active =false AND ph.is_active = CAST(:penaltyStatus AS boolean)))
                         AND (:reportedName IS NULL OR u.name LIKE CONCAT(:reportedName, '%') )
                         AND (:penaltyCreatedBy IS NULL OR (rh.is_active = false AND  ph.created_by LIKE CONCAT(:penaltyCreatedBy, '%')) )
                         AND (CAST(:startDate AS timestamp) IS NULL OR rh.created_at >= CAST(:startDate AS TIMESTAMP))
                         AND (CAST(:endDate AS timestamp) IS NULL OR rh.created_at < CAST(:endDate AS TIMESTAMP))
                    """
    )
    Page< Map< String, Object > > findAllUserReports(
            @Param("reportStatus") Boolean reportStatus,
            @Param("penaltyStatus") Boolean penaltyStatus,
            @Param("reportedName") String reportedName,
            @Param("penaltyCreatedBy") String penaltyCreatedBy,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable );


    @Query(nativeQuery = true, value = """
            select
                rh.id,
                rh.linked_id as reportLinkId,
                u.name as reportedUserName,
                u.role as role,
                rh.is_active as report,
                CAST(ph.is_active AS boolean) as penalty,
                rh.created_at as latestCreatedAt,
                -- 처리자
                CASE
                    WHEN rh.is_active = true THEN null
                    ELSE ph.created_by
                END AS penaltyCreatedBy,
                -- 처리 일시
                CASE
                    WHEN rh.is_active = true THEN null
                    ELSE ph.created_at
                END AS penaltyCreatedAt,
                -- 처리 사유
                 CASE
                    WHEN rh.is_active = true THEN null
                    ELSE ph.description
                END AS penaltyDescription
            FROM (
                SELECT DISTINCT ON (linked_id)
                       *
                FROM community.reports_hist
                WHERE type = 'user'
                ORDER BY linked_id, created_at DESC
            ) rh
            LEFT JOIN LATERAL (
                SELECT
                    ph1.linked_id,
                    ph1.is_active,
                    ph1.created_at,
                    ph1.created_by,
                    ph1.description
                FROM community.penalty_hist ph1
                WHERE ph1.type = 'user'
                AND ph1.linked_id = rh.linked_id
                ORDER BY ph1.created_at DESC
                LIMIT 1
            ) ph ON true
            INNER JOIN public.users u ON u.id = rh.linked_id
            WHERE rh.linked_id = :userId
            """)
    Optional< Map< String, Object > > findLatestUserReport( @Param("userId") Long userId);


    @Query(nativeQuery = true, value = """
            SELECT
                (COUNT(*) OVER()) - ROW_NUMBER() OVER (ORDER BY rh.created_at DESC) + 1 AS rowNum,
                 rh.id,
                 rh.user_id AS reportUserId,
                 rh.linked_id AS reportedLinkId,
                 rh.is_active AS report,
                 ph.is_active AS penalty,
                 u.name AS reportCreatedBy,
                 rh.created_at AS reportCreatedAt
            FROM (
                 SELECT
                 rh1.id,
                 rh1.user_id,
                 rh1.linked_id,
                 rh1.is_active,
                 rh1.created_at,
                 rh1.updated_at
                 FROM community.reports_hist rh1
                 WHERE rh1.type = :type
                 AND rh1.linked_id = :linkedId ) as rh
            INNER JOIN public.users u on u.id = rh.user_id -- 신고한 유저 정보
            LEFT JOIN LATERAL ( -- 가장 최신 페널티 정
                SELECT *
                FROM community.penalty_hist ph1
                WHERE ph1.linked_id = rh.linked_id
                  AND ph1.type = :type
                  AND rh.is_active = false -- 신고 처리 완료된 상태
                  AND  rh.updated_at >= ph1.created_at
                ORDER BY ph1.created_at DESC
                LIMIT 1
            ) ph ON true
            ORDER BY rh.created_at DESC
            """,
            countQuery = """
                      SELECT COUNT(*)
                      FROM community.reports_hist rh1, public.users u1
                      WHERE rh1.type = :type
                      AND rh1.linked_id = u1.id
                      AND rh1.linked_id = :linkedId
                    """)
    Page< Map< String, Object > > findReportHistsByLinkedIdAndType( @Param("linkedId") Long linkedId,
                                                                    @Param("type") String type,
                                                                    Pageable pageable );


    /// post report
    @Query(nativeQuery = true, value = """
            SELECT
            	(COUNT(*) OVER()) - ROW_NUMBER() OVER (ORDER BY rh.created_at DESC) + 1 AS rowNum,
            	rh.id,                                  -- report seq
                rh.created_at AS latestCreatedAt,       -- report created at
                rh.linked_id AS reportedPostId,         -- post seq
                u."name" AS reportedUserName,           -- 작성자 ID
                c."name" AS reportedChannelName,        -- 채널 ID
                rc.report_count as reportCount,         -- 해당 포스트에 대한 총 신고 건수
                CAST(rh.is_active AS boolean) AS report,-- 신고처리 여부 [true:접수 / false:처리완료]
                CASE                                    -- 제재 처리 상태 [true:정상 / false;차단]
                    WHEN rh.is_active = true THEN null
                    ELSE CAST(ph.is_active AS boolean)
                END AS penalty,
                CASE                                    -- 제재 처리자
                    WHEN rh.is_active = true THEN null
                    ELSE ph.created_by
                END AS penaltyCreatedBy,
                CASE                                    -- 제재 처리 일시
                    WHEN rh.is_active = true THEN null
                    ELSE ph.created_at
                END AS penaltyCreatedAt
            -- 일차로 포스트에 대한 linked_id 를 가져오기
            FROM (
                    SELECT DISTINCT ON (linked_id) *
                    FROM community.reports_hist
                    WHERE type = 'post'
                    ORDER BY linked_id, created_at desc) as rh
            -- 최신 제재 건
            LEFT JOIN LATERAL (
                      SELECT ph1.linked_id, ph1.is_active, ph1.created_at, ph1.created_by
                      FROM community.penalty_hist ph1
                      WHERE ph1.type = 'post'
                    AND ph1.linked_id = rh.linked_id
                  ORDER BY ph1.created_at DESC
                  LIMIT 1
             ) ph ON true
             -- 포스트
             INNER JOIN community.posts p ON p.id = rh.linked_id
             -- 사용자
             LEFT JOIN public.users u ON u.id = p.user_id
             -- 채널
             LEFT JOIN community.channels c ON c.id = p.channel_id
             -- 전체 신고 건수
             LEFT JOIN (
              SELECT linked_id, COUNT(*) AS report_count
              FROM community.reports_hist
              WHERE type = 'post'
              GROUP BY linked_id
            ) rc ON rc.linked_id = rh.linked_id
            WHERE
                (:reportStatus IS NULL OR rh.is_active = CAST(:reportStatus AS BOOLEAN))
                AND (:penaltyStatus IS NULL OR (rh.is_active =false AND ph.is_active = CAST(:penaltyStatus AS boolean)))
                AND (:reportedUserName IS NULL OR u."name" LIKE CONCAT(:reportedUserName, '%') )
                AND (:reportedChannelName IS NULL OR c."name" LIKE CONCAT(:reportedChannelName, '%') )
                AND (:penaltyCreatedBy IS NULL OR (rh.is_active = false AND  ph.created_by LIKE CONCAT(:penaltyCreatedBy, '%')) )
                AND (CAST(:startDate AS timestamp) IS NULL OR rh.created_at >= CAST(:startDate AS TIMESTAMP))
                AND (CAST(:endDate AS timestamp) IS NULL OR rh.created_at < CAST(:endDate AS TIMESTAMP))
            ORDER BY rh.created_at DESC, rh.id desc
            """, countQuery = """
            SELECT count(*)
            -- 일차로 포스트에 대한 linked_id 를 가져오기
            FROM (
                    SELECT DISTINCT ON (linked_id) *
                    FROM community.reports_hist
                    WHERE type = 'post'
                    ORDER BY linked_id, created_at desc) as rh
            -- 최신 제재 건
            LEFT JOIN LATERAL (
                      SELECT ph1.linked_id, ph1.is_active, ph1.created_at, ph1.created_by
                      FROM community.penalty_hist ph1
                      WHERE ph1.type = 'post'
                    AND ph1.linked_id = rh.linked_id
                  ORDER BY ph1.created_at DESC
                  LIMIT 1
             ) ph ON true
             -- 포스트
             INNER JOIN community.posts p ON p.id = rh.linked_id
             -- 사용자
             LEFT JOIN public.users u ON u.id = p.user_id
             -- 채널
             LEFT JOIN community.channels c ON c.id = p.channel_id
            WHERE
                (:reportStatus IS NULL OR rh.is_active = CAST(:reportStatus AS BOOLEAN))
                AND (:penaltyStatus IS NULL OR (rh.is_active =false AND ph.is_active = CAST(:penaltyStatus AS boolean)))
                AND (:reportedUserName IS NULL OR u."name" LIKE CONCAT(:reportedUserName, '%') )
                AND (:reportedChannelName IS NULL OR c."name" LIKE CONCAT(:reportedChannelName, '%') )
                AND (:penaltyCreatedBy IS NULL OR (rh.is_active = false AND  ph.created_by LIKE CONCAT(:penaltyCreatedBy, '%')) )
                AND (CAST(:startDate AS timestamp) IS NULL OR rh.created_at >= CAST(:startDate AS TIMESTAMP))
                AND (CAST(:endDate AS timestamp) IS NULL OR rh.created_at < CAST(:endDate AS TIMESTAMP))
            """)
    Page< Map< String, Object > > findAllPostReports( @Param("reportStatus") Boolean reportStatus,
                                                      @Param("penaltyStatus") Boolean penaltyStatus,
                                                      @Param("reportedUserName") String reportedUserName,
                                                      @Param("reportedChannelName") String reportedChannelName,
                                                      @Param("penaltyCreatedBy") String penaltyCreatedBy,
                                                      @Param("startDate") Timestamp startDate,
                                                      @Param("endDate") Timestamp endDate,
                                                      Pageable pageable
    );


    @Query(nativeQuery = true, value = """
            WITH lastest_reports AS (
              SELECT *, ROW_NUMBER() OVER (PARTITION BY linked_id ORDER BY created_at DESC) AS rn
              FROM community.reports_hist
              WHERE type = 'post' AND linked_id = :postId
            )
            SELECT
             rh.id,                                     -- report seq
             rh.is_active as report,                    -- report active
             rh.created_at as latestCreatedAt,          -- 최근 신고 일시
             u.id as reportedUserId,                    -- 신고된 사용자 seq
             u.name as reportedUserName,                -- 신고된 사용자 명
             c.id as reportedChannelId,                 -- 신고된 채널 seq
             c.name as reportedChannelName,             -- 신고된 채널 명
             p.id as reportedPostId,                    -- 신고된 게시글 seq
             p.is_active as reportedPostActive,         -- 신고된 게시글 active
             p.content as reportedPostContent,          -- 신고된 게시글 내용
             -- 제재 처리
            CAST(ph.is_active AS boolean) as penalty,  -- 제재 처리 active
            CASE                                        -- 처리 일시
                WHEN rh.is_active = true THEN null
                ELSE ph.created_at
            END AS penaltyCreatedAt,
            CASE                                        -- 처리자
                WHEN rh.is_active = true THEN null
                ELSE ph.created_by
            END AS penaltyCreatedBy,
             CASE                                       -- 처리 사유
                WHEN rh.is_active = true THEN null
                ELSE ph.description
            END AS penaltyDescription
            
            FROM lastest_reports rh
            LEFT JOIN LATERAL (
                SELECT
                    ph1.linked_id,
                    ph1.is_active,
                    ph1.created_at,
                    ph1.created_by,
                    ph1.description
                FROM community.penalty_hist ph1
                WHERE ph1.type = 'post'
                AND ph1.linked_id = rh.linked_id
                ORDER BY ph1.created_at DESC
                LIMIT 1
            ) ph ON true
            INNER JOIN community.posts p ON p.id = rh.linked_id
            INNER JOIN public.users u ON u.id = p.user_id
            INNER JOIN community.channels c on c.id = p.channel_id
            WHERE rn = 1
            """)
    Optional<Map<String, Object>> findLatestPostReport(@Param( "postId" ) Long postId);


    int countByTypeAndIsActiveTrueAndLinkedId( @Param("type") String type,
                                               @Param("linkedId") Long linkedId );
}
