package com.ral.young.ftp.service;

import com.ral.young.ftp.entity.BodyLabelEntity;
import com.ral.young.ftp.entity.FaceLabelEntity;

import java.util.List;

public interface LabelService {

    // 人脸标签查询
    FaceLabelEntity findFaceLabelByIndex(Integer index);

    List<FaceLabelEntity> getAllFaceLabels();

    // 人体标签查询
    BodyLabelEntity findBodyLabelByParentIndexAndEnglishName(Integer parentIndex, String englishName);

    List<BodyLabelEntity> getBodyLabelsByParentIndex(Integer parentIndex);
}