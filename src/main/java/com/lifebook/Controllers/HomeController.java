package com.lifebook.Controllers;

import com.cloudinary.utils.ObjectUtils;
import com.lifebook.Model.*;
import com.lifebook.Repositories.*;
import com.lifebook.Service.CloudinaryConfig;
import com.lifebook.Service.EmailService;
import com.lifebook.Service.UserService;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Controller
public class HomeController {
    @Autowired
    AppRoleRepository roles;


    @Autowired
    UserService userService;

    @Autowired
    UserPostRepository posts;

    @Autowired
    SettingRepository settings;

    @Autowired
    AppUserRepository users;

    @Autowired
    CloudinaryConfig cloudc;

    @Autowired
    EmailService emailService;




    @RequestMapping("/")
    public String homePage() {
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registration(Model model){
        model.addAttribute("user", new AppUser());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(
            @Valid @ModelAttribute("user") AppUser user,
            @RequestParam("file") MultipartFile file,
            BindingResult result, Model model, HttpServletRequest request) {
        AppUser userExists = userService.findByEmail(user.getEmail());
        model.addAttribute("user", user);
        if (result.hasErrors()) {
            return "registration";
        } else {
            if (file.isEmpty()) {
                user.setProfilePic("/img/user.png");
                userService.saveUser(user);
                return "redirect:/login";
            }
            else {
                try {
                    Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
                    String uploadedName = (String) uploadResult.get("public_id");

                    String transformedImage = cloudc.createUrl(uploadedName);
                    user.setEnabled(false);
                    user.setConfirmationToken(UUID.randomUUID().toString());
                    user.setProfilePic(transformedImage);
                    userService.saveUser(user);
                    String appUrl = request.getScheme() + "://" + request.getServerName()+":"+request.getLocalPort();
                    senndEmail(user.getEmail(),"Registration Confirmation", "To confirm your e-mail address, please click the link below:\n"
                            + appUrl + "/confirm?token=" + user.getConfirmationToken());



                } catch (IOException e) {
                    e.printStackTrace();
                    return "redirect:/login";
                }
            }
        }
        model.addAttribute("goConfirm","a verification email has been sent via the email you entered , please open your emial and confirm before login");
        return "login";
    }

    // Process confirmation link
    @RequestMapping(value="/confirm", method = RequestMethod.GET)
    public String showConfirmationPage(Model model, @RequestParam("token") String token) {

        AppUser user = userService.findByConfirmationToken(token);

        if (user == null) { // No token found in DB


            model.addAttribute("invalidToken", "Oops!  This is an invalid confirmation link.");
        } else { // Token found
            user.setEnabled(true);
            userService.saveUser(user);
            model.addAttribute("confirmationToken", user.getConfirmationToken());
            model.addAttribute("confirmationMessage","your email has been confirmed , you can now login ");
        }


        return "login";
    }


    @Async
   public void senndEmail(String to, String sub, String body)

    {
        emailService.sendSimpleMessage(to, sub, body);
    }
  @PostConstruct
    public void loadData() {
       AppRole admin = new AppRole();
       admin.setRole("ADMIN");
       roles.save(admin);


       AppRole user = new AppRole();
       user.setRole("USER");
       roles.save(user);

       AppUser adminLogin = new AppUser();
       adminLogin.setUsername("admin");
       adminLogin.setPassword("adminp");
       adminLogin.setEnabled(true);
       adminLogin.setEmail("zelalem.agmasse@gmail.com");
       adminLogin.getRoles().add(admin);
       users.save(adminLogin);




    }

}