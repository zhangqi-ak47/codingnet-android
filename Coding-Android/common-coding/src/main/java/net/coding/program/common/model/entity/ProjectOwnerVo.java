package net.coding.program.common.model.entity;

public class ProjectOwnerVo extends ProjectEntity {

    private Integer oId;
    /**
     *
     */
    private String avatar;
    /**
     *
     */
    private String globalKey;
    /**
     *
     */
    private String oName;
    /**
     *
     */
    private String path;

    public Integer getoId() {
        return oId;
    }

    public void setoId(Integer oId) {
        this.oId = oId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGlobalKey() {
        return globalKey;
    }

    public void setGlobalKey(String globalKey) {
        this.globalKey = globalKey;
    }

    public String getoName() {
        return oName;
    }

    public void setoName(String oName) {
        this.oName = oName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
