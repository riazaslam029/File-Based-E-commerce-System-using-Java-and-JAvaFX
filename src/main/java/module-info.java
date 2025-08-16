module com.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires jdk.compiler;
    requires java.desktop;

    opens com.example.javafx to javafx.fxml;
    exports com.example.javafx;
}