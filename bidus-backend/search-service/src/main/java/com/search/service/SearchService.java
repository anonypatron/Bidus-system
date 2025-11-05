package com.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.search.entity.AuctionSearchDocument;
import com.search.repository.AuctionSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
@Slf4j
public class SearchService {

    private final AuctionSearchRepository auctionSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public Page<AuctionSearchDocument> search(
            String keyword,
            String status,
            String category,
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

        if (StringUtils.hasText(category)) {
            boolQueryBuilder.must(QueryBuilders.term()
                    .field("category")
                    .value(category)
                    .build()._toQuery()
            );
        }

        Query nativeQuery = NativeQuery.builder()
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
