package com.pic.optimize.http.constant;

/**
 * 公共配置信息
 */
public class Url {

    /**
     * 协议类型 http
     */
    public static final String PROTOCOL_HTTP = "http://";

    /**
     * 协议类型 https
     */
    public static final String PROTOCOL_HTTPS = "https://";
    /**
     * 主机类型
     */
    private static final String HOST = "api";
    /**
     * 主机类型 www
     */
    private static final String WWW = "www";
    /**
     * 主机类型 open
     */
    private static final String OPEN = "open";
    /**
     * 上传主机类型
     */
    private static final String UPLOAD_HOST = "upload";
    /**
     * 主机名
     */
    private static final String DOMAIN_NAME = "babytree";
    /**
     * 顶级域名
     */
    private static final String TOP_DOMAIN = "com";
    /**
     * 整个域名
     */
    private static final String WHOLE_DOMAIN = "." + DOMAIN_NAME + "." + TOP_DOMAIN;

    /**
     * URL地址 api.babytree.com
     */
    public static final String HOST_URL = PROTOCOL_HTTP + HOST + WHOLE_DOMAIN;

    /**
     * URL地址 www.babytree.com
     */
    public static final String WWW_URL = PROTOCOL_HTTP + WWW + WHOLE_DOMAIN;

    /**
     * URL地址 open.babytree.com   /   open.ldy.babytree-dev.com
     */
    public static final String OPEN_URL = PROTOCOL_HTTP + OPEN + WHOLE_DOMAIN;

    /**
     * AppLog
     */
    public static final String APP_LOG_URL = PROTOCOL_HTTP + "applog" + WHOLE_DOMAIN;


    /**
     * webview url
     */
    public static final String WEBVIEW_URL = PROTOCOL_HTTP + "webview" + WHOLE_DOMAIN;

    /**
     * wap Url
     */
    public static final String WAP_URL = "http://m" + WHOLE_DOMAIN;

    /**
     * wap https Url
     */
    public static final String WAP_HTTPS_URL = "https://m" + WHOLE_DOMAIN;

    /**
     * 上传URL
     */
    // public static final String UPLOAD_PHOTO =
    // "http://upload.api.babytree.com";
    public static final String UPLOAD_URL = PROTOCOL_HTTP + UPLOAD_HOST + WHOLE_DOMAIN;

    /**
     * 合并后上传图片地址s
     */
    // public static final String UPLOAD_PHOTO =
    // "http://upload.babytree.com/api";
    public static final String UPLOAD_PHOTO = UPLOAD_URL + "/api";
    /**
     * 育儿问答WAP页
     */
    public static final String HIDE_TITLE_ASK_URL = "/app/ask/";
    public static final String APP_ASK_URL = WEBVIEW_URL + HIDE_TITLE_ASK_URL;

    /**
     * 闪购URL
     */
    public static final String SALE_MALL_URL = "http://mall." + DOMAIN_NAME + "." + TOP_DOMAIN + "/flashsale/";
    /**
     * 美囤URL
     */
    public static final String SALES_MEITUN_URL = "http://btm.meitun.com";

    /**
     * 美屯域名
     */
    public static final String SALES_MEITUN_DOMAIN = ".meitun";

    /**
     * 知识链接
     */
    //public static final String KNOWLEDGE_URL = "http://knowledge." + DOMAIN_NAME + "img" + "." + TOP_DOMAIN + "/knowledge/detail/";

    public static final String GET_KNOWLEDGE_URL(boolean isHttps){
        StringBuilder sb = new StringBuilder();
        if(isHttps){
            sb.append("https://knowledge.");
        }else{
            sb.append("http://knowledge.");
        }
        sb.append(DOMAIN_NAME) .append( "img.").append(TOP_DOMAIN).append("/knowledge/detail/");
        return sb.toString();
    }

    /**
     * 知识分享链接
     */
    //public static final String KNOWLEDGE_SHARE_URL = WAP_URL + "/knowledge/detail?id=";

    public static final String GET_KNOWLEDGE_SHARE_URL(boolean isHttps){
        StringBuilder sb = new StringBuilder();
        if(isHttps){
            sb.append(WAP_HTTPS_URL);
        }else{
            sb.append(WAP_URL);
        }
        sb.append("/knowledge/detail?id=");
        return sb.toString();
    }
    /**
     * 孕育辞典链接
     * http://webview.babytree.com/mobile/encyclopedia/detail?id=6&type=share
     */
//    public static final String ENCYCLOPEDIA_URL = "http://webview" + "."
//            + DOMAIN_NAME + "." + TOP_DOMAIN + "/mobile/encyclopedia/detail?type=share&id=";

    public static final String GET_ENCYCLOPEDIA_URL(boolean isHttps){
        StringBuilder sb = new StringBuilder();
        if(isHttps){
            sb.append("https://webview.");
        }else{
            sb.append("http://webview.");
        }
        sb.append(DOMAIN_NAME).append(".").append(TOP_DOMAIN).append("/mobile/encyclopedia/detail?type=share&id=");
        return sb.toString();
    }

    /**
     * 帖子详情url前缀
     */
    public static final String TOPIC_DETAIL_URL_PREFIX = "http://www.babytree.com/community/topic_mobile.php";

    /**
     * 删除应用反馈
     */
    public static final String UNINSTALL_URL_PREFIX = WAP_URL + "/uninstall/create?";


    /**
     * 邀请链接
     */
    public static final String INVITE_UESR_URL = WAP_URL + "/share?uid=";

    /**
     * 时光直接下载万能链接
     */
    public static final String MOOD_RECORD_URL = "http://r.babytree.com/znpm1y"; // 心情记录

    public static final String PREGNANCY_TIME_CARD_URL = "http://r.babytree.com/ireBHYP";//任务卡-去往小时光

    public static final String PREGNANCY_TIME_HOME_ICON = "http://r.babytree.com/06Z1QVO";//首页小鹿

    public static final String PREGNANCY_TIME_HOME_BANNER = "http://r.babytree.com/1i1wd4G";//首页头图

    public static final String PREGNANCY_TIME_HOME_MCENG = "http://r.babytree.com/8fdHCgL";//首页蒙层


    public static final String PINGAN_URL = "https://www.jk.cn";
    public static final String PINGAN_PA_URL = "https://www.pajk.cn";
    public static final String PINGAN_TEST_URL = "https://www.test.pajk.cn";
    public static final String PINGAN_PRE_URL = "https://www.pre.jk.cn";

    /**
     * 搜索联想地址
     */
    public static final String SEARCH_SUGGEST_URL = "http://suggest.babytree.com/suggest";

    /**
     * 达人申请相关地址
     */
    public static final String PREGNANCY_EXPERT_HELP = "http://webview.babytree.com/pregnancy/pubplatform/help";
    public static final String PREGNANCY_EXPERT_LICENCE = "http://webview.babytree.com/pregnancy/pubplatform/agreement";

    public static final String PREGNANCY_PROTOCOL_URL = Url.WEBVIEW_URL + "/app/pregnancy/terms";
    public static final String PREGNANCY_PRIVACY_URL = "http://www.babytree.com/app/privacy.html";

}