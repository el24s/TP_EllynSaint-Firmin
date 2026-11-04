package ca.cegep.biblio.service;

import ca.cegep.biblio.dto.LivreRequest;
import ca.cegep.biblio.exception.LivreIntrouvableException;
import ca.cegep.biblio.model.Livre;
import ca.cegep.biblio.model.LivreCategorie;
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

    public double calculerFraisRetard(LivreCategorie categorie, int joursRetard) {
        return categorie.calculerFraisRetard(joursRetard);
    }
}
