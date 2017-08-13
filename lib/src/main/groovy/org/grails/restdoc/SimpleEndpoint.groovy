package org.grails.restdoc

import groovy.transform.CompileStatic

@CompileStatic
class SimpleEndpoint extends RestEndpoint {

    String path
    HttpVerb httpVerb

    SimpleEndpoint(String path, String httpVerb) {
        this.path = path
        this.httpVerb = httpVerb as HttpVerb
    }

    @Override
    List<String> authorizationRoles() {
        return null
    }

    @Override
    HttpVerb httpVerb() {
        httpVerb
    }

    @Override
    String getPath() {
        path
    }

    @Override
    List<HeaderDoc> getHeaders() {
        []
    }

    @Override
    List<ParamDoc> getParams() {
        []
    }
}