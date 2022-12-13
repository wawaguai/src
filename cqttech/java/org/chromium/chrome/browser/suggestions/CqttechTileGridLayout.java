package org.chromium.chrome.browser.suggestions;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.chromium.chrome.R;

import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.chrome.browser.util.MathUtils;
import org.wwg.common.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

public class CqttechTileGridLayout extends FrameLayout {
    private final int mVerticalSpacing;
    private final int mMinHorizontalSpace;

    private int mMaxWidth;

    private int mMaxRows;
    private int mMaxColumns;
    private int mExtraVerticalSpacing;

    /**
     * Constructor for inflating from XML.
     *
     * @param context The view context in which this item will be shown.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public CqttechTileGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = getResources();
        mVerticalSpacing = SuggestionsConfig.useModernLayout()
                ? res.getDimensionPixelOffset(R.dimen.tile_grid_layout_vertical_spacing_modern)
                : res.getDimensionPixelOffset(R.dimen.tile_grid_layout_vertical_spacing);
        mMinHorizontalSpace =
                res.getDimensionPixelOffset(R.dimen.tile_grid_layout_min_horizontal_spacing);
        mMaxWidth = DisplayUtils.getScreenMetricsWidth(context);
    }

    /**
     * Sets the maximum number of rows to display. Any items that don't fit will be hidden.
     */
    public void setMaxRows(int rows) {
        mMaxRows = rows;
    }

    /**
     * Sets the maximum number of columns to display. Any items that don't fit will be hidden.
     */
    public void setMaxColumns(int columns) {
        mMaxColumns = columns;
    }

    /**
     * Sets the extra vertical spacing that must be used. It will be distributed evenly above each
     * row.
     */
    public void setExtraVerticalSpacing(int spacing) {
        if (mExtraVerticalSpacing == spacing) {
            return;
        }
        mExtraVerticalSpacing = spacing;

        // Clear the measure cache for this view and make sure it will be remeasured.
        forceLayout();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp instanceof MarginLayoutParams) {
            int ltMargin = ((MarginLayoutParams) lp).leftMargin;
            if (ltMargin > 0) {
                mMaxWidth -= ltMargin;
            }

            int rtMargin = ((MarginLayoutParams) lp).rightMargin;
            if (rtMargin > 0) {
                mMaxWidth -= rtMargin;
            }
        }

        int paddingStart = getPaddingStart();
        if (paddingStart > 0) {
            mMaxWidth -= paddingStart;
        }

