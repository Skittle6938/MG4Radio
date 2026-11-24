package app.mg4dash.radiodash;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Thème de MG4Dash appliqué à la radio : MG4Dash publie son choix dans Settings.Global
 * ("mg4dash_theme" = "fond,boutons"). Les couleurs de Radio Dash sont celles du thème d'origine
 * (bleu nuit + bleu) ; on remplace chacune par son équivalent du thème choisi, en parcourant les
 * vues (fonds, contours, textes, teintes). Mêmes tables que i18n/theme_gen.py de MG4Dash.
 */
public final class Palette {
    private static final String TAG = "RadioDash";

    /** Couleurs d'origine de Radio Dash (l'ancien fond Nuit de MG4Dash) : celles qu'on remplace par le thème choisi. */
    private static final int[] ORIG = {0x0B0F17, 0x141B29, 0x0E1420, 0x12161F, 0x2A3142, 0x1C2231, 0x283048, 0x262D3D};

    // Fonds : haut, bas, fin du dégradé, panneau, bordure, bouton au repos, bouton appuyé, jauge éteinte.
    // 24 couleurs dans l'ordre de MG4Dash (fonds v2, 24/11 : tous teintés sauf Noir), tirées de ses themes_palette.xml
    // et themes_colors.xml ; en clair, le panneau teinté est gardé opaque
    private static final int[][] BG = {
            {0x070C1B, 0x0C1531, 0x090F23, 0x0A1229, 0x142452, 0x0E1939, 0x13224E, 0x111D43},   // 0 Night
            {0x050506, 0x0E0E10, 0x08080A, 0x121214, 0x2A2A2F, 0x1B1B1F, 0x2A2A31, 0x26262B},   // 1 Black (inchangé)
            {0x0C1015, 0x171D26, 0x10151B, 0x131820, 0x263040, 0x1B222D, 0x242E3C, 0x202835},   // 2 Charcoal
            {0x081914, 0x0E2F24, 0x0A211A, 0x0C271E, 0x184E3C, 0x11372A, 0x164A39, 0x144132},   // 3 Forest
            {0x1B060B, 0x320B15, 0x23080F, 0x2A0911, 0x531323, 0x3A0D18, 0x4F1221, 0x450F1D},   // 4 Burgundy
            {0x0A1217, 0x12212B, 0x0D171F, 0x0F1B24, 0x1E3648, 0x152632, 0x1D3444, 0x192D3B},   // 5 Slate
            {0x04171D, 0x072A36, 0x051E26, 0x06232D, 0x0C465A, 0x09313F, 0x0C4355, 0x0A3A4A},   // 6 Petrol
            {0x051C1A, 0x0A3330, 0x072422, 0x082B28, 0x105650, 0x0B3C38, 0x10514C, 0x0D4742},   // 7 Teal
            {0x051D0D, 0x083517, 0x062510, 0x072C13, 0x0E5827, 0x0A3E1B, 0x0D5425, 0x0B4920},   // 8 Emerald
            {0x0E1809, 0x1A2C11, 0x121F0C, 0x16250E, 0x2C491D, 0x1E3314, 0x29461B, 0x243D18},   // 9 Moss
            {0x161908, 0x292F0F, 0x1D210A, 0x22270C, 0x454E18, 0x303611, 0x414A17, 0x394014},   // 10 Olive
            {0x17130B, 0x2A2214, 0x1D180E, 0x231D10, 0x453921, 0x312817, 0x42361F, 0x392F1B},   // 11 Sand
            {0x1C1205, 0x33220A, 0x241807, 0x2B1C08, 0x563910, 0x3C280B, 0x513610, 0x472F0D},   // 12 Bronze
            {0x1B0F07, 0x311C0C, 0x231409, 0x29170A, 0x522E14, 0x39200E, 0x4E2C13, 0x432611},   // 13 Brown
            {0x1D0A04, 0x361307, 0x260D05, 0x2D1006, 0x5A200C, 0x3F1609, 0x551E0C, 0x4A1A0A},   // 14 Rust
            {0x1D0404, 0x360707, 0x260505, 0x2D0606, 0x5A0C0C, 0x3F0909, 0x550C0C, 0x4A0A0A},   // 15 DeepRed
            {0x1B0713, 0x310C23, 0x230919, 0x290A1E, 0x52143B, 0x390E29, 0x4E1338, 0x431131},   // 16 Wine
            {0x190819, 0x2F0F2F, 0x210A21, 0x270C27, 0x4E184E, 0x361136, 0x4A174A, 0x401440},   // 17 Plum
            {0x12071B, 0x220C31, 0x180923, 0x1C0A29, 0x381452, 0x270E39, 0x35134E, 0x2E1143},   // 18 Purple
            {0x07051C, 0x0D0A33, 0x090724, 0x0B082B, 0x161056, 0x0F0B3C, 0x151051, 0x120D47},   // 19 Indigo
            {0x03071E, 0x050D38, 0x030A28, 0x040B2F, 0x08165E, 0x061042, 0x081559, 0x07134D},   // 20 Midnight
            {0x091318, 0x10232D, 0x0B1920, 0x0D1E26, 0x1B3B4B, 0x132935, 0x1A3847, 0x16313E},   // 21 Steel
            {0x111111, 0x1F1F1F, 0x161616, 0x1A1A1A, 0x333333, 0x242424, 0x303030, 0x2A2A2A},   // 22 Graphite
            {0x15110C, 0x261F17, 0x1B1610, 0x201A13, 0x403326, 0x2D241B, 0x3C3024, 0x352A20},   // 23 WarmGrey
    };
    // Boutons : action, sélection
    private static final int[][] ACC = {
            {0x2979FF, 0x00D2FF}, {0x8E99A8, 0xCFD6DF}, {0xE53935, 0xFF8A80}, {0x00C853, 0x69F0AE}, {0xFF8F00, 0xFFC46B},
            {0x00B8D4, 0x73D8E7}, {0x00BFA5, 0x73DCCE}, {0x1DE9B6, 0x83F3D7}, {0x64DD17, 0xAAEC7F}, {0xAEEA00, 0xD2F373},
            {0xFFD600, 0xFFE873}, {0xFFC400, 0xFFDF73}, {0xFFAB00, 0xFFD173}, {0xFF6D00, 0xFFAF73}, {0xFF6E40, 0xFFAF96},
            {0xFF4081, 0xFF96BA}, {0xF50057, 0xFA73A3}, {0xD500F9, 0xE873FC}, {0xAA00FF, 0xD073FF}, {0x7C4DFF, 0xB79DFF},
            {0xB388FF, 0xD5BEFF}, {0x536DFE, 0xA0AFFE}, {0x00B0FF, 0x73D4FF}, {0x40C4FF, 0x96DFFF},
    };

