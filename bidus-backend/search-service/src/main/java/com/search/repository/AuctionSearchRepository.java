package com.search.repository;

import com.search.entity.AuctionSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AuctionSearchRepository extends ElasticsearchRepository<AuctionSearchDocument, Long> {
}
