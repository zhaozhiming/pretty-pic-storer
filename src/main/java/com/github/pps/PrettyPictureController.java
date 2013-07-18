package com.github.pps;

import com.github.pps.util.PictureSaveUtil;
import com.github.pps.util.WeiboClientFactory;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
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
import weiboclient4j.StatusService;
import weiboclient4j.WeiboClient;
import weiboclient4j.WeiboClientException;
import weiboclient4j.model.Status;
import weiboclient4j.model.Timeline;
import weiboclient4j.oauth2.DisplayType;
import weiboclient4j.oauth2.GrantType;
import weiboclient4j.oauth2.ResponseType;
import weiboclient4j.oauth2.SinaWeibo2AccessToken;
import weiboclient4j.params.Parameters;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Controller
public class PrettyPictureController {
    private static final DateTime COMPARE_DATE = DateTime.now().withTime(0, 0, 0, 0);
    private static final int MAX_COUNT = 100;
    private static final int FEATURE_PIC = 2;
    public static final int MAX_UID_SIZE = 5;
    @Value("${appKey}")
    private String appKey;

    @Value("${appSecret}")
    private String appSecret;

    @Value("${callbackurl}")
    private String callBackUrl;

    private SinaWeibo2AccessToken accessToken;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String index(ModelMap model) throws Exception {
        System.out.println("start=================");

        String authUrl = WeiboClientFactory.getInstacne(appKey, appSecret)
                .getAuthorizationUrl(ResponseType.Code, DisplayType.Default, "test",
                        callBackUrl + "/code");
        System.out.println(String.format("authUrl:%s", authUrl));
        System.out.println("auth start");

        model.addAttribute("authUrl", authUrl);
        return "index";
    }

    @RequestMapping(value = "/code", method = RequestMethod.POST)
    public String listFriends(HttpServletRequest request, ModelMap model) throws Exception {
        System.out.println("listFriends start");
        String code = request.getParameter("code");
        System.out.println(String.format("code:%s", code));

        SinaWeibo2AccessToken accessToken = getAccessTokenBy(code);

        System.out.println(String.format("accessToken:%s", accessToken));
        model.addAttribute("token", accessToken.getToken());
        model.addAttribute("appKey", appKey);
        model.addAttribute("currentUid", accessToken.getUid());
        System.out.println("listFriends finish");
        return "pretty-picture";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void savePictures(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> uidList = getUidList(request);
        String rootPath = getRootPath(request);

        StatusService statusService = getStatusService();
        List<Status> totalStatuses = getTotalStatuses(uidList, statusService);
        PictureSaveUtil.recordProgress(rootPath, "save", 0, totalStatuses.size());

        File zipFile = PictureSaveUtil.save(rootPath, uidList, totalStatuses);

        downloadZipFile(response, zipFile);
        FileUtils.forceDelete(zipFile);
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public
    @ResponseBody
    String checkProgress(HttpServletRequest request) throws Exception {
        String rootPath = getRootPath(request);

        File checkFile = new File(rootPath + File.separator + "check.properties");
        if (!checkFile.exists()) {
            checkFile.createNewFile();
            String record = String.format("checkStatus=%s\nalreadySave=%d\ntotalCount=%d", "get", 0, 1);
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

        if ("zip".equals(checkStatus)) {
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

    private StatusService getStatusService() {
        WeiboClient client = WeiboClientFactory.getInstacne(appKey, appSecret);
        return client.getStatusService();
    }

    private String getRootPath(HttpServletRequest request) {
        String currentUid = request.getParameter("currentUid");
        System.out.println("currentUid:" + currentUid);
        return "E:/my-pictures" + File.separator + currentUid;
    }

    private List<String> getUidList(HttpServletRequest request) {
        String friends = request.getParameter("friends");
        if (Strings.isNullOrEmpty(friends)) {
            throw new RuntimeException("friends is empty");
        }
        System.out.println("friends:" + friends);

        String[] friendArray = friends.split(";");
        List<String> uidList = Arrays.asList(friendArray);
        if (uidList.size() > MAX_UID_SIZE) {
            throw new RuntimeException("uid size must be <= 5");
        }
        System.out.println("uidList:" + uidList);
        return uidList;
    }

    private List<Status> getTotalStatuses(List<String> uidList, StatusService statusService) throws WeiboClientException {
        int page = 1;
        List<Status> totalStatuses = Lists.newArrayList();
        boolean flag = true;
        while (flag) {
            Timeline timeline = statusService.getHomeTimeline(
                    getGetHomeTimeLineParams(MAX_COUNT, FEATURE_PIC, page));
            List<Status> statuses = timeline.getStatuses();
            System.out.println(String.format("page:%d, statuses.size():%d", page, statuses.size()));
            for (Status status : statuses) {
                if (new DateTime(status.getCreatedAt()).isBefore(COMPARE_DATE.toDate().getTime())) {
                    flag = false;
                    break;
                }

                String uid = Long.toString(status.getUser().getId());
                if (!uidList.contains(uid)) continue;

                totalStatuses.add(status);
                System.out.println("picture create at time:" + new DateTime(status.getCreatedAt()).toString(
                        DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss")));
            }
            page++;
        }
        return totalStatuses;
    }

    private StatusService.GetHomeTimelineParam getGetHomeTimeLineParams(
            final int count, final int feature, final int page) {
        return new StatusService.GetHomeTimelineParam() {
            @Override
            public void addParameter(Parameters params) {
                params.add("count", count);
                params.add("feature", feature);
                params.add("page", page);
            }
        };
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public String authentication() throws Exception {
        return "auth";
    }

    private SinaWeibo2AccessToken getAccessTokenBy(String code) {
        if (accessToken == null) {
            WeiboClient client = WeiboClientFactory.getInstacne(appKey, appSecret);
            accessToken = client.getAccessToken(GrantType.AuthorizationCode, code, callBackUrl);
            client.setAccessToken(accessToken);
        }
        return accessToken;
    }

}
