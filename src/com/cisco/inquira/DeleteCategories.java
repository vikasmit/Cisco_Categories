package com.cisco.inquira;

import com.inquira.client.serviceclient.IQServiceClient;
import com.inquira.client.serviceclient.IQServiceClientManager;
import com.inquira.client.serviceclient.request.IQCategoryRequest;
import com.inquira.im.ito.CategoryITO;
import com.inquira.im.ito.impl.CategoryITOImpl;

public class DeleteCategories {
public static void main(String[] args) {
	IQServiceClient client=null;
	try{
	 client =
            IQServiceClientManager.connect("admin", "admin123", "INQUIRABANK", "INQUIRABANK",
                "http://localhost:8226/imws/WebObjects/IMWebServicesNG.woa/ws/RequestProcessor",
                null
                , true);
    IQCategoryRequest categoryRequest= client.getCategoryRequest();
    	CategoryITO parentITO= categoryRequest.getCategoryByReferenceKey("PRODUCTS");
    	if(parentITO!=null)
    	categoryRequest.deleteCategory(parentITO);
    	
	}catch(Exception e){
		e.printStackTrace();
		//System.out.println(e.getStackTrace());
		
	}finally{
		client.close();
	}
}
}
