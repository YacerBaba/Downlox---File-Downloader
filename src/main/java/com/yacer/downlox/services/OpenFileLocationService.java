package com.yacer.downlox.services;

import com.yacer.downlox.utils.DesktopUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class OpenFileLocationService {
    public void execute(String uri) {
        File file = new File(uri);
        try {
            DesktopUtils.browse(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
