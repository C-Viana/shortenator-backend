package com.cviana.app.url.metrics;

import java.time.Instant;
import java.util.Objects;

import com.cviana.app.url.Url;

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

@Entity
@Table(name = "url_access_logs")
public class UrlAccessLog {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private Url url;

    @Column(updatable = false)
    private Instant accessedAt;

    private String deviceType;
    private String referrer;
    
    public UrlAccessLog() {}

    @PrePersist
    protected void onCreate() {
        this.accessedAt = Instant.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Url getUrl() {
		return url;
	}

	public void setUrl(Url url) {
		this.url = url;
	}

	public Instant getAccessedAt() {
		return accessedAt;
	}

	public void setAccessedAt(Instant accessedAt) {
		this.accessedAt = accessedAt;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accessedAt, deviceType, id, referrer, url);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UrlAccessLog other = (UrlAccessLog) obj;
		return Objects.equals(accessedAt, other.accessedAt) && Objects.equals(deviceType, other.deviceType)
				&& Objects.equals(id, other.id) && Objects.equals(referrer, other.referrer)
				&& Objects.equals(url, other.url);
	}
    
}
