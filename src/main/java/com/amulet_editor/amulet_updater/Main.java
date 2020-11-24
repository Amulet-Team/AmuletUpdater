package com.amulet_editor.amulet_updater;

import com.amulet_editor.amulet_updater.tasks.*;
import com.amulet_editor.amulet_updater.ui.UpdateUI;
import com.amulet_editor.amulet_updater.utils.Constants;
import com.amulet_editor.amulet_updater.utils.GithubAPI;
import com.jezhumble.javasysmon.JavaSysMon;
import org.apache.commons.cli.*;

import javax.swing.*;
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
        options.addOption("install_beta", "Switch to install the beta version");
        options.addOption("current_version", true, "The currently installed version");

        CommandLineParser parser = new DefaultParser();

        String workingDirectory = null;
        String currentVersion = null;
        boolean installBeta = false;
        String sPid = null;

        try {
            CommandLine cli = parser.parse(options, args);
            workingDirectory = cli.getOptionValue("wd");
            currentVersion = cli.getOptionValue("current_version");
            installBeta = cli.hasOption("install_beta");
            if (cli.hasOption("pid")) {
                sPid = cli.getOptionValue("pid");
            }
        } catch (ParseException exp) {
            exp.printStackTrace();
        }

        if (workingDirectory == null) {
            return;
        }

        Map<String, Object> environment = new HashMap<>();
        environment.put(Constants.WORKING_DIRECTORY, new File(workingDirectory));
        environment.put(Constants.CURRENT_VERSION, currentVersion);

        GithubAPI.ReleaseInfo releaseInfo = GithubAPI.getLatestRelease(installBeta);
        if (releaseInfo == null) {
            JOptionPane.showMessageDialog(UpdateUI.getInstanceComponent(), "Couldn't find latest release, please wait 1 hour and try again", "An Error has Occured", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (releaseInfo.releaseVersion.equals(currentVersion)) {
            return;
        }

        environment.put(Constants.TARGET_VERSION_INFO, releaseInfo);

        if (sPid != null) {
            int pid = Integer.parseInt(sPid);

            SYS_MON.killProcess(pid);
        }

        UpdateUI ui = new UpdateUI(currentVersion, releaseInfo.releaseVersion, defaultUpdateProcess.length);

        for (String cmd : defaultUpdateProcess) {
            String[] cmdArgs = cmd.split(" ");
            try {
                Class<? extends AbstractTask> targetClass = taskClasses.get(cmdArgs[0]);
                if (targetClass != null) {
                    AbstractTask task = targetClass.getConstructor().newInstance();
                    ui.updateStep(task.getTaskID());
                    System.out.print(task.getTaskID() + "(" + Arrays.toString(cmdArgs) + ")");
                    try {
                        boolean taskResult = task.runTask(cmdArgs, environment);
                        System.out.println(" => " + taskResult);
                        if (!taskResult) {
                            JOptionPane.showMessageDialog(UpdateUI.getInstanceComponent(), "An update task has failed.\nThe updater will now close", "An Error has Occurred", JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                        }
                    } catch (Throwable t) {
                        task.reportError(t, new File(workingDirectory));
                    }
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
    }
}
