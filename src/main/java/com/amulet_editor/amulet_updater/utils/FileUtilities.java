package com.amulet_editor.amulet_updater.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public final class FileUtilities {

    public static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            if (f.getName().equals("backups")) {
                return;
            }
            for (File g : Objects.requireNonNull(f.listFiles())) {
                delete(g);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete: " + f);
        }
    }
}
