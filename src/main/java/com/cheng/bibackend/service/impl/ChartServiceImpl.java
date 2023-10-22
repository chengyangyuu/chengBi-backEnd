package com.cheng.bibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.bibackend.mapper.ChartMapper;
import com.cheng.bibackend.model.entity.Chart;
import com.cheng.bibackend.service.ChartService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{
//
//    @Resource
//    private ChartService chartService;
//
//    @Override
//    public void handleChartUpdateError(long chartId, String execMessage) {
//        Chart updateChartResult = new Chart();
//        updateChartResult.setId(chartId);
//        updateChartResult.setStatus(ChartStatusEnum.FAILED.getValue());
//        updateChartResult.setExecMessage(execMessage);
//        boolean updateResult = chartService.updateById(updateChartResult);
//        if (!updateResult) {
//            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
//        }
//    }
}




