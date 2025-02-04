package com.example.bookingsystem;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;



import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;


public class bookingcontroller {
    private ArrayList<customer> clients = new ArrayList<>();
    private ArrayList<appointment> timeSlots = new ArrayList<>();
    private Map<String, ArrayList<appointment>> weeklyAppointments = new HashMap<>();
    private LocalDate currentWeekDate = LocalDate.now();

    @FXML
    private Text  weekDisplay, weekDisplay2;
    @FXML
    private Button addAppbtn, backbtn, forwardbtn, addCustBtn;
    @FXML
    private TextField ntxtF, numbtxtF, priceTextBox;
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
    private TableColumn<appointment, Double> priceColumn;

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

        monColumn.setCellFactory(col -> new AppointmentTableCell<>(this));
        tueColumn.setCellFactory(col -> new AppointmentTableCell<>(this));
        wedColumn.setCellFactory(col -> new AppointmentTableCell<>(this));
        thurColumn.setCellFactory(col -> new AppointmentTableCell<>(this));
        friColumn.setCellFactory(col -> new AppointmentTableCell<>(this));
        satColumn.setCellFactory(col -> new AppointmentTableCell<>(this));
        sunColumn.setCellFactory(col -> new AppointmentTableCell<>(this));


        loadCustomer();
        loadAppointments();
        loadWeekAppointments();

        if (appointmentTable.getItems().isEmpty()) {
            System.out.println("Creating time slots...");
            for (int hour = 9; hour <= 21; hour++) {
                String time = String.format("%02d:00", hour);
                appointment newAppointment = new appointment(time, "", "", "", "", "", "", "", 0.);
                timeSlots.add(newAppointment);
                appointmentTable.getItems().add(newAppointment);
                timeChoiceBox.getItems().add(time);
            }
        } else {
            System.out.println("Appointments already loaded, skipping time slot creation.");
            timeSlots.clear();
            timeChoiceBox.getItems().clear();
            for (appointment app : appointmentTable.getItems()) {
                timeSlots.add(app);
                if (!timeChoiceBox.getItems().contains(app.getTime())){
                    timeChoiceBox.getItems().add(app.getTime());
                }
            }
        }
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));
        priceColumn.setCellFactory(col -> new TableCell<appointment, Double>(){
            @Override
                    protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });
        dayChoiceBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        populatecustomer();
        addCustBtn.setOnAction((actionEvent -> addCustomer()));
        addAppbtn.setOnAction(actionEvent -> addAppointment());
        backbtn.setOnAction(ActionEvent -> navigateWeek(-1));
        forwardbtn.setOnAction(ActionEvent -> navigateWeek(+1));




    }

    public class AppointmentTableCell<S> extends TableCell<S, String>{
        private final bookingcontroller controller;
        public AppointmentTableCell(bookingcontroller controller){
            this.controller = controller;
        }
    @Override
    protected void updateItem(String item, boolean empty){
        super.updateItem(item, empty);
        if (empty || item == null || item.isEmpty()){
            setText(null);
            setOnMouseClicked(null);
        } else {
            setText(item);
            setOnMouseClicked(event -> {
                if (event.getClickCount() == 2){
                    appointment app = (appointment) getTableRow().getItem();
                    String day = getTableColumn().getText();
                    showContextMenu(app, day, event);
                }
            });
        }
    }
    private void showContextMenu(appointment app, String day, MouseEvent event){
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("Edit");
            MenuItem deleteItem = new MenuItem("delete");

            editItem.setOnAction(e -> controller.editAppointment(app, day));
            deleteItem.setOnAction(e -> controller.deleteAppointment(app, day));

            contextMenu.getItems().addAll(editItem, deleteItem);
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
    }
}
    public void editAppointment(appointment app, String day){
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("edit appointment Price");
        dialog.setHeaderText("Edit price for appointment on " + day + " at " + app.getTime());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField priceField = new TextField(String.format("%.2f", app.getPrice()));
        grid.add(new Label("Price:"), 0,0);
        grid.add(priceField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return Double.parseDouble(priceField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
            });
        Optional<Double> result = dialog.showAndWait();

        result.ifPresent(newPrice ->{
            app.setPrice(newPrice);
            appointmentTable.refresh();
            saveAppointments();
        });
    }

    public void deleteAppointment(appointment app, String day){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("delete Appointment");
        alert.setHeaderText("delete appointment for " + app.getTime() + " on " + day );
        alert.setContentText("U sure u wanna delete this bro?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            setAppointmentForDay(app, day, "");
            appointmentTable.refresh();
            saveAppointments();
        }

    }

    private String getAppointmentForDay(appointment app, String day) {
        switch (day) {
            case "Monday": return app.getMonday();
            case "Tuesday": return app.getTuesday();
            case "Wednesday": return app.getWednesday();
            case "Thursday": return app.getThursday();
            case "Friday": return app.getFriday();
            case "Saturday": return app.getSaturday();
            case "Sunday": return app.getSunday();
            default: return "";
        }
    }

    private void setAppointmentForDay(appointment app, String day, String value) {
        switch (day) {
            case "Monday": app.setMonday(value); break;
            case "Tuesday": app.setTuesday(value); break;
            case "Wednesday": app.setWednesday(value); break;
            case "Thursday": app.setThursday(value); break;
            case "Friday": app.setFriday(value); break;
            case "Saturday": app.setSaturday(value); break;
            case "Sunday": app.setSunday(value); break;
        }
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
        double price = Double.parseDouble(priceTextBox.getText());
        appointment newAppointment = new appointment(selectedTime, "", "", "", "", "", "", "", price);

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
                break;
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
    private String[] formatWeekDateRange(LocalDate date){
        LocalDate startOfWeek = date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM");
        String startDate = startOfWeek.format(formatter);
        String endDate = endOfWeek.format(formatter);

        return new String[]{startDate, endDate};
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

        String[] dateRange = formatWeekDateRange(currentWeekDate);
        weekDisplay.setText(dateRange[0]);
        weekDisplay2.setText(dateRange[1]);

        System.out.println("Appointments loaded for week: " + weekId);
    }
    private void initializeTimeSlots(ArrayList<appointment> weekAppointments) {
        for (int hour = 9; hour <= 21; hour++) {
            String time = String.format("%02d:00", hour);
            weekAppointments.add(new appointment(time, "", "", "", "", "", "", "", 0.));
        }
    }

}
