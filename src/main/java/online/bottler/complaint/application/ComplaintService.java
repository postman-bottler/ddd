package online.bottler.complaint.application;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import online.bottler.complaint.application.port.ComplaintPersistencePort;
import online.bottler.complaint.application.port.ComplaintUseCase;
import online.bottler.complaint.application.port.KeywordComplaintPersistencePort;
import online.bottler.complaint.application.port.KeywordReplyComplaintPersistencePort;
import online.bottler.complaint.application.port.MapComplaintPersistencePort;
import online.bottler.complaint.application.port.MapReplyComplaintPersistencePort;
import online.bottler.complaint.domain.Complaint;
import online.bottler.complaint.domain.ComplaintType;
import online.bottler.complaint.domain.Complaints;
import online.bottler.shared.exception.ApplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComplaintService implements ComplaintUseCase {
    private final KeywordComplaintPersistencePort keywordComplaintPersistencePort;
    private final MapComplaintPersistencePort mapComplaintPersistencePort;
    private final KeywordReplyComplaintPersistencePort keywordReplyComplaintPersistencePort;
    private final MapReplyComplaintPersistencePort mapReplyComplaintPersistencePort;

    @Override
    @Transactional
    public ComplaintResponse complain(ComplaintCommand complaintCommand) {
        Complaint newComplaint = complaintCommand.toComplaint();
        ComplaintPersistencePort persistencePort = getPersistencePort(complaintCommand.type());
        validateDuplicateComplaint(persistencePort, complaintCommand);
        return ComplaintResponse.from(persistencePort.save(newComplaint));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean needWarning(ComplaintType type, Long letterId) {
        ComplaintPersistencePort persistencePort = getPersistencePort(type);
        Complaints complaints = persistencePort.findByLetterId(letterId);
        return complaints.needWarning();
    }

    private ComplaintPersistencePort getPersistencePort(ComplaintType type) {
        return switch (type) {
            case MAP_LETTER -> mapComplaintPersistencePort;
            case MAP_REPLY_LETTER -> mapReplyComplaintPersistencePort;
            case KEYWORD_LETTER -> keywordComplaintPersistencePort;
            case KEYWORD_REPLY_LETTER -> keywordReplyComplaintPersistencePort;
        };
    }

    private void validateDuplicateComplaint(ComplaintPersistencePort persistencePort,
                                            ComplaintCommand complaintCommand) {
        Optional<Complaint> complaint = persistencePort.findByLetterIdAndReporterId(
                complaintCommand.letterId(), complaintCommand.reporterId());
        if (complaint.isPresent()) {
            throw new ApplicationException("이미 신고한 편지입니다.");
        }
    }
}
