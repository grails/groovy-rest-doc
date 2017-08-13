package demo

import groovy.transform.CompileStatic
import org.grails.restdoc.HeaderDoc
import org.grails.restdoc.HttpVerb
import org.grails.restdoc.ParamDoc
import org.grails.restdoc.RestEndpoint

@CompileStatic
class ApplicationControllerEndpoint extends RestEndpoint {
    List<String> authorizationRoles = []
    HttpVerb httpVerb = HttpVerb.GET
    String path = '/'
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
