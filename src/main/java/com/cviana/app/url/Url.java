package com.cviana.app.url;

import java.time.Instant;
import java.util.Objects;

import com.cviana.app.user.User;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Table(name = "urls")
@Entity
public class Url {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nameUrl;
	private String sourceDomain;
	private String sourceUrl;
	private String shortenedUrlCode;
	
	@Column(updatable = false)
	private Instant createdAt;
	
	@Nullable
	private Instant expiresAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	public Url() {}
	
	public Url(String nameUrl, String sourceDomain, String sourceUrl, String shortenedUrlCode, Instant createdAt, Instant expiresAt, User user) {
		super();
		this.nameUrl = nameUrl;
		this.sourceDomain = sourceDomain;
		this.sourceUrl = sourceUrl;
		this.shortenedUrlCode = shortenedUrlCode;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
		this.user = user;
	}



	@PrePersist
	protected void onCreate() {
	    this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return nameUrl;
	}

	public void setName(String name) {
		this.nameUrl = name;
	}

	public String getSourceDomain() {
		return sourceDomain;
	}

	public void setSourceDomain(String sourceDomain) {
		this.sourceDomain = sourceDomain;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getShortenedUrlCode() {
		return shortenedUrlCode;
	}

	public void setShortenedUrlCode(String shortenedUrlEndpoint) {
		this.shortenedUrlCode = shortenedUrlEndpoint;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdAt, expiresAt, id, nameUrl, shortenedUrlCode, sourceDomain, sourceUrl, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Url other = (Url) obj;
		return Objects.equals(createdAt, other.createdAt) && Objects.equals(expiresAt, other.expiresAt)
				&& Objects.equals(id, other.id) && Objects.equals(nameUrl, other.nameUrl)
				&& Objects.equals(shortenedUrlCode, other.shortenedUrlCode)
				&& Objects.equals(sourceDomain, other.sourceDomain) && Objects.equals(sourceUrl, other.sourceUrl)
				&& Objects.equals(user, other.user);
	}
	
}
