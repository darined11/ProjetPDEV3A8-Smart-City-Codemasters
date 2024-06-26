package tn.esprit.services.gestionShopping;



import tn.esprit.entities.gestionShopping.Article;
import tn.esprit.entities.gestionShopping.Commande;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommande implements IServiceCommande<Commande> {

    private Connection connection;

    public ServiceCommande() {
        connection = MyDataBase.getInstance().getConnection();
    }



    @Override
    public void ajouterCommande(Commande commande) throws SQLException {
        if (commande.getNombre_Article() < 1) {
            throw new IllegalArgumentException("Une commande doit contenir au moins un article.");
        }

        // Requête pour insérer une nouvelle commande avec l'ID de la personne du client
        String sql = "INSERT INTO commande (id_personne, nombre_article, prix_totale, delais_commande) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Insérer l'ID de la personne sélectionnée dans la commande
        preparedStatement.setInt(1, commande.getId_Personne()); // Supposons que vous avez une méthode getId_Personne() dans la classe Commande pour récupérer l'ID de la personne

        preparedStatement.setInt(2, commande.getNombre_Article());
        preparedStatement.setDouble(3, commande.getPrix_Totale());
        preparedStatement.setDate(4, new java.sql.Date(commande.getDelais_Commande().getTime()));
        preparedStatement.executeUpdate();

        // Récupérer l'ID de la commande nouvellement insérée
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        int id_Commande;
        if (generatedKeys.next()) {
            id_Commande = generatedKeys.getInt(1);
        } else {
            throw new SQLException("Échec de la création de la commande, aucun ID généré.");
        }

        // Insérer les ID des articles associés à la commande dans la table de liaison commande_article
        for (Article article : commande.getArticles()) {
            String insertCommandeArticleQuery = "INSERT INTO commande_article (id_commande, id_article) VALUES (?, ?)";
            PreparedStatement commandeArticleStatement = connection.prepareStatement(insertCommandeArticleQuery);
            commandeArticleStatement.setInt(1, id_Commande);
            commandeArticleStatement.setInt(2, article.getId_Article());
            commandeArticleStatement.executeUpdate();
        }
    }



    // Méthode pour mettre à jour l'ID de la commande dans les articles associés
    private void mettreAJourIdCommandeArticles(int idCommande, List<Integer> articleIds) throws SQLException {
        String updateQuery = "UPDATE article SET id_commande = ? WHERE id_article IN (";
        for (int i = 0; i < articleIds.size(); i++) {
            updateQuery += "?";
            if (i < articleIds.size() - 1) {
                updateQuery += ",";
            }
        }
        updateQuery += ")";

        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, idCommande);
            int parameterIndex = 2;
            for (int articleId : articleIds) {
                statement.setInt(parameterIndex++, articleId);
            }
            statement.executeUpdate();
        }
    }


    @Override
    public void modifierCommande(Commande commande) throws SQLException {
        String sql = "UPDATE commande SET nombre_article = ?, prix_totale = ?, delais_commande = ? WHERE id_commande = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, commande.getNombre_Article());
            preparedStatement.setDouble(2, commande.getPrix_Totale());
            preparedStatement.setDate(3, new java.sql.Date(commande.getDelais_Commande().getTime()));
            preparedStatement.setInt(4, commande.getId_Commande());
            preparedStatement.executeUpdate();
        }
    }

    public void supprimerArticlesDeCommande(Commande commande) throws SQLException {
        String sql = "DELETE FROM commande_article WHERE id_commande = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, commande.getId_Commande());
            preparedStatement.executeUpdate();
        }
    }

    // Insérer les nouveaux articles associés à la commande dans la table de jointure
    public void insererArticlesDeCommande(Commande commande) throws SQLException {
        String sql = "INSERT INTO commande_article (id_commande, id_article) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (Article article : commande.getArticles()) {
                preparedStatement.setInt(1, commande.getId_Commande());
                preparedStatement.setInt(2, article.getId_Article());
                preparedStatement.executeUpdate();
            }
        }
    }


    @Override
    public void supprimerCommande(int id_Commande) throws SQLException {
        // Supprimer la commande de la table de jointure commande_article
        String deleteCommandeArticleQuery = "DELETE FROM commande_article WHERE id_commande = ?";
        PreparedStatement deleteCommandeArticleStatement = connection.prepareStatement(deleteCommandeArticleQuery);
        deleteCommandeArticleStatement.setInt(1, id_Commande);
        deleteCommandeArticleStatement.executeUpdate();

        // Supprimer la commande
        String deleteCommandeQuery = "DELETE FROM commande WHERE id_commande = ?";
        PreparedStatement deleteCommandeStatement = connection.prepareStatement(deleteCommandeQuery);
        deleteCommandeStatement.setInt(1, id_Commande);
        deleteCommandeStatement.executeUpdate();
    }

    public List<Article> getArticlesDejaInseres(Commande commande) throws SQLException {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT a.* " +
                "FROM article a " +
                "INNER JOIN commande_article ca ON a.id_article = ca.id_article " +
                "WHERE ca.id_commande = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, commande.getId_Commande());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Article article = new Article();
                    article.setId_Article(resultSet.getInt("id_article"));
                    article.setNom_Article(resultSet.getString("nom_article"));
                    article.setPrix_Article(resultSet.getDouble("prix_article"));
                    // Ajoutez d'autres attributs si nécessaire
                    articles.add(article);
                }
            }
        }
        return articles;
    }

    public List<Article> getAllArticles() throws SQLException {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Article article = new Article();
                article.setId_Article(resultSet.getInt("id_article"));
                article.setNom_Article(resultSet.getString("nom_article"));
                article.setPrix_Article(resultSet.getDouble("prix_article"));
                // Ajoutez d'autres attributs si nécessaire
                articles.add(article);
            }
        }
        return articles;
    }

    public List<Article> getArticlesByCommande(Commande commande) throws SQLException {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT a.* " +
                "FROM article a " +
                "INNER JOIN commande_article ca ON a.id_article = ca.id_article " +
                "WHERE ca.id_commande = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, commande.getId_Commande());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Article article = new Article();
                    article.setId_Article(resultSet.getInt("id_article"));
                    article.setNom_Article(resultSet.getString("nom_article"));
                    article.setPrix_Article(resultSet.getDouble("prix_article"));
                    // Ajoutez d'autres attributs si nécessaire
                    articles.add(article);
                }
            }
        }
        return articles;
    }



    @Override
    public List<Commande> afficherCommande() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.id_commande, c.nombre_article, c.prix_totale, c.delais_commande, u.id, u.nom_personne, u.prenom_personne, GROUP_CONCAT(a.id_article) as id_articles, GROUP_CONCAT(a.nom_article) as nom_articles " +
                "FROM commande c " +
                "INNER JOIN commande_article ca ON c.id_commande = ca.id_commande " +
                "INNER JOIN article a ON ca.id_article = a.id_article " +
                "INNER JOIN user u ON c.id_personne = u.id " + // Utilisation de la table user au lieu de client
                "GROUP BY c.id_commande";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Commande commande = new Commande();
                commande.setId_Commande(rs.getInt("id_commande"));
                commande.setNombre_Article(rs.getInt("nombre_article"));
                commande.setPrix_Totale(rs.getDouble("prix_totale"));
                commande.setDelais_Commande(rs.getDate("delais_commande"));
                commande.setId_Personne(rs.getInt("id"));
                commande.setNom_Personne(rs.getString("nom_personne"));
                commande.setPrenom_Personne(rs.getString("prenom_personne"));

                String[] idArticles = rs.getString("id_articles").split(",");
                String[] nomArticles = rs.getString("nom_articles").split(",");
                List<Article> articles = new ArrayList<>();
                for (int i = 0; i < idArticles.length; i++) {
                    Article article = new Article();
                    article.setId_Article(Integer.parseInt(idArticles[i]));
                    article.setNom_Article(nomArticles[i]);
                    articles.add(article);
                }
                commande.setArticles(articles);
                commandes.add(commande);
            }
        }
        return commandes;
    }






}
