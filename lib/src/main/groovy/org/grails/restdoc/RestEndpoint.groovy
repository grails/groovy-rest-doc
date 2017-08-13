package org.grails.restdoc

import groovy.transform.CompileStatic

@CompileStatic
abstract class RestEndpoint {

    abstract List<String> getAuthorizationRoles()

    abstract HttpVerb getHttpVerb()

    abstract String getPath()

    abstract List<HeaderDoc> getHeaders()

    abstract List<ParamDoc> getParams()

    Map<String, Object> getHeadersMap() {
        Map m = [:]
        if ( headers ) {
            for ( HeaderDoc headerDoc : headers ) {
                m[headerDoc.name] = headerDoc.value
            }
        }
        m
    }

    String accept() {
        Map m = headersMap
        if ( m.containsKey('Accept') ) {
            return m['Accept']
        }
        null
    }

    String contentType() {
        Map m = headersMap
        if (m.containsKey('Content-Type')) {
            return m['Content-Type']
        }
        null
    }
}
