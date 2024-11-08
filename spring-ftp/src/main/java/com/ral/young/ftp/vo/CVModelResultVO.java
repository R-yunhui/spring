package com.ral.young.ftp.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author renyunhui
 * @description 调用 CV 模型返回的接口数据
 * @date 2024-11-01 15-45-23
 * @since 1.0.0
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class CVModelResultVO {

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("image")
    private String image;

    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class DataDTO {

        @JsonProperty("id")
        private String id;

        @JsonProperty("body")
        private BodyDTO body;

        @JsonProperty("face")
        private FaceDTO face;

        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class BodyDTO {

            @JsonProperty("box")
            private BoxDTO box;

            @JsonProperty("label")
            private String label;

            @JsonProperty("confidence")
            private Double confidence;

            @JsonProperty("props")
            private List<PropsDTO> props;
        }

        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class FaceDTO {

            @JsonProperty("box")
            private BoxDTO box;

            @JsonProperty("confidence")
            private Double confidence;

            @JsonProperty("props")
            private List<PropsDTO> props;
        }

        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class BoxDTO {

            @JsonProperty("x")
            private Integer x;

            @JsonProperty("y")
            private Integer y;

            @JsonProperty("width")
            private Integer width;

            @JsonProperty("height")
            private Integer height;
        }

        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class PropsDTO {

            @JsonProperty("label")
            private String label;

            @JsonProperty("confidence")
            private Double confidence;

            @JsonProperty("cls")
            private Integer cls;
        }
    }
}
