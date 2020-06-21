package com.ksj.eoisa.config;

import java.util.List;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class XSSConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(htmlEscapingConveter());
    }

    public MappingJackson2HttpMessageConverter htmlEscapingConveter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());

        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    public static class HTMLCharacterEscapes extends CharacterEscapes {

        @Override
        public int[] getEscapeCodesForAscii() {
            int[] asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
            asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
            asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
            asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
            asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
            asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
            asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
            asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;

            return asciiEscapes;
        }

        @Override
        public SerializableString getEscapeSequence(int ch) {
            return new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString((char) ch)));
        }

    }

}