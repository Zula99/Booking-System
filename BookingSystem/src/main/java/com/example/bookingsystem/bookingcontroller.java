package com.example.bookingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;


public class bookingcontroller {
    private ArrayList<customer> clients = new ArrayList<>();
    private ArrayList<appointment> timeSlots = new ArrayList<>();
    private Map<String, ArrayList<appointment>> weeklyAppointments = new HashMap<>();
    private LocalDate currentWeekDate = LocalDate.now();



    @FXML
    private Button addAppbtn, backbtn, forwardbtn, addCustBtn;
    @FXML
    private TextField ntxtF, numbtxtF;
    @FXML
    private ChoiceBox<String> timeChoiceBox, dayChoiceBox;
    @FXML
    private ChoiceBox<customer> custChoiceBox;
    @FXML
    private TableView<appointment> appointmentTable;
    @FXML
    private TableColumn<appointment, String> timeColumn;
    @FXML
    private TableColumn<appointment, String> monColumn;
    @FXML
    private TableColumn<appointment, String> tueColumn;
    @FXML
    private TableColumn<appointment, String> wedColumn;
    @FXML
    private TableColumn<appointment, String> thurColumn;
    @FXML
    private TableColumn<appointment, String> friColumn;
    @FXML
    private TableColumn<appointment, String> satColumn;
    @FXML
    private TableColumn<appointment, String> sunColumn;

    @FXML
    private void initialize() {
        System.out.println("Initializing controller...");

        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        monColumn.setCellValueFactory(new PropertyValueFactory<>("Monday"));
        tueColumn.setCellValueFactory(new PropertyValueFactory<>("Tuesday"));
        wedColumn.setCellValueFactory(new PropertyValueFactory<>("Wednesday"));
        thurColumn.setCellValueFactory(new PropertyValueFactory<>("Thursday"));
        friColumn.setCellValueFactory(new PropertyValueFactory<>("Friday"));
        satColumn.setCellValueFactory(new PropertyValueFactory<>("Saturday"));
        sunColumn.setCellValueFactory(new PropertyValueFactory<>("Sunday"));

        loadCustomer();
        loadAppointments();
        loadWeekAppointments();

        if (appointmentTable.getItems().isEmpty()) {
            System.out.println("Creating time slots...");
            for (int hour = 9; hour <= 21; hour++) {
                String time = String.format("%02d:00", hour);
                appointment newAppointment = new appointment(time, "", "", "", "", "", "", "");
                timeSlots.add(newAppointment);
                appointmentTable.getItems().add(newAppointment);
                timeChoiceBox.getItems().add(time);
            }
        } else {
            System.out.println("Appointments already loaded, skipping time slot creation.");
            timeSlots.clear();
            timeChoiceBox.getItems().clear();
            // Populate timeSlots and timeChoiceBox from existing appointments
            for (appointment app : appointmentTable.getItems()) {
                timeSlots.add(app);
                if (!timeChoiceBox.getItems().contains(app.getTime())){
                    timeChoiceBox.getItems().add(app.getTime());
                }
            }
        }

        dayChoiceBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        populatecustomer();

        addCustBtn.setOnAction((actionEvent -> addCustomer()));

        addAppbtn.setOnAction(actionEvent -> addAppointment());

        backbtn.setOnAction(ActionEvent -> navigateWeek(-1));
        forwardbtn.setOnAction(ActionEvent -> navigateWeek(+1));
//        backbtn.setOnAction(actionEvent -> {currentWeek--; loadWeekAppointments();});
//        forwardbtn.setOnAction(actionEvent -> {currentWeek++; loadWeekAppointments();});



    }



    private void populatecustomer() {
        custChoiceBox.getItems().clear();
        for (customer customer : clients) {
            custChoiceBox.getItems().add(customer);
        }
    }

    private void createCustomer(String name, String number) {
        customer newCustomer = new customer(name, number);
        clients.add(newCustomer);
        System.out.println("Creating customer: " + name + ", " + number);
        saveCustomer();
    }

    private void addCustomer() {
        String name = ntxtF.getText();
        String number = numbtxtF.getText();
        if (name == null || number == null || name.trim().isEmpty() || number.trim().isEmpty()) {
            Alert fail = new Alert(Alert.AlertType.INFORMATION);
            fail.setHeaderText("Error");
            fail.setContentText("Both Name and Number must be entered");
            fail.showAndWait();
        } else {
            createCustomer(name, number);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Success");
            alert.setContentText("Account successfully created\n" + name + " " + number);
            alert.showAndWait();
            populatecustomer();
        }
    }

    private void addAppointment() {
        customer selectedCust = custChoiceBox.getValue();
        String selectedDay = dayChoiceBox.getValue();
        String selectedTime = timeChoiceBox.getValue();

        if (selectedCust == null || selectedDay == null || selectedTime == null) {
            Alert fail = new Alert(Alert.AlertType.INFORMATION);
            fail.setHeaderText("Error");
            fail.setContentText("Must select customer, day and time");
            fail.showAndWait();
            return;
        }

        String weekID = getCurrentWeekId(currentWeekDate);
        ArrayList<appointment> weekAppointments = weeklyAppointments.getOrDefault(weekID, new ArrayList<>());

        boolean appointmentAdded = false;
        for (appointment appointment : weekAppointments) {
            if (appointment.getTime().equals(selectedTime)) {
                switch (selectedDay) {
                    case "Monday":
                        appointment.setMonday(selectedCust.getInfo());
                        break;
                    case "Tuesday":
                        appointment.setTuesday(selectedCust.getInfo());
                        break;
                    case "Wednesday":
                        appointment.setWednesday(selectedCust.getInfo());
                        break;
                    case "Thursday":
                        appointment.setThursday(selectedCust.getInfo());
                        break;
                    case "Friday":
                        appointment.setFriday(selectedCust.getInfo());
                        break;
                    case "Saturday":
                        appointment.setSaturday(selectedCust.getInfo());
                        break;
                    case "Sunday":
                        appointment.setSunday(selectedCust.getInfo());
                        break;
                }
                appointmentAdded = true;
                break;  // Exit the loop once the appointment is added
            }
        }

        if (appointmentAdded) {
            System.out.println("Adding appointment for " + selectedCust.getInfo() + " on " + selectedDay + " at " + selectedTime);
            weeklyAppointments.put(weekID, weekAppointments);
            appointmentTable.refresh();
            saveAppointments();
        } else {
            System.out.println("Failed to add appointment. Time slot not found.");
        }
    }

