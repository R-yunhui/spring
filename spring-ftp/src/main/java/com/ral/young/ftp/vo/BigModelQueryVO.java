package com.ral.young.ftp.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author renyunhui
 * @description 大模型接口请求参数
 * @date 2024-11-01 15-03-04
 * @since 1.0.0
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class BigModelQueryVO {

    @JsonProperty("temperature")
    private Double temperature;

    @JsonProperty("messages")
    private List<MessagesDTO> messages;

    @JsonProperty("model")
    private String model;

    @JsonProperty("max_tokens")
    private Integer max_tokens;

    @JsonProperty("presence_penalty ")
    private Double presence_penalty;

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class MessagesDTO {

        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private List<ContentDTO> content;

        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class ContentDTO {
            @JsonProperty("type")
            private String type;

            @JsonProperty("text")
            private String text;

            @JsonProperty("image_url")
            private ImageUrlDTO image_url;

            @NoArgsConstructor
            @Data
            @AllArgsConstructor
            @Builder
            public static class ImageUrlDTO {
                @JsonProperty("url")
                private String url;
            }
        }
    }
}
