package tn.esprit.controllers.gestionShopping;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import tn.esprit.entities.gestionShopping.Article;
import tn.esprit.services.gestionShopping.ServiceCommande;
import tn.esprit.entities.gestionShopping.Commande;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class AfficherCommandeController implements ModificationListener {

    @FXML
    private VBox itemsContainer; // Container pour afficher les éléments (articles et commandes)

    @FXML
    private ComboBox<String> comboBoxTri; // ComboBox pour trier les commandes

    private final ServiceCommande serviceCommande = new ServiceCommande();

    @FXML
    void initialize() {
        // Initialisation de la ComboBox de tri
        comboBoxTri.getItems().addAll("Date de livraison (récent)", "Date de livraison (ancien)");

        afficherCommandes();
    }

    private ImageView generateQRCodeCommande(String text, int width, int height) {
        try {
            // Créer un objet MultiFormatWriter
            MultiFormatWriter writer = new MultiFormatWriter();

            // Paramètres pour la génération du code QR
            final int WHITE = 0xFFFFFFFF;
            final int BLACK = 0xFF000000;
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);

            // Créer une image WritableImage pour afficher le code QR
            WritableImage image = new WritableImage(width, height);
            PixelWriter pixelWriter = image.getPixelWriter();

            // Écrire les pixels en fonction de la matrice de bits
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixelWriter.setArgb(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
                }
            }

            // Créer un ImageView pour afficher l'image WritableImage
            ImageView imageView = new ImageView(image);
            return imageView;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    void trierCommandes() {
        String triSelectionne = comboBoxTri.getValue();
        if (triSelectionne.equals("Date de livraison (récent)")) {
            // Tri des commandes par date de livraison la plus récente
            afficherCommandesTriees(Comparator.comparing(Commande::getDelais_Commande).reversed());
        } else if (triSelectionne.equals("Date de livraison (ancien)")) {
            // Tri des commandes par date de livraison la moins récente
            afficherCommandesTriees(Comparator.comparing(Commande::getDelais_Commande));
        }
    }

    private void afficherCommandesTriees(Comparator<Commande> comparator) {
        try {
            itemsContainer.getChildren().clear(); // Efface les anciennes commandes pour éviter les duplications
            List<Commande> commandes = serviceCommande.afficherCommande();
            commandes.stream()
                    .sorted(comparator)
                    .forEach(commande -> {
                        VBox commandeBox = createCommandeBox(commande);
                        itemsContainer.getChildren().add(commandeBox);
                    });
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des commandes : " + e.getMessage());
        }
    }

    private void afficherCommandes() {
        afficherCommandesTriees(Comparator.comparing(Commande::getDelais_Commande).reversed());
    }

    private void genererBonDeCommandePDF(Commande commande) {
        try {
            String fileName = "BonDeCommande_" + commande.getId_Commande() + ".pdf"; // Nom du fichier avec l'ID de commande
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Logo de l'entreprise
//            PDImageXObject image = PDImageXObject.createFromFile("C:\\Users\\Ezzedine\\Desktop\\ProjetPDEV3A8-Smart-City-Codemasters\\src\\main\\resources\\resourcesGestionShopping\\logo.png", document);
            float imageWidth = 100; // Largeur de l'image
            float imageHeight = 100; // Hauteur de l'image
            float imageX = 50; // Position horizontale du logo
            float imageY = page.getMediaBox().getHeight() - 150; // Position verticale du logo
        //    contentStream.drawImage(image, imageX, imageY, imageWidth, imageHeight);

// Entête de l'entreprise
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.setNonStrokingColor(0, 0, 0); // Couleur du texte : Noir






            // Titre de la commande
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.setNonStrokingColor(255, 0, 0);
            contentStream.beginText();
            contentStream.newLineAtOffset(page.getMediaBox().getWidth() / 2 - 50, page.getMediaBox().getHeight() - 150);
            contentStream.showText("Bon de Commande");
            contentStream.endText();

            // Ajouter un cadre autour de la page
            contentStream.setStrokingColor(0, 0, 0); // Couleur du cadre : Noir
            contentStream.addRect(50, 50, page.getMediaBox().getWidth() - 100, page.getMediaBox().getHeight() - 100);
            contentStream.stroke();

            // Ajouter les détails de la commande
            contentStream.setFont(PDType1Font.HELVETICA, 20);
            contentStream.setNonStrokingColor(0, 0, 0); // Couleur du texte : Noir
            contentStream.beginText();
            contentStream.newLineAtOffset(100, page.getMediaBox().getHeight() - 200);
            contentStream.showText("Référence Commande : " + commande.getId_Commande());
            contentStream.newLineAtOffset(0, -30); // Retour à la ligne
            // Ajouter les informations du client
            contentStream.showText("ID du client : " + commande.getId_Personne());
            contentStream.newLineAtOffset(0, -30);
            contentStream.showText("Nom du client : " + commande.getNom_Personne());
            contentStream.newLineAtOffset(0, -30);

            contentStream.showText("Nombre des articles commandés : " + commande.getNombre_Article());
            contentStream.newLineAtOffset(0, -30); // Retour à la ligne
            contentStream.showText("Prix total de la commande : " + commande.getPrix_Totale() + "DT");
            contentStream.newLineAtOffset(0, -30); // Retour à la ligne
            contentStream.showText("Date de livraison : " + commande.getDelais_Commande());
            contentStream.endText();

            contentStream.close();

            document.save("C:\\Users\\Ezzedine\\Desktop\\BonDeCommande"+commande.getId_Commande()+".pdf");

            document.close();

            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Le bon de commande a été téléchargé avec succès.");

        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la génération du bon de commande PDF.");
        }
    }


    private VBox createCommandeBox(Commande commande) {
        VBox commandeBox = new VBox();
        commandeBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 12px;");

        // Récupérer les détails de la personne associée à la commande
        int idPersonne = commande.getId_Personne(); // Supposons que vous avez une méthode getId_Personne() dans la classe Commande pour récupérer l'ID de la personne
        String nomPersonne = commande.getNom_Personne();
        String prenomPersonne = commande.getPrenom_Personne();

        Label idPersonneLabel = new Label("ID de la personne : " + idPersonne);
        idPersonneLabel.setStyle("-fx-font-size: 14px;");

        Label nomLabel = new Label("Nom de client : " + nomPersonne);
        nomLabel.setStyle("-fx-font-size: 14px;");


        // Création du code QR pour la commande
        // Modification du texte pour inclure les informations du client
        ImageView qrCodeImageView = generateQRCodeCommande("ID de la commande : " + commande.getId_Commande() + " - ID du client : " + commande.getId_Personne() + " - Nom du client : " + commande.getNom_Personne() + " - Prix total : " + commande.getPrix_Totale() + " - Date de livraison : " + commande.getDelais_Commande() + " - Nombre d'articles : " + commande.getNombre_Article(), 200, 200);
        qrCodeImageView.setFitWidth(100);
        qrCodeImageView.setFitHeight(100);

        Label idLabel = new Label("Référence Commande : " + commande.getId_Commande());
        idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label nombreArticleLabel = new Label("Nombre d'articles : " + commande.getNombre_Article());
        nombreArticleLabel.setStyle("-fx-font-size: 14px;");

        Label prixLabel = new Label("Prix total : " + commande.getPrix_Totale() + "DT");
        prixLabel.setStyle("-fx-font-size: 14px;");

        Label delaisLabel = new Label("Date de livraison de commande : " + commande.getDelais_Commande());
        delaisLabel.setStyle("-fx-font-size: 14px;");

        Separator separator = new Separator();
        VBox.setMargin(separator, new Insets(5, 0, 10, 0)); // Espacement autour du séparateur

        VBox articlesBox = new VBox();
        articlesBox.setSpacing(5);
        articlesBox.setStyle("-fx-padding: 10px;");

        Label articlesLabel = new Label("Articles :");
        articlesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        for (Article article : commande.getArticles()) {
            HBox articleBox = createArticleBox(article);
            articlesBox.getChildren().add(articleBox);
        }

        Button modifierButton = new Button("Modifier");
        modifierButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        modifierButton.setOnAction(event -> {
            ouvrirPageModificationCommande(commande);
        });

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        deleteButton.setOnAction(event -> {
            // Créer un dialogue de confirmation
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de suppression");
            confirmationAlert.setHeaderText("Suppression de commande");
            confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette commande ?");

            // Option pour confirmer ou annuler la suppression
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        serviceCommande.supprimerCommande(commande.getId_Commande());
                        afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "La commande a été supprimée avec succès.");
                        afficherCommandes();
                    } catch (SQLException e) {
                        afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de la commande : " + e.getMessage());
                    }
                }
            });
        });

        Button genererPDFButton = new Button("Télécharger PDF");
        genererPDFButton.setStyle("-fx-background-color: #0059ff; -fx-text-fill: white; -fx-font-size: 14px;");
        genererPDFButton.setOnAction(event -> {
            //genererBonDeCommandePDF(commande);
        });
        VBox.setMargin(genererPDFButton, new Insets(10, 0, 0, 0)); // Ajout de marge en haut pour le bouton PDF

        HBox buttonBox = new HBox(modifierButton, deleteButton, genererPDFButton);
        buttonBox.setSpacing(10);

        commandeBox.getChildren().addAll(qrCodeImageView, idPersonneLabel, nomLabel, idLabel, nombreArticleLabel, prixLabel, delaisLabel, separator, articlesLabel, articlesBox, buttonBox);
        return commandeBox;
    }

    private HBox createArticleBox(Article article) {
        HBox articleBox = new HBox();
        articleBox.setSpacing(10);

        Label articleLabel = new Label(article.getNom_Article());
        articleLabel.setStyle("-fx-font-size: 14px;");

        articleBox.getChildren().add(articleLabel);
        return articleBox;
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    private void ouvrirPageModificationCommande(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resourcesGestionShopping/ModifierCommande.fxml"));
            Parent root = loader.load();
            ModifierCommandeController modifierCommandeController = loader.getController();
            modifierCommandeController.setCommande(commande);
            modifierCommandeController.setModificationListener(this);

            // Initialiser la référence stage
            modifierCommandeController.setStage(new Stage());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onModification() {
        afficherCommandes();
    }
}
