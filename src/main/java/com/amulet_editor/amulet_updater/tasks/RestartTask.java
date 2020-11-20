package com.amulet_editor.amulet_updater.tasks;

import com.amulet_editor.amulet_updater.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class RestartTask extends AbstractTask {

    private static class AmuletRunnable implements Runnable {
        private final String path;

        AmuletRunnable(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            try {
                Runtime.getRuntime().exec(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean runTask(String[] args, Map<String, Object> environment) {
        File workingDirectory = (File) environment.get(Constants.WORKING_DIRECTORY);

        String amuletPath = Paths.get(workingDirectory.toString(), args[1]).toString();

        Thread t = new Thread(new AmuletRunnable(amuletPath));
        t.start();
        environment.put("amulet_thread", t);
        return true;
    }

    @Override
    public String getTaskID() { return "restart"; }
}
