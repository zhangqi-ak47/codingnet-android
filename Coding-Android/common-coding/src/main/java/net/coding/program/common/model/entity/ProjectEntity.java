package net.coding.program.common.model.entity;

import java.io.Serializable;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-04-17 14:27:43
 */
public class ProjectEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private Integer id;
	/**
	 * 
	 */
	private Integer groupId;
	/**
	 * 
	 */
	private Integer plan;
	/**
	 * 
	 */
	private Boolean team;
	/**
	 * 
	 */
	private Integer maxMember;
	/**
	 * 
	 */
	private Integer recommended;
	/**
	 * 
	 */
	private String backendProjectPath;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private Integer ownerId;
	/**
	 * 
	 */
	private String ownerUserHome;
	/**
	 * 
	 */
	private String ownerUserName;
	/**
	 * 
	 */
	private String ownerUserPicture;
	/**
	 * 
	 */
	private String projectPath;
	/**
	 * 
	 */
	private String sshUrl;
	/**
	 * 
	 */
	private String currentUserRole;
	/**
	 * 
	 */
	private Integer currentUserRoleId;
	/**
	 * 
	 */
	private String depotPath;
	/**
	 * 
	 */
	private String description;
	/**
	 * 
	 */
	private String gitUrl;
	/**
	 * 
	 */
	private String httpsUrl;
	/**
	 * 
	 */
	private String icon;
	/**
	 * 
	 */
	private Integer forkCount;
	/**
	 * 
	 */
	private Boolean forked;
	/**
	 * 
	 */
	private Integer createdAt;
	/**
	 * 
	 */
	private Integer starCount;
	/**
	 * 
	 */
	private Boolean stared;
	/**
	 * 
	 */
	private Integer status;
	/**
	 * 
	 */
	private Integer unReadActivitiesCount;
	/**
	 * 
	 */
	private Integer updateAt;
	/**
	 * 
	 */
	private Integer watchCount;
	/**
	 * 
	 */
	private Boolean watched;
	/**
	 * 
	 */
	private Boolean isPublic;
	/**
	 * 
	 */
	private Integer memberNum;
	/**
	 * 
	 */
	private Boolean pin;
	/**
	 * 
	 */
	private Integer type;
	/**
	 * 
	 */
	private Boolean shared;
	/**
	 * 
	 */
	private String forkPath;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getPlan() {
		return plan;
	}

	public void setPlan(Integer plan) {
		this.plan = plan;
	}

	public Boolean getTeam() {
		return team;
	}

	public void setTeam(Boolean team) {
		this.team = team;
	}

	public Integer getMaxMember() {
		return maxMember;
	}

	public void setMaxMember(Integer maxMember) {
		this.maxMember = maxMember;
	}

	public Integer getRecommended() {
		return recommended;
	}

	public void setRecommended(Integer recommended) {
		this.recommended = recommended;
	}

	public String getBackendProjectPath() {
		return backendProjectPath;
	}

	public void setBackendProjectPath(String backendProjectPath) {
		this.backendProjectPath = backendProjectPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerUserHome() {
		return ownerUserHome;
	}

	public void setOwnerUserHome(String ownerUserHome) {
		this.ownerUserHome = ownerUserHome;
	}

	public String getOwnerUserName() {
		return ownerUserName;
	}

	public void setOwnerUserName(String ownerUserName) {
		this.ownerUserName = ownerUserName;
	}

	public String getOwnerUserPicture() {
		return ownerUserPicture;
	}

	public void setOwnerUserPicture(String ownerUserPicture) {
		this.ownerUserPicture = ownerUserPicture;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getSshUrl() {
		return sshUrl;
	}

	public void setSshUrl(String sshUrl) {
		this.sshUrl = sshUrl;
	}

	public String getCurrentUserRole() {
		return currentUserRole;
	}

	public void setCurrentUserRole(String currentUserRole) {
		this.currentUserRole = currentUserRole;
	}

	public Integer getCurrentUserRoleId() {
		return currentUserRoleId;
	}

	public void setCurrentUserRoleId(Integer currentUserRoleId) {
		this.currentUserRoleId = currentUserRoleId;
	}

	public String getDepotPath() {
		return depotPath;
	}

	public void setDepotPath(String depotPath) {
		this.depotPath = depotPath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGitUrl() {
		return gitUrl;
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

	public String getHttpsUrl() {
		return httpsUrl;
	}

	public void setHttpsUrl(String httpsUrl) {
		this.httpsUrl = httpsUrl;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getForkCount() {
		return forkCount;
	}

	public void setForkCount(Integer forkCount) {
		this.forkCount = forkCount;
	}

	public Boolean getForked() {
		return forked;
	}

	public void setForked(Boolean forked) {
		this.forked = forked;
	}

	public Integer getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Integer createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getStarCount() {
		return starCount;
	}

	public void setStarCount(Integer starCount) {
		this.starCount = starCount;
	}

	public Boolean getStared() {
		return stared;
	}

	public void setStared(Boolean stared) {
		this.stared = stared;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getUnReadActivitiesCount() {
		return unReadActivitiesCount;
	}

	public void setUnReadActivitiesCount(Integer unReadActivitiesCount) {
		this.unReadActivitiesCount = unReadActivitiesCount;
	}

	public Integer getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Integer updateAt) {
		this.updateAt = updateAt;
	}

	public Integer getWatchCount() {
		return watchCount;
	}

	public void setWatchCount(Integer watchCount) {
		this.watchCount = watchCount;
	}

	public Boolean getWatched() {
		return watched;
	}

	public void setWatched(Boolean watched) {
		this.watched = watched;
	}

	public Boolean getPublic() {
		return isPublic;
	}

	public void setPublic(Boolean aPublic) {
		isPublic = aPublic;
	}

	public Integer getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(Integer memberNum) {
		this.memberNum = memberNum;
	}

	public Boolean getPin() {
		return pin;
	}

	public void setPin(Boolean pin) {
		this.pin = pin;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	public String getForkPath() {
		return forkPath;
	}

	public void setForkPath(String forkPath) {
		this.forkPath = forkPath;
	}
}
