//BO HUANG 1584795

package common.feedback;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonResult <T> implements Serializable {

    public interface CommonResultView{};

    //status code
    private  long code;

    //status message
    private  String message;

    //data
    private T data;

    private  CommonResult(long code, String message, T data){
        this.code=code;
        this.message=message;
        this.data=data;
    }
}