    // Mode clair (mêmes tables que MG4Dash) : fonds clairs, boutons plus soutenus, textes foncés
    private static final int[][] BG_LIGHT = {
            {0xD0D9F3, 0xE7EBF9, 0xDAE1F6, 0xF5F7FC, 0x9DAFE7, 0xC8D2F1, 0xB1C0EC, 0xBAC7EE},   // 0 Night
            {0xEDEDED, 0xF8F8F8, 0xF1F1F1, 0xFFFFFF, 0xCFCFD4, 0xE6E6EA, 0xD6D6DC, 0xD9D9DE},   // 1 Black (inchangé)
            {0xDAE0EA, 0xECEFF4, 0xE2E7EE, 0xF7F8FA, 0xB1BED2, 0xD3DBE6, 0xC1CCDC, 0xC8D1DF},   // 2 Charcoal
            {0xD2F2E7, 0xE7F8F2, 0xDCF4EC, 0xF5FCFA, 0xA1E3CD, 0xCAEFE3, 0xB4E9D7, 0xBCEBDB},   // 3 Forest
            {0xF3D0D9, 0xF9E7EB, 0xF6DAE1, 0xFCF5F7, 0xE79DAF, 0xF1C8D2, 0xECB1C0, 0xEEBAC7},   // 4 Burgundy
            {0xD5E4EE, 0xE9F1F6, 0xDEEAF2, 0xF6F9FB, 0xA8C6DC, 0xCEDFEB, 0xBAD2E3, 0xC1D7E6},   // 5 Slate
            {0xD0EAF3, 0xE7F4F9, 0xDAEFF6, 0xF5FBFC, 0x9DD4E7, 0xC8E7F1, 0xB1DDEC, 0xBAE1EE},   // 6 Petrol
            {0xD0F3F0, 0xE7F9F7, 0xDAF6F4, 0xF5FCFC, 0x9DE7E0, 0xC8F1EE, 0xB1ECE7, 0xBAEEE9},   // 7 Teal
            {0xD0F3DC, 0xE7F9ED, 0xDAF6E3, 0xF5FCF7, 0x9DE7B6, 0xC8F1D6, 0xB1ECC5, 0xBAEECB},   // 8 Emerald
            {0xDDEFD4, 0xEDF7E9, 0xE5F2DE, 0xF8FBF6, 0xB9DDA6, 0xD7ECCD, 0xC7E4B9, 0xCDE7C0},   // 9 Moss
            {0xECF1D2, 0xF5F8E8, 0xF0F4DC, 0xFBFCF5, 0xD7E2A2, 0xE9EFCB, 0xE0E8B5, 0xE3EABD},   // 10 Olive
            {0xEDE5D7, 0xF5F2EA, 0xF1EBDF, 0xFBF9F6, 0xD9C9AB, 0xE9E1D0, 0xE1D5BC, 0xE4D9C3},   // 11 Sand
            {0xF3E5D0, 0xF9F1E7, 0xF6EADA, 0xFCF9F5, 0xE7C89D, 0xF1E0C8, 0xECD3B1, 0xEED8BA},   // 12 Bronze
            {0xF3DFD0, 0xF9EEE7, 0xF6E6DA, 0xFCF8F5, 0xE7BC9D, 0xF1D9C8, 0xECCAB1, 0xEECFBA},   // 13 Brown
            {0xF3D9D0, 0xF9EBE7, 0xF6E1DA, 0xFCF7F5, 0xE7AF9D, 0xF1D2C8, 0xECC0B1, 0xEEC7BA},   // 14 Rust
            {0xF3D0D0, 0xF9E7E7, 0xF6DADA, 0xFCF5F5, 0xE79D9D, 0xF1C8C8, 0xECB1B1, 0xEEBABA},   // 15 DeepRed
            {0xF3D0E6, 0xF9E7F2, 0xF6DAEC, 0xFCF5FA, 0xE79DCC, 0xF1C8E2, 0xECB1D6, 0xEEBADB},   // 16 Wine
            {0xF1D2F1, 0xF8E8F8, 0xF4DCF4, 0xFCF5FC, 0xE2A2E2, 0xEFCBEF, 0xE8B5E8, 0xEABDEA},   // 17 Plum
            {0xE5D0F3, 0xF1E7F9, 0xEADAF6, 0xF9F5FC, 0xC89DE7, 0xE0C8F1, 0xD3B1EC, 0xD8BAEE},   // 18 Purple
            {0xD3D0F3, 0xE8E7F9, 0xDDDAF6, 0xF5F5FC, 0xA39DE7, 0xCBC8F1, 0xB6B1EC, 0xBEBAEE},   // 19 Indigo
            {0xD0D6F3, 0xE7EAF9, 0xDADFF6, 0xF5F6FC, 0x9DA9E7, 0xC8CFF1, 0xB1BBEC, 0xBAC2EE},   // 20 Midnight
            {0xD4E6F0, 0xE8F2F7, 0xDDECF3, 0xF6FAFC, 0xA4CCDF, 0xCCE2ED, 0xB7D6E6, 0xBFDBE8},   // 21 Steel
            {0xE2E2E2, 0xF0F0F0, 0xE8E8E8, 0xF9F9F9, 0xC2C2C2, 0xDDDDDD, 0xCFCFCF, 0xD4D4D4},   // 22 Graphite
            {0xEAE2DA, 0xF4F0EC, 0xEEE8E2, 0xFAF9F7, 0xD2C2B1, 0xE6DDD3, 0xDCCFC1, 0xDFD4C8},   // 23 WarmGrey
    };
    private static final int[][] ACC_LIGHT = {
            {0x1E63D6, 0x0088AD}, {0x5E6878, 0x46505E}, {0xD32F2F, 0xB71C1C}, {0x00A142, 0x007F35}, {0xE67E00, 0xB86200},
            {0x00869B, 0x006676}, {0x008A77, 0x00695B}, {0x118669, 0x0D6650}, {0x3B830E, 0x2D640A}, {0x5B7B00, 0x455D00},
            {0x846F00, 0x645400}, {0x8C6C00, 0x6B5200}, {0x9B6800, 0x764F00}, {0xCF5800, 0x9D4300}, {0xC75632, 0x974126},
            {0xD6366C, 0xB22D5A}, {0xCE0049, 0xAC003D}, {0xB300D1, 0x9500AE}, {0x8F00D6, 0x7700B2}, {0x6841D6, 0x5736B2},
            {0x7D5FB2, 0x5F4887}, {0x465CD5, 0x3A4CB2}, {0x0083BD, 0x006490}, {0x287A9F, 0x1E5D79},
    };
    private static final int TEXT_DARK = 0x141922;

