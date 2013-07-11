package com.github.pps.util;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import weiboclient4j.model.Status;

import java.io.*;
import java.net.URL;
import java.util.List;

import static org.joda.time.DateTime.now;

public class PictureSaveUtil {
    public static final DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static JSONObject save(String rootPath, List<String> uids, List<Status> statues) throws IOException {
        if (Strings.isNullOrEmpty(rootPath)) return null;
        if (uids == null || uids.isEmpty()) return null;
        if (statues == null || statues.isEmpty()) return null;

        long startTime = System.currentTimeMillis();
        File root = mkdir(rootPath);
        File todayFolder = createTodayFolder(root);

        System.out.println("total statuses size: " + statues.size());
        int fileCount = 0;
        for (Status status : statues) {
            String originalPic = status.getOriginalPic();
            if (Strings.isNullOrEmpty(originalPic)) {
                Status retweetedStatus = status.getRetweetedStatus();
                if (retweetedStatus == null) continue;

                String retweetedOriginalPic = retweetedStatus.getOriginalPic();
                if (Strings.isNullOrEmpty(retweetedOriginalPic)) continue;

                originalPic = retweetedOriginalPic;
            }

            String pictureFileName = originalPic.substring(originalPic.lastIndexOf("/") + 1);
            if ("gif".equals(pictureFileName.split("\\.")[1])) {
                continue;
            }

            File userFolder = mkdirUserFolder(todayFolder, status);
            String pictureFilePath = userFolder.getAbsolutePath() + File.separator + pictureFileName;
            File pictureFile = new File(pictureFilePath);
            if (pictureFile.exists()) continue;

            savePicture(originalPic, pictureFile);
            fileCount++;
            System.out.printf("file-%d: save picture: %s%n", fileCount, pictureFilePath);
        }

        long endTime = System.currentTimeMillis();
        return createSaveInfo(fileCount, todayFolder, endTime - startTime);
    }

    private static File mkdirUserFolder(File todayFolder, Status status) throws IOException {
        String userFolderPath = todayFolder.getAbsoluteFile() + File.separator
                + status.getUser().getId() + "-" + status.getUser().getScreenName();
        return mkdir(userFolderPath);
    }

    private static JSONObject createSaveInfo(int fileCount, File todayFolder, long consumeTime) {
        JSONObject result = new JSONObject();
        try {
            result.put("fileCount", fileCount);
            result.put("savePath", todayFolder.getAbsoluteFile());
            result.put("consumeTime", fmtConsumeTime(consumeTime / 1000));
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    private static String fmtConsumeTime(long consumeTime) {
        return String.format("%d:%02d:%02d", consumeTime / 3600,
                (consumeTime % 3600) / 60, (consumeTime % 60));
    }

    private static File mkdir(String folderPath) throws IOException {
        File folder = new File(folderPath);
        FileUtils.forceMkdir(folder);
        return folder;
    }

    private static File createTodayFolder(File root) throws IOException {
        String todayFolderPath = root.getAbsolutePath() + File.separator + now().toString(FMT);
        return mkdir(todayFolderPath);
    }

    private static void savePicture(String originalPic, File localFile) throws IOException {
        BufferedInputStream bis = null;
        OutputStream bos = null;
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
    }
}