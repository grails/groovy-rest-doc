package demo

import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import groovy.json.JsonOutput
import org.grails.restdoc.RestDoc
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@Integration
class ApiValidateSpec extends Specification {

    @Shared
    ApiLoginEndpoint loginEndpoint = new ApiLoginEndpoint()

    @Shared
    @Subject
    ApiValidateEndpoint endpoint = new ApiValidateEndpoint()

    def "test /api/validate"() {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        Map m = [username: 'sherlock', password: 'elementary']
        def resp = rest.post("http://localhost:${serverPort}${loginEndpoint.path}") {
            accept(loginEndpoint.accept())
            contentType(loginEndpoint.contentType())
            json JsonOutput.toJson(m)
        }

        then:
        resp.status == 200
        resp.json.roles.find { it == 'ROLE_BOSS' }

        when:
        String accessToken = resp.json.access_token

        then:
        accessToken

        when:
        resp = rest.get("http://localhost:${serverPort}${endpoint.path}") {
            accept(endpoint.accept())
            auth('Bearer wrong_access_token')
            json JsonOutput.toJson(m)
        }

        then:
        resp.status == 401

        when:
        new RestDoc(endpoint).doc {
            sample {
                description 'Return 401 if the user supplies a wrong accessToken'
                request {
                    headers endpoint.headersMap
                    jsonBody JsonOutput.toJson(m)
                }
                response {
                    statusCode resp.status
                    json resp.json.toString()
                }
            }
        }

        then:
        noExceptionThrown()


        when:
        resp = rest.get("http://localhost:${serverPort}${endpoint.path}") {
            accept(endpoint.accept())
            auth("Bearer $accessToken")
            json JsonOutput.toJson(m)
        }

        then:
        resp.status == 200

        when:
        Map sampleHeaders = endpoint.headersMap
        sampleHeaders['Authorization'] = "Bearer $accessToken"
        new RestDoc(endpoint).doc {
            sample {
                description 'Endpoint returns 200 if the access token is valid'
                request {
                    headers sampleHeaders
                    jsonBody JsonOutput.toJson(m)
                }
                response {
                    statusCode resp.status
                    json resp.json.toString()
                }
            }
        }

        then:
        noExceptionThrown()
    }
}
