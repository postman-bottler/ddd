package online.bottler.recommendation.application.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.recommendation.application.facade.RecommendFacade;
import online.bottler.user.application.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationScheduler {

    private final UserService userService;
    private final RecommendFacade recommendFacade;

    @Value("${scheduler.batch-size}")
    private int batchSize;

    @Value("${scheduler.parallelism}")
    private int parallelism;

    @Scheduled(cron = "0 0 23,11,17 * * *")
    public void generateAllUserRecommendationsAsync() {
        ExecutorService executorService = Executors.newFixedThreadPool(parallelism);
        try {
            List<List<Long>> batches = createBatches(userService.getAllUserIds());
            for (List<Long> batch : batches) {
                log.info("사용자 배치 처리 시작 (크기: {}): {}", batch.size(), batch);

                List<CompletableFuture<String>> futures = batch.stream().map(userId -> CompletableFuture.supplyAsync(
                                () -> recommendFacade.generateRecommendation(userId), executorService))
                        .toList();

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .thenRun(() -> futures.forEach(this::handleFutureResult))
                        .handle((result, ex) -> {
                            if (ex != null) {
                                log.error("배치 처리 중 예외 발생: {}", ex.getMessage(), ex);
                            } else {
                                log.info("배치 처리 성공");
                            }
                            return null;
                        }).join();
            }
        } finally {
            shutdownExecutorService(executorService);
        }
    }

    private void shutdownExecutorService(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("ExecutorService가 30초 내에 종료되지 않아 강제 종료합니다.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("ExecutorService 종료 중 인터럽트 발생: {}", ie.getMessage(), ie);
        }
    }

    @Scheduled(cron = "0 0 0,12,18 * * ?")
    public void updateAllRecommendationsAndNotify() {
        for (List<Long> batch : createBatches(userService.getAllUserIds())) {
            batch.forEach(recommendFacade::updateRecommendationsFromTemp);
        }
    }

    private List<List<Long>> createBatches(List<Long> items) {
        List<List<Long>> batches = new ArrayList<>();
        for (int i = 0; i < items.size(); i += batchSize) {
            batches.add(new ArrayList<>(items.subList(i, Math.min(items.size(), i + batchSize))));
        }
        return batches;
    }

    private void handleFutureResult(CompletableFuture<String> future) {
        try {
            log.info("작업 결과: {}", future.get());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("작업 결과를 가져오는 중 인터럽트 발생: {}", ie.getMessage(), ie);
        } catch (ExecutionException ee) {
            log.error("작업 결과를 가져오는 중 실행 예외 발생: {}", ee.getMessage(), ee);
        } catch (Exception ex) {
            log.error("작업 결과를 가져오는 중 알 수 없는 예외 발생: {}", ex.getMessage(), ex);
        }
    }
}
