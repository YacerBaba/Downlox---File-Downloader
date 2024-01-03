package com.yacer.downlox.controllers;


import com.yacer.downlox.enums.CustomSVG;
import com.yacer.downlox.enums.Status;
import com.yacer.downlox.models.Download;
import com.yacer.downlox.services.*;
import com.yacer.downlox.threads.DownloadThread;
import com.yacer.downlox.utils.DownloadUtils;
import com.yacer.downlox.utils.ThemeUtils;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class ItemController implements Initializable {
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    private HomeController homeController;
    @FXML
    private HBox itemHB;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_openLocation;
    @FXML
    private Button btn_play;
    @FXML
    private ImageView iv_fileIcon;
    @FXML
    private Label lb_fileName;
    @FXML
    private Label lb_fileSIze;
    @FXML
    private Label lb_progress;
    @FXML
    private Label lb_sizeUnit;
    @FXML
    private Label lb_status;

    @FXML
    private ProgressBar pb_progressIndicator;

    private DownloadThread thread;


    private void deleteRow() {
        DownloadUtils.getDownloadsObservableList().remove(download);
        homeController.itemsViewObservableList.remove(itemHB);
        homeController.lv_downloads.refresh();
    }


    private Download download;
    private FormatFileSizeService formatFileSizeService = new FormatFileSizeService();
    private OpenFileLocationService fileLocationService = new OpenFileLocationService();
    private PauseDownloadService pauseDownloadService = new PauseDownloadService();
    private CompleteDownloadService completeDownloadService = new CompleteDownloadService();
    private ResumeDownloadService resumeDownloadService = new ResumeDownloadService();
    private CancelDownloadService cancelDownloadService = new CancelDownloadService();
    private DeleteDownloadService deleteDownloadService = new DeleteDownloadService();
    private FailDownloadService failDownloadService = new FailDownloadService();

    public void setDownload(Download download) {
        this.download = download;
        lb_fileName.setText(download.getTitle());
        var regex = formatFileSizeService.execute(download.getSize()).split(" ");
        var size = regex[0];
        var unit = regex[1];
        lb_fileSIze.setText(size);
        lb_sizeUnit.setText(unit);
        lb_status.setText(download.getStatus().toString());
        iv_fileIcon.setImage(new Image(getClass().getResourceAsStream(download.getType().getIconPath())));
        if (download.getStatus() == Status.IN_PROGRESS) {
            setupRowByDownloadStatus(Status.IN_PROGRESS);
            startDownload();
        } else
            setupRowByDownloadStatus(download.getStatus());

    }

    private Boolean showConfirmationBoxAndWait(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setHeaderText(msg + " Confirmation");
        alert.setContentText("Are you sure you want to " + msg + " this item?");

        DialogPane pane = alert.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/css/Dialog.css").toExternalForm());
        pane.getScene().setFill(Color.TRANSPARENT);
        ThemeUtils.applyTheme(pane.getScene());

        SVGPath svg = new SVGPath();
        svg.setContent(CustomSVG.WARNING.getSVGPath());
        svg.setFill(Color.valueOf("#ffcc00"));
        pane.setGraphic(new StackPane(svg));

        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.YES);
        ButtonType stopButtonType = new ButtonType(msg, ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(stopButtonType, cancelButtonType);
        pane.lookupButton(cancelButtonType).getStyleClass().add("default-button");
        pane.lookupButton(stopButtonType).getStyleClass().add("cancel-button");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().equals(stopButtonType);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btn_play.setOnAction(event -> {
            if (download.getStatus() == Status.IN_PROGRESS) {
                thread.pauseDownload();
                download.setStatus(Status.PAUSED);
                setupRowByDownloadStatus(Status.PAUSED);
            } else if (download.getStatus() == Status.PAUSED) {
                CompletableFuture.runAsync(() -> {
                    thread.resumeDownload();
                });
                resumeDownloadService.execute(download.getId());
                download.setStatus(Status.IN_PROGRESS);
                setupRowByDownloadStatus(Status.IN_PROGRESS);
            } else if (download.getStatus() == Status.CANCELED || download.getStatus() == Status.FAILED) {
                // restart button
                startDownload();
            }
        });

        btn_cancel.setOnAction(event -> {
            if (List.of(Status.COMPLETED, Status.FAILED, Status.CANCELED).contains(download.getStatus())) {
                // delete button
                var result = showConfirmationBoxAndWait("Delete");
                if (result) {
                    deleteDownloadService.execute(download.getId());
                    deleteRow();
                }
            } else {
                // cancel button
                var result = showConfirmationBoxAndWait("Stop");
                if (result) {
                    if (thread != null) {
                        thread.kill();
                        thread = null;
                    }
                    setupRowByDownloadStatus(Status.CANCELED);
                    cancelDownloadService.execute(download.getId());
                    download.setStatus(Status.CANCELED);
                }
            }
        });

        btn_openLocation.setOnAction(event -> {
            System.out.println("Open location clicked");
            fileLocationService.execute(download.getDestination_path() + "/");
        });
    }

    private void startDownload() {
        AtomicInteger atomicInteger = new AtomicInteger(8);
        atomicInteger.incrementAndGet();
        thread = new DownloadThread(download, percentage -> {
            pb_progressIndicator.setProgress(percentage / 100);
            lb_progress.setText(String.format("%.2f", percentage) + " %");
        }, () -> {
            setupRowByDownloadStatus(Status.PAUSED);
            pauseDownloadService.execute(download.getId());
            download.setStatus(Status.PAUSED);
        }, () -> {
            setupRowByDownloadStatus(Status.COMPLETED);
            completeDownloadService.execute(download.getId());
            download.setStatus(Status.COMPLETED);
        }, () -> {
            System.out.println("On error executed");
            setupRowByDownloadStatus(Status.FAILED);
            failDownloadService.execute(download.getId());
            download.setStatus(Status.FAILED);
        });
        thread.start();
        setupRowByDownloadStatus(Status.IN_PROGRESS);
        download.setStatus(Status.IN_PROGRESS);
    }

    private void setPlayButtonIcon(CustomSVG svg) {
        if (svg != null)
            ((SVGPath) (btn_play.getGraphic())).setContent(svg.getSVGPath());
        else
            btn_play.setVisible(false);
    }

    private void setCancelButtonIcon(CustomSVG svg) {
        ((SVGPath) (btn_cancel.getGraphic())).setContent(svg != null ? svg.getSVGPath() : null);
    }

    public void setupRowByDownloadStatus(Status status) {
        switch (status) {
            case COMPLETED -> {
                lb_status.setText("Completed");
                lb_progress.setText("100,00 %");
                pb_progressIndicator.setProgress(1.0);
                setPlayButtonIcon(null);
                setCancelButtonIcon(CustomSVG.DELETE);
            }
            case PAUSED -> {
                lb_status.setText("Paused");
                setPlayButtonIcon(CustomSVG.PLAY);
            }
            case IN_PROGRESS -> {
                lb_status.setText("In progress");
                setPlayButtonIcon(CustomSVG.PAUSE);
                setCancelButtonIcon(CustomSVG.STOP);
                itemHB.pseudoClassStateChanged(PseudoClass.getPseudoClass("stop"), false);
            }
            case FAILED, CANCELED -> {
                System.out.println("Failed/Canceled ui executed");
                lb_status.setText(status.toString());
                setPlayButtonIcon(CustomSVG.RESTART);
                setCancelButtonIcon(CustomSVG.DELETE);
                itemHB.pseudoClassStateChanged(PseudoClass.getPseudoClass("stop"), true);
            }
        }
    }
}
