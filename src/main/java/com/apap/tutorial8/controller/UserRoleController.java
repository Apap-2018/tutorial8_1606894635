package com.apap.tutorial8.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.apap.tutorial8.model.PasswordModel;
import com.apap.tutorial8.model.UserRoleModel;
import com.apap.tutorial8.repository.UserRoleDb;
import com.apap.tutorial8.service.UserRoleService;
import com.mysql.cj.protocol.Message;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;

	@Autowired
	private UserRoleDb userRoleDb;

	@RequestMapping( value = "/addUser", method = RequestMethod.POST)
	private String addUserSubmit(@ModelAttribute UserRoleModel user, Model model) {
		String messages = "";
		if (validatePassword(user.getPassword())) {
			messages = "User berhasil ditambahkan";
			userService.addUser(user);
		}
		else {
			messages = "Password yang anda masukkan salah. Password harus memiliki panjang digit 8 dan harus mengandung 1 nomor serta mengandung huruf besar";
		}
		model.addAttribute("message", messages);
		return "home";
	}

	public boolean validatePassword(String password) {
		if (password.length()>=8 && Pattern.compile("[0-9]").matcher(password).find() &&  Pattern.compile("[a-zA-Z]").matcher(password).find())  {
			return true;
		}
		else {
			return false;
		}
	}

	@RequestMapping( value = "/changePassword", method = RequestMethod.POST)
	private String addUserSubmit(Principal principal, @ModelAttribute PasswordModel pass, Model model) {
		UserRoleModel changedUser = userRoleDb.findByUsername(principal.getName());

		PasswordEncoder token = new BCryptPasswordEncoder();

		Boolean passMatch = token.matches(pass.getPasswordLama(), changedUser.getPassword());
		Boolean confirmMatch = pass.getPasswordBaru().equals(pass.getPasswordKonfirmasi());
		Boolean confirmValid = pass.getPasswordBaru().equals(pass.getPasswordKonfirmasi());
		if (passMatch && confirmMatch && confirmValid) {

			UserRoleModel user = new UserRoleModel();
			user.setId(changedUser.getId());
			user.setUsername(changedUser.getUsername());
			user.setRole(changedUser.getRole());
			user.setPassword(pass.getPasswordBaru());
			userService.addUser(user);
			model.addAttribute("success", "Success Change Password!");
		}
		else {
			List<String> messages = new ArrayList<String>();
			if (!passMatch) {
				messages.add("Password Wrong");
			}
			if (!confirmMatch) {
				messages.add("Password Doesnt Match");
			}
			if (!confirmValid) {
				messages.add("Password Format not Valid");
			}
			model.addAttribute("error",messages);
		}
		return "home";
	}
}