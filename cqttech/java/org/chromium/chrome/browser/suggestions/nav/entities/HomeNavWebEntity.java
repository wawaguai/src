package org.chromium.chrome.browser.suggestions.nav.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeNavWebEntity {
    @SerializedName("data")
    private List<Category> category;

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public static class Category {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("weight")
        private int weight;

        @SerializedName("sides")
        private List<Nav> navs;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Nav> getNavs() {
            return navs;
        }

        public void setNavs(List<Nav> navs) {
            this.navs = navs;
        }
    }

    public static class Nav {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("weight")
        private int weight;

        @HomeNavType
        @SerializedName("type")
        private int type;

        @SerializedName("href")
        private String href;

        @SerializedName("logo")
        private String logo;

        @SerializedName("subscribed")
        private boolean subscribed;

        @SerializedName("updateTime")
        private long updateTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        @HomeNavType
        public int getType() {
            return type;
        }

        public void setType(@HomeNavType int type) {
            this.type = type;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public boolean isSubscribed() {
            return subscribed;
        }

        public void setSubscribed(boolean subscribed) {
            this.subscribed = subscribed;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
