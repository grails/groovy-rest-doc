package demo

class BootStrap {

    def init = { servletContext ->
        def authorities = ['ROLE_BOSS', 'ROLE_EMPLOYEE']
        authorities.each { String authority ->
            if ( !Role.findByAuthority(authority) ) {
                new Role(authority: authority).save()
            }
        }

        if ( !User.findByUsername('sherlock') ) {
            def u = new User(username: 'sherlock', password: 'elementary')
            u.save()
            new UserRole(user: u, securityRole: Role.findByAuthority('ROLE_BOSS')).save()
        }

        if ( !User.findByUsername('watson') ) {
            def u = new User(username: 'watson', password: '221Bbakerstreet')
            u.save()
            new UserRole(user: u, securityRole: Role.findByAuthority('ROLE_EMPLOYEE')).save()
        }
    }
    def destroy = {
    }
}
