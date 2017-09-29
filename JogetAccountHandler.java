/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics.helper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.joget.apps.app.service.AppUtil;
import org.joget.directory.dao.UserDao;
import org.joget.directory.model.Group;
import org.joget.directory.model.Role;
import org.joget.directory.model.User;
import org.springframework.beans.BeansException;

/**
 *
 * @author syeda
 */
public class JogetAccountHandler {
    
    public boolean createAccount(String userName, String password, String firstName, String lastName, String email, String groupId){
        boolean result = true;
        try{
            UserDao ud = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
            User user = new User();
            ud.getUser(userName);
            user.setId(userName);
            user.setUsername(userName); // set all the required values here like username, password, first name, last name etc
            user.setPassword(password);
            user.setConfirmPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setActive(1);
            HashSet setRole = new HashSet();
            Role role = new Role();
            role.setId("ROLE_USER");
            setRole.add(role);
            user.setRoles(setRole);
            HashSet setGroup = new HashSet();
            Group group = new Group();
            group.setId(groupId);
            setGroup.add(group);
            user.setGroups(setGroup);
            ud.addUser(user);
        }catch(BeansException e){
            System.out.println(e.getMessage());
            result = false;
        }
        return result;
    }
    
    public void updateUserGroup(String userName, String groupID){
        UserDao ud = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
        User user = ud.getUser(userName);
        if(user != null){
            HashSet setGroup = new HashSet();
            Group group = new Group();
            group.setId(groupID);
            setGroup.add(group);
            user.setGroups(setGroup);
            ud.updateUser(user);
        }
    }
    
    public boolean addToGroup(String userName, String groupID){
        UserDao ud = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
        return ud.assignUserToGroup(userName, groupID);
    }
    
    public boolean removeFromGroup(String userName, String groupID) {
        UserDao ud = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
        return ud.unassignUserFromGroup(userName, groupID);
    }
    
    public boolean removeUser(String userName) {
        UserDao ud = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
        return ud.deleteUser(userName);
    }
}
