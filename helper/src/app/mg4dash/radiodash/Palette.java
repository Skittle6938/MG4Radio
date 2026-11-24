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

    // Fonds : haut, bas, fin du dégradé, panneau, bordure, bouton au repos, bouton appuyé, jauge éteinte.
    // 24 couleurs dans l'ordre de MG4Dash (0 à 4 d'origine, 5 à 23 tirées de themes_palette.xml de MG4Dash)
    private static final int[][] BG = {
            {0x0B0F17, 0x141B29, 0x0E1420, 0x12161F, 0x2A3142, 0x1C2231, 0x283048, 0x262D3D},
            {0x050506, 0x0E0E10, 0x08080A, 0x121214, 0x2A2A2F, 0x1B1B1F, 0x2A2A31, 0x26262B},
            {0x15171A, 0x22252A, 0x191B1E, 0x21242A, 0x3A3F46, 0x2B2F35, 0x3A3F47, 0x33383F},
            {0x07110E, 0x0F2019, 0x0A1712, 0x0F1B16, 0x243A30, 0x16271F, 0x22392D, 0x1F2E27},
            {0x13090C, 0x231117, 0x190C10, 0x1C1015, 0x3B2229, 0x29161C, 0x3B2129, 0x2E1C22},
            {0x0B0D12, 0x131720, 0x0D1016, 0x10141B, 0x212938, 0x171C26, 0x212836, 0x1C222E},   // Slate
            {0x081114, 0x0F1F24, 0x0B1519, 0x0D1A1E, 0x1B363E, 0x12252B, 0x1A343D, 0x162C34},   // Petrol
            {0x091312, 0x112221, 0x0C1817, 0x0E1D1C, 0x1D3C3A, 0x142928, 0x1C3B38, 0x183230},   // Teal
            {0x0A120E, 0x12211A, 0x0C1712, 0x0F1C16, 0x1F3A2D, 0x15281F, 0x1E382B, 0x1A3025},   // Emerald
            {0x0D110B, 0x181F14, 0x11150E, 0x141A11, 0x2A3624, 0x1D2518, 0x283423, 0x232C1E},   // Moss
            {0x10120B, 0x1E2013, 0x15160D, 0x191B10, 0x343821, 0x242617, 0x333621, 0x2B2E1C},   // Olive
            {0x100F0C, 0x1D1B16, 0x15130F, 0x191712, 0x332F26, 0x23201A, 0x322E25, 0x2B271F},   // Sand
            {0x130F09, 0x221B11, 0x18130C, 0x1D170E, 0x3C2F1D, 0x292014, 0x3B2E1C, 0x322718},   // Bronze
            {0x120D0A, 0x211812, 0x17110C, 0x1C150F, 0x3A2A1F, 0x281D15, 0x38291E, 0x30231A},   // Brown
            {0x140B08, 0x24140F, 0x190E0B, 0x1E110D, 0x3E241B, 0x2B1812, 0x3D231A, 0x341E16},   // Rust
            {0x140808, 0x240F0F, 0x190B0B, 0x1E0D0D, 0x3E1B1B, 0x2B1212, 0x3D1A1A, 0x341616},   // DeepRed
            {0x120A0E, 0x21121A, 0x170C12, 0x1C0F16, 0x3A1F2D, 0x28151F, 0x381E2B, 0x301A25},   // Wine
            {0x120B12, 0x201320, 0x160D16, 0x1B101B, 0x382138, 0x261726, 0x362136, 0x2E1C2E},   // Plum
            {0x0F0A12, 0x1B1221, 0x130C17, 0x170F1C, 0x2F1F3A, 0x201528, 0x2E1E38, 0x271A30},   // Purple
            {0x0A0913, 0x121122, 0x0D0C18, 0x0F0E1D, 0x201D3C, 0x161429, 0x1F1C3B, 0x1A1832},   // Indigo
            {0x080A14, 0x0E1225, 0x0A0C1A, 0x0C0F1F, 0x191F41, 0x11152C, 0x181E3F, 0x141A36},   // Midnight
            {0x0C0E10, 0x16191D, 0x0F1215, 0x121619, 0x262D33, 0x1A1F23, 0x252B32, 0x1F252B},   // Steel
            {0x0E0E0E, 0x1A1A1A, 0x121212, 0x161616, 0x2D2D2D, 0x1F1F1F, 0x2B2B2B, 0x252525},   // Graphite
            {0x0F0E0D, 0x1C1A17, 0x131210, 0x171614, 0x302D29, 0x211F1C, 0x2F2B28, 0x282522},   // WarmGrey
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
            {0xE9EEF6, 0xF6F8FC, 0xEDF1F8, 0xFFFFFF, 0xCBD3E1, 0xE3E8F0, 0xD3DAE6, 0xD5DBE5},
            {0xEDEDED, 0xF8F8F8, 0xF1F1F1, 0xFFFFFF, 0xCFCFD4, 0xE6E6EA, 0xD6D6DC, 0xD9D9DE},
            {0xE4E6E9, 0xF2F3F5, 0xE9EBEE, 0xFCFCFD, 0xC7CBD1, 0xDDE0E4, 0xCDD1D7, 0xD2D6DB},
            {0xE6F0EB, 0xF4F9F6, 0xEAF3EE, 0xFFFFFF, 0xC4D6CC, 0xDCE8E1, 0xCCDDD3, 0xD2E0D8},
            {0xF2E8EA, 0xFAF4F5, 0xF5ECEE, 0xFFFFFF, 0xDCC7CC, 0xEDE0E3, 0xE2D0D4, 0xE4D5D8},
            {0xE9ECF2, 0xF7F8FA, 0xEDF0F4, 0xFFFFFF, 0xC6CDDD, 0xE1E5ED, 0xCFD6E2, 0xD6DBE6},   // Slate
            {0xE8F0F3, 0xF7FAFB, 0xEDF3F5, 0xFFFFFF, 0xC3D8DF, 0xE0EAEE, 0xCDDEE4, 0xD4E3E8},   // Petrol
            {0xE8F3F2, 0xF7FBFA, 0xEDF5F4, 0xFFFFFF, 0xC3DFDD, 0xE0EEED, 0xCDE4E2, 0xD4E8E6},   // Teal
            {0xE8F3ED, 0xF7FBF9, 0xEDF5F1, 0xFFFFFF, 0xC3DFD1, 0xE0EEE7, 0xCDE4D9, 0xD4E8DE},   // Emerald
            {0xECF1EA, 0xF8FAF7, 0xF0F4EE, 0xFFFFFF, 0xCEDAC8, 0xE5ECE2, 0xD6E0D1, 0xDCE4D7},   // Moss
            {0xF0F2E9, 0xFAFAF7, 0xF3F4ED, 0xFFFFFF, 0xD9DDC6, 0xEBEDE1, 0xDFE2CF, 0xE3E6D6},   // Olive
            {0xF0EEEA, 0xFAF9F8, 0xF3F2EF, 0xFFFFFF, 0xD8D3CA, 0xEAE8E3, 0xDEDBD3, 0xE3E0D9},   // Sand
            {0xF3EEE8, 0xFBF9F7, 0xF5F2ED, 0xFFFFFF, 0xDFD3C3, 0xEEE8E0, 0xE4DBCD, 0xE8E0D4},   // Bronze
            {0xF3ECE8, 0xFBF8F7, 0xF5F0ED, 0xFFFFFF, 0xDFCFC3, 0xEEE6E0, 0xE4D7CD, 0xE8DCD4},   // Brown
            {0xF3EAE8, 0xFBF8F7, 0xF5EFED, 0xFFFFFF, 0xDFCAC3, 0xEEE3E0, 0xE4D3CD, 0xE8D9D4},   // Rust
            {0xF3E8E8, 0xFBF7F7, 0xF5EDED, 0xFFFFFF, 0xDFC3C3, 0xEEE0E0, 0xE4CDCD, 0xE8D4D4},   // DeepRed
            {0xF3E8ED, 0xFBF7F9, 0xF5EDF1, 0xFFFFFF, 0xDFC3D1, 0xEEE0E7, 0xE4CDD9, 0xE8D4DE},   // Wine
            {0xF2E9F2, 0xFAF7FA, 0xF4EDF4, 0xFFFFFF, 0xDDC6DD, 0xEDE1ED, 0xE2CFE2, 0xE6D6E6},   // Plum
            {0xEEE8F3, 0xF9F7FB, 0xF2EDF5, 0xFFFFFF, 0xD3C3DF, 0xE8E0EE, 0xDBCDE4, 0xE0D4E8},   // Purple
            {0xE9E8F3, 0xF7F7FB, 0xEDEDF5, 0xFFFFFF, 0xC6C3DF, 0xE1E0EE, 0xCFCDE4, 0xD6D4E8},   // Indigo
            {0xE8EAF3, 0xF7F7FB, 0xEDEEF5, 0xFFFFFF, 0xC3C8DF, 0xE0E2EE, 0xCDD1E4, 0xD4D7E8},   // Midnight
            {0xEAEDF0, 0xF8F9FA, 0xEFF1F3, 0xFFFFFF, 0xCAD1D8, 0xE3E7EA, 0xD3D9DE, 0xD9DEE3},   // Steel
            {0xEDEDED, 0xF9F9F9, 0xF1F1F1, 0xFFFFFF, 0xD1D1D1, 0xE7E7E7, 0xD9D9D9, 0xDEDEDE},   // Graphite
            {0xEFEDEC, 0xF9F9F8, 0xF2F1F0, 0xFFFFFF, 0xD5D1CD, 0xE9E7E5, 0xDCD9D6, 0xE1DEDB},   // WarmGrey
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
        if (v == null) v = "0,0";
        if (v.equals(loaded)) return false;
        loaded = v;
        int b = 0, a = 0;
        boolean lm = false;
        try {
            String[] p = v.split(",");
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
        identity = b == 0 && a == 0 && !lm;
        MAP.clear();
        done.clear();
        textDone.clear();
        int[] o = BG[0], n = (lm ? BG_LIGHT : BG)[b];
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
                String name = v.getResources().getResourceEntryName(v.getId());
                ColorStateList tint = ColorStateList.valueOf(starColor());
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
            if (lightMode && v instanceof ImageView && !done.contains("icon" + System.identityHashCode(v))) {
                ImageView iv = (ImageView) v;
                Drawable d = iv.getDrawable();
                String name = "";
                try { if (v.getId() != View.NO_ID) name = v.getResources().getResourceEntryName(v.getId()); } catch (Throwable ignored) {}
                boolean vector = d instanceof android.graphics.drawable.VectorDrawable
                        || (d instanceof DrawableContainer && !(d.getCurrent() instanceof android.graphics.drawable.BitmapDrawable));
                // Icônes blanches sur fond clair → foncées ; pas le bouton lecture (sur fond coloré) ni le cœur des favoris
                if (vector && iv.getColorFilter() == null && iv.getImageTintList() == null
                        && !name.equals("btn_play_pause") && !name.equals("ib_favorite") && !name.equals("ib_dab_favorite") && !"rd_art".equals(v.getTag())) {
                    iv.setColorFilter(0xFF000000 | TEXT_DARK, android.graphics.PorterDuff.Mode.SRC_IN);
                }
                done.add("icon" + System.identityHashCode(v));
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
