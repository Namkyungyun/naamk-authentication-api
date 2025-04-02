package kr.co.naamk.naamkauthenticationapi.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class SearchCommon {
    public List< Map< String, String > > userStatus = getUserStatus();
    public List< Map< String, Object > > penaltyStatus = getPenaltyStatus();
    public List< Map< String, Object > > reportStatus = getReportStatus();


    private List< Map< String, String > > getUserStatus( ) {
        /// 계정 상태 3개 [정상, 탈퇴 신청, pw 초기화]
        List< RoleType > types = List.of( RoleType.user, RoleType.withdrawal, RoleType.resetPwdUser );

        return types.stream().map( type -> {
            Map< String, String > map = new HashMap<>();
            map.put( "label", type.getRoleNameKo() );
            map.put( "value", type.getRoleName() );

            return map;
        } ).toList();
    }

    private List< Map< String, Object > > getPenaltyStatus( ) {
        /// 패널티 상태 2개 [정상, 차단 ]
        List< PenaltyStatusType > types = List.of( PenaltyStatusType.ok, PenaltyStatusType.block );

        return types.stream().map( type -> {
            Map< String, Object > map = new HashMap<>();
            map.put( "label", type.getStatusName() );
            map.put( "value", type.getStatusValue() );
            return map;
        } ).toList();
    }

    private List< Map< String, Object > > getReportStatus( ) {
        /// 신고 상태 2개 [ 접수 , 처리완료 ]
        List< ReportStatusType > types = List.of( ReportStatusType.complete, ReportStatusType.unComplete );

        return types.stream().map( type -> {
            Map< String, Object > map = new HashMap<>();
            map.put( "label", type.getStatusName() );
            map.put( "value", type.getStatusValue() );
            return map;
        } ).toList();
    }


}
