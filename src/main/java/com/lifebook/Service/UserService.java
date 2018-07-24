package com.lifebook.Service;

import com.lifebook.Model.AppRole;
import com.lifebook.Model.AppUser;
import com.lifebook.Repositories.AppRoleRepository;
import com.lifebook.Repositories.AppUserRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {


    @Autowired
    AppUserRepository users;

    @Autowired
    AppRoleRepository roles;

    @Autowired
    public UserService(AppUserRepository users) {
        this.users = users;
    }

    public AppUser findByUsername(String username) {
        return users.findByUsername(username);
    }

    public void save(AppUser user) {
        users.save(user);
    }

    public void saveUser(AppUser user) {

        user.getRoles().add(roles.findByRole("USER"));
        users.save(user);
    }

    public AppUser findByEmail(String email) {
        return users.findByEmail(email);
    }

    public AppUser findByConfirmationToken(String confirmationToken) {
        return users.findByConfirmationToken(confirmationToken);
    }

}
