package com.mindex.utilities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ToDo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String title;
	private String description;
	private LocalDateTime dueDate;
	
	
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = LocalDateTime.parse(dueDate);
    }
}
