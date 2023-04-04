package helpdesk_ticketing_system.authentication.entities;

public class Response
{
    private final Integer status;
    private final String message;
    private final String sessionToken;
    private final String accessToken;
    private final Boolean isFirstTimeLoggingIn;

    public Response(Integer status, String message, String sessionToken, String accessToken, Boolean isFirstTimeLoggingIn) {
        this.status = status;
        this.message = message;
        this.sessionToken = sessionToken;
        this.accessToken = accessToken;
        this.isFirstTimeLoggingIn = isFirstTimeLoggingIn;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Boolean getFirstTimeLoggingIn() {
        return isFirstTimeLoggingIn;
    }

    //For Dev
    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", isFirstTimeLoggingIn=" + isFirstTimeLoggingIn +
                '}';
    }
}
