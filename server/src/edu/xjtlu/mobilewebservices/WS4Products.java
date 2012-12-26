package edu.xjtlu.mobilewebservices;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.Connection;

public class WS4Products {
	Connection connect;
	ArrayList<String> aList = new ArrayList<String>();;
	ResultSet rSet,rSet1,rSet2;
	Statement statment;
    String st;
	
	public String productsQuery(String search_item) throws SQLException, Exception, IllegalAccessException, ClassNotFoundException{
		try{  
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/fyp","root","123456");
		 
		statment = connect.createStatement();
			rSet = statment.executeQuery("SELECT * FROM products WHERE barcode='"+search_item+"'");
			    while(rSet.next()){
	                 String item1 = rSet.getString("shop_id");
	                 String item2 = rSet.getString("product_price");
	                aList.add(item1);
                    aList.add(item2);
            }   
		    
		    rSet = statment.executeQuery("SELECT * FROM products WHERE product_name='"+search_item+"'");
            while(rSet.next()){
                 String item1 = rSet.getString("shop_id");
                 String item2 = rSet.getString("product_price");
                 aList.add(item1);
                 aList.add(item2);
         } 		    
			
		}catch(Exception e){
			 e.printStackTrace();
		 } 
		 connect.close();
		 if(aList.isEmpty()){
			 aList.add("Sorry, can not find anything!");
		 }
		 
		 int count = aList.size();
		 String result="";
		 for(int i=0;i<count;i++){
		     result=result+aList.get(i)+";";
		 }
		return result;	
	}
}
