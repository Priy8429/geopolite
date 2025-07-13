package com.priyhotel.service;

import com.priyhotel.constants.RefundStatus;
import com.priyhotel.entity.Payment;
import com.priyhotel.entity.Refund;
import com.priyhotel.repository.RefundRepository;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RefundService {

    @Autowired
    RefundRepository refundRepository;

    @Autowired
    PaymentService paymentService;

    public Refund getRefundById(String refundId){
        return refundRepository.findByRefundId(refundId);
    }

    public Refund getByPaymentId(String paymentId) throws RazorpayException {
        Payment payment = paymentService.getByPaymentId(paymentId);
        Refund refund = refundRepository.findByPaymentId(payment.getId());
        if(Objects.nonNull(refund) && refund.getRefundStatus().equals(RefundStatus.pending)){
            com.razorpay.Refund razorpayRefund = paymentService.getRazorpayClient().payments.fetchRefund(refund.getRefundId());
            if(razorpayRefund.get("status").equals(RefundStatus.processed.toString())){
                refund.setRefundStatus(RefundStatus.processed);
                refundRepository.save(refund);
                return refund;
            }else if(razorpayRefund.get("status").equals(RefundStatus.failed.toString())){
                refund.setRefundStatus(RefundStatus.failed);
                refundRepository.save(refund);
                return refund;
            }
        }
        return refund;
    }

}
