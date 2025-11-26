package com.kedu.project.chatbot;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client geminiClient;
    private final String model = "gemini-2.5-flash";

    // 유저별 대화 히스토리 저장소
    private final Map<String, List<String>> conversationMap = new ConcurrentHashMap<>();

    public GeminiService(@Value("${gemini.api.key}") String apiKey) {
        this.geminiClient = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String generateText(String prompt, String userId) {

        // 유저 대화 히스토리 가져오거나 생성
        conversationMap.putIfAbsent(userId, new ArrayList<>());
        List<String> history = conversationMap.get(userId);
        // 시스템 규칙: AI 응답 가이드라인
        String baseCondition = """
                다음 질문에 답해주세요.

                [응답 규칙]
                1. 아기 또는 산모의 건강과 관련된 질문일 경우:
                   - 의학적 진단이나 확정적 표현(예: "~입니다", "확실합니다")은 피하고,
                     일반적인 정보와 참고 가능한 조언만 제공해주세요.
                   - 증상이 심각해 보이거나 일반적이지 않거나 응급 상황이 의심되는 경우,
                     "정확한 판단을 위해 의료 전문가와 상담하시길 권장드립니다."라고 안내해주세요.
                   - 질문이 모호하거나 대상(아기/산모)이 불명확하면,
                     "아기 증상인지, 산모 증상인지 알려주시면 더 정확히 도와드릴 수 있습니다."라고 물어봐주세요.

                2. 아기나 산모의 건강과 직접적으로 관련 없는 질문일 경우:
                   "해당 질문은 아기 또는 산모의 증상과 직접적으로 관련되지 않아
                    현재 제공되는 상담 범위에서는 도움드리기 어렵습니다."라고 부드럽게 안내해주세요.

                3. 정보 제공 시 과도한 불안감을 주는 표현은 피하고,
                   부모 입장에서 이해하기 쉬운 친절한 표현으로 설명해주세요.
                """;

        // 히스토리까지 포함된 실제 프롬프트 만들기
        StringBuilder fullPrompt = new StringBuilder();
        fullPrompt.append(baseCondition).append("\n\n");

        // 지난 대화 넣기
        for (int i = 0; i < history.size(); i++) {
            fullPrompt.append("이전 질문 ").append(i + 1).append(": ").append(history.get(i)).append("\n");
        }

        // 새 질문 넣기
        fullPrompt.append("사용자 질문: ").append(prompt);

        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.7f)
                .maxOutputTokens(2048)
                .build();

        Content content = Content.builder()
                .role("user")
                .parts(List.of(
                        Part.builder().text(fullPrompt.toString()).build()))
                .build();

        GenerateContentResponse resp = geminiClient.models.generateContent(
                model,
                content,
                config);

        String answer = resp.text();

        // 히스토리 업데이트
        history.add(prompt);
        // 필요하면 길조정 (10개 이상이면 오래된 것 삭제)
        if (history.size() > 10) {
            history.remove(0);
        }

        return answer;
    }
}
