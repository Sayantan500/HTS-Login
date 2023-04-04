package helpdesk_ticketing_system.authentication.login;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import helpdesk_ticketing_system.authentication.service.Login;
import helpdesk_ticketing_system.authentication.service.UsernamePasswordAuth;
import helpdesk_ticketing_system.authentication.utility.CognitoClient;

import java.util.Map;

public class LoginHandler implements RequestHandler<Map<String,String>,Object>
{
    private final CognitoClient cognitoClient;
    private final Login usernamePasswordLoginAuth;
    public LoginHandler(){
        this.cognitoClient = new CognitoClient();
        this.usernamePasswordLoginAuth = new UsernamePasswordAuth();
    }

    @Override
    public Object handleRequest(Map<String, String> inputCredentials, Context context) {
        return usernamePasswordLoginAuth.signIn(
                cognitoClient,
                inputCredentials.get("username"),
                inputCredentials.get("password"),
                context
        );
    }
}
