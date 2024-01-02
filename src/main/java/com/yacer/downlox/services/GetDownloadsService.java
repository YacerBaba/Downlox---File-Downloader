package com.yacer.downlox.services;

import com.yacer.downlox.daos.DownloadDAO;
import com.yacer.downlox.models.Download;
import com.yacer.downlox.utils.EMHelper;

import java.util.List;

public class GetDownloadsService {


    public List<Download> execute() {
        try {
            var dao = new DownloadDAO(EMHelper.getEntityManager());
            return dao.findAll();
        } finally {
            EMHelper.closeEntityManager();
        }
    }

//    public List<Download> getByKeyword(String keyword) {
//        return dao.findDownloadsByKeyword(keyword);
//    }


}
