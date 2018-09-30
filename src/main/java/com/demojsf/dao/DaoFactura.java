
package com.demojsf.dao;

import java.util.List;


public interface DaoFactura<Factura> {
    void save(Factura f);
    List<Factura> getFact();
    List<Factura> getListFact();
    List<Factura> getListRecau();
}
