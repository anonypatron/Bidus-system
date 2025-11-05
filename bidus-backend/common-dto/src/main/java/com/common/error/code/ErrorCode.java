package com.common.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C-001", "입력 값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C-002", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-003", "서버 내부 에러가 발생했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-001", "해당 사용자를 찾을 수 없습니다."),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "U-002", "해당 작업을 수행할 권한이 없습니다."),

    // Auction
    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, "A-001", "해당 경매를 찾을 수 없습니다."),
    AUCTION_FINISHED(HttpStatus.BAD_REQUEST, "A-002", "경매가 종료되었습니다."),
    INVALID_AUCTION_STATUS(HttpStatus.BAD_REQUEST, "A-003", "경매 상태가 올바르지 않아 작업을 수행할 수 없습니다."),

    // Bookmark
    BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "B-001", "이미 북마크가 되어있습니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "B-002", "이미 북마크가 삭제되었습니다."),

    // Request
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C-001", "잘못된 요청입니다."),

    // Bidding
    INVALID_INPUT_PRICE(HttpStatus.BAD_REQUEST, "D-001", "현재가보다 더 높은 금액을 제시해야 합니다."),

    // Graph
    Graph_NOT_FOUND(HttpStatus.NOT_FOUND, "E-001", "아직 그래프가 생성되지 않았습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
