package com.web.bff.service;

import com.common.AuctionStatus;
import com.web.bff.dto.auction.AuctionCreateRequestDto;
import com.web.bff.dto.auction.AuctionPriceDto;
import com.web.bff.dto.auction.AuctionResponseDto;
import com.web.bff.dto.auction.AuctionUpdateRequestDto;
import com.web.bff.helper.RestPageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class AuctionBffService {

    private final WebClient webClient;
    private final HttpHeaders jsonHeaders;

    public AuctionBffService(WebClient webClient) {
        this.webClient = webClient;
        this.jsonHeaders = new HttpHeaders();
        this.jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public Mono<AuctionResponseDto> save(Long userId, AuctionCreateRequestDto dto, Mono<FilePart> imagePartMono) {
        // [핵심] Mono<FilePart>를 WebClient의 body로 전달합니다.
        // BodyInserters.fromMultipartData를 사용하여 multipart 요청을 만듭니다.
        return imagePartMono.flatMap(imagePart -> {

            // MultipartBodyBuilder를 동기적으로 생성합니다.
            MultipartBodyBuilder builder = new MultipartBodyBuilder();

            // [핵심 수정 2] DTO를 HttpEntity로 감싸서 명시적으로 JSON임을 알립니다.
            builder.part("auctionData", new HttpEntity<>(dto, jsonHeaders));

            // FilePart를 추가합니다.
            builder.part("image", imagePart);

            // WebClient 호출을 flatMap 내부에서 실행합니다.
            return webClient
                    .post()
                    .uri("http://auction-service/api/auctions")
                    .header("X-User-ID", String.valueOf(userId))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    // builder.build()로 생성된 MultiValueMap을 body에 넣습니다.
                    .body(BodyInserters.fromValue(builder.build()))
                    .retrieve()
                    .bodyToMono(AuctionResponseDto.class);
        });
    }

    // 2. 특정 경매 검색 (GET)
    public Mono<AuctionResponseDto> getAuction(Long auctionId) {
        return webClient
                .get()
                .uri("http://auction-service/api/auctions/{auctionId}", auctionId)
                .retrieve()
                .bodyToMono(AuctionResponseDto.class);
    }

    // 데이터를 조합하여 로그인에 따라 북마크를 한 경매를 리턴
    public Mono<Page<AuctionResponseDto>> getAuctions(Long userId, AuctionStatus status, Pageable pageable) {
        Mono<Set<Long>> bookmarkIdsMono;
        // 사용자가 로그인을 한 상태면 즐겨찾기한 목록 가져오고 아니면 빈 set<T>
        if (userId == null) {
            bookmarkIdsMono = Mono.just(Collections.emptySet());
        } else {
            bookmarkIdsMono = webClient.get()
                    .uri("http://bookmark-service/api/bookmarks")
                    .header("X-User-ID", String.valueOf(userId))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Set<Long>>() {})
                    .onErrorReturn(Collections.emptySet());
        }

        // 일반 경매 목록 가져오기
        Mono<RestPageImpl<AuctionResponseDto>> auctionPageMono = webClient.get()
                .uri("http://auction-service/api/auctions", uriBuilder -> uriBuilder
                        .queryParam("status", status.name())
                        .queryParam("page", pageable.getPageNumber())
                        .queryParam("size", pageable.getPageSize())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });

        // 정보 조합하기
        return Mono.zip(bookmarkIdsMono, auctionPageMono)
                .map(tuple -> {
                    Set<Long> bookmarkIds = tuple.getT1();

                    Page<AuctionResponseDto> auctionPage = tuple.getT2();

                    auctionPage.getContent().forEach(auction -> {
                        if (bookmarkIds.contains(auction.getId())) {
                            auction.setBookmarked(true);
                        }
                    });

                    return auctionPage;
                });
    }

    // 북마크한 경매만 보여주기 (로그인 한 상태임)
    public Mono<Page<AuctionResponseDto>> getBookmarkedAuctions(Long userId, AuctionStatus status, Pageable pageable) {
        Mono<Set<Long>> bookmarkIdsMono = webClient.get()
                .uri("http://bookmark-service/api/bookmarks")
                .header("X-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<Long>>() {})
                .onErrorReturn(Collections.emptySet());

        return bookmarkIdsMono.flatMap(ids -> {
           if (ids == null || ids.isEmpty()) {
               return Mono.just(new PageImpl<>(Collections.emptyList(), pageable, 0));
           }

           return webClient.get()
                   .uri("http://auction-service/api/auctions/list", uriBuilder ->
                           uriBuilder.queryParam("ids", ids)
                                   .queryParam("status", status.name())
                                   .queryParam("page", pageable.getPageNumber())
                                   .queryParam("size", pageable.getPageSize())
                                   .build()
                   )
                   .retrieve()
                   .bodyToMono(new ParameterizedTypeReference<RestPageImpl<AuctionResponseDto>>() {})
                   .map(page -> {
                       page.getContent().forEach(auction -> auction.setBookmarked(true));
                       return (Page<AuctionResponseDto>) page;
                   });
        });
    }

    // 4. 사용자 경매 기록 (GET)
    public Mono<List<AuctionResponseDto>> getAuctionHistory(Long userId, String role, AuctionStatus status) {
        return webClient
                .get()
                .uri("http://auction-service/api/auctions/history", uriBuilder -> uriBuilder
                        .queryParam("role", role)
                        .queryParam("status", status.name())
                        .build())
                .header("X-User-ID", String.valueOf(userId)) // 인증 컨텍스트 전파
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    // 5. 경매 수정 (PATCH - Multipart)
    public Mono<Void> update(Long userId, Long auctionId, AuctionUpdateRequestDto dto, Mono<FilePart> imagePartMono) {
        Mono<Optional<FilePart>> imagePartOptionalMono = imagePartMono
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty());

        return imagePartOptionalMono.flatMap(imagePartOpt -> {

            MultipartBodyBuilder builder = new MultipartBodyBuilder();

            builder.part("auctionData", new HttpEntity<>(dto, jsonHeaders));

            imagePartOpt.ifPresent(imagePart ->
                    builder.part("image", imagePart)
            );

            return webClient
                    .patch()
                    .uri("http://auction-service/api/auctions/{auctionId}", auctionId)
                    .header("X-User-ID", String.valueOf(userId))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromValue(builder.build()))
                    .retrieve()
                    .bodyToMono(Void.class);
        });
    }

    // 6. 경매 삭제 (DELETE)
    public Mono<Void> delete(Long userId, Long auctionId) {
        return webClient
                .delete()
                .uri("http://auction-service/api/auctions/{auctionId}", auctionId)
                .header("X-User-ID", String.valueOf(userId))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<AuctionPriceDto> getAuctionPrice(Long id) {
        return webClient.get()
                .uri("http://auction-service/api/auctions/{id}/current-price", id)
                .retrieve()
                .bodyToMono(AuctionPriceDto.class);
    }

    public Mono<List<AuctionResponseDto>> getAuctionCurrentBidding(Long id) {
        return webClient.get()
                .uri("http://auction-service/api/auctions/current-bidding")
                .header("X-User-ID", String.valueOf(id))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

}
