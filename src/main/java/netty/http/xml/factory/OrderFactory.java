package netty.http.xml.factory;

import netty.http.xml.model.Address;
import netty.http.xml.model.Customer;
import netty.http.xml.model.Order;
import netty.http.xml.model.Shipping;

/**
 * OrderFactory
 *
 * @author liuruichao
 * Created on 2015-12-10 10:33
 */
public class OrderFactory {
    public static Order create(long orderId) {
        Order order = new Order();
        order.setOrderNumber(orderId);
        order.setTotal(9999.999f);
        Address address = new Address();
        address.setCity("北京");
        address.setCountry("中国");
        address.setPostCode("100000");
        address.setState("你猜");
        address.setStreet1("你再猜");
        order.setBillTo(address);
        Customer customer = new Customer();
        customer.setCustomerNumber(orderId);
        customer.setFirstName("liu");
        customer.setLastName("ruichao");
        order.setCustomer(customer);
        Shipping shipping = new Shipping();
        shipping.setDescription("发货中...");
        order.setShipping(shipping);
        order.setShipTo(address);
        return order;
    }
}