        int paddingEnd = getPaddingEnd();
        if (paddingEnd > 0) {
            mMaxWidth -= paddingEnd;
        }
    }

    public void addTileView(CqttechTileView tileView) {
        ViewGroup.LayoutParams lp = tileView.getLayoutParams();
        if (lp == null) {
            lp = generateDefaultLayoutParams();
        }
        addViewInLayout(tileView, -1, lp, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = Math.min(MeasureSpec.getSize(widthMeasureSpec), mMaxWidth);
        int childCount = getChildCount();
        if (childCount == 0) {
            setMeasuredDimension(totalWidth, resolveSize(0, heightMeasureSpec));
            return;
        }

        // Measure the children. We don't use the ViewGroup.measureChildren() method here because
        // it only measures visible children. In a situation where a child is invisible before
        // this measurement and we decide to show it after the measurement, it will not have its
        // dimensions and will not be displayed.
        int childHeight = 0;
        int childWidth = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            childWidth = Math.max(childWidth, child.getMeasuredWidth());
            childHeight = Math.max(childHeight, child.getMeasuredHeight());
        }

        childWidth = Math.min(childWidth, computeChildMaxWithDimensions(totalWidth, mMaxColumns));

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            lp.width = childWidth;
            lp.height = childHeight;
            measureChild(child, MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
        }

        // Determine the number of columns that will fit.

        int numColumns = MathUtils.clamp(
                (totalWidth + mMinHorizontalSpace) / (childWidth + mMinHorizontalSpace), 1,
                mMaxColumns);

        // Determine how much padding to use between and around the tiles.
        int gridWidthMinusColumns = Math.max(0, totalWidth - numColumns * childWidth);
        Pair<Integer, Integer> gridProperties =
                computeHorizontalDimensions(gridWidthMinusColumns, numColumns);
        int gridStart = gridProperties.first;
        int horizontalSpacing = gridProperties.second;

        // Limit the number of rows to mMaxRows.
        int visibleChildCount = Math.min(childCount, mMaxRows * numColumns);

        // Arrange the visible children in a grid.
        int numRows = (visibleChildCount + numColumns - 1) / numColumns;
        int paddingTop = getPaddingTop();
        boolean isRtl = ApiCompatibilityUtils.isLayoutRtl(this);

        List<CqttechTileView> orderedChildren = getCorrectTileViewOrder(numColumns, numRows);

        for (int i = 0; i < visibleChildCount; i++) {
            View child = orderedChildren.get(i);
            child.setVisibility(View.VISIBLE);
            int row = i / numColumns;
            int column = i % numColumns;
            int verticalOffset = Math.round(mExtraVerticalSpacing * ((float) (row + 1) / numRows));
            int childTop = row * (childHeight + mVerticalSpacing) + verticalOffset;
            int childStart = gridStart + (column * (childWidth + horizontalSpacing));
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            layoutParams.setMargins(isRtl ? 0 : childStart, childTop, isRtl ? childStart : 0, 0);
            child.setLayoutParams(layoutParams);
        }

        // Hide any extra children in case there are more than needed for the maximum number of
        // rows.
        for (int i = visibleChildCount; i < childCount; i++) {
            orderedChildren.get(i).setVisibility(View.GONE);
        }

        int totalHeight = paddingTop + getPaddingBottom() + numRows * childHeight
                + (numRows - 1) * mVerticalSpacing + mExtraVerticalSpacing;

        setMeasuredDimension(totalWidth, resolveSize(totalHeight, heightMeasureSpec));
    }

    /**
     * Returns a list of {@link CqttechTileView}s in the order that they should be displayed in the tile
     * grid. The {@link CqttechTileView}s in the list are the children of the {@link CqttechTileGridLayout}.
     *
     * If there is a home page tile view:
     *  - For multiple rows: pin it to the very first position.
     *  - For a single row: keep the position or use it as last tile in that role.
     *
     * @param numColumns The number of columns that the tile grid will display.
     * @param numRows The number of rows that the tile grid will display.
     * @return A list of {@link CqttechTileView}s in the order they should be displayed.
     */
    private List<CqttechTileView> getCorrectTileViewOrder(int numColumns, int numRows) {
        List<CqttechTileView> orderedChildren = new ArrayList<>(getChildCount());

        for (int i = 0; i < getChildCount(); i++) {
            CqttechTileView view = (CqttechTileView) getChildAt(i);

            if (view.getTileSource() != TileSource.HOMEPAGE) {
                orderedChildren.add(view);
            } else if (numRows > 1) {
                orderedChildren.add(0, view);
            } else {
                orderedChildren.add(Math.min(i, numColumns - 1), view);
            }
        }

        return orderedChildren;
    }

    /**
     * @param availableWidth The space available to spread between and around the tiles.
     * @param numColumns The number of columns to be organised.
     * @return The [gridStart, horizontalSpacing] pair of dimensions.
     */
    @VisibleForTesting
    Pair<Integer, Integer> computeHorizontalDimensions(int availableWidth, int numColumns) {
        int spaceWidth = availableWidth - 2 * mMinHorizontalSpace;
        float horizontalSpacing = (float) spaceWidth / Math.max(1, numColumns - 1);
        return Pair.create(mMinHorizontalSpace, Math.round(horizontalSpacing));
    }


    int computeChildMaxWithDimensions(int availableWidth, int numColumns) {
        int spaceSize = mMinHorizontalSpace * Math.max(0, numColumns - 1);
        return (int) ((availableWidth - spaceSize) * 1f / numColumns);
    }

    @Nullable
    public CqttechTileView getTileView(SiteSuggestion suggestion) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            CqttechTileView tileView = (CqttechTileView) getChildAt(i);
            if (suggestion.equals(tileView.getData())) return tileView;
        }
        return null;
    }
}
