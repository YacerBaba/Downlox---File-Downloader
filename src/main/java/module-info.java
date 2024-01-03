module com.yacer.downlox {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires mysql.connector.j;
    requires java.persistence;
    requires org.eclipse.persistence.jpa;
    requires java.sql;
    requires org.apache.commons.lang3;
    requires com.jthemedetector;
    requires java.desktop;
    requires org.slf4j;
    opens com.yacer.downlox to javafx.fxml;
    exports com.yacer.downlox;
    opens com.yacer.downlox.models;
    exports com.yacer.downlox.controllers;
    opens com.yacer.downlox.controllers to javafx.fxml;
}