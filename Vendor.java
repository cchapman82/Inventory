/*
 *
 *	Basic Inventory App: Vendor
 *	
 *		Object for Vendor
 *			DTO for between user and database
 *
 *		Instance variables
 *			String name
 *			String address
 *			String phone
 *			String email
 *			String contactPerson
 *			String deliveryDay
 *			Double deliveryMin
 *			String deiveryDeadline
 *
 *		methods
 *			getters
 *			setters
 *			vendorToString()			
 *
 *
 * 	Author: CChapman
 * 	Date: 2024-03-06
 * 	Version: 1.0
 *
 * */

public class Vendor {

	private String name;
        private String address;
        private String phone;
        private String email;
        private String contactPerson;
        private String deliveryDay;
        private Double deliveryMin;
        private String deliveryDeadline;

	public String getName() {
		return name;
	}
	public void setName(String n) {
		name = n;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String a) {
		address = a;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String p) {
		phone = p;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String e) {
		email = e;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String cp) {
		contactPerson = cp;
	}
	public String getDeliveryDay() {
		return deliveryDay;
	}
	public void setDeliveryDay(String dd) {
		deliveryDay = dd;
	}
	public Double getDeliveryMin() {
		return deliveryMin;
	}
	public void setDeliveryMin(Double dm) {
		deliveryMin = dm;
	}
	public String getDeliveryDeadline() {
		return deliveryDeadline;
	}
	public void setDeliveryDeadline(String dd) {
		deliveryDeadline = dd;
	}



	public String vendorToString() {
		return getName() + ",(" +  getAddress() + ")," + getPhone() + "," +
		      	getEmail() + "," +  getContactPerson() + "," + getDeliveryDay() + 
			"," + String.valueOf(getDeliveryMin()) + "," + getDeliveryDeadline(); 
	}


}
