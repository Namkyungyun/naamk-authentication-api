package kr.co.naamk.naamkauthenticationapi.web.repository;

import kr.co.naamk.naamkauthenticationapi.domain.community.TbPenaltyHist;
import kr.co.naamk.naamkauthenticationapi.web.dto.PenaltyHistDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenaltyHistRepository extends JpaRepository< TbPenaltyHist, Long > {

    @Query(nativeQuery = true, value = """
            SELECT
            	(COUNT(*) OVER()) - ROW_NUMBER() OVER (ORDER BY ph.created_at DESC) + 1 AS rowNum,
                ph.id,
                ph.type,
                ph.linked_id AS linkedId,
                ph.is_active AS penalty,
                ph.description,
                ph.created_by AS createdBy,
                CAST(ph.created_at AS timestamp) as createdAt,
                CASE
            	    WHEN CAST(rh.is_active AS boolean) IS NULL THEN false   -- 접수된 신고 없음 -> false
            	    WHEN CAST(ph.is_active AS boolean) IS true THEN false   -- 제재='정상' -> false
            	    ELSE true                                               -- 제재='차단' -> true
            	END AS isExistReport
            FROM community.penalty_hist AS ph
            LEFT JOIN LATERAL (
            		SELECT *
            		FROM community.reports_hist rh1
            		WHERE rh1.type = :type
            		AND rh1.updated_at >= ph.created_at
            		AND rh1.created_at <= ph.created_at
            		ORDER BY rh1.updated_at DESC
            		LIMIT 1
            	) rh ON true
            WHERE ph.linked_id = :linkedId
            AND ph.type = :type
            ORDER BY ph.created_at DESC
            """)
    List< PenaltyHistDto > findPenaltyHistsByLinkedIdAndType( @Param( "linkedId" ) Long linkedId,
                                                     @Param( "type" ) String type );
}
