package com.careerflow.reviewms.review.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.careerflow.reviewms.review.Review;
import com.careerflow.reviewms.review.ReviewRepository;
import com.careerflow.reviewms.review.ReviewService;


@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;


    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> getAllReviews(Long companyId) {
        List<Review> reviews = reviewRepository.findByCompanyId(companyId);
        return reviews;
    }

    @Override
    public boolean addReview(Long companyId, Review review) {
        if(companyId != null && review != null) {
            review.setCompanyId(companyId);
            reviewRepository.save(review);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Review getReviewById( Long reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }

    @Override
    public boolean updateReview( Long reviewId, Review review) throws IllegalArgumentException {
        Review existingReview = getReviewById( reviewId);
        if (existingReview != null) {
            existingReview.setTitle(review.getTitle());
            existingReview.setDescription(review.getDescription());
            existingReview.setRating(review.getRating());
            reviewRepository.save(existingReview);
            return true;
        } else {
            //throw new IllegalArgumentException("Review with ID " + reviewId + " does not exist for company with ID ");
            return false;
        }
    }

    @Override
    public boolean deleteReview(Long reviewId) {
        Review existingReview = reviewRepository.findById(reviewId).orElse(null);
        if (existingReview != null) {
            reviewRepository.delete(existingReview);
            return true;
        } else {
            return false;
        }
    }

    

}
