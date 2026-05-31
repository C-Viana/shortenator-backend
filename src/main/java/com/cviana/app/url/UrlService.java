package com.cviana.app.url;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cviana.app.shared.exception.errors.BadRequestException;
import com.cviana.app.shared.exception.errors.InvalidAccessException;
import com.cviana.app.shared.exception.errors.NotFoundException;
import com.cviana.app.shared.exception.errors.ServerErrorException;
import com.cviana.app.shared.exception.errors.UrlExpiredException;
import com.cviana.app.shared.exception.messages.SystemErrorMessages;
import com.cviana.app.shared.files.FileCreator;
import com.cviana.app.shared.util.Base62Encoder;
import com.cviana.app.shared.util.DeviceTypeResolver;
import com.cviana.app.url.dto.UrlRequestDto;
import com.cviana.app.url.dto.UrlResponseDto;
import com.cviana.app.url.mail.GmailSender;
import com.cviana.app.url.metrics.AccessLogRepository;
import com.cviana.app.url.metrics.UrlAccessLog;
import com.cviana.app.url.metrics.dto.MetricsResponseDto;
import com.cviana.app.user.User;

import jakarta.mail.MessagingException;

@Service
@Transactional
public class UrlService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private UrlRepository repository;
	private AccessLogRepository accessLogRepository;
	private GmailSender email;
	
	private final RedisTemplate<String, String> redisTemplate;
	private static final String CACHE_PREFIX = "url:";
    private static final long CACHE_TTL_HOURS = 24;
	
	public UrlService(UrlRepository repository, AccessLogRepository accessLogRepository, RedisTemplate<String, String> redisTemplate, GmailSender email) {
		super();
		this.repository = repository;
		this.accessLogRepository = accessLogRepository;
		this.redisTemplate = redisTemplate;
		this.email = email;
	}
	
	//TODO: cache desativado devido limitação do serviço de hospedagem
	// private void cacheUrl(String code, String targetUrl, Instant expiresAt) {
    //     String key = CACHE_PREFIX + code;
    //     if (expiresAt != null) {
    //         long secondsUntilExpiry = Instant.now().until(expiresAt, ChronoUnit.SECONDS);
    //         if (secondsUntilExpiry > 0) {
    //             redisTemplate.opsForValue().set(key, targetUrl, secondsUntilExpiry, TimeUnit.SECONDS);
    //         }
    //     } else {
    //         redisTemplate.opsForValue().set(key, targetUrl, CACHE_TTL_HOURS, TimeUnit.HOURS);
    //     }
    // }

	public Url shortenUrl(UrlRequestDto sourceData, User user) {
		Instant expiration = null;
		if(sourceData.expiresAt() != null) {
			if(sourceData.expiresAt().isBefore(LocalDateTime.now(ZoneId.of("GMT-3"))))
				throw new BadRequestException(SystemErrorMessages.INVALID_PAST_EXPIRATION_DATE);
			expiration = Instant.from(sourceData.expiresAt().toInstant(ZoneOffset.UTC));
		}
		
		String sourceDomain = URI.create(sourceData.sourceUrl()).getHost();
		
		Url savedUrl = repository.save(
				new Url(
						sourceData.nameUrl(),
						sourceDomain,
						sourceData.sourceUrl(),
						"temp",
						Instant.now(Clock.systemDefaultZone()),
						expiration,
						user
				)
		);
		
		String urlCode = Base62Encoder.encode(savedUrl.getId());
		savedUrl.setShortenedUrlCode(urlCode);
		//TODO: cache desativado devido limitação do serviço de hospedagem
		// cacheUrl(savedUrl.getShortenedUrlCode(), savedUrl.getSourceUrl(), savedUrl.getExpiresAt());
		
		return repository.save(savedUrl);
	}
	
	public Page<UrlResponseDto> fetchAllUrls(User user, Optional<String> domain, Pageable pageable) {
		Page<Url> result = domain.isPresent()
		        ? repository.findByUserAndSourceDomain(user, domain.get(), pageable)
		        : repository.findByUser(user, pageable);
	    if (result.isEmpty()) return Page.empty(pageable);
	    return result.map(UrlResponseDto::toResponse);
	}

	public void deleteAllUrls(User user, Optional<String> domain) {
		if(domain.isPresent()) {
			repository.deleteByUserAndSourceDomain(user, domain.get());
		}
		else
			repository.deleteByUser(user);
	}

	public void deleteUrl(User user, Long id) {
		Url url = repository.findById(id).orElse(null);
		if(url == null || !url.getUser().equals(user)) return;
		
		repository.deleteById(id);
	}
	
	//TODO: cache desativado devido limitação do serviço de hospedagem
	// public String redirectUrl(String shortenatorCode, String userAgent, String referrer) throws Exception {
	// 	String cached = redisTemplate.opsForValue().get(CACHE_PREFIX + shortenatorCode);
	// 	String targetUrl;
		
    //     if (cached != null) {
    //         log.info("Cache hit for code: {}", shortenatorCode);
    //         targetUrl = cached;
            
    //         Url url = repository.findByShortenedUrlCode(shortenatorCode).orElseThrow(() -> new NotFoundException(SystemErrorMessages.URL_CODE_NOT_FOUND));
    //         registerAccessLog(url, userAgent, referrer);
    //     }
    //     else {
    //     	log.info("Cache miss for code: {}", shortenatorCode);
    // 		Url url = repository.findByShortenedUrlCode(shortenatorCode).orElseThrow(() -> new NotFoundException(SystemErrorMessages.URL_CODE_NOT_FOUND));
    		
    // 		if (url.getExpiresAt() != null && Instant.now(Clock.system(ZoneId.of("GMT-3"))).isAfter(url.getExpiresAt())) throw new UrlExpiredException();
    // 		cacheUrl(url.getShortenedUrlCode(), url.getSourceUrl(), url.getExpiresAt());
    // 		registerAccessLog(url, userAgent, referrer);
    //         targetUrl = url.getSourceUrl();
    //     }
        
	// 	return targetUrl;
	// }
	
	public String redirectUrl(String shortenatorCode, String userAgent, String referrer) throws Exception {
		String targetUrl;
		
        Url url = repository.findByShortenedUrlCode(shortenatorCode).orElseThrow(() -> new NotFoundException(SystemErrorMessages.URL_CODE_NOT_FOUND));
		if (url.getExpiresAt() != null && Instant.now(Clock.system(ZoneId.of("GMT-3"))).isAfter(url.getExpiresAt())) throw new UrlExpiredException();
		registerAccessLog(url, userAgent, referrer);
		targetUrl = url.getSourceUrl();
        
		return targetUrl;
	}
	
	private void registerAccessLog(Url url, String userAgent, String referrer) {
		UrlAccessLog accessLog = new UrlAccessLog();
	    accessLog.setUrl(url);
	    accessLog.setDeviceType(DeviceTypeResolver.resolve(userAgent));
	    accessLog.setReferrer(referrer);
	    accessLogRepository.save(accessLog);
	}

	public Resource exportToFile(FileCreator fileRequest, User user, Optional<String> domain) {
		List<Url> content = domain.isPresent()
		        ? repository.findByUserAndSourceDomain(user, domain.get())
		        : repository.findByUser(user);
		
		if (content.isEmpty())
	        throw new NotFoundException(SystemErrorMessages.NO_URL_FOUND);
		
		String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
		
		return fileRequest.create(content, baseUrl);
	}
	
	public void shareUrls(String sender, String[] to, String title, String content, String filePath) {
		try {
			email.sendMessageWithAttachment(sender, to, title, content, filePath);
		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new ServerErrorException(e.getMessage());
		}
	}
	
	public MetricsResponseDto getMetrics(Long urlId, User user) {
		Url url = repository.findById(urlId).orElseThrow(() -> new NotFoundException(SystemErrorMessages.URL_CODE_NOT_FOUND));
		if (!url.getUser().equals(user)) throw new InvalidAccessException(SystemErrorMessages.FORBIDDEN_ACCESS);
		List<UrlAccessLog> logs = accessLogRepository.findByUrl(url);
		
		if (logs.isEmpty()) return new MetricsResponseDto(0L, Map.of(), Map.of(), Map.of(), null);
		
		long total = logs.size();
		Map<LocalDate, Long> perDay = logs.stream().collect(
	        Collectors.groupingBy(
	            l -> l.getAccessedAt().atZone(ZoneId.of("GMT-3")).toLocalDate(),
	            Collectors.counting()
	        )
	    );
		
		Map<String, Long> perDevice = logs.stream().collect(
	        Collectors.groupingBy(UrlAccessLog::getDeviceType, Collectors.counting())
	    );

	    Map<String, Long> perReferrer = logs.stream()
	        .filter(l -> l.getReferrer() != null)
	        .collect(Collectors.groupingBy(
	            l -> {
	                try { return new URI(l.getReferrer()).getHost(); }
	                catch (Exception e) { return "unknown"; }
	            },
	            Collectors.counting()
	        ));

	    Instant lastAccess = logs.stream()
	        .map(UrlAccessLog::getAccessedAt)
	        .max(Comparator.naturalOrder())
	        .orElse(null);

	    return new MetricsResponseDto(total, perDay, perDevice, perReferrer, lastAccess);
	}
	
}
