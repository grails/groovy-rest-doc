package org.grails.restdoc

import groovy.json.JsonOutput
import groovy.transform.CompileStatic

@CompileStatic
class RequestSample {
    Map headers
    Map params
    String jsonBody

    Map formUrlEncoded

    void formUrlEncoded(Map m) {
        formUrlEncoded = m
    }

    void headers(Map headers) {
        this.headers = headers
    }

    void params(Map params) {
        this.params = params
    }

    void jsonBody(String jsonBody) {
        this.jsonBody = jsonBody
    }

    void jsonMap(Map m) {
        this.jsonBody = JsonOutput.toJson(m)
    }

    String endpointAsCurRequest(RestEndpoint endpoint, String serverUrl) {
        StringBuffer sb = new StringBuffer()
        sb.append('curl ')
        if ( endpoint.httpVerb != HttpVerb.GET ) {
            sb.append("-X \"${endpoint.httpVerb as String}\" ")
        }
        sb.append("\"${serverUrl}${endpoint.path}")
        if ( params ) {
            sb.append('?')
            sb.append(encodedUrl(params))
        }
        sb.append("\"")
        if (headers || jsonBody) {
            sb.append(" \\")
            sb.append("\n")
        }
        sb.toString()
    }

    static String encodedUrl(Map params, String encoding = 'UTF-8') {
        params.collect { k,v -> "${URLEncoder.encode(k as String, encoding)}=${URLEncoder.encode(v as String, encoding)}" }.join('&')
    }

    String asCurl(RestEndpoint endpoint, String serverUrl = 'http://localhost:8080') {
        StringBuffer sb = new StringBuffer()
        sb.append(endpointAsCurRequest(endpoint, serverUrl))

        if ( headers ) {
            def l = []
            for ( String key : headers.keySet() ) {
                l << asCurlHeader(headers, key)
            }
            sb.append(l.join(" \\\n"))
            if ( jsonBody || formUrlEncoded ) {
                sb.append(" \\\n")
            }
        }
        if ( jsonBody ) {
            sb.append(asCurlJson(jsonBody))
        }
        if ( formUrlEncoded ) {
            def formUrlEncodedList = []
            for ( String key : formUrlEncoded.keySet() ) {
                String value = formUrlEncoded[key]
                formUrlEncodedList << "     --data-urlencode \"${key}=${value}\""
            }
            sb.append(formUrlEncodedList.join(" \\\n"))
        }

        sb.toString()
    }

    static String asCurlHeader(Map m, String k) {
        "     -H \"${k}: ${m[k]}\""
    }

    static String asCurlJson(String str) {
        "     -d \$'${JsonOutput.prettyPrint(str)}'"
    }
}
