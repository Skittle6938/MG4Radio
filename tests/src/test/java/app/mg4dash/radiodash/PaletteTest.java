package app.mg4dash.radiodash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Thème de MG4Dash appliqué à la radio (Palette) : réglage Settings.Global mg4dash_theme =
 * "fond,boutons,clair,barre verticale,lecteur,barre large,barre à droite,AM".
 */
public class PaletteTest {
    private static final int BLUE = 0xFF2979FF, CYAN = 0xFF00D2FF, TOP = 0xFF0B0F17, RADIO_BG = 0xFF141925, WHITE = 0xFFFFFFFF;

    @Before public void setUp() {
        Statics.set(Palette.class, "loaded", null);
        Palette.apply(null);
    }

    @Test public void originalThemeChangesNothing() {
        assertEquals(BLUE, Palette.map(BLUE));
        assertEquals(TOP, Palette.map(TOP));
        assertEquals(BLUE, Palette.accent());
        assertFalse(Palette.isLight());
    }

    @Test public void missingSettingIsTheOriginalTheme() {
        Palette.apply("2,2,0,1");
        Palette.apply(null);
        assertEquals(BLUE, Palette.map(BLUE));
        assertEquals(TOP, Palette.map(TOP));
        assertFalse("réglages remis à zéro sans MG4Dash", Palette.verticalDock);
    }

    @Test public void nightFromMg4DashIsTheTintedNight() {
        Palette.apply("0,0");
        assertEquals("Nuit v2", 0xFF070C1B, Palette.map(TOP));
        assertEquals(BLUE, Palette.map(BLUE));
    }

    @Test public void sameValueTwiceIsNotAChange() {
        assertTrue(Palette.apply("2,2"));
        assertFalse(Palette.apply("2,2"));
        assertTrue(Palette.apply("2,3"));
    }

    @Test public void darkThemeMapsBackgroundsAndButtons() {
        Palette.apply("2,2");   // fond graphite, boutons rouges
        assertEquals(0xFFE53935, Palette.map(BLUE));
        assertEquals(0xFFFF8A80, Palette.map(CYAN));
        assertEquals(0xFF0C1015, Palette.map(TOP));
        assertEquals("fond propre à la radio", 0xFF171D26, Palette.map(RADIO_BG));
        assertEquals("bouton appuyé : action assombrie à 70 %", 0xFFA02725, Palette.map(0xFF1A5CCC));
        assertEquals("textes blancs en mode sombre", WHITE, Palette.map(WHITE));
        assertEquals("couleur inconnue inchangée", 0xFF123456, Palette.map(0xFF123456));
        assertEquals(0xFFE53935, Palette.starColor());
    }

    @Test public void alphaIsKept() {
        Palette.apply("0,3");
        assertEquals(0x4000C853, Palette.map(0x402979FF));
        assertEquals(0x0000C853, Palette.map(0x002979FF));
    }

    @Test public void lightThemeDarkensTexts() {
        Palette.apply("1,3,1");
        assertTrue(Palette.isLight());
        assertEquals(0xFF141922, Palette.map(WHITE));
        assertEquals(0xFF00A142, Palette.map(BLUE));
        assertEquals(0xFFEDEDED, Palette.map(TOP));
        assertEquals(0xFF4A5466, Palette.map(0xFFA0AEC0));
    }

