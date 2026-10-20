
package com.cards.transaction.logging;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class ApiLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(ApiLoggingFilter.class);

    private final JsonMapper objectMapper;

    public ApiLoggingFilter(JsonMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.nanoTime();
        String requestId = UUID.randomUUID().toString();

        ContentCachingRequestWrapper requestWrapper =
                new ContentCachingRequestWrapper(request, 8192);

        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(response);

        String method = request.getMethod();
        String path = request.getRequestURI();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            try {
                long durationMs =
                    (System.nanoTime() - startTime) / 1_000_000;

                logger.info(
                    "REQUEST id={} method={} path={} body={}",
                    requestId,
                    method,
                    path,
                    summarizeBody(
                        requestWrapper.getContentAsByteArray(),
                        request.getContentType(),
                        true
                    )
                );

                logger.info(
                    "RESPONSE id={} method={} path={} status={} durationMs={} body={}",
                    requestId,
                    method,
                    path,
                    responseWrapper.getStatus(),
                    durationMs,
                    summarizeBody(
                        responseWrapper.getContentAsByteArray(),
                        responseWrapper.getContentType(),
                        false
                    )
                );
            } finally {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    private String summarizeBody(
            byte[] body,
            String contentType,
            boolean isRequest) {

        if (body.length == 0) {
            return "[empty]";
        }

        if (contentType == null ||
            !contentType.toLowerCase(java.util.Locale.ROOT)
                .contains("application/json")) {
            return "[non-JSON body omitted]";
        }

        try {
            JsonNode json = objectMapper.readTree(
                new String(body, StandardCharsets.UTF_8)
            );

            // Deliberately log only known, approved fields.
            StringBuilder summary = new StringBuilder("{");

            if (isRequest) {
                appendField(summary, json, "merchantName");
                appendField(summary, json, "amount");
                appendField(summary, json, "currency");
                appendField(summary, json, "status");
            } else {
                appendField(summary, json, "id");
                appendField(summary, json, "referenceNumber");
                appendField(summary, json, "status");

                if (json.has("page")) {
                    appendField(summary, json, "page");
                    appendField(summary, json, "size");
                    appendField(summary, json, "totalElements");
                }

                if (json.has("error")) {
                    appendField(summary, json, "error");
                }
            }

            return summary.append("}").toString();

        } catch (Exception exception) {
            return "[JSON summary unavailable]";
        }
    }

    private void appendField(
            StringBuilder summary,
            JsonNode json,
            String fieldName) {

        if (!json.isObject() || !json.hasNonNull(fieldName)) {
            return;
        }

        JsonNode value = json.get(fieldName);

        // Only log scalar values, never nested objects or arrays.
        if (!value.isValueNode()) {
            return;
        }

        if (summary.length() > 1) {
            summary.append(", ");
        }

        String text = value.asText()
                .replaceAll("[\\r\\n\\t]", " ")
                .replaceAll("[^\\p{Print}]", "?");

        if (text.length() > 120) {
            text = text.substring(0, 120) + "...";
        }

        summary.append(fieldName)
               .append("=")
               .append(text);
    }
}
