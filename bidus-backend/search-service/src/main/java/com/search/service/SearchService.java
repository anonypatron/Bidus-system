package com.search.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.search.entity.AuctionSearchDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class SearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 형태소 분석기(nori)에 의해 분석되 단어들이 title, desc, categories에
     * 하나라도 포함이 되어있다면 Page형태로 반환
     * 단, categories에는 여러개 중 하나만 포함되어 있어도 반환
     * @param keyword
     * @param status
     * @param categories
     * @param pageable
     * @return AuctionSearchDocument
     */
    public Page<AuctionSearchDocument> search(
            String keyword,
            String status,
            List<String> categories,
            Pageable pageable
    ) {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

        if (StringUtils.hasText(keyword)) {
            boolQueryBuilder.must(QueryBuilders.multiMatch()
                    .query(keyword)
                    .fields("title", "description", "sellerUserName")
                    .build()._toQuery()
            );
        }

        if (StringUtils.hasText(status)) {
            boolQueryBuilder.must(QueryBuilders.term()
                    .field("status")
                    .value(status)
                    .build()._toQuery()
            );
        }

        if (categories != null && !categories.isEmpty()) {
            List<FieldValue> categoryValues = categories.stream()
                    .map(FieldValue::of)
                    .collect(Collectors.toList());

            Query termsQuery = QueryBuilders.terms(tq -> tq
                    .field("categories") // ES의 필드명 (복수형)
                    .terms(tf -> tf.value(categoryValues))
            );

            boolQueryBuilder.must(termsQuery);
        }

        org.springframework.data.elasticsearch.core.query.Query nativeQuery = NativeQuery.builder()
                .withQuery(boolQueryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();
        SearchHits<AuctionSearchDocument> searchHits = elasticsearchOperations
                .search(nativeQuery, AuctionSearchDocument.class);

        return SearchHitSupport
                .searchPageFor(searchHits, pageable)
                .map(SearchHit::getContent);
    }

}
