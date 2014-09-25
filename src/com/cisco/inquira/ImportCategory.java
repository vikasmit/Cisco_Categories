package com.cisco.inquira;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.inquira.client.serviceclient.IQServiceClient;
import com.inquira.client.serviceclient.IQServiceClientManager;
import com.inquira.client.serviceclient.request.IQCategoryRequest;
import com.inquira.im.ito.CategoryITO;
import com.inquira.im.ito.impl.CategoryITOImpl;

public class ImportCategory{
	public static Properties prop = null;
	public void loadResource(){
		try{
			prop=new Properties();
			InputStream inObjTransformation = getClass().getResourceAsStream("resources.properties");
			prop.load(inObjTransformation);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
			
	}
	
	
	public static void main(String [] args){
		

		try {
			ImportCategory classObj = new ImportCategory();
			 classObj.loadResource();
			
			
			//FileInputStream file = new FileInputStream(new File("Products_hierarchy_1st_cut_cleaned_up.xlsx"));
			 FileInputStream file = new FileInputStream(new File("product_categories_0918.xlsx"));
				
			//System.out.println(file.);
			XSSFWorkbook hssfWorkbook=new XSSFWorkbook(file);
			XSSFSheet hssfSheet=hssfWorkbook.getSheetAt(0);
			Iterator<Row> rowIterator=hssfSheet.iterator();
			DocumentBuilderFactory builderFactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder builder=builderFactory.newDocumentBuilder();
			Document document=builder.newDocument();
			Element rootElement=document.createElement("Category");
			document.appendChild(rootElement);
			Element level1=null;
			Element level2=null;
			Element level3;
			Set<String> level1Set=new HashSet<String>();
			Set<String> level2Set=new HashSet<String>();
			Set<String> level3Set=new HashSet<String>();
			//HashMap<String,List<Categories>> catMap=new HashMap<String,List<Category>>();
			
			while(rowIterator.hasNext()){
				level1Set.clear();
				Row row=rowIterator.next();
				
				Iterator<Cell> cellIterator=row.cellIterator();
				while(cellIterator.hasNext()){
					
					Cell cell=cellIterator.next();
					
					String value=cell.getStringCellValue().toString();
					//System.out.println(value);
					value=value.replaceAll(" ", "_");
				
					if(value.contains("&")){
						value=value.replaceAll("&",prop.getProperty("&") );
					}
					if(value.contains("(")){
						String temp1=value;
						String temp= temp1.substring(temp1.indexOf("("), temp1.indexOf(")")+1);
						temp=temp.substring(temp.indexOf("(")+1);
						temp=temp.substring(0,temp.indexOf(")"));
						value=value.substring(0, value.indexOf("("))+prop.getProperty("(")+temp+prop.getProperty(")");
						
					}
					if(value.contains("\"")){
						value=value.replaceAll("\"",prop.getProperty("\""));
					}
					
					if(value.length()<=0){
						continue;
					}
				String rowColNumber=cell.getRowIndex()+"__"+cell.getColumnIndex()+"";
					System.out.println("Reading-->"+rowColNumber+"<Value>"+cell.getStringCellValue());
					if(cell.getColumnIndex()==0){
						level1=document.createElement("First");
						level1.setAttribute("Category_Name", value);
						//level1.setAttribute("Reference_Key", value+"_"+(int)(Math.floor(Math.random()*100)));
						level1.setAttribute("Reference_Key", value+"_A"+(cell.getRowIndex()+1));
						level1.setAttribute("Parent_key", "PRODUCTS");
						rootElement.appendChild(level1);
					}else if(cell.getColumnIndex()==1){
						level2=document.createElement("Second");
						level2.setAttribute("Category_Name", value);
						level2.setAttribute("Reference_Key",  value+"_B"+(cell.getRowIndex()+1));
						level2.setAttribute("Parent_key",  level1.getAttribute("Reference_Key"));
						level1.appendChild(level2);
					}else if(cell.getColumnIndex()==2){
						level3=document.createElement("Third");
						level3.setAttribute("Category_Name", value);
						level3.setAttribute("Reference_Key",  value+"_C"+(cell.getRowIndex()+1));
						level3.setAttribute("Parent_key",  level2.getAttribute("Reference_Key"));
						level2.appendChild(level3);
					}
				}
			
			}
			
			TransformerFactory factory=TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();  
			DOMSource domSource = new DOMSource(document);  
			StreamResult streamResult = new StreamResult(new File("createFile.xml"));  
			 transformer.transform(domSource, streamResult);  
			 System.out.println("File saved to specified path!"); 
			 
		//	Element element=document.getDocumentElement();
			 NodeList list=document.getElementsByTagName("First");//ChildNodes();
			if(list!=null && list.getLength()>0){
				for(int i=0;i<list.getLength();i++){
					Element e1=(Element)list.item(i);
					String parentKey1=e1.getAttribute("Parent_key");
					String referenceKey1=e1.getAttribute("Reference_Key");
					String CategoryName=e1.getAttribute("Category_Name");
					addToCategory(parentKey1,referenceKey1,CategoryName);
					NodeList list2=e1.getElementsByTagName("Second");
					level2Set.clear();
					if(list2.getLength()>0){
						for(int j=0;j<list2.getLength();j++){
							Element e2=(Element)list2.item(j);
							String parentKey2=e2.getAttribute("Parent_key");
							String referenceKey2=e2.getAttribute("Reference_Key");
							String CategoryName2=e2.getAttribute("Category_Name");
			//				if(!level2Set.contains(CategoryName2)){
								addToCategory(parentKey2,referenceKey2,CategoryName2);
				//			}
							level2Set.add(CategoryName2);
							NodeList list3=e2.getElementsByTagName("Third");
							level3Set.clear();
							if(list3.getLength()>0){
								for(int k=0;k<list3.getLength();k++){
									Element e3=(Element)list3.item(k);
									String parentKey3=e3.getAttribute("Parent_key");
									String referenceKey3=e3.getAttribute("Reference_Key");
									String CategoryName3=e3.getAttribute("Category_Name");
					//				if(!level3Set.contains(CategoryName3)){
										addToCategory(parentKey3,referenceKey3,CategoryName3);
						//			}
									level3Set.add(CategoryName3);
								}
							}
						}
					}
				}
			}
			 
			 
		}catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }catch(Exception e){
			System.out.println("exception"+e.getMessage());
		}
	}
	
	
	
