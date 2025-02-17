

/*	Basic Inventory App: User Interface
 *		global variables
 *			String[] inventoryItems
 *			Scanner in
 *			LocalDate currDate
 *			Database Controller dc
 * 		
 *		action options for user 
 *			Enter into current inventory in quick or regular mode
 *			Enter into master inventory
 *			Make items for inventory, venor
 *			Update current inventory
 *			Update master inventory
 *			print reports
 *			close inventory by checking for missed items and making sure items are
 *			 	in master inventory
 *
 * 		methods
 * 			makeItem(String name)
 * 				-> dc.insertItem(name)
 * 				-> dc.insertCurrInventory(name)
 * 			makeVendor(String name)
 * 				-> dc.insertVendor(name)
 * 			sendAmount(String name)
 * 				-> dc.updateAmount(name, amount, flag, date)
 * 			cleanInvenory()
 * 				-> dc.getNullItems()
 * 				-> dc.updateAmount(name, amount, flag, date)
 * 				-> dc.setCurrInvAvail(name)
 * 				-> dc.upateItem(name, field, value)
 * 				-> dc.getMissingItems()
 * 			updateCurrAmount()
 * 				-> dc.updateAmount(name, amount, flag, date)
 * 			updateItem()
 * 				-> dc.updateItem(name, field, value)
 * 			updateVendor()
 * 				-> dc.updateVendor(name, field, value)
 * 			printMenu()
 * 				-> InventoryReports.writeMasterInventory(type)
 * 				-> dc.checkDate(date)
 * 				-> dc.getInventories()
 * 				-> InventoryReports.writeInventory(date)
 * 				-> InventoryReports.writeInventoryRange(startDate, endDate)
 * 			getNumberInput()
 * 			getDoubleInput()
 * 			getDateInput(int i)
 * 			getDNInput(String s)
 *
 * 	Author: CChapman
 * 	Date: 2024-03-05
 * 	Version: 1.0
 *
 *
 * */


import java.util.Scanner;
import java.time.LocalDate;



public class InventoryApp {


	private static String[] inventoryItems;
	private static Scanner in = new Scanner(System.in).useDelimiter("\n");
	private static LocalDate currDate = LocalDate.now();

	public static DatabaseController dc = DatabaseController.getInstance();



