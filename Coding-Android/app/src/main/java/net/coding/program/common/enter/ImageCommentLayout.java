package net.coding.program.common.enter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tbruyelle.rxpermissions2.RxPermissions;

import net.coding.program.R;
import net.coding.program.common.Global;
import net.coding.program.common.ImageInfo;
import net.coding.program.common.ImageLoadTool;
import net.coding.program.common.maopao.ContentAreaMushImageOption;
import net.coding.program.common.util.PermissionUtil;
import net.coding.program.pickphoto.PhotoPickActivity;
import net.coding.program.pickphoto.PhotoPickDetailActivity;

import java.util.ArrayList;

/**
 * Created by chenchao on 15/3/30.
 * 可以发图片的评论输入框
 */
public class ImageCommentLayout {

    public static final int RESULT_REQUEST_COMMENT_IMAGE = 1100;
    public static final int RESULT_REQUEST_COMMENT_IMAGE_DETAIL = 1101;

    private EnterLayout mEnterLayout;
    private View mRootLayout;

    private ViewGroup mFlowLayout;
    private ArrayList<ImageView> mImageViews = new ArrayList<>();
    private Activity mActivity;
    private ImageLoadTool mImageLoader;
    private ArrayList<ImageInfo> mArrayImages = new ArrayList<>();
    private View.OnClickListener mClickImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag(R.id.image);
            Intent intent = new Intent(mActivity, PhotoPickDetailActivity.class);
            intent.putExtra(PhotoPickDetailActivity.Companion.getPHOTO_BEGIN(), pos);
            intent.putExtra(PhotoPickDetailActivity.Companion.getEXTRA_MAX(), Global.PHOTO_MAX_COUNT);
            intent.putExtra(PhotoPickDetailActivity.Companion.getPICK_DATA(), mArrayImages);
            intent.putExtra(PhotoPickDetailActivity.Companion.getALL_DATA(), mArrayImages);
            mActivity.startActivityForResult(intent, RESULT_REQUEST_COMMENT_IMAGE_DETAIL);
        }
    };

    public ImageCommentLayout(Activity activity, View.OnClickListener onClickSend, ImageLoadTool imageLoader) {
        mEnterLayout = new EnterLayout(activity, onClickSend, EnterLayout.Type.TextOnly) {
            @Override
            protected boolean sendButtonEnable() {
                return getContent().length() > 0 ||
                        !mArrayImages.isEmpty();
            }
        };

        mImageLoader = imageLoader;
        mActivity = activity;

        View v = activity.findViewById(R.id.commonEnterRoot);
        mRootLayout = v;
        v.findViewById(R.id.commentImageButton).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                new RxPermissions((FragmentActivity) activity)
                        .request(PermissionUtil.STORAGE)
                        .subscribe(granted -> {
                            if (granted) {
                                Intent intent = new Intent(mActivity, PhotoPickActivity.class);
                                intent.putExtra(PhotoPickActivity.Companion.getEXTRA_PICKED(), mArrayImages);
                                mActivity.startActivityForResult(intent, RESULT_REQUEST_COMMENT_IMAGE);
                            }
                        });
            }
        });

        mFlowLayout = (ViewGroup) v.findViewById(R.id.imageLayout);
        mFlowLayout.setVisibility(View.GONE);
    }

    public EnterLayout getEnterLayout() {
        return mEnterLayout;
    }

    public void hide() {
        mRootLayout.setVisibility(View.GONE);
    }

    public void onActivityResult(int RESULT_TYPE, Intent data) {
        if (data == null) {
            return;
        }

        if (RESULT_TYPE == RESULT_REQUEST_COMMENT_IMAGE) {
            @SuppressWarnings("unchecked")
            ArrayList<ImageInfo> images = (ArrayList) data.getSerializableExtra("data");
            mArrayImages.clear();
            mArrayImages.addAll(images);
            updateCommentImage();

        } else if (RESULT_TYPE == RESULT_REQUEST_COMMENT_IMAGE_DETAIL) {
            @SuppressWarnings("unchecked")
            ArrayList<ImageInfo> images = (ArrayList) data.getSerializableExtra("data");
            mArrayImages = images;
            updateCommentImage();
        }
    }

    public void clearContent() {
        mEnterLayout.hideKeyboard();
        mEnterLayout.clearContent();
        mArrayImages.clear();
        updateCommentImage();
    }

    private void updateCommentImage() {
        final int imageUrlCount = mArrayImages.size();
        if (imageUrlCount == 0) {
            mFlowLayout.setVisibility(View.GONE);
            mFlowLayout.removeAllViews();
            return;
        }

        mFlowLayout.setVisibility(View.VISIBLE);
        int count = mFlowLayout.getChildCount();

        if (imageUrlCount > count) {
            int need = imageUrlCount - count;
            LayoutInflater inflater = LayoutInflater.from(mFlowLayout.getContext());
            for (int i = 0; i < need; ++i) {
                inflater.inflate(R.layout.comment_image, mFlowLayout);
            }
        } else if (imageUrlCount < count) {
            int release = count - imageUrlCount;
            for (int i = 0; i < release; ++i) {
                mFlowLayout.removeViewAt(count - 1 - i);
            }
        }

        for (int i = 0; i < imageUrlCount; ++i) {
            ImageView image = (ImageView) mFlowLayout.getChildAt(i);
            image.setOnClickListener(mClickImage);
            image.setTag(R.id.image, i);
            mImageLoader.loadImage(image, mArrayImages.get(i).path, ContentAreaMushImageOption.imageOptions);
        }

        mEnterLayout.updateSendButtonStyle();
    }

    public ArrayList<ImageInfo> getPickPhotos() {
        return mArrayImages;
    }
}
