package demo

import groovy.transform.CompileStatic
import org.grails.restdoc.HeaderDoc
import org.grails.restdoc.HttpVerb
import org.grails.restdoc.ParamDoc
import org.grails.restdoc.ParamType
import org.grails.restdoc.RestEndpoint

@CompileStatic
class ApiLoginEndpoint extends RestEndpoint {
    List<String> authorizationRoles = []
    HttpVerb httpVerb = HttpVerb.POST
    String path = '/api/login'
    List<HeaderDoc> headers = [
                HeaderDoc.builder()
                        .name('Accept')
                        .value('application/json')
                        .build(),
                HeaderDoc.builder()
                        .name('Content-Type')
                        .value('application/json')
                        .build()
        ]

    List<ParamDoc> params = [
                ParamDoc.builder()
                        .name('username')
                        .type(ParamType.JSON)
                        .required(true)
                        .schema(String.simpleName)
                        .build(),

                ParamDoc.builder()
                        .name('password')
                        .type(ParamType.JSON)
                        .required(true)
                        .schema(String.simpleName)
                        .description('Plain text password')
                        .build(),
        ]
}