	public static void main(String[] args) {
		// User Interface
		System.out.println(currDate.toString() );
		System.out.println( " Simple Inventory App ");

		//populate list of items
		inventoryItems = dc.getItems();
		//get date of last inventory
		String lastInventory = dc.getLastInventory();
		System.out.println("Date of last inventory worked on: " + lastInventory);

		//continue with app function until ended by user
		Boolean control = true;
		int num = 0;
		int action;
		int quick = 0;
		while (control) {
			if (num == 0) {
				//initial user menu
				System.out.println("Please enter action : " +	
					"\n1 to enter new inventory under current date" +
					"\n  	or 99 to work on a previous inventory" +
					"\n2 to update current date inventory amount" +
					"\n3 to update item in master inventory" +
				        "\n4 to add new vendor" +
					"\n5 to update vendor" +	
					"\n6 for print menu" +
					"\n7 to add item to master inventory" +
					"\n0 to quit");
				if (quick == 0) {
					System.out.println("\n\nActions are for REGULAR inventory" +
							"\n98 for quick inventory");
				} else {
					System.out.println("\n\nActions are for QUICK inventory" +
							"\n97 for regular functions");
				}
				action = getNumberInput();
			} else {
				action = 1;
			}
			switch (action) { 
				case 1:
					//create new inventory column in database current date
					dc.newInventory(currDate.toString());
					Boolean amntControl = true;
					while (amntControl) {
						System.out.println(" Please enter the name " +
							       "of the item or done to come back " +
							       " or close to finish and check");
						String name =in.next().toLowerCase().replace(" ","_");
						if (name.equals("done")) {
							amntControl = false;
							num = 0;
						} else if (name.equals("close")) {
						       cleanInventory();
					       		amntControl = false;
					 		num = 0; 		
						} else {
							String names = "";
							int cont = 1;
							for (String s : inventoryItems) {
								if (s.equals(name)) {
									sendAmount(name);
									cont = 0;
								} else if (!s.equals("") && 
										(s.contains(name) ||
										name.contains(s))) {
									names += s + ",";
								} 
							}
						       	if (cont == 1) {
							   if (quick == 0) {	
								System.out.println(name + " was not" +
									" found in inventory, would" +
								      	" you like to search(1)" +
								      	" or start over (0)");
								int choice = getNumberInput();
								if (choice == 0) {
									continue;
								} else if (choice == 1)  {
									if (!names.isEmpty()) {
										String[] ss = 
											names.
											split(",");
										System.out.
											println("Did"+
											"you mean: ");
										for (int i = 0; 
										  i < ss.length; i++){
											System.out.
											 println(i + 1 												+ "\t " +	 											ss[i]);
										}
										System.out.
										 println(
										"Please enter" +
										"number of selection"+
									       " or 0 " +
										"if not present");
										int option = 
										   getNumberInput();
										if (option != 0) {
											if (!dc.
											isCurrent(
											ss[option-1]))
											{
											dc.updateItem
											(ss[option-1],
											 "current", 
											 "y");
											dc.
										       setCurrInvAvail
										       (ss[option-1], 
											1);
											}
											sendAmount
										       (ss[option-1]);
										} else {
										     makeItem(name);
									             sendAmount(name);
										}
									} else {
										makeItem(name);
										sendAmount(name);
									}
								}
							   } else {
								sendAmount(name);
							   }
							}
						}
					}
					break;
				case 2:
					//update item in current date inventory
					updateCurrAmount();
					break;
				case 3:
					//update item in master inventory
					updateItem();
					break;
				case 4:
					//make vendor
					System.out.println(" Please enter the name " +
					       "of the vendor to add");
					String name =in.next().toLowerCase().replace(" ","_");
					makeVendor(name);
					break;
				case 5:
					//update vendor
					updateVendor();
					break;
				case 6:
					printMenu();
					break;
				case 7:
					// Enter item into master inventory
					System.out.println("Please enter name of Item");
					makeItem(in.next().toLowerCase().replace(" ", "_"));
					break;
				case 97:
					// change to regular inventory
					quick = 0;
					break;
				case 98:
					// change to quick inventory
					quick = 1;
					break;
				case 99:
					// change date of inventory being worked on
					System.out.println("Please enter date to work on " +
							"in the format YYYY-MM-DD");
					System.out.println("Would you like to enter date yourself(0) "
						       + " or guided(1)");
					String date = getDateInput(getNumberInput());
					num = dc.checkDate(date);
					if (num == 1) {
						currDate = LocalDate.parse(date);
						System.out.println("Date set to: " + currDate.toString());
					} else {
						String[] inventories = dc.getInventories();
						System.out.println("Please entery number of inventory");
						for (int i = 0; i < inventories.length; i++) {
							if (i % 2 == 1) {
								System.out.print(i + "\t" + inventories[i] + "\t");
							} else {
								System.out.print(i + "\t" + inventories[i] + "\n");
							}
						}
						int index = getNumberInput();
						currDate = LocalDate.parse(inventories[index]);
						System.out.println("Date set to: " + currDate.toString());
					}
					num = 1;
					break;
				case 0:
					// close program
					control = false;
					break;
				default:
					System.out.println("Please enter viable option from above menu");
					break;
			}
		}
	}

	


