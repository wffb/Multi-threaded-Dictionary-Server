//BO HUANG 1584795

package common.feedback;

import lombok.Getter;

@Getter
public enum RequestCode {


    //operation code
    ADD_WORD("0"),
    DELETE("1"),
    UPDATE("2"),
    SEARCH("3"),
    ADD_MEANING("4");


    private String code;

    private RequestCode(String num){
        this.code = num;
    }
}
