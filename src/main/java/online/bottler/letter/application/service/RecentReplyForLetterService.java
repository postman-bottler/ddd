package online.bottler.letter.application.service;

public interface RecentReplyForLetterService {
    void push(Long receiverId, Long id, String label);

    void delete(Long receiverId, Long id, String label);
}
