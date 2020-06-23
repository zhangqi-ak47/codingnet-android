package net.coding.program.common.model;

import net.coding.program.common.GlobalData;

import org.json.JSONObject;

import java.io.Serializable;

public class BaseComment implements Serializable {
    public String content = "";
    public long created_at; // 1408614375604,
    public int id; // 9291,
    public net.coding.program.common.model.DynamicObject.Owner owner = new net.coding.program.common.model.DynamicObject.Owner();
    public int owner_id; // 8205,

    public BaseComment(JSONObject json) {
        if (json == null) {
            return;
        }

        content = json.optString("content");
        created_at = json.optLong("created_at");
        id = json.optInt("id");

        if (json.has("owner")) {
            owner = new DynamicObject.Owner(json.optJSONObject("owner"));
        } else if (json.has("author")) {
            owner = new DynamicObject.Owner(json.optJSONObject("author"));
        }

        if (json.has("owner_id")) {
            owner_id = json.optInt("owner_id");
        }
    }

    public BaseComment(DynamicObject.DynamicProjectFileComment dynamic) {
        content = dynamic.getComment();
        id = dynamic.id;
        created_at = dynamic.created_at;
        owner = dynamic.getOwner();
        owner_id = 0;
    }

    public BaseComment() {
    }

    public boolean isEmpty() {
        return id == 0;
    }

    public boolean isMy() {
        if (owner_id != 0) {
            return GlobalData.sUserObject.id == owner_id;
        } else {
            return GlobalData.sUserObject.global_key.equals(owner.global_key);
        }
    }
}