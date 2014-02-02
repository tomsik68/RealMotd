package sk.tomsik68.realmotd.api;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FilesManager {
    private final File dataFolder;
    private final IConfig config;
    private final String subdirName;
    private final String templateFileName;
    private final String ext;
    private boolean isGroupSpecific;

    public FilesManager(IConfig config, File dataFolder, String subDir, String templateName, String extension) {
        this.dataFolder = dataFolder;
        this.config = config;
        this.subdirName = subDir;
        templateFileName = templateName;
        if(!extension.startsWith("."))
            extension = ".".concat(extension);
        ext = extension;
    }

    public File getMotdFile(EMotdMode mode, String group, String world, int month, int day) {
        isGroupSpecific = group.length() > 0 && config.isGroupSpecific();
        List<File> potentialFiles = getPotentialFiles(mode, group, world, month, day);
        
        for (File file : potentialFiles) {
            if (file.exists()) {
                return file;
            }
        }
        return getDefaultMotdFile();
    }

    private List<File> getPotentialFiles(EMotdMode mode, String group, String world, int month, int day) {
        String basePath = dataFolder.getAbsolutePath() + File.separator + subdirName;
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
        if (isGroupSpecific) {
            if (config.isWorldSpecific()) {
                result.addAll(Arrays.asList(new File(getGroupFolder(basePath, group), world).listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File arg0, String arg1) {
                        return arg1.endsWith(ext);
                    }
                })));
            }
            result.addAll(Arrays.asList(getGroupFolder(basePath, group).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File arg0, String arg1) {
                    return arg1.endsWith(ext);
                }
            })));
        }
        if (config.isWorldSpecific()) {
            result.addAll(Arrays.asList(new File(basePath, world).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File arg0, String arg1) {
                    return arg1.endsWith(ext);
                }
            })));
        }
        result.addAll(Arrays.asList(new File(basePath).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File arg0, String arg1) {
                return arg1.endsWith(ext);
            }
        })));
        Collections.shuffle(result, new Random());
        return result;
    }

    private Collection<? extends File> getSingleMotdFiles(String basePath, String group, String world) {
        List<File> result = new ArrayList<File>();
        if (isGroupSpecific) {
            if (config.isWorldSpecific()) {
                result.add(new File(getGroupFolder(basePath, group), world + File.separator + templateFileName+ext));
            }
            result.add(new File(getGroupFolder(basePath, group), templateFileName+ext));
        }
        if (config.isWorldSpecific()) {
            result.add(new File(basePath, world + File.separator + templateFileName+ext));
        }
        result.add(new File(basePath, templateFileName+ext));
        return result;

    }

    private Collection<? extends File> getDailyMotdFiles(String basePath, String group, String world, int month, int day) {
        List<File> result = new ArrayList<File>();
        if (isGroupSpecific) {
            if (config.isWorldSpecific()) {
                result.add(new File(getGroupFolder(basePath, group), world + File.separator + templateFileName+"_" + month + "_" + day + ext));
            }
            result.add(new File(getGroupFolder(basePath, group), templateFileName+"_" + month + "_" + day + ext));
        }
        if (config.isWorldSpecific()) {
            result.add(new File(basePath, world + File.separator + templateFileName+"_" + month + "_" + day + ext));
        }
        result.add(new File(basePath, templateFileName+"_" + month + "_" + day + ext));
        return result;
    }

    private File getGroupFolder(String basePath, String group) {
        return new File(basePath, group);
    }

    public File getDefaultMotdFile() {
        return new File(dataFolder, subdirName.concat(File.separator).concat(templateFileName+ext));
    }
}
