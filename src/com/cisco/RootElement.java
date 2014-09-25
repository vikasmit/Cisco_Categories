package com.cisco;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RootElement {
	
	List<Categories> list;

	public List<Categories> getList() {
		return list;
	}

	public void setList(List<Categories> list) {
		this.list = list;
	}
	
	

}
