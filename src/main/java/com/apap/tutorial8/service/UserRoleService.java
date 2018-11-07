package com.apap.tutorial8.service;

import com.apap.tutorial8.model.UserRoleModel;

public interface UserRoleService {
	UserRoleModel addUser(UserRoleModel user);
	public String encrypt(String password);
//	Boolean cekPassword(String username, PasswordModel pass);
//	void updatePassword(String username, PasswordModel pass);
}