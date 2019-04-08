package ca.uhn.fhir.jpa.starter.custom;

import ca.uhn.fhir.jpa.starter.HapiProperties;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.interceptor.auth.AuthorizationInterceptor;
import ca.uhn.fhir.rest.server.interceptor.auth.IAuthRule;
import ca.uhn.fhir.rest.server.interceptor.auth.RuleBuilder;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Charles Chigoriwa
 */
public class DHIS2AuthInterceptor extends AuthorizationInterceptor {

    @Override
    public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {
        boolean authorized = false;
        String bearertoken = theRequestDetails.getHeader("Authorization");
        if (bearertoken != null) {
            String token = bearertoken.split(" ")[1];
            authorized = checkToken(token);
        }

        if (authorized) {
            return new RuleBuilder()
                    .allowAll()
                    .build();
        } else {
            return new RuleBuilder()
                    .denyAll()
                    .build();
        }

    }

    private boolean checkToken(String token) {
        String bearerAuthorization = GeneralUtility.getBearerAuthorization(token);
        String url = HapiProperties.getCustomDhis2BaseUrl() + "/api/me";
        try {
            DHIS2HttpUtility.httpGet(url, bearerAuthorization);
            return true;
        } catch (UnauthorizedApiException ex) {
            return false;
        } catch (IOException ex) {
            throw new FrismException(ex);
        }

    }

}
