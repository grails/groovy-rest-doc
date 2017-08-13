package org.grails.restdoc

import groovy.transform.CompileStatic

@CompileStatic
enum ParamType {
    URL, JSON, FORM_URL_ENCODED
}