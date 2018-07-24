package com.lifebook.Controllers;

import com.cloudinary.utils.ObjectUtils;
import com.lifebook.Model.AppUser;
import com.lifebook.Model.Interest;
import com.lifebook.Model.UserPost;
import com.lifebook.Repositories.*;
import com.lifebook.Service.CloudinaryConfig;
import com.lifebook.Service.FollowingService;
import com.lifebook.Service.NewsService;
import com.lifebook.Service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    AppRoleRepository roles;

    @Autowired
    FollowingService followingService;

    @Autowired
    UserPostRepository posts;

    @Autowired
    InterestRepository interests;

    @Autowired
    AppUserRepository users;

    @Autowired
    CloudinaryConfig cloudc;

    @Autowired
    WeatherService weatherService;

    @Autowired
    NewsService newsService;

    @RequestMapping("/")
    public String homePageLoggedIn(Authentication authentication, Model model) {

        if (users.findByUsername(authentication.getName()).getRoles().contains(roles.findByRole("ADMIN"))) {
            return "redirect:/admin/";
        }
        else {

            model.addAttribute("articles", newsService.personalized(authentication));
            return "index";
        }
    }

    @PostMapping("/newmessage")
    public String sendMessage(@ModelAttribute("post") UserPost post,
                              @RequestParam("file") MultipartFile file, Authentication authentication) {
        post.setCreator(users.findByUsername(authentication.getName()));
        if (!file.isEmpty()) {
            try {
                Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
                String uploadedName = (String) uploadResult.get("public_id");

                String transformedImage = cloudc.createUrl(uploadedName);
                post.setImageUrl(transformedImage);

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/users/profile";
            }
        }

        Date today = new Date();

        post.setDateOfPost(today.toString());
        posts.save(post);

        return "redirect:/users/profile";
    }

    @RequestMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        AppUser user = users.findByUsername(authentication.getName());

        model.addAttribute("currentuser", user);
        UserPost post = new UserPost();
        model.addAttribute("post",post);

        model.addAttribute("weatherFirstDay",weatherService.fetchForcast(user.getZipCode(),7).getForecast().getForecastday().get(0).getDay().getCondition().getText()) ;
        model.addAttribute("weatherSecondDay",weatherService.fetchForcast(user.getZipCode(),7).getForecast().getForecastday().get(1).getDay().getCondition().getText()) ;
        model.addAttribute("weatherThirdDay",weatherService.fetchForcast(user.getZipCode(),7).getForecast().getForecastday().get(2).getDay().getCondition().getText()) ;
        model.addAttribute("weatherFourthDay",weatherService.fetchForcast(user.getZipCode(),7).getForecast().getForecastday().get(3).getDay().getCondition().getText()) ;
        model.addAttribute("weatherFifthDay",weatherService.fetchForcast(user.getZipCode(),7).getForecast().getForecastday().get(4).getDay().getCondition().getText()) ;
        model.addAttribute("weatherSixthDay",weatherService.fetchForcast(user.getZipCode(),7).getForecast().getForecastday().get(5).getDay().getCondition().getText()) ;
        model.addAttribute("weatherSeventhDay",weatherService.fetchForcast(user.getZipCode(),7).getForecast().getForecastday().get(6).getDay().getCondition().getText()) ;

        AppUser sessionUser =users.findByUsername(authentication.getName());
            Set<AppUser> following = sessionUser.getFollowing();
            List<UserPost> posts = new ArrayList<>(sessionUser.getPosts());
            for (AppUser u: following) {
                posts.addAll(u.getPosts());
            }
          Collections.reverse(posts);

            model.addAttribute("posts", posts.toArray());
        return "profile";
    }

    @RequestMapping("/following")
    public String displayFolloing(Model model, Authentication authentication) {
        AppUser sessionUser =users.findByUsername(authentication.getName());
        model.addAttribute("currentuser", sessionUser);
        Set<AppUser> following = sessionUser.getFollowing();
        Set<UserPost> posts = new HashSet<>();
        for (AppUser u: following) {
            posts.addAll(u.getPosts());
        }
        model.addAttribute("posts", posts);
        return "following";
    }

    @RequestMapping("/follow/{id}")
    public String follow (@PathVariable("id") long id, Authentication auth) {

        AppUser detail = users.findById(id).get();
        AppUser sessionUser = users.findByUsername(auth.getName());
        if(sessionUser.getFollowing().contains(detail)){
            detail.setMyFriend(true);
        }

        sessionUser.getFollowing().add(detail);
        sessionUser.setNoOfFriends(sessionUser.getFollowing().size());
        users.save(sessionUser);
        return "redirect:/users/profile";
    }
    @RequestMapping("/unfollow/{id}")
    public String unfollow (@PathVariable("id") long id, Authentication auth) {

        AppUser detail = users.findById(id).get();
        AppUser sessionUser = users.findByUsername(auth.getName());
        detail.setMyFriend(false);
        sessionUser.getFollowing().remove(detail);
        sessionUser.setNoOfFriends(sessionUser.getFollowing().size());
        users.save(sessionUser);
        return "redirect:/users/profile";
    }

    @RequestMapping("/weather")
    public String weather() {
        return "weather";
    }

    @RequestMapping("/news")
    public String news() {
        return "news";
    }

    @RequestMapping("/findpost")
    public String showResults(HttpServletRequest request, Model model) {
        model.addAttribute("posts", posts.findAllByContentContainingIgnoreCase(request.getParameter("query")));
        return "results";
    }
}