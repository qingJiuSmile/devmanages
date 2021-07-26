package com.weds.devmanages.entity.record;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 档案管理参数
 *
 * @author tjy
 **/
@Data
public class DevRecordParam {

    private String devIp;

    private String fieldName;

    private String order = "";

    @ApiModelProperty("当前页")
    private Integer pageNum = 1;

    @ApiModelProperty("当前页数")
    private Integer pageSize = 15;

    private String queryKey = "";

    private Integer rowCount;

    private String tableName;


}
