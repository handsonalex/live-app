package org.live.user.utils;

public class TagInfoUtils {

    public static boolean isContain(Long tagInfo,Long matchTag){
        return tagInfo != null && matchTag != null && (tagInfo & matchTag) == matchTag;
    }
}
