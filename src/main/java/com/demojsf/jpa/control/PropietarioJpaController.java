/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demojsf.jpa.control;

import com.demojsf.jpa.control.exceptions.IllegalOrphanException;
import com.demojsf.jpa.control.exceptions.NonexistentEntityException;
import com.demojsf.jpa.control.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.demojsf.jpa.model.Comision;
import java.util.ArrayList;
import java.util.Collection;
import com.demojsf.jpa.model.Propiedad;
import com.demojsf.jpa.model.Propietario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Carlos
 */
public class PropietarioJpaController implements Serializable {

    public PropietarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Propietario propietario) throws RollbackFailureException, Exception {
        if (propietario.getComisionCollection() == null) {
            propietario.setComisionCollection(new ArrayList<Comision>());
        }
        if (propietario.getPropiedadCollection() == null) {
            propietario.setPropiedadCollection(new ArrayList<Propiedad>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Comision> attachedComisionCollection = new ArrayList<Comision>();
            for (Comision comisionCollectionComisionToAttach : propietario.getComisionCollection()) {
                comisionCollectionComisionToAttach = em.getReference(comisionCollectionComisionToAttach.getClass(), comisionCollectionComisionToAttach.getIdcomision());
                attachedComisionCollection.add(comisionCollectionComisionToAttach);
            }
            propietario.setComisionCollection(attachedComisionCollection);
            Collection<Propiedad> attachedPropiedadCollection = new ArrayList<Propiedad>();
            for (Propiedad propiedadCollectionPropiedadToAttach : propietario.getPropiedadCollection()) {
                propiedadCollectionPropiedadToAttach = em.getReference(propiedadCollectionPropiedadToAttach.getClass(), propiedadCollectionPropiedadToAttach.getPropiedadPK());
                attachedPropiedadCollection.add(propiedadCollectionPropiedadToAttach);
            }
            propietario.setPropiedadCollection(attachedPropiedadCollection);
            em.persist(propietario);
            for (Comision comisionCollectionComision : propietario.getComisionCollection()) {
                Propietario oldIdpropietarioOfComisionCollectionComision = comisionCollectionComision.getIdpropietario();
                comisionCollectionComision.setIdpropietario(propietario);
                comisionCollectionComision = em.merge(comisionCollectionComision);
                if (oldIdpropietarioOfComisionCollectionComision != null) {
                    oldIdpropietarioOfComisionCollectionComision.getComisionCollection().remove(comisionCollectionComision);
                    oldIdpropietarioOfComisionCollectionComision = em.merge(oldIdpropietarioOfComisionCollectionComision);
                }
            }
            for (Propiedad propiedadCollectionPropiedad : propietario.getPropiedadCollection()) {
                Propietario oldIdpropietarioOfPropiedadCollectionPropiedad = propiedadCollectionPropiedad.getIdpropietario();
                propiedadCollectionPropiedad.setIdpropietario(propietario);
                propiedadCollectionPropiedad = em.merge(propiedadCollectionPropiedad);
                if (oldIdpropietarioOfPropiedadCollectionPropiedad != null) {
                    oldIdpropietarioOfPropiedadCollectionPropiedad.getPropiedadCollection().remove(propiedadCollectionPropiedad);
                    oldIdpropietarioOfPropiedadCollectionPropiedad = em.merge(oldIdpropietarioOfPropiedadCollectionPropiedad);
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

    public void edit(Propietario propietario) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Propietario persistentPropietario = em.find(Propietario.class, propietario.getIdpropietario());
            Collection<Comision> comisionCollectionOld = persistentPropietario.getComisionCollection();
            Collection<Comision> comisionCollectionNew = propietario.getComisionCollection();
            Collection<Propiedad> propiedadCollectionOld = persistentPropietario.getPropiedadCollection();
            Collection<Propiedad> propiedadCollectionNew = propietario.getPropiedadCollection();
            List<String> illegalOrphanMessages = null;
            for (Comision comisionCollectionOldComision : comisionCollectionOld) {
                if (!comisionCollectionNew.contains(comisionCollectionOldComision)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comision " + comisionCollectionOldComision + " since its idpropietario field is not nullable.");
                }
            }
            for (Propiedad propiedadCollectionOldPropiedad : propiedadCollectionOld) {
                if (!propiedadCollectionNew.contains(propiedadCollectionOldPropiedad)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Propiedad " + propiedadCollectionOldPropiedad + " since its idpropietario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Comision> attachedComisionCollectionNew = new ArrayList<Comision>();
            for (Comision comisionCollectionNewComisionToAttach : comisionCollectionNew) {
                comisionCollectionNewComisionToAttach = em.getReference(comisionCollectionNewComisionToAttach.getClass(), comisionCollectionNewComisionToAttach.getIdcomision());
                attachedComisionCollectionNew.add(comisionCollectionNewComisionToAttach);
            }
            comisionCollectionNew = attachedComisionCollectionNew;
            propietario.setComisionCollection(comisionCollectionNew);
            Collection<Propiedad> attachedPropiedadCollectionNew = new ArrayList<Propiedad>();
            for (Propiedad propiedadCollectionNewPropiedadToAttach : propiedadCollectionNew) {
                propiedadCollectionNewPropiedadToAttach = em.getReference(propiedadCollectionNewPropiedadToAttach.getClass(), propiedadCollectionNewPropiedadToAttach.getPropiedadPK());
                attachedPropiedadCollectionNew.add(propiedadCollectionNewPropiedadToAttach);
            }
            propiedadCollectionNew = attachedPropiedadCollectionNew;
            propietario.setPropiedadCollection(propiedadCollectionNew);
            propietario = em.merge(propietario);
            for (Comision comisionCollectionNewComision : comisionCollectionNew) {
                if (!comisionCollectionOld.contains(comisionCollectionNewComision)) {
                    Propietario oldIdpropietarioOfComisionCollectionNewComision = comisionCollectionNewComision.getIdpropietario();
                    comisionCollectionNewComision.setIdpropietario(propietario);
                    comisionCollectionNewComision = em.merge(comisionCollectionNewComision);
                    if (oldIdpropietarioOfComisionCollectionNewComision != null && !oldIdpropietarioOfComisionCollectionNewComision.equals(propietario)) {
                        oldIdpropietarioOfComisionCollectionNewComision.getComisionCollection().remove(comisionCollectionNewComision);
                        oldIdpropietarioOfComisionCollectionNewComision = em.merge(oldIdpropietarioOfComisionCollectionNewComision);
                    }
                }
            }
            for (Propiedad propiedadCollectionNewPropiedad : propiedadCollectionNew) {
                if (!propiedadCollectionOld.contains(propiedadCollectionNewPropiedad)) {
                    Propietario oldIdpropietarioOfPropiedadCollectionNewPropiedad = propiedadCollectionNewPropiedad.getIdpropietario();
                    propiedadCollectionNewPropiedad.setIdpropietario(propietario);
                    propiedadCollectionNewPropiedad = em.merge(propiedadCollectionNewPropiedad);
                    if (oldIdpropietarioOfPropiedadCollectionNewPropiedad != null && !oldIdpropietarioOfPropiedadCollectionNewPropiedad.equals(propietario)) {
                        oldIdpropietarioOfPropiedadCollectionNewPropiedad.getPropiedadCollection().remove(propiedadCollectionNewPropiedad);
                        oldIdpropietarioOfPropiedadCollectionNewPropiedad = em.merge(oldIdpropietarioOfPropiedadCollectionNewPropiedad);
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
                Integer id = propietario.getIdpropietario();
                if (findPropietario(id) == null) {
                    throw new NonexistentEntityException("The propietario with id " + id + " no longer exists.");
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
            Propietario propietario;
            try {
                propietario = em.getReference(Propietario.class, id);
                propietario.getIdpropietario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The propietario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Comision> comisionCollectionOrphanCheck = propietario.getComisionCollection();
            for (Comision comisionCollectionOrphanCheckComision : comisionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Propietario (" + propietario + ") cannot be destroyed since the Comision " + comisionCollectionOrphanCheckComision + " in its comisionCollection field has a non-nullable idpropietario field.");
            }
            Collection<Propiedad> propiedadCollectionOrphanCheck = propietario.getPropiedadCollection();
            for (Propiedad propiedadCollectionOrphanCheckPropiedad : propiedadCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Propietario (" + propietario + ") cannot be destroyed since the Propiedad " + propiedadCollectionOrphanCheckPropiedad + " in its propiedadCollection field has a non-nullable idpropietario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(propietario);
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

    public List<Propietario> findPropietarioEntities() {
        return findPropietarioEntities(true, -1, -1);
    }

    public List<Propietario> findPropietarioEntities(int maxResults, int firstResult) {
        return findPropietarioEntities(false, maxResults, firstResult);
    }

    private List<Propietario> findPropietarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Propietario.class));
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

    public Propietario findPropietario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Propietario.class, id);
        } finally {
            em.close();
        }
    }

    public int getPropietarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Propietario> rt = cq.from(Propietario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
