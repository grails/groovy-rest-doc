package demo

import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import groovy.json.JsonOutput
import org.grails.restdoc.RestDoc
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@Integration
class ApplicationControllerSpec extends Specification {

    @Shared
    ApiLoginEndpoint loginEndpoint = new ApiLoginEndpoint()

    @Shared
    @Subject
    ApplicationControllerEndpoint endpoint = new ApplicationControllerEndpoint()

    def "test getting homepage is secured"() {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        def resp = rest.get("http://localhost:${serverPort}${endpoint.path}") {
            accept(endpoint.accept())
        }

        then:
        resp.status == 401
        resp.json

        when:
        new RestDoc(endpoint).doc {
            sample {
                description '/ is secured'
                request {
                    headers endpoint.headersMap
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
        Map m = [username: 'sherlock', password: 'elementary']
        resp = rest.post("http://localhost:${serverPort}${loginEndpoint.path}") {
            accept(loginEndpoint.accept())
            contentType(loginEndpoint.contentType())
            json JsonOutput.toJson(m)
        }

        then:
        resp.status == 200
        resp.json.roles.find { it == 'ROLE_BOSS' }

        when:
        def accessToken = resp.json.access_token

        then:
        accessToken

        when:
        resp = rest.get("http://localhost:${serverPort}${endpoint.path}") {
            accept(endpoint.accept())
            header('Authorization', "Bearer $accessToken")
        }

        then:
        resp.status == 200
        resp.json

        when:
        new RestDoc(endpoint).doc {
            sample {
                request {
                    headers (endpoint.headersMap + ['Authorization': "Bearer $accessToken"])
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
