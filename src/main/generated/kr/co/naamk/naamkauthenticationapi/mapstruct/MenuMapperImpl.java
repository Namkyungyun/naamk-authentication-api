package kr.co.naamk.naamkauthenticationapi.mapstruct;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.naamk.naamkauthenticationapi.domain.TbMenus;
import kr.co.naamk.naamkauthenticationapi.web.dto.MenuDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class MenuMapperImpl implements MenuMapper {

    @Override
    public TbMenus createDtoToEntity(MenuDto.CreateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        TbMenus tbMenus = new TbMenus();

        tbMenus.setCode( dto.getCode() );
        tbMenus.setName( dto.getName() );
        tbMenus.setDesc( dto.getDesc() );
        tbMenus.setParentId( dto.getParentId() );
        tbMenus.setUrl( dto.getUrl() );

        return tbMenus;
    }

    @Override
    public TbMenus updateDtoToEntity(MenuDto.UpdateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        TbMenus tbMenus = new TbMenus();

        tbMenus.setId( dto.getId() );
        tbMenus.setName( dto.getName() );
        tbMenus.setDesc( dto.getDesc() );
        tbMenus.setParentId( dto.getParentId() );
        tbMenus.setUrl( dto.getUrl() );
        tbMenus.setOrder( dto.getOrder() );
        tbMenus.setIsActive( dto.getIsActive() );

        return tbMenus;
    }

    @Override
    public MenuDto toDto(TbMenus entity) {
        if ( entity == null ) {
            return null;
        }

        MenuDto.MenuDtoBuilder menuDto = MenuDto.builder();

        menuDto.id( entity.getId() );
        menuDto.code( entity.getCode() );
        menuDto.name( entity.getName() );
        menuDto.desc( entity.getDesc() );
        menuDto.parentId( entity.getParentId() );
        menuDto.order( entity.getOrder() );
        menuDto.isActive( entity.getIsActive() );
        menuDto.url( entity.getUrl() );

        return menuDto.build();
    }

    @Override
    public List<MenuDto> toDtoList(List<TbMenus> entities) {
        if ( entities == null ) {
            return null;
        }

        List<MenuDto> list = new ArrayList<MenuDto>( entities.size() );
        for ( TbMenus tbMenus : entities ) {
            list.add( toDto( tbMenus ) );
        }

        return list;
    }
}
