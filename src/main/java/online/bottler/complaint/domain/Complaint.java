package online.bottler.complaint.domain;

import lombok.Getter;
import online.bottler.shared.exception.DomainException;

import java.time.LocalDateTime;

@Getter
public class Complaint {
    private Long id;

    private final Long letterId;

    private final Long reporterId;

    private final String description;

    private final LocalDateTime createdAt;

    private Complaint(Long letterId, Long reporterId, String description) {
        this.letterId = letterId;
        this.reporterId = reporterId;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    private Complaint(Long id, Long letterId, Long reporterId, String description, LocalDateTime createdAt) {
        this.id = id;
        this.letterId = letterId;
        this.reporterId = reporterId;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Complaint create(Long letterId, Long reporterId, String description) {
        return new Complaint(letterId, reporterId, description);
    }

    public static Complaint of(Long id, Long letterId, Long reporterId, String description, LocalDateTime createdAt) {
        return new Complaint(id, letterId, reporterId, description, createdAt);
    }

    public void validateDuplicateComplaint(Long letterId, Long reporterId) {
        if (letterId.equals(this.letterId) && reporterId.equals(this.reporterId)) {
            throw new DomainException("이미 신고된 편지입니다.");
        }
    }
}