    private static final Map<Integer, Integer> MAP = new HashMap<>();
    private static boolean lightMode = false;
    /** Thème clair publié par MG4Dash. */
    public static boolean isLight() { return lightMode; }
    /** Barre verticale à gauche choisie dans MG4Dash (4e champ de mg4dash_theme). */
    public static boolean verticalDock = false;
    /** Lecteur choisi dans MG4Dash (5e champ) : 0 = Immersif (défaut), 1 = Classique. */
    public static int mediaStyle = 0;
    /** Barre verticale large (réglage « barre large » de MG4Dash, 6e champ) : deux fois plus large. */
    public static boolean wideBar = false;
    /** Barre verticale à droite (conduite à droite ou côté forcé dans MG4Dash, 7e champ). */
    public static boolean rightBar = false;
    /** Bouton antibrouillards masqué par MG4Dash (menu de raccourcis, 9e champ) : la barre gauche prend toute la hauteur. */
    public static boolean fogHidden = false;
    /**
     * Android Auto button chosen in MG4Dash (10th field, #5): 0 Android Auto, 1 CarPlay, 2 another app ([aaApp]);
     * -1 when MG4Dash does not publish it (older version): the button keeps opening Android Auto.
     */
    public static int aaChoice = -1;
    /** The other app of the Android Auto button (11th field, a component), null otherwise. */
    public static String aaApp = null;

