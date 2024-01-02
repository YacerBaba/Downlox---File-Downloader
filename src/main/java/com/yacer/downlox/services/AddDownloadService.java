package com.yacer.downlox.services;

import com.yacer.downlox.daos.DownloadDAO;
import com.yacer.downlox.enums.FileType;
import com.yacer.downlox.enums.Status;
import com.yacer.downlox.models.Download;
import com.yacer.downlox.utils.EMHelper;

public class AddDownloadService {


    public Download execute(String title, FileType type, Long size, String url, String dir, Boolean isResumable) {
        try {
            var dao = new DownloadDAO(EMHelper.getEntityManager());
            Download download =
                    new Download(title, type, size, 0l,
                            Status.IN_PROGRESS, url,
                            dir, isResumable);
            EMHelper.beginTransaction();
            dao.addDownload(download);
            EMHelper.commit();
            return download;
        } finally {
            EMHelper.closeEntityManager();
        }
    }

}
