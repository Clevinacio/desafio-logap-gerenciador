package com.logap.teste.gerenciadorbackend.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class RequestLogginFilter extends OncePerRequestFilter {
    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String USER_KEY = "user";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Prepara o contexto de log (MDC)
        final String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID_KEY, correlationId);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            MDC.put(USER_KEY, authentication.getName());
        }

        long startTime = System.currentTimeMillis();

        // Log da requisição recebida
        log.info("Requisição recebida: {} {}", request.getMethod(), request.getRequestURI());

        try {
            // 2. Continua o fluxo normal da requisição
            filterChain.doFilter(request, response);
        } finally {
            // 3. Este bloco é executado DEPOIS que a requisição foi processada
            long duration = System.currentTimeMillis() - startTime;

            // Log da resposta enviada
            log.info("Requisição finalizada: {} {} - Status {} em {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

            // 4. Limpa o contexto para a próxima requisição
            MDC.clear();
        }
    }
}
