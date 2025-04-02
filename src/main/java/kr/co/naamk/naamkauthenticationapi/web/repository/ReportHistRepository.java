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
    List<TbReportsHist> findByIsActiveTrueAndCreatedAtBefore( Timestamp createdAt );

    @Query(nativeQuery = true, value = """
              SELECT
                rh.id,
                rh.created_at AS latestCreatedAt,
                rh.linked_id AS reportedUserId,
                u.name AS reportedUserName,
                (SELECT COUNT(*) from community.reports_hist where linked_id = rh.linked_id and type ='user') AS reportCount,
                CAST(rh.is_active AS boolean) AS report,
                -- penalty 처리 여부 value
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
                SELECT DISTINCT ON (linked_id)
                       *
                FROM community.reports_hist
                WHERE type = 'user'
                ORDER BY linked_id, created_at DESC
            ) rh
            LEFT JOIN LATERAL (
                SELECT ph1.linked_id, ph1.is_active, ph1.created_at, ph1.created_by
                FROM community.penalty_hist ph1
                WHERE ph1.type = 'user'
                AND ph1.linked_id = rh.linked_id
                ORDER BY ph1.created_at DESC
                LIMIT 1
            ) ph ON true
            INNER JOIN public.users u ON u.id = rh.linked_id
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
                  SELECT DISTINCT ON (linked_id)
                         *
                  FROM community.reports_hist
                  WHERE type = 'user'
                  ORDER BY linked_id, created_at DESC
              ) rh
              LEFT JOIN LATERAL (
                  SELECT ph1.linked_id, ph1.is_active, ph1.created_at, ph1.created_by
                  FROM community.penalty_hist ph1
                  WHERE ph1.type = 'user'
                    AND ph1.linked_id = rh.linked_id
                  ORDER BY ph1.created_at DESC
                  LIMIT 1
              ) ph ON true
              INNER JOIN public.users u ON u.id = rh.linked_id
              WHERE
                (:reportStatus IS NULL OR rh.is_active = CAST(:reportStatus AS BOOLEAN))
                 AND (:penaltyStatus IS NULL OR (rh.is_active =false AND ph.is_active = CAST(:penaltyStatus AS boolean)))
                AND (:reportedName IS NULL OR u.name LIKE CONCAT(:reportedName, '%') )
                AND (:penaltyCreatedBy IS NULL OR (rh.is_active = false AND  ph.created_by LIKE CONCAT(:penaltyCreatedBy, '%')) )
                AND (CAST(:startDate AS timestamp) IS NULL OR rh.created_at >= CAST(:startDate AS TIMESTAMP))
                AND (CAST(:endDate AS timestamp) IS NULL OR rh.created_at < CAST(:endDate AS TIMESTAMP))
              ORDER BY rh.created_at DESC, rh.id DESC
           """
    )
    Page< Map<String, Object> > findAllReportsWithUserAndPenalty(
            @Param( "reportStatus" ) Boolean reportStatus,
            @Param( "penaltyStatus" ) Boolean penaltyStatus,
            @Param( "reportedName" ) String reportedName,
            @Param( "penaltyCreatedBy" ) String penaltyCreatedBy,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable);


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
                WHERE type = :type
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
                WHERE ph1.type = :type
                AND ph1.linked_id = rh.linked_id
                ORDER BY ph1.created_at DESC
                LIMIT 1
            ) ph ON true
            INNER JOIN public.users u ON u.id = rh.linked_id
            WHERE rh.linked_id = :userId
            """)
    Optional<Map<String, Object>> findReportWithPenaltyAndUser( @Param( "userId" ) Long userId,
                                                                @Param( "type" ) String type);


    @Query(nativeQuery = true, value = """
            SELECT
                rh.id,
                rh.user_id AS reportUserId,
                rh.linked_id AS reportedLinkId,
                rh.is_active AS report,
                CASE
                    WHEN (rh.updated_at >= ph.created_at  AND rh.is_active = false)
                    THEN CAST(ph.is_active AS boolean)
                    ELSE null
                END AS penalty,
                u.name AS reportCreatedBy,
                rh.created_at AS reportCreatedAt
            FROM community.reports_hist rh
            LEFT JOIN LATERAL (
                SELECT *
                FROM community.penalty_hist ph1
                WHERE ph1.linked_id = rh.linked_id
                  AND ph1.type = :type
                  AND  rh.updated_at >= ph1.created_at
                ORDER BY ph1.created_at DESC
                LIMIT 1
            ) ph ON true
            LEFT JOIN public.users u ON rh.user_id = u.id
            WHERE ph.type = :type
            AND rh.type = :type
            AND rh.linked_id = :userId
            ORDER BY rh.created_at DESC
            """,
            countQuery = """
            --신규 접수 처리 개수
            SELECT COUNT(*)
            FROM community.reports_hist rh
            LEFT JOIN community.penalty_hist AS ph ON ph.linked_id = rh.linked_id
            INNER JOIN public.users u ON rh.user_id = u.id
            WHERE ph.type = :type
            AND rh.type = :type
            AND rh.linked_id = :userId
            AND rh.is_active = true
           """)
    Page< Map<String, Object> > findReportHists( @Param( "userId") Long userId,
                                                 @Param( "type" ) String type,
                                                 Pageable pageable);
}
