import { fetchAnalysis, fetchAnalyzes } from '../api/analysis';
import { useQuery } from "@tanstack/react-query";

/**
 * key 폴더링 방식
 * key를 디렉터리 나누듯이 구조화 하는 것이 유연하게 캐시를 무효화할 수 있다.
 */
export const ANALYSIS_QUERY_KEYS = {
    all: ['analysis'] as const, // analysis 도메인 => ['analysis']
    lists: () => [...ANALYSIS_QUERY_KEYS.all, 'lists'] as const, // analysis의 목록 => ['analysis', 'lists']
    list: (ids: Array<number>) => [...ANALYSIS_QUERY_KEYS.lists(), ids] as const, // 특정 목록 => ['analysis', 'lists', [1, 2, 3]]
    details: () => [...ANALYSIS_QUERY_KEYS.all, 'details'] as const, // analysis의 상세 => ['analysis', 'details']
    detail: (id: number) => [...ANALYSIS_QUERY_KEYS.details(), id] as const, // 특정 상세 => ['analysis', 'lists', 1]
};

export const useAnalysisQuery = (id: number) => {
    return useQuery({
        queryKey: ANALYSIS_QUERY_KEYS.detail(id),
        queryFn: () => fetchAnalysis(id),
        staleTime: 1000 * 60 * 5,
        enabled: !!id,
    });
};

export const useAnalyzesQuery = (ids: Array<number>) => {
    return useQuery({
        queryKey: ANALYSIS_QUERY_KEYS.list(ids),
        queryFn: () => fetchAnalyzes(ids),
        staleTime: 1000 * 60 * 5,
        enabled: !!ids && ids.length > 0,
    });
};
