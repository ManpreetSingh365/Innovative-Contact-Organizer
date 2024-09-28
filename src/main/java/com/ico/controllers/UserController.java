package com.ico.controllers;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ico.entities.User;
import com.ico.forms.UserForm;
import com.ico.helpers.Helper;
import com.ico.helpers.Message;
import com.ico.helpers.MessageType;
import com.ico.services.ImageService;
import com.ico.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
    private ImageService imageService;


	// user dashbaord page

	@RequestMapping(value = "/dashboard")
	public String userDashboard() {
		System.out.println("User dashboard");
		return "user/dashboard";
	}

	// user profile page

	@RequestMapping(value = "/profile")
	public String userProfile(Model model, Authentication authentication) {

		return "user/profile";
	}

	// user add contacts page

	// user view contacts

	// user edit contact
	@RequestMapping(value = "/view/userDetailsUpdateForm")
	public String userProfileEdit(Model model, Authentication authentication) {

		String username = Helper.getEmailOfLoggedInUser(authentication);
		User userData = userService.getUserByEmail(username);

		UserForm userForm = new UserForm();

		userForm.setName(userData.getName());
		userForm.setEmail(userData.getEmail());
		userForm.setPhoneNumber(userData.getPhoneNumber());
		userForm.setAbout(userData.getAbout());

		model.addAttribute("userForm", userForm);

		return "user/profileEdit";
	}

//  Processing for Update_Contact Handler----->
	@PostMapping("/update/userDetailsUpdateForm")
	public String processUpdateUserFormView(@Valid @ModelAttribute("userForm") UserForm userForm,
			BindingResult bindingResult, Model model,
			Authentication authentication, HttpSession session) throws Exception {

		System.out.println("processUpdateUserFormView Handler..........");
		String username = Helper.getEmailOfLoggedInUser(authentication);
		User userData = userService.getUserByEmail(username);

		// update the contact
//		if (bindingResult.hasErrors()) {
//			return "user/profileEdit";
//		}
		
        if (userForm.getUserImage() != null && !userForm.getUserImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(userForm.getUserImage(), filename);
            userData.setProfilePic(fileURL);
            userData.setCloudinaryImagePublicId(filename);
        }
		userData.setName(userForm.getName());
		userData.setEmail(userForm.getEmail());
		userData.setPhoneNumber(userForm.getPhoneNumber());
		userData.setAbout(userForm.getAbout());
		
		var updateUser = userService.updateUser(userData);
		logger.info("Updated User {}", updateUser);


		// Adding Message that Register Successfully :)
		Message message = Message.builder().content("Your Data is Updated Successful :)").type(MessageType.green)
				.build();
		session.setAttribute("message", message);

		return "redirect:/user/profile";
	}

	// user delete contact

}
