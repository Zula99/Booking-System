module com.example.bookingsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;

    opens com.example.bookingsystem to javafx.fxml;
    exports com.example.bookingsystem;
}