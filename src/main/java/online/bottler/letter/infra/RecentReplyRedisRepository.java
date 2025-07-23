package online.bottler.letter.infra;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.service.RecentReplyForLetterService;
import online.bottler.letter.util.RedisLetterKeyUtil;
import online.bottler.reply.application.ReplyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecentReplyRedisRepository implements RecentReplyForLetterService {

    private final RedisTemplate<String, Object> redisTemplateForReply;

    @Value("${recommendation.saved-replies}")
    private int redisSavedReply;

    @Override
    public void push(Long receiverId, Long id, String label) {
        String replyKey = getReplyKey(receiverId);
        String replyValue = getReplyValue(id, label);

        Long size = redisTemplateForReply.opsForList().size(replyKey);
        if (size != null && size >= redisSavedReply) {
            redisTemplateForReply.opsForList().rightPop(replyKey);
        }

        if (!Objects.requireNonNull(redisTemplateForReply.opsForList().range(replyKey, 0, -1)).contains(replyValue)) {
            redisTemplateForReply.opsForList().leftPush(replyKey, replyValue);
        }
    }

    @Override
    public void delete(Long receiverId, Long id, String label) {
        String key = getReplyKey(receiverId);
        String value = getReplyValue(id, label);

        redisTemplateForReply.opsForList().remove(key, 1, value);
    }

    private String getReplyKey(Long receiverId) {
        return RedisLetterKeyUtil.getReplyKey(receiverId);
    }

    private String getReplyValue(Long letterId, String labelUrl) {
        return ReplyType.KEYWORD + ":" + letterId + ":" + labelUrl;
    }
}
