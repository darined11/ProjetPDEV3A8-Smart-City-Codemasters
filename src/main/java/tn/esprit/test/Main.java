package tn.esprit.test;
import tn.esprit.services.ServiceMedecin;
import tn.esprit.services.ServiceRendezVous;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        ServiceRendezVous serviceRendezVous = new ServiceRendezVous();
        System.out.println(serviceRendezVous.getAllDateRendezVousByidMedeicn(24));
    }
}