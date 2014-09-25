package com.cisco.inquira;

import javax.xml.bind.annotation.XmlElement;


public class Categories {
	
	String refrenceKey;
	String parentKey;
	String categoryName;
	//Categories categories;
	
	public String getRefrenceKey() {
		return refrenceKey;
	}
	
	 @XmlElement
	public void setRefrenceKey(String refrenceKey) {
		this.refrenceKey = refrenceKey;
	}
	public String getParentKey() {
		return parentKey;
	}
	
	 @XmlElement
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}
	public String getCategoryName() {
		return categoryName;
	}
	
	 @XmlElement
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/*public Categories getCategories() {
		return categories;
	}

	 @XmlElement
	public void setCategories(Categories categories) {
		this.categories = categories;
	}*/
	
	 
	

}
