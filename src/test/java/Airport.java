import java.io.*;
import java.util.ArrayList;

public class Airport {
    private ArrayList<Flight> flights = new ArrayList<Flight>();

    public Airport() {
        populateFlights();
    }

    public void populateFlights(){
        Flight flight1 = new Flight("001","Sydney");
        Flight flight2 = new Flight("002","Melbourne");
        Flight flight3 = new Flight("003","Beijing");
        flights.add(flight1);
        flights.add(flight2);
        flights.add(flight3);
    }

    public void addFlight(){
        for (Flight flight:flights){
            flight.setCost(300.5);
        }
    }

    public void listFlights(){
        for (Flight flight:flights){
            System.out.println(flight);
        }
    }

    public void run(){
        populateFlights();
        addFlight();
        listFlights();
        saveFlight();
        readFlight();
        listFlights();
    }

    public static void main(String[] args) {
        Airport airport = new Airport();
        airport.run();
    }

    public void saveFlight(){
        try {
            FileOutputStream fileOut = new FileOutputStream("flights.dat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(flights);
            out.close();
            fileOut.close();
            System.out.println("Flights serialized successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFlight(){
        try {
            FileInputStream fileIn = new FileInputStream("flights.dat");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            flights = (ArrayList<Flight>) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Flights deserialized successfully.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
