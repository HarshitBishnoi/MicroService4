package micro_service_4.micro_service_4.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import micro_service_4.micro_service_4.Modules.*;
import micro_service_4.micro_service_4.Repository.ServicablePinCodeRepository;
import micro_service_4.micro_service_4.Service.AddressDetailsService;
import micro_service_4.micro_service_4.Service.OrderProductMapService;
import micro_service_4.micro_service_4.Service.OrderService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;


@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AddressDetailsService addressDetailsService;
    @Autowired
    private OrderProductMapService orderProductMapService ;

    @Autowired
    private ServicablePinCodeRepository servicablePinCodeRepository;


    // make order summary when user checks out cart or clicks on buy now
    @RequestMapping(method = RequestMethod.POST, value = "/addCartEntry")
    public ResponseEntity<CustomResponse> addCartEntry(@RequestBody CartRequest request) throws Exception {

        System.out.println("frontend se call aai");
        System.out.println(request.getAddress());

        //basic validations on data sent in the request
        if(request.getProducts().size() == 0)
                return ResponseEntity.status(200).body(new CustomResponse(404,"No products Found",null));

        if(request.getTotalCost() == 0)
            return ResponseEntity.status(200).body(new CustomResponse(500,"Zero total Cost not allowed  ",null));

        if(request.getAddress().getPinCode() == null || request.getAddress().getPinCode().equals(""))
            return ResponseEntity.status(200).body(new CustomResponse(500,"Pin Code required",null));

        for(ProductDetails prodDetail: request.getProducts()){

            if(prodDetail.getQuantity() <= 0 ){
                return ResponseEntity.status(200).body(new CustomResponse(500,"Zero product Quantity not allowed",null));
            }
        }


        ServiceablePinCode pc = servicablePinCodeRepository.findById(request.getAddress().getPinCode())
                .orElse(null);

        if(pc == null || pc.isServicable() == false)
            return ResponseEntity.status(200).body(new CustomResponse(404,"Pin code not servicable",null));




        List<ProductDetails> validate_products= request.getProducts();
        List<String> requestToValidate = new ArrayList<>();

        for(ProductDetails prod: validate_products){
            requestToValidate.add(prod.getProductId());
        }

        RestTemplate restTemplate = new RestTemplate();

        final String uri ="http://10.10.212.75:8080/getProductsById";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List> entity = new HttpEntity<>(requestToValidate,headers);

        String productList = restTemplate.postForObject(uri,entity,String.class);

        JSONObject obj = new JSONObject(productList);
        JSONArray arr = obj.getJSONArray("responseData");


        ObjectMapper mapper = new ObjectMapper();
        int totalCost =0;

        //validations on data after fetching data from catalog service
        for(int i=0;i<arr.length();i++){

            ProductDetails prod = mapper.readValue(arr.getJSONObject(i).toString(),ProductDetails.class);

            if(!prod.getProductId().equals(validate_products.get(i).getProductId()))
                return ResponseEntity.status(200).body(new CustomResponse(403,"Product Id doesn't match",null));
            if(prod.getQuantity()<validate_products.get(i).getQuantity())
                return ResponseEntity.status(200).body(new CustomResponse(403,"Quantity doesn't match",null));
            if(!prod.getPrice().equals(validate_products.get(i).getPrice()))
                return ResponseEntity.status(200).body(new CustomResponse(403,"Price has been changed",null));


            totalCost += validate_products.get(i).getPrice() * validate_products.get(i).getQuantity();
        }

        if(totalCost != request.getTotalCost()){
            return ResponseEntity.status(200).body(new CustomResponse(403,"Total cost is incorrect",null));
        }

        //saving address and making the call to order service to make order entry to db
        addressDetailsService.addAddressDetails(request.getAddress());
        CartRequestResponse cartRequestResponse = orderService.makeCartEntryToOrders
                (
                        request.getCartId(),
                        request.getProducts(),
                        request.getAddress(),
                        request.getTotalCost()
                );

        return ResponseEntity.status(200).body(new CustomResponse(200,"All okay",cartRequestResponse));

    }

    //call to confirm order after payment service makes payment and order status needs to be changed
    @RequestMapping(method = RequestMethod.POST, value = "/orderConfirmation")
    public ResponseEntity<CustomResponse> confirmPaymentForOrder(@RequestBody PaymentRequest request) {

        OrderSummaryResponse response = orderService.confirmOrderPaymentRequest
                (
                        request.getOrderId(),
                        request.getPaymentId(),
                        request.getDateOfPurchase(),
                        request.getModeOfPayment(),
                        request.getSuccess()
                );

        List<ProductDetails> productList = orderProductMapService.getAllProductsByOrderId(request.getOrderId());

        UpdateQuantity updateQuantity = new UpdateQuantity( productList,true);
        final String uri = "http://10.10.212.75:8080/updateQuantity";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateQuantity> entity = new HttpEntity<>(updateQuantity,headers);

        String resp = restTemplate.postForObject(uri,entity,String.class);

        return ResponseEntity.status(200).body(new CustomResponse(200,"All okay",response));


    }


    //get order summary corresponding to an orderId
    @RequestMapping(method = RequestMethod.GET, value = "/orderSummary/{orderId}")
    public ResponseEntity<CustomResponse> postOrderSummary(@PathVariable("orderId") UUID orderId){

        OrderSummaryResponse orderSummaryResponse = orderService.createResponseForOrderSummary(orderId);

        return ResponseEntity.status(200).body(new CustomResponse(200,"All okay",orderSummaryResponse));
    }


    // get all order summaries in the database
    @RequestMapping(method = RequestMethod.GET, value = "/orderSummary")
    public ResponseEntity<CustomResponse> postAllOrderSummary(){
        List<OrderSummaryResponse> response =  orderService.createResponseForAllOrderSummary();

        return ResponseEntity.status(200).body(new CustomResponse(200,"All okay",response));
    }


    //end point to cancel order
    @RequestMapping(method = RequestMethod.GET, value = "/cancelOrder/{orderId}")
    public ResponseEntity<CustomResponse> cancelOrderRequest(@PathVariable UUID orderId) throws Exception{
        orderService.cancelOrderRequest(orderId);

        final String cancelOrderRequest = "http://10.10.212.19:8080/payment/cancel/" + orderId;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(cancelOrderRequest,String.class);

        JSONObject obj = new JSONObject(response);
        int statusCode = obj.getInt("statusCode");
        if(statusCode!=200)
            return ResponseEntity.status(200).body(new CustomResponse(404,"Sorry could not initiate refund",null));

        return ResponseEntity.status(200).body(new CustomResponse(200,"All okay",null));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/changeStatusToOFD/{orderId}")
    public ResponseEntity<CustomResponse> updateOrderStatusToOFD(@PathVariable UUID orderId)
    {
        orderService.updateOrderRequestToOFD(orderId);
        return ResponseEntity.status(200).body(new CustomResponse(200,"All okay",null));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/changeStatusToD/{orderId}")
    public ResponseEntity<CustomResponse> updateOrderStatusToD(@PathVariable UUID orderId)
    {
        orderService.updateOrderRequestToD(orderId);
        return ResponseEntity.status(200).body(new CustomResponse(200,"All okay",null));
    }


    @RequestMapping(method = RequestMethod.GET, value= "/orderDetails/{orderIdString}")
    public ResponseEntity<CustomResponse> getOrderDetailsForPayment(@PathVariable String orderIdString){

        UUID orderId;
        try{
            orderId = UUID.fromString(orderIdString);
        }catch (Exception e){
            return ResponseEntity.status(200).body(new CustomResponse(500,"Invalid Orderid",null));
        }

        PaymentOrderResponse paymentOrderResponse = orderService.getResponseForPaymentService(orderId);
        return ResponseEntity.status(200).body(new CustomResponse(200,"All Okay",paymentOrderResponse));


    }

    @RequestMapping(method = RequestMethod.GET,value = "/addPin/{pinCode}")
    public ResponseEntity<CustomResponse> addPinCode(@PathVariable Integer pinCode){

        ServiceablePinCode pc = new ServiceablePinCode(pinCode,true);
        servicablePinCodeRepository.save(pc);

        return ResponseEntity.status(200).body(new CustomResponse(200,"All Okay",pc));


    }

}
