package netty.http.xml.model;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * TestOrder
 *
 * @author liuruichao
 *         Created on 2015-12-09 15:03
 */
public class TestOrder {
    private IBindingFactory factory = null;
    private StringWriter writer = null;
    private StringReader reader = null;
    private final static String CHARSET_NAME = "UTF-8";

    private String encode2Xml(Order order) throws Exception {
        factory = BindingDirectory.getFactory(Order.class);
        writer = new StringWriter();
        IMarshallingContext mctx = factory.createMarshallingContext();
        mctx.setIndent(2);

        mctx.marshalDocument(order, CHARSET_NAME, null, writer);
        String xmlStr = writer.toString();
        writer.close();
        return xmlStr;
    }

    private Order decode2Order(String xmlBody) throws Exception {
        reader = new StringReader(xmlBody);
        IUnmarshallingContext uctx = factory.createUnmarshallingContext();
        Order order = (Order) uctx.unmarshalDocument(reader);
        return order;
    }

    public static void main(String[] args) throws Exception {
        TestOrder test = new TestOrder();
        Order order = new Order();
        Address address = new Address();
        address.setCity("北京");
        address.setCountry("123");
        address.setPostCode("100000");
        address.setState("1");
        address.setStreet1("123");
        order.setBillTo(address);
        Customer customer = new Customer();
        customer.setFirstName("liu");
        customer.setLastName("ruichao");
        order.setCustomer(customer);
        order.setOrderNumber(10);
        order.setShipTo(address);
        order.setTotal(100F);
        String body = test.encode2Xml(order);
        System.out.println(body);
        Order order2 = test.decode2Order(body);
        System.out.println(order2.getTotal());
    }
}
