package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisNotificationEntity;
import kr.co.naamk.naamkauthenticationapi.redis.repository.RedisNotificationRepository;
import kr.co.naamk.naamkauthenticationapi.redis.type.NotificationMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.type.NotificationType;
import kr.co.naamk.naamkauthenticationapi.utils.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisService {

    public final long TIME_TO_LIVE_DAYS = 7 * 24 * 60 * 60 * 1000; // 일 * 시간 * 분 * 초 * 밀리 = 7일
    private final RedisNotificationRepository notificationRedisRepository;

    /**
     * <pre>
     *     사용자에게 필요한 알림을 설정한다
     * </pre>
     *
     * @param username
     * @param type
     * @param linkedId
     * @throws ServiceException
     */
    public void saveNotificationPenalty( final String username, final NotificationType type, final Boolean isBlock, final Long linkedId ) throws ServiceException {
        UUID uuid = UUID.randomUUID();
        String serial = uuid.toString();

        RedisNotificationEntity.RedisNotificationEntityBuilder notificationRedis = RedisNotificationEntity.builder();
        notificationRedis.id( serial );
        notificationRedis.username( username ); // encrypted username
        notificationRedis.popPower( 0 );
        notificationRedis.type( "penalty" );
        notificationRedis.linkUrl( null ); // url은 없음
        notificationRedis.timeToLive( TIME_TO_LIVE_DAYS );
        notificationRedis.createdAt( DateTimeUtil.getLocalDateTime() );

        String message = null;
        switch ( type ) {
            case post:
                message = isBlock ?
                        NotificationMessageType.post_penalty_block.getMessage()
                        : NotificationMessageType.post_penalty_ok.getMessage();
                notificationRedis.message( message);
                break;
            case user:
                message = isBlock ?
                        NotificationMessageType.user_penalty_block.getMessage()
                        : NotificationMessageType.user_penalty_ok.getMessage();
                notificationRedis.message( message );
                break;
            default:
                throw new ServiceException( ServiceMessageType.SERVICE_ID_NOT_VALID );
        }

        notificationRedisRepository.save( notificationRedis.build() );
    }


    /**
     * <pre>사용자의 알림 정보를 조회 하도록 한다.</pre>
     *
     * @param username
     * @return
     * @throws ServiceException
     */
    public List< RedisNotificationEntity > searchNotification( final String username ) throws ServiceException {
        List< RedisNotificationEntity > NotificationRedisList = notificationRedisRepository.findByUsername( username ); // encrypted username
        if ( NotificationRedisList.isEmpty() ) {
            throw new ServiceException( ServiceMessageType.NO_DATA );
        }

        List< RedisNotificationEntity > latestNotificationList = NotificationRedisList.stream()
                .sorted( Comparator.comparing(
                                RedisNotificationEntity::getCreatedAt,
                                Comparator.nullsLast( Comparator.naturalOrder() ) )
                        .reversed() )
                .collect( Collectors.toList() );
        return latestNotificationList;
    }

}
