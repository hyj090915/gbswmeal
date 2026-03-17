package com.gbsw.meal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbsw.meal.entity.Meal;
import com.gbsw.meal.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NeisApiService {

    private final MealRepository mealRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${neis.api.base-url}")
    private String baseUrl;

    @Value("${neis.api.atpt-code}")
    private String atptCode;

    @Value("${neis.api.school-code}")
    private String schoolCode;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 단일 날짜 조회
    public void fetchAndSaveMeal(LocalDate date) {
        String dateStr = date.format(DATE_FMT);
        String url = baseUrl + "/mealServiceDietInfo"
                + "?Type=json"
                + "&ATPT_OFCDC_SC_CODE=" + atptCode
                + "&SD_SCHUL_CODE=" + schoolCode
                + "&MLSV_YMD=" + dateStr
                + "&pSize=100";
        fetchUrl(url);
    }

    // 날짜 범위 조회 (월별 한 번에)
    public void fetchAndSaveMealRange(LocalDate from, LocalDate to) {
        String url = baseUrl + "/mealServiceDietInfo"
                + "?Type=json"
                + "&ATPT_OFCDC_SC_CODE=" + atptCode
                + "&SD_SCHUL_CODE=" + schoolCode
                + "&MLSV_FROM_YMD=" + from.format(DATE_FMT)
                + "&MLSV_TO_YMD=" + to.format(DATE_FMT)
                + "&pSize=200";
        fetchUrl(url);
    }

    private void fetchUrl(String url) {
        try {
            String response = restTemplate.getForObject(url, String.class);
            parseAndSave(response);
        } catch (Exception e) {
            log.error("나이스 API 호출 실패: {}", url, e);
        }
    }

    private void parseAndSave(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);

            if (!root.has("mealServiceDietInfo")) {
                log.info("급식 데이터 없음 (응답에 mealServiceDietInfo 없음)");
                return;
            }

            JsonNode info = root.get("mealServiceDietInfo");
            int totalCount = info.get(0).get("head").get(0).get("list_total_count").asInt();
            if (totalCount == 0) return;

            JsonNode rows = info.get(1).get("row");
            for (JsonNode row : rows) {
                String mealDateStr = row.get("MLSV_YMD").asText(); // yyyyMMdd
                LocalDate mealDate = LocalDate.parse(mealDateStr, DATE_FMT);
                String mealType = row.get("MMEAL_SC_NM").asText();
                String dishNames = row.get("DDISH_NM").asText()
                        .replace("<br/>", "\n")
                        .replace("<br>", "\n");
                String calInfo = row.has("CAL_INFO") ? row.get("CAL_INFO").asText() : "";
                String ntrInfo = row.has("NTR_INFO") ? row.get("NTR_INFO").asText() : "";

                mealRepository.findByMealDateAndMealType(mealDate, mealType)
                        .ifPresentOrElse(
                                existing -> {
                                    existing.setDishNames(dishNames);
                                    existing.setCalInfo(calInfo);
                                    existing.setNtrInfo(ntrInfo);
                                    mealRepository.save(existing);
                                },
                                () -> mealRepository.save(Meal.builder()
                                        .mealDate(mealDate)
                                        .mealType(mealType)
                                        .dishNames(dishNames)
                                        .calInfo(calInfo)
                                        .ntrInfo(ntrInfo)
                                        .build())
                        );
            }
            log.info("급식 데이터 저장 완료: {}건", totalCount);
        } catch (Exception e) {
            log.error("급식 데이터 파싱 실패", e);
        }
    }
}
