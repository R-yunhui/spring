package com.ral.young.enums;

import java.text.DecimalFormat;

/**
 * @author renyunhui
 * @description 这是一个TimeEnum类
 * @date 2024-08-28 10-29-26
 * @since 1.0.0
 */
public enum TimeEnum {

    /**
     * 近5分钟
     */
    NEARLY_FIVE_MINUTES("近5分钟") {
        @Override
        public String getNearlyTime() {
            long timeMillis = System.currentTimeMillis();
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            timeMillis = timeMillis - 1000 * 60 * 5;
            double timeStampWithDecimal = timeMillis / 1000.0;
            return decimalFormat.format(timeStampWithDecimal);
        }
    },
    /**
     * 近15分钟
     */
    NEARLY_FIFTEEN_MINUTES("近15分钟") {
        @Override
        public String getNearlyTime() {
            long timeMillis = System.currentTimeMillis();
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            timeMillis = timeMillis - 1000 * 60 * 15;
            double timeStampWithDecimal = timeMillis / 1000.0;
            return decimalFormat.format(timeStampWithDecimal);
        }
    },
    /**
     * 近30分钟
     */
    NEARLY_THIRTY_MINUTES("近30分钟") {
        @Override
        public String getNearlyTime() {
            long timeMillis = System.currentTimeMillis();
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            timeMillis = timeMillis - 1000 * 60 * 30;
            double timeStampWithDecimal = timeMillis / 1000.0;
            return decimalFormat.format(timeStampWithDecimal);
        }
    },
    /**
     * 近1小时
     */
    NEARLY_ONE_HOUR("近1小时") {
        @Override
        public String getNearlyTime() {
            long timeMillis = System.currentTimeMillis();
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            timeMillis = timeMillis - 1000 * 60 * 60;
            double timeStampWithDecimal = timeMillis / 1000.0;
            return decimalFormat.format(timeStampWithDecimal);
        }
    },
    /**
     * 近3小时
     */
    NEARLY_THREE_HOUR("近3小时") {
        @Override
        public String getNearlyTime() {
            long timeMillis = System.currentTimeMillis();
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            timeMillis = timeMillis - 1000 * 60 * 60 * 3;
            double timeStampWithDecimal = timeMillis / 1000.0;
            return decimalFormat.format(timeStampWithDecimal);
        }
    },
    /**
     * 近6小时
     */
    NEARLY_SIX_HOUR("近6小时") {
        @Override
        public String getNearlyTime() {
            long timeMillis = System.currentTimeMillis();
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            timeMillis = timeMillis - 1000 * 60 * 60 * 6;
            double timeStampWithDecimal = timeMillis / 1000.0;
            return decimalFormat.format(timeStampWithDecimal);
        }
    },
    /**
     * 近12小时
     */
    NEARLY_TWELVE_HOUR("近12小时") {
        @Override
        public String getNearlyTime() {
            long timeMillis = System.currentTimeMillis();
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            timeMillis = timeMillis - 1000 * 60 * 60 * 12;
            double timeStampWithDecimal = timeMillis / 1000.0;
            return decimalFormat.format(timeStampWithDecimal);
        }
    },
    /**
     * 近24小时
     */
    NEARLY_TWENTY_FOUR_HOUR("近24小时") {
        @Override
        public String getNearlyTime() {
            long timeMillis = System.currentTimeMillis();
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            timeMillis = timeMillis - 1000 * 60 * 60 * 24;
            double timeStampWithDecimal = timeMillis / 1000.0;
            return decimalFormat.format(timeStampWithDecimal);
        }
    },
    /**
     * 近2天
     */
    NEARLY_TWO_DAYS("近2天") {
        @Override
        public String getNearlyTime() {
            long timeMillis = System.currentTimeMillis();
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            timeMillis = timeMillis - 1000 * 60 * 60 * 48;
            double timeStampWithDecimal = timeMillis / 1000.0;
            return decimalFormat.format(timeStampWithDecimal);
        }
    };

    private String desc;

    TimeEnum(String desc) {
        this.desc = desc;
    }

    public TimeEnum getEnumsByName(String enumName) {
        return TimeEnum.valueOf(enumName);
    }

    public abstract String getNearlyTime();
}
