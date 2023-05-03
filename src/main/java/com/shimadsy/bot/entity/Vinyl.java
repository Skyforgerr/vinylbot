package com.shimadsy.bot.entity;

import jakarta.persistence.*;

/**
 *
 * @author Ivan
 */

@Entity
public class Vinyl {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String name;
    private String description;
    private int cost;
    private int year;
    private String lable;
    
    public Vinyl(){
        
    }
    
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public int getCost(){
        return cost;
    }
    public int getYear(){
        return year;
    }
    public String getLable(){
        return lable;
    }
    
    public void setId(int id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setCost(int cost){
        this.cost = cost;
    }
    public void setYear(int year){
        this.year = year;
    }
    public void setLable(String lable){
        this.lable = lable;
    }
}
