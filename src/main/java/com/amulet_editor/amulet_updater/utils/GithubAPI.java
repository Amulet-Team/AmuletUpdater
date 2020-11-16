package com.amulet_editor.amulet_updater.utils;

import com.jcabi.github.*;

import java.io.IOException;

public class GithubAPI {

    public static String getLatestRelease() {
        try {
            Github github = new RtGithub();
            Repo repo = github.repos().get(
                    new Coordinates.Simple("Amulet-Team/Amulet-Map-Editor")
            );
            Releases releases = repo.releases();
            Release.Smart latestRelease = null;
            for (Release rel : releases.iterate()) {
                latestRelease = new Release.Smart(rel);
                if (!latestRelease.draft()) {
                    break;
                }
            }
            if (latestRelease != null) {
                System.out.println(latestRelease.assetsUrl());
                for (ReleaseAsset asset : latestRelease.assets().iterate()) {
                    ReleaseAsset.Smart smartAsset = new ReleaseAsset.Smart(asset);
                    System.out.println(smartAsset.name());
                }
            }
        } catch (IOException | AssertionError ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
}
