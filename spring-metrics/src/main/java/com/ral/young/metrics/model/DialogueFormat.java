package com.ral.young.metrics.model;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public enum DialogueFormat {
    
    LLAVA {
        @Override
        public JSONArray parse(String input) {
            if (isFilePath(input)) {
                return parseFile(input);
            }
            return parseNormal(input);
        }

        @Override
        protected JSONArray parseNormal(String jsonContent) {
            try {
                JSONArray array = JSONUtil.parseArray(jsonContent);
                return formatLlavaContent(array);
            } catch (Exception e) {
                log.error("Llava格式解析失败", e);
                throw new RuntimeException("Llava格式解析失败", e);
            }
        }

        @Override
        public JSONArray parseStream(InputStream inputStream) {
            JSONArray resultArray = new JSONArray();
            AtomicInteger conversationCount = new AtomicInteger(0);
            Snowflake snowflake = IdUtil.getSnowflake();
            
            try (JsonParser parser = new JsonFactory().createParser(inputStream)) {
                validateArrayStart(parser);
                
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    JSONObject dialogueGroup = new JSONObject();
                    JSONArray dialogues = new JSONArray();
                    String system = "";
                    
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        String fieldName = parser.getCurrentName();
                        if ("system".equals(fieldName)) {
                            parser.nextToken();
                            system = parser.getText();
                        } else if ("conversations".equals(fieldName)) {
                            parser.nextToken(); // START_ARRAY
                            parseLlavaConversations(parser, dialogues, snowflake, conversationCount);
                        }
                    }
                    
                    dialogueGroup.set("System", system);
                    dialogueGroup.set("Conversations", dialogues);
                    resultArray.add(dialogueGroup);
                }
                
                log.info("Llava格式解析完成，共处理 {} 个对话", conversationCount.get());
                return resultArray;
            } catch (Exception e) {
                log.error("Llava流式解析失败", e);
                throw new RuntimeException("Llava流式解析失败", e);
            }
        }

        private void parseLlavaConversations(JsonParser parser, JSONArray dialogues, 
                                           Snowflake snowflake, AtomicInteger count) throws IOException {
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                JSONObject dialogue = new JSONObject();
                dialogue.set("Id", snowflake.nextId());
                
                String prompt = "";
                String response = "";
                
                // 解析human消息
                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    if ("value".equals(parser.getCurrentName())) {
                        parser.nextToken();
                        prompt = parser.getText();
                    }
                }
                
                // 解析assistant消息
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        if ("value".equals(parser.getCurrentName())) {
                            parser.nextToken();
                            response = parser.getText();
                        }
                    }
                }
                
                dialogue.set("Prompt", prompt);
                dialogue.set("Response", response);
                dialogues.add(dialogue);
                count.incrementAndGet();
            }
        }

        private JSONArray formatLlavaContent(JSONArray input) {
            JSONArray resultArray = new JSONArray();
            Snowflake snowflake = IdUtil.getSnowflake();
            
            for (int i = 0; i < input.size(); i++) {
                JSONObject item = input.getJSONObject(i);
                JSONObject dialogueGroup = new JSONObject();
                JSONArray dialogues = new JSONArray();
                
                // 处理system
                dialogueGroup.set("System", item.getStr("system", ""));
                
                // 处理conversations
                JSONArray conversations = item.getJSONArray("conversations");
                if (conversations != null) {
                    for (int j = 0; j < conversations.size(); j += 2) {
                        JSONObject dialogue = new JSONObject();
                        dialogue.set("Id", snowflake.nextId());
                        
                        JSONObject human = conversations.getJSONObject(j);
                        dialogue.set("Prompt", human.getStr("value", ""));
                        
                        if (j + 1 < conversations.size()) {
                            JSONObject assistant = conversations.getJSONObject(j + 1);
                            dialogue.set("Response", assistant.getStr("value", ""));
                        } else {
                            dialogue.set("Response", "");
                        }
                        
                        dialogues.add(dialogue);
                    }
                }
                
                dialogueGroup.set("Conversations", dialogues);
                resultArray.add(dialogueGroup);
            }
            
            return resultArray;
        }
    },
    
    ALPACA {
        @Override
        public JSONArray parse(String input) {
            if (isFilePath(input)) {
                return parseFile(input);
            }
            return parseNormal(input);
        }

        @Override
        protected JSONArray parseNormal(String jsonContent) {
            try {
                JSONArray array = JSONUtil.parseArray(jsonContent);
                return formatAlpacaContent(array);
            } catch (Exception e) {
                log.error("Alpaca格式解析失败", e);
                throw new RuntimeException("Alpaca格式解析失败", e);
            }
        }

        @Override
        public JSONArray parseStream(InputStream inputStream) {
            JSONArray resultArray = new JSONArray();
            AtomicInteger conversationCount = new AtomicInteger(0);
            Snowflake snowflake = IdUtil.getSnowflake();
            
            try (JsonParser parser = new JsonFactory().createParser(inputStream)) {
                validateArrayStart(parser);
                
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    JSONObject dialogueGroup = new JSONObject();
                    JSONArray dialogues = new JSONArray();
                    String system = "";
                    String instruction = "";
                    String output = "";
                    
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        String fieldName = parser.getCurrentName();
                        switch (fieldName) {
                            case "system":
                                parser.nextToken();
                                system = parser.getText();
                                break;
                            case "instruction":
                                parser.nextToken();
                                instruction = parser.getText();
                                break;
                            case "output":
                                parser.nextToken();
                                output = parser.getText();
                                break;
                            case "history":
                                parser.nextToken(); // START_ARRAY
                                parseAlpacaHistory(parser, dialogues, snowflake, conversationCount);
                                break;
                        }
                    }
                    
                    // 添加主对话
                    if (!instruction.isEmpty()) {
                        JSONObject mainDialogue = new JSONObject();
                        mainDialogue.set("Id", snowflake.nextId());
                        mainDialogue.set("Prompt", instruction);
                        mainDialogue.set("Response", output);
                        dialogues.add(0, mainDialogue);
                        conversationCount.incrementAndGet();
                    }
                    
                    dialogueGroup.set("System", system);
                    dialogueGroup.set("Conversations", dialogues);
                    resultArray.add(dialogueGroup);
                }
                
                log.info("Alpaca格式解析完成，共处理 {} 个对话", conversationCount.get());
                return resultArray;
            } catch (Exception e) {
                log.error("Alpaca流式解析失败", e);
                throw new RuntimeException("Alpaca流式解析失败", e);
            }
        }

        private void parseAlpacaHistory(JsonParser parser, JSONArray dialogues, 
                                      Snowflake snowflake, AtomicInteger count) throws IOException {
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                JSONObject dialogue = new JSONObject();
                dialogue.set("Id", snowflake.nextId());
                
                parser.nextToken(); // START_ARRAY
                parser.nextToken();
                dialogue.set("Prompt", parser.getText());
                parser.nextToken();
                dialogue.set("Response", parser.getText());
                parser.nextToken(); // END_ARRAY
                
                dialogues.add(dialogue);
                count.incrementAndGet();
            }
        }

        private JSONArray formatAlpacaContent(JSONArray input) {
            JSONArray resultArray = new JSONArray();
            Snowflake snowflake = IdUtil.getSnowflake();
            
            for (int i = 0; i < input.size(); i++) {
                JSONObject item = input.getJSONObject(i);
                JSONObject dialogueGroup = new JSONObject();
                JSONArray dialogues = new JSONArray();
                
                // 处理system
                dialogueGroup.set("System", item.getStr("system", ""));
                
                // 处理主对话
                String instruction = item.getStr("instruction", "");
                String output = item.getStr("output", "");
                if (!instruction.isEmpty()) {
                    JSONObject mainDialogue = new JSONObject();
                    mainDialogue.set("Id", snowflake.nextId());
                    mainDialogue.set("Prompt", instruction);
                    mainDialogue.set("Response", output);
                    dialogues.add(mainDialogue);
                }
                
                // 处理历史对话
                JSONArray history = item.getJSONArray("history");
                if (history != null) {
                    for (int j = 0; j < history.size(); j++) {
                        JSONArray historyItem = history.getJSONArray(j);
                        JSONObject dialogue = new JSONObject();
                        dialogue.set("Id", snowflake.nextId());
                        dialogue.set("Prompt", historyItem.get(0));
                        dialogue.set("Response", historyItem.get(1));
                        dialogues.add(dialogue);
                    }
                }
                
                dialogueGroup.set("Conversations", dialogues);
                resultArray.add(dialogueGroup);
            }
            
            return resultArray;
        }
    };

    private static final long SIZE_THRESHOLD = 5 * 1024 * 1024; // 5MB阈值

    // 抽象方法定义
    public abstract JSONArray parse(String input);

    protected abstract JSONArray parseNormal(String jsonContent);

    public abstract JSONArray parseStream(InputStream inputStream);

    // 通用工具方法
    protected boolean isFilePath(String input) {
        try {
            Path path = Paths.get(input);
            return Files.exists(path);
        } catch (Exception e) {
            return false;
        }
    }

    protected JSONArray parseFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.size(path) > SIZE_THRESHOLD) {
                try (InputStream inputStream = Files.newInputStream(path)) {
                    return parseStream(inputStream);
                }
            } else {
                String content = FileUtil.readString(path.toFile(), StandardCharsets.UTF_8);
                return parseNormal(content);
            }
        } catch (IOException e) {
            log.error("文件解析失败: {}", filePath, e);
            throw new RuntimeException("文件解析失败", e);
        }
    }

    protected void validateArrayStart(JsonParser parser) throws IOException {
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new IllegalStateException("Expected content to be an array");
        }
    }

    public static DialogueFormat getFormatByName(String name) {
        try {
            return DialogueFormat.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("不支持的格式类型: {}", name);
            throw new IllegalArgumentException("不支持的格式类型: " + name);
        }
    }
}