package helpdesk_ticketing_system.authentication.login;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import helpdesk_ticketing_system.authentication.entities.RequestBody;
import helpdesk_ticketing_system.authentication.entities.Response;
import helpdesk_ticketing_system.authentication.service.Login;
import helpdesk_ticketing_system.authentication.service.UsernamePasswordAuth;
import helpdesk_ticketing_system.authentication.utility.CognitoClient;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
{
    private final Gson gson;
    private final CognitoClient cognitoClient;
    private final Login usernamePasswordLoginAuth;
    public LoginHandler(){
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.cognitoClient = new CognitoClient();
        this.usernamePasswordLoginAuth = new UsernamePasswordAuth();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayInput, Context context) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json; charset=utf-8");
        if(apiGatewayInput==null)
            return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);

        String requestBodyJsonString = apiGatewayInput.getBody();
        try{
            RequestBody requestBody = gson.fromJson(requestBodyJsonString, RequestBody.class);

            String username = requestBody.getUsername();
            String password = requestBody.getPassword();

            //both username and password values are present as expected
            if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password))
            {
                // username or password has some whitespaces in between
                if(username.contains(" ") || password.contains(" "))
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(HttpStatusCode.BAD_REQUEST)
                            .withHeaders(headers)
                            .withBody("Username or password must not contain any whitespaces in between.");

                Response response = usernamePasswordLoginAuth.signIn(cognitoClient, username, password, context);

                //correct username or password
                if(response.getStatus()==HttpStatusCode.OK)
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(HttpStatusCode.OK)
                            .withHeaders(headers)
                            .withBody(gson.toJson(response));

                //incorrect username or password
                else if(response.getStatus()==HttpStatusCode.UNAUTHORIZED)
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(HttpStatusCode.UNAUTHORIZED)
                            .withHeaders(headers)
                            .withBody(gson.toJson(response));
            }

            //username and/or password are not present
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatusCode.BAD_REQUEST)
                    .withHeaders(headers)
                    .withBody("Username or Password cannot be Empty.");
        }
        catch (JsonSyntaxException e){ //malformed request body
            context.getLogger().log("[ Error occurred while parsing ] " + e.getMessage() + "\n");
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatusCode.BAD_REQUEST)
                    .withHeaders(headers)
                    .withBody("Malformed Request Body");
        }
        catch (Exception e ){ //any other errors occurs
            context.getLogger().log("[ Error occurred ] " + e.getMessage() + "\n");
        }
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .withHeaders(headers);
    }
}
