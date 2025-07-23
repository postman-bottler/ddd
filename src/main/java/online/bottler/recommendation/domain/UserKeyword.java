package online.bottler.recommendation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "user_keyword",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_keyword", columnNames = {"user_id", "keyword"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserKeyword extends AbstractAuditing {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Builder
    private UserKeyword(Long id, Long userId, String keyword) {
        this.id = id;
        this.userId = userId;
        this.keyword = keyword;
    }

    public static UserKeyword create(Long userId, String keyword) {
        return new UserKeyword(null, userId, keyword);
    }

    public static List<UserKeyword> createList(Long userId, List<String> keywords) {
        return keywords.stream().map(keyword -> UserKeyword.create(userId, keyword)).toList();
    }
}