	public static void addToCategory(String parentKey,String referenceKey,String categoryName){
		IQServiceClient client=null;
		try{
		 client =
	            IQServiceClientManager.connect("admin", "admin123", "INQUIRABANK", "INQUIRABANK",
	                "http://localhost:8226/imws/WebObjects/IMWebServicesNG.woa/ws/RequestProcessor",
	                null
	                , true);
	    IQCategoryRequest categoryRequest= client.getCategoryRequest();
	    CategoryITO categoryITO=new CategoryITOImpl();
	   
	    
	    //referenceKey=replaceSpclChars(referenceKey,"referenceKey");
	    //parentKey=replaceSpclChars(parentKey,"parentKey");
	    categoryName=replaceSpclChars(categoryName,"categoryName");
	    
	    categoryITO.setReferenceKey(referenceKey);
	    categoryITO.setName(categoryName);
	    if(parentKey.length()>0){
	    	CategoryITO parentITO= new CategoryITOImpl();
	    	parentITO = categoryRequest.getCategoryByReferenceKey(parentKey);
	    	categoryITO.setParent(parentITO);
	    }
	    	categoryRequest.addCategory(categoryITO, "en_US");
	    	
		}catch(Exception e){
			System.out.println("Error in adding::"+categoryName+"::parentKey::"+parentKey+"::referenceKey::"+referenceKey);
			e.printStackTrace();
			//System.out.println(e.getStackTrace());
			
		}finally{
			client.close();
		}
		
	}
	
	public static String replaceSpclChars(String key,String keyType){
		if(key.contains(prop.getProperty("&"))){
			key=key.replace(prop.getProperty("&"), "&");
		}
	    if(key.contains(prop.getProperty("("))){
	    	key=key.replace(prop.getProperty("("), "(");
	    	key=key.replace(prop.getProperty(")"), ")");
	    }
	    if(key.contains("_") && keyType=="categoryName"){
	    	key=key.replace("_", " ");
	    }
	    if(key.contains(prop.getProperty("\""))){
	    	key=key.replaceAll(prop.getProperty("\""),"\"");
		}
	    return key;
	}
	

}
