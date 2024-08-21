package com.ral.young.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author renyunhui
 * @description 通过 Prometheus 查询 metrics 的结果
 * @date 2024-08-20 09-40-39
 * @since 1.2.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryRangeMetricsResult {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {

        @JsonProperty("resultType")
        private String resultType;

        @JsonProperty("result")
        private List<ResultDTO> result;

        @NoArgsConstructor
        @Data
        public static class ResultDTO {

            @JsonProperty("metric")
            private Map<String, String> metric;

            @JsonProperty("values")
            private List<List<Double>> values;
        }
    }
}
