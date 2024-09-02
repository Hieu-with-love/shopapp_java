package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        // Tim xem userId co ton tai khong
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot found user has id = " + orderDTO.getUserId()));

        // convert OrderDTO -> Order -> created
        // use library
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order =  modelMapper.map(orderDTO, Order.class);
        order.setUser(existingUser);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING.toString());
        LocalDate shippingDate = orderDTO.getShppingDate() == null
                ? LocalDate.now():orderDTO.getShppingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Shipping Date be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(long id, OrderDTO orderDTO) throws DataNotFoundException {
        // Want not to update userId, haven't pass userId to, because we
        // Get order origin
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new DateTimeException("Cannot found order has id = " + id));

        if (orderDTO.getUserId() == null){
            orderDTO.setUserId(existingOrder.getUser().getId());
        }

        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user has id = " + orderDTO.getUserId()));
        existingOrder.setUser(existingUser);
        modelMapper.map(OrderDTO.class, existingOrder);

        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(long id) throws DataNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot found order has id = " + id));

        order.setActive(false);
    }

    @Override
    public Order getOrderById(long id) throws DataNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot found order has id = " + id));
    }

    @Override
    public List<Order> getOrderByUserId(Long userId) throws DataNotFoundException {
        return orderRepository.findByUserId(userId);
    }
}
