package com.yacer.downlox.controllers;

import com.yacer.downlox.utils.ThemeUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TestController implements Initializable {
    @FXML public VBox baseVBox;
    @FXML Button btn_addURL;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(()-> {
            stage = (Stage) baseVBox.getScene().getWindow();
            if(stage!= null) ThemeUtils.applyTheme(stage.getScene());
        });

    }
}