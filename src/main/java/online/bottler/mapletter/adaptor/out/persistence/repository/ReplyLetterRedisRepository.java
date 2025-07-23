package online.bottler.mapletter.adaptor.out.persistence.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.AdaptorException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReplyLetterRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public List<Object> getRecentReplies(String key) {
        try {
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            throw new AdaptorException("Redis에서 최근 답장 목록을 가져오는 중 오류 발생");
        }
    }

    public void saveRecentReply(String key, String value, int redisSavedReplySize) {
        Long size = redisTemplate.opsForList().size(key);
        if (size != null && size >= redisSavedReplySize) {
            redisTemplate.opsForList().rightPop(key);
        }

        if (!redisTemplate.opsForList().range(key, 0, -1).contains(value)) {
            redisTemplate.opsForList().leftPush(key, value);
        }
    }

    public void deleteRecentReply(String key, String value) {
        redisTemplate.opsForList().remove(key, 1, value);
    }
}
