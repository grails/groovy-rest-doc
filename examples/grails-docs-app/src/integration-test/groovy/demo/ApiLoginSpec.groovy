package demo

import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import groovy.json.JsonOutput
import org.grails.restdoc.RestDoc
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@Integration
class ApiLoginSpec extends Specification {

    @Shared
    @Subject
    ApiLoginEndpoint endpoint = new ApiLoginEndpoint()

    def "test login"() {
        given:
        RestBuilder rest = new RestBuilder()

        when: 'login with valid credentials'
        Map m = [username: 'sherlock', password: 'elementary']
        def resp = rest.post("http://localhost:${serverPort}${endpoint.path}") {
            accept(endpoint.accept())
            contentType(endpoint.contentType())
            json JsonOutput.toJson(m)
        }

        then: 'server returns access token and roles'
        resp.status == 200
        resp.json.roles.find { it == 'ROLE_BOSS' }
        resp.json.access_token
        resp.json.refresh_token

        when:
        def accessToken = resp.json.access_token

        then:
        accessToken

        when:
        new RestDoc(endpoint).doc {
            sample {
                description 'Successful login'
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

        when: 'login with wrong password'
        m = [username: 'sherlock', password: 'wrongpassword']
        resp = rest.post("http://localhost:${serverPort}${endpoint.path}") {
            accept(endpoint.accept())
            contentType(endpoint.contentType())
            json JsonOutput.toJson(m)
        }

        then: 'server returns unauthorized'
        resp.status == 401

        when:
        new RestDoc(endpoint).doc {
            sample {
                description 'login with wrong password, server returns unauthorized'
                request {
                    headers endpoint.headersMap
                    jsonBody JsonOutput.toJson(m)
                }
                response {
                    statusCode resp.status
                }
            }
        }

        then:
        noExceptionThrown()
    }

}
