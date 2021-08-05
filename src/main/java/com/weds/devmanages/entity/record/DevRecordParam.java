package com.weds.devmanages.entity.record;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weds.devmanages.entity.SignatureEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 档案管理参数
 *
 * @author tjy
 **/
@Data
public class DevRecordParam extends SignatureEntity {

    private String devIp;

    private String fieldName;

    private String order = "";

    @ApiModelProperty("当前页")
    @JsonProperty("page")
    private Integer pageNum = 1;

    @ApiModelProperty("当前页数")
    @JsonProperty("rows")
    private Integer pageSize = 15;

    private String queryKey = "";

    private Integer rowCount;

    private String tableName;


}
