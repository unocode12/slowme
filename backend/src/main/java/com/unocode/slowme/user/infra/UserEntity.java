package com.unocode.slowme.user.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String nickname;

	protected UserEntity() {
	}

	public UserEntity(String nickname) {
		this.nickname = nickname;
	}

	public Long getId() {
		return id;
	}

	public String getNickname() {
		return nickname;
	}
}


