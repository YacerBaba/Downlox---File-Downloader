package com.yacer.downlox.services;

import com.yacer.downlox.daos.DownloadDAO;
import com.yacer.downlox.utils.EMHelper;

public class ResumeDownloadService {

    public void execute(Integer id) {
        try {
            var dao = new DownloadDAO(EMHelper.getEntityManager());
            var download = dao.findById(id);
            EMHelper.beginTransaction();
            dao.resumeDownload(download);
            EMHelper.commit();
        } finally {
            EMHelper.closeEntityManager();
        }
    }
}
