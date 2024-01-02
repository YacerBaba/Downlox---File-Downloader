package com.yacer.downlox.services;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class OpenFileLocationService {
    public void execute(String uri) {
        File file = new File(uri);
//        EnumOS os = getOs();
        System.out.println("Supported : " + Desktop.isDesktopSupported());
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            System.out.println("We're trying to open this file");
            Desktop.getDesktop().browseFileDirectory(file);
        } else {
            System.out.println("Desktop operations are not supported on this platform");
        }
    }
}
