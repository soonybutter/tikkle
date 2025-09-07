package com.secure_tikkle.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.secure_tikkle.domain.Badge;
import com.secure_tikkle.domain.BadgeConditionType;
import com.secure_tikkle.repository.BadgeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class BadgeSeeder implements CommandLineRunner {

	private final BadgeRepository badgeRepository;

    @Override public void run(String... args) { seed(); }

    @Transactional
    public void seed() {
        // 0) 요청대로 커피 관련 배지 제거
        deleteIfExists("COFFEE_10");
        deleteIfExists("COFFEE_SKIP");
        deleteIfExists("COFFEE_SKIP_10");

        // 1) 신규 배지(예: 34개) — 필요시 텍스트/이모지 마음껏 바꿔도 됨
        List<Badge> S = new ArrayList<>();

        // A) 저축 횟수
        S.add(b("SV_CNT_001","첫 저축","첫 입금 성공!","🌱", BadgeConditionType.SAVINGS_COUNT,1,""));
        S.add(b("SV_CNT_003","세 번의 시작","저축 3회 달성","🟢", BadgeConditionType.SAVINGS_COUNT,3,""));
        S.add(b("SV_CNT_005","꾸준함 시작","저축 5회 달성","🔵", BadgeConditionType.SAVINGS_COUNT,5,""));
        S.add(b("SV_CNT_010","두 자릿수 돌파","저축 10회 달성","💪", BadgeConditionType.SAVINGS_COUNT,10,""));
        S.add(b("SV_CNT_020","20번의 의지","저축 20회 달성","🏃", BadgeConditionType.SAVINGS_COUNT,20,""));
        S.add(b("SV_CNT_030","꾸준함 장착","저축 30회 달성","✨", BadgeConditionType.SAVINGS_COUNT,30,""));
        S.add(b("SV_CNT_050","반백 번 저축","저축 50회 달성","⚡", BadgeConditionType.SAVINGS_COUNT,50,""));
        S.add(b("SV_CNT_075","거의 백 번","저축 75회 달성","🔥", BadgeConditionType.SAVINGS_COUNT,75,""));
        S.add(b("SV_CNT_100","저축 마스터","저축 100회 달성","🏆", BadgeConditionType.SAVINGS_COUNT,100,""));

        // B) 저축 누적액(원)
        S.add(b("SV_SUM_10K","1만 원 모으기","누적 1만 원 달성","💰", BadgeConditionType.SAVINGS_SUM,10_000,""));
        S.add(b("SV_SUM_50K","5만 원 모으기","누적 5만 원 달성","💰", BadgeConditionType.SAVINGS_SUM,50_000,""));
        S.add(b("SV_SUM_100K","10만 원 모으기","누적 10만 원 달성","💰", BadgeConditionType.SAVINGS_SUM,100_000,""));
        S.add(b("SV_SUM_200K","20만 원 모으기","누적 20만 원 달성","💰", BadgeConditionType.SAVINGS_SUM,200_000,""));
        S.add(b("SV_SUM_300K","30만 원 모으기","누적 30만 원 달성","💰", BadgeConditionType.SAVINGS_SUM,300_000,""));
        S.add(b("SV_SUM_500K","50만 원 모으기","누적 50만 원 달성","💰", BadgeConditionType.SAVINGS_SUM,500_000,""));
        S.add(b("SV_SUM_1M","100만 원 모으기","누적 100만 원 달성","💎", BadgeConditionType.SAVINGS_SUM,1_000_000,""));
        S.add(b("SV_SUM_2M","200만 원 모으기","누적 200만 원 달성","💎", BadgeConditionType.SAVINGS_SUM,2_000_000,""));
        S.add(b("SV_SUM_5M","500만 원 모으기","누적 500만 원 달성","💎", BadgeConditionType.SAVINGS_SUM,5_000_000,""));
        S.add(b("SV_SUM_10M","1,000만 원 모으기","누적 1,000만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,10_000_000,""));
        S.add(b("SV_SUM_10M","1,500만 원 모으기","누적 1,500만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,15_000_000,""));
        S.add(b("SV_SUM_10M","2,000만 원 모으기","누적 2,000만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,20_000_000,""));
        S.add(b("SV_SUM_10M","2,500만 원 모으기","누적 2,500만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,25_000_000,""));
        S.add(b("SV_SUM_10M","3,000만 원 모으기","누적 3,000만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,30_000_000,""));
        S.add(b("SV_SUM_10M","3,500만 원 모으기","누적 3,500만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,35_000_000,""));
        S.add(b("SV_SUM_10M","4,000만 원 모으기","누적 4,000만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,40_000_000,""));
        S.add(b("SV_SUM_10M","4,500만 원 모으기","누적 4,500만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,45_000_000,""));
        S.add(b("SV_SUM_10M","5,000만 원 모으기","누적 5,000만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,50_000_000,""));
        S.add(b("SV_SUM_10M","5,500만 원 모으기","누적 5,500만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,55_000_000,""));
        S.add(b("SV_SUM_10M","6,000만 원 모으기","누적 6,000만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,60_000_000,""));
        S.add(b("SV_SUM_10M","6,500만 원 모으기","누적 6,500만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,65_000_000,""));
        S.add(b("SV_SUM_10M","7,000만 원 모으기","누적 7,000만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,70_000_000,""));
        S.add(b("SV_SUM_10M","7,500만 원 모으기","누적 7,500만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,75_000_000,""));
        S.add(b("SV_SUM_10M","8,000만 원 모으기","누적 8,000만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,80_000_000,""));
        S.add(b("SV_SUM_10M","8,500만 원 모으기","누적 8,500만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,85_000_000,""));
        S.add(b("SV_SUM_10M","9,000만 원 모으기","누적 9,000만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,90_000_000,""));
        S.add(b("SV_SUM_10M","9,500만 원 모으기","누적 9,500만 원 달성","👑", BadgeConditionType.SAVINGS_SUM,95_000_000,""));
        S.add(b("SV_SUM_10M","1억 모으기","누적 1억 달성🌟","👑", BadgeConditionType.SAVINGS_SUM,100_000_000,""));
        
        // C) 목표 달성 개수
        S.add(b("GOAL_001","첫 목표 달성","목표 1개 완료","🎯", BadgeConditionType.GOAL_COMPLETED,1,""));
        S.add(b("GOAL_003","세 개 달성","목표 3개 완료","🎯", BadgeConditionType.GOAL_COMPLETED,3,""));
        S.add(b("GOAL_005","다섯 개 달성","목표 5개 완료","🎯", BadgeConditionType.GOAL_COMPLETED,5,""));
        S.add(b("GOAL_007","일곱 개 달성","목표 7개 완료","🎯", BadgeConditionType.GOAL_COMPLETED,7,""));
        S.add(b("GOAL_010","열 개 달성","목표 10개 완료","🏁", BadgeConditionType.GOAL_COMPLETED,10,""));
        S.add(b("GOAL_010","열 개 달성","목표 20개 완료","🏁", BadgeConditionType.GOAL_COMPLETED,10,""));
        S.add(b("GOAL_010","열 개 달성","목표 30개 완료","🏁", BadgeConditionType.GOAL_COMPLETED,10,""));
        S.add(b("GOAL_010","열 개 달성","목표 40개 완료","🏁", BadgeConditionType.GOAL_COMPLETED,10,""));
        S.add(b("GOAL_010","열 개 달성","목표 50개 완료","🏁", BadgeConditionType.GOAL_COMPLETED,10,""));
        
        // D) 메모 키워드 절약
        S.add(b("MEMO_TAXI_003","택시 대신 대중교통","메모 '택시' 3회 절약","🚕", BadgeConditionType.MEMO_COUNT,3,"택시|콜택시|카카오T|카카오 택시|카카오택시|"));
        S.add(b("MEMO_DELIVERY_003","배달 참기","메모 '배달' 3회 절약","📦", BadgeConditionType.MEMO_COUNT,3,"배달|배민|쿠팡이츠|요기요|이츠|배달의민족"));
        S.add(b("MEMO_SUBS_003","구독 점검","메모 '구독' 3회 절약","🧾", BadgeConditionType.MEMO_COUNT,3,"구독|넷플|해지|유튜브프리미엄|네이버플러스멤버쉽|네이버플러스|쿠팡와우|쿠팡"));
        S.add(b("MEMO_LUNCH_003","도시락의 힘","메모 '점심' 3회 절약","🍱", BadgeConditionType.MEMO_COUNT,3,"도시락"));
        S.add(b("MEMO_SNACK_003","간식 참기","메모 '간식' 3회 절약","🍪", BadgeConditionType.MEMO_COUNT,3,"간식|디저트|케이크|휘낭시에|티라미수|요아정"));
        S.add(b("MEMO_SHOP_003","충동구매 컷","메모 '쇼핑' 3회 절약","🛍️", BadgeConditionType.MEMO_COUNT,3,"쇼핑|충동구매"));
        S.add(b("MEMO_NIGHT_003","야식 끊기","메모 '야식' 3회 절약","🌙", BadgeConditionType.MEMO_COUNT,3,"야식"));
        S.add(b("MEMO_GAME_003","인앱결제 줄이기","메모 '게임' 3회 절약","🎮", BadgeConditionType.MEMO_COUNT,3,"게임"));
        S.add(b("MEMO_CVS_003","편의점 대신 장보기","메모 '편의점' 3회 절약","🏪", BadgeConditionType.MEMO_COUNT,3,"편의점"));

        // upsert
        for (Badge s : S) {
            badgeRepository.findByCode(s.getCode()).ifPresentOrElse(ex -> {
                ex.setTitle(s.getTitle());
                ex.setDescription(s.getDescription());
                ex.setIcon(s.getIcon());
                ex.setConditionType(s.getConditionType());
                ex.setThreshold(s.getThreshold());
                ex.setKeyword(s.getKeyword());
                badgeRepository.save(ex);
            }, () -> badgeRepository.save(s));
        }
    }

    private void deleteIfExists(String code) {
        badgeRepository.findByCode(code).ifPresent(badgeRepository::delete);
    }

    private static Badge b(String code, String title, String desc, String icon,
                           BadgeConditionType type, long threshold, String keyword) {
        return Badge.builder()
                .code(code).title(title).description(desc).icon(icon)
                .conditionType(type).threshold(threshold).keyword(keyword)
                .build();
    }
}
