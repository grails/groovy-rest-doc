package demo

import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import groovy.json.JsonOutput
import org.grails.restdoc.RestDoc
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@Integration
class OauthAccessTokenSpec extends Specification {

    @Shared
    ApiLoginEndpoint loginEndpoint = new ApiLoginEndpoint()

    @Shared
    @Subject
    OauthAccessTokenEndpoint endpoint = new OauthAccessTokenEndpoint()

    def "test /oauth/access_token"() {
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
        String refreshToken = resp.json.refresh_token

        then:
        accessToken
        refreshToken

        when:
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
        form.add("grant_type", 'refresh_token')
        form.add("refresh_token", refreshToken)
        resp = rest.post("http://localhost:${serverPort}${endpoint.path}") {
            accept(endpoint.accept())
            contentType(endpoint.contentType())
            body form
        }

        then:
        resp.status == 200
        resp.json
        resp.json.access_token
        resp.json.refresh_token

        when:
        new RestDoc(endpoint).doc {
            sample {
                description 'Successful access token refresh'
                request {
                    headers endpoint.headersMap
                    formUrlEncoded form.toSingleValueMap()
                }
                response {
                    statusCode resp.status
                    json resp.json as String
                }
            }
        }

        then:
        noExceptionThrown()

        when:
        form = new LinkedMultiValueMap<String, String>()
        form.add("grant_type", 'refresh_token')
        form.add("refresh_token", "wrong refresh token")
        resp = rest.post("http://localhost:${serverPort}${endpoint.path}") {
            accept(endpoint.accept())
            contentType(endpoint.contentType())
            body form
        }

        then:
        resp.status == 403

        when:
        new RestDoc(endpoint).doc {
            sample {
                description 'Return 401 if the supplied accessToken is not valid'
                request {
                    headers endpoint.headersMap
                    formUrlEncoded form.toSingleValueMap()
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
