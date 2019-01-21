package com.zhixin.kotlinapp.mvp.model.bean;

import java.util.List;

/**
 * Created by shine on 2017/5/4.
 */

public class ZhihuHomeListBean {
    /**
     * https://github.com/izzyleung/ZhihuDailyPurify/wiki/%E7%9F%A5%E4%B9%8E%E6%97%A5%E6%8A%A5-API-%E5%88%86%E6%9E%90
     * date : 日期 20170504
     * stories 当日新闻
     * TopStoriesBean 界面顶部 ViewPager 滚动显示的显示内容（子项格式同上）（请注意区分此处的 image 属性与 stories 中的 images 属性）
     */

    private String date; // 日期
    private List<StoriesBean> stories;//当日新闻
    private List<TopStoriesBean> top_stories;//界面顶部 ，这里做成Banner

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<StoriesBean> getStories() {
        return stories;
    }

    public void setStories(List<StoriesBean> stories) {
        this.stories = stories;
    }

    public List<TopStoriesBean> getTop_stories() {
        return top_stories;
    }

    public void setTop_stories(List<TopStoriesBean> top_stories) {
        this.top_stories = top_stories;
    }

    public static class StoriesBean {
        /**
         * images : ["https://pic3.zhimg.com/v2-7643b9864c75b1b83288342d95ee0a8e.jpg"]
         * type : 0
         * id : 9397441
         * ga_prefix : 050416
         * title : 一个未上市公司的老板说去年赚了 4 个亿，他在说谎吗？
         */

        private int type;//作用未知
        private int id;//url 与 share_url 中最后的数字（应为内容的 id）
        private String ga_prefix;//供 Google Analytics 使用
        private String title;//新闻标题
        private List<String> images;//图片
        private String date;//时间

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class TopStoriesBean {
        /**
         * image : https://pic1.zhimg.com/v2-20a52d17e71f83b8b4ea4103ab300b54.jpg
         * type : 0
         * id : 9397808
         * ga_prefix : 050407
         * title : 转折发生在 1902 年，爱因斯坦通过找关系，做了公务员
         */

        private String image;//图片
        private int type;//作用未知
        private int id;//url 与 share_url 中最后的数字（应为内容的 id）
        private String ga_prefix;//供 Google Analytics 使用
        private String title;//新闻标题

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
