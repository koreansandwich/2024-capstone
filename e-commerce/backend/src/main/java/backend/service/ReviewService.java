package backend.service;

import backend.DTO.ItemDTO;
import backend.entity.User;
import backend.repository.UserHistoryRepository;
import backend.repository.ItemRepository;
import backend.entity.UserHistory;
import backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ReviewService는 사용자가 구매한 제품에 대한 리뷰 및 평점 관리 기능을 제공합니다.
 * 주요 기능:
 * 1. 사용자가 구매한 제품 리스트 조회
 * 2. 사용자가 특정 제품에 대해 평점과 리뷰를 저장
 */

@Service
public class ReviewService {
    private final UserRepository userRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final ItemRepository itemRepository;

    /**
     * ReviewService 생성자: 필요한 Repository를 주입받습니다.
     *
     * @param userHistoryRepository UserHistory 관련 데이터 접근 Repository
     * @param itemRepository        Item 관련 데이터 접근 Repository
     */
    public ReviewService(UserRepository userRepository, UserHistoryRepository userHistoryRepository, ItemRepository itemRepository) {
        this.userHistoryRepository = userHistoryRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    /**
     * 사용자가 구매한 제품 리스트를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자가 구매한 제품 리스트 (제품의 주요 정보 포함)
     */
    public List<ItemDTO> getReviewItems(Long userId) {
        // 1. user_history 테이블에서 해당 사용자가 구매한 item_id 조회
        List<Long> itemIds = userHistoryRepository.findItemIdsByUserId(userId);

        // 2. items 테이블에서 itemIds로 데이터 조회
        List<ItemDTO> items = itemRepository.findItemsByIds(itemIds);

        for (ItemDTO item : items) {
            System.out.println("Item Name: " + item.getItemName());
            System.out.println("Item Image URL: " + item.getItemImageUrl());
            System.out.println("Item Link: " + item.getItemLink());
        }

        return items;
    }

    /**
     * 특정 제품에 대한 평점과 리뷰를 저장합니다.
     *
     * @param userId 사용자의 ID
     * @param itemId 제품의 ID
     * @param rating 사용자가 매긴 평점 (nullable)
     * @param review 사용자가 작성한 리뷰 (nullable)
     */
    public void saveReview(Long userId, Long itemId, Integer rating, String review) {
        // user_history 테이블에서 해당 userId와 itemId로 데이터 조회
        UserHistory userHistory = userHistoryRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 user_id와 item_id에 대한 기록이 없습니다."));

        // 평점과 리뷰를 업데이트
        userHistory.setRating(rating);
        userHistory.setReview(review);

        // 변경된 데이터 저장
        userHistoryRepository.save(userHistory);
    }
}

