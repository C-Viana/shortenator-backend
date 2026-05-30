package com.cviana.app.url.metrics;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cviana.app.url.Url;

public interface AccessLogRepository extends JpaRepository<UrlAccessLog, Long> {

	List<UrlAccessLog> findByUrl(Url url);

}
