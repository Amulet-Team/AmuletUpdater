package com.amulet_editor.amulet_updater.tasks;

import com.amulet_editor.amulet_updater.utils.Constants;
import com.amulet_editor.amulet_updater.utils.FileUtilities;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BackupTask extends AbstractTask {

    private static final List<String> ignoreFiles = Arrays.asList(
            "updater.jar",
            "backups",
            "updater-tmp"
    );

    private static class BackupFileFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            for (String ignored : ignoreFiles) {
                if (pathname.getAbsolutePath().contains(ignored)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public String getTaskID() {
        return "backup";
    }

    @Override
    public boolean runTask(String[] args, Map<String, Object> environment) {
        File workingDirectory = (File) environment.get(Constants.WORKING_DIRECTORY);
        String currentVersion = (String) environment.get(Constants.CURRENT_VERSION);

        Path backupsPath = Paths.get(workingDirectory.getAbsolutePath(), "backups");
        Path backupPath = Paths.get(backupsPath.toString(), "backup_" + currentVersion);
        try {
            Files.createDirectories(backupPath);
            BackupFileFilter filter = new BackupFileFilter();
            FileUtils.copyDirectory(workingDirectory, backupPath.toFile(), filter);

            try {
                for (File f : Objects.requireNonNull(workingDirectory.listFiles())) {
                    if (filter.accept(f)) {
                        FileUtilities.delete(f);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                this.reportError(ioe, workingDirectory);
                return false;
            }

            ZipParameters params = new ZipParameters();
            params.setCompressionMethod(CompressionMethod.STORE);

            ZipFile backupZip = new ZipFile(Paths.get(backupsPath.toString(), "backup_" + currentVersion + ".zip").toFile());
            if (!backupZip.getFile().exists()) {
                backupZip.addFolder(backupPath.toFile(), params);
            } else {
                System.err.println(backupZip.getFile().getName() + " already exists!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.reportError(e, workingDirectory);
            return false;
        }

        environment.put(Constants.CURRENT_BACKUP_PATH, backupPath.toFile());

        return true;
    }


}
