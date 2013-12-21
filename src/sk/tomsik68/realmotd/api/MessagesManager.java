package sk.tomsik68.realmotd.api;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.plugin.Plugin;

import sk.tomsik68.realmotd.EMotdMode;

public class MessagesManager {
    private final File dataFolder;
    private final IConfig config;
    private final String subdirName;

    public MessagesManager(IConfig config, File dataFolder, String subDir) {
        this.dataFolder = dataFolder;
        this.config = config;
        this.subdirName = subDir;
    }

    public File getMotdFile(Plugin plugin, EMotdMode mode, String group, String world, int month, int day) {
        List<File> potentialFiles = getPotentialFiles(plugin, mode, group, world, month, day);
        for (File file : potentialFiles) {
            if (file.exists()) {
                return file;
            }
        }
        return getDefaultMotdFile();
    }

    private List<File> getPotentialFiles(Plugin plugin, EMotdMode mode, String group, String world, int month, int day) {
        String basePath = plugin.getDataFolder().getAbsolutePath() + File.separator + subdirName;
        List<File> result = new ArrayList<File>();
        if (mode == EMotdMode.DAILY) {
            result.addAll(getDailyMotdFiles(basePath, group, world, month, day));
        } else if (mode == EMotdMode.RANDOM) {
            result.addAll(getRandomMotdFiles(basePath, group, world));
        }
        result.addAll(getSingleMotdFiles(basePath, group, world));
        return result;
    }

    private Collection<? extends File> getRandomMotdFiles(String basePath, String group, String world) {
        List<File> result = new ArrayList<File>();
        if (config.isGroupSpecific()) {
            if (config.isWorldSpecific()) {
                result.addAll(Arrays.asList(new File(getGroupFolder(basePath, group), world).listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File arg0, String arg1) {
                        return arg1.endsWith(".txt");
                    }
                })));
            }
            result.addAll(Arrays.asList(getGroupFolder(basePath, group).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File arg0, String arg1) {
                    return arg1.endsWith(".txt");
                }
            })));
        }
        if (config.isWorldSpecific()) {
            result.addAll(Arrays.asList(new File(basePath, world).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File arg0, String arg1) {
                    return arg1.endsWith(".txt");
                }
            })));
        }
        result.addAll(Arrays.asList(new File(basePath).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File arg0, String arg1) {
                return arg1.endsWith(".txt");
            }
        })));
        Collections.shuffle(result, new Random());
        return result;
    }

    private Collection<? extends File> getSingleMotdFiles(String basePath, String group, String world) {
        List<File> result = new ArrayList<File>();
        if (config.isGroupSpecific()) {
            if (config.isWorldSpecific()) {
                result.add(new File(getGroupFolder(basePath, group), world + File.separator + "motd.txt"));
            }
            result.add(new File(getGroupFolder(basePath, group), "motd.txt"));
        }
        if (config.isWorldSpecific()) {
            result.add(new File(basePath, world + File.separator + "motd.txt"));
        }
        result.add(new File(basePath, "motd.txt"));
        return result;

    }

    private Collection<? extends File> getDailyMotdFiles(String basePath, String group, String world, int month, int day) {
        List<File> result = new ArrayList<File>();
        if (config.isGroupSpecific()) {
            if (config.isWorldSpecific()) {
                result.add(new File(getGroupFolder(basePath, group), world + File.separator + "motd_" + month + "_" + day + ".txt"));
            }
            result.add(new File(getGroupFolder(basePath, group), "motd_" + month + "_" + day + ".txt"));
        }
        if (config.isWorldSpecific()) {
            result.add(new File(basePath, world + File.separator + "motd_" + month + "_" + day + ".txt"));
        }
        result.add(new File(basePath, "motd_" + month + "_" + day + ".txt"));
        return result;
    }

    private File getGroupFolder(String basePath, String group) {
        return new File(basePath, group);
    }

    public File getDefaultMotdFile() {
        return new File(dataFolder, subdirName.concat(File.separator).concat("motd.txt"));
    }
}
