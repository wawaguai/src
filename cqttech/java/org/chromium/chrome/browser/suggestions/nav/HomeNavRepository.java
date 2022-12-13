package org.chromium.chrome.browser.suggestions.nav;

import static java.util.Collections.sort;

import android.content.Context;

import org.chromium.chrome.browser.suggestions.nav.entities.GroupId;
import org.chromium.chrome.browser.suggestions.nav.entities.HomeNavEntity;
import org.chromium.chrome.browser.suggestions.nav.entities.HomeNavWebEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class HomeNavRepository {
    public static final int INSERT_SUCCESSFULLY = 0;
    public static final int OVER_LIMIT = -1;
    public static final int ALREADY_EXIST = -2;

    private static final String TAG = "HomeNavRepository";

    private final IHomeNavWebSource webSource = new HomeNavWebSourceImpl();

    public Observable<List<HomeNavEntity>> requestHomePageItems(Context context, boolean useCache) {
        return webSource.getHomePageItems(context, useCache).map(response -> {
            List<HomeNavEntity> result = new ArrayList<>();
            if (response.isValid()) {
                List<HomeNavWebEntity.Category> categories = response.getData().getCategory();
                for (HomeNavWebEntity.Category category : categories) {
                    List<HomeNavWebEntity.Nav> navs = category.getNavs();
                    List<HomeNavEntity> entities = new ArrayList<>(navs.size());
                    for (HomeNavWebEntity.Nav nav : navs) {
                        HomeNavEntity entity = new HomeNavEntity();
                        entity.id = nav.getId();
                        entity.itemName = nav.getName();
                        entity.description = nav.getDescription();
                        entity.defaultSubscribe = nav.isSubscribed();
                        entity.href = nav.getHref();
                        entity.logo = nav.getLogo();
                        entity.type = nav.getType();
                        entity.groupId = GroupId.INVALID;
                        entity.order = nav.getWeight();
                        entity.comeFrom = HomeNavEntity.ComeFrom.OPERATE;
                        entity.updateTime = nav.getUpdateTime();
                        entity.categoryId = category.getId();
                        entities.add(entity);
                    }
                    sort(entities);
                    result.addAll(entities);
                }

                sort(result);
            }

            return result;
        });
    }
}
