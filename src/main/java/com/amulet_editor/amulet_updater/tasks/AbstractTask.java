package com.amulet_editor.amulet_updater.tasks;

import java.util.Map;

public abstract class AbstractTask {

    public AbstractTask() { }

    public abstract boolean runTask(String[] args, Map<String, Object> environment);

    public abstract String getTaskID();
}
