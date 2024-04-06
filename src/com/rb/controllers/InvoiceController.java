package com.rb.controllers;

import com.rb.Aplication;
import com.rb.domain.Order;
import com.rb.domain.ProductoPed;
import java.util.List;

/**
 *
 * @author lrod
 */
public class InvoiceController {

    private final Aplication app;

    public InvoiceController(Aplication app) {
        this.app = app;
    }

    public void orderInvoice(Order order) {
        if (order != null && !orderIsInvoiced(order.getId())) {
            List<ProductoPed> products = order.getProducts();
            for (ProductoPed product : products) {
                System.out.println(product);
            }
        }

    }

    /**
     * Return true if all products in Order have been invoiced
     *
     * @param idOrder
     * @return
     */
    public boolean orderIsInvoiced(long idOrder) {
        return app.getControl().countUninvoicedProducts(idOrder) == 0;

    }

}
