package com.snda.gfriend.common.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.snda.gfriend.R;
import com.snda.gfriend.business.home.findFriend.PostListImageItem;
import com.snda.gfriend.common.callback.Callback;
import com.snda.gfriend.model.Picture;
import com.snda.mcommon.support.image.Image;
import com.snda.mcommon.support.image.ShowImageActivity;
import com.snda.mcommon.util.L;
import com.snda.mcommon.util.PicassoUtil;
import com.snda.mcommon.util.ScreenUtil;
import com.snda.mcommon.xwidget.SimpleArrayAdapter;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaofeng02 on 2015/5/29.
 */
@EViewGroup(R.layout.custom_multi_image_view)
public class CustomMultiImageView extends FrameLayout{
    private static final String TAG = CustomMultiImageView.class.getSimpleName();
    private static final int MAX_WIDTH_DP = 215;
    private static final int MAX_HEIGHT_DP = 215;
    private static int MAX_WIDTH = 0;
    private static int MAX_HEIGHT = 0;
    private static float RATIO = 0;
    private static int REAL_COLUMN_WIDTH = 0;
    private static int GRID_COLUMN_WIDTH = 105;
    private static final int GRID_COLUMN_COUNT = 3;
    private static final int GRID_COLUMN_HORIZONTAL_SPACING = 8;

    @ViewById
    GridView gv_image_list;
    @ViewById
    ImageView iv_image;

    public CustomMultiImageView(Context context) {
        super(context);
    }

    public CustomMultiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bind(final List<Picture> pictures, final Callback clickCallback){
        if (pictures != null && pictures.size() > 0) {
            setVisibility(VISIBLE);

            if(pictures.size() > 1){
                initGridColumnWidth();
                gv_image_list.setColumnWidth(REAL_COLUMN_WIDTH);
                iv_image.setImageDrawable(null);
                iv_image.setVisibility(GONE);
                SimpleArrayAdapter<Picture, PostListImageItem> adapter = new SimpleArrayAdapter<Picture, PostListImageItem>(getContext()) {
                    @Override
                    protected PostListImageItem build(Context context) {
                        PostListImageItem result = new PostListImageItem(context);
                        result.setLayoutParams(new AbsListView.LayoutParams(REAL_COLUMN_WIDTH, REAL_COLUMN_WIDTH));
                        return result;
                    }
                };
                adapter.addAll(pictures);
                gv_image_list.setAdapter(adapter);

                gv_image_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final ArrayList<Image> imageList = convertPictureList(pictures);
                        ShowImageActivity.go((Activity) getContext(), position, imageList);
                        if(clickCallback != null){
                            clickCallback.callback();
                        }
                    }
                });
                gv_image_list.setVisibility(VISIBLE);
            } else if(pictures.size() == 1){
                gv_image_list.setAdapter(null);
                gv_image_list.setVisibility(GONE);

                initRatio();
                Picture picture = pictures.get(0);
                int width = 0, height = 0;
                try{
                    int index1 = picture.middle.lastIndexOf("_");
                    int index2 = picture.middle.lastIndexOf("x");
                    int picWidth = Integer.parseInt(picture.middle.substring(index1 + 1, index2));
                    int picHeight = Integer.parseInt(picture.middle.substring(index2 + 1, picture.middle.length() - 4));
                    if(picWidth <= MAX_WIDTH && picHeight <= MAX_HEIGHT){
                        width = picWidth;
                        height = picHeight;
                    } else {
                        float picRatio = (float) picWidth / picHeight;
                        if(picRatio > RATIO){
                            width = MAX_WIDTH;
                            height = (int)(((float)picHeight / picWidth) * width);
                        } else if(picRatio <= RATIO){
                            height = MAX_HEIGHT;
                            width = (int)(picRatio * height);
                        }
                    }
                }catch (Exception e){
                    L.e(TAG, "picture format error: " + picture.middle);
                    width = ScreenUtil.convertDipToPixel(getContext(), 105);
                    height = width;
                }

                ViewGroup.LayoutParams layoutParams = iv_image.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                PicassoUtil.showWithSize(iv_image, getContext(), picture.middle, width, height, R.drawable.photo);

                iv_image.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ArrayList<Image> imageList = convertPictureList(pictures);
                        ShowImageActivity.go((Activity) getContext(), 0, imageList);
                        if(clickCallback != null){
                            clickCallback.callback();
                        }
                    }
                });
                iv_image.setVisibility(VISIBLE);
            } else {
                gv_image_list.setAdapter(null);
                gv_image_list.setVisibility(GONE);
                iv_image.setImageDrawable(null);
                iv_image.setVisibility(GONE);
            }
        } else {
            setVisibility(GONE);
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

    private void initRatio(){
        if(MAX_WIDTH == 0){
            MAX_WIDTH = ScreenUtil.convertDipToPixel(getContext(), MAX_WIDTH_DP);
        } else {
            return;
        }

        if(MAX_HEIGHT == 0){
            MAX_HEIGHT = ScreenUtil.convertDipToPixel(getContext(), MAX_HEIGHT_DP);
        }
        if(RATIO == 0){
            RATIO = (int)((float)MAX_WIDTH / MAX_HEIGHT);
        }
    }

    private void initGridColumnWidth(){
        if(REAL_COLUMN_WIDTH == 0){
            int screenWidth = ScreenUtil.getWidth(getContext());
            MarginLayoutParams layoutParams = (MarginLayoutParams)getLayoutParams();
            int actualWidth =  screenWidth - layoutParams.leftMargin - layoutParams.rightMargin;
            actualWidth -= (GRID_COLUMN_COUNT - 1) * ScreenUtil.convertDipToPixel(getContext(), GRID_COLUMN_HORIZONTAL_SPACING);
            actualWidth = actualWidth / 3;
            int specifyGridColumnWidth = ScreenUtil.convertDipToPixel(getContext(), GRID_COLUMN_WIDTH);
            if(actualWidth < specifyGridColumnWidth){
                REAL_COLUMN_WIDTH = actualWidth;
            } else {
                REAL_COLUMN_WIDTH = specifyGridColumnWidth;
            }
        }
    }
}
