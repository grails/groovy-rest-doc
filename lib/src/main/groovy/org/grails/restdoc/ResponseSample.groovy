package org.grails.restdoc

import groovy.json.JsonException
import groovy.json.JsonOutput
import groovy.transform.CompileStatic

@CompileStatic
class ResponseSample {
    String payload
    int statusCode
    Map headers

    void headers(Map headers) {
        this.headers = headers
    }

    void statusCode(int statusCode) {
        this.statusCode = statusCode
    }

    void text(String text) {
        this.payload = text
    }

    void json(String json) {
        this.payload = json
    }

    String asText() {
        StringBuffer sb = new StringBuffer()
        sb.append("${statusCode}\n")
        if ( headers ) {
            for ( String key : headers.keySet() ) {
                sb.append("${key}: ${headers[key]}\n")
            }
        }
        sb.append("\n")
        if ( payload ) {
            try {
                sb.append(JsonOutput.prettyPrint(payload))
            } catch(JsonException e ){
                sb.append(payload)
            }

        }
        sb.toString()
    }
}
