/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entities.Entreprise;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author John624
 */
@Stateless
@LocalBean
public class EntrepriseManager {

    @PersistenceContext(unitName = "PromoCoupon-ejbPU")
    private EntityManager em;

    public List<Entreprise> getAllEntreprise() {
        Query query = em.createNamedQuery("Entreprise.findAll");
        return query.getResultList();
    }
    public Boolean authEntreprise(String login, String password){
        Query query = em.createNamedQuery("Entreprise.findByMailE");
        query.setParameter("mailE", login);
        List<Entreprise> le = query.getResultList();
        for(Entreprise e : le)
            if(e.getPassE().equals(password))
                return true;
        return false;
    }
    public Entreprise update(Entreprise entreprise) {
        return em.merge(entreprise);
    }

    public void persist(Object object) {
        em.persist(object);
    }
    
    public Integer nextId(){
        Query query = em.createNamedQuery("Entreprise.maxId");
        Integer res = (Integer)query.getResultList().get(0);
        if(res==null)
            return new Integer(0);
        return res+1;
    }

    public Entreprise getEntrepriseByName(String entrerpise) {
        Query query = em.createNamedQuery("Entreprise.findByNomE");
        query.setParameter("nomE", entrerpise);
        Entreprise res = (Entreprise)query.getResultList().get(0);        
        return res;
    }
        public boolean emailUsed(String mail) {
        Query query = em.createNamedQuery("Entreprise.findByMailE");
        query.setParameter("mailE", mail);
        return !query.getResultList().isEmpty();
    }
}
