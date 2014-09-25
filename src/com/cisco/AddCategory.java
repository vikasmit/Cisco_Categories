package com.cisco;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.inquira.client.serviceclient.IQServiceClient;
import com.inquira.client.serviceclient.IQServiceClientManager;
import com.inquira.client.serviceclient.request.IQCategoryRequest;
import com.inquira.im.ito.CategoryITO;
import com.inquira.im.ito.impl.CategoryITOImpl;


public class AddCategory {

	public static void main(String args[]){
		
		 try
	        {
			 RootElement rootElement=new RootElement();
			 List<Categories> sList=new ArrayList<Categories>();
	         //   FileInputStream file = new FileInputStream(new File("Products_hierarchy_1st_cut_cleaned_up.xlsx"));
			 FileInputStream file = new FileInputStream(new File("product_category_0918_modified.xlsx"));
	           
	            XSSFWorkbook workbook = new XSSFWorkbook(file);
	            XSSFSheet sheet = workbook.getSheetAt(0);
	            Iterator<Row> rowIterator = sheet.iterator();
	            String parentref1="";
				 String parentref2="";
	           
				while (rowIterator.hasNext())
	            {
					 Categories cat1=new Categories();
					 Categories cat2=new Categories();
					 Categories cat3=new Categories();
					 Row row = rowIterator.next();
	                Iterator<Cell> cellIterator = row.cellIterator();
	               
	                if(row.getRowNum()!=0){
		                while (cellIterator.hasNext())
		                	
		                {
		                	
		                    Cell cell = cellIterator.next();
		                   
		                    String rowColNumber=cell.getRowIndex()+"__"+cell.getColumnIndex()+"";
							System.out.println("Reading-->"+rowColNumber+"<Value>"+cell.getStringCellValue());
							
		                    if(cell.getColumnIndex()==0 && cell.getStringCellValue().length()>0)
		                    {
		                    	cat1.setCategoryName(cell.getStringCellValue());
		                    	cat1.setParentKey("");
		                    	if(cell.getStringCellValue().length()>0)
		                    	cat1.setRefrenceKey(cell.getStringCellValue().replaceAll(" ", "_")+"_"+row.getRowNum()+0);
		                    	parentref1=cat1.getRefrenceKey();
		                    	 sList.add(cat1);
		                    }
		                   if(cell.getColumnIndex()==1 && cell.getStringCellValue().length()>0)
		                   {
		                	   cat2.setCategoryName(cell.getStringCellValue());
		                	   cat2.setParentKey(parentref1);
		                	   if(cell.getStringCellValue().length()>0)
		                	   cat2.setRefrenceKey(cell.getStringCellValue().replaceAll(" ", "_")+"_"+row.getRowNum()+1);
		                	   parentref2=cat2.getRefrenceKey();
		                	   sList.add(cat2);
		                   }  
		                   if(cell.getColumnIndex()==2 && cell.getStringCellValue().length()>0){
		                	   cat3.setCategoryName(cell.getStringCellValue());
		                	   cat3.setParentKey(parentref2);
		                	   cat3.setRefrenceKey(cell.getStringCellValue().replaceAll(" ", "_")+"_"+row.getRowNum()+2);
		                	   sList.add(cat3);
		                   }
		                }
	                }
	            }
				rootElement.setList(sList);
				
				for(Categories cat:sList){
					//addToCategory(cat.getParentKey(), cat.getRefrenceKey(), cat.getCategoryName());
				}
				System.out.println("categories added");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	
	public static void addToCategory(String parentKey,String referenceKey,String categoryName){
		IQServiceClient client=null;
		try{
		 client =
	            IQServiceClientManager.connect("admin", "admin", "INQUIRABANK", "INQUIRABANK",
	                "http://ritujain:8226/imws/WebObjects/IMWebServicesNG.woa/ws/RequestProcessor",
	                null
	                , true);
	    IQCategoryRequest categoryRequest= client.getCategoryRequest();
	    CategoryITO categoryITO=new CategoryITOImpl();
	    categoryITO.setReferenceKey(referenceKey);
	    categoryITO.setName(categoryName);
	    if(parentKey.length()>0){
	    	CategoryITO parentITO= new CategoryITOImpl();
	    	parentITO = categoryRequest.getCategoryByReferenceKey(parentKey);
	    	categoryITO.setParent(parentITO);
	    }
	    categoryRequest.addCategory(categoryITO, "en_US");
	    	
		}catch(Exception e){
				e.printStackTrace();
			
		}finally{
			client.close();
		}
		
	}
}
