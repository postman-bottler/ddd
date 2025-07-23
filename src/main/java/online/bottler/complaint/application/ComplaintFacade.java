package online.bottler.complaint.application;

import lombok.RequiredArgsConstructor;
import online.bottler.complaint.application.port.ComplaintUseCase;
import online.bottler.complaint.domain.ComplaintType;
import online.bottler.letter.application.service.BlockLetterService;
import online.bottler.letter.application.service.BlockReplyLetterService;
import online.bottler.mapletter.application.BlockMapLetterType;
import online.bottler.mapletter.application.port.in.MapLetterUseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ComplaintFacade {
    private final ComplaintUseCase complaintUseCase;
    private final MapLetterUseCase mapLetterUseCase;
    private final BlockReplyLetterService blockReplyLetterService;
    private final BlockLetterService blockLetterService;

    @Transactional
    public ComplaintResponse complain(ComplaintCommand complaintCommand) {
        ComplaintResponse complaintResponse = complaintUseCase.complain(complaintCommand);
        if (complaintUseCase.needWarning(complaintCommand.type(), complaintCommand.letterId())) {
            blockLetter(complaintCommand.type(), complaintCommand.letterId());
        }
        return complaintResponse;
    }

    private void blockLetter(ComplaintType type, Long letterId) {
        switch (type) {
            case MAP_LETTER -> mapLetterUseCase.letterBlock(BlockMapLetterType.MAP_LETTER, letterId);
            case MAP_REPLY_LETTER -> mapLetterUseCase.letterBlock(BlockMapLetterType.REPLY, letterId);
            case KEYWORD_LETTER -> blockLetterService.blockLetter(letterId);
            case KEYWORD_REPLY_LETTER -> blockReplyLetterService.blockReplyLetter(letterId);
        }
    }
}