    // Les 24 couleurs de MG4Dash, recopiées des contrats (fonds : contracts/theme-backgrounds-v2.md, boutons : theme-24-colours.md) et non des tables de Palette
    private static final int[] BG_TOP = {0xFF070C1B, 0xFF050506, 0xFF0C1015, 0xFF081914, 0xFF1B060B, 0xFF0A1217, 0xFF04171D, 0xFF051C1A, 0xFF051D0D, 0xFF0E1809, 0xFF161908, 0xFF17130B, 0xFF1C1205, 0xFF1B0F07, 0xFF1D0A04, 0xFF1D0404, 0xFF1B0713, 0xFF190819, 0xFF12071B, 0xFF07051C, 0xFF03071E, 0xFF091318, 0xFF111111, 0xFF15110C};
    private static final int[] BG_BOTTOM = {0xFF0C1531, 0xFF0E0E10, 0xFF171D26, 0xFF0E2F24, 0xFF320B15, 0xFF12212B, 0xFF072A36, 0xFF0A3330, 0xFF083517, 0xFF1A2C11, 0xFF292F0F, 0xFF2A2214, 0xFF33220A, 0xFF311C0C, 0xFF361307, 0xFF360707, 0xFF310C23, 0xFF2F0F2F, 0xFF220C31, 0xFF0D0A33, 0xFF050D38, 0xFF10232D, 0xFF1F1F1F, 0xFF261F17};
    private static final int[] BG_IDLE = {0xFF0E1939, 0xFF1B1B1F, 0xFF1B222D, 0xFF11372A, 0xFF3A0D18, 0xFF152632, 0xFF09313F, 0xFF0B3C38, 0xFF0A3E1B, 0xFF1E3314, 0xFF303611, 0xFF312817, 0xFF3C280B, 0xFF39200E, 0xFF3F1609, 0xFF3F0909, 0xFF390E29, 0xFF361136, 0xFF270E39, 0xFF0F0B3C, 0xFF061042, 0xFF132935, 0xFF242424, 0xFF2D241B};
    private static final int[] BG_BORDER = {0xFF142452, 0xFF2A2A2F, 0xFF263040, 0xFF184E3C, 0xFF531323, 0xFF1E3648, 0xFF0C465A, 0xFF105650, 0xFF0E5827, 0xFF2C491D, 0xFF454E18, 0xFF453921, 0xFF563910, 0xFF522E14, 0xFF5A200C, 0xFF5A0C0C, 0xFF52143B, 0xFF4E184E, 0xFF381452, 0xFF161056, 0xFF08165E, 0xFF1B3B4B, 0xFF333333, 0xFF403326};
    private static final int[] BGL_TOP = {0xFFD0D9F3, 0xFFEDEDED, 0xFFDAE0EA, 0xFFD2F2E7, 0xFFF3D0D9, 0xFFD5E4EE, 0xFFD0EAF3, 0xFFD0F3F0, 0xFFD0F3DC, 0xFFDDEFD4, 0xFFECF1D2, 0xFFEDE5D7, 0xFFF3E5D0, 0xFFF3DFD0, 0xFFF3D9D0, 0xFFF3D0D0, 0xFFF3D0E6, 0xFFF1D2F1, 0xFFE5D0F3, 0xFFD3D0F3, 0xFFD0D6F3, 0xFFD4E6F0, 0xFFE2E2E2, 0xFFEAE2DA};
    private static final int[] BGL_BORDER = {0xFF9DAFE7, 0xFFCFCFD4, 0xFFB1BED2, 0xFFA1E3CD, 0xFFE79DAF, 0xFFA8C6DC, 0xFF9DD4E7, 0xFF9DE7E0, 0xFF9DE7B6, 0xFFB9DDA6, 0xFFD7E2A2, 0xFFD9C9AB, 0xFFE7C89D, 0xFFE7BC9D, 0xFFE7AF9D, 0xFFE79D9D, 0xFFE79DCC, 0xFFE2A2E2, 0xFFC89DE7, 0xFFA39DE7, 0xFF9DA9E7, 0xFFA4CCDF, 0xFFC2C2C2, 0xFFD2C2B1};
    private static final int[] ACTION = {0xFF2979FF, 0xFF8E99A8, 0xFFE53935, 0xFF00C853, 0xFFFF8F00, 0xFF00B8D4, 0xFF00BFA5, 0xFF1DE9B6, 0xFF64DD17, 0xFFAEEA00, 0xFFFFD600, 0xFFFFC400, 0xFFFFAB00, 0xFFFF6D00, 0xFFFF6E40, 0xFFFF4081, 0xFFF50057, 0xFFD500F9, 0xFFAA00FF, 0xFF7C4DFF, 0xFFB388FF, 0xFF536DFE, 0xFF00B0FF, 0xFF40C4FF};
    private static final int[] SELECT = {0xFF00D2FF, 0xFFCFD6DF, 0xFFFF8A80, 0xFF69F0AE, 0xFFFFC46B, 0xFF73D8E7, 0xFF73DCCE, 0xFF83F3D7, 0xFFAAEC7F, 0xFFD2F373, 0xFFFFE873, 0xFFFFDF73, 0xFFFFD173, 0xFFFFAF73, 0xFFFFAF96, 0xFFFF96BA, 0xFFFA73A3, 0xFFE873FC, 0xFFD073FF, 0xFFB79DFF, 0xFFD5BEFF, 0xFFA0AFFE, 0xFF73D4FF, 0xFF96DFFF};
    private static final int[] ACTION_L = {0xFF1E63D6, 0xFF5E6878, 0xFFD32F2F, 0xFF00A142, 0xFFE67E00, 0xFF00869B, 0xFF008A77, 0xFF118669, 0xFF3B830E, 0xFF5B7B00, 0xFF846F00, 0xFF8C6C00, 0xFF9B6800, 0xFFCF5800, 0xFFC75632, 0xFFD6366C, 0xFFCE0049, 0xFFB300D1, 0xFF8F00D6, 0xFF6841D6, 0xFF7D5FB2, 0xFF465CD5, 0xFF0083BD, 0xFF287A9F};
    private static final int[] SELECT_L = {0xFF0088AD, 0xFF46505E, 0xFFB71C1C, 0xFF007F35, 0xFFB86200, 0xFF006676, 0xFF00695B, 0xFF0D6650, 0xFF2D640A, 0xFF455D00, 0xFF645400, 0xFF6B5200, 0xFF764F00, 0xFF9D4300, 0xFF974126, 0xFFB22D5A, 0xFFAC003D, 0xFF9500AE, 0xFF7700B2, 0xFF5736B2, 0xFF5F4887, 0xFF3A4CB2, 0xFF006490, 0xFF1E5D79};

