
package com.demojsf.jsf.bean;

import com.demojsf.dao.DaoCliente;
import com.demojsf.impl.DaoClienteImpl;
import com.demojsf.model.Cliente;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.component.export.PDFOptions;

@Named(value = "clientBean")
@ViewScoped
public class ClientJSFManagedBean implements Serializable {

    private Cliente cliente = new Cliente();
    private List<Cliente> lista = new ArrayList<>();
    private DaoClienteImpl dao = new DaoClienteImpl();
    private boolean modoInsert = false;
    private boolean modoEdit = true;
    
    
    private PDFOptions pdfOpt = new PDFOptions(); //add getter and setter too
    
    
  
     pdfOpt.setFacetBgColor("#F88017");

     //if, for example, your PDF table has 4 columns
     //1st column will occupy 10% of table's horizontal width,...3rd - 20%, 4th - 60% 
     float[] columnWidths = new float[]{0.1f, 0.1f, 0.2f, 0.6f};
    pdfOpt.setColumnWidths(columnWidths);
    
    
    public PDFOptions getPdfOpt() {
        return pdfOpt;
    }

    public void setPdfOpt(PDFOptions pdfOpt) {
        this.pdfOpt = pdfOpt;
    }
    

    public boolean isModoInsert() {
        return modoInsert;
    }

    public void setModoInsert(boolean modoInsert) {
        this.modoInsert = modoInsert;
    }

    public boolean isModoEdit() {
        return modoEdit;
    }

    public void setModoEdit(boolean modoEdit) {
        this.modoEdit = modoEdit;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @PostConstruct
    public void iniciar() {
        lista = dao.getCliente();
        cliente.setIdcliente(lista.size() + 1);
    }

    public List<Cliente> getLista() {
        return lista;
    }

    public void setLista(List<Cliente> lista) {
        this.lista = lista;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public ClientJSFManagedBean() {
    }
    
    
    public String register() {
        return "thanks?faces-redirect=true";
    }
   

    public void save() {

        dao.save(cliente);
        lista = dao.getCliente();
        cliente = new Cliente();
        cliente.setIdcliente(lista.size() + 1);
    }
    
      public void delete() {

        dao.delete(cliente);
        lista = dao.getCliente();
        cliente = new Cliente();
        cliente.setIdcliente(lista.size() + 1);
        modoEdit = true;
        modoInsert = false;
    }

    public void update() {

        dao.update(cliente);
        lista = dao.getCliente();
        cliente = new Cliente();
        cliente.setIdcliente(lista.size() + 1);
        modoEdit = true;
        modoInsert = false;
    }

    public void changeMode() {
        modoEdit = false;
        modoInsert = true;
    }
}
