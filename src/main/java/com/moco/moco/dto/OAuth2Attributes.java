package com.moco.moco.dto;

import java.util.HashMap;
import java.util.Map;

import com.moco.moco.domain.Role;
import com.moco.moco.domain.User;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OAuth2Attributes {
	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String name;
	private String email;
	private String picture;

	@Builder
	public OAuth2Attributes(Map<String, Object> attributes,
		String nameAttributeKey,
		String name,
		String email,
		String picture) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.name = name;
		this.email = email;
		this.picture = picture;
	}

	public static OAuth2Attributes of(String registrationId, String userNameAttributeName,
		Map<String, Object> attributes) {

		switch (registrationId) {
			case "kakao":
				return ofKakao("id", attributes);
			case "github":
				return ofGithub("id", attributes);
		}
		return ofGoogle(userNameAttributeName, attributes);
	}

	public static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuth2Attributes.builder()
			.name((String)attributes.get("name"))
			.email((String)attributes.get("email"))
			.picture("/img/userIcon.png")
			.attributes(attributes)
			.nameAttributeKey(userNameAttributeName)
			.build();
	}

	public static OAuth2Attributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuth2Attributes.builder()
			.name((String)attributes.get("name"))
			.email((String)attributes.get("name") + "@github.com")
			.picture("/img/userIcon.png")
			.attributes(attributes)
			.nameAttributeKey(userNameAttributeName)
			.build();
	}

	public static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
		//kakao는 kakao_account에 유저정보가 있다(email)
		Map<String, Object> kakaoAcount = (Map<String, Object>)attributes.get("kakao_account");
		//kakao_account안에 또 profile이라는 JSON객체가 있다. (nickname, profile_image,)
		Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAcount.get("profile");
		return OAuth2Attributes.builder()
			.name((String)kakaoProfile.get("nickname"))
			.email((String)kakaoAcount.get("email"))
			.picture("/img/userIcon.png")
			.attributes(attributes)
			.nameAttributeKey(userNameAttributeName)
			.build();
	}

	/* 최초가입시 */
	public User toEntity() {
		return User.builder()
			.name(name)
			.email(email)
			.picture(picture)
			.role(Role.USER)
			.build();
	}

	public Map<String, Object> convertToMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", nameAttributeKey);
		map.put("key", nameAttributeKey);
		map.put("email", email);
		map.put("name", name);
		map.put("picture", picture);
		return map;
	}
}