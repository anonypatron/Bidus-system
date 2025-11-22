package com.auction.repository;

import com.auction.dto.response.CategoryStatsDto;
import com.auction.entity.Auction;
import com.common.AuctionStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.auction.entity.QAuction.auction;
import static com.auction.entity.QAuctionCategory.auctionCategory;
import static com.auction.entity.QCategory.category;

@RequiredArgsConstructor
public class AuctionRepositoryImpl implements AuctionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 시작 시간이 지났지만 아직 '예정' 상태인 경매 조회(카테고리 포함)
    @Override
    public List<Auction> findAllByStatusAndStartTimeBeforeWithCategories(
            AuctionStatus status,
            Instant now
    ) {
        return queryFactory
                .selectFrom(auction)
                .distinct()
                .leftJoin(auction.auctionCategories, auctionCategory).fetchJoin()
                .leftJoin(auctionCategory.category, category).fetchJoin()
                .where(
                        auction.status.eq(status),
                        auction.startTime.before(now)
                )
                .fetch();
    }

    // 종료 시간이 지났지만 아직 '진행 중' 상태인 경매 조회
    @Override
    public List<Auction> findAllByStatusAndEndTimeBefore(
            AuctionStatus status,
            Instant now
    ) {
        return queryFactory
                .selectFrom(auction)
                .distinct()
                .where(
                        auction.status.eq(status),
                        auction.endTime.before(now)
                )
                .fetch();
    }

    // page를 사용할 때는 fetch join보다 batchsize(In query)를 적극 활용할 것.
    // Pageable과 oneToMany는 fetchJoin과 함께 사용하면 안됨.
    @Override
    public Page<Auction> findAllByStatusWithCategories(
            Pageable pageable,
            AuctionStatus status
    ) {
        JPAQuery<Auction> contentQuery = queryFactory
                .selectFrom(auction)
                .where(auction.status.eq(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        for (Sort.Order order : pageable.getSort()) {
            PathBuilder<Auction> pathBuilder = new PathBuilder<>(auction.getType(), auction.getMetadata());
            contentQuery.orderBy(new OrderSpecifier<>(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(order.getProperty(), Comparable.class)
            ));
        }

        List<Auction> content = contentQuery.fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(auction.status.eq(status));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Auction> findAllByWinnerIdAndStatusWithCategories(
            Long id,
            AuctionStatus status
    ) {
        return queryFactory
                .selectFrom(auction)
                .distinct()
                .leftJoin(auction.auctionCategories, auctionCategory).fetchJoin()
                .where(
                        auction.winnerId.eq(id),
                        auction.status.eq(status)
                )
                .fetch();
    }

    @Override
    public List<Auction> findAllBySellerIdAndStatusWithCategories(
            Long id,
            AuctionStatus status
    ) {
        return queryFactory
                .selectFrom(auction)
                .distinct()
                .leftJoin(auction.auctionCategories, auctionCategory).fetchJoin()
                .where(
                        auction.sellerId.eq(id),
                        auction.status.eq(status)
                )
                .fetch();
    }

    @Override
    public List<Auction> findCurrentBiddingByUserId(Long id) {
        return queryFactory
                .selectFrom(auction)
                .distinct()
                .leftJoin(auction.auctionCategories, auctionCategory).fetchJoin()
                .where(
                        auction.highestBidderId.eq(id),
                        auction.status.eq(AuctionStatus.IN_PROGRESS)
                )
                .fetch();
    }

    // 단일 행으로 단정하면 예외발생 가능성 있음(여러개의 행이 반환될 수 있음)
    // 따라서 리스트형태로 받으면 jpa가 내부적으로 엔티티 중복을 제거함.
    // fetch + distinct 사용
    @Override
    public Optional<Auction> findByIdWithCategories(Long id) {
        List<Auction> results = queryFactory
                .selectFrom(auction)
                .distinct()
                .leftJoin(auction.auctionCategories, auctionCategory).fetchJoin()
                .leftJoin(auctionCategory.category, category).fetchJoin()
                .where(auction.id.eq(id))
                .fetch();
        return Optional.ofNullable(results.isEmpty() ? null : results.get(0));
    }

    @Override
    public Long findSellCountThisMonth(Long id) {
        Instant thisMonthStart = getStartOfThisMonth();
        Instant nextMonthStart = getStartOfNextMonth();

        return queryFactory
                .select(auction.id.count())
                .from(auction)
                .where(
                        auction.sellerId.eq(id),
                        auction.status.eq(AuctionStatus.CLOSED),
                        auction.winnerId.isNotNull(),
                        auction.endTime.goe(thisMonthStart), // >= 이번달 1일
                        auction.endTime.lt(nextMonthStart) // < 다음달 1일
                )
                .fetchOne();
    }

    @Override
    public Long findWinCountThisMonth(Long id) {
        Instant thisMonthStart = getStartOfThisMonth();
        Instant nextMonthStart = getStartOfNextMonth();

        return queryFactory
                .select(auction.id.count())
                .from(auction)
                .where(
                        auction.winnerId.eq(id),
                        auction.status.eq(AuctionStatus.CLOSED),
                        auction.endTime.goe(thisMonthStart),
                        auction.endTime.lt(nextMonthStart)
                )
                .fetchOne();
    }

    @Override
    public Long findCurrentBiddingCount(Long id) {
        return queryFactory
                .select(auction.id.count())
                .from(auction)
                .where(
                        auction.status.eq(AuctionStatus.IN_PROGRESS),
                        auction.highestBidderId.eq(id)
                )
                .fetchOne();
    }

    @Override
    public List<CategoryStatsDto> findTop10Categories(Long id) {
        return queryFactory
                .select(Projections.constructor(CategoryStatsDto.class,
                        category.name,
                        category.name.count()
                ))
                .from(auction)
                .leftJoin(auction.auctionCategories, auctionCategory)
                .leftJoin(auctionCategory.category, category)
                .where(
                        auction.status.eq(AuctionStatus.CLOSED),
                        auction.winnerId.eq(id)
                )
                .groupBy(category.name)
                .orderBy(category.name.count().desc())
                .limit(10)
                .fetch();
    }

    // 이번달 1일 00:00:00
    private Instant getStartOfThisMonth() {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime startOfMonth = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        return startOfMonth.toInstant();
    }

    // 다음달 1일 00:00:00
    private Instant getStartOfNextMonth() {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime startOfNextMonth = now.withDayOfMonth(1)
                .plusMonths(1)
                .truncatedTo(ChronoUnit.DAYS);
        return startOfNextMonth.toInstant();
    }

}
