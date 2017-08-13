package org.grails.restdoc

import org.grails.restdoc.RestDoc
import spock.lang.Specification

class RestDocSpec extends Specification {

    def "test removeFirstCharIfEqualsTo"(String str, String expected) {
        expect:
        expected == RestDoc.removeFirstCharIfEqualsTo(str, '/' as char)

        where:
        str             | expected
        '/bean'         | 'bean'
        '/bean/index'   | 'bean/index'
        'bean/index'    | 'bean/index'
    }
}
