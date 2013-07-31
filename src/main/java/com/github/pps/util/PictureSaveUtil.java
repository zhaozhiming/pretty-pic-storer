package com.github.pps.util;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import weibo4j.model.Status;

import java.io.*;
import java.net.URL;
import java.util.List;

import static org.joda.time.DateTime.now;

public class PictureSaveUtil {
    public static final DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final String ENCODING_UTF_8 = "UTF-8";
    private static final String GIF_SUFFIX = "gif";

    public static File save(String rootPath, List<String> uids, List<Status> statues) throws IOException {
        if (Strings.isNullOrEmpty(rootPath)) return null;
        if (uids == null || uids.isEmpty()) return null;
        if (statues == null || statues.isEmpty()) return null;

        File root = mkdir(rootPath);
        File todayFolder = createTodayFolder(root);

        System.out.println("total statuses size: " + statues.size());
        saveAllPicFiles(statues, todayFolder);
        return zipImageFiles(rootPath, todayFolder);
    }

    private static File zipImageFiles(String rootPath, File todayFolder) {
        File zipFile = new File(rootPath + File.separator + now().toString(FMT) + ".zip");

        Zip zip = new Zip();
        zip.setProject(new Project());
        zip.setDestFile(zipFile);
        zip.setBasedir(todayFolder);
        zip.setUpdate(true);
        zip.setEncoding(ENCODING_UTF_8);
        zip.execute();

        return zipFile;
    }

    private static void saveAllPicFiles(List<Status> statues, File todayFolder) throws IOException {
        int fileCount = 0;
        System.out.println("saveAllPicFiles start");
        for (Status status : statues) {
            String originalPic = status.getOriginalPic();
            if (Strings.isNullOrEmpty(originalPic)) {
                Status retweetedStatus = status.getRetweetedStatus();
                if (retweetedStatus == null) continue;

                String retweetedOriginalPic = retweetedStatus.getOriginalPic();
                if (Strings.isNullOrEmpty(retweetedOriginalPic)) continue;

                originalPic = retweetedOriginalPic;
            }

            System.out.println("originalPic:" + originalPic);
            String pictureFileName = originalPic.substring(originalPic.lastIndexOf("/") + 1);
            if (GIF_SUFFIX.equals(pictureFileName.split("\\.")[1])) {
                continue;
            }

            File pictureFile = getPictureFile(todayFolder, status, pictureFileName);
            if (pictureFile.exists()) continue;

            System.out.println("pictureFile:" + pictureFile.getAbsolutePath());
            savePicture(originalPic, pictureFile);
            fileCount++;
            System.out.printf("file-%d: save picture: %s%n", fileCount, pictureFile.getAbsolutePath());
        }
    }

    private static File getPictureFile(File todayFolder, Status status, String pictureFileName) throws IOException {
        File userFolder = mkdirUserFolder(todayFolder, status);
        String pictureFilePath = userFolder.getAbsolutePath() + File.separator + pictureFileName;
        return new File(pictureFilePath);
    }

    private static File mkdirUserFolder(File todayFolder, Status status) throws IOException {
        String userFolderPath = todayFolder.getAbsoluteFile() + File.separator
                + status.getUser().getId() + "-" + status.getUser().getScreenName();
        return mkdir(userFolderPath);
    }

    private static File mkdir(String folderPath) throws IOException {
        File folder = new File(folderPath);
        FileUtils.forceMkdir(folder);
        return folder;
    }

    private static File createTodayFolder(File root) throws IOException {
        String todayFolderPath = root.getAbsolutePath() + File.separator + now().toString(FMT);
        File todayFolder = new File(todayFolderPath);
        if (todayFolder.exists()) {
            FileUtils.deleteDirectory(todayFolder);
        }

        return mkdir(todayFolderPath);
    }

    private static void savePicture(String originalPic, File localFile) throws IOException {
        BufferedInputStream bis = null;
        OutputStream bos = null;
        System.out.println("download picture start");
        try {
            URL url = new URL(originalPic);
            bis = new BufferedInputStream(url.openStream());
            byte[] bytes = new byte[100];
            bos = new FileOutputStream(localFile);
            int len;
            while ((len = bis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(bos);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(bos);
        }
        System.out.println("download picture finish");
    }
}