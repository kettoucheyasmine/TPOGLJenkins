import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test
    void testMainExists() {
        // Vérifie simplement que la classe App existe et est accessible
        assertDoesNotThrow(() -> {
            App.main(new String[]{});
        });
    }
}