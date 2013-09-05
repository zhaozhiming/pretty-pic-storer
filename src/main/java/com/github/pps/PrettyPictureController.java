package com.github.pps;

import com.github.pps.dto.Task;
import com.github.pps.repo.TaskRepository;
import com.github.pps.util.PictureSaveUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sina.sae.storage.SaeStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static com.github.pps.repo.TaskRepository.TASK_STATUS_DONE;
import static java.util.Arrays.asList;
import static org.joda.time.DateTime.now;

@Controller
public class PrettyPictureController {
    public static final DateTimeFormatter FMT_SEC = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final Log log = LogFactory.getLog(PrettyPictureController.class);
    private static final DateTime COMPARE_DATE = DateTime.now().withTime(0, 0, 0, 0);
    private static final int MAX_COUNT = 30;
    private static final int FEATURE_PIC = 2;
    private static final int MAX_UID_SIZE = 5;
    private static final String DOMAIN_NAME = "mydomain";

    @Autowired
    private TaskRepository taskRepository;

    @Value("${appKey}")
    private String appKey;

    @Value("${appSecret}")
    private String appSecret;

    @Value("${callbackurl}")
    private String callBackUrl;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String index(ModelMap model) throws Exception {
        log.debug("pps start");
        model.addAttribute("appKey", appKey);
        model.addAttribute("callBackUrl", callBackUrl + "/main");
        return "index";
    }

    @RequestMapping(value = "/main", method = RequestMethod.POST)
    public String listFriends(HttpServletRequest request, ModelMap model) throws Exception {
        log.debug("get user token start");
        String signedRequest = request.getParameter("signed_request");
        log.debug(String.format("signed_request:%s", signedRequest));

        if (signedRequest == null) return "redirect:/";
        Oauth auth = new Oauth();
        auth.parseSignedRequest(signedRequest);

        if (auth.user_id == null) return "redirect:/";

        log.debug(String.format("accessToken:%s", auth.access_token));
        log.debug(String.format("user id:%s", auth.user_id));
        model.addAttribute("token", auth.access_token);
        model.addAttribute("appKey", appKey);
        model.addAttribute("currentUid", auth.user_id);
        log.debug("get user token finish");
        return "show-pre-pic";
    }

    @RequestMapping(value = "/task/run", method = RequestMethod.GET)
    public
    @ResponseBody
    String runTask() throws Exception {
        Task task = taskRepository.findOneNewTask();
        if (task == null) return new JSONObject().toString();

        Long taskId = task.getId();
        taskRepository.updateTaskRunning(taskId);

        List<String> uidList = getUidList(task.getStatueIds());
        List<Status> totalStatuses = getTotalStatuses(uidList, task.getToken());
        int totalStatusSize = totalStatuses.size();
        log.debug("totalStatuses size:" + totalStatusSize);

        if (totalStatusSize == 0) {
            taskRepository.updateTaskNothing(taskId);
            return new JSONObject().toString();
        }

        String url = putZipToStorage(task, totalStatuses);
        taskRepository.updateTaskDone(url, taskId);
        return new JSONObject().toString();
    }

    @RequestMapping(value = "/tasks/delete", method = RequestMethod.GET)
    public
    @ResponseBody
    String deleteTasks() throws Exception {
        List<Task> tasks = taskRepository.queryFinishedTasks();
        if (tasks == null || tasks.isEmpty()) return new JSONObject().toString();

        for (Task task : tasks) {
            if (TASK_STATUS_DONE.equals(task.getStatus())) {
                SaeStorage saeStorage = new SaeStorage();
                String url = task.getUrl();
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                saeStorage.delete(DOMAIN_NAME, fileName);
            }
            taskRepository.deleteTask(task);
        }
        return new JSONObject().toString();
    }

    @RequestMapping(value = "/tasks/{uid}", method = RequestMethod.GET)
    public
    @ResponseBody
    String queryTasks(@PathVariable String uid) throws Exception {
        List<Task> tasks = taskRepository.findTasksBy(uid);
        return getTasksJson(tasks).toString();
    }

