/*	Basic Inventory App: Inventory Reports
 *
 * 	generates reports from database to files
 * 	date inventories are ordered by vendor
 * 	master inventory can be ordered automatically, by type or by vendor
 *
 * 	methods
 * 		writeInventory(currDate)
 * 		writeInventoryRange(startDate, endDate)
 * 		writeMasterInventory(type)
 * 		writeVendors()
 *
 * 	Author: CChapman
 * 	Date: 2024-03-06
 * 	Version: 1.0
 *
 * 	*/

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;

public class InventoryReports {


	//print inventory by single date
        public static void writeInventory(String currDate) {
                try {
			File inventoryFile = new File("inventory" + currDate + ".txt");
                        FileWriter fWriter = new FileWriter(inventoryFile);
			PrintWriter writer = new PrintWriter(fWriter);
                        // get set of strings from database controller to print inventory
                        String[] strings = DatabaseController.getInstance().
				getCurrDateInventory(currDate);
                        for(int i = 0; i < strings.length; i++) {
				String[] row = strings[i].split(",");
				if (i == 0) {
					writer.printf("%-30s\t%10s\t%-30s\n\n\n", row[0], row[1],
							row[2]);
				} else {
                                	writer.printf("%-30s\t%-10.2f\t%-30s\n", row[0], 
							Double.parseDouble(row[1]), row[2]);
				}
                        }
                        writer.close();
                } catch (IOException e) {
                        e.printStackTrace();
			System.out.println("Failed to print inventory for " + currDate);
                }
        }

	//print inventory by date range
        public static void writeInventoryRange(String startDate, String endDate) {
                try {
			File inventoryFile = new File("inventoryR" + startDate + 
					"_" + endDate + ".txt");
                        FileWriter fWriter = new FileWriter(inventoryFile);
			PrintWriter writer = new PrintWriter(fWriter);
                        // get set of strings from database controller to print inventory
                        String[] strings = DatabaseController.getInstance().
				getMultipleInventories(startDate, endDate);
                        for(int i = 0; i < strings.length; i++) {
				String[] row = strings[i].split(",");
				for (int j = 0; j < row.length; j++) {
					if (j == 0) {
						writer.printf("%-30s\t", row[j]);
					} else if (j == row.length - 1) {
						writer.printf("%-30s\t", row[j]);
					} else if (i == 0) {
						writer.printf("%-10s\t", row[j]);
					} else {
						writer.printf("%-10.2f\t", Double.parseDouble(row[j]));
					}
				}
                                if (i == 0) {
					writer.printf("\n\n\n");
				} else {
					writer.printf("\n");
				}
                        }
                        writer.close();
                } catch (IOException e) {
                        e.printStackTrace();
			System.out.println("Failed to print inventories from " + startDate +
					" to " + endDate);
                }
        }
	

	//print master inventory by alphabetic order, vendor or type
        public static void writeMasterInventory(String action) {
                try {
			File inventoryFile = new File("inventoryM" + action + ".txt");
                        FileWriter fWriter = new FileWriter(inventoryFile);
			PrintWriter writer = new PrintWriter(fWriter);
                        // get set of strings from database controller to print inventory
                        String[] strings = DatabaseController.getInstance().
				getMaster(action);
                        for(int i = 0; i < strings.length; i++) {
				String[] row = strings[i].split(",");
				if (i == 0) {
					writer.printf("%-30s\t%-10s\t%-5s\t%-30s\t%-3s\t%-5s%-3s\n\n",
							row[0], row[1], row[2], row[3], row[4],row[5],
							row[6]);
				} else {
					writer.printf("%s-30\t%-10s\t%.2f\t%-30s\t%-3s\t%.2f%.2f\n\n",
							row[0], row[1], Double.parseDouble(row[2]),
							row[3], row[4], Double.parseDouble(row[5]),
							Double.parseDouble(row[6]));
				}
                        }
                        writer.close();
                } catch (IOException e) {
                        e.printStackTrace();
			System.out.println("Failed to print master inventory");
		}
        }

	//print vendors
        public static void writeVendors() {
                try {
			File inventoryFile = new File("vendors.txt");
                        FileWriter fWriter = new FileWriter(inventoryFile);
			PrintWriter writer = new PrintWriter(fWriter);
                        // get set of strings from database controller to print inventory
                        String[] strings = DatabaseController.getInstance().
				getVendors();
                        for(int i = 0; i < strings.length; i++) {
				String[] row = strings[i].split(",");
				if (i == 0) {
					writer.printf("%-30s\t%-30s\t%-12s\t%-30s\t%-30s\t%-12s\t" +
							"%-15s\t%-30s\n\n\n", row[0], row[1], row[2],
 							row[3], row[4], row[5], row[6], row[7]);
				} else {
					writer.printf("%-30s\t%-30s\t%-12s\t%-30s\t%-30s\t%-12s\t" +
							"%.2f\t%-30s\n", row[0], row[1], row[2],
 							row[3], row[4], row[5], 
							Double.parseDouble(row[6]), row[7]);
				}
                        }
                        writer.close();
                } catch (IOException e) {
                        e.printStackTrace();
			System.out.println("Failed to print vendors.txt");
		}
        }

}
