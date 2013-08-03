package com.github.pps;

import com.github.pps.dto.Task;
import com.github.pps.util.PictureSaveUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sina.sae.storage.SaeStorage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.joda.time.DateTime.now;

@Controller
public class PrettyPictureController {
    private static final DateTime COMPARE_DATE = DateTime.now().withTime(0, 0, 0, 0);
    private static final int MAX_COUNT = 100;
    private static final int FEATURE_PIC = 2;
    private static final int MAX_UID_SIZE = 5;
    private static final String DOMAIN_NAME = "mydomain";

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
        System.out.println("get user token start");
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
        System.out.println("get user token finish");
        return "pretty-picture";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public
    @ResponseBody
    String savePictures(HttpServletRequest request) throws Exception {
        final String uids = request.getParameter("uids");
        final String token = request.getParameter("token");
        final String currentUid = request.getParameter("currentUid");

        Task task = new Task(currentUid, "running", now().getMillis());
        EntityManager entityManager = getEntityManager();
        entityManager.persist(task);

        System.out.println("taskid:" + task.getId());
        Query query = entityManager.createQuery("select t from " + Task.class.getName() + " t");
        List<Task> tasks = query.getResultList();

        entityManagerClose(entityManager);

        Thread thread = createSaveThread(uids, token, currentUid, task.getId());
        thread.start();
        String resultJson = resultJson("OK", tasks);
        System.out.println("resultJson:" + resultJson);
        return resultJson;
    }

    private EntityManager getEntityManager() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("defaultPersistenceUnit");
        EntityManager entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();
        return entityManager;
    }

    private void entityManagerClose(EntityManager entityManager) {
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private Thread createSaveThread(final String uids, final String token, final String currentUid, final Long taskId) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> uidList = getUidList(uids);
                    List<Status> totalStatuses = getTotalStatuses(uidList, token);
                    int totalStatusSize = totalStatuses.size();
                    System.out.println("totalStatuses size:" + totalStatusSize);

                    if (totalStatusSize == 0) return;

                    SaeStorage storage = new SaeStorage();
                    String zipFileName = currentUid + "-" + now().getMillis() + ".zip";
                    byte[] zipFileBytes = PictureSaveUtil.getZipFileBytes(totalStatuses);
                    storage.write(DOMAIN_NAME, zipFileName, zipFileBytes);

                    System.out.println("taskid:" + taskId);

//                    EntityManager entityManager = getEntityManager();
//
//                    Task task = entityManager.find(Task.class, taskId);
//                    System.out.println("task:" + task);
//                    task.setStatus("done");
//                    String url = storage.getUrl(DOMAIN_NAME, zipFileName);
//                    task.setUrl(url);
//
//                    entityManagerClose(entityManager);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private String resultJson(String message, List<Task> tasks) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("message", message);

        JSONArray tasksJson = new JSONArray();
        for (Task task : tasks) {
            JSONObject taskJson = new JSONObject();
            taskJson.put("taskStatus", task.getStatus());
            taskJson.put("zipFileUrl", task.getUrl());
            tasksJson.put(taskJson);
        }
        result.put("task", tasksJson);
        return result.toString();
    }

    private List<String> getUidList(String uids) {
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
