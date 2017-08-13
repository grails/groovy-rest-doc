package org.grails.restdoc

import groovy.transform.CompileStatic

@CompileStatic
class RestSample {
    RequestSample request
    ResponseSample response
    String description

    void description(String description) {
        this.description = description
    }
    void request(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = RequestSample) Closure cls) {
        request = new RequestSample()
        cls.resolveStrategy = Closure.DELEGATE_FIRST
        cls.delegate = request
        cls()
    }

    void response(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ResponseSample) Closure cls) {
        response = new ResponseSample()
        cls.resolveStrategy = Closure.DELEGATE_FIRST
        cls.delegate = response
        cls()
    }

    String getId() {
        UUID.randomUUID().toString().toLowerCase()
    }
}
