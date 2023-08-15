package dto;

import lombok.Data;
import lombok.NoArgsConstructor;


public class UnSuccessReg {
    public String error;

    public UnSuccessReg(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
