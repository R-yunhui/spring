package com.ral.young.metrics.model;

import lombok.Data;
import java.util.List;

@Data
public class AlpacaFormat {
    private String system;
    private String instruction;
    private String input;
    private String output;
    private List<List<String>> history;
} 