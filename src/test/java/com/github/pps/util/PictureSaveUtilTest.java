package com.github.pps.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import weibo4j.model.Status;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class PictureSaveUtilTest {

    private static final String ROOT_PATH = "/home/kingzzm/pic-save";
    private static final long UID1 = 1000001L;
    private static final long UID2 = 1000002L;
    private static final String PICTURE_1 = "http://sae.sina.com.cn/static/image/index_service/level7.png";
    private static final String PICTURE_2 = "http://sae.sina.com.cn/static/image/index_service/safecheck.png";
    private static final String USER1_NAME = "-张三_";
    private static final String USER2_NAME = "-李四_";
    public static final String ZIP_NAME = "myzip.zip";

    private File root;
    private List<Status> statues;

    private DateTime today;

    @Before
    public void setUp() throws Exception {
        root = new File(ROOT_PATH);
        root.mkdir();

        today = DateTime.now();
        statues = new ArrayList<Status>();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(root);
    }

    @Test
    public void should_return_null_when_statues_is_empty() throws Exception {
        assertNull(PictureSaveUtil.getZipFileBytes(statues));
    }

    @Test
    public void should_return_zip_bytes_correct() throws Exception {
        Status statue1 = createStatus(UID1, USER1_NAME, today.toDate(), PICTURE_1, null);
        Status statue2 = createStatus(UID2, USER2_NAME, today.toDate(), PICTURE_2, null);
        statues.add(statue1);
        statues.add(statue2);

        byte[] zipFileBytes = PictureSaveUtil.getZipFileBytes(statues);
        saveFile(zipFileBytes, ROOT_PATH + File.separator + ZIP_NAME);

        assertThat(new File(ROOT_PATH + File.separator + ZIP_NAME).exists(), is(true));
    }

    @Test
    public void should_normal_zip_when_have_same_pic() throws Exception {
        Status statue1 = createStatus(UID1, USER1_NAME, today.toDate(), PICTURE_1, null);
        Status statue2 = createStatus(UID1, USER1_NAME, today.toDate(), null, statue1);
        statues.add(statue1);
        statues.add(statue2);

        byte[] zipFileBytes = PictureSaveUtil.getZipFileBytes(statues);
        saveFile(zipFileBytes, ROOT_PATH + File.separator + ZIP_NAME);

        assertThat(new File(ROOT_PATH + File.separator + ZIP_NAME).exists(), is(true));
    }

    private Status createStatus(long uid, String screenName, Date createdAt
            , String originalPic, Status retweetedStatus) throws WeiboException {
        Status statue = new Status();
        User user1 = new User(new JSONObject());
        user1.setId(Long.toString(uid));
        user1.setScreenName(screenName);
        statue.setUser(user1);
        statue.setCreatedAt(createdAt);
        statue.setOriginalPic(originalPic);
        statue.setRetweetedStatus(retweetedStatus);
        return statue;
    }

    private void saveFile(byte[] bytes, String zipFileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(zipFileName));
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(bytes);
        IOUtils.closeQuietly(bos);
    }

}
