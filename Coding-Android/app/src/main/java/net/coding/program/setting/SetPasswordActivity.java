package net.coding.program.setting;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;

import com.loopj.android.http.RequestParams;

import net.coding.program.R;
import net.coding.program.common.Global;
import net.coding.program.common.SimpleSHA1;
import net.coding.program.common.umeng.UmengEvent;
import net.coding.program.common.util.ViewStyleUtil;
import net.coding.program.common.widget.LoginEditText;
import net.coding.program.compatible.CodingCompat;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

@EActivity(R.layout.activity_set_password)
public class SetPasswordActivity extends MenuButtonActivity {

    final String Url = Global.HOST_API + "/user/updatePassword";

    @ViewById
    LoginEditText oldPassword, newPassword, confirmPassword;

    @AfterViews
    void initSetPasswordActivity() {
    }

    boolean passwordFormatError(String oldPwd, String newPwd, String confirmPwd) {
        if (TextUtils.isEmpty(oldPwd)) {
            showMiddleToast("当前密码不能为空");
            return true;

        } else if (TextUtils.isEmpty(newPwd)) {
            showMiddleToast("新密码不能为空");
            return true;

        } else if (TextUtils.isEmpty(confirmPwd)) {
            showMiddleToast("确认密码不能为空");
            return true;

        } else if (!newPwd.equals(confirmPwd)) {
            showMiddleToast("两次密码输入不一致");
            return true;

        } else if (newPwd.length() < 6) {
            showMiddleToast("密码不能少于6位");
            return true;

        } else if (newPwd.length() > 64) {
            showMiddleToast("密码不能大于64位");
            return true;
        }

        return false;
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (code == 0) {
            umengEvent(UmengEvent.USER, "修改密码");
            umengEvent(UmengEvent.E_USER_CENTER, "修改密码_修改成功");

            showButtomToast("密码修改成功");
            popDialog();
        } else {
            showErrorMsg(code, respanse);
        }
    }

    private void popDialog() {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setMessage("修改密码后需要重新登录")
                .setNegativeButton("确定", (dialog1, which) -> {
                    finish();
                    startActivity(new Intent(SetPasswordActivity.this, CodingCompat.instance().getLoginActivity()));
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void afterMenuInit(MenuItem actionSend) {
        ViewStyleUtil.editTextBindButton(actionSend, oldPassword, newPassword, confirmPassword);
    }

    @Override
    protected void actionSend() {
        RequestParams params = new RequestParams();
        try {
            String oldPwd = oldPassword.getText().toString();
            String newPwd = newPassword.getText().toString();
            String confirmPwd = confirmPassword.getText().toString();
            if (passwordFormatError(oldPwd, newPwd, confirmPwd)) {
                return;
            }
            params.put("current_password", SimpleSHA1.sha1(oldPwd));
            params.put("password", SimpleSHA1.sha1(newPwd));
            params.put("confirm_password", SimpleSHA1.sha1(confirmPwd));
            postNetwork(Url, params, "");
            Global.hideSoftKeyboard(this);
        } catch (Exception e) {
            showMiddleToast(e.toString());
        }
    }
}
