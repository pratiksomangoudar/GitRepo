package com.pratik.hciproject.common;

import java.io.Serializable;


import android.net.Uri;

public class ContactDetail implements Serializable {
private String name;
private int color;
private String number;
private String id;
private String imageURI;

public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

public String getNumber() {
	return number;
}
public void setNumber(String number) {
	this.number = number;
}
public int getColor() {
	return color;
}
public void setColor(int color) {
	this.color = color;
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}
public String getImageURI() {
	return imageURI;
}
public void setImageURI(String imageURI) {
	this.imageURI = imageURI;
}



}
