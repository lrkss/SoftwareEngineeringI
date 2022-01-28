package versionsverwaltung.main;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProjektTest {

    @ParameterizedTest
    @ValueSource(strings = {"Testprojekt"})
    void neuesProjektAnlegen() {
        assertTrue(true);
    }

}