package com.lifebook.Service;

import com.lifebook.Model.AppRole;
import com.lifebook.Model.AppUser;
import com.lifebook.Repositories.AppUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Transactional
@Service
public class SSUDS implements UserDetailsService {

    private AppUserRepository userRepository;

    public SSUDS(AppUserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        try {
            AppUser theUser = userRepository.findByUsername(username);
            if (theUser == null)
                throw new UsernameNotFoundException("Unable to find that user");
            return new User(theUser.getUsername(), theUser.getPassword(), theUser.isEnabled(),
                    accountNonExpired, credentialsNonExpired, accountNonLocked, getAuthorities(theUser));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(AppUser user) {
        Set<GrantedAuthority> userAuthorities = new HashSet<>();

        for (AppRole role : user.getRoles()) {
            userAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
        }

        return userAuthorities;
    }

}