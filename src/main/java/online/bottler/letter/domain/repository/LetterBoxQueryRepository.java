package online.bottler.letter.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.domain.QLetter;
import online.bottler.letter.domain.QLetterBox;
import online.bottler.letter.domain.QLetterBoxType;
import online.bottler.letter.domain.QLetterContent;
import online.bottler.letter.domain.QReplyLetter;
import online.bottler.letter.domain.model.LetterSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import online.bottler.letter.domain.BoxType;
import online.bottler.letter.domain.LetterType;

@Repository
@RequiredArgsConstructor
public class LetterBoxQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<LetterSummaryProjection> fetchLetterSummariesByUserIdAndBoxType(Long userId, BoxType boxType, Pageable pageable) {
        QLetterBox letterBox = QLetterBox.letterBox;
        QLetterBoxType letterBoxType = letterBox.letterBoxType;

        QLetter letter = QLetter.letter;
        QLetterContent letterContent = letter.letterContent;

        QReplyLetter replyLetter = QReplyLetter.replyLetter;
        QLetterContent replyLetterContent = replyLetter.letterContent;

        StringExpression letterTitle = getLetterTitle(letterBoxType, letterContent, replyLetterContent);
        StringExpression letterLabel = getLetterLabel(letterBoxType, letterContent, replyLetterContent);

        BooleanBuilder condition = buildFetchCondition(userId, letterBox, letterBoxType, boxType);

        List<LetterSummaryProjection> letterSummaryProjections = queryFactory
                .select(Projections.constructor(
                        LetterSummaryProjection.class,
                        letterBox.letterId,
                        letterTitle,
                        letterLabel,
                        letterBoxType.letterType,
                        letterBoxType.boxType,
                        letterBox.createdAt
                ))
                .from(letterBox)
                .leftJoin(letter).on(letterBox.letterId.eq(letter.id)
                        .and(letterBoxType.letterType.eq(LetterType.LETTER)))
                .leftJoin(replyLetter).on(letterBox.letterId.eq(replyLetter.id)
                        .and(letterBoxType.letterType.eq(LetterType.REPLY_LETTER)))
                .where(condition)
                .orderBy(letterBox.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = countLetters(condition);

        return new PageImpl<>(letterSummaryProjections, pageable, total);
    }

    private long countLetters(BooleanBuilder condition) {
        QLetterBox letterBox = QLetterBox.letterBox;

        Long count = queryFactory
                .select(letterBox.id.count())
                .from(letterBox)
                .where(condition)
                .fetchOne();

        return count != null ? count : 0L;
    }

    public void deleteLetters(Long userId, List<Long> letterIds, LetterType letterType, BoxType boxType) {
        QLetterBox letterBox = QLetterBox.letterBox;
        BooleanBuilder condition = buildDeletionCondition(userId, letterIds, letterType, boxType, letterBox);

        queryFactory
                .delete(letterBox)
                .where(condition)
                .execute();
    }

    private StringExpression getLetterTitle(QLetterBoxType letterBoxType, QLetterContent letterContent,
                                            QLetterContent replyLetterContent) {
        return new CaseBuilder()
                .when(letterBoxType.letterType.eq(LetterType.LETTER)).then(letterContent.title)
                .when(letterBoxType.letterType.eq(LetterType.REPLY_LETTER)).then(replyLetterContent.title)
                .otherwise("Unknown Title");
    }

    private StringExpression getLetterLabel(QLetterBoxType letterBoxType, QLetterContent letterContent,
                                            QLetterContent replyLetterContent) {
        return new CaseBuilder()
                .when(letterBoxType.letterType.eq(LetterType.LETTER)).then(letterContent.label)
                .when(letterBoxType.letterType.eq(LetterType.REPLY_LETTER)).then(replyLetterContent.label)
                .otherwise("Unknown Label");
    }

    private BooleanBuilder buildFetchCondition(Long userId, QLetterBox letterBox, QLetterBoxType letterBoxType, BoxType boxType) {
        BooleanBuilder condition = new BooleanBuilder();
        condition.and(letterBox.userId.eq(userId));

        if (boxType != null) {
            condition.and(letterBoxType.boxType.eq(boxType));
        }

        return condition;
    }

    private BooleanBuilder buildDeletionCondition(Long userId, List<Long> letterIds, LetterType letterType,
                                                  BoxType boxType, QLetterBox letterBox) {
        QLetterBoxType letterBoxType = letterBox.letterBoxType;
        BooleanBuilder condition = new BooleanBuilder();

        if (userId != null) {
            condition.and(letterBox.userId.eq(userId));
        }
        if (letterIds != null) {
            condition.and(letterBox.letterId.in(letterIds));
        }
        if (letterType != null) {
            condition.and(letterBoxType.letterType.eq(letterType));
        }
        if (boxType != null) {
            condition.and(letterBoxType.boxType.eq(boxType));
        }

        return condition;
    }
}
