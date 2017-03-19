package org.app.finance.defrayment.controller;

import java.util.List;

import javax.persistence.Query;

import org.app.finance.defrayment.dto.User;
import org.app.finance.defrayment.util.HibernateUtil;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping(UserController.USER_BASE_URI)
public class UserController {
	
	public static final String USER_BASE_URI = "api/v1/users";
	
	@RequestMapping(value = "{userId}")                              
	public User getUser(@PathVariable final int userId){
		return new User();
	}
	
	@RequestMapping(method = RequestMethod.POST, headers="Content-Type=application/json")
	public User add(@RequestBody User request){
		Session session = HibernateUtil.getSessionFactory().openSession();
		  
        session.beginTransaction();
 
        User user = new User();
        System.out.println("========"+request.getState()+" : "+ request.getName());
        user.setName(request.getName());
        user.setState(request.getState());
        session.save(user);
 
 
        session.getTransaction().commit();
 
        return user;
	}
}
