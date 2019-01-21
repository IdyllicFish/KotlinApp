package com.zhixin.kotlinapp.mvp.model.bean;

import java.util.List;

/**
 * Created by shine on 2017/5/17.
 * 知乎新闻评论
 */

public class ZhihuInfoCommentBean {


    private List<CommentsBean> comments;

    public List<CommentsBean> getComments() {
        return comments;
    }

    public void setComments(List<CommentsBean> comments) {
        this.comments = comments;
    }

    public static class CommentsBean {
        /**
         * author : RR
         * content : 没人去看的话首先现在水族馆所有的生命会被饿死，然后人们对他们的了解不多，没有保护意识，紧接着就是大量的不杀得不到阻止，进一步导致物种的灭绝

         所以至少我们去的动物园水族馆，他们对于稀有动物会跟我们普及他们的珍贵性，让我们了解他们
         * avatar : http://pic1.zhimg.com/304743e9c837bafac4a56b0b2e422f38_im.jpg
         * time : 1494765646
         * id : 28995557
         * reply_to : {"content":"『超感人前辈说，你我都了解智人。\n\n如果他们感到安全，他们可能是超级耐撕的人。\n\n但是，一旦他们感到害怕，为他们找到与他人划清界限的理由，\n\n你会发现，还是这些人，往整个城市扔炸弹，自相残杀。对自己人都能干出这样的事情。\n\n可以想象，他们对不同的种族能干出什么事情？』\n\n他们只是觉得自己站在食物链顶端，便拥有了对一切生物生杀予夺的权力。\n即便折磨虐杀一个对自身生存毫无威胁生物，他们不仅认为这理所应当并且对他人的反对意见表示\u201c呸，圣母婊\u201d\n\n对于其他物种来说，怜悯？是什么？能吃吗？","status":0,"id":28962827,"author":"佾佾"}
         * likes : 0
         */

        private String author;
        private String content;
        private String avatar;
        private int time;
        private int id;
        private ReplyToBean reply_to;
        private int likes;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ReplyToBean getReply_to() {
            return reply_to;
        }

        public void setReply_to(ReplyToBean reply_to) {
            this.reply_to = reply_to;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public static class ReplyToBean {
            /**
             * content : 『超感人前辈说，你我都了解智人。

             如果他们感到安全，他们可能是超级耐撕的人。

             但是，一旦他们感到害怕，为他们找到与他人划清界限的理由，

             你会发现，还是这些人，往整个城市扔炸弹，自相残杀。对自己人都能干出这样的事情。

             可以想象，他们对不同的种族能干出什么事情？』

             他们只是觉得自己站在食物链顶端，便拥有了对一切生物生杀予夺的权力。
             即便折磨虐杀一个对自身生存毫无威胁生物，他们不仅认为这理所应当并且对他人的反对意见表示“呸，圣母婊”

             对于其他物种来说，怜悯？是什么？能吃吗？
             * status : 0
             * id : 28962827
             * author : 佾佾
             */

            private String content;
            private int status;
            private int id;
            private String author;
            private String error_msg;

            public String getError_msg() {
                return error_msg;
            }

            public void setError_msg(String error_msg) {
                this.error_msg = error_msg;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }
        }
    }
}