    /**
     * What the Android Auto button opens (#5): through MG4Dash, which opens what its own button does (Android Auto,
     * CarPlay, the chosen app or the phone's projection); Android Auto itself with a MG4Dash that does not say.
     */
    public static String projectionTarget() {
        return aaChoice >= 0 ? "app.mg4dash/.ui.OpenProjectionActivity" : "com.allgo.app.androidauto/.ProjectionActivity";
    }
    private static String loaded = null;
    private static boolean identity = true;
    private static final Map<TextView, ColorStateList> textDone = new WeakHashMap<>();
    private static final Set<Object> done = Collections.newSetFromMap(new WeakHashMap<Object, Boolean>());
    /** Icônes déjà vues en mode clair, par image affichée (une nouvelle image est revue). */
    private static final Set<Drawable> iconDone = Collections.newSetFromMap(new WeakHashMap<Drawable, Boolean>());
    /** Noms des id de vues : getResourceEntryName coûte cher et lève une exception pour les id générés. */
    private static final Map<Integer, String> names = new HashMap<>();

    /** Nom de l'id de la vue ("" sans id ou pour un id généré), mis en cache. */
    static String name(View v) {
        int id = v.getId();
        if (id == View.NO_ID) return "";
        synchronized (names) {
            String n = names.get(id);
            if (n == null) {
                try { n = v.getResources().getResourceEntryName(id); } catch (Throwable t) { n = ""; }
                names.put(id, n);
            }
            return n;
        }
    }

    private Palette() {}

    /**
     * Fond et boutons : index de 0 à 23 (24 couleurs de MG4Dash, contrat theme-24-colours). Un index absent, illisible ou
     * hors plage donne le défaut de MG4Dash (fond Noir, boutons Bleu), sans empêcher la lecture des autres champs.
     */
    static final int DEFAULT_BG = 1, DEFAULT_ACC = 0;

