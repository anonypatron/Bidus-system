package com.generator.service;

import com.generator.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class DataSetupService implements CommandLineRunner {

    private final ApplicationContext context;

    private final String BASE_URL = "http://localhost";
    private final Random random = new Random();
    private final List<String> CATEGORIES = List.of(
            "컴퓨터공학", "심리학", "언어학", "물리학", "역사학", "마케팅", "금융", "스타트업", "회계", "인사관리",
            "인공지능", "웹개발", "사이버보안", "데이터사이언스", "클라우드컴퓨팅", "음악", "미술", "사진", "문학",
            "영화제작", "사회학", "정지학", "철학", "언론", "미디어", "문화인류학", "요리", "운동", "피트니스",
            "여행", "패션", "정신건강", "자기계발"
    );

    @Override
    public void run(String... args) {
        createAllData();

        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }

    private void createAllData() {
        System.out.println("====== 1. 테스트 유저 생성 시작 ======");
        createUsers(100); // n명
        System.out.println("====== 1. 테스트 유저 생성 완료 ======");

        System.out.println("====== 2. 경매 물품 1,000개 생성 시작 (user1~10) ======");
        createAuctions(1000, 10); // 10,000개를 100명이 나눠서 등록
        System.out.println("====== 2. 경매 물품 1,0000개 생성 완료 ======");

        System.out.println("====== 3. 입찰 10,000건 생성 시작 (user101~1000) ======");
        placeBids(10000, 11, 100, 1000); // 100,000건을 user101~1000이 1,000개씩 물품에 입찰
        System.out.println("====== 3. 입찰 10,000건 생성 완료 ======");
    }

    private void createUsers(int count) {
        RestTemplate restTemplate = new RestTemplate();
        String signupUrl = BASE_URL + "/api/auth/signup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try (PrintWriter pw = new PrintWriter("jmeter_users.csv")){
            pw.println("email,password");

            for (int i = 1; i <= count; i++) {
                String email = "user" + i + "@example.com";
                String username = "user" + i;
                String password = "1234";

                SignupRequestDto dto = new SignupRequestDto(email, username, password, "USER");

                try {
                    HttpEntity<SignupRequestDto> requestEntity = new HttpEntity<>(dto, headers);
                    restTemplate.exchange(signupUrl, HttpMethod.POST, requestEntity, String.class);
                    // JMeter용 CSV 파일 작성
                    pw.println(email + "," + password);
                } catch (Exception e) {
                    System.err.println("유저 생성 실패 (user" + i + "): " + e.getMessage());
                }

                if (i % 10 == 0) {
                    System.out.println("... " + i + "번째 유저 생성 완료.");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("jmeter_users.csv 파일 생성 실패: " + e.getMessage());
        }
    }

    private void createAuctions(int totalAuctions, int sellerCount) {
        String auctionUrl = BASE_URL + "/api/auctions";
        FileSystemResource dummyImage = new FileSystemResource("dummy.png");
        int auctionsPerSeller = totalAuctions / sellerCount;

        for (int i = 1; i <= sellerCount; i++) {
            String email = "user" + i + "@example.com";
            String password = "1234";

            // 1. 판매자 로그인 (쿠키가 자동 저장되는 RestTemplate 획득)
            RestTemplate authRestTemplate = login(email, password);
            if (authRestTemplate == null) continue; // 로그인 실패

            System.out.println(">>> " + email + " 판매자, 경매 등록 시작 (" + auctionsPerSeller + "건)...");

            for (int j = 1; j <= auctionsPerSeller; j++) {
                // 2. 경매 DTO 생성
                AuctionCreateRequestDto dto = new AuctionCreateRequestDto(
                        "경매 물품 (" + email + "-" + j + ")",
                        "이것은 " + email + "님이 등록한 물품의 상세 설명입니다.",
                        List.of(CATEGORIES.get(random.nextInt(CATEGORIES.size()))), // 카테고리 1개 랜덤 선택
                        10000L + (random.nextInt(100) * 100), // 10,000 ~ 19,900원
                        Instant.now().plus(1, ChronoUnit.MINUTES), // n분 뒤 시작
                        Instant.now().plus(2, ChronoUnit.HOURS)     // n분 뒤 마감
                );

                // 3. Multipart/form-data 요청 구성
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("auctionData", dto);
                body.add("image", dummyImage);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                try {
                    // 4. API 호출
                    authRestTemplate.postForEntity(auctionUrl, requestEntity, String.class);
                } catch (Exception e) {
                    System.err.println("경매 등록 실패 (" + email + "-" + j + "): " + e.getMessage());
                }
            }
        }
    }

    private void placeBids(int totalBids, int bidderStartIdx, int bidderEndIdx, int totalAuctions) {
        String bidUrl = BASE_URL + "/api/bids";
        int bidderCount = (bidderEndIdx - bidderStartIdx) + 1;
        int bidsPerUser = totalBids / bidderCount;

        for (int i = bidderStartIdx; i <= bidderEndIdx; i++) {
            String email = "user" + i + "@example.com";
            String password = "1234";

            // 1. 입찰자 로그인
            RestTemplate authRestTemplate = login(email, password);
            if (authRestTemplate == null) continue;

            System.out.println(">>> " + email + " 입찰자, 입찰 시작 (" + bidsPerUser + "건)...");

            for (int j = 0; j < bidsPerUser; j++) {
                // 2. 입찰 DTO 생성
                long randomAuctionId = random.nextInt(totalAuctions) + 1; // 1 ~ totalAuctions
                long currentPrice = getCurrentHighestPrice(authRestTemplate, randomAuctionId);

                if (currentPrice == -1L) {
                    continue; // 이 입찰은 건너뜀
                }

                long priceIncrement = (random.nextInt(100) + 1) * 100L;
                long newBidPrice = currentPrice + priceIncrement;

                BidRequestDto dto = new BidRequestDto(randomAuctionId, newBidPrice);

                try {
                    authRestTemplate.postForObject(bidUrl, dto, String.class);
                } catch (Exception e) {
                    log.error("입찰 실패 : {}", e.getMessage());
                }
            }
        }
    }

    /**
     * [핵심] 로그인을 수행하고, 쿠키(accessToken)가 자동으로 관리되는
     * 인증된 RestTemplate 인스턴스를 반환합니다.
     */
    private RestTemplate login(String email, String password) {
        try {
            // 1. (v5) 쿠키를 저장할 수 있는 HttpClient 생성
            CookieStore cookieStore = new BasicCookieStore();
            HttpClient httpClient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();

            // 2. (v5) HttpClient를 사용하는 RestTemplate 생성
            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);
            RestTemplate restTemplate = new RestTemplate(requestFactory);

            // 3. 로그인 DTO 생성 및 API 호출
            LoginRequestDto loginDto = new LoginRequestDto(email, password);
            String loginUrl = BASE_URL + "/api/auth/login";

            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, loginDto, String.class);

            // 4. 로그인 성공 시
            if (response.getStatusCode() == HttpStatus.OK) {
                return restTemplate;
            }
        } catch (Exception e) {
            System.err.println("로그인 실패 (" + email + "): " + e.getMessage());
        }
        return null;
    }

    private long getCurrentHighestPrice(RestTemplate authRestTemplate, long auctionId) {
        // (가정) 이 API가 현재 최고가 또는 시작가를 반환합니다.
        String getPriceUrl = BASE_URL + "/api/auctions/" + auctionId + "/current-price";

        try {
            AuctionPriceDto dto = authRestTemplate.getForObject(getPriceUrl, AuctionPriceDto.class);
            if (dto != null && dto.getPrice() != null) {
                return dto.getPrice();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return -1L; // 조회 실패 시 -1 반환
    }

}
