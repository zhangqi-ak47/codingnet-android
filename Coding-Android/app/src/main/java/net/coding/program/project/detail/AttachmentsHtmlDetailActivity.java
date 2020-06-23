package net.coding.program.project.detail;

import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import net.coding.program.CodingGlobal;
import net.coding.program.R;
import net.coding.program.common.Global;
import net.coding.program.common.SimpleSHA1;
import net.coding.program.common.model.AttachmentFileObject;
import net.coding.program.common.util.FileUtil;
import net.coding.program.common.widget.BottomToolBar;
import net.coding.program.pickphoto.detail.ImagePagerFragment;
import net.coding.program.project.detail.file.FileDynamicActivity;
import net.coding.program.project.detail.file.MarkdownEditActivity_;
import net.coding.program.project.detail.file.TxtEditActivity;
import net.coding.program.route.BlankViewDisplay;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 展示某一项目文件目录下面HTML文件和Markdown类型文件的Activity
 * HTML文件就纯展示
 * Markdown文件拿到生成的HTML代码之后，使用了从网页版Coding中扒来的css样式,直接写在markdown-html模板里
 * Created by yangzhen
 */
@EActivity(R.layout.activity_attachments_html)
public class AttachmentsHtmlDetailActivity extends AttachmentsDetailBaseActivity {

    private static final int RESULT_MODIFY_TXT = 1;

    @ViewById
    WebView webview;
    @ViewById
    TextView textView;
    @ViewById
    View blankLayout;
    @ViewById
    BottomToolBar bottomToolBar;

    String urlFiles = Global.HOST_API + "/project/%d/files/%s/view";
    String urlMdPreview = Global.HOST_API + "/markdown/preview";
    AttachmentFileObject mFiles = new AttachmentFileObject();
    String markdown;

    public static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            Global.errorLog(e);
        }

        return outputStream.toString();
    }

    @AfterViews
    void initAttachmentsHtmlDetailActivity() {
        textView.setMovementMethod(new ScrollingMovementMethod());

        try {
            markdown = readTextFile(getAssets().open("markdown.html"));
        } catch (Exception e) {
            Global.errorLog(e);
        }

        Global.initWebView(webview);

        if (mExtraFile != null) {
            try {
                requestMd2Html(Global.readTextFile(mExtraFile));
                bottomToolBar.setVisibility(View.GONE);
            } catch (Exception e) {
                showButtomToast("读取文件错误");
                finish();
            }
        } else {
            urlFiles = String.format(urlFiles, mProjectObjectId, mAttachmentFileObject.file_id);
            showDialogLoading();
            updateLoadFile();
        }

        bottomToolBar.setClick(clickBottomBar);
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.project_attachment_txt;
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        super.parseJson(code, respanse, tag, pos, data);
        if (tag.equals(urlFiles)) {
            showDialogLoading();
            if (code == 0) {
                JSONObject file = respanse.getJSONObject("data").getJSONObject("file");
                mFiles = new AttachmentFileObject(file);
                String content = respanse.getJSONObject("data").optString("content");
                mAttachmentFileObject = mFiles;
//                if (mFiles.isHtml()) {
//                    hideDialogLoading();
//                    webview.loadDataWithBaseURL("about:blank", content, "text/html", "utf-8", null);
//                } else
//                if (mFiles.isMd()) {
                requestMd2Html(content);
//                }
                invalidateOptionsMenu();
                BlankViewDisplay.setBlank(1, this, true, blankLayout, null);


            } else {
                if (code == ImagePagerFragment.Companion.getHTTP_CODE_FILE_NOT_EXIST()) {
                    BlankViewDisplay.setBlank(0, this, true, blankLayout, null);
                } else {
                    BlankViewDisplay.setBlank(0, this, false, blankLayout, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getFileUrlFromNetwork();
                        }
                    });
                }

                hideProgressDialog();
                showErrorMsg(code, respanse);
            }
        } else if (tag.equals(urlMdPreview)) {
            if (code == 0) {
                hideProgressDialog();
                String html = respanse.optString("data", "");

                String htmlCacheName = SimpleSHA1.sha1(mFile.getPath());
                File htmlCache = new File(FileUtil.getCacheDir(this), htmlCacheName);
                if (htmlCache.exists()) {
                    htmlCache.delete();
                }
                TxtEditActivity.writeFile(htmlCache, html);
                showHtml(html);

            } else {
                hideProgressDialog();
                String content = Global.readTextFile(mFile);
                webview.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
//                webview.loadDataWithBaseURL("about:blank", markdown.replace("${webview_content}", content), "text/html", "UTF-8", null);
                textView.setText(content);

                showButtomToast(R.string.connect_service_fail);
            }
            showDialogLoading();
        }
    }

    private void showHtml(String html) {
        webview.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        CodingGlobal.setWebViewContent(webview, CodingGlobal.WebviewType.markdown, html);
    }

    private void requestMd2Html(String content) {
        RequestParams params = new RequestParams();
        params.put("content", content);
        postNetwork(urlMdPreview, params, urlMdPreview);
    }

    @OptionsItem
    public void action_add() {
    }

    @OptionsItem
    protected final void action_edit() {
        FileDynamicActivity.ProjectFileParam param = new FileDynamicActivity.ProjectFileParam(mAttachmentFileObject,
                mProject).openByEditor();
        MarkdownEditActivity_.intent(this)
                .mParam(param)
                .startForResult(RESULT_MODIFY_TXT);
    }

    @OnActivityResult(RESULT_MODIFY_TXT)
    protected void onResultModify(int result, Intent intent) {
        if (result == Activity.RESULT_OK) {
            setResult(result, intent);
            onRefresh();
        }
    }

    private void updateLoadFile() {
        mFiles = mAttachmentFileObject;
        mFile = FileUtil.getDestinationInExternalPublicDir(getFileDownloadPath(), mAttachmentFileObject.getSaveName(mProjectObjectId));
        if (mFile.exists()) {
            String htmlCacheName = SimpleSHA1.sha1(mFile.getPath());
            File htmlCache = new File(FileUtil.getCacheDir(this), htmlCacheName);
            if (!htmlCache.exists()) {
                String content = Global.readTextFile(mFile);
                requestMd2Html(content);
            } else {
                hideProgressDialog();
                String html = Global.readTextFile(htmlCache);
                showHtml(html);
            }
        } else {
            getFileUrlFromNetwork();
        }
    }

    @Override
    protected void onRefresh() {
        getFileUrlFromNetwork();
    }

    private void getFileUrlFromNetwork() {
        getNetwork(urlFiles, urlFiles);
    }


}
