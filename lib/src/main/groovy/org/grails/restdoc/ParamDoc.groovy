package org.grails.restdoc

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder

@Builder
@CompileStatic
class ParamDoc {
    String name
    boolean required
    String schema
    String description
    ParamType type
}
