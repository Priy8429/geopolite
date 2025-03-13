package com.priyhotel.service;

import com.priyhotel.dto.ReviewRequestDto;
import com.priyhotel.entity.Asset;
import com.priyhotel.entity.Hotel;
import com.priyhotel.entity.Review;
import com.priyhotel.entity.User;
import com.priyhotel.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    HotelService hotelService;

    @Autowired
    AuthService userService;

    @Autowired
    AssetService assetService;

    public Review addReview(ReviewRequestDto reviewRequestDto){
        Hotel hotel = hotelService.getHotelById(reviewRequestDto.getHotelId());
        User user = userService.getUserById(reviewRequestDto.getUserId());
        List<Asset> assets = assetService.getAllAssetsByIds(reviewRequestDto.getAssetIds());

        Review newReview = new Review();
        newReview.setComment(reviewRequestDto.getComment());
        newReview.setRating(reviewRequestDto.getRating());
        newReview.setHotel(hotel);
        newReview.setUser(user);
        newReview.setAssets(assets);
        return reviewRepository.save(newReview);
    }

    public List<Review> getReviewsForHotel(Long hotelId) {
        return reviewRepository.findByHotelId(hotelId);
    }
}
