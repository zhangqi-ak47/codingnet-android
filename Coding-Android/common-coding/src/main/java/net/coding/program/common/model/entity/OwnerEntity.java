package net.coding.program.common.model.entity;


import java.io.Serializable;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-04-17 14:27:43
 */
public class OwnerEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private Integer id;
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
	private String name;
	/**
	 * 
	 */
	private String path;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
