package com.douyinpay.api.indirectmerchant.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QueryAppIdResponse {
    /**
     * 字段含义：应用信息列表。
     */
    @SerializedName("app_info_list") private List<AppInfo> appInfoList;
    public List<AppInfo> getAppInfoList() { return appInfoList; }
    public void setAppInfoList(List<AppInfo> value) { appInfoList = value; }
}
