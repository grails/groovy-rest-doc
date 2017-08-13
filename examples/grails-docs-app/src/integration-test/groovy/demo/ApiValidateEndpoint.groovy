package demo

import org.grails.restdoc.HeaderDoc
import org.grails.restdoc.HttpVerb
import org.grails.restdoc.ParamDoc
import org.grails.restdoc.RestEndpoint

class ApiValidateEndpoint extends RestEndpoint {
    List<String> authorizationRoles = []
    HttpVerb httpVerb = HttpVerb.POST
    String path = '/api/validate'
    List<HeaderDoc> headers = [
                HeaderDoc.builder()
                        .name('Accept')
                        .value('application/json')
                        .build(),
                HeaderDoc.builder()
                        .name('Authorization')
                        .value('Bearer XXXXXXX')
                        .build(),
        ]

    List<ParamDoc> params = []
}
