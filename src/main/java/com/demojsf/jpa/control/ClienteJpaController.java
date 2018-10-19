/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demojsf.jpa.control;

import com.demojsf.jpa.control.exceptions.IllegalOrphanException;
import com.demojsf.jpa.control.exceptions.NonexistentEntityException;
import com.demojsf.jpa.control.exceptions.RollbackFailureException;
import com.demojsf.jpa.model.Cliente;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.demojsf.jpa.model.Factura;
import java.util.ArrayList;
import java.util.Collection;
import com.demojsf.jpa.model.Contrato;
import com.demojsf.jpa.model.Recaudo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Carlos
 */
public class ClienteJpaController implements Serializable {

    public ClienteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cliente cliente) throws RollbackFailureException, Exception {
        if (cliente.getFacturaCollection() == null) {
            cliente.setFacturaCollection(new ArrayList<Factura>());
        }
        if (cliente.getContratoCollection() == null) {
            cliente.setContratoCollection(new ArrayList<Contrato>());
        }
        if (cliente.getRecaudoCollection() == null) {
            cliente.setRecaudoCollection(new ArrayList<Recaudo>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Factura> attachedFacturaCollection = new ArrayList<Factura>();
            for (Factura facturaCollectionFacturaToAttach : cliente.getFacturaCollection()) {
                facturaCollectionFacturaToAttach = em.getReference(facturaCollectionFacturaToAttach.getClass(), facturaCollectionFacturaToAttach.getIdfactura());
                attachedFacturaCollection.add(facturaCollectionFacturaToAttach);
            }
            cliente.setFacturaCollection(attachedFacturaCollection);
            Collection<Contrato> attachedContratoCollection = new ArrayList<Contrato>();
            for (Contrato contratoCollectionContratoToAttach : cliente.getContratoCollection()) {
                contratoCollectionContratoToAttach = em.getReference(contratoCollectionContratoToAttach.getClass(), contratoCollectionContratoToAttach.getNumcontrato());
                attachedContratoCollection.add(contratoCollectionContratoToAttach);
            }
            cliente.setContratoCollection(attachedContratoCollection);
            Collection<Recaudo> attachedRecaudoCollection = new ArrayList<Recaudo>();
            for (Recaudo recaudoCollectionRecaudoToAttach : cliente.getRecaudoCollection()) {
                recaudoCollectionRecaudoToAttach = em.getReference(recaudoCollectionRecaudoToAttach.getClass(), recaudoCollectionRecaudoToAttach.getIdrecaudo());
                attachedRecaudoCollection.add(recaudoCollectionRecaudoToAttach);
            }
            cliente.setRecaudoCollection(attachedRecaudoCollection);
            em.persist(cliente);
            for (Factura facturaCollectionFactura : cliente.getFacturaCollection()) {
                Cliente oldIdclieOfFacturaCollectionFactura = facturaCollectionFactura.getIdclie();
                facturaCollectionFactura.setIdclie(cliente);
                facturaCollectionFactura = em.merge(facturaCollectionFactura);
                if (oldIdclieOfFacturaCollectionFactura != null) {
                    oldIdclieOfFacturaCollectionFactura.getFacturaCollection().remove(facturaCollectionFactura);
                    oldIdclieOfFacturaCollectionFactura = em.merge(oldIdclieOfFacturaCollectionFactura);
                }
            }
            for (Contrato contratoCollectionContrato : cliente.getContratoCollection()) {
                Cliente oldIdclieOfContratoCollectionContrato = contratoCollectionContrato.getIdclie();
                contratoCollectionContrato.setIdclie(cliente);
                contratoCollectionContrato = em.merge(contratoCollectionContrato);
                if (oldIdclieOfContratoCollectionContrato != null) {
                    oldIdclieOfContratoCollectionContrato.getContratoCollection().remove(contratoCollectionContrato);
                    oldIdclieOfContratoCollectionContrato = em.merge(oldIdclieOfContratoCollectionContrato);
                }
            }
            for (Recaudo recaudoCollectionRecaudo : cliente.getRecaudoCollection()) {
                Cliente oldIdclienteOfRecaudoCollectionRecaudo = recaudoCollectionRecaudo.getIdcliente();
                recaudoCollectionRecaudo.setIdcliente(cliente);
                recaudoCollectionRecaudo = em.merge(recaudoCollectionRecaudo);
                if (oldIdclienteOfRecaudoCollectionRecaudo != null) {
                    oldIdclienteOfRecaudoCollectionRecaudo.getRecaudoCollection().remove(recaudoCollectionRecaudo);
                    oldIdclienteOfRecaudoCollectionRecaudo = em.merge(oldIdclienteOfRecaudoCollectionRecaudo);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cliente cliente) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cliente persistentCliente = em.find(Cliente.class, cliente.getIdcliente());
            Collection<Factura> facturaCollectionOld = persistentCliente.getFacturaCollection();
            Collection<Factura> facturaCollectionNew = cliente.getFacturaCollection();
            Collection<Contrato> contratoCollectionOld = persistentCliente.getContratoCollection();
            Collection<Contrato> contratoCollectionNew = cliente.getContratoCollection();
            Collection<Recaudo> recaudoCollectionOld = persistentCliente.getRecaudoCollection();
            Collection<Recaudo> recaudoCollectionNew = cliente.getRecaudoCollection();
            List<String> illegalOrphanMessages = null;
            for (Factura facturaCollectionOldFactura : facturaCollectionOld) {
                if (!facturaCollectionNew.contains(facturaCollectionOldFactura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Factura " + facturaCollectionOldFactura + " since its idclie field is not nullable.");
                }
            }
            for (Contrato contratoCollectionOldContrato : contratoCollectionOld) {
                if (!contratoCollectionNew.contains(contratoCollectionOldContrato)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Contrato " + contratoCollectionOldContrato + " since its idclie field is not nullable.");
                }
            }
            for (Recaudo recaudoCollectionOldRecaudo : recaudoCollectionOld) {
                if (!recaudoCollectionNew.contains(recaudoCollectionOldRecaudo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Recaudo " + recaudoCollectionOldRecaudo + " since its idcliente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Factura> attachedFacturaCollectionNew = new ArrayList<Factura>();
            for (Factura facturaCollectionNewFacturaToAttach : facturaCollectionNew) {
                facturaCollectionNewFacturaToAttach = em.getReference(facturaCollectionNewFacturaToAttach.getClass(), facturaCollectionNewFacturaToAttach.getIdfactura());
                attachedFacturaCollectionNew.add(facturaCollectionNewFacturaToAttach);
            }
            facturaCollectionNew = attachedFacturaCollectionNew;
            cliente.setFacturaCollection(facturaCollectionNew);
            Collection<Contrato> attachedContratoCollectionNew = new ArrayList<Contrato>();
            for (Contrato contratoCollectionNewContratoToAttach : contratoCollectionNew) {
                contratoCollectionNewContratoToAttach = em.getReference(contratoCollectionNewContratoToAttach.getClass(), contratoCollectionNewContratoToAttach.getNumcontrato());
                attachedContratoCollectionNew.add(contratoCollectionNewContratoToAttach);
            }
            contratoCollectionNew = attachedContratoCollectionNew;
            cliente.setContratoCollection(contratoCollectionNew);
            Collection<Recaudo> attachedRecaudoCollectionNew = new ArrayList<Recaudo>();
            for (Recaudo recaudoCollectionNewRecaudoToAttach : recaudoCollectionNew) {
                recaudoCollectionNewRecaudoToAttach = em.getReference(recaudoCollectionNewRecaudoToAttach.getClass(), recaudoCollectionNewRecaudoToAttach.getIdrecaudo());
                attachedRecaudoCollectionNew.add(recaudoCollectionNewRecaudoToAttach);
            }
            recaudoCollectionNew = attachedRecaudoCollectionNew;
            cliente.setRecaudoCollection(recaudoCollectionNew);
            cliente = em.merge(cliente);
            for (Factura facturaCollectionNewFactura : facturaCollectionNew) {
                if (!facturaCollectionOld.contains(facturaCollectionNewFactura)) {
                    Cliente oldIdclieOfFacturaCollectionNewFactura = facturaCollectionNewFactura.getIdclie();
                    facturaCollectionNewFactura.setIdclie(cliente);
                    facturaCollectionNewFactura = em.merge(facturaCollectionNewFactura);
                    if (oldIdclieOfFacturaCollectionNewFactura != null && !oldIdclieOfFacturaCollectionNewFactura.equals(cliente)) {
                        oldIdclieOfFacturaCollectionNewFactura.getFacturaCollection().remove(facturaCollectionNewFactura);
                        oldIdclieOfFacturaCollectionNewFactura = em.merge(oldIdclieOfFacturaCollectionNewFactura);
                    }
                }
            }
            for (Contrato contratoCollectionNewContrato : contratoCollectionNew) {
                if (!contratoCollectionOld.contains(contratoCollectionNewContrato)) {
                    Cliente oldIdclieOfContratoCollectionNewContrato = contratoCollectionNewContrato.getIdclie();
                    contratoCollectionNewContrato.setIdclie(cliente);
                    contratoCollectionNewContrato = em.merge(contratoCollectionNewContrato);
                    if (oldIdclieOfContratoCollectionNewContrato != null && !oldIdclieOfContratoCollectionNewContrato.equals(cliente)) {
                        oldIdclieOfContratoCollectionNewContrato.getContratoCollection().remove(contratoCollectionNewContrato);
                        oldIdclieOfContratoCollectionNewContrato = em.merge(oldIdclieOfContratoCollectionNewContrato);
                    }
                }
            }
            for (Recaudo recaudoCollectionNewRecaudo : recaudoCollectionNew) {
                if (!recaudoCollectionOld.contains(recaudoCollectionNewRecaudo)) {
                    Cliente oldIdclienteOfRecaudoCollectionNewRecaudo = recaudoCollectionNewRecaudo.getIdcliente();
                    recaudoCollectionNewRecaudo.setIdcliente(cliente);
                    recaudoCollectionNewRecaudo = em.merge(recaudoCollectionNewRecaudo);
                    if (oldIdclienteOfRecaudoCollectionNewRecaudo != null && !oldIdclienteOfRecaudoCollectionNewRecaudo.equals(cliente)) {
                        oldIdclienteOfRecaudoCollectionNewRecaudo.getRecaudoCollection().remove(recaudoCollectionNewRecaudo);
                        oldIdclienteOfRecaudoCollectionNewRecaudo = em.merge(oldIdclienteOfRecaudoCollectionNewRecaudo);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cliente.getIdcliente();
                if (findCliente(id) == null) {
                    throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cliente cliente;
            try {
                cliente = em.getReference(Cliente.class, id);
                cliente.getIdcliente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Factura> facturaCollectionOrphanCheck = cliente.getFacturaCollection();
            for (Factura facturaCollectionOrphanCheckFactura : facturaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cliente (" + cliente + ") cannot be destroyed since the Factura " + facturaCollectionOrphanCheckFactura + " in its facturaCollection field has a non-nullable idclie field.");
            }
            Collection<Contrato> contratoCollectionOrphanCheck = cliente.getContratoCollection();
            for (Contrato contratoCollectionOrphanCheckContrato : contratoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cliente (" + cliente + ") cannot be destroyed since the Contrato " + contratoCollectionOrphanCheckContrato + " in its contratoCollection field has a non-nullable idclie field.");
            }
            Collection<Recaudo> recaudoCollectionOrphanCheck = cliente.getRecaudoCollection();
            for (Recaudo recaudoCollectionOrphanCheckRecaudo : recaudoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cliente (" + cliente + ") cannot be destroyed since the Recaudo " + recaudoCollectionOrphanCheckRecaudo + " in its recaudoCollection field has a non-nullable idcliente field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cliente);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    public List<Cliente> findClienteEntities(int maxResults, int firstResult) {
        return findClienteEntities(false, maxResults, firstResult);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cliente.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Cliente findCliente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    public int getClienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cliente> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
