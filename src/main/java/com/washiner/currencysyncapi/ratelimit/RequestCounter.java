package com.washiner.currencysyncapi.ratelimit;

import java.util.concurrent.atomic.AtomicInteger;

// Essa classe representa a "ficha" de um único IP: quantas requisições
// ele já fez, e em qual "janela" de tempo (minuto) essa contagem começou.
public class RequestCounter {

    // AtomicInteger: uma versão "thread-safe" do int comum.
    // Como várias requisições podem chegar AO MESMO TEMPO (múltiplas
    // threads), precisamos garantir que o contador seja incrementado
    // de forma segura, sem duas threads "pisarem" uma na outra.
    private final AtomicInteger count;

    // Guarda o instante (em milissegundos) em que essa janela começou.
    private volatile long windowStartMillis;

    public RequestCounter(long windowStartMillis) {
        this.count = new AtomicInteger(1);
        this.windowStartMillis = windowStartMillis;
    }

    public int incrementAndGet() {
        return count.incrementAndGet();
    }

    public long getWindowStartMillis() {
        return windowStartMillis;
    }

    public void resetWindow(long newWindowStartMillis) {
        this.count.set(1);
        this.windowStartMillis = newWindowStartMillis;
    }
}