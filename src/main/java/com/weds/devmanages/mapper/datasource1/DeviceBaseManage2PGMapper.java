package com.weds.devmanages.mapper.datasource1;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;



/**
 * 设备基础管理操作（PG库）
 *
 * @author tjy
 * @date 2021/8/11
 **/

@Mapper
@Component
public interface DeviceBaseManage2PGMapper {

    /**
     *  全量操作设备档案下发（全量档案）
     **/
    int insertFullOperationFile2PG(@Param("devId")String devId,
                                   @Param("operationNumber")Integer operationNumber);
    /**
     *  全量操作设备档案下发（全量规则）
     **/
    int insertFullOperationFileRule2PG(@Param("devId")String devId,
                                   @Param("operationNumber")Integer operationNumber);

}