	//get item information from user, update list and add to database
	private static void makeItem(String name) {
		InventoryItem item = new InventoryItem();
		System.out.println("Please enter the information for " + name.replace("_", " "));
		item.setName(name);
		System.out.println("Please enter the type of the inventory item");
		item.setType(in.next().toLowerCase());
		System.out.println("Please enter the cost of the inventory item");
		item.setCost(getDoubleInput());
		System.out.println("Please enter the vendor of the inventory item");
		String vendor = in.next().toLowerCase();
		item.setVendor(vendor.replace(" ", "_"));
		item.setCurrent("y");
		System.out.println("Please enter the size of the inventory item");
		item.setSize(getNumberInput());
		System.out.println("Please enter the par level of the inventory item");
		item.setPar(getNumberInput());
		//=====TEST PRINT
		System.out.println(item.itemToString());

		// add to database
		dc.insertItem(item);
		// add to curr_inventory
		dc.insertCurrInventory(item.getName());
		// update list
		inventoryItems = dc.getItems();
		
	}
	//get vendor information from user and add to database
	private static void makeVendor(String name) {
		Vendor vendor = new Vendor();
		name = name.replace(" ", "_");
		System.out.println("Please enter the information for " + name);
		vendor.setName(name.toLowerCase());
		System.out.println("Please enter the address for " + name + 
				" separated with commas");
		vendor.setAddress(in.next().toLowerCase().replace(" ", "_"));
		System.out.println("Please enter the phone number for " + name);
		vendor.setPhone(in.next());
		System.out.println("Please enter the email for " + name);
		vendor.setEmail(in.next());
		System.out.println("Please enter the delivery day for " + name);
		vendor.setDeliveryDay(in.next());
		System.out.println("Please enter the delivery minimum for " + name);
		vendor.setDeliveryMin(getDoubleInput());
		System.out.println("Please enter the delivery deadline for " + name);
		vendor.setDeliveryDeadline(in.next());
		//=====TEST PRINT
		System.out.println(vendor.vendorToString());

		// add to database
		dc.insertVendor(vendor);
		
	}

	//send amount to database controller
	private static void sendAmount(String name) {
		System.out.println("Please enter amount");
		Double amount = getDoubleInput();
		name = name.replace(" ", "_");
		dc.updateAmount(name.toLowerCase(), amount, 0, currDate.toString());
	}



	//check current inventory for empty items, check with user to update
	private static void cleanInventory() {
		System.out.println("Checking inventory");
		// get a set of strings from database controller
		String[] strings = dc.getNullItems(currDate.toString());
		if (strings.length > 0) {
			for (String s : strings) {	
				if (s.isEmpty()) {
					continue;
				} else {	
					System.out.println("Do you want to update amount of " + s 
						+ "(y/n)");
					String update = in.next();
					if (update.equals("y")) {
						System.out.println("Please enter amount");
						Double amount = getDoubleInput();
						// update the amount in database
						dc.updateAmount(s, amount, 0, currDate.toString());
					} else {
						System.out.println(s + " updated to zero and set to not current");
						dc.updateAmount(s, 0.0, 0, currDate.toString());
						dc.setCurrInvAvail(s, 0);
						dc.updateItem(s, "current", "n");
					}
				}
			}
		}
		strings = dc.getMissingItems();
		if (strings.length > 0) {
			for (String s : strings) {
				if (s.isEmpty()) {
					continue;
				} else {	
					System.out.println("Do you want to update " + s 
						+ " in master inventory (y/n)");
					String update = in.next();
					if (update.equals("y")) {
						makeItem(s);
					} else {
						System.out.println("Needs to be added to master");
					}
				}
			}
		}
	}


	private static void updateCurrAmount() {
		System.out.println("Would you like to enter a new entry(1) ?" +
		 			"or add to existing entry(2)?");
		int option = getNumberInput();
		System.out.println("Please enter name of item to update:");
		String name = in.next().toLowerCase();
		System.out.println("Please amount to update:");
		Double amount = getDoubleInput();
		switch (option) {
			case 1: 
				dc.updateAmount(name, amount, 0, currDate.toString());
				break;
			case 2:
				dc.updateAmount(name, amount, 1, currDate.toString());
				break;
			default:
				System.out.println("Please enter viable option");
				updateCurrAmount();
				break;
		}
	}


