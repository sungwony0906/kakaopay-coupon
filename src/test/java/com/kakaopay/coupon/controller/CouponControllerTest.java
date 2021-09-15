package com.kakaopay.coupon.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.coupon.model.Coupon;
import com.kakaopay.coupon.model.dto.CouponCreateDTO;
import com.kakaopay.coupon.repository.CouponRepository;
import com.kakaopay.coupon.service.CouponService;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(CouponController.class)
//@Controller, @ControllerAdvice, @JsonComponent, @Converter, @GenericConverter, @Filter, @WebMvcConfigurer, @HandlerMethodArgumentResolver 등만 Bean으로 등록
class CouponControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    CouponService couponService;

    @MockBean
    CouponRepository couponRepository;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void getCoupon_성공() throws Exception {
        doReturn(Coupon.builder().build()).when(couponService).get(1L);
        mvc.perform(get("/api/v1/coupon/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getCouponList_성공() throws Exception {
        doReturn(new PageImpl<Coupon>(new ArrayList<Coupon>())).when(couponService).getList(any(Pageable.class));
        mvc.perform(get("/api/v1/coupon"))
                .andExpect(status().isOk());
    }

    @Test
    void createCoupon_파라미터_없을경우_실패() throws Exception {
        mvc.perform(post("/api/v1/coupon"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createCoupon_성공() throws Exception {
        //given
        CouponCreateDTO couponCreateDTO = new CouponCreateDTO();
        couponCreateDTO.setEmail("sample@gmail.com");
        String code = "code";

        doReturn(Coupon.builder()
                         .email(couponCreateDTO.getEmail())
                         .code(code)
                         .build())
                .when(couponService)
                .create(any(CouponCreateDTO.class));

        //when
        MvcResult mvcResult = mvc.perform(post("/api/v1/coupon")
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(mapper.writeValueAsString(
                                                          couponCreateDTO)))
                                      .andExpect(status().isCreated())
                                      .andReturn();

        Coupon responseCoupon = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                Coupon.class);

        assertThat(responseCoupon.getEmail()).isEqualTo(couponCreateDTO.getEmail());
        assertThat(responseCoupon.getCode()).isEqualTo(code);
    }
}