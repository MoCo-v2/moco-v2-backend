package com.board.board.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass   //클래스가 만들어지지 않는 기초 클래스라는 어노테이션
@EntityListeners(value = {AuditingEntityListener.class})    //Entity의 변화를 감지하는 리스너
public abstract class Time {    //상속

	@CreatedDate
	@Column(updatable = false, name = "created_date")
	private LocalDateTime createdDate;

	@LastModifiedDate
	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;
}
