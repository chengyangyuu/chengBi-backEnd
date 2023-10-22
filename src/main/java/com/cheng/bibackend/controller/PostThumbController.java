package com.cheng.bibackend.controller;

import com.cheng.bibackend.common.BaseResponse;
import com.cheng.bibackend.common.ErrorCode;
import com.cheng.bibackend.common.ResultUtils;
import com.cheng.bibackend.exception.BusinessException;
import com.cheng.bibackend.model.entity.User;
import com.cheng.bibackend.service.PostThumbService;
import com.cheng.bibackend.model.dto.postthumb.PostThumbAddRequest;
import com.cheng.bibackend.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子点赞接口
 */
@RestController
@RequestMapping("/post_thumb")
@Slf4j
//@CrossOrigin(origins = "http://chengbi.icu", allowCredentials = "true")
//@CrossOrigin(origins = "http://localhost:8000", allowCredentials = "true")
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
                                         HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }

}
