package com.lifebook.Controllers;

import com.cloudinary.utils.ObjectUtils;
import com.lifebook.Model.*;
import com.lifebook.Repositories.*;
import com.lifebook.Service.CloudinaryConfig;
import com.lifebook.Service.EmailService;
import com.lifebook.Service.NewsService;
import com.lifebook.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
public class HomeController {
    @Autowired
    AppRoleRepository roles;

    @Autowired
    NewsService newsService;

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

    @Autowired
    InterestRepository interests;

    @RequestMapping("/")
    public String homePage(Model model) {
        model.addAttribute("articles", newsService.articlesByCategory("Technology"));
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registration(Model model){
        model.addAttribute("user", new AppUser());
        model.addAttribute("interests", interests.findAll());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(
            @Valid @ModelAttribute("user") AppUser user,
            BindingResult result, HttpServletRequest request, Model model) {
        model.addAttribute("user", user);

        System.out.println(result.getAllErrors().toString());

        if (result.hasErrors()) {
            return "registration";
        }

        if (users.findByEmail(user.getEmail()) != null) {
            model.addAttribute("existingemail",
                    "There is an account with that email: " + user.getEmail());
            return "registration";
        }

        if (users.findByUsername(user.getUsername()) != null) {
            model.addAttribute("existingusername",
                    "There is an account with that username: " + user.getUsername());
            return "registration";
        }

        // Disable user until they click on confirmation link in email
        user.setEnabled(false);
        // Generate random 36-character string token for confirmation link
        user.setConfirmationToken(UUID.randomUUID().toString());

        userService.save(user);

        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort();
        try {
            emailService.sendEmailMessage(user.getEmail(), "Registration Confirmation",
                    "To confirm your e-mail address, please click the link below:\n"
                            + appUrl + "/confirm?token=" + user.getConfirmationToken());
        } catch (MessagingException e) {
            model.addAttribute("errorMessage",
                    "Failed to send confirmation email.");
            return "registration";
        }
        model.addAttribute("confirmationMessage",
                "A confirmation e-mail has been sent to " + user.getEmail());
        return "registration";
    }


    @GetMapping("/resendconfirmation")
    public String resendConfirmationForm() {
        return "resendconfirmation";
    }

    @PostMapping("/resendconfirmation")
    public String resendConfirmation(Model model, @RequestParam("email") String email,
                                     HttpServletRequest request) {

        AppUser user = userService.findByEmail(email);

        if (user == null) {
            model.addAttribute("nonexistingemail",
                    "There is no account with that email: " + email);
        } else {
            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort();
            try {
                emailService.sendEmailMessage(user.getEmail(), "Registration Confirmation",
                        "To confirm your e-mail address, please click the link below:\n"
                                + appUrl + "/confirm?token=" + user.getConfirmationToken());
            } catch (MessagingException e) {
                model.addAttribute("errorMessage",
                        "Failed to send confirmation email. Try again.");
                return "redirect:/resendconfirmation";
            }
            model.addAttribute("confirmationMessage",
                    "A confirmation e-mail has been sent to " + user.getEmail());
        }

        return "resendconfirmation";
    }

    @GetMapping("/confirm")
    public String confirmRegistration(Model model, @RequestParam("token") String token) {

        AppUser user = userService.findByConfirmationToken(token);

        if (user == null) { // No token found in DB
            model.addAttribute("invalidToken", "Oops!  This is an invalid confirmation link.");
        } else { // Token found
            model.addAttribute("confirmationToken", user.getConfirmationToken());
        }

        return "confirm";
    }

    @PostMapping("/confirm")
    public String confirmRegistration(Model model,
                                      @RequestParam Map<String, String> requestParams,
                                      @RequestParam("file") MultipartFile file) {

        // Find the user associated with the reset token
        AppUser user = userService.findByConfirmationToken(requestParams.get("token"));

        String passwordInput = requestParams.get("password");
        String userZipCode = requestParams.get("zipcode");
        // Set new password
        if (passwordInput.isEmpty()) {
            model.addAttribute("errorMessage", "Please provide a valid password");
            return "redirect:/confirm?token="+ user.getConfirmationToken();
        } else if (passwordInput.length() < 4) {
            model.addAttribute("errorMessage", "Please provide a strong password");
            return "redirect:/confirm?token="+ user.getConfirmationToken();
        }

        if (userZipCode.isEmpty()) {
            model.addAttribute("errorMessage", "Please provide a valid zip code");
            return "redirect:/confirm?token="+ user.getConfirmationToken();
        }

        if (!file.isEmpty()) {
            try {
                Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
                String uploadedName = (String) uploadResult.get("public_id");

                String transformedImage = cloudc.createUrl(uploadedName);

                user.setProfilePic(transformedImage);

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/login";
            }
        } else {
            user.setProfilePic("/img/user.png");
        }

        user.setPassword(new BCryptPasswordEncoder().encode(passwordInput));

        user.setZipCode(userZipCode);

        // Set user to enabled
        user.setEnabled(true);

        // Save user
        userService.saveUser(user);

        model.addAttribute("successPasswordMessage", "Your password has been set!");
        model.addAttribute("successMessage", "Your account has been activated!");


        return "confirm";
    }

  @PostConstruct
    public void loadData() {
       AppRole admin = new AppRole();
       admin.setRole("ADMIN");
       roles.save(admin);


       AppRole user = new AppRole();
       user.setRole("USER");
       roles.save(user);

      Interest interest;
      List<String> interestList = new ArrayList<>(Arrays
              .asList("business", "entertainment", "general", "health", "science",
                      "sports", "technology"));

      for (String s: interestList) {
          interest = new Interest();
          interest.setName(s);
          interests.save(interest);
      }

      AppUser adminLogin;
      for (int l = 1; l <= 2; l++) {
          adminLogin = new AppUser();
          adminLogin.setFirstName("Admin");
          adminLogin.setLastName((l == 1) ? "One" : "Two");
          adminLogin.setUsername("admin" + l);
          adminLogin.setPassword(new BCryptPasswordEncoder().encode("adminp" + l));
          adminLogin.setEnabled(true);
          adminLogin.setProfilePic("/img/user.png");
          adminLogin.setEmail("admin" + l +"@gmail.com");
          adminLogin.getRoles().add(admin);
          users.save(adminLogin);
      }

      AppUser usersList;
      for (int l = 1; l <= 3; l++) {
          usersList = new AppUser();
          usersList.setFirstName("User");
          usersList.setLastName(l == 1 ? "One" : l == 2 ? "Two" : "Three");
          usersList.setUsername("user" + l);
          usersList.setPassword(new BCryptPasswordEncoder().encode("userp" + l));
          usersList.setEnabled(true);
          usersList.setEmail("email@email"+ l +".com");
          usersList.setProfilePic("/img/user.png");
          usersList.getRoles().add(user);
          for (Interest interests: interests.findAll()){
              usersList.getInterests().add(interests);
          }
          users.save(usersList);
      }

    }

}