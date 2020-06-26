package net.coding.program.network;

import net.coding.program.common.model.ProjectObject;
import net.coding.program.common.model.entity.ProjectOwnerVo;
import net.coding.program.common.model.entity.UserInfoObject;
import net.coding.program.common.model.form.ProjectForm;
import net.coding.program.common.model.util.PageUtils;
import net.coding.program.network.model.HttpResult;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface AppRequest {

    @POST("account/login")
    Observable<HttpResult<UserInfoObject>> login(@Body Map<String, String> map);

    @POST("business/project")
    Observable<HttpResult<PageUtils<ProjectOwnerVo>>> queryProjectData(@Body ProjectForm form);

}
