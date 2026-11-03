package ca.cegep.biblio.service;

import ca.cegep.biblio.exception.GestionnaireErreurs;
import ca.cegep.biblio.model.Livre;
import ca.cegep.biblio.repository.LivreRepository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * TP partie C : à vous d'implémenter les 3 tests ci-dessous.
 * Retirez @Disabled de chaque test une fois son implémentation écrite.
 * Respectez le modèle Arrange / Act / Assert.
 */
@ExtendWith(MockitoExtension.class)
class LivreServiceTest {

    @Mock
    private LivreRepository repository;

    @InjectMocks
    private LivreService service;

    // ---------- 1 test simple (sans mock) ----------

    @Test
    void calculerFraisRetard_longRetard_plafonneLeMontant() {
        // DONE À tester : pour une catégorie donnée, un très grand nombre de jours de retard
        // ne dépasse jamais le plafond de cette catégorie.
        // Arrange
        // Act
        double frais = service.calculerFraisRetard("SCIENCE", 45);
        // Assert
        assertEquals(20.0, frais);
    }

    // ---------- 1 test sur une exception (assertThrows) ----------

    @Test
    @Disabled("ASAP")
    void emprunter_livreDejaEmprunte_lanceIllegalStateException() {
        // TODO À tester : emprunter un livre dont disponible == false lance une IllegalStateException.
        // Indice : le repository (mock) doit retourner ce livre pour son id.
        // Arrange
        // Act
        // Livre livre = new Livre(4, "Pomme", "", "", 0, "SCIENCE");

        // Assert
        // IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.emprunter(2));
        // assertAll("Illegal State Exception",
        //     () -> assertEquals(HttpStatus.IllegalStateException.value(), livre.getStatus()),
        //     () -> assertEquals("Error: response status is 409", livre.getMessage())
        // );
    }

    // ---------- 1 test avec mock (when / thenReturn / verify) ----------

    @Test
    @Disabled("À implémenter")
    void emprunter_livreDisponible_marqueIndisponibleEtSauvegarde() {
        // À tester : emprunter un livre disponible le rend indisponible
        // et appelle repository.save(...) exactement une fois.
        // Définissez le comportement du mock avec when(...).thenReturn(...).
    }
}


