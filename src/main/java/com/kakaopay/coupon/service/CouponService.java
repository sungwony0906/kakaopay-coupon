package com.kakaopay.coupon.service;

import com.kakaopay.coupon.model.dto.CouponCreateDTO;
import com.kakaopay.coupon.core.CodeGenerator;
import com.kakaopay.coupon.error.exception.*;
import com.kakaopay.coupon.model.Coupon;
import com.kakaopay.coupon.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepo;

    @Autowired
    private CodeGenerator codeGenerator;

    private static final int TRY_COUNT_IN_COLLISION = 5;

    @Transactional(readOnly = true)
    public Coupon get(Long id) {
        Coupon coupon = couponRepo.getById(id);
        if (coupon == null) {
            throw new NotExistCouponException("Not exist coupon with id : " + id);
        }
        return coupon;
    }

    @Transactional(readOnly = true)
    public Page<Coupon> getList(Pageable pageable) {
        return couponRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Coupon getByEmail(String email) {
        return couponRepo.findByEmail(email);
    }

    @Transactional
    public Coupon create(String email) {
        return create(new CouponCreateDTO(email));
    }

    @Transactional
    public Coupon create(CouponCreateDTO dto) {
        if (StringUtils.isEmpty(dto.getEmail())) {
            log.info("CouponService - create : empty dto");
            throw new EmptyEmailException("Fail to create Coupon. Email is null or empty.");
        }
        String code = generateUniqueCode();
        if (StringUtils.isEmpty(code)) {
            log.warn("CouponService - create : empty code");
            throw new EmptyCodeException("Fail to create Coupon. Code is null or empty.");
        } else if (couponRepo.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Fail to create Coupon. Already coupon issued for this mail.");
        }

        Coupon coupon = new Coupon(dto.getEmail(), code);
        couponRepo.save(coupon);
        log.info("CouponService - create : success with coupon code : {}", coupon.getCode());
        return coupon;
    }

    private String generateUniqueCode() {
        int tryCount = TRY_COUNT_IN_COLLISION;
        String code = null;
        while (tryCount  > 0) {
            code = codeGenerator.generateCode();
            if (!couponRepo.existsByCode(code)) {
                break;
            }
            tryCount--;
            if (tryCount == 0) {
                throw new CodeCollisionException("Fail to create Coupon. Collision occur more than 5 in code generator.");
            }
        }
        return code;
    }
}
