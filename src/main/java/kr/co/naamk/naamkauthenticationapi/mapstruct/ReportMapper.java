package kr.co.naamk.naamkauthenticationapi.mapstruct;

import kr.co.naamk.naamkauthenticationapi.domain.community.TbReportsHist;
import kr.co.naamk.naamkauthenticationapi.web.dto.ReportHistDto;
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
public interface ReportMapper {
    ReportMapper INSTANCE = Mappers.getMapper( ReportMapper.class );

    TbReportsHist toEntity( UserReportDto.CreateRequest dto);

    @Mapping(target = "rowNum", source = "rowNum", qualifiedByName = "mapToLong")
    @Mapping(target = "id", source = "id", qualifiedByName = "mapToLong")
    @Mapping(target = "reportUserId", source = "reportUserId", qualifiedByName = "mapToLong")
    @Mapping(target = "reportedUserId", source = "reportedLinkId", qualifiedByName = "mapToLong")
    @Mapping(target = "report", source = "report", qualifiedByName = "mapToBoolean")
    @Mapping(target = "penalty", source = "penalty", qualifiedByName = "mapToBoolean")
    @Mapping(target = "reportCreatedBy", source = "reportCreatedBy", qualifiedByName = "mapToString")
    @Mapping(target = "reportCreatedAt", source = "reportCreatedAt", qualifiedByName = "mapToTimestamp")
    ReportHistDto toReportHistResponse( Map<String, Object> map);
    List< ReportHistDto > toReportHistResponseList( List<Map<String, Object>> mapList);

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
