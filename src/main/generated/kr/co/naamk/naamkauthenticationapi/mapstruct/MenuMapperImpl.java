package kr.co.naamk.naamkauthenticationapi.mapstruct;

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

        return tbMenus;
    }

    @Override
    public TbMenus updateDtoToEntity(MenuDto.UpdateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        TbMenus tbMenus = new TbMenus();

        tbMenus.setName( dto.getName() );
        tbMenus.setDesc( dto.getDesc() );
        tbMenus.setParentId( dto.getParentId() );
        tbMenus.setOrder( dto.getOrder() );
        tbMenus.setIsActive( dto.getIsActive() );

        return tbMenus;
    }

    @Override
    public MenuDto toDto(TbMenus role) {
        if ( role == null ) {
            return null;
        }

        MenuDto.MenuDtoBuilder menuDto = MenuDto.builder();

        menuDto.id( role.getId() );
        menuDto.code( role.getCode() );
        menuDto.name( role.getName() );
        menuDto.desc( role.getDesc() );
        menuDto.parentId( role.getParentId() );
        menuDto.order( role.getOrder() );
        menuDto.isActive( role.getIsActive() );

        return menuDto.build();
    }
}
