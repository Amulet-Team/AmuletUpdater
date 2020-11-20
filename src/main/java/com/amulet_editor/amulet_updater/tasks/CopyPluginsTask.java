package com.amulet_editor.amulet_updater.tasks;

import com.amulet_editor.amulet_updater.utils.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class CopyPluginsTask extends AbstractTask {

    @Override
    public boolean runTask(String[] args, Map<String, Object> environment) {
        File currentBackupPth = (File) environment.get(Constants.CURRENT_BACKUP_PATH);
        File latestPath = (File) environment.get(Constants.LATEST_PATH);
        File workingDirectory = (File) environment.get(Constants.WORKING_DIRECTORY);

        Path sourcePluginPath = Paths.get(currentBackupPth.toString(), args[1]);
        Path destPluginPath = Paths.get(latestPath.toString(), args[2]);

        if (!sourcePluginPath.toFile().exists()) {
            return false;
        }

        try {
            Files.createDirectories(destPluginPath);
            FileUtils.copyDirectory(sourcePluginPath.toFile(), destPluginPath.toFile());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            this.reportError(ioe, workingDirectory);
            return true;
        }
        return true;
    }

    @Override
    public String getTaskID() { return "copy_plugins"; }
}
