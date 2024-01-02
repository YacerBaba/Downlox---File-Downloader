package com.yacer.downlox.daos;

import com.yacer.downlox.models.Download;
import com.yacer.downlox.enums.Status;
import com.yacer.downlox.utils.DbUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class DownloadDAO {
    private EntityManager manager;

    public DownloadDAO(EntityManager manager) {
        this.manager = manager;
    }

    public Download findById(Integer id) {
        return manager.find(Download.class, id);
    }

    public List<Download> findAll() {
        TypedQuery<Download> typedQuery = manager.createNamedQuery("Download.findAll", Download.class);
        typedQuery.setParameter("deletedStatus",Status.DELETED);
        return typedQuery.getResultList();
    }

    public List<Download> findDownloadsByKeyword(String keyword) {
        TypedQuery<Download> typedQuery = manager.createNamedQuery("Download.findByKeyword", Download.class);
        typedQuery.setParameter("keyword", keyword);
        return typedQuery.getResultList();
    }

    public Download addDownload(Download download) {
        manager.persist(download);
        return download;
    }

    public void updateDownloadProgress(Download download, Long downloadedBytes) {
        download.setDownloadedBytes(downloadedBytes);
    }

    public void cancelDownload(Download download) {
        download.setStatus(Status.CANCELED);
    }

    public void deleteDownload(Download download) {
        download.setStatus(Status.DELETED);
    }


    public void pauseDownload(Download download) {
        download.setStatus(Status.PAUSED);
    }

    public void failDownload(Download download) {
        download.setStatus(Status.FAILED);
    }

    public void completeDownload(Download download) {
        download.setStatus(Status.COMPLETED);
    }


    public void resumeDownload(Download download) {
        download.setStatus(Status.IN_PROGRESS);
    }
}
