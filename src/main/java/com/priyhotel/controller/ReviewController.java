package com.priyhotel.controller;

import com.priyhotel.constants.Constants;
import com.priyhotel.dto.ReviewRequestDto;
import com.priyhotel.entity.Asset;
import com.priyhotel.entity.Review;
import com.priyhotel.service.AssetService;
import com.priyhotel.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @Autowired
    AssetService assetService;

    @PostMapping
    public ResponseEntity<Review> addReview(@RequestBody ReviewRequestDto reviewRequestDto) {
        Review review = reviewService.addReview(reviewRequestDto);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Review>> getReviewsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getReviewsForHotel(hotelId));
    }

    @PostMapping("/assets/upload")
    public ResponseEntity<List<Asset>> uploadAssets(@RequestParam("files") List<MultipartFile> files){
        return ResponseEntity.ok(assetService.uploadAssets(files, Constants.REVIEW_DIR_NAME));
    }

}
