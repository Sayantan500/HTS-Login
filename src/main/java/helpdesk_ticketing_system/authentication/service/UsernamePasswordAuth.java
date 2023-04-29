package helpdesk_ticketing_system.authentication.service;

import com.amazonaws.services.lambda.runtime.Context;
import helpdesk_ticketing_system.authentication.entities.Response;
import helpdesk_ticketing_system.authentication.utility.CognitoClient;
import helpdesk_ticketing_system.authentication.utility.HashingUtils;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class UsernamePasswordAuth implements Login{
    private final String userPoolId;

    public UsernamePasswordAuth() {
        this.userPoolId = System.getenv("aws_cognito_userPoolId");
    }

    @Override
    public Response signIn(CognitoClient cognitoClient, String username, String password, Context context) {
        Response signInResponse = null;
        Map<String, String> authParameters = new LinkedHashMap<>();
        authParameters.put("USERNAME", username);
        authParameters.put("PASSWORD", password);

        authParameters.put("SECRET_HASH", HashingUtils.computeSecretHash(
                cognitoClient.getClientID(),
                cognitoClient.getClientSecret(),
                username
        ));

        AdminInitiateAuthRequest adminInitiateAuthRequest =
                AdminInitiateAuthRequest.builder()
                        .clientId(cognitoClient.getClientID())
                        .userPoolId(userPoolId)
                        .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                        .authParameters(authParameters)
                        .build();

        try {
            AdminInitiateAuthResponse adminInitiateAuthResponse =
                    cognitoClient.getCognitoIdentityProviderClient().adminInitiateAuth(adminInitiateAuthRequest);

            ChallengeNameType challengeName = adminInitiateAuthResponse.challengeName();
            // user is not signing in for the first
            if (challengeName == null) {
                signInResponse = new Response(
                        adminInitiateAuthResponse.sdkHttpResponse().statusCode(),
                        "Sign in successful",
                        null,
                        adminInitiateAuthResponse.authenticationResult().accessToken(),
                        adminInitiateAuthResponse.authenticationResult().idToken(),
                        Boolean.FALSE

                );
            }

            // user is signing in for the first time
            else if (challengeName.equals(ChallengeNameType.NEW_PASSWORD_REQUIRED)) {
                signInResponse = new Response(
                        adminInitiateAuthResponse.sdkHttpResponse().statusCode(),
                        "Sign in successful, But Password Must Be Changed Before Using Any Feature",
                        adminInitiateAuthResponse.session(),
                        null,
                        null,
                        Boolean.TRUE
                );
            }
        }
        catch (NotAuthorizedException exception) {
            // some other error may have occurred
            context.getLogger().log("[ Exception occurred while signing in ] " + exception.getMessage());
            signInResponse = new Response(
                    HttpStatusCode.UNAUTHORIZED,
                    "Incorrect username and/or password",
                    null,
                    null,
                    null,
                    null
            );
        }

        return signInResponse;
    }
}
