/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import entities.Adresse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.PersistenceContext;
import session.AdresseManager;
import session.EntrepriseManager;
import session.UtilisateurManager;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import panier.Panier;
import session.CouponManager;
import session.OffreManager;

/**
 *
 * @author John624
 */
@ManagedBean
@Named(value = "sessionMBean")
@SessionScoped
public class SessionMBean implements Serializable {

    public enum State {

        NotConnected, ConnectedAsCustomer, ConnectedAsOrganization, ConnectedAsAdmin
    }

    private State state;
    private Integer idUser;
    private String loginName;

    private Panier panier;

    @EJB
    private UtilisateurManager UM;
    @EJB
    private EntrepriseManager EM;
    @EJB
    private CouponManager CM;
    @EJB
    private OffreManager OM;
    

    public SessionMBean() {
        this.state = State.NotConnected;
        this.loginName = null;
        this.idUser = null;
        UM = new UtilisateurManager();
        EM = new EntrepriseManager();
        CM = new CouponManager();
        OM = new OffreManager();
        panier = new Panier();
    }

    /**
     * returns customer list for display in a datatable DataTable
     *
     * @return
     */
    public String userConnect(String login, String password) {
        if (UM.authUtilisateur(login, password)) {
            this.loginName = login;
            this.state = State.ConnectedAsCustomer;
            return "index";
        } else {
            if (EM.authEntreprise(login, password)) {
                this.loginName = login;
                this.state = State.ConnectedAsOrganization;
                panier = null;
                return "offresEntreprise";
            }
        }
        return "#";
    }

    public String disconnect() {
        panier = null;
        return "index";
    }

    public String adminConnect(String login, String password) {
        return null;
    }

    public String getState() {
        return this.state.toString();
    }

    public Boolean isConnectedAsCustomer() {
        return this.state == State.ConnectedAsCustomer;
    }

    public Boolean isConnectedAsOrganization() {
        return this.state == State.ConnectedAsOrganization;
    }

    public String getLogin() {
        return this.loginName;
    }

    public String ajouterOffreAuPanier(int idOffre) {
        String[] res = CM.getFirstAvailableForOffre(idOffre).split("\\|");
        long idCoupon = Long.parseLong(res[0]);
        double prix = Double.parseDouble(res[1]);
        if (idCoupon == -1) {
            return "#";
        }
        panier.addCoupon(idOffre, idCoupon, prix);
        return "#";
    }

    public String retirerOffreDuPanier(int idOffre) {
        String[]res = OM.getTitreEtPrixById(idOffre).split("\\|");
        panier.removeOne(idOffre, Double.parseDouble(res[1]));
        return "#";
    }
    
    // Titre|Prix|Quantité
    public List<String> getOffresPanier(){
        ArrayList<String> res = new ArrayList<String>();
        for(Integer i : panier.getOffres()){
            res.add(OM.getTitreEtPrixById(i) +"|"+panier.getQuantity(i));
        }
        return res;
    }
    
    public double getPrixTotalPanier(){
        return panier.getPrixTotal();
    }
}
