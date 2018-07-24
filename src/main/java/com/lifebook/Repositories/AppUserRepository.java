package com.lifebook.Repositories;

import com.lifebook.Model.AppUser;
import org.springframework.data.repository.CrudRepository;

public interface AppUserRepository extends CrudRepository<AppUser,Long>{
    AppUser findByUsername(String username);
    AppUser findAppUserByUsername(String username);
    AppUser findByConfirmationToken(String confirmationToken);
    AppUser findByEmail(String email);

}
