package com.ral.young.metrics.model;

import lombok.Data;

import java.util.List;

@Data
public class LlavaFormat {
    private String system;
    private List<Conversation> conversations;

    @Data
    public static class Conversation {
        private String from;
        private String value;
    }
} 