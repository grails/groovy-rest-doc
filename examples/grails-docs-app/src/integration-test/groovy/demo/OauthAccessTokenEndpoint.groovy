package demo

import org.grails.restdoc.HeaderDoc
import org.grails.restdoc.HttpVerb
import org.grails.restdoc.ParamDoc
import org.grails.restdoc.ParamType
import org.grails.restdoc.RestEndpoint
import groovy.transform.CompileStatic

@CompileStatic
class OauthAccessTokenEndpoint extends RestEndpoint {
    List<String> authorizationRoles = []
    HttpVerb httpVerb = HttpVerb.POST
    String path = '/oauth/access_token'
    List<ParamDoc> params = [
            ParamDoc.builder()
                    .name('grant_type')
                    .schema(String.simpleName)
                    .required(true)
                    .type(ParamType.FORM_URL_ENCODED)
                    .build(),
            ParamDoc.builder()
                    .name('refresh_token')
                    .schema(String.simpleName)
                    .required(true)
                    .type(ParamType.FORM_URL_ENCODED)
                    .build(),
    ]

    List<HeaderDoc> headers = [
                HeaderDoc.builder()
                        .name('Accept')
                        .value('application/json')
                        .build(),
                HeaderDoc.builder()
                        .name('Content-Type')
                        .value('application/x-www-form-urlencoded')
                        .build()
    ]
}
