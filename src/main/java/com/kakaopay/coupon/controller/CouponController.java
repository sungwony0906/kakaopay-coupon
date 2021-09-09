package com.kakaopay.coupon.controller;

import com.kakaopay.coupon.error.exception.InvalidEmailException;
import com.kakaopay.coupon.model.Coupon;
import com.kakaopay.coupon.model.dto.CouponCreateDTO;
import com.kakaopay.coupon.service.CouponService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/coupon/{id}", method = RequestMethod.GET)
    public Coupon getCoupon(@PathVariable Long id) {
        return couponService.get(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/coupon", method = RequestMethod.GET)
    public Page<Coupon> getCouponListWithPage(Pageable pageable) {
        log.info("getCouponListWithPage called : " + pageable);
        return couponService.getList(pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/coupon", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
    public Coupon createCoupon(@RequestBody @Valid CouponCreateDTO couponCreateDTO, BindingResult error) {
        if (error.hasErrors()) {
            for (ObjectError err :error.getAllErrors()) {
                if (InvalidEmailException.errorCode.equals(err.getDefaultMessage())) {
                    log.info("CouponController - createCoupon : invalid email");
                    throw new InvalidEmailException("Fail to create Coupon. Email format is invalid.");
                }
            }
        }
        return couponService.create(couponCreateDTO);
    }
}
