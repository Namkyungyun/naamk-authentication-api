package kr.co.naamk.naamkauthenticationapi.domain.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@Setter
@Getter
@EntityListeners( value = { AuditingEntityListener.class} )
@MappedSuperclass
public abstract class AuditEntity {

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy = "system";


    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_at", updatable = false)
    @Comment("생성일자")
    private Timestamp createdAt;


    @LastModifiedBy
    @Column(name = "updated_by", nullable = false, length = 50)
    @Comment("수정자")
    private String updatedBy = "system";


    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_at", nullable = false)
    @Comment("수정일자")
    private Timestamp updatedAt;

}
