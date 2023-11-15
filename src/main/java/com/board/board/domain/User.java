package com.board.board.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user")
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Time {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank @Length(min = 2, max = 10)
	@Pattern(regexp = "^([a-zA-Z0-9ㄱ-ㅎ|ㅏ-ㅣ|가-힣]).{1,10}$") //한글, 영문, 숫자만 가능하며 2-10자리 가능
	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private String picture;

	@Column(nullable = false)
	private boolean namecheck = false;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Builder
	public User(String name, String email, String picture, boolean namecheck,
		Role role) {
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.namecheck = namecheck;
		this.role = role;
	}

	/* Oauth 로그인 갱신 날짜 갱신 */
	public void updateProfile(String pictureURL) {
		this.picture = pictureURL;
	}

	/* 첫 소셜 로그인시 별명 중복검사 시키기 */
	public User updateName(String name, String picture) {
		this.name = name;
		this.picture = picture;
		return this;
	}

	/* 설정 에서 별명 바꾸기 */
	public void updateNameInSetting(String name) {
		this.name = name;
	}

	/* 권한 타입 가져오기 */
	public String getRoleKey() {
		return this.role.getKey();
	}

}
