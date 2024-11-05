package com.ral.young.ftp.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author renyunhui
 * @description 这是一个AnalysisVO类
 * @date 2024-11-01 17-05-18
 * @since 1.0.0
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class AnalysisVO {

    @JsonProperty("code")
    private String code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class DataDTO {

        @JsonProperty("users")
        private List<UsersDTO> users;

        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class UsersDTO {

            @JsonProperty("id")
            private Long id;

            @JsonProperty("boxes")
            private List<BoxesDTO> boxes;

            @JsonProperty("content")
            private String content;

            @JsonProperty("labels")
            private List<LabelsDTO> labels;

            @NoArgsConstructor
            @Data
            @AllArgsConstructor
            @Builder
            public static class BoxesDTO {

                @JsonProperty("height")
                private Integer height;

                @JsonProperty("width")
                private Integer width;

                @JsonProperty("x")
                private Integer x;

                @JsonProperty("y")
                private Integer y;
            }

            @NoArgsConstructor
            @Data
            @AllArgsConstructor
            @Builder
            public static class LabelsDTO {

                @JsonProperty("labelName")
                private String labelName;

                @JsonProperty("des")
                private String des;
            }
        }
    }
}