	private static void updateItem() {
		String value;
		System.out.println("Please enter the name of item to update:");
		String name = in.next().toLowerCase().replace(" ", "_");
		System.out.println("Please ener field to update: \n" +
					"\t1\tfor type\n\t2\tfor cost\n" +
					"\t3\tfor vendor\n\t4\tfor current\n" +
					"\t5\tfor size\n\t6\tfor par");
		int option = getNumberInput();
		switch (option) {
			case 1:
				System.out.println("Please enter new type value for " + name);
				value = in.next();
				dc.updateItem(name, "type", value);
				break;
			case 2:
				System.out.println("Please enter new cost value for " + name);
				value = in.next();
				dc.updateItem(name, "cost", value);
				break;
			case 3:
				System.out.println("Please enter new vendor value for " + name);
				value = in.next();
				dc.updateItem(name, "vendor", value);
				break;
			case 4:
				System.out.println("Please enter new current value for " + name);
				value = in.next();
				dc.updateItem(name, "current", value);
				break;
			case 5:
				System.out.println("Please enter new size value for " + name);
				value = in.next();
				dc.updateItem(name, "size", value);
				break;
			case 6:
				System.out.println("Please enter new par level value for " + name);
				value = in.next();
				dc.updateItem(name, "par", value);
				break;
			default:
				System.out.println("Please enter viable option");
				updateItem();
				break;
			}
	}

	private static void updateVendor() {
		String value;
		System.out.println("Please enter the name of vendor to update:");
		String name = in.next().toLowerCase().replace(" ", "_");
		System.out.println("Please ener field to update: \n" +
					"\t1\tfor address\n\t2\tfor phone number\n" +
					"\t3\tfor email address\n\t4\tfor contact person\n" +
					"\t5\tfor delivery day\n\t6\tfor delivery minimum" +
					"\t7\tfor delivery deadline");
		int option = getNumberInput();
		switch (option) {
			case 1:
				System.out.println("Please enter new address for " + name);
				value = in.next();
				dc.updateVendor(name, "address", value);
				break;
			case 2:
				System.out.println("Please enter new phone number for " + name);
				value = in.next();
				dc.updateVendor(name, "cost", value);
				break;
			case 3:
				System.out.println("Please enter new email address for " + name);
				value = in.next();
				dc.updateVendor(name, "email", value);
				break;
			case 4:
				System.out.println("Please enter new contact person for " + name);
				value = in.next();
				dc.updateVendor(name, "contactPerson", value);
				break;
			case 5:
				System.out.println("Please enter new delivery day for " + name);
				value = in.next();
				dc.updateVendor(name, "deliveryDay", value);
				break;
			case 6:
				System.out.println("Please enter new par delivery minimum for " +
					       	name);
				value = in.next();
				dc.updateVendor(name, "deliveryMin", value);
				break;
			case 7:
				System.out.println("Please enter new par delivery deadline for " +
					       	name);
				value = in.next();
				dc.updateVendor(name, "deliveryDeadline", value);
				break;
			default:
				System.out.println("Please enter viable option");
				updateVendor();
				break;
			}
	}

