package kr.co.naamk.naamkauthenticationapi.web.repository;

import kr.co.naamk.naamkauthenticationapi.domain.community.TbPenaltyHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenaltyHistRepository extends JpaRepository< TbPenaltyHist, Long > {
    List<TbPenaltyHist> findByLinkedIdAndTypeOrderByCreatedAtDesc( Long linkedId, String type );
}
