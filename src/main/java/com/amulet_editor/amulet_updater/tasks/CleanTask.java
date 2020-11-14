package com.amulet_editor.amulet_updater.tasks;

import com.amulet_editor.amulet_updater.utils.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CleanTask extends AbstractTask {

    @Override
    public boolean runTask(String[] args, Map<String, Object> environment) {
        File currentBackupPath = (File) environment.get(Constants.CURRENT_BACKUP_PATH);

        try {
            FileUtils.deleteDirectory(currentBackupPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getTaskID() {
        return "clean";
    }
}
