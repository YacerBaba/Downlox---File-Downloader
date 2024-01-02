package com.yacer.downlox.utils;

import com.yacer.downlox.models.Download;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class DownloadUtils {
    public static String DEFAULT_DESTINATION_PATH = "/home/yacer/DownloxFiles/";
    public static ObservableList<Download> getDownloadsObservableList() {
        return downloadsObservableList;
    }
    public static void addItems(List<Download> items) {
        downloadsObservableList.addAll(items);
    }

    public static void addDownload(Download download){
        downloadsObservableList.add(download);
    }
    private static ObservableList<Download> downloadsObservableList = FXCollections.observableArrayList();

}
