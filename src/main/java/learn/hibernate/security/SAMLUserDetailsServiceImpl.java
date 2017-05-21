package learn.hibernate.security;

import static java.util.Collections.singletonList;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

/**
 * @author Sergii Stets
 *         Date 20.05.2017
 */
@Service
public class SAMLUserDetailsServiceImpl implements SAMLUserDetailsService {

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        // here user is already authenticated by SSO
        String username = credential.getNameID().getValue();

        List<SimpleGrantedAuthority> authorities = singletonList(new SimpleGrantedAuthority(Roles.USER.getRoleName()));

        return new User(username, "notarealpassword", true, true, true, true, authorities);
    }
}
