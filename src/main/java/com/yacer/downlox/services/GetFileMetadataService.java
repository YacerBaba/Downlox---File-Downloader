package com.yacer.downlox.services;

import com.yacer.downlox.daos.DownloadDAO;
import com.yacer.downlox.models.FileMetaData;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.yacer.downlox.utils.DbUtils.manager;

public class GetFileMetadataService {

    public FileMetaData execute(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            long file_length = connection.getContentLength();
            String content_type = connection.getContentType();
            String file_name = Paths.get(fileUrl).getFileName().toString();
            connection.disconnect();
            return new FileMetaData(file_length, content_type, file_name);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
