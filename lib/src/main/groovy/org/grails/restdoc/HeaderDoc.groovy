package org.grails.restdoc

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder

@Builder
@CompileStatic
class HeaderDoc {
    String name
    String value
    String description
}
