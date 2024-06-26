package tn.esprit.entities.gestionUserEntities;

public class Client extends Personne {

    private String genre;
    private int age;

    public Client() {
    }

    public Client(String genre, int age) {
        this.genre = genre;
        this.age = age;
    }


    public Client(int id_personne, String nom_personne, String prenom_personne, int numero_telephone, String mail_personne, String mdp_personne, String image, String genre, int age) {
        super(id_personne, nom_personne, prenom_personne, numero_telephone, mail_personne, mdp_personne, image);
        this.genre = genre;
        this.age = age;
    }


    public Client(String nom_personne, String prenom_personne, int numero_telephone, String mail_personne, String mdp_personne, String image_personne, String genre, int age) {
        super(nom_personne, prenom_personne, numero_telephone, mail_personne, mdp_personne, image_personne);
        this.genre = genre;
        this.age = age;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return getNom_personne()+" "+getPrenom_personne();
    }

}
