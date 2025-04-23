package org.example.honorsparkingbe.controller;

import org.example.honorsparkingbe.security.util.SecurityUtil;
import org.example.honorsparkingbe.service.AlarmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AlarmController {

    private final AlarmService alarmService;

    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    /**
     * 회원 알람 불러오기 (현재 로그인한 사용자)
     * GET /api/v1/alarmAll
     * @param category
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/alarmAll")
    public ResponseEntity<Map<String, Object>> getAlarms(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Long memberId = getCurrentMemberId();

            // JPA 에서는 page 0부터 시작하므로
            int adjustedPage = Math.max(page - 1, 0); // 음수 방지

            Map<String, Object> response = alarmService.getAlarms(memberId, category, adjustedPage, size);

            // Pagination 값 조정
            Map<String, Object> pagination = new HashMap<>((Map<String, Object>) response.get("pagination"));
            pagination.put("currentPage", (int) pagination.get("currentPage") + 1);

            response = new HashMap<>(response);
            response.put("pagination", pagination);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) { // 잘못된 category 값 등 처리
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 읽지 않은 알림이 있는지 확인
     * GET /api/v1/alarmUnread
     * @return
     */
    @GetMapping("/alarmUnread")
    public ResponseEntity<Map<String, Object>> getUnreadAlarms() {
        try{
            // Long memberId = getCurrentMemberId(); // 시현씨가 따로 구현한 걸로 OAuth2 실패(SecurityUtil 사용할 것)
            Long memberId= SecurityUtil.getCurrentUserId();
            boolean hasUnread= alarmService.hasUnreadAlarms(memberId);
            return ResponseEntity.ok(Map.of("hasUnread", hasUnread));
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * 회원 알람 읽기
     * PUT /api/v1/alarm
     * @param requestBody
     * @return
     */
    @PutMapping("/alarm")
    public ResponseEntity<Map<String, Object>> updateAlarmsToRead(@RequestBody Map<String, List<Long>> requestBody) {
        // 요청에서 alarmIDList 추출
        List<Long> alarmIDList = requestBody.get("alarmIDList");

        try {
            // 서비스 호출
            Map<String, Object> response = alarmService.updateAlarmsToRead(alarmIDList);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 알람 선택 삭제
     * DELETE /api/v1/alarm
     * @param requestBody
     * @return
     */
    @DeleteMapping("/alarm")
    public ResponseEntity<Map<String, Object>> deleteAlarms(@RequestBody Map<String, List<Long>> requestBody) {
        // 요청에서 alarmIDList 추출
        List<Long> alarmIDList = requestBody.get("alarmIDList");

        try {
            Map<String, Object> response = alarmService.deleteAlarms(alarmIDList);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 알람 전체 삭제 (현재 로그인한 사용자)
     * DELETE /api/v1/alarm/all
     * @return
     */
    @DeleteMapping("/alarm/all")
    public ResponseEntity<Map<String, Object>> deleteAllAlarms() {
        try {
            Long memberId = getCurrentMemberId(); // 로그인한 사용자 ID 가져오기
            Map<String, Object> response = alarmService.deleteAllAlarms(memberId);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {  // 인증되지 않은 경우 예외 처리
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 현재 로그인한 사용자의 memberId 가져오기
     * 일반 로그인: UserDetails에서 authId 가져와서 memberId 조회
     * 소셜 로그인: OAuth2User에서 socialId 가져와서 memberId 조회
     * @return memberId
     */
    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        System.out.println("Principal: " + principal.getClass().getName()); // 디버깅 로그

        // 일반 로그인 처리 (UserDetails 기반)
        if (principal instanceof UserDetails userDetails) {
            return findMemberIdByAuthId(userDetails.getUsername()); // authId → memberId 조회
        }

        // 소셜 로그인 처리 (OAuth2User)
        if (principal instanceof OAuth2User oAuth2User) {
            String authId = (String) oAuth2User.getAttribute("sub"); // 소셜 로그인 고유 ID
            return findMemberIdByAuthId(authId);  // 🔥 authId로 memberId 조회
        }

        throw new IllegalStateException("Invalid user authentication data");
    }

    /**
     * 로그인 사용자의 authId로 memberId 조회
     */
    private Long findMemberIdByAuthId(String authId) {
        Long memberId = alarmService.findMemberIdByAuthId(authId);
        if (memberId == null) {
            throw new IllegalStateException("User not found with authId: " + authId);
        }
        return memberId;
    }

}
