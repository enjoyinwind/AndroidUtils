package com.snda.gfriend.common.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snda.gfriend.R;
import com.snda.gfriend.common.callback.Callback;
import com.snda.gfriend.common.pool.ObjectPool;
import com.snda.gfriend.model.Picture;
import com.snda.mcommon.support.image.Image;
import com.snda.mcommon.support.image.ShowImageActivity;
import com.snda.mcommon.util.L;
import com.snda.mcommon.util.PicassoUtil;
import com.snda.mcommon.util.ScreenUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaofeng on 2016/8/6.
 */
public class GridImageView extends ViewGroup{
    private static final String TAG = GridImageView.class.getSimpleName();
    private static final int GRID_COLUMN_COUNT = 3;
    private static final int GRID_COLUMN_WIDTH_DP = 105;
    private static final int GRID_COLUMN_SPACING_DP = 8;
    private static int GRID_COLUMN_WIDTH_PX;
    private static int GRID_COLUMN_SPACING_PX;

    //单张图时，图片按比例显示，但不能超过最大宽高
    private static final int MAX_WIDTH_DP = 215;
    private static final int MAX_HEIGHT_DP = 215;
    private static int MAX_WIDTH_PX = 0;
    private static int MAX_HEIGHT_PX = 0;
    private static float RATIO = 0;

    private int realColumnWidth;

