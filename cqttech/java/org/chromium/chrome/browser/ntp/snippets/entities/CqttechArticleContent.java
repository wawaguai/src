package org.chromium.chrome.browser.ntp.snippets.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CqttechArticleContent implements Serializable {
    //热点标题
    @SerializedName("title")
    public String title;

    //热点跳转链接
    @SerializedName("url")
    public String url;

    //热点标签
    @SerializedName("mark")
    public String mark;

    //热点分值
    @SerializedName("score")
    public String score;

    //热点序列号
    @SerializedName("no")
    public int no;

    //作者
    @SerializedName("author")
    public String author;

    //图片
    @SerializedName("thumb")
    public String thumb;

    //视频访问量
    @SerializedName("video_view")
    public String videoView;

    //评论量
    @SerializedName("comment_count")
    public String commentCount;

    //背景图
    @SerializedName("mark_bg_img")
    public String bgUrl;

    public CqttechArticleContent() {
    }

    public CqttechArticleContent(
            String title, String url, String mark, String score,
            int no, String author, String thumb, String videoView,
            String commentCount, String bgUrl) {
        this.title = title;
        this.url = url;
        this.mark = mark;
        this.score = score;
        this.no = no;
        this.author = author;
        this.thumb = thumb;
        this.videoView = videoView;
        this.commentCount = commentCount;
        this.bgUrl = bgUrl;
    }


    @Override
    public String toString() {
        return "HotContent{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", mark='" + mark + '\'' +
                ", score='" + score + '\'' +
                ", no=" + no +
                ", author='" + author + '\'' +
                ", thumb='" + thumb + '\'' +
                ", video_view='" + videoView + '\'' +
                ", comment_count='" + commentCount + '\'' +
                ", mark_bg_img='" + bgUrl + '\'' +
                '}';
    }
}
