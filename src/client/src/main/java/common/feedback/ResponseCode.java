//BO HUANG 1584795

package common.feedback;

import lombok.Getter;

@Getter
public enum ResponseCode {

    SUCCESS(200, "Operation successful"),
    FAILED(500, "Operation failed"),
    VALIDATE_FAILED(404, "The parameter test failed."),
    UNAUTHORIZED(401, "Not logged in yet or the token has expired."),
    FORBIDDEN(403, "Lack of relevant authorization");

    private Long code;
    private String message;



    private ResponseCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

}
