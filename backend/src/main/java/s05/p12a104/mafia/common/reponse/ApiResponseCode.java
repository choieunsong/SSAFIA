package s05.p12a104.mafia.common.reponse;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.common.util.EnumType;

@Getter
@RequiredArgsConstructor
public enum ApiResponseCode implements EnumType {

    SUCCESS("success"),
    FAIL("fail"),
    UNAUTHORIZED("unauthorized");

    private final String message;

    @Override
    public String getId() {
        return this.name();
    }

    @Override
    public String getText() {
        return message;
    }

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }
}
