
/*	Basic Inventory App: Database Controller
 *
 * 		Singleton Design
 *		Database controls
 *		Does not dynamically create database or tables
 *		creates all tables in one method
 *		has hard coded connection strings
 *
 * 		instance variables
 * 			Connection conn
 * 			Statement stmt
 * 			String user
 * 			String password
 * 			String url
 * 			DatabaseController dc
 *
 *  		methods
 *			getInstance()
 *			getConnected()
 *
 *			Create
 *				createTable()
 *				newInventory(date)
 *				insertItem(item)
 *				insertCurrInventory(name)
 *				instertVendor(vendor)
 *			Read
 *				getLastInventory()
 *				getInventories()
 *				getItems()
 *				getCurrInventory(date)
 *				getMultipleInventories(startDate, endDate)
 *				getMaster(type)
 *				getVendors()
 *				getNullItems(date)
 *				getMissingItems()
 *			Update
 *				
 *				updateItem(name, field, value)
 *				updateVendor(name, field, value)
 *				updateAmount(name, amount, flag, date);
 *				updateCurrAmount(name, amount)
 *			Delete
 *				setCurrInvAvail(name, available)
 *
 *			formatResultSetMaster(resulltSet, string[], header) 
 *			doesExist(name) throws SQLException
 *			checkDate(date)
 *			isCurrent(name)
 *
 *			
 *
 *
 *	Author: CChapman
 *	Date: 2024-03-06
 *	Version: 1.0
 *
 * */

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseController {

	private static Connection conn = null;
	private static Statement stmt = null;
	private final String password = "";
	private final String user = "";
	private final String url = "jdbc:postgresql://127.0.0.1:5432/inventory";
	private static DatabaseController inst = null;

	private DatabaseController() {
	}

	//get singleton instance if created, otherwise create and return
	public static DatabaseController getInstance() {
		if (inst == null) {
			inst = new DatabaseController();
			inst.getConnected();
			// test connection
			try {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// check if tables are in databese
			inst.createTable();
	
		}
		return inst;
	}


	//connect to database
	private void getConnected() {
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Connection successfull");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Connection did not work!");
		}
	}

	//check if database has tables, create if not
	private void createTable() {
		try {
			DatabaseMetaData metaData = conn.getMetaData();
			// entire inventory of items
			ResultSet itemTable = metaData.getTables(null, null, "inventory", null);
			if (itemTable.next()) {
				System.out.println("Table is available");
			} else {
				String sql = "CREATE TABLE inventory (name varchar primary key," +
				      "type varchar, cost float, vendor varchar, current" +
				      " varchar, size int, par int)";
				stmt.executeUpdate(sql);
			}
			// current date's inventory items and amounts
			ResultSet numberTable = metaData.getTables(null,null, "curr_inventory",null);
			if (numberTable.next()) {
				System.out.println("Table is available");
			} else {
				String sql= "CREATE TABLE curr_inventory (name varchar primary key," +
				        " current int)";
				stmt.executeUpdate(sql);
			}	
			// vendor table
			ResultSet vendorTable = metaData.getTables(null, null, "vendors", null);
			if (vendorTable.next()) {
				System.out.println("Table is available");
			} else {
				String sql = "CREATE TABLE vendors (name varchar primary key," +
				      "address varchar, phone varchar, email varchar, contactPerson" +
				      " varchar, deliveryDay varchar, deliveryMin float," + 
				      "deliveryDeadline varchar)";
				stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Table creation was unsuccessfull");
		}
	}





	//create new column entry for current date
	public void newInventory(String currDate) {
		try {
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rs = metaData.getColumns(null, null, "curr_inventory", currDate);
			if (rs.next()) {
				System.out.println("Continuing with previously started inventory");
			} else {
				String sql = "alter table curr_inventory add column \"" 
					+ currDate + "\" float";
				stmt.executeUpdate(sql);
				System.out.println("Column " + currDate + "created");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Column was not created");
		}
	}

	//insert new item into master and current date's inventory
	public void insertItem(InventoryItem item) {
		try {
			String sql = "insert into inventory(name, type, cost, vendor, current," +
			       "size, par) values ('" + item.getName() + "', '" + item.getType() + 
			       "', '" + item.getCost() + "','" + item.getVendor() + "', '" + 
			       item.getCurrent() + "','" + item.getSize() + "','" + 
			       item.getPar() + "')";
			stmt.executeUpdate(sql);
			System.out.println("Item entered into inventory");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Item not entered into inventory");
		}
	}

	//insert item into curr_inventory
	public void insertCurrInventory(String name) {
		try {
			String sql = "insert into curr_inventory(name, current) values ('" 
				+ name + "', 1) on conflict (name) do nothing";
			stmt.executeUpdate(sql);
			System.out.println("Update executed successfully.");

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Update not executed");
		} 
	}

	//insert new vendor
	public void insertVendor(Vendor vendor) {
		try {
			String sql = "insert into vendors (name, address, phone, email, " + 
				"contactPerson, deliveryDay, deliveryMin, deliveryDeadline) " +
				"values ('" + vendor.getName() + "', '" + vendor.getAddress() + 
			       "', '" + vendor.getPhone() + "','" + vendor.getEmail() + "', '" + 
			       vendor.getContactPerson() + "','" + vendor.getDeliveryDay() + "','" + 
			       vendor.getDeliveryMin() + "','" + vendor.getDeliveryDeadline() + "')";
			stmt.executeUpdate(sql);
			System.out.println("Vendor entered into inventory");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Vendor failed to enter");
		}
	}

	//get date of last inventory worked on
	public String getLastInventory() {
		String lastInventory = "";
		try {
			ResultSet rs = stmt.executeQuery("select * from curr_inventory limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			lastInventory = rsmd.getColumnName(count);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Last Inventory not restrieved");
			lastInventory = "Unavailable";
		}
		return lastInventory;
	}

	//get list of inventory dates
	public String[] getInventories() {
		String inventories = "";
		try {
			ResultSet rs = stmt.executeQuery("select * from curr_inventory limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			for (int i = 2; i <= count; i++) {
				if (i == count) {
					inventories += rsmd.getColumnName(i);
				} else {
					inventories += rsmd.getColumnName(i) + ",";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to retireve list of inventories available");
		}
		return inventories.split(",");
	}

	//get the names of all items in inventory
	public String[] getItems() {
		String[] ss = new String[0];
		try {
			String sql = "select name from inventory";
			ResultSet rs = stmt.executeQuery(sql);
			String strings = "";
			while (rs.next()) {
				strings = strings + " " + rs.getString("name");
			}
			ss = strings.split(" ");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to retrieve list of items in inventory");
		}
		return ss;
	}

	//get formated inventory ordered by vendor in list of strings
	public String[] getCurrDateInventory(String currDate) {
		String header = "NAME," + currDate + ",VENDOR";
		String[] ss = new String[0];
		int index = 0;
		try {
			String sql = "select curr_inventory.name, \"" + currDate +
			       "\", vendor from curr_inventory where current = 1 join inventory"
			      + " on curr_inventory.name = inventory.name order by vendor";
			// column names
			ResultSet rs = stmt.executeQuery(sql);
			rs.last();
			ss = new String[rs.getRow() + 1];
			rs.beforeFirst();
			ss[index] = header;
			while (rs.next()) {
				index++;
				ss[index] = rs.getString("name") + "," +
						rs.getDouble(currDate) + "," + rs.getString("vendor");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to retrieve the current inventory for " 
					+ currDate);
		}
		return ss;
	}

	//get foramted ncurr_inventory for multiple dates
	public String[] getMultipleInventories(String start, String end) {
		LocalDate startDate = LocalDate.parse(start);
		LocalDate endDate = LocalDate.parse(end);
		List<LocalDate> dates = startDate.datesUntil(endDate).collect(Collectors.toList());
		ArrayList<String> availableDates = new ArrayList<String>();
		String[] stringDates = new String[dates.size()];
		int i = 0;
		for (LocalDate d : dates) {
			stringDates[i] = d.toString();
			i++;
		}
		String[] inventories = getInventories();
		for (int n = 0; n < inventories.length; n++) {
			for (int j = 0; j < stringDates.length; j++) {
				if (inventories[n].equals(stringDates[j])) {
					availableDates.add(stringDates[j]);
				}
			}
		}
		String sql = "select curr_inventory.name, ";
		String[] inventoryStrings = new String[availableDates.size()];
		for (int m = 0; m < availableDates.size(); m++) {
			if (m == availableDates.size() - 1) {
				sql += "\"" + availableDates.get(m) +"\" ";
				
			} else {
				sql += "\"" + availableDates.get(m) +"\", ";
			}
			inventoryStrings[m] = availableDates.get(m);
		}
		sql += ", vendor from curr_inventory join inventory on curr_inventory.name" +
			" = inventory.name order by vendor";
		String[] ss = new String[0];
		int index = 0;
		String headings = "NAME,";
		for (int j = 0; j < inventoryStrings.length; j++) {
			if (j == inventoryStrings.length - 1) {
				headings += inventoryStrings[j];
			} else {
				headings += inventoryStrings[j] + ",";
			}
		}
		headings += ",VENDOR";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			rs.last();
			ss = new String[rs.getRow() + 1];
			rs.beforeFirst();
			ss[index] = headings;
			while (rs.next()) {
				index++;
				String s = rs.getString("name") + ",";
				for (int k = 0; k < inventoryStrings.length; k++) {
					if (k == inventoryStrings.length - 1) {
						s += rs.getString(inventoryStrings[k]);
					} else {	
						s += rs.getString(inventoryStrings[k]) + ","; 
					}
				}
				s += "," + rs.getString("vendor");
				ss[index] = s;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to retrieve inventories for " + start +
					"to " + end);
		}
		return ss;
	}


	//get formated master inventory auto, type or vendor order
	public String[] getMaster(String action) {
		String[] ss = new String[0];
		String header = "NAME,TYPE,COST,VENDOR,CURRENT,SIZE,PAR";
		String sql = "select * from inventory";
		ResultSet rs;
		try {
			switch (action) {
				case "allAuto":
					rs = stmt.executeQuery(sql);
					ss = formatResultSetMaster(rs, ss, header);
					break;
				case "allType":
					sql += " order by type";	
					rs = stmt.executeQuery(sql);
					ss = formatResultSetMaster(rs, ss, header);
					break;
				case "allVendor":
					sql += " order by vendor";	
					rs = stmt.executeQuery(sql);
					ss = formatResultSetMaster(rs, ss, header);
					break;
				case "currAuto":
					sql += " where current = \'y\'";
					rs = stmt.executeQuery(sql);
					ss = formatResultSetMaster(rs, ss, header);
					break;
				case "currType":
					sql += " where current = \'y\' order by type";	
					rs = stmt.executeQuery(sql);
					ss = formatResultSetMaster(rs, ss, header);
					break;
				case "currVendor":
					sql += " where current = \'y\' order by vendor";	
					rs = stmt.executeQuery(sql);
					ss = formatResultSetMaster(rs, ss, header);
					break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to retrieve master inventory");
		}
		return ss;
	}

	//get formated list of vendors
	public String[] getVendors() {
		String[] ss = new String[0];
		int index = 0;
		String header = "NAME,ADDRESS,PHONE,EMAIL,CONTACT,DAY,MIN,DEADLINE";
		try { 
			String sql = "select * from vendors";
			ResultSet rs = stmt.executeQuery(sql);
			rs.last();
			ss = new String[rs.getRow() + 1];
			rs.beforeFirst();
			ss[index] = header;
			while (rs.next()) {
				index++;
				ss[index] = rs.getString("name") + "," + 
					rs.getString("address").replace(",", ".") + 
					"," + rs.getString("phone") + "," 
					+ rs.getString("email") + "," 
					+ rs.getString("contactPerson")	+ "," 
					+ rs.getString("deliveryDay") + "," 
					+ String.valueOf(rs.getDouble("deliveryMin")) + "," 
					+ rs.getString("deliveryDeadline");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to retrieve vendors from inventory");
		}
		return ss;
	}


	//get string names of items that did not have amounts entered for current date
	public String[] getNullItems(String currDate) {
		String s = "";
		try {
			String sql = "select name, \"" + currDate + "\" from curr_inventory";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Double d = rs.getDouble(currDate);
				if (rs.wasNull()) {
					s += rs.getString("name") + ",";
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to retrieve the inventory for " + currDate);
		}
		return s.split(",");
	}

	//get string names of items in curr_inventory and not in master inventory
	public String[] getMissingItems() {
		String s = "";
		try {
			String sql = "select name from curr_inventory";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				if (doesExist(name) == 0) {
					s += name + ",";
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to retrieve items in current inventory " +
					"and not in master inventory");
		}
		return s.split(",");
	}


	//update item in master inventory
	public void updateItem(String name, String field, String value) {
		try {
			String sql = "";
			if (field == "cost") {
				sql = "update inventory set \"" + field + "\" = " + 
					Double.parseDouble(value) + " where name = '" + name + "'";
			} else if ((field == "size") || (field == "par")) { 
				sql = "update inventory set " + field + " = '" + 
					Integer.parseInt(value) + "' where name = '" + name + "'";
			} else {
				sql = "update inventory set " + field + " = '" + 
					value.replace(" ", "_") + "' where name = '" + name + "'";
			}
		       	stmt.executeUpdate(sql);
			System.out.println("Item updated successfully");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Item updated unsuccessfully");
		}
	}

	//update vendor
	public void updateVendor(String name, String field, String value) {
		try {
			String sql = "";
			if (field == "deliveryMin") {
				sql = "update vendors set \"" + field + "\" = " + 
					Double.parseDouble(value) + " where name = '" + name + "'";
			} else {
				sql = "update vendors set " + field + " = '" + 
					value.replace(" ", "_") + "' where name = '" + name + "'";
			}
		       	stmt.executeUpdate(sql);
			System.out.println("Vendor updated successfully");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Vendor not updated successfully");
		}
	}

	//update amount of item in curr_inventory
	public void updateAmount(String name, Double amount, int flag, String currDate) {
		try {
			// new entry
			if (flag == 0) {
				String sql = "update curr_inventory SET \"" + currDate + "\" = " +
					amount + " where name = '" + name + "'";
				int i = stmt.executeUpdate(sql);
				if (i == 0) {
					System.out.println("not in inventory, adding to current inventory");
					insertCurrInventory(name);
					updateAmount(name, amount, flag, currDate);
					
				} else {
					System.out.println("affected " + i + " row for amount " + amount +
						" and name " + name);
				}
			} 
			// add to existing entry
			else if (flag == 1) {
				String sql = "select \"" + currDate + "\" from curr_inventory where " +
					"name = '" + name + "'";
				ResultSet rs = stmt.executeQuery(sql);
				if (rs.next()) {
					Double prevAmnt = rs.getDouble(currDate);
					amount += prevAmnt;
				} else {
					System.out.println("Result set empty");
				}
				sql = "update curr_inventory set \"" + currDate + "\" = " +
					amount + " where name = '" + name + "'";
				stmt.executeUpdate(sql);
				System.out.println("Amount " + amount + " updated successfully" +
						"for " + name + " on " + currDate);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Amount " + amount + " not updated successfully" +
					"for " + name + " on " + currDate);
		}
	}

	//set entry in current inventory to current or not
	public void setCurrInvAvail(String name, int i) {
		try {
			String sql = "update curr_inventory set current = " + i + " where " +
				" name = '" + name + "'";
		       stmt.executeUpdate(sql);	
		       System.out.println(name + " set to " + i);
		} catch (SQLException e) {
			e.printStackTrace();
		       System.out.println(name + " not set to " + i);
		}
	}

	//helpers
	//
	//get strings for print from resultSet for master inventory, all fields
	public String[] formatResultSetMaster(ResultSet rs, String[] ss, 
			String header) throws SQLException {
		rs.last();
		ss = new String[rs.getRow() + 1];
		rs.beforeFirst();
		int index = 1;
		ss[0] = header;
		while (rs.next()) {
			ss[index] = rs.getString("name") + "," +  rs.getString("type")
				+ "," + rs.getDouble("cost") + "," + 
				rs.getString("vendor") + "," + 
				rs.getString("current") + "," +
				rs.getInt("size") + ","+ rs.getInt("par");
			index++;
		}
		return ss;
	}

	// check if item is in master inventory
	public int doesExist(String name) throws SQLException {
		int result = 0;
		Statement stmt2 = conn.createStatement();
		String sql = "select name from inventory where name = '" + name +"'";
		ResultSet rs = stmt2.executeQuery(sql);
		if (rs.next()) {
			result = 1;
		}
		return result;
	}

	//check if inventory date is available
	public int checkDate(String date) {
		int available = 0;
		try {
			ResultSet rs = stmt.executeQuery("select \"" + date + "\" from curr_inventory");
			available = 1;
		} catch (SQLException e) {
			System.out.println("Inventory not available");
		}
		return available;
	}

	//check if item is current
	public Boolean isCurrent(String name) {
		Boolean isCurrent = false;
		try {
			String sql = "select current from inventory where name = '" + name + "'";
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			if (rs.getString("current").equals("y")) {
				isCurrent = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Item not in inventory");
		}
		return isCurrent;
	}
}
