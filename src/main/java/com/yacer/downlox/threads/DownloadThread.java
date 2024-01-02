package com.yacer.downlox.threads;

import com.yacer.downlox.callbacks.OnCompleteListener;
import com.yacer.downlox.callbacks.OnErrorCallback;
import com.yacer.downlox.callbacks.OnPauseListener;
import com.yacer.downlox.callbacks.OnProgressListener;
import com.yacer.downlox.models.Download;
import com.yacer.downlox.services.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadThread extends Thread {
    static AtomicInteger atomicInteger = new AtomicInteger(0);

    private Download downloadItem;
    private CompleteDownloadService completeDownloadService = new CompleteDownloadService();
    private PauseDownloadService pauseDownloadService = new PauseDownloadService();
    private boolean inProgress = true;
    private OnProgressListener listener;
    private OnPauseListener onPauseListener;
    private OnCompleteListener onCompleteListener;
    private OnErrorCallback onErrorCallback;
    private CyclicBarrier barrier = new CyclicBarrier(2);

    public DownloadThread(Download downloadItem, OnProgressListener listener,
                          OnPauseListener onPauseListener, OnCompleteListener onCompleteListener,
                          OnErrorCallback onErrorCallback) {
        this.downloadItem = downloadItem;
        this.listener = listener;
        this.onPauseListener = onPauseListener;
        this.onCompleteListener = onCompleteListener;
        this.onErrorCallback = onErrorCallback;

        atomicInteger.incrementAndGet();
    }

    @Override
    public void run() {
        try {
            URL url = new URL(downloadItem.getDownloadUrl());
            downloadFileFromUrl(url);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void downloadFileFromUrl(URL url) throws IOException {

        Timer networkCheckTimer = new Timer();
        networkCheckTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isNetworkAvailable()) {
                    System.out.println("network connection lost");
                    Platform.runLater(() -> {
                        onErrorCallback.execute();
                    });
                    networkCheckTimer.cancel();
                    kill();
                }
            }
        }, 0, 2000);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        System.out.println("Download started : " + downloadItem.getDownloadUrl());
        try {
            System.out.println(currentThread().getName() + " is running");
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(
                    downloadItem.getDestination_path() + "/" + downloadItem.getTitle() + "." + downloadItem.getType());
            FileChannel fileChannel = fileOutputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(100);
            Long downloadedBytes = downloadItem.getDownloadedBytes();
            int nbBytesRead = readableByteChannel.read(buffer);
            System.out.println("nb bytes read  = " + nbBytesRead);
            while (nbBytesRead != -1) {
                if (inProgress) {
                    buffer.flip(); // switch to reading mode
                    fileChannel.write(buffer);
                    buffer.clear();
                    downloadedBytes += nbBytesRead;
//                    System.out.println("downloaded bytes :" + downloadedBytes);
                    double percentage = (((double) downloadedBytes) / ((double) downloadItem.getSize())) * 100;
//                    System.out.println("percentage : " + percentage);
                    Platform.runLater(() -> {
                        listener.onUpdate(percentage);
                    });
                    downloadItem.setDownloadedBytes(downloadedBytes);
                    nbBytesRead = readableByteChannel.read(buffer); // read again
                } else {
                    System.out.println("In progress : " + inProgress);
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            completeDownloadService.execute(downloadItem.getId());
            Platform.runLater(() -> {
                onCompleteListener.onComplete();
                atomicInteger.decrementAndGet();
                if (checkForDownloadsCompletion()) {
                    showDownloadsCompletionAlert();
                }
                System.out.println("downloaded successfully");
            });
            fileOutputStream.close();
        } finally {
            connection.disconnect();
        }
    }

    private boolean isNetworkAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public void kill() {
        this.interrupt();
    }

    public void pauseDownload() {
        onPauseListener.onPause();
        System.out.println("download paused by the user");
        inProgress = false;
    }

    public void resumeDownload() {
        try {
            inProgress = true;
            barrier.await();
            barrier.reset();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkForDownloadsCompletion() {
        return atomicInteger.get() == 0;
    }

    private void showDownloadsCompletionAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Downloads Completed");
        alert.setHeaderText(null);
        alert.setContentText("All downloads have been completed successfully!");
        alert.showAndWait();
    }
}
