package com.yacer.downlox.controllers;


import com.yacer.downlox.enums.FileType;
import com.yacer.downlox.enums.SizeUnit;
import com.yacer.downlox.services.FormatFileSizeService;
import com.yacer.downlox.services.GetFileMetadataService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class AddUrlController implements Initializable {

    private BooleanProperty isValidURL = new SimpleBooleanProperty(false);
    private BooleanProperty isValidDirectory = new SimpleBooleanProperty(false);
    private GetFileMetadataService fileMetadataService = new GetFileMetadataService();

    @FXML
    private Stage stage;
    @FXML
    protected TextField tf_url, tf_directory;
    @FXML
    protected Label lb_fileSize;
    @FXML
    private ImageView iv_fileType;
    @FXML
    protected TextField tf_fileName;
    @FXML
    private Button btn_chooseDirectory;

    private FormatFileSizeService formatFileSizeService = new FormatFileSizeService();
    private FileType fileType;
    private Long fileSize;

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isValidURL.bind(Bindings.createBooleanBinding(
                () -> validateURL(tf_url.getText()), tf_url.textProperty())
        );

        isValidDirectory.bind(Bindings.createBooleanBinding(
                () -> validateDirectory(tf_directory.getText()), tf_directory.textProperty()
        ));

        tf_url.textProperty().addListener((observableValue, oldValue, newValue) -> {
            System.out.println("We are here nowwww");
            if (validateURL(newValue)) {
                CompletableFuture.runAsync(() -> {
                    var metadata = fileMetadataService.execute(newValue);
                    System.out.println("Content type : " + metadata.getContent_type());
                    fileType = getFileTypeFromContentType(metadata.getContent_type());
                    fileSize = metadata.getFile_length();
                    System.out.println("File size : " + fileSize);
                    System.out.println("File type :" + fileType);
                    if (metadata.getFileName() != null) {
                        Platform.runLater(() -> {
                            if (fileType != null)
                                iv_fileType.setImage(new Image(getClass().getResourceAsStream(fileType.getIconPath())));
                            tf_fileName.setText(metadata.getFileName());
                            lb_fileSize.setText(formatFileSizeService.execute(fileSize));
                        });
                    }
                });
            } else
                System.out.println("Invalid URL");
        });

        btn_chooseDirectory.setOnAction(event -> {
            openDirectoryChooser();
        });
    }


    public void openDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            tf_directory.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private boolean validateURL(String fileUrl) {
        if (fileUrl.isEmpty()) return false;
        try {
            URL url = new URL(fileUrl);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    private boolean validateDirectory(String directory) {
        Path path = Paths.get(directory);
        return Files.isDirectory(path) && Files.exists(path);
    }

    private FileType getFileTypeFromContentType(String contentType) {
        var regex = contentType.split("/");
        var content = regex[0].toLowerCase();
        var type = regex[1].toLowerCase();
        if (type.equals("jpeg")) {
            return FileType.JPEG;
        }
        if (type.equals("png"))
            return FileType.PNG;
        if (type.equals("plain"))
            return FileType.TXT;
        if (type.equals("mp4")) {
            return FileType.MP4;
        }
        if (content.equals("audio") && type.equals("mpeg"))
            return FileType.MP3;
        if (content.equals("application")) {
            if (type.equals("rar") || type.equals("vnd.rar") || type.equals("x-rar-compressed") || type.equals("otect-stream"))
                return FileType.RAR;
            if (type.equals("zip") || type.equals("x-zip-compressed") || type.equals("gzip"))
                return FileType.ZIP;
        }
        return null;
    }


    public BooleanProperty getIsValidDirectory() {
        return isValidDirectory;
    }

    public BooleanProperty getIsValidURL() {
        return isValidURL;
    }


}
