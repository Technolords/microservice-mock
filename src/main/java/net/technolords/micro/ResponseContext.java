package net.technolords.micro;

public class ResponseContext {
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String XML_CONTENT_TYPE = "application/xml";
    public static final String PLAIN_TEXT_CONTENT_TYPE = "text/plain";
    public static final String DEFAULT_CONTENT_TYPE = JSON_CONTENT_TYPE;
    private String response;
    private String errorCode;
    private String contentType = DEFAULT_CONTENT_TYPE;

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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