    @Test public void each24BackgroundIsRead() {
        for (int b = 0; b < 24; b++) {
            Palette.apply(b + ",0,0,1,0,0,0,0,0,1,,0");
            assertEquals("fond " + b + " haut", BG_TOP[b], Palette.map(TOP));
            assertEquals("fond " + b + " bas", BG_BOTTOM[b], Palette.map(0xFF141B29));
            assertEquals("fond " + b + " bouton", BG_IDLE[b], Palette.map(0xFF1C2231));
            assertEquals("fond " + b + " bordure", BG_BORDER[b], Palette.map(0xFF2A3142));
            assertTrue("les autres champs restent lus (" + b + ")", Palette.verticalDock);
            assertEquals(1, Palette.aaChoice);
            Palette.apply(b + ",0,1");
            assertEquals("fond clair " + b + " haut", BGL_TOP[b], Palette.map(TOP));
            assertEquals("fond clair " + b + " bordure", BGL_BORDER[b], Palette.map(0xFF2A3142));
        }
    }

    @Test public void each24AccentIsRead() {
        for (int a = 0; a < 24; a++) {
            Palette.apply("0," + a + ",0,1");
            assertEquals("boutons " + a, ACTION[a], Palette.map(BLUE));
            assertEquals("sélection " + a, SELECT[a], Palette.map(CYAN));
            assertTrue(Palette.verticalDock);
            Palette.apply("0," + a + ",1");
            assertEquals("boutons clairs " + a, ACTION_L[a], Palette.map(BLUE));
            assertEquals("sélection claire " + a, SELECT_L[a], Palette.map(CYAN));
        }
    }

    @Test public void unknownIdsFallBackToBlackAndBlueAndKeepTheOtherFields() {
        Palette.apply("24,99,0,1,0,1,1,0,1,2,com.example/.Main,0");
        assertEquals("fond Noir", 0xFF050506, Palette.map(TOP));
        assertEquals("boutons Bleu", BLUE, Palette.map(BLUE));
        assertTrue(Palette.verticalDock);
        assertTrue(Palette.wideBar);
        assertTrue(Palette.rightBar);
        assertTrue(Palette.fogHidden);
        assertEquals(2, Palette.aaChoice);
        assertEquals("com.example/.Main", Palette.aaApp);
        Palette.apply("-1,x,1,1");
        assertEquals("fond clair Noir", 0xFFEDEDED, Palette.map(TOP));
        assertEquals("boutons clairs Bleu", 0xFF1E63D6, Palette.map(BLUE));
        assertTrue(Palette.isLight());
        assertTrue(Palette.verticalDock);
    }

    @Test public void unreadableValueKeepsTheOriginalColours() {
        Palette.apply("2,2");
        Palette.apply("abc");
        assertEquals(BLUE, Palette.map(BLUE));
        assertFalse(Palette.isLight());
    }

    @Test public void layoutOptionsFollowTheirFields() {
        Palette.apply("0,0,0,1,1,1,1");
        assertTrue(Palette.verticalDock);
        assertEquals(1, Palette.mediaStyle);
        assertTrue(Palette.wideBar);
        assertTrue(Palette.rightBar);
        Palette.apply("0,0,0,0,0,0,0");
        assertFalse(Palette.verticalDock);
        assertEquals(0, Palette.mediaStyle);
        assertFalse(Palette.wideBar);
        assertFalse(Palette.rightBar);
        Palette.apply("0,0,0,1");
        Palette.apply("0,0");
        assertFalse("champs absents : éteints", Palette.verticalDock);
    }

    @Test public void amOptionIsTheEighthField() {
        assertFalse(Bands.amOption(null));
        assertFalse(Bands.amOption("0,0"));
        assertFalse(Bands.amOption("0,0,0,0,0,0,0,0"));
        assertFalse("7e champ = barre à droite", Bands.amOption("0,0,0,0,0,0,1"));
        assertTrue(Bands.amOption("0,0,0,0,0,0,0,1"));
        assertTrue(Bands.amOption("2,3,1,0,0,0,0, 1 "));
    }

    /** #5: the Android Auto button follows MG4Dash's choice (10th field) and its other app (11th). */
    @Test public void projectionChoiceFollowsMg4Dash() {
        Palette.apply("0,0,0,0,0,0,0,0,0");
        assertEquals(-1, Palette.aaChoice);
        assertEquals("com.allgo.app.androidauto/.ProjectionActivity", Palette.projectionTarget());
        Palette.apply("0,0,0,0,0,0,0,0,0,1,");
        assertEquals(1, Palette.aaChoice);
        assertEquals("app.mg4dash/.ui.OpenProjectionActivity", Palette.projectionTarget());
        Palette.apply("0,0,0,0,0,0,0,0,0,2,com.waze/.FreeMapAppActivity");
        assertEquals(2, Palette.aaChoice);
        assertEquals("com.waze/.FreeMapAppActivity", Palette.aaApp);
        Palette.apply("0,0,0,0,0,0,0,0,0,0,");
        assertEquals(0, Palette.aaChoice);
        assertNull(Palette.aaApp);
    }
}
