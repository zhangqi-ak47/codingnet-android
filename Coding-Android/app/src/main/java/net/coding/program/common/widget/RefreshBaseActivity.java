package net.coding.program.common.widget;

import android.support.v4.widget.SwipeRefreshLayout;

import net.coding.program.R;
import net.coding.program.common.ui.BackActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by chenchao on 15/6/10.
 * 对 refresh 的操作放到基类里
 */
@EActivity
public abstract class RefreshBaseActivity extends BackActivity implements SwipeRefreshLayout.OnRefreshListener {

    @ViewById
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected final boolean isRefreshing() {
        return swipeRefreshLayout.isRefreshing();
    }

    protected final void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    @AfterViews
    protected final void initRefreshBaseActivity() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.font_green);
    }

    protected final void disableRefreshing() {
        swipeRefreshLayout.setEnabled(false);
    }
}
