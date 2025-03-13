package com.priyhotel.controller;

import com.priyhotel.entity.Coupon;
import com.priyhotel.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
@CrossOrigin(origins = "*")
public class CouponController {

    @Autowired
    CouponService couponService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestParam String code, @RequestParam double orderAmount) {
        try {
            Coupon coupon = couponService.validateCoupon(code, orderAmount);
            double discountedPrice = couponService.applyDiscount(coupon, orderAmount);
            return ResponseEntity.ok(Map.of("discountedPrice", discountedPrice, "message", "Coupon applied successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @PostMapping("/create")
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.createCoupon(coupon));
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateCoupon(@RequestParam String couponCode) {
        try {
            couponService.markInactive(couponCode);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateCoupon(@RequestParam String couponCode) {
        try {
            couponService.markActive(couponCode);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}
