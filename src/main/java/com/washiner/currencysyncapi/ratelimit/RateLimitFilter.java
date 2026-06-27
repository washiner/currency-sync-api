package com.washiner.currencysyncapi.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

// OncePerRequestFilter: classe-base do Spring que garante que esse
// filtro execute apenas UMA VEZ por requisição (evita duplicação em
// certos cenários internos do servlet).
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Limite máximo de requisições permitidas POR JANELA (por minuto).
    private static final int MAX_REQUESTS_PER_WINDOW = 10;

    // Duração da janela em milissegundos (1 minuto = 60.000 ms).
    private static final long WINDOW_DURATION_MILLIS = 60_000;

    // ConcurrentHashMap: a "gaveta de fichas" -- guarda um RequestCounter
    // para CADA IP que já fez alguma requisição. É "Concurrent" porque
    // múltiplas threads podem ler/escrever nesse mapa ao mesmo tempo,
    // sem corromper os dados (diferente de um HashMap comum).
    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        // computeIfAbsent: se o IP ainda não tem ficha, cria uma nova.
        // Se já tem, retorna a ficha existente. Tudo isso de forma
        // atômica/segura, sem risco de duas threads criarem duas fichas
        // para o mesmo IP ao mesmo tempo.
        RequestCounter counter = requestCounts.computeIfAbsent(clientIp,
                ip -> new RequestCounter(now));

        synchronized (counter) {
            boolean windowExpired = (now - counter.getWindowStartMillis()) > WINDOW_DURATION_MILLIS;

            if (windowExpired) {
                // A janela anterior já passou (mudou o "minuto") -- zera e
                // começa uma nova contagem.
                counter.resetWindow(now);
            } else {
                // Ainda estamos na mesma janela -- soma mais uma requisição.
                int currentCount = counter.incrementAndGet();

                if (currentCount > MAX_REQUESTS_PER_WINDOW) {
                    // Passou do limite -- bloqueia com 429 e NÃO deixa a
                    // requisição seguir para o Controller.
                    response.setStatus(429); // 429 = Too Many Requests
                    response.getWriter().write("Limite de requisições excedido. Tente novamente em breve.");
                    return; // <-- importante: encerra aqui, sem chamar filterChain.doFilter()
                }
            }
        }

        // Se passou pelas checagens acima, deixa a requisição seguir
        // normalmente para o próximo filtro / Controller.
        filterChain.doFilter(request, response);
    }
}
