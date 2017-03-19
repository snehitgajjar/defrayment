package org.app.finance.defrayment.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table
public class User {

	@Id
    @GeneratedValue
	private int userId;
	
	private String name;
	private String State;

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
