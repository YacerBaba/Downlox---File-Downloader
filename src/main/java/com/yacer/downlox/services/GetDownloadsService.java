package com.yacer.downlox.services;

import com.yacer.downlox.daos.DownloadDAO;
import com.yacer.downlox.models.Download;
import com.yacer.downlox.utils.EMHelper;

import java.util.List;

public class GetDownloadsService {


    public List<Download> all() {
        try {
            var dao = new DownloadDAO(EMHelper.getEntityManager());
            return dao.findAll();
        } finally {
            EMHelper.closeEntityManager();
        }
    }

    public List<Download> findByKeyword(String keyword) {
        try {
            var dao = new DownloadDAO(EMHelper.getEntityManager());
            return dao.findByKeyword(keyword);
        } finally {
            EMHelper.closeEntityManager();
        }
    }


}
