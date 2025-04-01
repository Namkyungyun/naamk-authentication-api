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
                ph.id,
                ph.type,
                ph.linked_id as linkedId,
                ph.is_active as isActive,
                ph.description,
                ph.created_by as createdBy,
                CAST(ph.created_at AS timestamp) as createdAt,
                CASE
                  WHEN rh.created_at IS NULL THEN false
                  WHEN rh.created_at > ph.created_at
                    AND ph.is_active = false THEN true
                  ELSE false
                END AS isExistReport
            FROM community.penalty_hist ph
            LEFT JOIN (SELECT linked_id, created_at
                       FROM community.reports_hist
                       WHERE type = :type -- user 신고 케이스만 확인
                       AND is_active = true -- 신고 접수 상태 (미처리 상태)
                       ) rh ON rh.linked_id = ph.linked_id -- userId 일치하는 경우
            WHERE ph.linked_id = :userId
            AND ph.type = :type
            ORDER BY createdAt DESC
            """)
    List< PenaltyHistDto > findPenaltyHistsByUserIdAndType( @Param( "userId" ) Long userId,
                                                     @Param( "type" ) String type );
}
