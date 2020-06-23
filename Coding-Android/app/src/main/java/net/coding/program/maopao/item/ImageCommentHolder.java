package net.coding.program.maopao.item;

import android.text.Html;
import android.view.View;

import net.coding.program.common.ImageLoadTool;
import net.coding.program.common.comment.BaseCommentHolder;
import net.coding.program.common.comment.BaseCommentParam;
import net.coding.program.common.model.BaseComment;
import net.coding.program.common.model.Commit;
import net.coding.program.common.model.DynamicObject;

/**
 * Created by chenchao on 15/3/31.
 * 可以带多张小图片的评论item
 */
public class ImageCommentHolder extends BaseCommentHolder {

    private ContentAreaMuchImages contentArea;

    public ImageCommentHolder(View convertView, View.OnClickListener onClickComment, Html.ImageGetter imageGetter, ImageLoadTool imageLoadTool, View.OnClickListener clickUser, View.OnClickListener clickImage) {
        super(convertView, onClickComment, imageGetter, imageLoadTool, clickUser);
        contentArea = new ContentAreaMuchImages(convertView, onClickComment, clickImage, imageGetter, imageLoadTool); //
    }

    public ImageCommentHolder(View convertView, int rootLayoutId, View.OnClickListener onClickComment, Html.ImageGetter imageGetter, ImageLoadTool imageLoadTool, View.OnClickListener clickUser, View.OnClickListener clickImage) {
        super(convertView, rootLayoutId, onClickComment, imageGetter, imageLoadTool, clickUser);
        contentArea = new ContentAreaMuchImages(convertView, onClickComment, clickImage, imageGetter, imageLoadTool); //
    }

    public ImageCommentHolder(View convertView, BaseCommentParam param) {
        super(convertView, param);
        this.contentArea = new ContentAreaMuchImages(convertView, param.onClickComment, param.mClickImage, param.imageGetter, param.imageLoadTool);
    }

    @Override
    public void setContent(Object data) {
        super.setContent(data);
        if (data instanceof BaseComment) {
            String content = ((BaseComment) data).content;
            contentArea.setDataContent(content, data);
            bindMathLabButton(content);
        } else if (data instanceof Commit) {
            contentArea.setDataContent(((Commit) data).getTitle(), data);
        } else if (data instanceof DynamicObject.DynamicProjectFileComment) {
            contentArea.setDataContent(((DynamicObject.DynamicProjectFileComment) data).getComment(), data);
        } else if (data instanceof DynamicObject.DynamicMergeRequest) {
            contentArea.setDataContent(((DynamicObject.DynamicMergeRequest) data).comment_content, data);
        }
    }
}
