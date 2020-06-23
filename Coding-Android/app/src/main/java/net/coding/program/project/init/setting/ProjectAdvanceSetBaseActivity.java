package net.coding.program.project.init.setting;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.coding.program.R;
import net.coding.program.common.Global;
import net.coding.program.common.GlobalData;
import net.coding.program.common.SimpleSHA1;
import net.coding.program.common.WeakRefHander;
import net.coding.program.common.model.AccountInfo;
import net.coding.program.common.model.ProjectObject;
import net.coding.program.common.ui.BackActivity;
import net.coding.program.common.umeng.UmengEvent;
import net.coding.program.login.auth.AuthInfo;
import net.coding.program.login.auth.TotpClock;
import net.coding.program.project.EventProjectModify;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jack wang on 2015/3/31.
 * 删除项目
 */
@EActivity(R.layout.init_activity_project_advance_set)
public abstract class ProjectAdvanceSetBaseActivity extends BackActivity implements Handler.Callback {

    protected final String TAG_DELETE_PROJECT_2FA = "TAG_DELETE_PROJECT_2FA";
    protected final String TAG_TRANSFER_PROJECT = "TAG_TRANSFER_PROJECT";
    protected final String TAG_ARCHIVE_PROJECT = "TAG_ARCHIVE_PROJECT";
    private final String HOST_NEED_2FA = Global.HOST_API + "/user/2fa/method";
    @Extra
    protected ProjectObject mProjectObject;
    @ViewById
    TextView deleteBut;

    Handler hander2fa;
    private EditText edit2fa;

    @AfterViews
    protected final void initProjectAdvanceSetActivity() {
        hander2fa = new WeakRefHander(this, 100);
    }

    protected boolean checkInput() {
        return true;
    }

    @Click
    void deleteBut() {
        if (!checkInput()) {
            return;
        }

        showProgressBar(true);
        getNetwork(HOST_NEED_2FA, HOST_NEED_2FA);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (edit2fa != null) {
            String secret = AccountInfo.loadAuth(this, GlobalData.sUserObject.global_key);
            if (secret.isEmpty()) {
                return true;
            }

            String code2FA = new AuthInfo(secret, new TotpClock(this)).getCode();
            edit2fa.setText(code2FA);
        }
        return true;
    }

    private void showDeleteDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_delete_project, null);
        final EditText edit1 = textEntryView.findViewById(R.id.edit1);
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle("需要验证密码")
                .setView(textEntryView)
                .setPositiveButton("确定", (dialog1, whichButton) -> {
                    String editStr1 = edit1.getText().toString().trim();
                    if (TextUtils.isEmpty(editStr1)) {
                        Toast.makeText(ProjectAdvanceSetBaseActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                        return;
                    }

                    actionDelete2FA(SimpleSHA1.sha1(editStr1));
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showDeleteDialog2fa() {
        hander2fa.sendEmptyMessage(0);
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_delete_project_2fa, null);
        edit2fa = textEntryView.findViewById(R.id.edit1);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        AlertDialog dialog = builder
                .setView(textEntryView)
                .setPositiveButton("确定", (dialog1, whichButton) -> {
                    String editStr1 = edit2fa.getText().toString().trim();
                    if (TextUtils.isEmpty(editStr1)) {
                        Toast.makeText(ProjectAdvanceSetBaseActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                        return;
                    }
                    actionDelete2FA(editStr1);
                })
                .setNegativeButton("取消", null)
                .show();

        dialog.setOnDismissListener(dialog1 -> {
            hander2fa.removeMessages(0);
            edit2fa = null;
        });
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(HOST_NEED_2FA)) {
            showProgressBar(false);
            if (code == 0) {
                String passwordType = respanse.optString("data", "");
                if (passwordType.equals("totp")) {

                    showDeleteDialog2fa();
                } else { //  password
                    showDeleteDialog();
                }
            } else {
                showErrorMsg(code, respanse);
            }
        } else if (tag.equals(TAG_ARCHIVE_PROJECT)) {
             showProgressBar(false);
            if (code == 0) {
                umengEvent(UmengEvent.PROJECT, "项目归档");
                showButtomToast("归档成功");

                EventBus.getDefault().post(new EventProjectModify().setExit());
                setResult(RESULT_OK);
                finish();
            } else {
                showErrorMsg(code, respanse);
            }
        } else if (tag.equals(TAG_DELETE_PROJECT_2FA)) {
            showProgressBar(false);
            if (code == 0) {
                umengEvent(UmengEvent.PROJECT, "删除项目");
                showButtomToast("删除成功");

                EventBus.getDefault().post(new EventProjectModify().setExit());
                setResult(RESULT_OK);
                finish();
            } else {
                showErrorMsg(code, respanse);
            }
        } else if (tag.equals(TAG_TRANSFER_PROJECT)) {
            showProgressBar(false);
            if (code == 0) {
                umengEvent(UmengEvent.PROJECT, "转让项目");
                showButtomToast("转让成功");

                EventBus.getDefault().post(new EventProjectModify().setExit());
                setResult(RESULT_OK);
                finish();
            } else {
                showErrorMsg(code, respanse);
            }
        }
    }

    abstract void actionDelete2FA(String code);

}
