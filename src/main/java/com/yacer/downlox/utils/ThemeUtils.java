package com.yacer.downlox.utils;

import com.jthemedetecor.OsThemeDetector;
import javafx.application.Platform;
import javafx.scene.Scene;

import java.util.function.Consumer;

public class ThemeUtils {

    public static void applyTheme(Scene scene) {
        final OsThemeDetector detector = OsThemeDetector.getDetector();
        Consumer<Boolean> darkThemeListener = isDark -> {
            Platform.runLater(() -> {
                final String LIGHT_THEME_FILE = "/css/lightTheme.css";
                final String DARK_THEME_FILE = "/css/darkTheme.css";
                scene.getRoot().getStylesheets().add(ThemeUtils.class.getResource(isDark ? DARK_THEME_FILE : LIGHT_THEME_FILE).toExternalForm());
                scene.getRoot().getStylesheets().removeIf(stylesheet-> stylesheet.contains(isDark ? LIGHT_THEME_FILE : DARK_THEME_FILE));
            });
        };

        darkThemeListener.accept(detector.isDark());
        detector.registerListener(darkThemeListener);
    }
}

