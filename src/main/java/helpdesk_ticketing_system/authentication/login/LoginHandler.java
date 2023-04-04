package helpdesk_ticketing_system.authentication.login;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import helpdesk_ticketing_system.authentication.entities.Response;

import java.util.Map;

public class LoginHandler implements RequestHandler<Map<String,String>,Object>
{

    @Override
    public Object handleRequest(Map<String, String> inputCredentials, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log(inputCredentials.toString());

        Response response = new Response(
                200,
                "OK",
                "session_token",
                "access_token",
                Boolean.FALSE
        );

        return response;
    }
}
