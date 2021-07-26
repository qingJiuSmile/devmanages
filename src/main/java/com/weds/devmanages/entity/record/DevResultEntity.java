package com.weds.devmanages.entity.record;

import lombok.Data;



/**
 * 设备数据返回处理类
 *
 * @author tjy
 **/
@Data
public class DevResultEntity {

    private Pagination pagination;

    @Data
    public static class Pagination {

        private Integer current;

        private Integer pageSize;

        private Long total;
    }
}
