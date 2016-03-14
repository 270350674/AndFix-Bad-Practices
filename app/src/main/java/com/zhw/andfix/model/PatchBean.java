package com.zhw.andfix.model;

import java.io.Serializable;

/**
 * Created by zhonghw on 2016/3/11.
 */
public class PatchBean implements Serializable {
    public String app_v;    //应用的版本
    public String path_v;   //补丁的版本
    public String url;  //补丁的下载地址
}
