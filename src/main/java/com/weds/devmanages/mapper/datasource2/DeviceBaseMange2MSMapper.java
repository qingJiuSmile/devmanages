package com.weds.devmanages.mapper.datasource2;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 设备基础管理操作（MSSQL库）
 *
 * @author tjy
 * @date 2021/8/11
 **/

@Mapper
@Component
public interface DeviceBaseMange2MSMapper {

    /**
     *  全量操作设备档案下发
     **/
    int insertFullOperationFile2MS(@Param("devId")String devId,
                                   @Param("operationNumber")Integer operationNumber);
    /**
     *  全量操作设备档案下发
     **/
    int insertFullOperationFileRule2MS(@Param("devId")String devId,
                                   @Param("operationNumber")Integer operationNumber);
}
