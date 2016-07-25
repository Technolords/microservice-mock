package net.technolords.micro;

/**
 * Created by Technolords on 2016-Jul-25.
 */
public class ResponseContext {
    private String response;
    private String errorCode;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
