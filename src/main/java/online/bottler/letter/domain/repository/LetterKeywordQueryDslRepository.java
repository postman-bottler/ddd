package online.bottler.letter.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.domain.LetterStatus;
import online.bottler.letter.domain.QLetterKeyword;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LetterKeywordQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<Long> getMatchedLetters(List<String> userKeywords, List<Long> letterIds, int limit) {
        QLetterKeyword qLetterKeyword = QLetterKeyword.letterKeyword;

        return queryFactory
                .select(qLetterKeyword.letter.id)
                .from(qLetterKeyword)
                .where(qLetterKeyword.keyword.in(userKeywords)
                        .and(qLetterKeyword.letter.id.notIn(letterIds))
                        .and(qLetterKeyword.status.eq(LetterStatus.OPEN)))
                .groupBy(qLetterKeyword.letter.id)
                .orderBy(qLetterKeyword.letter.id.count().desc())
                .limit(limit)
                .fetch();
    }
}
