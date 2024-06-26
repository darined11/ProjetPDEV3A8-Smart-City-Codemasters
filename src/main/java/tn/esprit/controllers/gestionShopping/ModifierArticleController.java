package tn.esprit.controllers.gestionShopping;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import tn.esprit.entities.gestionShopping.Article;
import tn.esprit.services.gestionShopping.ServiceArticle;


import java.io.File;
import java.sql.SQLException;
import java.util.function.UnaryOperator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ModifierArticleController {

    @FXML
    private TextField nomArticleTextField;

    @FXML
    private TextField prixArticleTextField;

    @FXML
    private TextField quantiteArticleTextField;

    @FXML
    private ComboBox<Article.TypeArticle> typeArticleCB;

    @FXML
    private TextField descriptionArticleTextField;

    private final ServiceArticle serviceArticle = new ServiceArticle();
    private Article article;
    private ModificationListener modificationListener;
    @FXML
    void initialize() {

        // Limiter la saisie de quantité à des entiers uniquement

        quantiteArticleTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, new UnaryOperator<TextFormatter.Change>() {

            @Override

            public TextFormatter.Change apply(TextFormatter.Change change) {

                if (change.isContentChange()) {

                    if (!change.getControlNewText().matches("\\d*")) {

                        change.setText("");

                    }

                }

                return change;

            }

        }));

        typeArticleCB.getItems().addAll(Article.TypeArticle.values());
        quantiteArticleTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                if (change.isContentChange()) {
                    if (!change.getControlNewText().matches("\\d*")) {
                        change.setText("");
                    }
                }
                return change;
            }
        }));

    }


    public void setArticle(Article article) {
        this.article = article;
        afficherDetailsArticle();
    }

    private void afficherDetailsArticle() {
        if (article != null) {
            nomArticleTextField.setText(article.getNom_Article());
            prixArticleTextField.setText(String.valueOf(article.getPrix_Article()));
            quantiteArticleTextField.setText(String.valueOf(article.getQuantite_Article()));
            typeArticleCB.setValue(article.getType_article());
            descriptionArticleTextField.setText(article.getDescription_article());
        }
    }



    @FXML
    void modifierArticle() {
        try {
            String nomArticle = nomArticleTextField.getText().trim();
            String prixText = prixArticleTextField.getText().trim();
            String quantiteText = quantiteArticleTextField.getText().trim();
            String descriptionArticle = descriptionArticleTextField.getText().trim();
            Article.TypeArticle typeArticle = typeArticleCB.getValue();

            if (nomArticle.isEmpty() || prixText.isEmpty() || quantiteText.isEmpty() || descriptionArticle.isEmpty() || typeArticle == null) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            double prixArticle = Double.parseDouble(prixText);
            int quantiteArticle = Integer.parseInt(quantiteText);

            if (article != null && typeArticle != null) {
                Article nouvelArticle = new Article(
                        article.getId_Article(),
                        nomArticle,
                        prixArticle,
                        quantiteArticle,
                        typeArticle,
                        descriptionArticle,
                        article.getPhoto_article(),
                        article.getCommandes()
                );
                serviceArticle.modifierArticle(nouvelArticle);
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "L'article a été modifié avec succès.");
                if (modificationListener != null) {
                    modificationListener.onModification();
                }
                // Fermer la fenêtre de modification après la réussite de la modification
                Stage stage = (Stage) nomArticleTextField.getScene().getWindow();
                stage.close();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Aucun article sélectionné ou type d'article non spécifié.");
            }
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir des valeurs valides pour le prix et la quantité.");
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la modification de l'article : " + e.getMessage());
        }
    }



    public void setModificationListener(ModificationListener modificationListener) {
        this.modificationListener = modificationListener;
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
    private String cheminPhotoSelectionne = "";
    private final String CHEMIN_HTDOCS = "C:/xampp/htdocs"; // Chemin vers htdocs
    private final String URL_HTDOCS = "http://localhost/"; // URL de base pour accéder à htdocs

    @FXML
    void modifierPhotoArticle() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Modifier la photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // Copier l'image dans le répertoire htdocs
            String nomFichier = selectedFile.getName();
            Path destination = Paths.get(CHEMIN_HTDOCS, "img", nomFichier);
            try {
                Files.copy(selectedFile.toPath(), destination);
                // Mettre à jour le champ de texte de la photo avec le nouveau chemin de la photo sélectionnée
                article.setPhoto_article(URL_HTDOCS + "img/" + nomFichier);
            } catch (IOException e) {
                e.printStackTrace();
                // Gérer l'exception si nécessaire
            }
        }
    }



}
