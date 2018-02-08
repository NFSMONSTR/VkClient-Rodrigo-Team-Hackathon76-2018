package VkClient;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VkAuth {

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = "";
            if (param.split("=").length>1) {
                value = param.split("=")[1];
            } else {
                value = "";
            }
            map.put(name, value);
        }
        return map;
    }

    public static String getCode(String location, String redirectUrl) {
        location = location.replace(redirectUrl.concat("#"), "");
        Map<String, String> map = getQueryMap(location);
        return map.get("code");
    }
}