    static int id(String[] p, int i, int count, int fallback) {
        if (p.length <= i) return fallback;
        try {
            int n = Integer.parseInt(p[i].trim());
            return n >= 0 && n < count ? n : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /** Relit le thème choisi dans MG4Dash ; renvoie true s'il a changé. */
    public static boolean load(Context c) {
        String v = null;
        try { v = Settings.Global.getString(c.getContentResolver(), "mg4dash_theme"); } catch (Throwable ignored) {}
        return apply(v);
    }

    /** Applique la valeur de mg4dash_theme ("fond,boutons,clair,…", null = thème d'origine) ; true si elle a changé. */
    static boolean apply(String v) {
        // Sans MG4Dash (réglage absent) : les couleurs d'origine de Radio Dash, rien n'est remplacé
        boolean none = v == null;
        if (none) v = "";
        if (v.equals(loaded)) return false;
        loaded = v;
        int b = 0, a = 0;
        boolean lm = false;
        try {
            String[] p = (none ? "0,0" : v).split(",");
            b = id(p, 0, BG.length, DEFAULT_BG);
            a = id(p, 1, ACC.length, DEFAULT_ACC);
            if (p.length > 2) lm = "1".equals(p[2].trim());
            verticalDock = p.length > 3 && "1".equals(p[3].trim());
            mediaStyle = p.length > 4 && "1".equals(p[4].trim()) ? 1 : 0;
            wideBar = p.length > 5 && "1".equals(p[5].trim());
            rightBar = p.length > 6 && "1".equals(p[6].trim());
            fogHidden = p.length > 8 && "1".equals(p[8].trim());
            aaChoice = -1; aaApp = null;
            if (p.length > 9) aaChoice = Math.max(0, Math.min(2, Integer.parseInt(p[9].trim())));
            if (aaChoice == 2 && p.length > 10 && !p[10].trim().isEmpty()) aaApp = p[10].trim();
        } catch (Throwable ignored) {}
        lightMode = lm;
        identity = none;
        MAP.clear();
        done.clear();
        iconDone.clear();
        textDone.clear();
        int[] o = ORIG, n = (lm ? BG_LIGHT : BG)[b];
        for (int i = 0; i < o.length; i++) MAP.put(o[i], n[i]);
        MAP.put(0x141925, n[1]);                       // fond propre à la radio
        MAP.put(0x3A4356, light(n[4], 0.10f));          // graduations du cadran FM
        int act = (lm ? ACC_LIGHT : ACC)[a][0], sel = (lm ? ACC_LIGHT : ACC)[a][1];
        if (lm) {
            // Textes : blanc → foncé, gris → gris foncé
            MAP.put(0xFFFFFF, TEXT_DARK);
            MAP.put(0xE6EAF0, TEXT_DARK);
            MAP.put(0xA0AEC0, 0x4A5466);
            MAP.put(0x8A94A6, 0x5A6475);
        }
        MAP.put(0x2979FF, act);
        MAP.put(0x00D2FF, sel);
        MAP.put(0x1A5CCC, shade(act, 0.70f));
        MAP.put(0x1A4FB0, shade(act, 0.70f));
        MAP.put(0x123A8C, shade(act, 0.45f));
        MAP.put(0x0D2A66, shade(act, 0.32f));
        MAP.put(0x4A8CFF, light(act, 0.18f));
        Log.i(TAG, "thème " + v);
        return true;
    }

    /** Couleur du thème choisi correspondant à une couleur d'origine (alpha conservé). */
    public static int map(int color) {
        if (identity) return color;
        Integer n = MAP.get(color & 0xFFFFFF);
        return n == null ? color : (color & 0xFF000000) | n;
    }

    /** Lecteur DAB+ immersif (carré, image en fond) : toujours, le choix Immersif / Classique ne vaut que pour MG4Dash. */
    public static boolean immersiveDab() { return true; }

    /** Couleur d'action du thème (bouton lecture), quand l'image n'en donne pas. */
    public static int accent() { return map(0xFF2979FF); }

    /** Couleur des étoiles de favoris : le bouton d'action du thème MG4Dash choisi. */
    public static int starColor() {
        return map(0xFF2979FF);
    }

    /** Favourite badge colours (15/11): not in favourites, a dark square with a border and a light hollow star. */
    public static final int STAR_OFF_FILL = 0xFF202838, STAR_OFF_BORDER = 0xFF4A5670, STAR_OFF = 0xFFC9D1DD;
    /** White of the favourite star: almost white, so the light theme's recolouring (pure white → dark) leaves it. */
    public static final int STAR_ON = 0xFFFEFEFE;

    /**
     * Favourite badge, the same on the station tiles and on the player's star button, in every band: a rounded
     * square filled with the theme colour when [on], otherwise dark with a 2 dp border.
     */
    public static android.graphics.drawable.GradientDrawable starBadge(android.content.Context c, boolean on) {
        float d = c.getResources().getDisplayMetrics().density;
        android.graphics.drawable.GradientDrawable g = new android.graphics.drawable.GradientDrawable();
        g.setCornerRadius(12 * d);
        g.setColor(on ? starColor() : STAR_OFF_FILL);
        if (!on) g.setStroke(Math.round(2 * d), STAR_OFF_BORDER);
        return g;
    }

    private static android.graphics.drawable.Drawable.ConstantState starOnState;

    /** The player's star button shows the favourite star (the stock radio swaps radio_btn_collection / _uncollection). */
    private static boolean starOn(ImageView v) {
        try {
            if (starOnState == null) {
                int res = v.getResources().getIdentifier("radio_btn_collection", "drawable", v.getContext().getPackageName());
                if (res != 0) starOnState = v.getContext().getDrawable(res).getConstantState();
            }
            Drawable d = v.getDrawable();
            return d != null && starOnState != null && d.getConstantState() == starOnState;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Teinte les étoiles de favoris à la couleur du thème : le bouton étoile de la radio (ib_favorite,
     * ib_dab_favorite) et l'étoile des listes de stations (fond de img_radio_list_item_favorite).
     * Ne dépend pas du mode clair : appelé à chaque rafraîchissement.
     */
    private static final int STAR_TAG = 0x7f7a0010;

    public static void tintStars(View v) {
        if (v == null) return;
        try {
            if (v instanceof ImageView && v.getId() != View.NO_ID) {
                String name = name(v);
                if (name.equals("ib_favorite") || name.equals("ib_dab_favorite")) {
                    // The player's star button: the badge, 60 dp, star of its own colour (white or light grey)
                    ImageView iv = (ImageView) v;
                    iv.setImageTintList(null);
                    boolean on = starOn(iv);
                    if (!Boolean.valueOf(on).equals(iv.getTag(STAR_TAG))) {
                        iv.setBackground(starBadge(iv.getContext(), on));
                        iv.setTag(STAR_TAG, on);
                    }
                    float d = iv.getResources().getDisplayMetrics().density;
                    android.view.ViewGroup.LayoutParams lp = iv.getLayoutParams();
                    int side = Math.round(60 * d);
                    if (lp != null && lp.width != side) { lp.width = side; lp.height = side; iv.setLayoutParams(lp); }
                    int pad = Math.round(13 * d);
                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    if (iv.getPaddingLeft() != pad) iv.setPadding(pad, pad, pad, pad);
                } else if (name.equals("img_radio_list_item_favorite")) {
                    ColorStateList tint = ColorStateList.valueOf(starColor());
                    v.setBackgroundTintList(tint);
                    ((ImageView) v).setImageTintList(tint);
                }
            }
            if (v instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) v;
                for (int i = 0; i < g.getChildCount(); i++) tintStars(g.getChildAt(i));
            }
        } catch (Throwable t) {
            Log.w(TAG, "tintStars : " + t);
        }
    }

    /** Recolore une vue et ses enfants (déjà traités : ignorés). */
    public static void recolor(View v) {
        if (identity || v == null) return;
        try {
            Drawable bg = v.getBackground();
            if (bg != null && !done.contains(bg)) {
                Drawable m = bg.mutate();
                recolor(m);
                done.add(m);
                if (m != bg) v.setBackground(m);
            }
            if (v instanceof TextView) {
                TextView t = (TextView) v;
                // L'appli rebranche la couleur d'origine quand la station change : on la recolore alors aussi.
                if (textDone.get(t) != t.getTextColors()) {
                    ColorStateList mapped = map(t.getTextColors());
                    t.setTextColor(mapped);
                    textDone.put(t, t.getTextColors());
                }
            }
            if (lightMode && v instanceof ImageView && ((ImageView) v).getDrawable() != null
                    && !iconDone.contains(((ImageView) v).getDrawable())) {
                ImageView iv = (ImageView) v;
                Drawable d = iv.getDrawable();
                String name = name(v);
                boolean vector = d instanceof android.graphics.drawable.VectorDrawable
                        || (d instanceof DrawableContainer && !(d.getCurrent() instanceof android.graphics.drawable.BitmapDrawable));
                // Icônes blanches sur fond clair → foncées ; pas le bouton lecture (sur fond coloré) ni le cœur des favoris
                if (vector && iv.getColorFilter() == null && iv.getImageTintList() == null
                        && !name.equals("btn_play_pause") && !name.equals("ib_favorite") && !name.equals("ib_dab_favorite") && !"rd_art".equals(v.getTag())) {
                    iv.setColorFilter(0xFF000000 | TEXT_DARK, android.graphics.PorterDuff.Mode.SRC_IN);
                }
                iconDone.add(d);
            }
            if (v instanceof ImageView) {
                ImageView iv = (ImageView) v;
                ColorStateList tint = iv.getImageTintList();
                if (tint != null && !done.contains(v)) { iv.setImageTintList(map(tint)); done.add(v); }
            }
            if (v instanceof ViewGroup) {
                ViewGroup g = (ViewGroup) v;
                for (int i = 0; i < g.getChildCount(); i++) recolor(g.getChildAt(i));
            }
        } catch (Throwable t) {
            Log.w(TAG, "recolor : " + t);
        }
    }

    private static void recolor(Drawable d) throws Exception {
        if (d instanceof GradientDrawable) {
            GradientDrawable g = (GradientDrawable) d;
            int[] colors = g.getColors();
            if (colors != null) {
                int[] c2 = new int[colors.length];
                for (int i = 0; i < colors.length; i++) c2[i] = map(colors[i]);
                g.setColors(c2);
            } else if (g.getColor() != null) {
                g.setColor(map(g.getColor()));
            }
            Object st = field(g, "mGradientState");
            if (st != null) {
                Object w = field(st, "mStrokeWidth");
                Object sc = field(st, "mStrokeColors");
                if (w instanceof Integer && (Integer) w > 0 && sc instanceof ColorStateList) {
                    g.setStroke((Integer) w, map((ColorStateList) sc));
                }
            }
        } else if (d instanceof DrawableContainer) {
            Drawable.ConstantState cs = d.getConstantState();
            if (cs instanceof DrawableContainer.DrawableContainerState) {
                for (Drawable child : ((DrawableContainer.DrawableContainerState) cs).getChildren()) {
                    if (child != null) recolor(child);
                }
            }
        } else if (d instanceof LayerDrawable) {
            LayerDrawable l = (LayerDrawable) d;
            for (int i = 0; i < l.getNumberOfLayers(); i++) recolor(l.getDrawable(i));
        } else if (d instanceof ColorDrawable) {
            ColorDrawable c = (ColorDrawable) d;
            c.setColor(map(c.getColor()));
        }
    }

    private static ColorStateList map(ColorStateList csl) {
        if (csl == null) return null;
        try {
            int[][] states = (int[][]) field(csl, "mStateSpecs");
            int[] colors = (int[]) field(csl, "mColors");
            if (states != null && colors != null) {
                int[] c2 = new int[colors.length];
                for (int i = 0; i < colors.length; i++) c2[i] = map(colors[i]);
                return new ColorStateList(states, c2);
            }
        } catch (Throwable ignored) {}
        return ColorStateList.valueOf(map(csl.getDefaultColor()));
    }

    private static Object field(Object o, String name) {
        for (Class<?> c = o.getClass(); c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(o);
            } catch (NoSuchFieldException ignored) {
            } catch (Throwable t) {
                return null;
            }
        }
        return null;
    }

    private static int shade(int rgb, float f) {
        int r = (int) (((rgb >> 16) & 0xFF) * f), g = (int) (((rgb >> 8) & 0xFF) * f), b = (int) ((rgb & 0xFF) * f);
        return (r << 16) | (g << 8) | b;
    }

    private static int light(int rgb, float f) {
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        r = (int) (r + (255 - r) * f); g = (int) (g + (255 - g) * f); b = (int) (b + (255 - b) * f);
        return (r << 16) | (g << 8) | b;
    }
}
