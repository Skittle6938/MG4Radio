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
        Palette.apply("0,0");
    }

    @Test public void originalThemeChangesNothing() {
        assertEquals(BLUE, Palette.map(BLUE));
        assertEquals(TOP, Palette.map(TOP));
        assertEquals(BLUE, Palette.accent());
        assertFalse(Palette.isLight());
    }

    @Test public void missingSettingIsTheOriginalTheme() {
        Palette.apply("2,2");
        Palette.apply(null);
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
        assertEquals(0xFF15171A, Palette.map(TOP));
        assertEquals("fond propre à la radio", 0xFF22252A, Palette.map(RADIO_BG));
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

    // Les 24 couleurs de MG4Dash, recopiées du contrat (contracts/theme-24-colours.md) et non des tables de Palette
    private static final int[] BG_TOP = {0xFF0B0F17, 0xFF050506, 0xFF15171A, 0xFF07110E, 0xFF13090C, 0xFF0B0D12, 0xFF081114, 0xFF091312, 0xFF0A120E, 0xFF0D110B, 0xFF10120B, 0xFF100F0C, 0xFF130F09, 0xFF120D0A, 0xFF140B08, 0xFF140808, 0xFF120A0E, 0xFF120B12, 0xFF0F0A12, 0xFF0A0913, 0xFF080A14, 0xFF0C0E10, 0xFF0E0E0E, 0xFF0F0E0D};
    private static final int[] BG_BOTTOM = {0xFF141B29, 0xFF0E0E10, 0xFF22252A, 0xFF0F2019, 0xFF231117, 0xFF131720, 0xFF0F1F24, 0xFF112221, 0xFF12211A, 0xFF181F14, 0xFF1E2013, 0xFF1D1B16, 0xFF221B11, 0xFF211812, 0xFF24140F, 0xFF240F0F, 0xFF21121A, 0xFF201320, 0xFF1B1221, 0xFF121122, 0xFF0E1225, 0xFF16191D, 0xFF1A1A1A, 0xFF1C1A17};
    private static final int[] BG_IDLE = {0xFF1C2231, 0xFF1B1B1F, 0xFF2B2F35, 0xFF16271F, 0xFF29161C, 0xFF171C26, 0xFF12252B, 0xFF142928, 0xFF15281F, 0xFF1D2518, 0xFF242617, 0xFF23201A, 0xFF292014, 0xFF281D15, 0xFF2B1812, 0xFF2B1212, 0xFF28151F, 0xFF261726, 0xFF201528, 0xFF161429, 0xFF11152C, 0xFF1A1F23, 0xFF1F1F1F, 0xFF211F1C};
    private static final int[] BG_BORDER = {0xFF2A3142, 0xFF2A2A2F, 0xFF3A3F46, 0xFF243A30, 0xFF3B2229, 0xFF212938, 0xFF1B363E, 0xFF1D3C3A, 0xFF1F3A2D, 0xFF2A3624, 0xFF343821, 0xFF332F26, 0xFF3C2F1D, 0xFF3A2A1F, 0xFF3E241B, 0xFF3E1B1B, 0xFF3A1F2D, 0xFF382138, 0xFF2F1F3A, 0xFF201D3C, 0xFF191F41, 0xFF262D33, 0xFF2D2D2D, 0xFF302D29};
    private static final int[] BGL_TOP = {0xFFE9EEF6, 0xFFEDEDED, 0xFFE4E6E9, 0xFFE6F0EB, 0xFFF2E8EA, 0xFFE9ECF2, 0xFFE8F0F3, 0xFFE8F3F2, 0xFFE8F3ED, 0xFFECF1EA, 0xFFF0F2E9, 0xFFF0EEEA, 0xFFF3EEE8, 0xFFF3ECE8, 0xFFF3EAE8, 0xFFF3E8E8, 0xFFF3E8ED, 0xFFF2E9F2, 0xFFEEE8F3, 0xFFE9E8F3, 0xFFE8EAF3, 0xFFEAEDF0, 0xFFEDEDED, 0xFFEFEDEC};
    private static final int[] BGL_BORDER = {0xFFCBD3E1, 0xFFCFCFD4, 0xFFC7CBD1, 0xFFC4D6CC, 0xFFDCC7CC, 0xFFC6CDDD, 0xFFC3D8DF, 0xFFC3DFDD, 0xFFC3DFD1, 0xFFCEDAC8, 0xFFD9DDC6, 0xFFD8D3CA, 0xFFDFD3C3, 0xFFDFCFC3, 0xFFDFCAC3, 0xFFDFC3C3, 0xFFDFC3D1, 0xFFDDC6DD, 0xFFD3C3DF, 0xFFC6C3DF, 0xFFC3C8DF, 0xFFCAD1D8, 0xFFD1D1D1, 0xFFD5D1CD};
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
