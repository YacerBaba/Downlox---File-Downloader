package com.yacer.downlox.controllers;


import com.yacer.downlox.daos.DownloadDAO;
import com.yacer.downlox.enums.CustomSVG;
import com.yacer.downlox.models.Download;
import com.yacer.downlox.services.AddDownloadService;
import com.yacer.downlox.services.GetDownloadsService;
import com.yacer.downlox.utils.DownloadUtils;
import com.yacer.downlox.utils.ThemeUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static com.yacer.downlox.utils.DownloadUtils.*;


public class HomeController implements Initializable {
    private Stage stage;
    @FXML
    public VBox baseVBox;

    @FXML
    private Button btn_about;

    @FXML
    private Button btn_addURL;

    @FXML
    private Button btn_deleteAll;

    @FXML
    private Button btn_pauseAll;

    @FXML
    private Button btn_playAll;

    @FXML
    private Button btn_search;

    @FXML
    public ListView<HBox> lv_downloads;

    @FXML
    private TextField tf_search;

    AddDownloadService addDownloadService = new AddDownloadService();
    GetDownloadsService getDownloadsService = new GetDownloadsService();
    ObservableList<HBox> itemsViewObservableList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        getDownloadsObservableList().addListener((ListChangeListener.Change<? extends Download> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Download item : change.getAddedSubList()) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/item.fxml"));
                        HBox itemHbox;
                        try {
                            itemHbox = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        ItemController itemController = loader.getController();
                        itemController.setDownload(item);
                        itemController.setHomeController(this);
                        itemsViewObservableList.add(itemHbox);
                    }
                }
                if (change.wasRemoved()) {
                    for (Download item : change.getRemoved()) {
                        getDownloadsObservableList().removeIf(download -> Objects.equals(download.getId(), item.getId()));
                    }
                }
            }
        });
        lv_downloads.setItems(itemsViewObservableList);

        CompletableFuture.runAsync(() -> {
            var downloads = getDownloadsService.all();
            DownloadUtils.addItems(downloads);
        });

        // Auto-completion feature commented because it has errors with controls.fx
//        var autoCompletion = TextFields.bindAutoCompletion(tf_search, request -> {
//            return getDownloadsService.findByKeyword(request.getUserText());
//        });
//        autoCompletion.setPrefWidth(tf_search.getPrefWidth());

        tf_search.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                String word = tf_search.getText();
                if (word.isEmpty()) return;
                var downloads = getDownloadsService.findByKeyword(word);
                itemsViewObservableList.clear();
                DownloadUtils.addItems(downloads);
            }
        });

        btn_addURL.setOnAction(event -> {
            try {
                addNewURL();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        btn_playAll.setOnMouseClicked(event -> {

        });

        btn_pauseAll.setOnMouseClicked(event -> {

        });

        btn_deleteAll.setOnMouseClicked(event -> {

        });
    }

    public void addNewURL() throws IOException {
        Download newDownload = openAddURLView();
        if (newDownload != null) addDownload(newDownload);
    }

    private Download openAddURLView() throws IOException {
        Download item = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_url_view.fxml"));
        VBox downloadGP = loader.load();
        AddUrlController addURLController = loader.getController();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setHeaderText("New Download");

        SVGPath svg = new SVGPath();
        svg.setContent(CustomSVG.DOWNLOAD.getSVGPath());
        svg.setFill(Color.valueOf("#1687d3"));
        dialog.setGraphic(svg);

        DialogPane pane = dialog.getDialogPane();
        pane.setContent(downloadGP);
        pane.getStylesheets().add(getClass().getResource("/css/Dialog.css").toExternalForm());
        pane.getScene().setFill(Color.TRANSPARENT);
        ThemeUtils.applyTheme(pane.getScene());

        ButtonType downloadButtonType = new ButtonType("Download", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(cancelButtonType, downloadButtonType);
        pane.lookupButton(downloadButtonType).getStyleClass().add("default-button");
        pane.lookupButton(cancelButtonType).getStyleClass().add("cancel-button");

//        pane.lookupButton(downloadButtonType).setDisable(true);
        addURLController.getIsValidDirectory().addListener((obs, oldVal, newVal) -> {
            System.out.println("valid directory : " + newVal);
            pane.lookupButton(downloadButtonType).setDisable(!newVal);
        });

        Optional<ButtonType> result = dialog.showAndWait();
        var isValidURL = addURLController.getIsValidDirectory().get();
        var isValidDir = addURLController.getIsValidURL().get();
        System.out.println("dialog closed");
        DownloadDAO itemDao = null;
        if (result.isPresent() && result.get().equals(downloadButtonType) && isValidDir && isValidURL) {
            var url = addURLController.tf_url.getText();
            var directory = addURLController.tf_directory.getText();
            var fileName = addURLController.tf_fileName.getText();
            var fileSize = addURLController.getFileSize();
            System.out.println("File size :" + fileSize);
            var fileType = addURLController.getFileType();
            item = addDownloadService.execute(fileName, fileType, fileSize, url, directory, false);
            System.out.println(item);
        }
        return item;
    }


}