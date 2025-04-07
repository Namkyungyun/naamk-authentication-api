package kr.co.naamk.naamkauthenticationapi.mapstruct;

import kr.co.naamk.naamkauthenticationapi.domain.community.TbReportsHist;
import kr.co.naamk.naamkauthenticationapi.web.dto.PostReportDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PostReportMapper {
    PostReportMapper INSTANCE = Mappers.getMapper( PostReportMapper.class );

    TbReportsHist toEntity( PostReportDto.CreateRequest dto);

    @Mapping(target = "rowNum", source = "rowNum", qualifiedByName = "mapToLong")
    @Mapping(target = "id", source = "id", qualifiedByName = "mapToLong")
    @Mapping(target = "latestCreatedAt", source = "latestCreatedAt", qualifiedByName = "mapToTimestamp")
    @Mapping(target = "reportedPostId", source = "reportedPostId", qualifiedByName = "mapToLong")
    @Mapping(target = "reportedUserName", source = "reportedUserName", qualifiedByName = "mapToString")
    @Mapping(target = "reportedChannelName", source = "reportedChannelName", qualifiedByName = "mapToString")
    @Mapping(target = "reportCount", source = "reportCount", qualifiedByName = "mapToLong")
    @Mapping(target = "report", source = "report", qualifiedByName = "mapToBoolean")
    @Mapping(target = "penalty", source = "penalty", qualifiedByName = "mapToBoolean")
    @Mapping(target = "penaltyCreatedBy", source = "penaltyCreatedBy", qualifiedByName = "mapToString")
    @Mapping(target = "penaltyCreatedAt", source = "penaltyCreatedAt", qualifiedByName = "mapToTimestamp")
    PostReportDto.ListResponse toListResponseDto( Map<String, Object> map);
    List< PostReportDto.ListResponse > toListResponseDtoList( List<Map<String, Object>> mapList);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapToLong")
    @Mapping(target = "report", source = "report", qualifiedByName = "mapToBoolean")
    @Mapping(target = "latestCreatedAt", source = "latestCreatedAt", qualifiedByName = "mapToTimestamp")
    @Mapping(target = "reportedUserId", source = "reportedUserId", qualifiedByName = "mapToLong")
    @Mapping(target = "reportedUserName", source = "reportedUserName", qualifiedByName = "mapToString")
    @Mapping(target = "reportedChannelId", source = "reportedChannelId", qualifiedByName = "mapToLong")
    @Mapping(target = "reportedChannelName", source = "reportedChannelName", qualifiedByName = "mapToString")
    @Mapping(target = "reportedPostId", source = "reportedPostId", qualifiedByName = "mapToLong")
    @Mapping(target = "reportedPostActive", source = "reportedPostActive", qualifiedByName = "mapToBoolean")
    @Mapping(target = "reportedPostContent", source = "reportedPostContent", qualifiedByName = "mapToString")
    @Mapping(target = "penalty", source = "penalty", qualifiedByName = "mapToBoolean")
    @Mapping(target = "penaltyCreatedAt", source = "penaltyCreatedAt", qualifiedByName = "mapToTimestamp")
    @Mapping(target = "penaltyCreatedBy", source = "penaltyCreatedBy", qualifiedByName = "mapToString")
    @Mapping(target = "penaltyDescription", source = "penaltyDescription", qualifiedByName = "mapToString")
    PostReportDto.DetailResponse toDetailResponse( Map<String, Object> map);


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
