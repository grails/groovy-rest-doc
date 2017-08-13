package demo

import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.codehaus.groovy.util.HashCodeHelper
import grails.compiler.GrailsCompileStatic

@SuppressWarnings(['FactoryMethodName', 'Instanceof'])
@GrailsCompileStatic
@ToString(cache=true, includeNames=true, includePackage=false)
class UserRole implements Serializable {

    private static final long serialVersionUID = 1

    User user
    Role securityRole

    @Override
    boolean equals(other) {
        if (other instanceof UserRole) {
            other.userId == user?.id && other.securityRoleId == securityRole?.id
        }
    }

    @Override
    int hashCode() {
        int hashCode = HashCodeHelper.initHash()
        if (user) {
            hashCode = HashCodeHelper.updateHash(hashCode, user.id)
        }
        if (securityRole) {
            hashCode = HashCodeHelper.updateHash(hashCode, securityRole.id)
        }
        hashCode
    }

    static UserRole get(long userId, long securityRoleId) {
        criteriaFor(userId, securityRoleId).get()
    }

    static boolean exists(long userId, long securityRoleId) {
        criteriaFor(userId, securityRoleId).count()
    }

    private static DetachedCriteria criteriaFor(long userId, long securityRoleId) {
        UserRole.where {
            user == User.load(userId) &&
                    securityRole == Role.load(securityRoleId)
        }
    }

    static UserRole create(User user, Role securityRole, boolean flush = false) {
        def instance = new UserRole(user: user, securityRole: securityRole)
        instance.save(flush: flush)
        instance
    }

    static boolean remove(User u, Role r) {
        if (u != null && r != null) {
            UserRole.where { user == u && securityRole == r }.deleteAll()
        }
    }

    static int removeAll(User u) {
        u == null ? 0 : UserRole.where { user == u }.deleteAll() as int
    }

    static int removeAll(Role r) {
        r == null ? 0 : UserRole.where { securityRole == r }.deleteAll() as int
    }

    static constraints = {
        securityRole validator: { Role r, UserRole ur ->
            if (ur.user?.id) {
                UserRole.withNewSession {
                    if (UserRole.exists(ur.user.id, r.id)) {
                        return ['userRole.exists']
                    }
                }
            }
        }
    }

    static mapping = {
        id composite: ['user', 'securityRole']
        version false
    }
}