    static {
        try {
            ObjectPool.init(ImageView.class, 30, Context.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public GridImageView(Context context) {
        super(context);
        init();
    }

    public GridImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        if(GRID_COLUMN_WIDTH_PX == 0){
            GRID_COLUMN_WIDTH_PX = ScreenUtil.convertDipToPixel(getContext(), GRID_COLUMN_WIDTH_DP);
            GRID_COLUMN_SPACING_PX = ScreenUtil.convertDipToPixel(getContext(), GRID_COLUMN_SPACING_DP);
        }
    }

    private void initRatio(){
        if(MAX_WIDTH_PX == 0){
            MAX_WIDTH_PX = ScreenUtil.convertDipToPixel(getContext(), MAX_WIDTH_DP);
        } else {
            return;
        }

        if(MAX_HEIGHT_PX == 0){
            MAX_HEIGHT_PX = ScreenUtil.convertDipToPixel(getContext(), MAX_HEIGHT_DP);
        }
        if(RATIO == 0){
            RATIO = (int)((float) MAX_WIDTH_PX / MAX_HEIGHT_PX);
        }
    }

    public ImageView createImageView(){
        try {
            ImageView imageView = ObjectPool.obtain(ImageView.class, getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView(imageView);
            return imageView;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void bind(final List<Picture> pictures, final Callback clickCallback){
        if (pictures != null && pictures.size() > 0) {
            adjustView(pictures.size());
            setVisibility(VISIBLE);

            int size = pictures.size();
            if(size == 1){
                initRatio();
                Picture picture = pictures.get(0);
                int width = 0, height = 0;
                try{
                    int index1 = picture.middle.lastIndexOf("_");
                    int index2 = picture.middle.lastIndexOf("x");
                    int picWidth = Integer.parseInt(picture.middle.substring(index1 + 1, index2));
                    int picHeight = Integer.parseInt(picture.middle.substring(index2 + 1, picture.middle.length() - 4));
                    if(picWidth <= MAX_WIDTH_PX && picHeight <= MAX_HEIGHT_PX){
                        width = picWidth;
                        height = picHeight;
                    } else {
                        float picRatio = (float) picWidth / picHeight;
                        if(picRatio > RATIO){
                            width = MAX_WIDTH_PX;
                            height = (int)(((float)picHeight / picWidth) * width);
                        } else if(picRatio <= RATIO){
                            height = MAX_HEIGHT_PX;
                            width = (int)(picRatio * height);
                        }
                    }
                }catch (Exception e){
                    L.e(TAG, "picture format error: " + picture.middle);
                    width = ScreenUtil.convertDipToPixel(getContext(), 105);
                    height = width;
                }

                ImageView imageView = (ImageView)getChildAt(0);
                imageView.setLayoutParams(new LayoutParams(width, height));
                PicassoUtil.showWithSize(imageView, getContext(), picture.middle, width, height, R.drawable.photo);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ArrayList<Image> imageList = convertPictureList(pictures);
                        ShowImageActivity.go((Activity) getContext(), 0, imageList);
                        if(clickCallback != null){
                            clickCallback.callback();
                        }
                    }
                });
            } else if(size > 1){
                for(int i = 0; i < size; i++){
                    Picture picture = pictures.get(i);
                    ImageView imageView = (ImageView)getChildAt(i);
                    if(realColumnWidth == 0){
                        PicassoUtil.show(imageView, getContext(), picture.square, R.drawable.photo);
                    }else {
                        PicassoUtil.showWithSize(imageView, getContext(), picture.square, realColumnWidth, realColumnWidth, R.drawable.photo);
                    }


                    final int position = i;
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ArrayList<Image> imageList = convertPictureList(pictures);
                            ShowImageActivity.go((Activity) getContext(), position, imageList);
                            if(clickCallback != null){
                                clickCallback.callback();
                            }
                        }
                    });
                }
            }
        } else {
            releaseViews();
            setVisibility(GONE);
        }
    }

    private void adjustView(int requireSize){
        int num = getChildCount();
        if(num == requireSize){
            return;
        } else if(num > requireSize){
            for(int i = requireSize; i < num; i++){
                ImageView imageView = (ImageView)getChildAt(i);
                imageView.setImageDrawable(null);
                ObjectPool.release(imageView);
            }

            removeViews(requireSize, num - requireSize);
        } else {
            int count = requireSize - num;
            for(int i = 0; i < count; i++){
                createImageView();
            }
        }
    }

    private ArrayList<Image> convertPictureList(final List<Picture> pictures){
        final ArrayList<Image> imageList = new ArrayList<Image>(pictures.size());
        for (Picture picture : pictures) {
            Image image = new Image();
            image.smallUrl = picture.small;
            image.url = picture.large;
            imageList.add(image);
        }

        return imageList;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int num = getChildCount();
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width = widthSize;
        int height = 0;
        if(num == 1){
            View view = getChildAt(0);
            LayoutParams layoutParams = view.getLayoutParams();
            width = layoutParams.width;
            height = layoutParams.height;
        } else if(num > 1){
            if(realColumnWidth == 0){
                int actualWidth =  width - (GRID_COLUMN_COUNT - 1) * GRID_COLUMN_SPACING_PX;
                actualWidth = actualWidth / 3;
                int specifyGridColumnWidth = GRID_COLUMN_WIDTH_PX;
                if(actualWidth < specifyGridColumnWidth){
                    realColumnWidth = actualWidth;
                } else {
                    realColumnWidth = specifyGridColumnWidth;
                }
            }

            int rows;
            if(num % GRID_COLUMN_COUNT == 0){
                rows = num / GRID_COLUMN_COUNT;
            } else {
                rows = num / GRID_COLUMN_COUNT + 1;
            }
            height = rows * realColumnWidth + (rows - 1) * GRID_COLUMN_SPACING_PX;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int num = getChildCount();
        if(num == 1){
            View view = getChildAt(0);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        } else {
            int currentLeft = 0;
            int currentTop = 0;
            int oneStepLength = GRID_COLUMN_SPACING_PX + realColumnWidth;
            for(int i = 0; i < num; i++){
                if(i != 0 && i % GRID_COLUMN_COUNT == 0){
                    currentLeft = 0;
                    currentTop += oneStepLength;
                }

                View view = getChildAt(i);
                view.layout(currentLeft, currentTop, currentLeft + realColumnWidth, currentTop + realColumnWidth);
                currentLeft += oneStepLength;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseViews();
    }

    private void releaseViews(){
        for(int i = 0; i < getChildCount(); i++){
            ImageView imageView = (ImageView)getChildAt(i);
            imageView.setImageDrawable(null);
            ObjectPool.release(imageView);
        }

        removeAllViews();
    }


}
