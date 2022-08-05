package com.bacon.controllers;

import com.bacon.Aplication;
import com.bacon.domain.Order;
import com.bacon.domain.ProductoPed;
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
