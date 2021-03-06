package com.zhouzifei.tool.consts;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 周子斐 (17600004572@163.com)
 * @remark 2021/1/27

 * @Description
 */
public enum StorageTypeConst {

    ALIYUN("aliyun"),
    QINIUYUN("qiniu"),
    LOCAL("local"),
    TENGXUNYUN("tengxunyun"),
    YOUPAIYUN("youpaiyun"),
    HUAWEIYUN("huaweiyun"),
    ;

    private String storageType;

    StorageTypeConst(String storageType) {
        this.storageType = storageType;
}
    public String getStorageType() {
        return storageType;
    }
    public static final List<String> list = Arrays.stream(values()).map(StorageTypeConst::getStorageType).collect(Collectors.toList());
}
