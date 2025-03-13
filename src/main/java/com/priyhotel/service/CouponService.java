package com.priyhotel.service;

import com.priyhotel.entity.Coupon;
import com.priyhotel.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Coupon createCoupon(Coupon coupon){
        return couponRepository.save(coupon);
    }

    public Coupon validateCoupon(String code, double orderAmount) {
        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Invalid or expired coupon"));

        if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Coupon expired");
        }

        if (orderAmount < coupon.getMinOrderAmount()) {
            throw new RuntimeException("Minimum order amount not met");
        }

        return coupon;
    }

    public double applyDiscount(Coupon coupon, double orderAmount) {
        if (coupon.getDiscountAmount() > 0) {
            return Math.max(0, orderAmount - coupon.getDiscountAmount());
        } else if (coupon.getDiscountPercentage() > 0) {
            double discountAmount;
            double discountAmountByPercentage = (orderAmount * coupon.getDiscountPercentage() / 100);
            return orderAmount - Math.min(coupon.getMaxDiscountAmount(), discountAmountByPercentage);
        }
        return orderAmount;
    }

    public void markInactive(String couponCode){
        Optional<Coupon> coupon = couponRepository.findByCodeIgnoreCase(couponCode);
        if(coupon.isPresent()){
            coupon.get().setActive(false);
            couponRepository.save(coupon.get());
        }else{
            throw new RuntimeException("Coupon does not exist");
        }
    }

    public void markActive(String couponCode) {
        Optional<Coupon> coupon = couponRepository.findByCodeIgnoreCase(couponCode);
        if(coupon.isPresent()){
            coupon.get().setActive(true);
            couponRepository.save(coupon.get());
        }else{
            throw new RuntimeException("Coupon does not exist");
        }
    }

    public Coupon getCouponByCouponCode(String couponCode){
        Optional<Coupon> coupon = couponRepository.findByCodeIgnoreCase(couponCode);
        if(coupon.isPresent()){
            return coupon.get();
        }else{
            throw new RuntimeException("Coupon not  found!");
        }
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }


}
