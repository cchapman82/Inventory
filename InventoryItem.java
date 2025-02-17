

/*	Basic Inventory App: Inventory Item
 *
 *		Object for items in inventory
 *			DTO for between user and database
 *
 *		instance variables
 *			String name
 *			String type
 *			Double cost
 *			String vendor
 *			int size
 *			int par
 *
 *		methods
 *			getters
 *			setters
 *			itemToString()
 *
 * 	Author: CChapman
 * 	Date: 2024-03-06
 * 	Version: 1.0
 *
 *
 * */


public class InventoryItem {

	private String name;
	private String type;
	private Double cost;
	private String vendor;
	private String current;
	private int size;
	private int par;


	public InventoryItem() {

	}



	//getters and setters
	public void setName(String n) {
		name = n;
	}
	public String getName() {
		return name;
	}
	public void setType(String t) {
		type = t;
	}
	public String getType() {
		return type;
	}
	public void setCost(Double c) {
		cost = c;
	}
	public Double getCost() {
		return cost;
	}
	public void setVendor(String v) {
		vendor = v;
	}
	public String getVendor() {
		return vendor;
	}
	public void setCurrent(String c) {
		current = c;
	}
	public String getCurrent() {
		return current;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int s) {
		size = s;
	}
	public int getPar() {
		return par;
	}
	public void setPar(int p) {
		par = p;
	}




	//to String
	public String itemToString() {
		return getName() + "," + getType() + "," + 
			String.valueOf(getCost()) + "," +
			getVendor() + "," + getSize() + "," + getPar();
	}
}
