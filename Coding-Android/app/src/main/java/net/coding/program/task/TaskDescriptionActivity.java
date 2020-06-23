package net.coding.program.task;

import android.content.Intent;

import net.coding.program.R;
import net.coding.program.common.Global;
import net.coding.program.common.base.MDEditPreviewActivity;
import net.coding.program.common.model.SingleTask;
import net.coding.program.common.model.topic.TopicData;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.json.JSONException;
import org.json.JSONObject;

@EActivity(R.layout.activity_task_description)
public class TaskDescriptionActivity extends MDEditPreviewActivity implements TaskDescrip {

    @Extra
    SingleTask.TaskDescription descriptionData;

    @Extra
    int taskId;

    @Extra
    String projectPath;
//    int projectId;

    String HOST_DESCRIPTION = Global.HOST_API + "/task/%s/description";

    private TopicData modifyData = new TopicData();

    @AfterViews
    protected final void initTaskDescriptionActivity() {
        editFragment = TaskDespEditFragment_.builder().build();
        previewFragment = TaskDespPreviewFragment_.builder().build();
        initEditPreviewFragment();

        String markdown = descriptionData.markdown;
        if (markdown.isEmpty()) {
            switchEdit();
        } else {
            modifyData.content = markdown;
            switchPreview();
        }
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(HOST_DESCRIPTION)) {
            hideProgressDialog();
            if (code == 0) {
                showButtomToast("修改描述成功");
                setResult(RESULT_OK);
                finish();
            } else {
                showErrorMsg(code, respanse);
            }
        }
    }

    @Override
    public void closeAndSave(String s) {
        Intent intent = new Intent();
        intent.putExtra("data", s);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public String createLocateHtml(String s) {
        try {
            final String bubble = Global.readTextFile(getAssets().open("topic-android.html"));
            return bubble.replace("${webview_content}", s);
        } catch (Exception e) {
            Global.errorLog(e);
            return "";
        }
    }

    @Override
    public void saveData(TopicData data) {
        modifyData = data;
    }

    @Override
    public TopicData loadData() {
        return modifyData;
    }

    @Override
    public void exit() {
        Intent intent = new Intent();
        intent.putExtra("data", modifyData.content);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public String getProjectPath() {
        return projectPath;
    }

    // 有任务的项目必定是私有项目
    @Override
    public boolean isProjectPublic() {
        return false;
    }
}
