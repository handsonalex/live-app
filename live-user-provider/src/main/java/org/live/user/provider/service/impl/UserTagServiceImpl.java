package org.live.user.provider.service.impl;

import jakarta.annotation.Resource;
import org.live.user.constants.UserTagFieldNameConstants;
import org.live.user.constants.UserTagsEnum;
import org.live.user.provider.dao.mapper.IUserTagMapper;
import org.live.user.provider.dao.po.UserTagPO;
import org.live.user.provider.service.IUserTagService;
import org.live.user.utils.TagInfoUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class UserTagServiceImpl implements IUserTagService {

    @Resource
    private IUserTagMapper userTagMapper;

    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagMapper.setTag(userId,userTagsEnum.getFieldName(),userTagsEnum.getTag()) > 0;
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagMapper.cancelTag(userId,userTagsEnum.getFieldName(),userTagsEnum.getTag()) > 0;
    }

    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        UserTagPO userTagPO = userTagMapper.selectById(userId);
        if (userTagPO == null){
            return false;
        }
        String queryFieldName = userTagsEnum.getFieldName();
        if (UserTagFieldNameConstants.TAG_INFO_01.equals(queryFieldName)) {
            return TagInfoUtils.isContain(userTagPO.getTagInfo01(), userTagsEnum.getTag());
        } else if (UserTagFieldNameConstants.TAG_INFO_02.equals(queryFieldName)) {
            return TagInfoUtils.isContain(userTagPO.getTagInfo02(), userTagsEnum.getTag());
        } else if (UserTagFieldNameConstants.TAG_INFO_03.equals(queryFieldName)) {
            return TagInfoUtils.isContain(userTagPO.getTagInfo03(), userTagsEnum.getTag());
        }
        return false;

    }
}
