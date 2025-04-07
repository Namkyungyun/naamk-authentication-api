package kr.co.naamk.naamkauthenticationapi.mapstruct;

import kr.co.naamk.naamkauthenticationapi.domain.community.TbPost;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbReportsHist;
import kr.co.naamk.naamkauthenticationapi.web.dto.PostDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper( PostMapper.class );

    @Mapping(target = "rowNum", source = "rowNum", qualifiedByName = "mapToLong")
    @Mapping(target = "id", source = "id", qualifiedByName = "mapToLong")
    @Mapping(target = "type", source = "type", qualifiedByName = "mapToString")
    @Mapping(target = "content", source = "content", qualifiedByName = "mapToString")
    @Mapping(target = "userName", source = "userName", qualifiedByName = "mapToString")
    @Mapping(target = "channelName", source = "channelName", qualifiedByName = "mapToString")
    @Mapping(target = "penalty", source = "penalty", qualifiedByName = "mapToBoolean")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "mapToTimestamp")
    PostDto toPostDto( Map<String, Object> map);
    List< PostDto > toPostDtoList( List<Map<String, Object>> mapList);

    @Mapping(target = "postId", source = "postId", qualifiedByName = "mapToLong")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "mapToTimestamp")
    @Mapping(target = "content", source = "content", qualifiedByName = "mapToString")
    @Mapping(target = "postStatus", source = "postStatus", qualifiedByName = "mapToString")
    @Mapping(target = "channelPenaltyStatus", source = "channelPenaltyStatus", qualifiedByName = "mapToString")
    @Mapping(target = "penalty", source = "penalty", qualifiedByName = "mapToBoolean")
    @Mapping(target = "channelName", source = "channelName", qualifiedByName = "mapToString")
    @Mapping(target = "channelNickName", source = "channelNickName", qualifiedByName = "mapToString")
    @Mapping(target = "userName", source = "userName", qualifiedByName = "mapToString")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "mapToLong")
    @Mapping(target = "popScore", source = "popScore", qualifiedByName = "mapToLong")
    @Mapping(target = "likeCount", source = "likeCount", qualifiedByName = "mapToLong")
    @Mapping(target = "replyCount", source = "replyCount", qualifiedByName = "mapToLong")
    @Mapping(target = "thumbs", source = "thumbs", qualifiedByName = "mapToStringList")
    PostDto.PostDetailResponse toPostDetailResponse( Map<String, Object> map);

    // 🔥 여기에 MapStruct가 사용할 커스텀 변환기 명시
    @Named("mapToLong")
    default Long mapToLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(value.toString());
    }

    @Named("mapToBoolean")
    default Boolean mapToBoolean(Object value) {
        if (value == null) return null;
        return Boolean.valueOf(value.toString());
    }

    @Named("mapToString")
    default String mapToString(Object value) {
        return value != null ? value.toString() : null;
    }


    @Named("mapToStringList")
    default List<String> mapToStringList(Object value) {
        if (value == null) return null;

        if (value instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .toList();
        }

        if (value instanceof Object[] arr) {
            return Arrays.stream(arr)
                    .map(Object::toString)
                    .toList();
        }

        return List.of(value.toString()); // 단일 값인 경우
    }


    @Named("mapToTimestamp")
    default Timestamp mapToTimestamp(Object value) {
        if (value instanceof Timestamp ts) return ts;
        if (value instanceof java.util.Date date) return new Timestamp(date.getTime());
        if (value instanceof java.time.Instant instant) return Timestamp.from(instant);  // ✅ 요거 추가!
        if (value instanceof String str) {
            try {
                return Timestamp.valueOf(str);
            } catch (Exception ignored) {}
        }

        return null;
    }
}
