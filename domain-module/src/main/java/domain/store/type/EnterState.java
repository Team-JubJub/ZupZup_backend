package domain.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnterState {

    NEW("신규 신청", "새로 신청이 들어옴"),
    WAIT("노출 대기", "어플에서 수정 가능"),
    CONFIRM("노출 승인", "승인 후 어플에서 확인 가능");

    private final String name;
    private final String desc;

}
