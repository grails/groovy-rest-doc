package org.grails.restdoc

import groovy.transform.CompileStatic

@CompileStatic
class RestDoc {
    RestEndpoint restEndpoint

    String generatedSnippetsPath = 'src/docs/asciidoc/generated'
    String h1 = 'API'
    String h2Headers = 'Headers'
    String h2Samples= 'Samples'
    String h2Params = 'Params'
    String thAuthorizationRoles = 'Authorization Roles'

    RestDoc(RestEndpoint restEndpoint) {
        this.restEndpoint = restEndpoint
    }

    RestDoc(String path, String method) {
        this(new SimpleEndpoint(path, method))
    }

    RestSample restSample

    void sample(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = RestSample) Closure cls) {
        restSample = new RestSample()
        cls.resolveStrategy = Closure.DELEGATE_FIRST
        cls.delegate = restSample
        cls()
    }

    void doc(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = RestDoc) Closure cls) {
        cls.resolveStrategy = Closure.DELEGATE_FIRST
        cls.delegate = this
        cls()

        if ( restEndpoint.headers ) {
            createHeadersAsciidocTable(restEndpoint.path, restEndpoint.headers)
        }

        if ( restEndpoint.params ) {
            createUrlParamsAsciidocTable(restEndpoint.path, restEndpoint.params)
        }

        if ( restSample ) {
            createRequestSampleAsciidocSnippet(restEndpoint, restSample)
        }

        createMethodUrlAsciidocTable(restEndpoint.path, (restEndpoint.httpVerb as String).toLowerCase())
    }

    private void createRequestSampleAsciidocSnippet(RestEndpoint endpoint, RestSample restSample) {

        def f = createFile(endpoint.path, "${(restEndpoint.httpVerb as String).toLowerCase()}_sample_$restSample.id")
        String text = ""
        if ( restSample.description ) {
            text += "=== ${restSample.description}\n"
        }
        if ( restSample.request ) {
            text += "\n"
            text += "\n"
            text += "**Request**\n"
            text += "[source, bash]\n"
            text += "----\n"
            text += restSample.request.asCurl(endpoint)
            text += "\n"
            text += "----\n"
        }
        if ( restSample.response ) {
            text += "**Response**\n"
            text += "[source, bash]\n"
            text += "----\n"
            text += restSample.response.asText()
            text += "\n"
            text += "----\n"
        }
        text += "\n"
        f.text = text

    }

    private void createUrlParamsAsciidocTable(String endpoint, List<ParamDoc> apiDocUrlParams) {
        def f = createFile(endpoint, "${(restEndpoint.httpVerb as String).toLowerCase()}_urlparams")
        def text = """[cols="5" options="header"]
|===
| Type
| Name
| Required
| Schema
| Description
"""
        apiDocUrlParams.each {
            text += "\n| $it.type"
            text += "\n| $it.name"
            text += "\n| ${it.required ? 'YES' : 'NO'}"
            text += "\n| $it.schema"
            text += "\n| ${it.description ?: ''} "
        }
        text += '\n|==='
        f.text = text

    }

    private static String endpointPath(String endpoint) {
        endpoint.substring(0, (endpoint.length() -  endpoint.reverse().indexOf('/')))
    }

    private File createFile(String endpoint, String extension) {
        String path = endpointPath(endpoint)
        File folder = new File("${generatedSnippetsPath}${path}".toLowerCase())
        folder.mkdirs()
        File f = new File("${generatedSnippetsPath}${endpoint}_${extension}.adoc".toLowerCase())
        f.createNewFile()
        f
    }

    private static String asciidocFileNameWithoutPathAndSuffix(String endpoint, String method, String suffix) {
        String path = endpointPath(endpoint)
        "${endpoint.replaceAll(path, '')}_${method}_${suffix}"
    }

    private void createHeadersAsciidocTable(String endpoint, List<HeaderDoc> headers) {
        def f = createFile(endpoint, "${(restEndpoint.httpVerb as String).toLowerCase()}_headers")
        def text = """[cols="2" options="header"]
|===
| Header Name
| HeaderValue
"""
        for ( HeaderDoc header : headers) {
            text += "\n| ${header.name}"
            text += "\n| ${header.value}"
        }
        text += '\n|==='
        f.text = text
    }

    private void createMethodUrlAsciidocTable(String endpoint, String method) {
        File f = createFile(endpoint, method)
        String text = """= $endpoint ${method.toUpperCase()} 
\n
[cols="2" options="header"]
|===
| Method
| URL
| ${method.toUpperCase()}
| $endpoint
|===
"""

        if ( restEndpoint.authorizationRoles ) {

            text += """
[cols="1" options="header"]
|===
| ${thAuthorizationRoles}
| ${restEndpoint.authorizationRoles.join(',')}
|===
\n
"""
        }
        if ( restEndpoint.headers ) {
            String headersText = """
== ${h2Headers}
include::${asciidocFileNameWithoutPathAndSuffix(endpoint, method, 'headers')}.adoc[]
"""
            text += headersText
        }
        if ( restEndpoint.params ) {
            String urlParamsText = """
== ${h2Params}
include::${asciidocFileNameWithoutPathAndSuffix(endpoint, method, 'urlparams')}.adoc[]
"""
            text += urlParamsText
        }

        String path = endpointPath(endpoint)
        boolean isHeaderPrinted = false
        File folder = new File("${generatedSnippetsPath}${path}".toLowerCase())
        for ( File file : folder.listFiles() ) {
            String fullPath = "${file.absolutePath}${file.name}"
            if ( fullPath.contains('sample_') && fullPath.contains(endpoint) && fullPath.contains(method)) {
                if ( !isHeaderPrinted ) {
                    text += "== ${h2Samples}\n"
                    isHeaderPrinted = true
                }
                text += "\n"
                text += "include::${file.name}[]"
                text += "\n"
                text += "\n"
                text += "\n"
            }
        }

        f.text = text


        File apiFile = new File("${generatedSnippetsPath}/index.adoc")
        String pathWithoutFile = apiFile.absolutePath.replaceAll('/index.adoc', '')
        if ( !apiFile.exists() ) {
            apiFile.createNewFile()
            apiFile.text += "= ${h1} \n"
            apiFile.text += "\n:leveloffset: +1\n"
            apiFile.text += "\n:leveloffset: -1\n"
        }
        String apiFileText = apiFile.text
        apiFileText = apiFileText.replaceAll("\n:leveloffset: -1\n", '')
        if ( !apiFileText.contains(f.name) ) {
            String filename = f.absolutePath.replaceAll(pathWithoutFile, '').replaceAll("${generatedSnippetsPath}", '')
            filename = removeFirstCharIfEqualsTo(filename, '/' as char)
            apiFileText += "\n\ninclude::${filename}[]\n\n"
        }
        apiFileText += "\n:leveloffset: -1\n"
        apiFile.text = apiFileText

    }

    static String removeFirstCharIfEqualsTo(String str, final char c) {
        if ( str[0] == c ) {
            return str.substring(1)
        }
        str
    }
}