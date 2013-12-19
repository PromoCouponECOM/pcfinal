/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import entities.Adresse;
import entities.Utilisateur;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import session.AdresseManager;
import session.UtilisateurManager;

/**
 *
 * @author liu
 */
@Named(value = "inscriptionUserMBean")
@RequestScoped
public class InscriptionUserMBean implements Serializable {
    @ManagedProperty("#{sessionMBean}")
    private SessionMBean sm;
    private Map<String, String> settings;
    @EJB
    private AdresseManager adrM;
    @EJB
    private UtilisateurManager userM;
    private Adresse adr;
    private Utilisateur user;

    public InscriptionUserMBean() {
        settings = new HashMap<String, String>();
        adr = new Adresse();
        user = new Utilisateur();
    }

    public void init() {
        adrM.getAllAdresses();
        userM.getAllUtilisateurs();
    }

    public String save() {
        if (userM.emailUsed(settings.get("mail"))) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email erreur", "Email utilisé pour inscription"));
            return "signup";
        }

        adr.setIdAdresse(adrM.nextId());
        adr.setNumEtRue(settings.get("rue"));
        adr.setComple(settings.get("compl"));
        adr.setVille(settings.get("ville"));
        adr.setPays(settings.get("pays"));
        adr.setCodePostale(settings.get("code"));
        adr.setDateModif(new Date());
        adrM.update(adr);

        user.setIdU(userM.nextId());
        user.setNomU(settings.get("nom"));
        user.setPrenomU(settings.get("prenom"));
        user.setSexe(settings.get("sexe").charAt(0));
        user.setPassU(settings.get("pwd"));
        user.setMailU(settings.get("mail"));
        user.setTelU(settings.get("tel"));
        user.setAdrU(adr);
        user.setDataModif(new Date());
        userM.update(user);
        // do sign in 
        sm.userConnect(user.getMailU(), user.getPassU());
        userM.authUtilisateur(user.getMailU(),user.getPassU() );
        System.out.println(sm.isConnectedAsCustomer());
        return "index";
    }

    public Map<String, String> getSettings() {
        return settings;
    }
}
