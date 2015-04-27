package com.aliyun.sts.demo.sts;

/**
 * @author ding.lid
 */
public class StsException extends RuntimeException {
    private static final long serialVersionUID = -397928272870884639L;

    private final String errorCode;
    private final String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public StsException(String message, String errorCode, String errorMessage, Throwable cause) {
        super(createMessage(message, errorCode, errorMessage, cause), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    private static String createMessage(String message, String errorCode, String errorMessage, Throwable cause) {
        StringBuilder stringBuilder = new StringBuilder();
        if (message != null && !message.isEmpty()) {
            stringBuilder.append(message);
        }
        if (errorCode != null && !errorCode.isEmpty()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("code: ").append(errorCode);
        }
        if (errorMessage != null && !errorMessage.isEmpty()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("message: ").append(errorMessage);
        }
        if (cause != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("cause: ").append(cause.getMessage());
        }
        return stringBuilder.toString();
    }
}
