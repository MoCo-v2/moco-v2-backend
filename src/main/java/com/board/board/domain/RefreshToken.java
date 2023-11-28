package com.board.board.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "refreshToken")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	private String refreshToken;
	@NotBlank
	private String email;

	public RefreshToken(String token, String email) {
		this.refreshToken = token;
		this.email = email;
	}

	public RefreshToken updateToken(String token) {
		this.refreshToken = token;
		return this;
	}
}