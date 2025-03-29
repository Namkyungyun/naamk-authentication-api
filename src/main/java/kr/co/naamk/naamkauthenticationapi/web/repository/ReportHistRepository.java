package kr.co.naamk.naamkauthenticationapi.web.repository;

import kr.co.naamk.naamkauthenticationapi.domain.community.TbReportsHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportHistRepository extends JpaRepository< TbReportsHist, Long > {
    boolean existsByLinkedIdAndType(Long linkedId, String type);
}