    @RequestMapping(value = "/task/create", method = RequestMethod.POST)
    public
    @ResponseBody
    String createTask(HttpServletRequest request) throws Exception {
        String statueIds = request.getParameter("statueIds");
        String token = request.getParameter("token");
        String currentUid = request.getParameter("currentUid");
        verifyRequestParam(statueIds, token, currentUid);

        taskRepository.createTask(statueIds, token, currentUid);
        return new JSONObject().toString();
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public String authentication() throws Exception {
        return "auth";
    }

    @RequestMapping(value = "/pictures", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public
    @ResponseBody
    String showStatusPictures(HttpServletRequest request) throws Exception {
        log.debug("show status pictures start");
        String token = request.getParameter("token");
        String currentPage = request.getParameter("currentPage");
        log.debug("token:" + token);
        log.debug("currentPage:" + currentPage);
        verifyRequestParam(token, currentPage);

        List<Status> statuses = getHomeTimeLineStatuses(token, Integer.valueOf(currentPage));
        log.debug("statuses size:" + statuses.size());

        JSONArray statusArrayJson = createStatusesJsonArray(statuses);
        log.debug("statusArrayJson size:" + statusArrayJson.length());

        log.debug("show status pictures finish");
        return statusArrayJson.toString();
    }

    private JSONArray createStatusesJsonArray(List<Status> statuses) throws JSONException {
        JSONArray statusArrayJson = new JSONArray();
        List<String> alreadyHavePics = Lists.newArrayList();
        for (Status statuse : statuses) {
            JSONObject statusJson = new JSONObject();
            statusJson.put("id", statuse.getId());
            statusJson.put("screenName", statuse.getUser().getScreenName());
            statusJson.put("createdAt", new DateTime(statuse.getCreatedAt()).toString(FMT_SEC));
            statusJson.put("text", statuse.getText());

            String originalPic = statuse.getOriginalPic();
            String thumbnailPic = statuse.getThumbnailPic();
            if (Strings.isNullOrEmpty(originalPic)) {
                Status retweetedStatus = statuse.getRetweetedStatus();
                if (retweetedStatus == null) continue;

                String retweetedOriginalPic = retweetedStatus.getOriginalPic();
                if (Strings.isNullOrEmpty(retweetedOriginalPic)) continue;

                originalPic = retweetedOriginalPic;
                thumbnailPic = retweetedStatus.getThumbnailPic();
            }

            if (alreadyHavePics.contains(originalPic)) continue;
            alreadyHavePics.add(originalPic);

            statusJson.put("originalPic", originalPic);
            statusJson.put("thumbnailPic", thumbnailPic);

            log.debug("statusJson:" + statusJson);
            statusArrayJson.put(statusJson);
        }
        return statusArrayJson;
    }

    private void verifyRequestParam(String... params) {
        for (String param : params) {
            log.debug("request param: " + param);
            if (Strings.isNullOrEmpty(param)) {
                throw new RuntimeException("request param is empty, please check");
            }
        }
    }

    private String putZipToStorage(Task task, List<Status> totalStatuses) throws IOException {
        SaeStorage storage = new SaeStorage();
        String zipFileName = task.getUid() + "-" + now().getMillis() + ".zip";
        byte[] zipFileBytes = PictureSaveUtil.getZipFileBytes(totalStatuses);
        storage.write(DOMAIN_NAME, zipFileName, zipFileBytes);
        return storage.getUrl(PrettyPictureController.DOMAIN_NAME, zipFileName);
    }

    private JSONArray getTasksJson(List<Task> tasks) throws JSONException {
        JSONArray tasksJson = new JSONArray();
        for (Task task : tasks) {
            JSONObject taskJson = new JSONObject();
            taskJson.put("createdAt", now().withMillis(task.getCreatedAt()).toString(FMT_SEC));
            taskJson.put("taskStatus", task.getStatus());
            taskJson.put("zipFileUrl", (task.getUrl() == null) ? "no" : task.getUrl());
            tasksJson.put(taskJson);
        }
        log.debug("tasksJson:" + tasksJson);
        return tasksJson;
    }

    private List<String> getUidList(String uids) {
        log.debug("uids:" + uids);

        String[] uidArray = uids.split(";");
        List<String> uidList = asList(uidArray);
        if (uidList.size() > MAX_UID_SIZE) {
            throw new RuntimeException("uid size must be <= 5");
        }
        log.debug("uidList:" + uidList);
        return uidList;
    }

    private List<Status> getTotalStatuses(List<String> uidList, String accessToken) throws WeiboException {
        int page = 1;
        List<Status> totalStatuses = Lists.newArrayList();
        boolean flag = true;
        while (flag) {
            List<Status> statuses = getHomeTimeLineStatuses(accessToken, page);

            log.debug(String.format("page:%d, statuses.size():%d", page, statuses.size()));
            for (Status singleStatus : statuses) {
                if (notTodayStatus(singleStatus)) {
                    flag = false;
                    break;
                }

                String uid = singleStatus.getUser().getId();
                if (!uidList.contains(uid)) continue;

                totalStatuses.add(singleStatus);
                log.debug("picture create at time:" + new DateTime(singleStatus.getCreatedAt()).toString(
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
}
