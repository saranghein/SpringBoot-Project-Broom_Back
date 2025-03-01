package com.kwhackathon.broom.board.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.kwhackathon.broom.board.dto.BoardResponse.BoardWithBookmarkDto;
import com.kwhackathon.broom.board.entity.QBoard;
import com.kwhackathon.broom.bookmark.entity.QBookmark;
import com.kwhackathon.broom.participant.entity.QParticipant;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardSearchRepository {
    private final JPAQueryFactory queryFactory;

    public Slice<BoardWithBookmarkDto> findBoardWithBookmarkByCondition(Pageable pageable, String userId, String title,
            String place, String trainingDate, boolean recruiting) {
        QBoard b = QBoard.board;
        QParticipant p = QParticipant.participant;
        QBookmark bm = QBookmark.bookmark;

        // 모집 중인 인원 수 계산
        NumberExpression<Long> participantCount = new CaseBuilder()
                .when(p.isExpelled.isFalse().and(b.user.userId.ne(p.user.userId)))
                .then(1L)
                .otherwise(0L)
                .sum();

        // 북마크 여부 계산
        BooleanExpression isBookmarked = JPAExpressions
                .select(bm.count())
                .from(bm)
                .where(bm.board.eq(b).and(bm.user.userId.eq(userId)))
                .gt(0L);

        List<BoardWithBookmarkDto> result = queryFactory.select(
                Projections.constructor(BoardWithBookmarkDto.class,
                        b, participantCount, isBookmarked))
                .from(b)
                .leftJoin(p).on(p.board.boardId.eq(b.boardId))
                .leftJoin(bm).on(bm.board.boardId.eq(b.boardId).and(bm.user.userId.eq(userId)))
                .where(titleContaining(title), placeContaining(place), trainingDateEqual(trainingDate))
                .groupBy(b)
                .having(recruiting ? participantCount.lt(b.personnel) : null)
                .orderBy(b.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) {
            result.remove(result.size() - 1);
        }

        // 페이지네이션을 위해 slice구현체 반환
        return new SliceImpl<>(result, pageable, hasNext);
    }

    private BooleanExpression titleContaining(String title) {
        if (title == null) {
            return null;
        }
        return QBoard.board.title.contains(title);
    }

    private BooleanExpression placeContaining(String place) {
        if (place == null) {
            return null;
        }
        return QBoard.board.place.contains(place);
    }

    private BooleanExpression trainingDateEqual(String trainingDate) {
        if (trainingDate == null) {
            return null;
        }
        return QBoard.board.trainingDate.eq(LocalDate.parse(trainingDate));
    }
}
