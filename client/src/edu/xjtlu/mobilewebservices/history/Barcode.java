package edu.xjtlu.mobilewebservices.history;

public class Barcode {
	int _id;
	String _name;
	String _barcode_number;
	
	//empty constructor
	public Barcode(){
	
	}
	
	//constructor
	public Barcode(String name,String _barcode_name){
		this._name =name;
		this._barcode_number = _barcode_name;
	}
	
	//constructor
	public Barcode(int id,String name,String _barcode_name){
		this._id =id;
		this._name =name;
		this._barcode_number = _barcode_name;
	}
	
	//getting id
	public int getID(){
		return this._id;
	}
	
	//setting id
	public void setID(int id){
		this._id = id;
	}
	
	//getting name
	public String getName(){
		return this._name;
	}
	
	//setting name
	public void setname(String name){
		this._name = name;
	}
	
	//getting barcode name
	public String getBarcodeNumber(){
		return this._barcode_number;
	}

	//setting barcode name
	public void setBarcodeNumber(String barcode_name){
		this._barcode_number = barcode_name;
	}
}
