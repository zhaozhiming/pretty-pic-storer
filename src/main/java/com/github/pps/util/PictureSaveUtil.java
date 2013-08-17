package com.github.pps.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import weibo4j.model.Status;

import java.io.*;
import java.net.URL;
import java.util.List;

import static org.joda.time.DateTime.now;

public class PictureSaveUtil {
    private static final Log log = LogFactory.getLog(PictureSaveUtil.class);
    public static final DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final String ENCODING_GBK = "GBK";
    private static final String GIF_SUFFIX_LOWWER = "gif";
    private static final String GIF_SUFFIX_UPPER = "GIF";
    public static final int CACHE_SIZE = 1024;

    public static byte[] getZipFileBytes(List<Status> statues) throws IOException {
        if (statues == null || statues.isEmpty()) return null;

        ByteArrayOutputStream aos = null;
        ZipOutputStream zos = null;
        InputStream is = null;
        try {
            aos = new ByteArrayOutputStream();
            zos = new ZipOutputStream(aos);
            zos.setEncoding(ENCODING_GBK);

            log.debug("put all pic to zip");
            is = putAllPicToZip(statues, zos, is);
            IOUtils.closeQuietly(zos);
            return aos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(aos);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(zos);
        }
    }

    private static InputStream putAllPicToZip(List<Status> statues, ZipOutputStream zos, InputStream is) throws IOException {
        List<String> picZipPath = Lists.newArrayList();
        for (Status status : statues) {
            String originalPic = status.getOriginalPic();
            if (Strings.isNullOrEmpty(originalPic)) {
                Status retweetedStatus = status.getRetweetedStatus();
                if (retweetedStatus == null) continue;

                String retweetedOriginalPic = retweetedStatus.getOriginalPic();
                if (Strings.isNullOrEmpty(retweetedOriginalPic)) continue;

                originalPic = retweetedOriginalPic;
            }

            log.debug("originalPic:" + originalPic);
            String picName = originalPic.substring(originalPic.lastIndexOf("/") + 1);
            if (GIF_SUFFIX_LOWWER.equals(picName.split("\\.")[1])
                    || GIF_SUFFIX_UPPER.equals(picName.split("\\.")[1])) {
                continue;
            }

            String fullPicPath = getPicPath(status) + File.separator + picName;
            log.debug("full pic path:" + fullPicPath);
            if (picZipPath.contains(fullPicPath)) continue;

            picZipPath.add(fullPicPath);
            zos.putNextEntry(new ZipEntry(fullPicPath));

            is = writeRemotePicToZip(zos, originalPic);
            IOUtils.closeQuietly(is);
        }
        return is;
    }

    private static InputStream writeRemotePicToZip(ZipOutputStream zos, String originalPic) throws IOException {
        URL url = new URL(originalPic);
        InputStream is = new BufferedInputStream(url.openStream());
        byte[] bytes = new byte[CACHE_SIZE];
        int len;
        while ((len = is.read(bytes)) > 0) {
            zos.write(bytes, 0, len);
        }
        return is;
    }

    private static String getPicPath(Status status) {
        return now().toString(FMT) + File.separator
                + status.getUser().getId() + "-" + status.getUser().getScreenName();
    }

}