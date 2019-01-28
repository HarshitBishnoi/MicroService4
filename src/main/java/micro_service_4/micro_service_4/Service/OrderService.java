package micro_service_4.micro_service_4.Service;

import com.google.common.collect.Lists;
import com.sun.jndi.toolkit.url.Uri;
import micro_service_4.micro_service_4.Exceptions.OrderNotFoundException;
import micro_service_4.micro_service_4.Modules.*;
import micro_service_4.micro_service_4.Repository.OrderCartMapRepository;
import micro_service_4.micro_service_4.Repository.OrderRepository;
import micro_service_4.micro_service_4.Modules.OrderSummaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderProductMapService orderProductMapService;


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressDetailsService addressDetailsService;

    @Autowired
    private OrderCartMapRepository orderCartMapRepository;


    public CartRequestResponse makeCartEntryToOrders(String cartId, List<ProductDetails> productDetails, AddressDetails address, Integer totalCost) {

        UUID orderId =  saveToOrderTable(null, address.getAddressId(), totalCost);

        OrderCartMap orderCartMap = new OrderCartMap(orderId,cartId);
        orderCartMapRepository.save(orderCartMap);

        for (ProductDetails prod : productDetails) {
            orderProductMapService.saveToOrderProductMap(orderId, prod.getProductId(), prod.getProductName(), prod.getQuantity(), prod.getPrice());
        }

        return new CartRequestResponse(orderId);
    }

    public OrderSummaryResponse confirmOrderPaymentRequest(UUID orderId, String paymentId, Date dateOfPurchase, String modeOfPayment, Boolean isSuccess) {

        OrderCartMap orderCartMap = orderCartMapRepository.findById(orderId).get();

        if(orderCartMap.getCartId().length() != 0) {
            RestTemplate restTemplate = new RestTemplate();
            final String emptyCartUri = "https://cb289950.ngrok.io/cart/emptyCart";
            restTemplate.delete(emptyCartUri);
        }

        this.confirmOrderPayment(orderId,dateOfPurchase,paymentId);
        return createResponseForOrderSummary(orderId);
    }


    public OrderSummaryResponse createResponseForOrderSummary(UUID orderId){

        Order order = orderRepository.findById(orderId)
                        .orElseThrow(()->new OrderNotFoundException(orderId));

        OrderSummaryResponse response = new OrderSummaryResponse();
        response.setOrder_id(order.getOrderId());
        response.setDate_of_purchase(order.getDateOfPurchase());
        response.setAddress(addressDetailsService.getAddressDetails(order.getAddressId()));
        response.setProducts(orderProductMapService.getAllProductsByOrderId(order.getOrderId()));
        response.setStatus(order.getStatus());
        response.setPayment_id(order.getPaymentId());
        return response;
    }

    public List<OrderSummaryResponse> createResponseForAllOrderSummary(){

        Iterable<Order> allOrdersIterable = orderRepository.findAll();
        List<OrderSummaryResponse> response = new ArrayList<>();

        List<Order> mlist = Lists.newArrayList(allOrdersIterable);
        Collections.reverse(mlist);

        for(Order order:mlist){
            response.add(createResponseForOrderSummary(order.getOrderId()));
        }

        return response;
    }



    private UUID saveToOrderTable(Date date_of_purchase, UUID address, Integer total_cost) {

        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, date_of_purchase, address, total_cost,null , Order.Status.Awaiting);
        this.addOrder(order);

        return orderId;

    }

    private void addOrder(Order order) {
        orderRepository.save(order);
    }

    private void confirmOrderPayment(UUID orderId,Date dateOfPurchase, String paymentId) {

        orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(Order.Status.Confirmed);
                    order.setDateOfPurchase(dateOfPurchase);
                    order.setPaymentId(paymentId);
                    return orderRepository.save(order);
                }).orElseThrow(()->new OrderNotFoundException(orderId));

    }

    public void cancelOrderRequest(UUID orderId){

        orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(Order.Status.Cancelled);
                    return orderRepository.save(order);
                }).orElseThrow(()->new OrderNotFoundException(orderId));

    }
    public void updateOrderRequestToOFD(UUID orderId){

        orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(Order.Status.OutForDelivery);
                    return orderRepository.save(order);
                }).orElseThrow(()->new OrderNotFoundException(orderId));

    }

    public void updateOrderRequestToD(UUID orderId){

        orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(Order.Status.Delivered);
                    return orderRepository.save(order);
                }).orElseThrow(()->new OrderNotFoundException(orderId));

    }

    public PaymentOrderResponse getResponseForPaymentService(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new OrderNotFoundException(orderId));

        List<ProductDetails> productDetails =orderProductMapService.getAllProductsByOrderId(order.getOrderId());
        List<String> productIds =new ArrayList<>();

        for(ProductDetails prod:productDetails){
            productIds.add(prod.getProductId());
        }

        return new PaymentOrderResponse(orderId,productIds,order.getTotalCost());


    }
}
