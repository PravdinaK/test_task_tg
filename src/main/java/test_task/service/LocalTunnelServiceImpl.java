package test_task.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class LocalTunnelServiceImpl implements LocalTunnelService {

    @Value("${localtunnel.subdomain}")
    private String subdomain;

    @Value("${localtunnel.port}")
    private int port;

    private Process localTunnelProcess;
    private final ExecutorService outputLogger = Executors.newSingleThreadExecutor();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    @Override
    public synchronized String getPublicUrl() {
        if (!running.get() || localTunnelProcess == null || !localTunnelProcess.isAlive()) {
            startLocalTunnel();
            waitForLocalTunnel();
        }
        return buildPublicUrl();
    }

    private void startLocalTunnel() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            List<String> command = new ArrayList<>();
            if (os.contains("win")) {
                command.add("cmd");
                command.add("/c");
            }
            command.add("lt");
            command.add("--port");
            command.add(String.valueOf(port));

            if (subdomain != null && !subdomain.isBlank()) {
                command.add("--subdomain");
                command.add(subdomain);
            }

            log.info("Запуск localtunnel с командой: {}", String.join(" ", command));
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            processBuilder.redirectErrorStream(true);
            Map<String, String> env = processBuilder.environment();
            if (env.get("PATH") == null) {
                env.put("PATH", System.getenv("PATH"));
            }
            log.info("Process environment PATH: {}", env.get("PATH"));

            localTunnelProcess = processBuilder.start();
            running.set(true);

            outputLogger.submit(() -> {
                String charsetName = System.getProperty("file.encoding", "UTF-8");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(localTunnelProcess.getInputStream(), charsetName))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String cleanedLine = line.replaceAll("[^\\p{Print}\\p{Space}]", "").trim();
                        if (!cleanedLine.isEmpty()) {
                            log.info("[LocalTunnel] {}", cleanedLine);
                        }
                    }
                } catch (IOException e) {
                    log.error("Ошибка чтения вывода LocalTunnel: {}", e.getMessage());
                }
            });

            log.info("LocalTunnel запущен с поддоменом '{}' на порту {}", subdomain, port);

        } catch (IOException e) {
            running.set(false);
            log.error("Не удалось запустить LocalTunnel: {}", e.getMessage());
            throw new IllegalStateException("Ошибка запуска localtunnel", e);
        }
    }

    private void waitForLocalTunnel() {
        log.info("Ожидание доступности LocalTunnel...");

        URI uri = URI.create(buildPublicUrl());
        int attempts = 3;
        boolean success = false;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        for (int i = 0; i < attempts; i++) {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int status = response.statusCode();

                if (status >= 200 && status < 400) {
                    log.info("LocalTunnel доступен по адресу: {}", buildPublicUrl());
                    success = true;
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Ожидание LocalTunnel прервано");
                break;
            } catch (Exception ignored) {}

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Ожидание LocalTunnel прервано");
                break;
            }
        }

        if (!success) {
            log.warn("LocalTunnel не стал доступен после {} попыток. Для дальнейшей работы требуется пройти авторизацию пользователя на публичном URL туннеля.", attempts);
            running.set(false);
        }
    }

    private String buildPublicUrl() {
        return "https://" + subdomain + ".loca.lt";
    }

    @PreDestroy
    public synchronized void stopLocalTunnel() {
        running.set(false);
        if (localTunnelProcess != null && localTunnelProcess.isAlive()) {
            log.info("Завершаем процесс LocalTunnel...");
            localTunnelProcess.destroy();
            try {
                if (!localTunnelProcess.waitFor(5, TimeUnit.SECONDS)) {
                    log.warn("LocalTunnel не завершился вовремя, принудительное уничтожение...");
                    localTunnelProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        outputLogger.shutdown();
        try {
            if (!outputLogger.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Executor для логирования LocalTunnel не завершился вовремя.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}