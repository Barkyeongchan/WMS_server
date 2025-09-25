// 생성/수정 일시를 자동으로 기록
package com.wasd.smartWMS.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @CreatedDate    // 엔티티가 DB에 저장될 때 자동으로 일시 기록
    private LocalDateTime

    @LastModifiedDate    // 엔티티가 수정될 때 자동으로 일시 시록
    private LocalDateTime
}
