package ca.uqac.csclight;

/**
 * Created by Osman on 29/11/2017.
 */

public class Contact {

    String nom;
    String prenom;
    String tel;
    String mail;

    public Contact(String nom, String prenom,String mail, String tel) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.tel = tel;

    }

    public String getNom() {return nom;}

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}

