package com.amulet_editor.amulet_updater.tasks;

import com.amulet_editor.amulet_updater.utils.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FinishInstallTask extends AbstractTask {

    @Override
    public boolean runTask(String[] args, Map<String, Object> environment) {
        File workingDirectory = (File) environment.get(Constants.WORKING_DIRECTORY);
        File latestPath = (File) environment.get(Constants.LATEST_PATH);

        try {
            FileUtils.copyDirectory(latestPath, workingDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getTaskID() {
        return "finish_install";
    }
}
