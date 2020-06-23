package net.coding.program.common;

import android.net.Uri;

import java.io.File;
import java.io.Serializable;

/**
 * Created by chenchao on 15/5/6.
 */
public class ImageInfo implements Serializable {
    private static final String prefix = "file://";
    public String path;
    public long photoId;
    public int width;
    public int height;

    public ImageInfo(String path) {
        this.path = pathAddPreFix(path);
    }

    public static String pathAddPreFix(String path) {
        if (path == null) {
            path = "";
        }

        if (path.startsWith("/")) {
            path = prefix + path;
        }
        return path;
    }

    public static boolean isLocalFile(String path) {
        return path.startsWith(prefix);
    }

    // path 是 uri 格式，file:///storage/emulated/0/DCIM/Camera/IMG_20170911_173528.jpg
    // getPath 是绝对路径，/storage/emulated/0/DCIM/Camera/IMG_20170911_173528.jpg
    public String getPath() {
        return Uri.parse(path).getPath();
    }

    public static File getLocalFile(String pathSrc) {
        String pathDesc = pathSrc;
        if (isLocalFile(pathSrc)) {
            pathDesc = pathSrc.substring(prefix.length(), pathSrc.length());
        }

        return new File(pathDesc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageInfo imageInfo = (ImageInfo) o;

        if (photoId != imageInfo.photoId) return false;
        if (width != imageInfo.width) return false;
        if (height != imageInfo.height) return false;
        return !(path != null ? !path.equals(imageInfo.path) : imageInfo.path != null);

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (int) (photoId ^ (photoId >>> 32));
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
