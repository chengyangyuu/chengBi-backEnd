package com.cheng.bibackend.bizmq;

import com.cheng.bibackend.common.ErrorCode;
import com.cheng.bibackend.constant.BiMqConstant;
import com.cheng.bibackend.exception.BusinessException;
import com.cheng.bibackend.manager.AiManager;
import com.cheng.bibackend.model.entity.Chart;
import com.cheng.bibackend.model.enums.ChartStatusEnum;
import com.cheng.bibackend.service.ChartService;
import com.rabbitmq.client.Channel;
import com.cheng.bibackend.constant.AiModelConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class BiMqMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    /**
     * 指定程序监听的消息队列和确认机制
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE}, ackMode = "MANUAL")
    private void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message={}", message);
        if (StringUtils.isBlank(message)) {
            // 消息为空，则拒绝掉消息 第三个参数 是否还要重新放回队列中
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接受到的消息为空");
        }
        // 获取到图表的id
        long chartId = Long.parseLong(message);
        // 从数据库中取出id
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            // 将消息拒绝
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图表为空");
        }
        // 等待-->执行中--> 成功/失败
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(ChartStatusEnum.RUNNING.getValue());
        boolean updateChartById = chartService.updateById(updateChart);
        if (!updateChartById) {
            // 将消息拒绝
            channel.basicNack(deliveryTag, false, false);
            Chart updateChartFailed = new Chart();
            updateChartFailed.setId(chart.getId());
            updateChartFailed.setStatus(ChartStatusEnum.FAILED.getValue());
            chartService.updateById(updateChartFailed);
            handleChartUpdateError(chart.getId(), "更新图表·执行中状态·失败");
            return;
        }
        // 调用AI
        String chartResult = aiManager.doChat(buildUserInput(chart), AiModelConstant.BI);

        //用"【【来分割 当初设计的"
        String[] splits = chartResult.split("【【【【【");
        if (splits.length < 3) {
            //throw new BusinessException(ErrorCode.SYSTEM_ERROR, "");
            handleChartUpdateError(chart.getId(), "AI生成错误");
            return;
        }
        // 生成前的内容  0是空字符串
        String preGenChart = splits[1].trim();
        if (StringUtils.isBlank(preGenChart)){
            // 内容生成错误，拒绝消息
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"AI生成错误");
        }

        // 判断是否包含有双引号，是否符合JSON格式
        boolean flag = preGenChart.substring(0, 10).chars()
                .mapToObj(c -> (char) c)
                .anyMatch(c -> c == '"');

        if (!flag){
            // 内容生成错误，拒绝消息
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"AI生成错误");
        }

        String genResult = splits[2].trim();

//        // 生成后端检验
//        String validGenChart = ChartUtils.getValidGenChart(preGenChart);


        // 生成的最终结果-成功
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(preGenChart);
        //updateChartResult.setGenChart(validGenChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setStatus(ChartStatusEnum.SUCCEED.getValue());
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            // 将消息拒绝
            channel.basicNack(deliveryTag, false, false);
            Chart updateChartFailed = new Chart();
            updateChartFailed.setId(chart.getId());
            updateChartFailed.setStatus(ChartStatusEnum.FAILED.getValue());
            chartService.updateById(updateChartFailed);
            handleChartUpdateError(chart.getId(), "更新图表·成功状态·失败");
        }

        // 成功，则确认消息
        channel.basicAck(deliveryTag, false);
    }

    /**
     * 构建用户的输入信息
     *
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String chartData = chart.getChartData();

        // 无需Prompt，直接调用现有模型
        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(chartData).append("\n");
        return userInput.toString();
    }

    public void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus(ChartStatusEnum.FAILED.getValue());
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }


}
