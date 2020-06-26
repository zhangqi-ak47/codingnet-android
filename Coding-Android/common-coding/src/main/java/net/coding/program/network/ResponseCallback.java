package net.coding.program.network;

import android.support.annotation.NonNull;

public interface ResponseCallback<T> {
    default void success(int tag, T data){

    }
    default void fail(int tag, int errorCode, @NonNull String error){

    }
    default void start(int tag){

    }
    default void end(int tag){

    }
    default String loading(int tag){
        return "";
    }
    default boolean successToast(int tag){
        return false;
    }
    default boolean failToast(int tag){
        return true;
    }
}
