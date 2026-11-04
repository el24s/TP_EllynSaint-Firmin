package ca.cegep.biblio.service;

import ca.cegep.biblio.controller.LivreRequest;
import ca.cegep.biblio.exception.LivreIntrouvableException;
import ca.cegep.biblio.model.Livre;
import ca.cegep.biblio.repository.LivreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Logique métier de la bibliothèque. */
@Service
public class LivreService {

    private final LivreRepository repository;

    public LivreService(LivreRepository repository) {
        this.repository = repository;
    }

    public List<Livre> lister() {
        return repository.findAll();
    }

    public Livre trouver(Long id) {
        return repository.findById(id).orElseThrow(() -> new LivreIntrouvableException(id));
    }

    public void verificationBase(LivreRequest request) {
       if (request.titre() == null || request.titre().isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        if (request.auteur() == null || request.auteur().isBlank()) {
            throw new IllegalArgumentException("L'auteur est obligatoire");
        }
        if (request.anneePublication() < 1450 || request.anneePublication() > 2100) {
            throw new IllegalArgumentException("L'année de publication est invalide");
        } 
    }

    public Livre creer(LivreRequest request) {
        verificationBase(request);

        Livre livre = new Livre(null, request.titre(), request.auteur(), request.isbn(), request.anneePublication(), request.categorie());
        return repository.save(livre);
    }

    public Livre modifier(Long id, LivreRequest request) {
        Livre livre = trouver(id);
        verificationBase(request);

        livre.setTitre(request.titre());
        livre.setAuteur(request.auteur());
        livre.setIsbn(request.isbn());
        livre.setAnneePublication(request.anneePublication());
        livre.setCategorie(request.categorie());

        return repository.save(livre);
    }

    public void supprimer(Long id) {
        trouver(id);
        repository.deleteById(id);
    }

    public Livre emprunter(Long id) {
        Livre livre = trouver(id);
        if (!livre.isDisponible()) {
            throw new IllegalStateException("Ce livre est déjà emprunté");
        }
        livre.setDisponible(false);
        return repository.save(livre);
    }

    public Livre retourner(Long id) {
        Livre livre = trouver(id);
        if (livre.isDisponible()) {
            throw new IllegalStateException("Ce livre n'est pas emprunté");
        }
        livre.setDisponible(true);
        return repository.save(livre);
    }

    public double calculerFraisRetard(String categorie, int joursRetard) {
        double frais = 0;
        if (joursRetard > 0) {
            if (categorie.equals("JEUNESSE")) {
                frais = joursRetard * 0.10;
                if (frais > 5) {
                    frais = 5;
                }
            } else if (categorie.equals("SCIENCE")) {
                frais = joursRetard * 0.50;
                if (frais > 20) {
                    frais = 20;
                }
            } else {
                frais = joursRetard * 0.25;
                if (frais > 10) {
                    frais = 10;
                }
            }
        }
        return frais;
    }
}
