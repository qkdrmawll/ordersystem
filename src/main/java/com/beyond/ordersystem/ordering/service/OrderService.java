package com.beyond.ordersystem.ordering.service;

import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.member.repository.MemberRepository;
import com.beyond.ordersystem.ordering.domain.OrderDetail;
import com.beyond.ordersystem.ordering.domain.OrderStatus;
import com.beyond.ordersystem.ordering.domain.Ordering;
import com.beyond.ordersystem.ordering.dto.OrderCreateReqDto;
import com.beyond.ordersystem.ordering.dto.OrderListResDto;
import com.beyond.ordersystem.ordering.repository.OrderingRepository;
import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public Long orderCreate(List<OrderCreateReqDto> orderDetailReqDtos) {
//        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmailAndDelYn(memberEmail,"N").orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        Ordering order = Ordering.builder().member(member).build();

        for (OrderCreateReqDto orderDetailDto : orderDetailReqDtos) {
            Product product = productRepository.findById(orderDetailDto.getProductId()).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
            Integer productCount = orderDetailDto.getProductCount();
            if (product.getStockQuantity() < productCount){
                throw new IllegalArgumentException("재고가 부족합니다.");
            }
            product.decreaseStockQuantity(productCount);

            OrderDetail orderDetail = OrderDetail.builder()
                    .ordering(order)
                    .product(product)
                    .quantity(productCount)
                    .build();
            order.getOrderDetails().add(orderDetail);
        }
        Ordering savedOrder = orderingRepository.save(order);
        return savedOrder.getId();
    }

    public List<OrderListResDto> orderList() {
        List<OrderListResDto> resDtos= new ArrayList<>();
        List<Ordering> all = orderingRepository.findAll();
        for (Ordering ordering : all) {
            OrderListResDto dto = ordering.fromEntity();
            List<OrderDetail> orderDetails = ordering.getOrderDetails();
            for (OrderDetail orderDetail : orderDetails) {
                OrderListResDto.OrderDetailResDto detailResDto = orderDetail.fromEntity();
                dto.getOrderDetailDtos().add(detailResDto);
            }
            resDtos.add(dto);
        }
        return resDtos;
    }

    public Page<OrderListResDto> myOrderList(Pageable pageable) {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmailAndDelYn(memberEmail, "N")
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        Page<Ordering> orderings = orderingRepository.findByMemberAndDelYn(pageable, member, "N");

        List<OrderListResDto> resDtos = new ArrayList<>();

        for (Ordering ordering : orderings) {
            OrderListResDto dto = ordering.fromEntity();
            List<OrderDetail> orderDetails = ordering.getOrderDetails();
            for (OrderDetail orderDetail : orderDetails) {
                OrderListResDto.OrderDetailResDto detailResDto = orderDetail.fromEntity();
                dto.getOrderDetailDtos().add(detailResDto);
            }
            resDtos.add(dto);
        }

        return new PageImpl<>(resDtos, pageable, orderings.getTotalElements());
    }

    public Ordering orderCancel(Long id) {
        Ordering ordering = orderingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not found"));
        ordering.updateStatus(OrderStatus.CANCELD);
        return ordering;


    }
}
