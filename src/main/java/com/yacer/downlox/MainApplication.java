package com.yacer.downlox;

import com.yacer.downlox.utils.ThemeUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Downlox");
        stage.getIcons().add(new Image("logo.png"));
        stage.setScene(scene);
        stage.setResizable(false);
        ThemeUtils.applyTheme(stage.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}