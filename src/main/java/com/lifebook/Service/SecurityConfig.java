package com.lifebook.Service;



import com.lifebook.Repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AppUserRepository userRepository;

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new SSUDS(userRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/register", "/confirm", "/resendconfirmation", "/error").permitAll()
                .antMatchers("/css/**", "/js/**","/img/**").permitAll()
                .antMatchers("/users/**").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/h2/**", "/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/users/", true).permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout").permitAll().permitAll();

        http.csrf().disable();

        http.headers().frameOptions().disable();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceBean()).passwordEncoder(new BCryptPasswordEncoder());
    }

    /*@Bean
    public WebMvcConfigurer myWebMvcConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                ViewControllerRegistration r = registry.addViewController("/login");
                r.setViewName("login");

                ViewControllerRegistration denied = registry.addViewController("/403");
                denied .setViewName("403");

            }
        };
    }
*/
}