	//printing menu
	private static void printMenu() {
		Boolean pCont = true;
		while (pCont) { 
			System.out.println("Please enter file to print : " +
					"\n 1\tMaster Inventory" +
					"\n 2\tMaster Inventory by Type" +
					"\n 3\tMaster Inventory by Vendor" +
					"\n 4\tCurrent Master Inventory" +
					"\n 5\tCurrent Master Inventory by Type" +
					"\n 6\tCurrent Master Inventory by Vendor" +
					"\n 7\tInventory by Single Date" +
					"\n 8\tInventory by Multiple Dates" +
					"\n 9\tVendors" +
					"\n 0\tTo go back to main menu");
			switch (getNumberInput()) {
				case 1:
					InventoryReports.writeMasterInventory("allAuto");
					break;
				case 2:
					InventoryReports.writeMasterInventory("allType");
					break;
				case 3:
					InventoryReports.writeMasterInventory("allVendor");
					break;
				case 4:
					InventoryReports.writeMasterInventory("currAuto");
					break;
				case 5:
					InventoryReports.writeMasterInventory("currType");
					break;
				case 6:
					InventoryReports.writeMasterInventory("currVendor");
					break;
				case 7:
					System.out.println("Please enter the date of inventory to" +
						       	"print\nin the format YYYY-MM-DD \n Enter (0)"
							+ " to enter or (1) for help");
					int option = getNumberInput();
					String date = getDateInput(option);
					int num = dc.checkDate(date);
					if (num == 1) {
						currDate = LocalDate.parse(date);
						System.out.println("Date set to: " 
								+ currDate.toString());
					} else {
						String[] inventories = 
							dc.getInventories();
					      System.out.println("Please entery number of inventory");
						for (int i = 0; i < inventories.length; i++) {
							if (i % 2 == 1) {
								System.out.print(i + "\t" + 
									inventories[i] + "\t");
							} else {
								System.out.print(i + "\t" + 
									inventories[i] + "\n");
							}
						}
						int index = getNumberInput();
						currDate = LocalDate.parse(inventories[index]);
						System.out.println("Date set to: " 
								+ currDate.toString());
					}
					InventoryReports.writeInventory(currDate.toString());
					break;
				case 8:
					System.out.println("Please enter (0) to enter the dates" +
					       "or (1) for help with date range range\n");
					int option2 = getNumberInput();
					System.out.println( "Start date : ");
					String start = getDateInput(option2);
					System.out.println("End date : ");
					String end = getDateInput(option2);
					InventoryReports.writeInventoryRange(start, end);
					break;
				case 9: 
					InventoryReports.writeVendors();
					break;
				case 0 :
					pCont = false;
					break;
				default:
					System.out.println("Please enter viable option");
					printMenu();
					break;
			}
		}

	}

	//helpers
	public static int getNumberInput() {
		int result = 0;
		try {
			result = Integer.parseInt(in.next());
		} catch (NumberFormatException e) {
			System.out.println("Please enter viable option from menu above");
			getNumberInput();
		}
		return result;
	}


	public static Double getDoubleInput() {
		Double result = 0.0;
		try {
			result = Double.parseDouble(in.next());
		} catch (NumberFormatException e) {
			System.out.println("Please enter viable amount in the form 0.0");
			getDoubleInput();
		}
		return result;
	}
	public static String getDateInput(int i) {
		String result = "";
		switch (i) {
			case 0:
				System.out.println("Enter date");
				result = in.next();
				String[] results = result.split("-");
				int cont = 0;
				System.out.println("getting date " + currDate.getYear());
				if (results.length != 3) {
					System.out.println("Please enter in the form YYYY-MM-DD");
					getDateInput(0);
				} else if (!(getDNInput(results[0]) <= currDate.getYear())) {
					System.out.println("Please enter valid year");
					getDateInput(0);
				} else if (!(getDNInput(results[1]) <= 12)) {	
					System.out.println("Please enter valid month");
					getDateInput(0);
				} else if (!(getDNInput(results[2]) <= 31)) {
					System.out.println("Please enter valid day");
					getDateInput(0);
				}
				break;
			case 1:
				System.out.println("Please enter year: ");
				result = String.valueOf(getNumberInput() + "-");
				System.out.println("Please enter two number month: ");
				result = String.valueOf(getNumberInput() + "-");
				System.out.println("Please enter two number day: ");
				result = String.valueOf(getNumberInput());
				break;
			default:
				System.out.println("Please enter viable option");
				getDateInput(i);
				break;
		}		
		return result;
	}

	public static int getDNInput(String s) {
		int result = 0;
		try {
			result = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			System.out.println("Please enter viable date format");
			getDNInput(s);
		}
		return result;
	}
}	
		

