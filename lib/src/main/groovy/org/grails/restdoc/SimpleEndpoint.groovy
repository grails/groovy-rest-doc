package org.grails.restdoc

import groovy.transform.CompileStatic

@CompileStatic
class SimpleEndpoint extends RestEndpoint {
    String path
    HttpVerb httpVerb
    List<HeaderDoc> headers = []
    List<String> authorizationRoles = []
    List<ParamDoc> params = []

    SimpleEndpoint(String path, String httpVerb) {
        this.path = path
        this.httpVerb = httpVerb as HttpVerb
    }
}