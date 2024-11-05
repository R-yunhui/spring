package com.ral.young.ftp.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author renyunhui
 * @description 调用大模型接口返回的数据结构
 * @date 2024-11-01 14-39-16
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BigModelAnalysisVO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String object;

    @JsonProperty("created")
    private Integer created;

    @JsonProperty("model")
    private String model;

    @JsonProperty("choices")
    private List<ChoicesDTO> choices;

    @JsonProperty("usage")
    private UsageDTO usage;

    @NoArgsConstructor
    @Data
    public static class UsageDTO {

        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        @JsonProperty("total_tokens")
        private Integer totalTokens;

        @JsonProperty("completion_tokens")
        private Integer completionTokens;
    }

    @NoArgsConstructor
    @Data
    public static class ChoicesDTO {

        @JsonProperty("index")
        private Integer index;

        @JsonProperty("message")
        private MessageDTO message;

        @JsonProperty("logprobs")
        private Object logprobs;

        @JsonProperty("finish_reason")
        private String finishReason;

        @NoArgsConstructor
        @Data
        public static class MessageDTO {

            @JsonProperty("role")
            private String role;

            @JsonProperty("content")
            private String content;

            @JsonProperty("tool_calls")
            private Object toolCalls;
        }
    }
}
