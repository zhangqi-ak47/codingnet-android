package net.coding.program.task.add;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.coding.program.R;
import net.coding.program.common.CommentBackup;
import net.coding.program.common.DatePickerFragment;
import net.coding.program.common.Global;
import net.coding.program.common.GlobalCommon;
import net.coding.program.common.GlobalData;
import net.coding.program.common.HtmlContent;
import net.coding.program.common.ImageInfo;
import net.coding.program.common.MyImageGetter;
import net.coding.program.common.PhotoOperate;
import net.coding.program.common.StartActivity;
import net.coding.program.common.base.MyJsonResponse;
import net.coding.program.common.enter.EnterLayout;
import net.coding.program.common.enter.ImageCommentLayout;
import net.coding.program.common.event.EventBoardRefreshRequest;
import net.coding.program.common.event.EventRefreshTask;
import net.coding.program.common.event.EventRequestTaskCount;
import net.coding.program.common.model.AccountInfo;
import net.coding.program.common.model.AttachmentFileObject;
import net.coding.program.common.model.DynamicObject;
import net.coding.program.common.model.ProjectObject;
import net.coding.program.common.model.RefResourceObject;
import net.coding.program.common.model.SingleTask;
import net.coding.program.common.model.TopicLabelObject;
import net.coding.program.common.model.UserObject;
import net.coding.program.common.network.MyAsyncHttpClient;
import net.coding.program.common.param.MessageParse;
import net.coding.program.common.ui.CodingToolbarBackActivity;
import net.coding.program.common.umeng.UmengEvent;
import net.coding.program.common.widget.input.SimpleTextWatcher;
import net.coding.program.network.BaseHttpObserver;
import net.coding.program.network.HttpObserver;
import net.coding.program.network.Network;
import net.coding.program.network.model.task.BoardList;
import net.coding.program.network.model.task.TaskCreating;
import net.coding.program.network.model.user.Member;
import net.coding.program.pickphoto.ClickSmallImage;
import net.coding.program.project.detail.MembersActivity_;
import net.coding.program.project.detail.TaskListFragment;
import net.coding.program.project.detail.TopicAddActivity;
import net.coding.program.project.detail.TopicLabelActivity;
import net.coding.program.project.detail.TopicLabelActivity_;
import net.coding.program.project.detail.TopicLabelBar;
import net.coding.program.route.BlankViewDisplay;
import net.coding.program.task.TaskDescriptionActivity_;
import net.coding.program.task.board.PickTaskBoardActivity_;
import net.coding.program.third.EmojiFilter;
import net.coding.program.util.TextWatcherAt;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static net.coding.program.common.util.LogUtils.makeLogTag;

@EActivity(R.layout.activity_task_add)
public class TaskAddActivity extends CodingToolbarBackActivity implements StartActivity, DatePickerFragment.DateSet, NewTaskParam {

    public static final String RESULT_GLOBARKEY = "RESULT_GLOBARKEY";
    public static final int RESULT_REQUEST_FOLLOW = 1002;
    public static final int RESULT_REQUEST_SELECT_USER = 3;
    public static final int RESULT_REQUEST_DESCRIPTION = 4;
    public static final int RESULT_REQUEST_DESCRIPTION_CREATE = 5;
    public static final int RESULT_REQUEST_PICK_PROJECT = 6;
    public static final int RESULT_LABEL = 7;
    public static final int RESULT_REQUEST_PICK_WATCH_USER = 8;
    public static final int RESULT_RESUSE_REFRESOURCE = 9;
    public static final int RESULT_PICK_BOARD_LIST = 10;

    private static final String TAG = makeLogTag(TaskAddActivity.class);
    private static final String TAG_HTTP_REMOVE_LABEL = "TAG_HTTP_REMOVE_LABEL";
    final String HOST_COMMENT_ADD = Global.HOST_API + "/task/%s/comment";
    final String HOST_TASK_ADD = Global.HOST_API + "%s/task";
    final String HOST_FORMAT_TASK_COMMENT = Global.HOST_API + "/activity/task/%s?last_id=999999999";
    final String HOST_TASK_UPDATE = Global.HOST_API + "/task/%s/update";
    final String TAG_TASK_UPDATE = "TAG_TASK_UPDATE";
    final String hostDeleteComment = Global.HOST_API + "/task/%s/comment/%s";
    final String tagTaskDetail = "tagTaskDetail";
    final String HOST_PREVIEW = Global.HOST_API + "/markdown/preview";
    private final MyImageGetter myImageGetter = new MyImageGetter(this);
    private final ClickSmallImage onClickImage = new ClickSmallImage(this);
    private final String MSG_ADD_TASK_SUCCESS = "新建任务成功";
    @ViewById
    protected View blankLayout;
    View listFooter;
    @Extra
    SingleTask mSingleTask;
    @Extra
    ProjectObject mProjectObject = new ProjectObject();
    @Extra
    UserObject mUserOwner;
    @Extra
    TaskJumpParams mJumpParams;
    @Extra
    BoardList boardParam;

    @Extra
    boolean canPickProject = true;

    @ViewById
    ListView listView;

    View mHeadView;
    EditText title;
    TopicLabelBar labelBar;

    TaskAttrItem layoutProjectName;
    TaskAttrItem layoutName;
    TaskAttrItem layoutBoard;
    TaskAttrItem layoutPriovity;
    TaskAttrItem layoutDeadline;
    TaskAttrItem layoutPhase;
    TaskAttrItem layoutWatch;
    ViewGroup layoutRefResourceParent;

    TextView description;
    ViewGroup descriptionLayout;
    TextView descriptionButton;
    SingleTask.TaskDescription descriptionData = new SingleTask.TaskDescription();
    SingleTask.TaskDescription descriptionDataNew = new SingleTask.TaskDescription();

