package com.lifebook.Controllers;

import com.lifebook.Model.AppUser;
import com.lifebook.Model.UserPost;
import com.lifebook.Repositories.AppUserRepository;
import com.lifebook.Repositories.UserPostRepository;
import com.lifebook.Service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AppUserRepository users;

    @Autowired
	NewsService newsService;

    @Autowired
    UserPostRepository posts;

    @RequestMapping("/")
    public String homePageAdmin(Model model) {
		model.addAttribute("articles", newsService.defaultArticles());
        return "index";
    }

	@RequestMapping("/allmessages")
	public String messages(Model model, Authentication authentication) {
		model.addAttribute("posts", posts.findAllByOrderByIdDesc());

		model.addAttribute("currentuser", users.findByUsername(authentication.getName()));
		return "results";
	}

    @RequestMapping("/delete/{id}")
    public String deleteMessage (@PathVariable("id") long id) {
        posts.deleteById(id);
        return "redirect:/allmessages";
    }

	@RequestMapping("/displayusers")
	public String showAllUsers(Model model, Authentication auth) {
		model.addAttribute("currentUser",users.findAppUserByUsername(auth.getName()));
		model.addAttribute("users", users.findAll());
		return "displayusers";
	}

	@RequestMapping("/availability/{id}")
	public String suspend(@PathVariable("id") long id, Model model) {
		AppUser user = users.findById(id).get();
		user.setEnabled(!user.isEnabled());
		users.save(user);
		return "redirect:/admin/displayusers";
	}
}