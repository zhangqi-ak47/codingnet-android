package net.coding.program.common.model;

import com.loopj.android.http.RequestParams;

/**
 * Created by chenchao on 15/7/23.
 * 返回post参数，现在只是一个单纯的数据类
 */
public class RequestData {
    public String url;
    public RequestParams params;

    public RequestData(String url, RequestParams params) {
        this.url = url;
        this.params = params;
    }

    public void setContent(String input) {
        params.put("content", input);
    }
}