    TaskParams mNewParam;
    TaskParams mOldParam;
    ImageCommentLayout mEnterComment;
    String HOST_DESCRIPTER = Global.HOST_API + "/task/%s/description";
    MenuItem mMenuSave;
    String urlComments = "";
    @Bean
    PriorityAdapter mPriorityAdapter;
    @Bean
    StatusAdapter mStatusAdapter;
    ArrayList<DynamicObject.DynamicTask> mData = new ArrayList<>();
    HashMap<String, String> mSendedImages = new HashMap<>();
    String tagUrlCommentPhoto = "";
    boolean mTaskNoFound = false;
    private ArrayList<UserObject> watchUsers = new ArrayList<>();
    private View.OnClickListener mOnClickComment = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final SingleTask.TaskComment comment = (SingleTask.TaskComment) v.getTag();
            if (comment.isMy()) {
                showDialog("删除评论？", (dialog, which) -> {
                    String url = String.format(hostDeleteComment, comment.taskId, comment.id);
                    deleteNetwork(url, hostDeleteComment, comment.id);
                });
            } else {
                EnterLayout mEnterLayout = mEnterComment.getEnterLayout();
                mEnterLayout.content.setTag(comment);
                String format = "回复 %s";
                mEnterLayout.content.setHint(String.format(format, comment.owner.name));

                mEnterLayout.restoreLoad(comment);

                mEnterLayout.popKeyboard();
            }
        }
    };

    BaseAdapter commentAdpter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            DynamicObject.DynamicTask task = mData.get(position);
            if (task.target_type.equals("TaskComment")) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

            if (getCount() == 0) {
                listFooter.setVisibility(View.INVISIBLE);
            } else {
                listFooter.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int count = getCount();
            if (getItemViewType(position) == 0) {
                CommentHolder holder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.activity_task_comment_much_image_task, parent, false);
                    holder = new CommentHolder(convertView, mOnClickComment, myImageGetter, getImageLoad(), GlobalCommon.mOnClickUser, onClickImage);
                    convertView.setTag(R.id.layout, holder);

                } else {
                    holder = (CommentHolder) convertView.getTag(R.id.layout);
                }

                SingleTask.TaskComment data = mData.get(position).getTaskComment();
                holder.setContent(data);

                holder.updateLine(position, count);

                return convertView;
            } else {
                TaskListHolder holder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.task_list_item_dynamic, parent, false);
                    holder = new TaskListHolder(convertView);
                } else {
                    holder = (TaskListHolder) convertView.getTag(TaskListHolder.getTagId());
                }

                DynamicObject.DynamicTask data = mData.get(position);

                holder.mContent.setText(data.dynamicTitle());

                int iconResId = R.drawable.ic_task_dynamic_update;
                try {
                    String resName = "ic_task_dynamic_" + data.action;
                    Field field = R.drawable.class.getField(resName);
                    iconResId = Integer.parseInt(field.get(null).toString());
                } catch (Exception e) {
                    Global.errorLog(e);
                }
                holder.mIcon.setImageResource(iconResId);

                holder.updateLine(position, count);

                return convertView;
            }
        }
    };

    private View.OnClickListener onClickCreateDescription = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (noSelectProject()) {
                showMiddleToast(R.string.pick_project_first);
                return;
            }

            TaskDescriptionActivity_
                    .intent(TaskAddActivity.this)
                    .taskId(0)
                    .projectPath(mSingleTask.project.getProjectPath())
                    .descriptionData(descriptionDataNew)
                    .startForResult(RESULT_REQUEST_DESCRIPTION_CREATE);
        }
    };
    private ArrayList<RefResourceObject> refResourceList = new ArrayList<>();
    View.OnClickListener clickRefResource = v -> {
        TaskParam param = new TaskParam(mSingleTask.project.getProjectPath(),
                mSingleTask.getNumberValue());

        RefResourceActivity_.intent(TaskAddActivity.this)
                .mData(refResourceList)
                .mParam(param)
                .startForResult(RESULT_RESUSE_REFRESOURCE);
    };

    @Override
    public TaskParams getNewParam() {
        return mNewParam;
    }

    @Override
    protected ProjectObject getProject() {
        if (mSingleTask == null || mSingleTask.isEmpty()) {
            return null;
        }

        return mSingleTask.project;
    }

    @AfterViews
    protected final void initTaskAddActivity() {
        initControl();

        if (mSingleTask != null) {
            initData();
        } else if (mJumpParams != null) { // 跳转过来的，要先取得任务数据
            requestTaskFromNetwork();
        } else {
            mSingleTask = new SingleTask();

            if (!mProjectObject.isEmpty()) {
                mSingleTask.project = mProjectObject;
                mSingleTask.project_id = mProjectObject.getId();
                mSingleTask.priority = mProjectObject.getId();
                mSingleTask.taskBoardList = boardParam;
            } else {
                if (canPickProject) {
                    PickProjectActivity_.intent(TaskAddActivity.this)
                            .startForResult(RESULT_REQUEST_PICK_PROJECT);
                }
            }

            initData();
        }

        requestTaskCreating();
    }

    private void requestTaskFromNetwork() {
        final String hostTaskDetail = Global.HOST_API + "/user/%s/project/%s/task/%s";
        String url = String.format(hostTaskDetail, mJumpParams.userKey, mJumpParams.projectName, mJumpParams.taskId);
        getNetwork(url, tagTaskDetail);
        showDialogLoading();
    }

    private void initControl() {
        mEnterComment = new ImageCommentLayout(this, v -> sendCommentAll(), getImageLoad());

        // 单独提出来是因为弹出软键盘时，由于head太长，导致 title 会被顶到消失，现在的解决方法是 edit作为一个单独的head加载
        View headEdit = mInflater.inflate(R.layout.activity_task_add_head_edit, listView, false);
        title = (EditText) headEdit.findViewById(R.id.title);
        labelBar = (TopicLabelBar) headEdit.findViewById(R.id.labelBar);

        listView.addHeaderView(headEdit, null, false);

        mHeadView = mInflater.inflate(R.layout.activity_task_add_head, listView, false);

        layoutProjectName = mHeadView.findViewById(R.id.layoutProjectName);
        layoutName = mHeadView.findViewById(R.id.layoutName);
        layoutBoard = mHeadView.findViewById(R.id.layoutBoard);
        layoutPriovity = mHeadView.findViewById(R.id.layoutPriovity);
        layoutDeadline = mHeadView.findViewById(R.id.layoutDeadline);
        layoutPhase = mHeadView.findViewById(R.id.layoutPhase);
        layoutWatch = mHeadView.findViewById(R.id.layoutWatch);
        layoutRefResourceParent = mHeadView.findViewById(R.id.layoutRefResourceParent);
        layoutRefResourceParent.setOnClickListener(clickRefResource);

        descriptionLayout = mHeadView.findViewById(R.id.descriptionLayout);
        description = mHeadView.findViewById(R.id.description);
        descriptionButton = mHeadView.findViewById(R.id.descriptionButton);
        listView.addHeaderView(mHeadView, null, false);
        listFooter = mInflater.inflate(R.layout.task_add_footer, listView, false);
        listView.addFooterView(listFooter);

        listFooter.setVisibility(View.INVISIBLE);
    }

    private void updateLabels(List<TopicLabelObject> labels) {
        labelBar.bind(labels, new TopicLabelBar.Controller() {
            @Override
            public boolean canShowLabels() {
                return true;
            }

            @Override
            public boolean canEditLabels() {
                return true;
            }

            @Override
            public void onEditLabels(TopicLabelBar view) {
                if (!mSingleTask.project.isEmpty()) {
                    TopicLabelActivity_.intent(TaskAddActivity.this)
                            .labelType(TopicLabelActivity.LabelType.Task)
                            .projectPath(mSingleTask.project.getProjectPath())
                            .id(mSingleTask.getId())
                            .checkedLabels(mSingleTask.labels)
                            .startForResult(RESULT_LABEL);
                } else {
                    showMiddleToast("请先选择项目");
                }
            }

            @Override
            public void onRemoveLabel(TopicLabelBar view, int labelId) {
                String url = mSingleTask.getHttpRemoveLabal(labelId);
                deleteNetwork(url, TAG_HTTP_REMOVE_LABEL, labelId);
            }
        });
    }

    private void updateSendButton() {
        if (title.getText().toString().isEmpty()
                || isContentUnmodify() || mSingleTask.project.isEmpty()) {
            enableSendButton(false);
        } else {
            enableSendButton(true);
        }
    }

    private boolean isContentUnmodify() {
        return ((mNewParam != null && mNewParam.equals(mOldParam)) && !descripChange()) ||
                mTaskNoFound;
    }

    private boolean descripChange() {
        return !mSingleTask.isEmpty() &&
                !descriptionData.markdown.equals(descriptionDataNew.markdown);
    }

    private void enableSendButton(boolean enable) {
        if (mMenuSave == null) {
            return;
        }

        if (enable) {
            mMenuSave.setIcon(R.drawable.ic_menu_ok);
            mMenuSave.setEnabled(true);
        } else {
            mMenuSave.setIcon(R.drawable.ic_menu_ok_unable);
            mMenuSave.setEnabled(false);
        }
    }

    void initData() {
        if (mSingleTask.isEmpty()) {
            if (mUserOwner.id == 0) {
                mSingleTask.owner = AccountInfo.loadAccount(this);
            } else {
                mSingleTask.owner = mUserOwner;
            }
            mSingleTask.owner_id = mSingleTask.owner.id;
            mSingleTask.priority = 1; // 默认优先级是 1：正常处理
        }

        updateLabels(mSingleTask.labels);

        invalidateOptionsMenu();

        mNewParam = new TaskParams(mSingleTask);
        mOldParam = new TaskParams(mSingleTask);

        mEnterComment.getEnterLayout().content.addTextChangedListener(new TextWatcherAt(this, this, RESULT_REQUEST_FOLLOW, mSingleTask.project));

        setHeadData();
        listView.setAdapter(commentAdpter);

        selectMember();

        if (mSingleTask.isEmpty()) {
            setActionBarTitle("新建任务");
            layoutPhase.setText2("未完成");
            layoutPhase.setVisibility(View.GONE);
            mEnterComment.hide();

            findViewById(R.id.line2_comment_off).setVisibility(View.VISIBLE);
            findViewById(R.id.line2_comment_on).setVisibility(View.GONE);

            findViewById(R.id.descriptionLayout).setVisibility(View.GONE);

        } else {
            setActionBarTitle(mSingleTask.project.name);
            title.setText(mSingleTask.content);

            setStatus();
            setPriority();
            layoutPhase.setVisibility(View.VISIBLE);

            findViewById(R.id.line2_comment_off).setVisibility(View.GONE);
            findViewById(R.id.line2_comment_on).setVisibility(View.VISIBLE);
        }

        setDeadline();

        initDescription();

        // 获取任务的评论
        if (!mSingleTask.isEmpty()) {
            urlComments = String.format(HOST_FORMAT_TASK_COMMENT, mSingleTask.getId());
            updateDynamicFromNetwork();
        }

        if (!mSingleTask.isEmpty()) {
            CommentBackup.BackupParam param = new CommentBackup.BackupParam(CommentBackup.Type.Task, mSingleTask.getId(), 0);
            EnterLayout mEnterLayout = mEnterComment.getEnterLayout();
            mEnterLayout.content.setTag(param);
            mEnterLayout.restoreLoad(param);
        }

        TextView time = (TextView) mHeadView.findViewById(R.id.time);
        TextView createName = (TextView) mHeadView.findViewById(R.id.createrName);
        if (mSingleTask.isEmpty()) {
            createName.setText(mNewParam.owner.name);
            time.setText("现在");
        } else {
            createName.setText(mSingleTask.creator.name);
            time.setText(Global.dayToNow(mSingleTask.created_at));
        }

        TextView refrenceId = (TextView) mHeadView.findViewById(R.id.referenceId);
        if (mSingleTask.isEmpty()) {
            refrenceId.setVisibility(View.GONE);
        } else {
            refrenceId.setVisibility(View.VISIBLE);
            refrenceId.setText(mSingleTask.getNumber());
        }

        // 设置任务的关注者
        watchUserUpdateFromNetwork();
        uiBindDataWatch();

        uiBindDataProject();

        if (!canPickProject) {
            layoutProjectName.setVisibility(View.GONE);
        }

        updateRefResourceNetwork();
    }

    private void watchUserUpdateFromNetwork() {
        if (mSingleTask.isEmpty()) {
            return;
        }

        ProjectObject project = mSingleTask.project;
        String url = String.format(project.getHttpProjectApi() + "/task/%d/watchers?pageSize=1000", mSingleTask.getId());
        MyAsyncHttpClient.get(this, url, new MyJsonResponse(TaskAddActivity.this) {
            @Override
            public void onMySuccess(JSONObject response) {
                super.onMySuccess(response);
                watchUsers.clear();
                JSONArray jsonList = response.optJSONObject("data").optJSONArray("list");
                if (jsonList != null && jsonList.length() > 0) {
                    for (int i = 0; i < jsonList.length(); ++i) {
                        watchUsers.add(new UserObject(jsonList.optJSONObject(i)));
                    }
                }

                uiBindDataWatch();
            }
        });
    }

    private void uiBindDataWatch() {
        int watchUserCount = watchUsers.size();
        String countString;
        if (watchUserCount == 0) {
            countString = "添加";
        } else {
            countString = String.format("%s人关注", watchUserCount);
        }
        layoutWatch.setText2(countString);
    }

    private void updateDynamicFromNetwork() {
        getNetwork(urlComments, HOST_FORMAT_TASK_COMMENT);
    }

    private void initDescription() {
//        if (mSingleTask.isEmpty()) {
//            descriptionLayout.setOnClickListener(onClickCreateDescription);
//        } else {
//            if (mSingleTask.has_description) {
//                description.setText("载入备注中...");
//                HOST_DESCRIPTER = String.format(HOST_DESCRIPTER, mSingleTask.getCreateTime());
//                getNetwork(HOST_DESCRIPTER, HOST_DESCRIPTER);
//            } else {
//                descriptionLayout.setOnClickListener(onClickCreateDescription);
//            }
//        }
        if (mSingleTask.isEmpty()) {
            descriptionButtonUpdate(false);
            descriptionButton.setOnClickListener(onClickCreateDescription);
        } else {
            if (mSingleTask.has_description) {
                descriptionButtonUpdate(true);
                HOST_DESCRIPTER = String.format(HOST_DESCRIPTER, mSingleTask.getId());
                getNetwork(HOST_DESCRIPTER, HOST_DESCRIPTER);
            } else {
                descriptionButtonUpdate(false);
                descriptionButton.setOnClickListener(onClickCreateDescription);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mTaskNoFound) {
            MenuInflater menuInflater = getMenuInflater();
            if (mJumpParams == null && mSingleTask.isEmpty()) {
                menuInflater.inflate(R.menu.task_add, menu);
            } else {
                menuInflater.inflate(R.menu.task_add_edit, menu);
                if (mSingleTask != null && !mSingleTask.isEmpty() && !mSingleTask.creator.isMe()) {
                    menu.findItem(R.id.action_delete).setVisible(false);
                }
            }
            mMenuSave = menu.findItem(R.id.action_save);
            updateSendButton();
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void deleteTask() {
        showDialog("删除任务？", (dialog, which) -> {
            SingleTask task = mSingleTask;
            String url = String.format(TaskListFragment.getHostTaskDelete(), task.project.owner_user_name, task.project.name, task.getId());
            deleteNetwork(url, TaskListFragment.getHostTaskDelete());
            showProgressBar(true, "删除任务中...");
        });
    }

    private TaskCreating taskCreating;

    private void requestTaskCreating() {
        Network.getRetrofit(this)
                .taskCreateParam()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpObserver<TaskCreating>(this) {
                    @Override
                    public void onSuccess(TaskCreating data) {
                        super.onSuccess(data);
                        taskCreating = data;
                    }

                    @Override
                    public void onFail(int errorCode, @NonNull String error) {
                        super.onFail(errorCode, error);
                    }
                });
    }

    private void jumpPickBoard() {
        int projectId = mSingleTask.project.id;
        ArrayList<BoardList> list = new ArrayList<>();
        for (BoardList item : taskCreating.taskBoardList) {
            if (item.projectId == projectId) {
                list.add(item);
            }
        }

        if (list.isEmpty()) {
            showButtomToast(R.string.project_no_board);
            return;
        }

        PickTaskBoardActivity_.IntentBuilder_ builder = PickTaskBoardActivity_.intent(this).boardLists(list);
        if (mNewParam.taskBoard != null) {
            builder.pickBoardId(mNewParam.taskBoard.id);
        }
        builder.startForResult(RESULT_PICK_BOARD_LIST);
    }

    private void setHeadData() {
        title.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mNewParam.content = s.toString();
                updateSendButton();
            }
        });
        title.setText("");

