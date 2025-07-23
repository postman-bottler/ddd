package online.bottler.recommendation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
abstract class AbstractAuditing {

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    protected Long createdBy;

    @Getter
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "modified_by")
    protected Long modifiedBy;

    @LastModifiedDate
    @Column(name = "modifiedAt")
    protected LocalDateTime modifiedAt;
}
