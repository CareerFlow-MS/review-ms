package com.careerflow.reviewms.review;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.careerflow.reviewms.review.messaging.ReviewMessageProducer;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private ReviewService reviewService;
    private ReviewMessageProducer reviewMessageProducer;

    public ReviewController(ReviewService reviewService, ReviewMessageProducer reviewMessageProducer) {
        this.reviewService = reviewService;
        this.reviewMessageProducer = reviewMessageProducer;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam Long companyId) {
        List<Review> reviews = reviewService.getAllReviews(companyId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<String> addReview(@RequestParam Long companyId, @RequestBody Review review) {
        boolean isReviewAdded = reviewService.addReview(companyId, review);
        if (isReviewAdded) {
            reviewMessageProducer.sendReviewMessage(review);
            return ResponseEntity.status(HttpStatus.CREATED).body("Review added successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add review");
        }
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long reviewId) {
        return new ResponseEntity<>(reviewService.getReviewById(reviewId), HttpStatus.OK);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId, @RequestBody Review review) {
        boolean isReviewUpdated = reviewService.updateReview(reviewId, review);
        if (isReviewUpdated) {
            return ResponseEntity.ok("Review updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        boolean isReviewDeleted = reviewService.deleteReview(reviewId);
        if (isReviewDeleted) {
            return ResponseEntity.ok("Review deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }
    }

    @GetMapping("/averageRating")
    public Double getAverageReview(@RequestParam Long companyId) {
        List<Review> reviewList =  reviewService.getAllReviews(companyId);
        return reviewList.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }




}
