package helpdesk_ticketing_system.authentication.service;

import com.amazonaws.services.lambda.runtime.Context;
import helpdesk_ticketing_system.authentication.entities.Response;
import helpdesk_ticketing_system.authentication.utility.CognitoClient;

public interface Login {
    Response signIn(CognitoClient cognitoClient, String username, String password, Context context);
}
