const padZero = (num: number): string => String(num).padStart(2, '0');

/**
 * ISO 8601 형식의 날짜 문자열 (예: "2025-10-04T18:00:00")을
 * "YYYY년 MM월 DD일 HH:mm" 형식으로 변환합니다.
 * @param dateString 변환할 날짜 문자열
 * @returns 변환된 날짜 문자열
 */
export function formatDateTime(dateString: string): string {
    const date = new Date(dateString);

    // 날짜가 유효하지 않은 경우 처리
    if (isNaN(date.getTime())) {
      console.error("Invalid date format provided:", dateString);
      return "유효하지 않은 날짜";
    }

    const year = date.getFullYear();
    const month = padZero(date.getMonth() + 1); // getMonth()는 0부터 시작하므로 +1
    const day = padZero(date.getDate());
    const hours = padZero(date.getHours());
    const minutes = padZero(date.getMinutes());

    return `${year}년 ${month}월 ${day}일 ${hours}:${minutes}`;
}