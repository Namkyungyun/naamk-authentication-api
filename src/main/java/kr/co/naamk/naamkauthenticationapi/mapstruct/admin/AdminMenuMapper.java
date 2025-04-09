package kr.co.naamk.naamkauthenticationapi.mapstruct.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminMenus;
import kr.co.naamk.naamkauthenticationapi.web.dto.admin.AdminMenuDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AdminMenuMapper {

    AdminMenuMapper INSTANCE = Mappers.getMapper( AdminMenuMapper.class );

    TbAdminMenus createDtoToEntity( AdminMenuDto.CreateRequest dto );
    TbAdminMenus updateDtoToEntity( AdminMenuDto.UpdateRequest dto );

    AdminMenuDto toDto( TbAdminMenus entity );
    List< AdminMenuDto > toDtoList( List< TbAdminMenus > entities );

    @Mapping(target = "id", source = "id", qualifiedByName = "mapToInteger")
    @Mapping(target = "parentId", source = "parentId", qualifiedByName = "mapToInteger")
    @Mapping(target = "name", source = "name", qualifiedByName = "mapToString")
    @Mapping(target = "url", source = "url", qualifiedByName = "mapToString")
    @Mapping(target = "order", source = "order", qualifiedByName = "mapToInteger")
    @Mapping(target = "level", source = "level", qualifiedByName = "mapToInteger")
    @Mapping(target = "path", source = "path", qualifiedByName = "mapToString")
    @Mapping(target = "submenus", ignore = true) // 트리는 별도로 처리
    AdminMenuDto.MenuTreeDto objToMenuTreeDTO( Map<String, Object> row);
    List<AdminMenuDto.MenuTreeDto> objsToMenuTreeDTOS(List<Map<String, Object>> rows);

    // 🔥 여기에 MapStruct가 사용할 커스텀 변환기 명시
    @Named("mapToLong")
    default Long mapToLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(value.toString());
    }

    @Named("mapToInteger")
    default Integer mapToInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.intValue();
        return Integer.parseInt(value.toString());
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


}
