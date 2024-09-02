package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService {
    // Relate to 3 Repository includes: OrderDetailRepository, ProductRepository,
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new
                        DataNotFoundException("Cannot order has id = " + orderDetailDTO.getOrderId()));

        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new
                        DataNotFoundException("Cannot product has id = " + orderDetailDTO.getProductId()));

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setPrice(orderDetailDTO.getPrice());
        orderDetail.setQuantity(orderDetailDTO.getQuantity());
        orderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        orderDetail.setColor(orderDetailDTO.getColor());
        orderDetail.setOrder(existingOrder);
        orderDetail.setProduct(existingProduct);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail updateOrderDetail(Long orderDetailId, OrderDetailDTO orderDetailDTO) throws DataNotFoundException{
        OrderDetail existingOrderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new DataNotFoundException("Cannot order has id = " + orderDetailId));
        if (orderDetailDTO.getOrderId() == null) {
            orderDetailDTO.setOrderId(existingOrderDetail.getOrder().getId());
        }
        if (orderDetailDTO.getProductId() == null) {
            orderDetailDTO.setProductId(existingOrderDetail.getProduct().getId());
        }
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new
                        DataNotFoundException("Cannot order has id = " + orderDetailDTO.getOrderId()));

        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new
                        DataNotFoundException("Cannot product has id = " + orderDetailDTO.getProductId()));

        modelMapper.map(orderDetailDTO, existingOrderDetail);
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);

        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    public void deleteOrderDetail(Long id) throws DataNotFoundException {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot found order detail has id = " + id));
    }

    @Override
    public Order getOrderByOrderId(Long orderId) throws DataNotFoundException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new
                        DataNotFoundException("Cannot found order detail has id = " + orderId));
    }

    @Override
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }
}
