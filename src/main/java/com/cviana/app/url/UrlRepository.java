package com.cviana.app.url;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cviana.app.user.User;

public interface UrlRepository extends JpaRepository<Url, Long> {
	Optional<Url> findByShortenedUrlCode(String code);

	List<Url> findByUser(User user);
	
	Page<Url> findByUser(User user, Pageable pageable);

	List<Url> findByUserAndSourceDomain(User user, String sourceDomain);

    Page<Url> findByUserAndSourceDomain(User user, String sourceDomain, Pageable pageable);

    void deleteByUser(User user);

    void deleteByUserAndSourceDomain(User user, String sourceDomain);
    
}
