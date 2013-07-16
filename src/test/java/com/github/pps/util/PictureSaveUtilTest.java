package com.github.pps.util;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import weiboclient4j.model.Status;
import weiboclient4j.model.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PictureSaveUtilTest {

    private static final String ROOT_PATH = "d:/pic-save";
    private static final long UID1 = 1000001L;
    private static final long UID2 = 1000002L;
    private static final String PICTURE_1 = "http://sae.sina.com.cn/static/image/index_service/level7.png";
    private static final String PICTURE_2 = "http://sae.sina.com.cn/static/image/index_service/safecheck.png";
    private static final String PICTURE_3 = "http://img.t.sinajs.cn/t4/appstyle/open/images/common/transparent.gif";
    private static final String USER1_NAME = "-张三_";
    private static final String USER2_NAME = "-李四_";

    private File root;
    private List<Status> statues;

    private DateTime today;
    private String createDataFolderPath;
    private ArrayList<String> uids;
    private String uid1FolderPath;
    private String uid2FolderPath;

    @Before
    public void setUp() throws Exception {
        root = new File(ROOT_PATH);
        uids = Lists.newArrayList(Long.toString(UID1));
        today = DateTime.now();

        statues = new ArrayList<Status>();
        Status statue = createStatus(UID1, USER1_NAME, today.toDate(), null, null);
        statues.add(statue);

        createDataFolderPath = ROOT_PATH + File.separator + today.toString(PictureSaveUtil.FMT);
        uid1FolderPath = createDataFolderPath + File.separator + UID1 + "-" + USER1_NAME;
        uid2FolderPath = createDataFolderPath + File.separator + UID2 + "-" + USER2_NAME;
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(root);
    }

    @Test
    public void should_do_nothing_when_parameter_is_empty() throws Exception {
        PictureSaveUtil.save(null, null, null);
        assertThat(root.exists(), is(false));

        PictureSaveUtil.save(ROOT_PATH, null, null);
        assertThat(root.exists(), is(false));

        JSONObject result = PictureSaveUtil.save(ROOT_PATH, uids, null);
        assertThat(root.exists(), is(false));

        assertThat(result.getInt("fileCount"), is(0));
        assertThat(result.getString("savePath"), is(ROOT_PATH));
        assertThat(result.getString("consumeTime"), is("0:00:00"));
    }

    @Test
    public void should_create_root_file_path_correct() throws Exception {
        PictureSaveUtil.save(ROOT_PATH, uids, statues);
        assertThat(root.exists(), is(true));

        PictureSaveUtil.save(ROOT_PATH, uids, statues);
        assertThat(root.exists(), is(true));
    }

    @Test
    public void should_create_today_folder_in_root_folder_correct() throws IOException {
        PictureSaveUtil.save(ROOT_PATH, uids, statues);
        assertThat(new File(createDataFolderPath).exists(), is(true));

        PictureSaveUtil.save(ROOT_PATH, uids, statues);
        assertThat(new File(createDataFolderPath).exists(), is(true));
    }

    @Test
    public void should_save_one_picture_in_uid_folder_correct() throws Exception {
        statues.clear();
        Status statue = createStatus(UID1, USER1_NAME, today.toDate(), PICTURE_1, null);
        statues.add(statue);

        PictureSaveUtil.save(ROOT_PATH, uids, statues);
        String pictureFilePath = uid1FolderPath + File.separator + "level7.png";

        assertThat(new File(pictureFilePath).exists(), is(true));
    }

    @Test
    public void should_save_multiply_pictures_of_uid1_in_uid_folder_correct() throws Exception {
        statues.clear();
        Status statue1 = createStatus(UID1, USER1_NAME, today.toDate(), PICTURE_1, null);
        Status statue2 = createStatus(UID1, USER1_NAME, today.toDate(), PICTURE_2, null);
        statues.add(statue1);
        statues.add(statue2);

        PictureSaveUtil.save(ROOT_PATH, uids, statues);

        String pictureFilePath1 = uid1FolderPath + File.separator + "level7.png";
        String pictureFilePath2 = uid1FolderPath + File.separator + "safecheck.png";
        assertThat(new File(pictureFilePath1).exists(), is(true));
        assertThat(new File(pictureFilePath2).exists(), is(true));
    }

    @Test
    public void should_save_multiply_picture_of_multiply_uid_in_uid_folder_correct() throws Exception {
        statues.clear();
        Status statue1 = createStatus(UID1, USER1_NAME, today.toDate(), PICTURE_1, null);
        Status statue2 = createStatus(UID2, USER2_NAME, today.toDate(), PICTURE_2, null);
        Status statue3 = createStatus(UID2, USER2_NAME, today.toDate(), PICTURE_3, null);
        statues.add(statue1);
        statues.add(statue2);
        statues.add(statue3);

        uids.add(Long.toString(UID2));
        JSONObject result = PictureSaveUtil.save(ROOT_PATH, uids, statues);
        String pictureFilePath1 = uid1FolderPath + File.separator + "level7.png";
        String pictureFilePath2 = uid2FolderPath + File.separator + "safecheck.png";
        String pictureFilePath3 = uid2FolderPath + File.separator + "transparent.gif";

        assertThat(new File(pictureFilePath1).exists(), is(true));
        assertThat(new File(pictureFilePath2).exists(), is(true));
        assertThat(new File(pictureFilePath3).exists(), is(false));

        assertThat(result.getInt("fileCount"), is(2));
        assertNotNull(result.getString("savePath"));
        assertNotNull(result.getString("consumeTime"));
    }

    @Test
    public void should_save_retweeted_statue_pictures_correct() throws Exception {
        statues.clear();
        Status retweetedStatus = createStatus(UID2, USER2_NAME, today.toDate(), PICTURE_1, null);
        Status statue = createStatus(UID1, USER1_NAME, today.toDate(), null, retweetedStatus);

        statues.add(statue);
        PictureSaveUtil.save(ROOT_PATH, uids, statues);
        String pictureFilePath = uid1FolderPath + File.separator + "level7.png";

        assertThat(new File(pictureFilePath).exists(), is(true));
    }

    @Test
    public void should_no_save_gif_pictures() throws Exception {
        statues.clear();
        Status statue = createStatus(UID1, USER1_NAME, today.toDate(), PICTURE_3, null);
        statues.add(statue);

        PictureSaveUtil.save(ROOT_PATH, uids, statues);

        String pictureFilePath = uid1FolderPath + File.separator + "transparent.gif";
        assertThat(new File(pictureFilePath).exists(), is(false));
    }

    private Status createStatus(long uid, String screenName, Date createdAt
            , String originalPic, Status retweetedStatus) {
        Status statue = new Status();
        User user1 = new User();
        user1.setId(uid);
        user1.setScreenName(screenName);
        statue.setUser(user1);
        statue.setCreatedAt(createdAt);
        statue.setOriginalPic(originalPic);
        statue.setRetweetedStatus(retweetedStatus);
        return statue;
    }

}
