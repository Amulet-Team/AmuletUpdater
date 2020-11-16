package com.amulet_editor.amulet_updater;

import com.amulet_editor.amulet_updater.tasks.*;
import com.amulet_editor.amulet_updater.ui.UpdateUI;
import com.amulet_editor.amulet_updater.utils.Constants;
import com.amulet_editor.amulet_updater.utils.GithubAPI;
import com.jezhumble.javasysmon.JavaSysMon;
import org.apache.commons.cli.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final JavaSysMon SYS_MON = new JavaSysMon();

    private static final String[] defaultUpdateProcess = new String[] {
            "backup",
            "download",
            "copy_config config config",
            "copy_plugins plugins plugins",
            "finish_install",
            "clean",
            "restart amulet_app.exe"
    };

    private static final Map<String, Class<? extends AbstractTask>> taskClasses = new HashMap<String, Class<? extends AbstractTask>>() {{
        put("backup", BackupTask.class);
        put("download", DownloadTask.class);
        put("copy_config", CopyConfigTask.class);
        put("copy_plugins", CopyPluginsTask.class);
        put("finish_install", FinishInstallTask.class);
        put("clean", CleanTask.class);
        put("restart", RestartTask.class);
    }};

    public static void main(String[] args) {
        Options options = new Options();

        options.addOption("wd", true, "The desired working directory");
        options.addOption("pid", true, "The PID to kill");
        options.addOption("target_version", true, "The version");
        options.addOption("current_version", true, "The currently installed version");

        CommandLineParser parser = new DefaultParser();

        String workingDirectory = null;
        String currentVersion = null;
        String targetVersion = null;
        String sPid = null;

        try {
            CommandLine cli = parser.parse(options, args);
            workingDirectory = cli.getOptionValue("wd");
            currentVersion = cli.getOptionValue("current_version");
            targetVersion = cli.getOptionValue("target_version", "latest");
            if (cli.hasOption("pid")) {
                sPid = cli.getOptionValue("pid");
            }
        } catch (ParseException exp) {
            exp.printStackTrace();
        }

        if (workingDirectory == null) {
            return;
        }

        if (targetVersion.equalsIgnoreCase("latest")) {
            String version = GithubAPI.getLatestRelease();
            if (version == null) {
                // Error
                return;
            }
            targetVersion = version;
        }

        Map<String, Object> environment = new HashMap<>();
        environment.put(Constants.WORKING_DIRECTORY, new File(workingDirectory));
        environment.put(Constants.CURRENT_VERSION, currentVersion);
        environment.put(Constants.TARGET_VERSION, targetVersion);

        if (sPid != null) {
            int pid = Integer.parseInt(sPid);

            SYS_MON.killProcess(pid);
        }

        UpdateUI ui = new UpdateUI(currentVersion, targetVersion, defaultUpdateProcess.length);

        for (String cmd : defaultUpdateProcess) {
            String[] cmdArgs = cmd.split(" ");
            try {
                Class<? extends AbstractTask> targetClass = taskClasses.get(cmdArgs[0]);
                if (targetClass != null) {
                    AbstractTask task = targetClass.getConstructor().newInstance();
                    ui.updateStep(task.getTaskID());
                    System.out.print(task.getTaskID() + "(" + Arrays.toString(cmdArgs) + ")");
                    System.out.println(" => " + task.runTask(cmdArgs, environment));
                    ui.incrementStep();
                } else {
                    System.err.println("Couldn't find class for task \"" + cmdArgs[0] + "\"");
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (environment.containsKey("amulet_thread")) {
            ui.close();
            Thread t = (Thread) environment.get("amulet_thread");
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*
        //GithubAPI.getLatestRelease();
        //ITask task = new DownloadTask();
        //task.runTask();
        ITask backupTask = new BackupTask();
        backupTask.runTask(new String[0], environment);
         */
    }
}
