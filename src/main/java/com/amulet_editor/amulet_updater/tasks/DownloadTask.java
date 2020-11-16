package com.amulet_editor.amulet_updater.tasks;

import com.amulet_editor.amulet_updater.utils.Constants;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.Map;

public class DownloadTask extends AbstractTask {

    @Override
    public String getTaskID() {
        return "download";
    }

    @Override
    public boolean runTask(String[] args, Map<String, Object> environment) {
        String target = (String) environment.get(Constants.TARGET_VERSION);
        try {
            URL url = new URL("https://github.com/Amulet-Team/Amulet-Map-Editor/releases/download/v"+ target + "/Amulet-v0.7.2.4-windows.zip");
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream("update.zip");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();

        } catch (IOException mue) {
            mue.printStackTrace();
        }

        File workingDirectory = (File) environment.get(Constants.WORKING_DIRECTORY);
        File tempDir = Paths.get(workingDirectory.getAbsolutePath(), "tmp").toFile();

        ZipFile newReleaseZip = new ZipFile("update.zip");
        try {
            newReleaseZip.extractAll(tempDir.getAbsolutePath());
            environment.put(Constants.LATEST_PATH, Paths.get(tempDir.getAbsolutePath(), "Amulet").toFile());
            return true;
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return false;
    }
}
