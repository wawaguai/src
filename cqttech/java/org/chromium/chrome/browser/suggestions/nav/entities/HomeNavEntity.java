package org.chromium.chrome.browser.suggestions.nav.entities;

import java.util.Objects;

public class HomeNavEntity implements Comparable<HomeNavEntity> {
    public int id = -1; // 运营配置数据的唯一性标识
    public String itemName = "";
    public String description = "";
    public String href = ""; // 唯一字段
    public String logo = ""; // 可能会有中文字符，db层使用时要转换为 byte数组
    public @HomeNavType int type = HomeNavType.LINK;
    public @GroupId int groupId = GroupId.INVALID;
    public int categoryId = Integer.MIN_VALUE;
    public boolean defaultSubscribe = false;

    public int order = -1;
    public int comeFrom = -1;
    public long updateTime = -1;

    // description 字段没有使用到，未存数据库
    public boolean areContentSame(HomeNavEntity another) {
        return (id == another.id)
                && itemName.equals(another.itemName)
//                && description.equals(another.description)
                && href.equals(another.href)
                && type == another.type
                && logo.equals(another.logo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HomeNavEntity entity = (HomeNavEntity) o;
        return id == entity.id &&
                type == entity.type &&
                categoryId == entity.categoryId &&
                order == entity.order &&
                comeFrom == entity.comeFrom &&
                updateTime == entity.updateTime &&
                itemName.equals(entity.itemName) &&
                description.equals(entity.description) &&
                href.equals(entity.href) &&
                logo.equals(entity.logo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemName, description, href, logo, type, categoryId, order, comeFrom, updateTime);
    }

    @Override
    public int compareTo(HomeNavEntity o) {
        return Integer.compare(order, o.order);
    }

    @Override
    public String toString() {
        return "HomeNavEntity{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", description='" + description + '\'' +
                ", href='" + href + '\'' +
                ", logo='" + logo + '\'' +
                ", type=" + type +
                ", groupId" + groupId +
                ", categoryId=" + categoryId +
                ", defaultSubscribe=" + defaultSubscribe +
                ", order=" + order +
                ", comeFrom=" + comeFrom +
                ", updateTime=" + updateTime +
                '}';
    }

    public static class ComeFrom {
        public static final int UNKNOWN = -1; //未知来源
        public static final int OPERATE = 0; //运营
        public static final int BOOK_MARKER = 1; //书签
        public static final int HISTORY = 2; //历史记录
        public static final int CUSTOM = 3; //自定义
    }
}
