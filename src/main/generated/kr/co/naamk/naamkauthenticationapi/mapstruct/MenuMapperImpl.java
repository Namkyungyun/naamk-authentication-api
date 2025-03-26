package kr.co.naamk.naamkauthenticationapi.mapstruct;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminMenus;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminMenuDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class MenuMapperImpl implements MenuMapper {

    @Override
    public TbAdminMenus createDtoToEntity(AdminMenuDto.CreateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        TbAdminMenus tbAdminMenus = new TbAdminMenus();

        tbAdminMenus.setCode( dto.getCode() );
        tbAdminMenus.setName( dto.getName() );
        tbAdminMenus.setDesc( dto.getDesc() );
        tbAdminMenus.setParentId( dto.getParentId() );
        tbAdminMenus.setUrl( dto.getUrl() );

        return tbAdminMenus;
    }

    @Override
    public TbAdminMenus updateDtoToEntity(AdminMenuDto.UpdateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        TbAdminMenus tbAdminMenus = new TbAdminMenus();

        tbAdminMenus.setId( dto.getId() );
        tbAdminMenus.setName( dto.getName() );
        tbAdminMenus.setDesc( dto.getDesc() );
        tbAdminMenus.setParentId( dto.getParentId() );
        tbAdminMenus.setUrl( dto.getUrl() );
        tbAdminMenus.setOrder( dto.getOrder() );
        tbAdminMenus.setIsActive( dto.getIsActive() );

        return tbAdminMenus;
    }

    @Override
    public AdminMenuDto toDto(TbAdminMenus entity) {
        if ( entity == null ) {
            return null;
        }

        AdminMenuDto.AdminMenuDtoBuilder adminMenuDto = AdminMenuDto.builder();

        adminMenuDto.id( entity.getId() );
        adminMenuDto.code( entity.getCode() );
        adminMenuDto.name( entity.getName() );
        adminMenuDto.desc( entity.getDesc() );
        adminMenuDto.parentId( entity.getParentId() );
        adminMenuDto.order( entity.getOrder() );
        adminMenuDto.isActive( entity.getIsActive() );
        adminMenuDto.url( entity.getUrl() );

        return adminMenuDto.build();
    }

    @Override
    public List<AdminMenuDto> toDtoList(List<TbAdminMenus> entities) {
        if ( entities == null ) {
            return null;
        }

        List<AdminMenuDto> list = new ArrayList<AdminMenuDto>( entities.size() );
        for ( TbAdminMenus tbAdminMenus : entities ) {
            list.add( toDto( tbAdminMenus ) );
        }

        return list;
    }
}
