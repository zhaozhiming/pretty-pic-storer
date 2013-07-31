package com.github.pps;

import com.github.pps.util.PictureSaveUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.sina.sae.util.SaeUserInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import weibo4j.Oauth;
import weibo4j.Timeline;
import weibo4j.model.PostParameter;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import weibo4j.util.WeiboConfig;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.github.pps.util.PictureSaveUtil.FMT;
import static org.joda.time.DateTime.now;

@Controller
public class PrettyPictureController {
    private static final DateTime COMPARE_DATE = DateTime.now().withTime(0, 0, 0, 0);
    private static final int MAX_COUNT = 100;
    private static final int FEATURE_PIC = 2;
    private static final int MAX_UID_SIZE = 5;
    private static final String GET_STATUS = "get";
    private static final String ZIP_STATUS = "zip";

    @Value("${appKey}")
    private String appKey;

    @Value("${appSecret}")
    private String appSecret;

    @Value("${callbackurl}")
    private String callBackUrl;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String index(ModelMap model) throws Exception {
        System.out.println("start=================");
        model.addAttribute("appKey", appKey);
        model.addAttribute("callBackUrl", callBackUrl + "/code");
        return "index";
    }

    @RequestMapping(value = "/code", method = RequestMethod.POST)
    public String listFriends(HttpServletRequest request, ModelMap model) throws Exception {
        System.out.println("listFriends start");
        String signedRequest = request.getParameter("signed_request");
        System.out.println(String.format("signed_request:%s", signedRequest));

        if (signedRequest == null) return "redirect:/";
        Oauth auth = new Oauth();
        auth.parseSignedRequest(signedRequest);

        if (auth.user_id == null) return "redirect:/";

        System.out.println(String.format("accessToken:%s", auth.access_token));
        System.out.println(String.format("user id:%s", auth.user_id));
        model.addAttribute("token", auth.access_token);
        model.addAttribute("appKey", appKey);
        model.addAttribute("currentUid", auth.user_id);
        System.out.println("listFriends finish");
        return "pretty-picture";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void savePictures(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String rootPath = getRootPath(request);

        List<String> uidList = getUidList(request);
        List<Status> totalStatuses = getTotalStatuses(uidList, request.getParameter("token"));
        int totalStatusSize = totalStatuses.size();
        System.out.println("totalStatuses size:" + totalStatusSize);

        File zipFile = PictureSaveUtil.save(rootPath, uidList, totalStatuses);
        zipFile = dealNoPic(rootPath, zipFile);

        downloadZipFile(response, zipFile);
        FileUtils.forceDelete(zipFile);
        FileUtils.deleteDirectory(new File(rootPath + File.separator + now().toString(FMT)));
    }

    private File dealNoPic(String rootPath, File zipFile) throws IOException {
        if (zipFile == null) {
            zipFile = new File(rootPath + File.separator + now().toString(FMT) + ".txt");
            System.out.println("zip file:" + zipFile.getAbsolutePath());
            zipFile.createNewFile();
            Files.write("没有图片可以下载".getBytes(), zipFile);
        }
        return zipFile;
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public
    @ResponseBody
    String checkProgress(HttpServletRequest request) throws Exception {
        String rootPath = getRootPath(request);

        File checkFile = new File(rootPath + File.separator + "check.properties");
        if (!checkFile.exists()) {
            checkFile.createNewFile();
            String record = String.format("checkStatus=%s\nalreadySave=%d\ntotalCount=%d", GET_STATUS, 0, 1);
            Files.write(record.getBytes(), checkFile);
        }

        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream(checkFile);
        properties.load(fis);
        IOUtils.closeQuietly(fis);

        JSONObject result = new JSONObject();
        String checkStatus = setJsonResult(properties, result, "checkStatus");
        setJsonResult(properties, result, "alreadySave");
        setJsonResult(properties, result, "totalCount");

        if (ZIP_STATUS.equals(checkStatus)) {
            FileUtils.forceDelete(checkFile);
        }
        return result.toString();
    }

    private String setJsonResult(Properties properties, JSONObject result, String property) throws JSONException {
        String checkStatus = properties.getProperty(property);
        System.out.println(property + ":" + checkStatus);
        result.put(property, checkStatus);
        return checkStatus;
    }

    private void downloadZipFile(HttpServletResponse response, File zipFile) throws IOException {
        InputStream ins = null;
        OutputStream ous = null;
        try {
            ins = new BufferedInputStream(new FileInputStream(zipFile));
            byte[] bytes = new byte[ins.available()];
            ins.read(bytes);
            IOUtils.closeQuietly(ins);

            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + zipFile.getName());
            Cookie cookie = new Cookie("fileDownload", "true");
            cookie.setPath("/");
            response.addCookie(cookie);

            ous = response.getOutputStream();
            ous.write(bytes);
            IOUtils.closeQuietly(ous);
        } finally {
            IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(ous);
        }
    }

    private String getRootPath(HttpServletRequest request) {
        String currentUid = request.getParameter("currentUid");
        System.out.println("currentUid:" + currentUid);
        String saeTmpPath = SaeUserInfo.getSaeTmpPath();
        System.out.println("saeTmpPath:" + saeTmpPath);
        return saeTmpPath + File.separator + currentUid;
    }

    private List<String> getUidList(HttpServletRequest request) {
        String uids = request.getParameter("uids");
        if (Strings.isNullOrEmpty(uids)) {
            throw new RuntimeException("uids is empty");
        }
        System.out.println("uids:" + uids);

        String[] uidArray = uids.split(";");
        List<String> uidList = Arrays.asList(uidArray);
        if (uidList.size() > MAX_UID_SIZE) {
            throw new RuntimeException("uid size must be <= 5");
        }
        System.out.println("uidList:" + uidList);
        return uidList;
    }

    private List<Status> getTotalStatuses(List<String> uidList, String accessToken) throws WeiboException {
        int page = 1;
        List<Status> totalStatuses = Lists.newArrayList();
        boolean flag = true;
        while (flag) {
            List<Status> statuses = getHomeTimeLineStatuses(accessToken, page);

            System.out.println(String.format("page:%d, statuses.size():%d", page, statuses.size()));
            for (Status singleStatus : statuses) {
                if (notTodayStatus(singleStatus)) {
                    flag = false;
                    break;
                }

                String uid = singleStatus.getUser().getId();
                if (!uidList.contains(uid)) continue;

                totalStatuses.add(singleStatus);
                System.out.println("picture create at time:" + new DateTime(singleStatus.getCreatedAt()).toString(
                        DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss")));
            }
            page++;
        }
        return totalStatuses;
    }

    private boolean notTodayStatus(Status singleStatus) {
        return new DateTime(singleStatus.getCreatedAt()).isBefore(COMPARE_DATE.toDate().getTime());
    }

    private List<Status> getHomeTimeLineStatuses(String accessToken, int page) throws WeiboException {
        Timeline tm = new Timeline();
        tm.client.setToken(accessToken);
        StatusWapper status = Status.constructWapperStatus(
                tm.client.get(WeiboConfig.getValue("baseURL") + "statuses/home_timeline.json",
                        new PostParameter[]{
                                new PostParameter("count", MAX_COUNT),
                                new PostParameter("feature", FEATURE_PIC),
                                new PostParameter("page", page)}));
        return status.getStatuses();
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public String authentication() throws Exception {
        return "auth";
    }
}
