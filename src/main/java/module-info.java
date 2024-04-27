module com.example.nnknotepad {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.nnknotepad to javafx.fxml;
    exports com.example.nnknotepad;
}