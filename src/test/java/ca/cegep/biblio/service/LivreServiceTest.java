package ca.cegep.biblio.service;

import ca.cegep.biblio.model.Livre;
import ca.cegep.biblio.model.LivreCategorie;
import ca.cegep.biblio.repository.LivreRepository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // ---------- 1 test simple (sans mock) 
    void calculerFraisRetard_longRetard_plafonneLeMontant() {
        // DONE À tester : pour une catégorie donnée, un très grand nombre de jours de retard
        // ne dépasse jamais le plafond de cette catégorie.

        // Act
        double frais = service.calculerFraisRetard(LivreCategorie.SCIENCE, 45);
        // Assert
        assertEquals(20.0, frais);
    }

    // ---------- 1 test sur une exception (assertThrows) ----------

    @Test
    void emprunter_livreDejaEmprunte_lanceIllegalStateException() {
        /// DONE À tester : emprunter un livre dont disponible == false lance une IllegalStateException.
        // Indice : le repository (mock) doit retourner ce livre pour son id.
        /// Arrange
        Livre livre = new Livre();
        livre.setDisponible(false);
        when(repository.findById(2L)).thenReturn(Optional.of(livre));

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class, () -> service.emprunter(2L));

        assertAll("Illegal State Exception",
            () -> assertEquals("Ce livre est déjà emprunté", exception.getMessage())
        );

        verify(repository, never()).save(any());
    }

    // ---------- 1 test avec mock (when / thenReturn / verify) ----------

    @Test
    void emprunter_livreDisponible_marqueIndisponibleEtSauvegarde() {
        /// DONE À tester : emprunter un livre disponible le rend indisponible
        // et appelle repository.save(...) exactement une fois.
        // Définissez le comportement du mock avec when(...).thenReturn(...).

        // Arrange 
        Livre livre = new Livre();
        livre.setId(4L);
        livre.setDisponible(true);

        when(repository.findById(4L)).thenReturn(Optional.of(livre));
        when(repository.save(livre)).thenReturn(livre);

        // Act
        Livre resultat = service.emprunter(4L);

        /// Assert
        assertFalse(resultat.isDisponible());
        verify(repository, times(1)).save(livre);        
    }
}