    public void saveCustomer() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("customers.dat"))) {
            System.out.println("Saving customers...");
            out.writeObject(clients);
            System.out.println("Customers saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCustomer() {
        File file = new File("customers.dat");
        if (!file.exists()) {
            System.out.println("No customers.dat file found.");
            return; // File does not exist, nothing to load
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("customers.dat"))) {
            clients = (ArrayList<customer>) in.readObject();
            System.out.println("Customers loaded: " + clients.size());
            populatecustomer();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadAppointments() {
        File file = new File("appointments.dat");
        if (!file.exists()) {
            System.out.println("No appointments.dat file found.");
            return; // File does not exist, nothing to load
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("appointments.dat"))) {
            weeklyAppointments = (HashMap<String, ArrayList<appointment>>) in.readObject();
            System.out.println("Appointments loaded into weeklyAppointments: " + weeklyAppointments.size());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void saveAppointments() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("appointments.dat"))) {
            System.out.println("Saving appointments...");
            out.writeObject(weeklyAppointments);
            System.out.println("Appointments saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getCurrentWeekId(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
        int year = date.getYear();
        return year + "-W" + weekNumber;
    }

    private void navigateWeek(int offset){
        currentWeekDate = currentWeekDate.plusWeeks(offset);
        loadWeekAppointments();
    }
    private void loadWeekAppointments(){
        String weekId = getCurrentWeekId(currentWeekDate);
        ArrayList<appointment> weekAppointments = weeklyAppointments.getOrDefault(weekId, new ArrayList<>());

        if(weekAppointments.isEmpty()){
            initializeTimeSlots(weekAppointments);
            weeklyAppointments.put(weekId, weekAppointments);
        }
        appointmentTable.getItems().clear();
        appointmentTable.getItems().addAll(weekAppointments);


        System.out.println("Appointments loaded for week: " + weekId);
    }
    private void initializeTimeSlots(ArrayList<appointment> weekAppointments) {
        for (int hour = 9; hour <= 21; hour++) {
            String time = String.format("%02d:00", hour);
            weekAppointments.add(new appointment(time, "", "", "", "", "", "", ""));
        }
    }

}

//public void loadAppointment() {
//    File file = new File("appointments.dat");
//    if (!file.exists()) return;
//    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
//        HashMap<Integer, ArrayList<appointment>> appointmentsMap = (HashMap<Integer, ArrayList<appointment>>) in.readObject();
//        if (appointmentsMap != null) {
//            // Load appointments for the current week
//            ArrayList<appointment> weekAppointments = appointmentsMap.getOrDefault(currentWeek, new ArrayList<>());
//            appointmentTable.getItems().setAll(weekAppointments);
//        }
//    } catch (IOException | ClassNotFoundException e) {
//        e.printStackTrace();
//    }
//}



//public void loadAppointment() {
//    File file = new File("appointment.dat");
//    if (!file.exists()) {
//        System.out.println("No appointment.dat file found.");
//        return; // File does not exist, nothing to load
//    }
//    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("appointment.dat"))) {
//        ArrayList<appointment> loadedAppointments = (ArrayList<appointment>) in.readObject();
//        System.out.println("Loaded appointments size before adding to TableView: " + loadedAppointments.size());
//        appointmentTable.getItems().clear(); // Clear existing items before loading
//        for (int hour = 9; hour <= 21; hour++) {
//            String time = String.format("%02d:00", hour);
//            appointmentTable.getItems().add(new appointment(time, "", "", "", "", "", "", ""));}
//        for(appointment loadedApp : loadedAppointments){
//            for(appointment tableApp : appointmentTable.getItems()){
//                if(tableApp.getTime().equals(loadedApp.getTime())){
//                    int index = appointmentTable.getItems().indexOf(tableApp);
//                    appointmentTable.getItems().set(index, loadedApp);
//                    break;
//                }
//            }
//        }
//        System.out.println("Appointments loaded into TableView after adding: " + appointmentTable.getItems().size());
//    } catch (IOException | ClassNotFoundException e) {
//        e.printStackTrace();
//    }
//}


//    public void saveAppointment() {
//        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("appointment.dat"))) {
//            System.out.println("Saving appointments...");
//            ArrayList<appointment> appointmentsToSave = new ArrayList<>();
//            for(appointment app : appointmentTable.getItems()){
//                if(!app.isEmpty()){
//                    appointmentsToSave.add(app);
//                }
//            }
//            out.writeObject(appointmentsToSave);
//            System.out.println("Appointments saved." + appointmentsToSave.size());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//public void saveAppointment() {
//    HashMap<Integer, ArrayList<appointment>> appointmentsMap = new HashMap<>();
//    // Group appointments by weekID
//    for (appointment appointment : appointmentTable.getItems()) {
//        int weekID = appointment.getWeekID();
//        appointmentsMap.putIfAbsent(weekID, new ArrayList<>());
//        appointmentsMap.get(weekID).add(appointment);
//    }
//    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("appointments.dat"))) {
//        out.writeObject(appointmentsMap);
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//}