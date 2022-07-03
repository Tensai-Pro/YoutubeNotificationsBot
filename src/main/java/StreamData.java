import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class StreamData {
    private static final String DEVELOPER_KEY = "AIzaSyBkzepr1bsRvwdQjyr1yPQ0ollbsAHXTZs";

    private static JSONArray streamsArr;

    private static boolean checkStreamInfo(String channelId) throws IOException {
        URL channelUrl = new URL("https://www.googleapis.com/youtube/v3/search?" +
                "part=snippet" +
                "&channelId=" + channelId +
                "&eventType=upcoming&type=video" +
                "&key=" + DEVELOPER_KEY);

        Scanner in = new Scanner((InputStream) channelUrl.getContent());
        String json = "";
        while (in.hasNext())
            json += in.nextLine();
        in.close();

        JSONObject streamSearch = new JSONObject(json);
        int res = streamSearch.getJSONObject("pageInfo").getInt("totalResults");
        if (res > 0) {
            streamsArr = streamSearch.getJSONArray("items");
            return true;
        }
        else
            return false;
    }

    public static String retrieveInfo(String channelId) throws IOException {

        if (checkStreamInfo(channelId)) {

            StringBuilder result = new StringBuilder();

            Youtuber youtuber = new Youtuber();
            youtuber.setChannelId(channelId);
            youtuber.setName(streamsArr.getJSONObject(0).getJSONObject("snippet").getString("channelTitle"));

            for (int i = 0; i < streamsArr.length(); i++) {
                JSONObject currentObj = streamsArr.getJSONObject(i);

                String title = currentObj.getJSONObject("snippet").getString("title");
                youtuber.setTitle(title);
                String liveContent = currentObj.getJSONObject("snippet").getString("liveBroadcastContent");
                youtuber.setEventType(liveContent);

                String streamId = currentObj.getJSONObject("id").getString("videoId");
                youtuber.setStreamId(streamId);
                URL streamUrl = new URL("https://www.googleapis.com/youtube/v3/videos?" +
                        "part=liveStreamingDetails" +
                        "&id=" + streamId +
                        "&key=" + DEVELOPER_KEY);

                Scanner in = new Scanner((InputStream) streamUrl.getContent());
                String json = "";
                while (in.hasNext()) {
                    json += in.nextLine();
                }
                in.close();

                JSONObject streamObj = new JSONObject(json);
                String scheduledStartTime = streamObj.getJSONArray("items")
                        .getJSONObject(0)
                        .getJSONObject("liveStreamingDetails")
                        .getString("scheduledStartTime");

                ZonedDateTime fromLocalZone = ZonedDateTime.parse(scheduledStartTime);
                ZonedDateTime toLocalZone = fromLocalZone.withZoneSameInstant(ZoneId.systemDefault());
                scheduledStartTime = toLocalZone.format(DateTimeFormatter.RFC_1123_DATE_TIME);
                YouTubeNotifBot.LOGGER.log(Level.INFO, "ScheduledStartTime: " + scheduledStartTime);

                int x = (int) ZonedDateTime.now().until(toLocalZone, ChronoUnit.HOURS);
                System.out.println("X = " + x);
                if (x < 0 || x > 24) {
                    YouTubeNotifBot.LOGGER.log(Level.INFO, ZonedDateTime.now() + ": The stream is not going to be soon...");
                }
                else {
                    YouTubeNotifBot.LOGGER.log(Level.INFO, ZonedDateTime.now() + ": The stream is going to be very soon!");
                    youtuber.setScheduledStartTime(scheduledStartTime);
                    result.append("Upcoming streams!\n" +
                            youtuber.getChannelName() + "\n" +
                            youtuber.getTitle() + " " + "\n" +
                            "Start time: " + youtuber.getScheduledStartTime());
                }
            }
            if (result.isEmpty())
                return "No upcoming streams...";
            else
                return result.toString();
        }
        return "No upcoming streams...";
    }
}