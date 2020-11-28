package com.amulet_editor.amulet_updater.utils;

import com.amulet_editor.amulet_updater.ui.UpdateUI;
import com.jcabi.github.*;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GithubAPI {

    private static final Github github = new RtGithub();

    public static void main(String[] args) throws MalformedURLException {
        ReleaseInfo info = getLatestRelease(false, null);
        assert info != null;
        System.out.println(info.releaseVersion + " -> " + info.parseUrl(info.getReleaseAsset()));
    }

    public static class ReleaseInfo {
        public final String releaseVersion;
        public final int releaseID;

        protected ReleaseInfo(String version, int releaseID) {
            this.releaseVersion = version;
            this.releaseID = releaseID;
        }

        public String getReleaseAsset() {
            try {
                Repo repo = github.repos().get(
                        new Coordinates.Simple("Amulet-Team/Amulet-Map-Editor")
                );

                Release.Smart release = new Release.Smart(repo.releases().get(releaseID));

                for (ReleaseAsset asset : release.assets().iterate()) {
                    ReleaseAsset.Smart smartAsset = new ReleaseAsset.Smart(asset);

                    String name = smartAsset.name();

                    if (SystemUtils.IS_OS_WINDOWS && name.contains("windows")) {
                        return name;
                    } else if (SystemUtils.IS_OS_MAC_OSX && name.contains("osx")) {
                        return name;
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                JOptionPane.showMessageDialog(UpdateUI.getInstanceComponent(), "Couldn't find release asset for " + this.releaseVersion, "An Error has Occured", JOptionPane.ERROR_MESSAGE);
            }

            return null;
        }

        public  URL parseUrl(String assetName) throws MalformedURLException {
            return new URL("https://github.com/Amulet-Team/Amulet-Map-Editor/releases/download/" + releaseVersion + "/" + assetName);
        }
    }

    public static ReleaseInfo getLatestRelease(boolean includeBetas, String targetVersion) {
        try {
            Repo repo = github.repos().get(
                    new Coordinates.Simple("Amulet-Team/Amulet-Map-Editor")
            );
            Releases releases = repo.releases();
            Release.Smart latestRelease = null;
            for (Release rel : releases.iterate()) {
                latestRelease = new Release.Smart(rel);

                if (latestRelease.prerelease() && !includeBetas) {
                    continue;
                }

                if (!latestRelease.draft()) {
                    if (targetVersion == null) {
                        break;
                    } else if (targetVersion.equals(latestRelease.tag())) {
                        break;
                    }
                }
            }
            if (latestRelease != null) {
                return new ReleaseInfo(latestRelease.tag(), latestRelease.number());
            }
        } catch (IOException | AssertionError ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
}