//        if (mSingleTask.isEmpty() && mSingleTask.project.isEmpty() && mProjectObject.isEmpty()) {
//            layoutProjectName.setVisibility(View.VISIBLE);
        layoutProjectName.setOnClickListener(v -> PickProjectActivity_.intent(TaskAddActivity.this)
                .startForResult(RESULT_REQUEST_PICK_PROJECT));
//        } else {
//            layoutProjectName.setVisibility(View.GONE);
//        }

        layoutName.setOnClickListener(v -> {
            if (noSelectProject()) {
                showMiddleToast(R.string.pick_project_first);
                return;
            }
            MembersActivity_
                    .intent(TaskAddActivity.this)
                    .mProjectObjectId(mSingleTask.project_id)
                    .startForResult(RESULT_REQUEST_SELECT_USER);
        });

        layoutBoard.setOnClickListener(v -> {
            if (noSelectProject()) {
                showMiddleToast(R.string.pick_project_first);
                return;
            }

            if (taskCreating == null) {
                Network.getRetrofit(this)
                        .taskCreateParam()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new HttpObserver<TaskCreating>(this) {
                            @Override
                            public void onSuccess(TaskCreating data) {
                                super.onSuccess(data);
                                taskCreating = data;

                                jumpPickBoard();
                            }

                            @Override
                            public void onFail(int errorCode, @NonNull String error) {
                                super.onFail(errorCode, error);
                            }
                        });
            } else {
                jumpPickBoard();
            }
        });

        layoutPhase.setOnClickListener(v -> popListSelectDialog(mStatusAdapter,
                (dialog, which) -> {
                    if (which == 0) {
                        mNewParam.status = SingleTask.STATUS_PROGRESS; // "未完成"
                    } else {
                        mNewParam.status = SingleTask.STATUS_FINISH;
                    }

                    setStatus();
                    updateSendButton();
                }));

        layoutDeadline.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment();

            Bundle bundle = new Bundle();
            bundle.putString("date", mNewParam.deadline);
            bundle.putBoolean("clear", true);
            newFragment.setArguments(bundle);

            newFragment.setCancelable(true);
            newFragment.show(getSupportFragmentManager(), "datePicker");
            getSupportFragmentManager().executePendingTransactions();
        });

        layoutPriovity.setOnClickListener(v -> popListSelectDialog(
                mPriorityAdapter,
                (dialog, which) -> {
                    mNewParam.priority = SingleTask.priorityDrawable.length - 1 - which;
                    setPriority();
                    updateSendButton();
                }));

        layoutWatch.setOnClickListener(v -> {
            if (noSelectProject()) {
                showMiddleToast(R.string.pick_project_first);
                return;
            }

            WatcherListActivity_.intent(TaskAddActivity.this)
                    .mProjectObjectId(mSingleTask.project_id)
                    .mTaskId(mSingleTask.getId())
                    .mWatchUsers(watchUsers)
                    .startForResult(RESULT_REQUEST_PICK_WATCH_USER);
        });
    }

    private boolean noSelectProject() {
        return mSingleTask.project.isEmpty();
    }

    private void setStatus() {
        layoutPhase.setText2(mNewParam.status == 1 ? "未完成" : "已完成");
    }

    public void dateSetResult(String date, boolean clear) {
        if (clear) {
            mNewParam.deadline = "";
        } else {
            mNewParam.deadline = date;
        }

        setDeadline();
        updateSendButton();
    }

    private void setPriority() {
        String[] strings_priority = getResources().getStringArray(R.array.strings_priority);

        String priority = strings_priority[strings_priority.length - 1];
        if (mNewParam.priority < strings_priority.length) {
            priority = strings_priority[mNewParam.priority];
        }
        layoutPriovity.setText2(priority);
    }

    private void setDeadline() {
        String s = mNewParam.deadline;
        if (s.isEmpty()) {
            s = "未指定";
        }

        layoutDeadline.setText2(s);
    }

    private void setDescription() {
        MessageParse parseData = HtmlContent.parseReplacePhoto(descriptionDataNew.description);
        description.setText(Html.fromHtml(parseData.text, myImageGetter, Global.tagHandler));
    }

    @OptionsItem
    void action_save() {
        String content = title.getText().toString();
        if (EmojiFilter.containsEmoji(this, content)) {
            return;
        }

        Global.popSoftkeyboard(this, title, false);

        if (mSingleTask.isEmpty()) {
            String url = String.format(HOST_TASK_ADD, mSingleTask.project.getBackendProjectPath());
            RequestParams params = new RequestParams();
            params.put("content", content);
            params.put("status", mNewParam.status);
            params.put("priority", mNewParam.priority);
            params.put("owner_id", mNewParam.ownerId);
            params.put("deadline", mNewParam.deadline);
            if (mNewParam.taskBoard != null) {
                params.put("task_board_list", mNewParam.taskBoard.id);
            }

            if (!descriptionDataNew.markdown.isEmpty()) {
                params.put("description", descriptionDataNew.markdown);
            }

            StringBuilder labels = TopicAddActivity.getLabelsParam(mSingleTask.labels);
            if (labels.length() > 0) {
                params.put("labels", labels);
            }

            postNetwork(url, params, HOST_TASK_ADD);
            showProgressBar(R.string.create_task_ing);

        } else {
            String url = String.format(HOST_TASK_UPDATE, mSingleTask.getId());
            RequestParams params = new RequestParams();
            if (!content.equals(mSingleTask.content)) {
                params.put("content", content);
            }
            if (mNewParam.status != mSingleTask.status) {
                params.put("status", mNewParam.status);
            }
            if (mNewParam.priority != mSingleTask.priority) {
                params.put("priority", mNewParam.priority);
            }
            if (mNewParam.ownerId != (mSingleTask.owner_id)) {
                params.put("owner_id", mNewParam.ownerId);
            }
            if (!mNewParam.deadline.equals(mSingleTask.deadline)) {
                params.put("deadline", mNewParam.deadline);
            }

//            if (mNewParam.taskBoard != null) {
//                params.put("task_board_list", mNewParam.taskBoard.id);
//            } else {
//                params.put("task_board_list", 0);
//            }

            String oldData = descriptionData.markdown;
            if (oldData != null && !oldData.equals(descriptionDataNew.markdown)) {
                params.put("description", descriptionDataNew.markdown);
            }

            putNetwork(url, params, TAG_TASK_UPDATE);
            showProgressBar(R.string.modify_task_ing);
        }
    }

    @OptionsItem
    void action_copy() {
        final String urlTemplate = Global.HOST + "/u/%s/p/%s/task/%d";
        String link = String.format(urlTemplate, mSingleTask.project.owner_user_name, mSingleTask.project.name, mSingleTask.getId());
        Global.tipCopyLink(this, link);
    }

    @OptionsItem
    void action_delete() {
        deleteTask();
    }

    void selectMember() {
        if (noSelectProject()) {
            layoutName.setText2(R.string.no_pick);
            layoutName.getImage().setImageResource(R.drawable.ic_task_user);

            layoutBoard.setText2("无");

        } else {
            layoutName.setText2(mNewParam.owner.name);
            ImageLoader.getInstance().displayImage(Global.makeSmallUrlSquare(mNewParam.owner.avatar, getResources().getDimensionPixelSize(R.dimen.task_add_user_icon_width)), layoutName.getImage());

            if (mNewParam.taskBoard != null) {
                layoutBoard.setText2(mNewParam.taskBoard.title);
            }
        }
    }

    private void closeActivity(String msg) {
        Intent intent = new Intent();
        if (mNewParam != null && mNewParam.owner != null) { // 友盟显示有可能为空
            intent.putExtra(RESULT_GLOBARKEY, mNewParam.owner.global_key);
            setResult(Activity.RESULT_OK, intent);
        }

        if (!msg.isEmpty()) {
            showButtomToast(msg);
            if (msg.equals(MSG_ADD_TASK_SUCCESS)) {
                EventBus.getDefault().post(new EventRefreshTask());
                EventBus.getDefault().post(new EventRequestTaskCount());
            }
        }

        finish();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(HOST_TASK_ADD)) {
            showProgressBar(false);
            if (code == 0) {
                umengEvent(UmengEvent.TASK, "新建任务");
                umengEvent(UmengEvent.E_TASK, "创建");
                closeActivity(MSG_ADD_TASK_SUCCESS);

                if (mSingleTask.taskBoardList != null) {
                    EventBus.getDefault().post(new EventBoardRefreshRequest(mSingleTask.taskBoardList.id, 1));
                }

            } else {
                showErrorMsg(code, respanse);
            }

        } else if (tag.equals(HOST_FORMAT_TASK_COMMENT)) {
            if (code == 0) {
                mData.clear();
                JSONArray jsonArray = respanse.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    mData.add(new DynamicObject.DynamicTask(jsonArray.getJSONObject(i)));
                }
                commentAdpter.notifyDataSetChanged();

                updateDynamicFromNetwork();

            } else {
                showErrorMsg(code, respanse);
            }

        } else if (tag.equals(HOST_COMMENT_ADD)) {
            showProgressBar(false);
            if (code == 0) {
                umengEvent(UmengEvent.TASK, "新建任务评论");
                umengEvent(UmengEvent.E_TASK, "评论");

                EnterLayout mEnterLayout = mEnterComment.getEnterLayout();
                mEnterLayout.restoreDelete(data);

                mEnterLayout.clearContent();
                mEnterLayout.hideKeyboard();
                mEnterLayout.content.setHint("");
                mEnterLayout.content.setTag(null);
                mEnterComment.clearContent();

                commentAdpter.notifyDataSetChanged();
                updateDynamicFromNetwork();
                updateRefResourceNetwork();
            } else {
                showErrorMsg(code, respanse);
            }
        } else if (tag.equals(hostDeleteComment)) {
            if (code == 0) {
                umengEvent(UmengEvent.TASK, "删除任务评论");

                int commentId = (int) data;
                for (int i = 0; i < mData.size(); ++i) {
                    if (mData.get(i).id == (commentId)) {
                        mData.remove(i);
                        break;
                    }
                }
                commentAdpter.notifyDataSetChanged();
                showButtomToast("删除成功");
                updateDynamicFromNetwork();

            } else {
                showErrorMsg(code, respanse);
            }
        } else if (tag.equals(TAG_TASK_UPDATE)) {
            showProgressBar(false);
            if (code == 0) {
                if (mOldParam != null && mNewParam != null
                        && mOldParam.status == SingleTask.STATUS_PROGRESS
                        && mNewParam.status == SingleTask.STATUS_FINISH) {
                    umengEvent(UmengEvent.E_TASK, "标记完成");
                } else {
                    umengEvent(UmengEvent.E_TASK, "修改任务");
                }
                closeActivity("修改任务成功");
            } else {
                showErrorMsg(code, respanse);
            }
        } else if (tag.equals(TaskListFragment.getHostTaskDelete())) {
            showProgressBar(false);
            if (code == 0) {
                umengEvent(UmengEvent.TASK, "删除任务");
                closeActivity("删除任务成功");
            } else {
                showErrorMsg(code, respanse);
            }
        } else if (tag.equals(tagTaskDetail)) {
            final View.OnClickListener onClickRetry = v -> requestTaskFromNetwork();
            if (code == 0) {
                mSingleTask = new SingleTask(respanse.getJSONObject("data"));
                initData();

                BlankViewDisplay.setBlank(1, this, true, blankLayout, onClickRetry);
            } else {
                showErrorMsg(code, respanse);
                if (code == 1600) {
                    mEnterComment.hide();
                    mTaskNoFound = true;
                    invalidateOptionsMenu();
                    BlankViewDisplay.setBlank(0, this, false, blankLayout, onClickRetry, "任务不存在");
                } else {
                    if (code == 1100) { // 项目不存在
                        mTaskNoFound = true;
                    }
                    BlankViewDisplay.setBlank(0, this, false, blankLayout, onClickRetry);
                }
            }
            hideProgressDialog();

        } else if (tag.equals(HOST_DESCRIPTER)) {
            if (code == 0) {
                descriptionData = new SingleTask.TaskDescription(respanse.getJSONObject("data"));
                descriptionDataNew = new SingleTask.TaskDescription(descriptionData);
                setDescription();

                descriptionButtonUpdate(false);
                descriptionButton.setOnClickListener(v -> TaskDescriptionActivity_
                        .intent(TaskAddActivity.this)
                        .descriptionData(descriptionDataNew)
                        .taskId(mSingleTask.getId())
                        .projectPath(mSingleTask.project.getProjectPath())
                        .startForResult(RESULT_REQUEST_DESCRIPTION));

            } else {
                showErrorMsg(code, respanse);
            }
        } else if (tag.equals(HOST_PREVIEW)) {
            if (code == 0) {
                descriptionDataNew.description = respanse.optString("data", "");
                setDescription();
            } else {
                showButtomToast("发生错误");
            }

            hideProgressDialog();
        } else if (tag.equals(tagUrlCommentPhoto)) {
            if (code == 0) {
                String fileUri;
                if (mSingleTask.project.isPublic()) {
                    fileUri = respanse.optString("data", "");
                } else {
                    AttachmentFileObject fileObject = new AttachmentFileObject(respanse.optJSONObject("data"));
                    fileUri = fileObject.owner_preview;
                }
                String mdPhotoUri = String.format("\n![图片](%s)", fileUri);
                mSendedImages.put((String) data, mdPhotoUri);
                sendCommentAll();
            } else {
                showErrorMsg(code, respanse);
                showProgressBar(false);
            }
        } else if (tag.equals(TAG_HTTP_REMOVE_LABEL)) {
            if (code == 0) {
                int labelId = (int) data;
                labelBar.removeLabel(labelId);
                mSingleTask.removeLabel(labelId);
            } else {
                showErrorMsg(code, respanse);
            }
        }
    }

    private void descriptionButtonUpdate(boolean loading) {
        if (!loading && descriptionDataNew.markdown.isEmpty()) {
            descriptionButton.setText("添加描述");
            descriptionButton.setTextColor(getResources().getColor(R.color.font_2));
        } else {
            descriptionButton.setText("查看描述");
            descriptionButton.setTextColor(getResources().getColor(R.color.font_green));
        }
    }

    @OnActivityResult(RESULT_REQUEST_PICK_PROJECT)
    final void pickProject(int result, Intent data) {
        if (result == RESULT_OK) {
            ProjectObject project = (ProjectObject) data.getSerializableExtra("data");
            mSingleTask.project = project;
            mSingleTask.project_id = project.getId();

            Member member = new Member(GlobalData.sUserObject);
            setPickUser(member);

            uiBindDataProject();
        } else {
            if (mSingleTask.project == null || mSingleTask.project.id == 0) {
                finish();
            }
        }
    }

    private void uiBindDataProject() {
        if (mSingleTask.project.isEmpty()) {
            return;
        }

        iconfromNetwork(layoutProjectName.getImage(), mSingleTask.project.icon);
        layoutProjectName.setText2(mSingleTask.project.name);

        updateLabels(mSingleTask.labels);


    }

    @OnActivityResult(ImageCommentLayout.RESULT_REQUEST_COMMENT_IMAGE)
    final void commentImage(int result, Intent data) {
        if (result == RESULT_OK) {
            mEnterComment.onActivityResult(
                    ImageCommentLayout.RESULT_REQUEST_COMMENT_IMAGE,
                    data);
        }
    }

    @OnActivityResult(ImageCommentLayout.RESULT_REQUEST_COMMENT_IMAGE_DETAIL)
    final void commentImageDetail(int result, Intent data) {
        if (result == RESULT_OK) {
            mEnterComment.onActivityResult(
                    ImageCommentLayout.RESULT_REQUEST_COMMENT_IMAGE_DETAIL,
                    data);
        }
    }

    @OnActivityResult(RESULT_REQUEST_DESCRIPTION)
    void resultDescription(int result, Intent data) {
        if (result == RESULT_OK) {
            updateDescriptionFromResult(data);
            updateSendButton();

            updateRefResourceNetwork();
        }
    }

    @OnActivityResult(RESULT_PICK_BOARD_LIST)
    void resultPickBoard(int result, @OnActivityResult.Extra BoardList intentData) {
        if (result == RESULT_OK) {
            if (!mSingleTask.isEmpty()) {
                ProjectObject project = mSingleTask.project;
                Network.getRetrofit(this)
                        .moveTaskToBoard(project.owner_user_name, project.name, intentData.boardId, intentData.id, mSingleTask.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseHttpObserver(this) {
                            @Override
                            public void onSuccess() {
                                super.onSuccess();
                                int oldBoardListId = 0;
                                if (mNewParam.taskBoard != null) {
                                    oldBoardListId = mNewParam.taskBoard.id;
                                }
                                setPickBoardList(intentData.id);

                                showButtomToast(String.format("已移动到 %s", intentData.title));
                                EventBus.getDefault().post(new EventBoardRefreshRequest(intentData.id, 1));
                                if (oldBoardListId != 0) {
                                    EventBus.getDefault().post(new EventBoardRefreshRequest(oldBoardListId, 1));
                                }
                            }
                        });
            } else {
                mNewParam.taskBoard = intentData;
                setPickBoardList(intentData.id);
            }
        }
    }

    @OnActivityResult(RESULT_REQUEST_DESCRIPTION_CREATE)
    void resultDescriptionCreate(int result, Intent data) {
        if (result == RESULT_OK) {
            updateDescriptionFromResult(data);
            updateSendButton();

            updateRefResourceNetwork();
        }
    }

    @OnActivityResult(RESULT_LABEL)
    void resultLabels(int result, @OnActivityResult.Extra ArrayList<TopicLabelObject> labels) {
        if (result == RESULT_OK) {
            mSingleTask.labels = labels;
            updateLabels(labels);
        }
    }

    @OnActivityResult(RESULT_REQUEST_PICK_WATCH_USER)
    void resultWatchUser(int result, @OnActivityResult.Extra ArrayList<UserObject> resultData) {
        if (result == RESULT_OK) {
            watchUsers = resultData;
            uiBindDataWatch();
        }
    }

    @OnActivityResult(RESULT_REQUEST_SELECT_USER)
    void resultSelectUser(int resultCode, @OnActivityResult.Extra Member members) {
        if (resultCode == Activity.RESULT_OK) {
            setPickUser(members);
        }
    }

    @OnActivityResult(RESULT_REQUEST_FOLLOW)
    void resultRequestFollow(int resultCode, @OnActivityResult.Extra String name) {
        if (resultCode == RESULT_OK) {
            mEnterComment.getEnterLayout().insertText(name);
        }
    }

    @OnActivityResult(RESULT_RESUSE_REFRESOURCE)
    void resultRefResource(int resultCode, @OnActivityResult.Extra ArrayList<RefResourceObject> resultData) {
        if (resultCode == RESULT_OK) {
            refResourceList = resultData;
            updateRefResourceUI();
        }
    }

    private void updateRefResourceNetwork() {
        if (mSingleTask.isEmpty()) {
            return;
        }

        String url = mSingleTask.project.getHttpProjectApi() +
                "/resource_reference/" + mSingleTask.getNumberValue();
        MyAsyncHttpClient.get(this, url, new MyJsonResponse(this) {
            @Override
            public void onMySuccess(JSONObject response) {
                super.onMySuccess(response);

                refResourceList.clear();
                JSONObject jsonData = response.optJSONObject("data");
                Iterator<String> iter = jsonData.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    JSONArray array = jsonData.optJSONArray(key);
                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject item = array.optJSONObject(i);
                        try {
                            refResourceList.add(new RefResourceObject(item));
                        } catch (Exception e) {
                            Global.errorLog(e);
                        }
                    }
                }

                updateRefResourceUI();
            }
        });

    }

    private void updateRefResourceUI() {
        if (mSingleTask.isEmpty()) {
            return;
        }

        if (refResourceList.isEmpty()) {
            layoutRefResourceParent.setVisibility(View.GONE);
        } else {
            layoutRefResourceParent.setVisibility(View.VISIBLE);
            TaskAttrItem item = (TaskAttrItem) layoutRefResourceParent.findViewById(R.id.layoutRefResource);
            item.setText2(refResourceList.size() + "个资源");
        }
    }

    void updateDescriptionFromResult(Intent data) {
        descriptionDataNew.markdown = data.getStringExtra("data");

        RequestParams params = new RequestParams();
        params.put("content", descriptionDataNew.markdown);
        postNetwork(HOST_PREVIEW, params, HOST_PREVIEW);

        descriptionButtonUpdate(false);
    }

    private void setPickUser(Member member) {
        mNewParam.ownerId = member.user.id;
        mNewParam.owner = member.user;
        selectMember();
        updateSendButton();
    }

    private void setPickBoardList(int id) {
        for (BoardList item : taskCreating.taskBoardList) {
            if (item.id == id) {
                mNewParam.taskBoard = item;
                mSingleTask.taskBoardList = item;
                selectMember();
                updateSendButton();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isContentUnmodify()) {
            showDialog("确定放弃此次编辑？", (dialog, which) -> closeActivity(""));
        } else {
            closeActivity("");
        }
    }

    private void popListSelectDialog(BaseAdapter selectsAdapter, DialogInterface.OnClickListener clickList) {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setAdapter(selectsAdapter, clickList)
                .show();
    }

    private void sendComment(String input) {
        mEnterComment.getEnterLayout().hideKeyboard();

        if (mSingleTask == null) {
            showButtomToast("发送评论失败");
            return;
        }

        EnterLayout mEnterLayout = mEnterComment.getEnterLayout();
        String s = input;

        if (EmojiFilter.containsEmptyEmoji(this, s)) {
            showProgressBar(false);
            return;
        }

        Object item = mEnterLayout.content.getTag();
        if (item != null && (item instanceof SingleTask.TaskComment)) {
            SingleTask.TaskComment comment = (SingleTask.TaskComment) item;
            s = Global.encodeInput(comment.owner.name, s);
        } else {
            s = Global.encodeInput("", s);
        }

        RequestParams params = new RequestParams();

        params.put("content", s);

        postNetwork(String.format(HOST_COMMENT_ADD, mSingleTask.getId()), params, HOST_COMMENT_ADD, 0, item);

        showProgressBar(R.string.sending_comment);
    }

    private void sendCommentAll() {
        showProgressBar(true);

        ArrayList<ImageInfo> photos = mEnterComment.getPickPhotos();
        for (ImageInfo item : photos) {
            String imagePath = item.path;
            if (!mSendedImages.containsKey(imagePath)) {
                try {
                    String url = mSingleTask.project.getHttpUploadPhoto();
                    RequestParams params = new RequestParams();
                    params.put("dir", 0);
                    Uri uri = Uri.parse(imagePath);
                    File file = new PhotoOperate(this).getFile(uri);
                    params.put("file", file);
                    tagUrlCommentPhoto = imagePath; // tag必须不同，否则无法调用下一次
                    postNetwork(url, params, tagUrlCommentPhoto, 0, imagePath);
                    showProgressBar(true);
                } catch (Exception e) {
                    showProgressBar(false);
                }

                return;
            }
        }

        StringBuilder sb = new StringBuilder(mEnterComment.getEnterLayout().getContent());
        for (ImageInfo item : photos) {
            sb.append(mSendedImages.get(item.path));
        }
        sendComment(sb.toString());
    }


}
