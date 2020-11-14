package com.amulet_editor.amulet_updater.tasks;

import com.amulet_editor.amulet_updater.utils.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class CopyConfigTask extends AbstractTask {

    @Override
    public String getTaskID() {
        return "copy_config";
    }

    @Override
    public boolean runTask(String[] args, Map<String, Object> environment) {
        File currentBackupPath = (File) environment.get(Constants.CURRENT_BACKUP_PATH);
        File latestPath = (File) environment.get(Constants.LATEST_PATH);

        Path sourceConfigPath = Paths.get(currentBackupPath.toString(), args[1]);
        Path destConfigPath = Paths.get(latestPath.toString(), args[2]);

        if (!sourceConfigPath.toFile().exists()) {
            return true;
        }

        try {
            Files.createDirectories(destConfigPath);
            FileUtils.copyDirectory(sourceConfigPath.toFile(), destConfigPath.toFile());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return false;
    }
}
