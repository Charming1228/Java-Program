import java.io.*;
import java.util.*;

class Room {
    private String type;
    private String description;
    private double rate;

    public Room(String type, String description, double rate) {
        this.type = type;
        this.description = description;
        this.rate = rate;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return type + " - " + description + " - $" + rate + " per day";
    }
}

public class HotelRoomCharges {
    private static List<Room> rooms = new ArrayList<>();
    private static String logFileName;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java HotelRoomCharges <roominfo.txt> <log.txt>");
            return;
        }

        String roomFileName = args[0];
        logFileName = args[1];

        // Read room information file
        if (!loadRoomData(roomFileName)) {
            System.out.println("Error loading room data.");
            return;
        }

        // Display menu
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nAvailable Rooms:");
            for (int i = 0; i < rooms.size(); i++) {
                System.out.println((i + 1) + ". " + rooms.get(i).toString());
            }
            System.out.println("0. Exit");
            System.out.print("Select a room type (Enter number): ");

            int choice = scanner.nextInt();
            if (choice == 0) {
                System.out.println("Exiting...");
                break;
            }
            if (choice < 1 || choice > rooms.size()) {
                System.out.println("Invalid selection. Try again.");
                continue;
            }

            Room selectedRoom = rooms.get(choice - 1);
            System.out.println("You selected: " + selectedRoom.toString());

            System.out.print("Enter number of rooms: ");
            int numRooms = scanner.nextInt();
            System.out.print("Enter number of days: ");
            int numDays = scanner.nextInt();

            double totalCharge = numRooms * numDays * selectedRoom.getRate();
            System.out.println("Estimated charge: $" + totalCharge);

            logQuery(selectedRoom.getType(), numRooms, numDays, totalCharge);
        }
        scanner.close();
    }

    private static boolean loadRoomData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            int numRooms = Integer.parseInt(br.readLine().trim());
            for (int i = 0; i < numRooms; i++) {
                String[] parts = br.readLine().split("\\|");
                if (parts.length != 3)
                    continue;
                String type = parts[0].trim();
                String description = parts[1].trim();
                double rate = Double.parseDouble(parts[2].trim());
                rooms.add(new Room(type, description, rate));
            }
            return true;
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading room file: " + e.getMessage());
            return false;
        }
    }

    private static void logQuery(String roomType, int numRooms, int numDays, double totalCharge) {
        try (FileWriter fw = new FileWriter(logFileName, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println("Room Type: " + roomType + ", Rooms: " + numRooms + ", Days: " + numDays + ", Charge: $"
                    + totalCharge);
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }
}
