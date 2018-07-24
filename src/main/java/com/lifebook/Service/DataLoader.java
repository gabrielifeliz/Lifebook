/*
package com.lifebook.Service;

import com.lifebook.Model.AppRole;
import com.lifebook.Model.AppUser;
import com.lifebook.Repositories.AppRoleRepository;
import com.lifebook.Repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class DataLoader implements CommandLineRunner {

    @Autowired
    AppUserRepository users;

    @Autowired
    AppRoleRepository roles;

    @Override
    public void run(String... args) throws Exception {

        AppRole r = new AppRole();
        r.setRole("USER");
        roles.save(r);

        AppRole a=new AppRole();
        a.setRole("ADMIN");
        roles.save(a);

        AppUser u = new AppUser();
        u.setUsername("u");
        u.setPassword("password");
        u.setEmail("user@user.edu");
        u.setFullName("John Doe");
        u.getRoles().add(r);
        users.save (u);

        u = new AppUser();
        u.setUsername("user");
        u.setPassword("password");
        u.setEmail("user@u.come");
        u.setFullName("Jane Doe");
        u.getRoles().add(r);
        users.save (u);

        u = new AppUser();
        u.setUsername("userthree");
        u.setPassword("password");
        u.setEmail("u@u.come");
        u.setFullName("Jim Doe");
        u.getRoles().add(r);
        users.save (u);


        u = new AppUser();
        u.setUsername("adminone");
        u.setPassword("password");
        u.setEmail("a@a.com");
        u.setFullName("Jim Admin");
        u.getRoles().add(a);
        users.save (u);

        u = new AppUser();
        u.setUsername("admintwo");
        u.setPassword("password");
        u.setEmail("a@a.net");
        u.setFullName("Jane Admin");
        u.getRoles().add(a);
        users.save (u);
    }
}
*/
