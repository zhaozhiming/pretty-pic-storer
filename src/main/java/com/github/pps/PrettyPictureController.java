package com.github.pps;

import com.github.pps.util.PictureSaveUtil;
import com.github.pps.util.WeiboClientFactory;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
public class PrettyPictureController {
    private static final DateTime COMPARE_DATE = DateTime.now().withTime(0, 0, 0, 0);
    private static final int MAX_COUNT = 100;
    private static final int FEATURE_PIC = 2;
    private SinaWeibo2AccessToken accessToken;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String index(ModelMap model) throws Exception {
        System.out.println("start=================");

        String authUrl = WeiboClientFactory.getInstacne()
                .getAuthorizationUrl(ResponseType.Code, DisplayType.Default, "test",
                        "http://apps.weibo.com/prettypicturequery/code");
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
        model.addAttribute("uid", accessToken.getUid());
        model.addAttribute("remindIn", accessToken.getRemindIn());
        model.addAttribute("expiresIn", accessToken.getExpiresIn());
        model.addAttribute("code", code);
        System.out.println("listFriends finish");
        return "prettyPicture";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public String savePictures(HttpServletRequest request) throws Exception {
        String friends = request.getParameter("friends");
        System.out.println("friends:" + friends);

        String[] friendArray = friends.split(";");
        List<String> uids = Arrays.asList(friendArray);
        System.out.println("uids:" + uids);

        String rootPath = request.getParameter("rootPath");

        WeiboClient client = WeiboClientFactory.getInstacne();
        StatusService statusService = client.getStatusService();

        List<Status> totalStatuses = getTotalStatuses(uids, statusService);
        JSONObject result = PictureSaveUtil.save(rootPath, uids, totalStatuses);
        return result.toString();
    }

    private List<Status> getTotalStatuses(List<String> uids, StatusService statusService) throws WeiboClientException {
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
                if (!uids.contains(uid)) continue;

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
            accessToken = WeiboClientFactory.getInstacne()
                    .getAccessToken(GrantType.AuthorizationCode, code,
                            "http://apps.weibo.com/prettypicturequery");
            WeiboClientFactory.getInstacne().setAccessToken(accessToken);
        }
        return accessToken;
    }

}
