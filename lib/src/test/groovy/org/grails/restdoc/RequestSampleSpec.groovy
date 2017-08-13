package org.grails.restdoc

import spock.lang.Specification

class RequestSampleSpec extends Specification {

    def "test asCurl"() {
        when:
        RequestSample sample = new RequestSample()
        sample.headers(['Accept': 'application/json', 'Content-Type': 'application/json'])
        sample.jsonBody('{"username": "admin", "password": "admin" }')
        String curl = sample.asCurl(new SimpleEndpoint('/api/login', 'POST'))
        String expected = """curl -X \"POST\" "http://localhost:8080/api/login" \\
     -H \"Accept: application/json" \\
     -H \"Content-Type: application/json" \\
     -d \$'{
    "username": "admin",
    "password": "admin"
}'"""
        then:
        curl == expected
    }

    def "test asCurl with params"() {
        when:
        RequestSample sample = new RequestSample()
        sample.params([offset: 10, max: 30])
        String curl = sample.asCurl(new SimpleEndpoint('/api/books', 'GET'))
        String expected = "curl \"http://localhost:8080/api/books?offset=10&max=30\""

        then:
        curl == expected
    }


    def "test application/x-www-form-urlencoded"() {
        when:
        RequestSample sample = new RequestSample()
        sample.formUrlEncoded(['grant_type': 'refresh_token', 'refresh_token': 'eyJhbGciOiJIUzI1NiJ9.eyJwcmluY2lwYWwiOiJINHNJQUFBQUFBQUFBSlZTUFVcL2NRQlI4dmh5Q0NJbVBTSW1VQXBwQUZcL21rcEx3cWgwZ2taSEVJY3cxSW9EMzdZUmJXdTJaM0RYY051aW9wS0loSWtKQm9LZmtuU1pNZkVJV0NscG8yYncySER4ckVWdmJiOGN5OEdWOWN3NURSOERIUmpBdmpaeUpQdVBSTnBybE1ERWE1NXJicjV3WjFqTFpBZkNtQUxackE3ZkVxNEFWUTRiR0ZWOEUyMjJNMXdXUlNhN2EzTWJMMWpvWVBTaWQzakp1YXBiaXY5STVcL3p4MHBqUThFU21ydnZBTERxekRKb2tqbDBpNHFPZFwvSnVNWjRGU2JLV2FDaUhUZDZIZEVOU3N1Wk1JUFFZWlNzTFRBT1lKVGxka3VSS2tkallmeldiRzY1cUlWbzZ3R01aTXdZY3Zkb2s5QTY2KzdlMlpTMHdTNGNRTFdUZVhRb3Uxa0g5UjJQUDZlRW9LMjVrbWFtSlZNVjgwM3V4SW1cL04zWDg1K2lzMTZvQVVDYnZuXC82bW5MOXRRT1wvWCtzMTBFYlFYV1hnellMMkUxVHNadVprc21WYzBPdVdcL3Awc1wvVHE2XC9yYjBnWllmNFwvUHcrWmo3ZEpkZWRVMm5HTkxOcW9DT2kzYSs2WnlKdlBFM2ViNkhyaHp6TkJOSWZKUzNHOXhJbE1hMWIxVXIwODdid2Nya1p6RzgwbW1IbzNrYk1GbXBCOVpQdVdMRzI2OHNQRkxWMWVQWDk5OUc3ZjhTeEFFTjdUT1JJcVUrVW9NVThiYVArZW5FeU5mcno4ckRZb2Y4XC9cL3dmNlQ5M3RFd01BQUE9PSIsInN1YiI6InNoZXJsb2NrIiwicm9sZXMiOlsiUk9MRV9CT1NTIl0sImlhdCI6MTUwMjYxMTMyNn0.KXoT6xLhm7uZwRrVU4dJ6tI1wXI7mUv4T5HWR2nsjAg'])
        sample.headers(['Accept': 'application/json', 'Content-Type': 'application/x-www-form-urlencoded'])

        String expected = """curl -X \"POST\" \"http://localhost:8080/oauth/access_token\" \\
     -H \"Accept: application/json\" \\
     -H "Content-Type: application/x-www-form-urlencoded\" \\
     --data-urlencode \"grant_type=refresh_token\" \\
     --data-urlencode \"refresh_token=eyJhbGciOiJIUzI1NiJ9.eyJwcmluY2lwYWwiOiJINHNJQUFBQUFBQUFBSlZTUFVcL2NRQlI4dmh5Q0NJbVBTSW1VQXBwQUZcL21rcEx3cWgwZ2taSEVJY3cxSW9EMzdZUmJXdTJaM0RYY051aW9wS0loSWtKQm9LZmtuU1pNZkVJV0NscG8yYncySER4ckVWdmJiOGN5OEdWOWN3NURSOERIUmpBdmpaeUpQdVBSTnBybE1ERWE1NXJicjV3WjFqTFpBZkNtQUxackE3ZkVxNEFWUTRiR0ZWOEUyMjJNMXdXUlNhN2EzTWJMMWpvWVBTaWQzakp1YXBiaXY5STVcL3p4MHBqUThFU21ydnZBTERxekRKb2tqbDBpNHFPZFwvSnVNWjRGU2JLV2FDaUhUZDZIZEVOU3N1Wk1JUFFZWlNzTFRBT1lKVGxka3VSS2tkallmeldiRzY1cUlWbzZ3R01aTXdZY3Zkb2s5QTY2KzdlMlpTMHdTNGNRTFdUZVhRb3Uxa0g5UjJQUDZlRW9LMjVrbWFtSlZNVjgwM3V4SW1cL04zWDg1K2lzMTZvQVVDYnZuXC82bW5MOXRRT1wvWCtzMTBFYlFYV1hnellMMkUxVHNadVprc21WYzBPdVdcL3Awc1wvVHE2XC9yYjBnWllmNFwvUHcrWmo3ZEpkZWRVMm5HTkxOcW9DT2kzYSs2WnlKdlBFM2ViNkhyaHp6TkJOSWZKUzNHOXhJbE1hMWIxVXIwODdid2Nya1p6RzgwbW1IbzNrYk1GbXBCOVpQdVdMRzI2OHNQRkxWMWVQWDk5OUc3ZjhTeEFFTjdUT1JJcVUrVW9NVThiYVArZW5FeU5mcno4ckRZb2Y4XC9cL3dmNlQ5M3RFd01BQUE9PSIsInN1YiI6InNoZXJsb2NrIiwicm9sZXMiOlsiUk9MRV9CT1NTIl0sImlhdCI6MTUwMjYxMTMyNn0.KXoT6xLhm7uZwRrVU4dJ6tI1wXI7mUv4T5HWR2nsjAg\""""

        String curl = sample.asCurl(new SimpleEndpoint('/oauth/access_token', 'POST'))

        then:
        curl == expected
    }
}