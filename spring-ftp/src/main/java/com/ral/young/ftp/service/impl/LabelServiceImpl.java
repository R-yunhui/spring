package com.ral.young.ftp.service.impl;

import cn.hutool.core.map.MapUtil;
import com.ral.young.ftp.entity.BodyLabelEntity;
import com.ral.young.ftp.entity.FaceLabelEntity;
import com.ral.young.ftp.service.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LabelServiceImpl implements LabelService {

    // 人脸标签存储
    private final List<FaceLabelEntity> faceLabels = new ArrayList<>();
    private final Map<Integer, FaceLabelEntity> faceIndexMap = new HashMap<>();

    // 人体标签存储
    private final List<BodyLabelEntity> bodyLabels = new ArrayList<>();

    private final Map<Integer, Map<String, BodyLabelEntity>> bodyParentIndexMap = new HashMap<>();

    @PostConstruct
    public void init() {
        initFaceLabels();
        initBodyLabels();
        buildMaps();
        log.info("标签数据初始化完成");
    }

    private void initFaceLabels() {
        faceLabels.addAll(Arrays.asList(
                new FaceLabelEntity(1, "_5o_Clock_Shadow", "满脸胡须"),
                new FaceLabelEntity(2, "Arched_Eyebrows", "拱形眉毛"),
                new FaceLabelEntity(3, "Attractive", "漂亮的"),
                new FaceLabelEntity(4, "Bags_Under_Eyes", "眼袋"),
                new FaceLabelEntity(5, "Bald", "秃头"),
                new FaceLabelEntity(6, "Bangs", "刘海"),
                new FaceLabelEntity(7, "Big_Lips", "大嘴唇"),
                new FaceLabelEntity(8, "Big_Nose", "大鼻子"),
                new FaceLabelEntity(9, "Black_Hair", "黑头发"),
                new FaceLabelEntity(10, "Blond_Hair", "金发"),
                new FaceLabelEntity(11, "Blurry", "模糊"),
                new FaceLabelEntity(12, "Brown_Hair", "棕色头发"),
                new FaceLabelEntity(13, "Bushy_Eyebrows", "浓密的眉毛"),
                new FaceLabelEntity(14, "Chubby", "圆胖的"),
                new FaceLabelEntity(15, "Double_Chin", "双下巴"),
                new FaceLabelEntity(16, "Eyeglasses", "戴眼镜的"),
                new FaceLabelEntity(17, "Goatee", "山羊胡"),
                new FaceLabelEntity(18, "Gray_Hair", "灰色头发"),
                new FaceLabelEntity(19, "Heavy_Makeup", "浓妆"),
                new FaceLabelEntity(20, "High_Cheekbones", "高颧骨"),
                new FaceLabelEntity(21, "Male", "男性"),
                new FaceLabelEntity(22, "Mouth_Slightly_Open", "嘴略张开"),
                new FaceLabelEntity(23, "Mustache", "小胡子"),
                new FaceLabelEntity(24, "Narrow_Eyes", "小眼睛"),
                new FaceLabelEntity(25, "No_Beard", "无胡子"),
                new FaceLabelEntity(26, "Oval_Face", "鹅蛋脸"),
                new FaceLabelEntity(27, "Pale_Skin", "苍白皮肤"),
                new FaceLabelEntity(28, "Pointy_Nose", "尖鼻"),
                new FaceLabelEntity(29, "Receding_Hairline", "发际线较高"),
                new FaceLabelEntity(30, "Rosy_Cheeks", "红扑扑的脸"),
                new FaceLabelEntity(31, "Sideburns", "两鬓胡须"),
                new FaceLabelEntity(32, "Smiling", "微笑"),
                new FaceLabelEntity(33, "Straight_Hair", "直发"),
                new FaceLabelEntity(34, "Wavy_Hair", "波浪状头发"),
                new FaceLabelEntity(35, "Wearing_Earrings", "戴耳环"),
                new FaceLabelEntity(36, "Wearing_Hat", "戴着帽子"),
                new FaceLabelEntity(37, "Wearing_Lipstick", "涂口红"),
                new FaceLabelEntity(38, "Wearing_Necklace", "戴项链"),
                new FaceLabelEntity(39, "Wearing_Necktie", "系领带"),
                new FaceLabelEntity(40, "Young", "青年")
        ));
    }

    private void initBodyLabels() {
        // 1. 鞋子种类 (parentIndex = 1)
        addBodyLabels(1, "鞋子种类", Arrays.asList(
                new BodyLabelEntity(0, 1, "unknown", "未知", "鞋子种类"),
                new BodyLabelEntity(1, 1, "leather_shoes", "皮鞋", "鞋子种类"),
                new BodyLabelEntity(2, 1, "boots", "靴子", "鞋子种类"),
                new BodyLabelEntity(3, 1, "sandals", "凉鞋", "鞋子种类"),
                new BodyLabelEntity(4, 1, "casual_shoes", "休闲鞋", "鞋子种类")
        ));

        // 2. 遮挡 (parentIndex = 2)
        addBodyLabels(2, "遮挡", Arrays.asList(
                new BodyLabelEntity(0, 2, "non_truncated", "无遮挡", "遮挡"),
                new BodyLabelEntity(1, 2, "partly_truncated", "部分遮挡", "遮挡"),
                new BodyLabelEntity(2, 2, "mostly_truncated", "完全遮挡", "遮挡")
        ));

        // 3. 裤子纹理 (parentIndex = 3)
        addBodyLabels(3, "裤子纹理", Arrays.asList(
                new BodyLabelEntity(0, 3, "unknown", "未知", "裤子纹理"),
                new BodyLabelEntity(1, 3, "stripes", "条纹", "裤子纹理"),
                new BodyLabelEntity(2, 3, "solid_color", "纯色", "裤子纹理"),
                new BodyLabelEntity(3, 3, "large_areas_of_single_colors", "大面积单色", "裤子纹理"),
                new BodyLabelEntity(4, 3, "squares", "格子", "裤子纹理")
        ));

        // 4. 出行方式 (parentIndex = 4)
        addBodyLabels(4, "出行方式", Arrays.asList(
                new BodyLabelEntity(0, 4, "cyclist", "骑单车人", "出行方式"),
                new BodyLabelEntity(1, 4, "pedestrian", "行人", "出行方式")
        ));

        // 5. 鞋子颜色 (parentIndex = 5)
        addBodyLabels(5, "鞋子颜色", Arrays.asList(
                new BodyLabelEntity(0, 5, "blue", "蓝色", "鞋子颜色"),
                new BodyLabelEntity(1, 5, "brown", "棕色", "鞋子颜色"),
                new BodyLabelEntity(2, 5, "unknown", "未知", "鞋子颜色"),
                new BodyLabelEntity(3, 5, "purple", "紫色", "鞋子颜色"),
                new BodyLabelEntity(4, 5, "grey", "灰色", "鞋子颜色"),
                new BodyLabelEntity(5, 5, "yellow", "黄色", "鞋子颜色"),
                new BodyLabelEntity(6, 5, "black", "黑色", "鞋子颜色"),
                new BodyLabelEntity(7, 5, "cyan", "青色", "鞋子颜色"),
                new BodyLabelEntity(8, 5, "green", "绿色", "鞋子颜色"),
                new BodyLabelEntity(9, 5, "white", "白色", "鞋子颜色"),
                new BodyLabelEntity(10, 5, "magenta", "品红色", "鞋子颜色"),
                new BodyLabelEntity(11, 5, "red", "红色", "鞋子颜色")
        ));

        // 6. 裤子类型 (parentIndex = 6)
        addBodyLabels(6, "裤子类型", Arrays.asList(
                new BodyLabelEntity(0, 6, "unknown", "未知", "裤子类型"),
                new BodyLabelEntity(1, 6, "long_skirts", "长裙", "裤子类型"),
                new BodyLabelEntity(2, 6, "shorts", "短裤", "裤子类型"),
                new BodyLabelEntity(3, 6, "short_skirts", "短裙", "裤子类型"),
                new BodyLabelEntity(4, 6, "trousers", "长裤", "裤子类型")
        ));

        // 7. 裤子颜色 (parentIndex = 7)
        addBodyLabels(7, "裤子颜色", Arrays.asList(
                new BodyLabelEntity(0, 7, "blue", "蓝色", "裤子颜色"),
                new BodyLabelEntity(1, 7, "unknown", "未知", "裤子颜色"),
                new BodyLabelEntity(2, 7, "brown", "棕色", "裤子颜色"),
                new BodyLabelEntity(3, 7, "purple", "紫色", "裤子颜色"),
                new BodyLabelEntity(4, 7, "green", "绿色", "裤子颜色"),
                new BodyLabelEntity(5, 7, "grey", "灰色", "裤子颜色"),
                new BodyLabelEntity(6, 7, "yellow", "黄色", "裤子颜色"),
                new BodyLabelEntity(7, 7, "black", "黑色", "裤子颜色"),
                new BodyLabelEntity(8, 7, "orange", "橙色", "裤子颜色"),
                new BodyLabelEntity(9, 7, "cyan", "青色", "裤子颜色"),
                new BodyLabelEntity(10, 7, "white", "白色", "裤子颜色"),
                new BodyLabelEntity(11, 7, "magenta", "品红色", "裤子颜色"),
                new BodyLabelEntity(12, 7, "red", "红色", "裤子颜色")
        ));

        // 8. 身体朝向 (parentIndex = 8)
        addBodyLabels(8, "身体朝向", Arrays.asList(
                new BodyLabelEntity(0, 8, "right", "朝右", "身体朝向"),
                new BodyLabelEntity(1, 8, "unknown", "未知", "身体朝向"),
                new BodyLabelEntity(2, 8, "back", "朝后", "身体朝向"),
                new BodyLabelEntity(3, 8, "front", "朝前", "身体朝向"),
                new BodyLabelEntity(4, 8, "left", "朝左", "身体朝向")
        ));

        // 9. 障碍物 (parentIndex = 9)
        addBodyLabels(9, "障碍物", Arrays.asList(
                new BodyLabelEntity(0, 9, "non_occluded", "无障碍", "障碍物"),
                new BodyLabelEntity(1, 9, "mostly_occluded", "大部分有障碍", "障碍物"),
                new BodyLabelEntity(2, 9, "partly_occluded", "局部有障碍", "障碍物")
        ));

        // 10. 年龄段 (parentIndex = 10)
        addBodyLabels(10, "年龄段", Arrays.asList(
                new BodyLabelEntity(0, 10, "middle_age", "中年人", "年龄段"),
                new BodyLabelEntity(1, 10, "the_youth", "青年", "年龄段"),
                new BodyLabelEntity(2, 10, "students", "学生", "年龄段"),
                new BodyLabelEntity(3, 10, "infants", "婴儿", "年龄段"),
                new BodyLabelEntity(4, 10, "senior", "老年人", "年龄段"),
                new BodyLabelEntity(5, 10, "children", "小孩子", "年龄段")
        ));

        // 11. 衣服颜色 (parentIndex = 11)
        addBodyLabels(11, "衣服颜色", Arrays.asList(
                new BodyLabelEntity(0, 11, "blue", "蓝色", "衣服颜色"),
                new BodyLabelEntity(1, 11, "brown", "棕色", "衣服颜色"),
                new BodyLabelEntity(2, 11, "unknown", "未知", "衣服颜色"),
                new BodyLabelEntity(3, 11, "purple", "紫色", "衣服颜色"),
                new BodyLabelEntity(4, 11, "grey", "灰色", "衣服颜色"),
                new BodyLabelEntity(5, 11, "yellow", "黄色", "衣服颜色"),
                new BodyLabelEntity(6, 11, "black", "黑色", "衣服颜色"),
                new BodyLabelEntity(7, 11, "cyan", "青色", "衣服颜色"),
                new BodyLabelEntity(8, 11, "orange", "橙色", "衣服颜色"),
                new BodyLabelEntity(9, 11, "green", "绿色", "衣服颜色"),
                new BodyLabelEntity(10, 11, "white", "白色", "衣服颜色"),
                new BodyLabelEntity(11, 11, "magenta", "品红色", "衣服颜色"),
                new BodyLabelEntity(12, 11, "red", "红色", "衣服颜色")
        ));

        // 12. 发型 (parentIndex = 12)
        addBodyLabels(12, "发型", Arrays.asList(
                new BodyLabelEntity(0, 12, "unknown", "未知", "发型"),
                new BodyLabelEntity(1, 12, "short_hair", "短发", "发型"),
                new BodyLabelEntity(2, 12, "wearing_hat", "戴帽子", "发型"),
                new BodyLabelEntity(3, 12, "baldness", "秃顶", "发型"),
                new BodyLabelEntity(4, 12, "long_hair", "长发", "发型")
        ));

        // 13. 附带物品 (parentIndex = 13)
        addBodyLabels(13, "附带物品", Arrays.asList(
                new BodyLabelEntity(0, 13, "none", "无", "附带物品"),
                new BodyLabelEntity(1, 13, "umbrella", "伞", "附带物品"),
                new BodyLabelEntity(2, 13, "backpack", "背包", "附带物品"),
                new BodyLabelEntity(3, 13, "cart", "手推车", "附带物品"),
                new BodyLabelEntity(4, 13, "messenger_bag", "信使包", "附带物品"),
                new BodyLabelEntity(5, 13, "unknown", "未知", "附带物品"),
                new BodyLabelEntity(6, 13, "handbag", "手提包", "附带物品")
        ));

        // 14. 口罩 (parentIndex = 14)
        addBodyLabels(14, "口罩", Arrays.asList(
                new BodyLabelEntity(0, 14, "unknown", "未知", "口罩"),
                new BodyLabelEntity(1, 14, "no_mask", "无口罩", "口罩"),
                new BodyLabelEntity(2, 14, "mask", "有口罩", "口罩")
        ));

        // 15. 性别 (parentIndex = 15)
        addBodyLabels(15, "性别", Arrays.asList(
                new BodyLabelEntity(0, 15, "male", "男性", "性别"),
                new BodyLabelEntity(1, 15, "female", "女性", "性别")
        ));

        // 16. 衣服纹理 (parentIndex = 16)
        addBodyLabels(16, "衣服纹理", Arrays.asList(
                new BodyLabelEntity(0, 16, "unknown", "未知", "衣服纹理"),
                new BodyLabelEntity(1, 16, "stripes", "条纹", "衣服纹理"),
                new BodyLabelEntity(2, 16, "solid_color", "纯色", "衣服纹理"),
                new BodyLabelEntity(3, 16, "large_areas_of_single_colors", "大面积单色", "衣服纹理"),
                new BodyLabelEntity(4, 16, "squares", "格子", "衣服纹理")
        ));

        // 17. 衣服类型 (parentIndex = 17)
        addBodyLabels(17, "衣服类型", Arrays.asList(
                new BodyLabelEntity(0, 17, "coat", "外套", "衣服类型"),
                new BodyLabelEntity(1, 17, "unknown", "未知", "衣服类型"),
                new BodyLabelEntity(2, 17, "shirt", "衬衫", "衣服类型"),
                new BodyLabelEntity(3, 17, "sleeveless", "无袖的", "衣服类型"),
                new BodyLabelEntity(4, 17, "sweater", "毛衣", "衣服类型"),
                new BodyLabelEntity(5, 17, "T_shirt", "体恤", "衣服类型"),
                new BodyLabelEntity(6, 17, "suit_jacket", "西装外套", "衣服类型"),
                new BodyLabelEntity(7, 17, "hoody", "连帽衫", "衣服类型")
        ));

        // 18. 眼镜 (parentIndex = 18)
        addBodyLabels(18, "眼镜", Arrays.asList(
                new BodyLabelEntity(0, 18, "sunglasses", "太阳镜", "眼镜"),
                new BodyLabelEntity(1, 18, "no_glasses", "无眼镜", "眼镜"),
                new BodyLabelEntity(2, 18, "unknown", "未知", "眼镜"),
                new BodyLabelEntity(3, 18, "transparent_glasses", "透明眼镜", "眼镜")
        ));
    }

    private void addBodyLabels(Integer parentIndex, String category, List<BodyLabelEntity> labels) {
        bodyLabels.addAll(labels);
        Map<String, BodyLabelEntity> englishNameMap = new HashMap<>();
        labels.forEach(label -> englishNameMap.put(label.getEnglishName(), label));
        bodyParentIndexMap.put(parentIndex, englishNameMap);
    }

    private void buildMaps() {
        faceLabels.forEach(label -> faceIndexMap.put(label.getIndex(), label));
    }

    @Override
    public FaceLabelEntity findFaceLabelByIndex(Integer index) {
        return faceIndexMap.get(index);
    }

    @Override
    public List<FaceLabelEntity> getAllFaceLabels() {
        return new ArrayList<>(faceLabels);
    }

    @Override
    public BodyLabelEntity findBodyLabelByParentIndexAndEnglishName(Integer parentIndex, String englishName) {
        Map<String, BodyLabelEntity> englishNameMap = bodyParentIndexMap.get(parentIndex);

        return MapUtil.isEmpty(englishNameMap) ? null : englishNameMap.get(englishName);
    }

    @Override
    public List<BodyLabelEntity> getBodyLabelsByParentIndex(Integer parentIndex) {
        Map<String, BodyLabelEntity> englishNameMap = bodyParentIndexMap.get(parentIndex);
        return MapUtil.isEmpty(englishNameMap) ? null : new ArrayList<>(englishNameMap.values());
    }
}