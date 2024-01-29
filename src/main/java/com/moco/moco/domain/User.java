package com.moco.moco.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Length;

import com.moco.moco.dto.UserDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends Time {

	@Id
	private String id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@NotBlank
	@Length(min = 2, max = 10)
	@Pattern(regexp = "^([a-zA-Z0-9ㄱ-ㅎ|ㅏ-ㅣ|가-힣]).{1,10}$") //한글, 영문, 숫자만 가능하며 2-10자리 가능
	@Column(nullable = false)
	private String name;

	@Column
	private String intro;

	@Column
	private String position;

	@Column
	private String stack;

	@Column
	private String career;

	@Column
	private String picture;

	@Column
	private boolean isDeleted = false;

	public User update(UserDto.Request userDto) {
		this.name = userDto.getName();
		this.intro = userDto.getIntro();
		this.position = userDto.getPosition();
		this.stack = userDto.getStack();
		this.career = userDto.getCareer();
		this.picture = userDto.getPicture();

		return this;
	}

	public String getRoleKey() {
		return this.role.getKey();
	}

	public void delete() {
		this.isDeleted = true;
	}

	public void join() {
		this.isDeleted = false;
	}

	@Builder
	public User(String id, String name, String intro, String position, String stack, String career, String picture,
		Role role, boolean isDeleted) {
		this.id = id;
		this.name = name;
		this.intro = intro;
		this.position = position;
		this.stack = stack;
		this.career = career;
		this.picture = picture;
		this.role = role;
		this.isDeleted = isDeleted;
	}
}
