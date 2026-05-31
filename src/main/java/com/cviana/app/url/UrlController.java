package com.cviana.app.url;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cviana.app.shared.constants.FileFormat;
import com.cviana.app.shared.exception.ExceptionResponseTemplate;
import com.cviana.app.shared.files.CsvFile;
import com.cviana.app.shared.files.ExcelFile;
import com.cviana.app.shared.files.PdfFile;
import com.cviana.app.shared.files.TextFile;
import com.cviana.app.url.dto.UrlRequestDto;
import com.cviana.app.url.dto.UrlResponseDto;
import com.cviana.app.url.metrics.dto.MetricsResponseDto;
import com.cviana.app.user.User;
import com.cviana.app.user.dto.UserResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@RestController
@Valid
@Tag(name = "URL", description = "CRUD para gerenciamento de URLs encurtadas")
public class UrlController {
	
	private UrlService urlService;
	
	public UrlController(UrlService urlService) {
		super();
		this.urlService = urlService;
	}
	
	@Operation(
        summary = "Encurtar URL",
        description = "Realiza o encurtamento de uma URL e persiste os dados no servidor",
        responses = {
            @ApiResponse(
                description = "Created",
                responseCode = "201",
                content = {
                    @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UrlResponseDto.class)
                    )
                }
            ),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@PostMapping(value = "api/v1/urls/shorten", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UrlResponseDto> shortenAndSaveUrl( @RequestBody UrlRequestDto dto, @AuthenticationPrincipal User currentUser ) {
		Url savedUrl = urlService.shortenUrl(
				dto,
				currentUser
			);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("{id}").buildAndExpand(savedUrl.getId()).toUri();
		return ResponseEntity.created(uri).body(UrlResponseDto.toResponse(savedUrl));
	}
	
	@Operation(
        summary = "Redirecionar URL",
        description = "Realiza o redirecionamento de uma URL encurtada para o endereço original",
        responses = {
            @ApiResponse(
                description = "Found",
                responseCode = "302"
            ),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@GetMapping("r/{urlCode}")
	public ResponseEntity<?> redirectUrl(@PathVariable String urlCode, HttpServletRequest request) throws Exception {
		String targetUrl = urlService.redirectUrl(urlCode, request.getHeader("User-Agent"), request.getHeader("Referrer"));
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(targetUrl)).build();
	}
	
	@Operation(
        summary = "Recupera as URLs do usuário",
        description = "Retorna todas as URLs do usuário de forma paginada. Opcionalmente, essa operação pode filtrar o resultado pelo domínio da URL (ex.: www.google.com)",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UserResponseDto.class)
                    )
                }
            ),
            @ApiResponse(
                description = "No Content", 
                responseCode = "204",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@GetMapping(value = "api/v1/urls", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<UrlResponseDto>> getAllUrls(
			@AuthenticationPrincipal User currentUser, 
			@RequestParam(required = false) String domain, 
			@RequestParam @DefaultValue("0") @Min(0) int page, 
			@RequestParam @DefaultValue("10") @Min(1) int pageSize, 
			@RequestParam @DefaultValue("asc") String order
	) {
		Direction sort = (order.toLowerCase().startsWith("asc")) ? Direction.ASC : Direction.DESC;
		Pageable pageable = PageRequest.of( page, pageSize, sort, "id" );
		Page<UrlResponseDto> result = urlService.fetchAllUrls(currentUser, Optional.ofNullable(domain), pageable);

		if(result.isEmpty()) return ResponseEntity.noContent().build();
		
		return ResponseEntity.ok(result);
	}
	
	@Operation(
        summary = "Remover URL",
        description = "Apaga do banco de dados uma URL especificada pelo seu ID",
        responses = {
            @ApiResponse(
                description = "No Content",
                responseCode = "204"
            ),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@DeleteMapping(value = "api/v1/urls/{id}")
	public ResponseEntity<Void> deleteUrl(@AuthenticationPrincipal User currentUser, @PathVariable long id) {
		urlService.deleteUrl(currentUser, id);
		return ResponseEntity.noContent().build();
	}
	
	@Operation(
        summary = "Remover URLs",
        description = "Apaga todas as URLs de um usuário. Opcionalmente, essa operação pode ser limitada ao filtrar o domínio da URL (ex.: www.google.com)",
        responses = {
            @ApiResponse(
                description = "No Content",
                responseCode = "204"
            ),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@DeleteMapping(value = "api/v1/urls")
	public ResponseEntity<Void> deleteAllUrls(@AuthenticationPrincipal User currentUser, @RequestParam(required = false) Optional<String> domain) {
		urlService.deleteAllUrls(currentUser, domain);
		return ResponseEntity.noContent().build();
	}
	
	@Operation(
        summary = "Exportar para CSV",
        description = "Disponibiliza um arquivo CSV (Valores Separados por Vírgulas) com todas as URLs do usuário",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = FileFormat.CSV
                    ),
                    @Content(
                        mediaType = FileFormat.CSV_UTF8
                    )
                }
            ),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@GetMapping(value = "api/v1/urls/export", produces = {FileFormat.CSV, FileFormat.CSV_UTF8})
	public ResponseEntity<Resource> exportUrlsToCsv(@AuthenticationPrincipal User currentUser, @RequestParam(required = false) String domain) {
		Resource file = urlService.exportToFile(new CsvFile(), currentUser, Optional.ofNullable(domain));
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"url_list.csv\"")
				.contentType(MediaType.parseMediaType(FileFormat.CSV_UTF8))
				.body(file);
	}
	
	@Operation(
        summary = "Exportar para texto",
        description = "Disponibiliza um arquivo TXT com todas as URLs do usuário",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = FileFormat.TXT
                    )
                }
            ),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@GetMapping(value = "api/v1/urls/export", produces = FileFormat.TXT)
	public ResponseEntity<Resource> exportUrlsToTxt(@AuthenticationPrincipal User currentUser, @RequestParam(required = false) String domain) {
		Resource file = urlService.exportToFile(new TextFile(), currentUser, Optional.ofNullable(domain));
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"url_list.txt\"")
				.contentType(MediaType.parseMediaType(FileFormat.TXT))
				.body(file);
	}
	
	@Operation(
        summary = "Exportar para PDF",
        description = "Disponibiliza um arquivo PDF com todas as URLs do usuário",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = FileFormat.PDF
                    )
                }
            ),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@GetMapping(value = "api/v1/urls/export", produces = FileFormat.PDF)
	public ResponseEntity<Resource> exportUrlsToPdf(@AuthenticationPrincipal User currentUser, @RequestParam(required = false) String domain) {
		Resource file = urlService.exportToFile(new PdfFile(), currentUser, Optional.ofNullable(domain));
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"url_list.pdf\"")
				.contentType(MediaType.parseMediaType(FileFormat.PDF))
				.body(file);
	}
	
	@Operation(
        summary = "Exportar para planilha",
        description = "Disponibiliza um arquivo XLS ou XLSX com todas as URLs do usuário",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = FileFormat.XLS
                    ),
                    @Content(
                        mediaType = FileFormat.XLSX
                    )
                }
            ),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@GetMapping(value = "api/v1/urls/export", produces = {FileFormat.XLS, FileFormat.XLSX})
	public ResponseEntity<Resource> exportUrlsToExcel(@AuthenticationPrincipal User currentUser, @RequestParam(required = false) String domain) {
		Resource file = urlService.exportToFile(new ExcelFile(), currentUser, Optional.ofNullable(domain));
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"url_list.xlsx\"")
				.contentType(MediaType.parseMediaType(FileFormat.XLSX))
				.body(file);
	}
	
	@Operation(
        summary = "Métricas de uso da URL",
        description = "Disponibiliza informações de uso de uma URL específica",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = MetricsResponseDto.class)
                    )
                }
            ),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@GetMapping(value = "api/v1/urls/{id}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MetricsResponseDto> getMetrics(
	        @AuthenticationPrincipal User currentUser,
	        @PathVariable Long id) {
	    return ResponseEntity.ok(urlService.getMetrics(id, currentUser));
	}
	
	@Operation(
        summary = "Compartilhar por e-mail",
        description = "Disponibiliza um arquivo de planilha (XLSX) por e-mail para os contatos especificados",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@PostMapping(value = "api/v1/urls/share")
	public ResponseEntity<Void> sendToEmail(
	        @AuthenticationPrincipal User currentUser,
	        @RequestParam @NotEmpty String[] receivers,
	        @RequestParam(name = "body") String emailContent,
	        @RequestParam(required = false) String domain) throws IOException {
		
		Resource file = urlService.exportToFile(new ExcelFile(), currentUser, Optional.ofNullable(domain));
		urlService.shareUrls(currentUser.getEmail(), receivers, "Shortenator URLs List", emailContent, file.getFilePath().toString() );
	    return ResponseEntity.ok().build();
	}
}
